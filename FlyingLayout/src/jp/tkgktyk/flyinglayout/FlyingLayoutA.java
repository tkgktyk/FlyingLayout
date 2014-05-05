package jp.tkgktyk.flyinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class FlyingLayoutA extends AbsoluteLayout {
	private static final String TAG = FlyingLayoutA.class.getSimpleName();

	private static final float DEFAULT_SPEED = 1.0f;
	private static final int DEFAULT_HORIZONTAL_PADDING = 0;
	private static final int DEFAULT_VERTICAL_PADDING = 0;
	private static final boolean DEFAULT_IGNORE_TOUCH_EVENT = false;
	private static final boolean DEFAULT_USE_CONTAINER = false;
	private static final int DEFAULT_OFFSET_X = 0;
	private static final int DEFAULT_OFFSET_Y = 0;

	/**
	 * Sentinel value for no current active pointer. Used by
	 * {@link #mActivePointerId}.
	 */
	private static final int INVALID_POINTER = -1;
	/**
	 * ID of the active pointer. This is used to retain consistency during
	 * drags/flings if multiple pointers are used.
	 */
	private int mActivePointerId = INVALID_POINTER;
	private int mTouchSlop;
	/**
	 * True if the user is currently dragging this ScrollView around. This is
	 * not the same as 'is being flinged', which can be checked by
	 * mScroller.isFinished() (flinging begins when the user lifts his finger).
	 */
	private boolean mIsBeingDragged = false;
	/**
	 * Position of the last motion event.
	 */
	private int mLastMotionX;
	private int mLastMotionY;

	private float mSpeed;
	private int mHorizontalPadding;
	private int mVerticalPadding;
	private boolean mIgnoreTouchEvent;
	private boolean mUseContainer;
	private int mOffsetX;
	private int mOffsetY;

	private void fetchAttribute(Context context, AttributeSet attrs,
			int defStyle) {
		// get attributes specified in XML
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.FlyingLayout, defStyle, 0);
		try {
			setSpeed(a.getFloat(R.styleable.FlyingLayout_speed, DEFAULT_SPEED));
			setHorizontalPadding(a.getDimensionPixelSize(
					R.styleable.FlyingLayout_horizontalPadding,
					DEFAULT_HORIZONTAL_PADDING));
			setVerticalPadding(a.getDimensionPixelSize(
					R.styleable.FlyingLayout_verticalPadding,
					DEFAULT_VERTICAL_PADDING));
			setIgnoreTouchEvent(a.getBoolean(
					R.styleable.FlyingLayout_ignoreTouchEvent,
					DEFAULT_IGNORE_TOUCH_EVENT));
			setUseContainer(a.getBoolean(R.styleable.FlyingLayout_useContainer,
					DEFAULT_USE_CONTAINER));
			setOffsetX(a.getInt(R.styleable.FlyingLayout_offsetX,
					DEFAULT_OFFSET_X));
			setOffsetY(a.getInt(R.styleable.FlyingLayout_offsetY,
					DEFAULT_OFFSET_Y));
		} finally {
			a.recycle();
		}
	}

	public FlyingLayoutA(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		fetchAttribute(context, attrs, defStyle);
	}

	public FlyingLayoutA(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlyingLayoutA(Context context) {
		super(context);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		setSpeed(DEFAULT_SPEED);
		setHorizontalPadding(DEFAULT_HORIZONTAL_PADDING);
		setVerticalPadding(DEFAULT_VERTICAL_PADDING);
		setIgnoreTouchEvent(DEFAULT_IGNORE_TOUCH_EVENT);
		setUseContainer(DEFAULT_USE_CONTAINER);
		setOffsetX(DEFAULT_OFFSET_X);
		setOffsetY(DEFAULT_OFFSET_Y);
	}

	public void setSpeed(float speed) {
		mSpeed = speed;
	}

	public float getSpeed() {
		return mSpeed;
	}

	public void setHorizontalPadding(int padding) {
		mHorizontalPadding = padding;
	}

	public int getHorizontalPadding() {
		return mHorizontalPadding;
	}

	public void setVerticalPadding(int padding) {
		mVerticalPadding = padding;
	}

	public int getVerticalPadding() {
		return mVerticalPadding;
	}

	public void setIgnoreTouchEvent(boolean ignore) {
		mIgnoreTouchEvent = ignore;
	}

	public boolean getIgnoreTouchEvent() {
		return mIgnoreTouchEvent;
	}

	public void setUseContainer(boolean use) {
		mUseContainer = use;
	}

	public boolean getUseContainer() {
		return mUseContainer;
	}

	public void setOffsetX(int offset) {
		mOffsetX = offset;
	}

	public int getOffsetX() {
		return mOffsetX;
	}

	public void setOffsetY(int offset) {
		mOffsetY = offset;
	}

	public int getOffsetY() {
		return mOffsetY;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mIsBeingDragged && mIgnoreTouchEvent) {
			return false;
		}

		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onMotionEvent will be called and we do the actual
		 * scrolling there.
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
			return true;
		}

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionY is set to the y value of
			 * the down event.
			 */
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				// If we don't have a valid id, the touch down wasn't on
				// content.
				break;
			}

			final int pointerIndex = ev.findPointerIndex(activePointerId);
			if (pointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + activePointerId
						+ " in onInterceptTouchEvent");
				break;
			}

			boolean isBeingDraggedX = false;
			boolean isBeingDraggedY = false;
			final int x = (int) ev.getX(pointerIndex);
			final int xDiff = Math.abs(x - mLastMotionX);
			final int y = (int) ev.getY(pointerIndex);
			final int yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop) {
				isBeingDraggedX = true;
				mLastMotionX = x;
			}
			if (yDiff > mTouchSlop) {
				isBeingDraggedY = true;
				mLastMotionY = y;
			}
			if (isBeingDraggedX || isBeingDraggedY) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				mIsBeingDragged = true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			final int x = (int) ev.getX();
			final int y = (int) ev.getY();
			/*
			 * Remember location of down touch. ACTION_DOWN always refers to
			 * pointer index 0.
			 */
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// always does not intercept

			/* Release the drag */
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mIsBeingDragged;
	};

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mIsBeingDragged && mIgnoreTouchEvent && insideOfContents(ev)) {
			return false;
		}

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (getChildCount() == 0) {
				return false;
			}

			// Remember where the motion event started
			mLastMotionX = (int) ev.getX();
			mLastMotionY = (int) ev.getY();
			mActivePointerId = ev.getPointerId(0);
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (!mIsBeingDragged && mIgnoreTouchEvent) {
				return false;
			}

			final int activePointerIndex = ev
					.findPointerIndex(mActivePointerId);
			if (activePointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + mActivePointerId
						+ " in onTouchEvent");
				break;
			}

			boolean isBeingDraggedX = false;
			boolean isBeingDraggedY = false;
			final int x = (int) ev.getX(activePointerIndex);
			int deltaX = mLastMotionX - x;
			if (!mIsBeingDragged && Math.abs(deltaX) > mTouchSlop) {
				isBeingDraggedX = true;
				if (deltaX > 0) {
					deltaX -= mTouchSlop;
				} else {
					deltaX += mTouchSlop;
				}
			}
			final int y = (int) ev.getY(activePointerIndex);
			int deltaY = mLastMotionY - y;
			if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
				isBeingDraggedY = true;
				if (deltaY > 0) {
					deltaY -= mTouchSlop;
				} else {
					deltaY += mTouchSlop;
				}
			}
			if (isBeingDraggedX || isBeingDraggedY) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				mIsBeingDragged = true;
			}
			if (mIsBeingDragged) {
				// Scroll to follow the motion event

				move(-deltaX, -deltaY);
				mLastMotionX = x;
				mLastMotionY = y;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged) {
				onMoveFinished();
				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
			} else {
				onUnhandledClick(ev);
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged && getChildCount() > 0) {
				onMoveFinished();
				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
			} else if (!mIsBeingDragged) {
				onUnhandledClick(ev);
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = ev.getActionIndex();
			mLastMotionX = (int) ev.getX(index);
			mLastMotionY = (int) ev.getY(index);
			mActivePointerId = ev.getPointerId(index);
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
			mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
			break;
		}
		return true;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = (int) ev.getX(newPointerIndex);
			mLastMotionY = (int) ev.getY(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	protected int clamp(int src, int limit) {
		if (src > limit) {
			return limit;
		} else if (src < -limit) {
			return -limit;
		}
		return src;
	}

	public void move(int deltaX, int deltaY) {
		deltaX = (int) Math.round(deltaX * mSpeed);
		deltaY = (int) Math.round(deltaY * mSpeed);
		moveWithoutSpeed(deltaX, deltaY);
	}

	public void moveWithoutSpeed(int deltaX, int deltaY) {
		int hLimit = getWidth() - getHorizontalPadding();
		int vLimit = getHeight() - getVerticalPadding();
		mOffsetX = clamp(mOffsetX + deltaX, hLimit);
		mOffsetY = clamp(mOffsetY + deltaY, vLimit);
		if (mOnFlyingEventListener != null) {
			mOnFlyingEventListener.onMove(this, deltaX, deltaY);
		}

		layoutAfterMoving();
	}

	public void goHome() {
		int x = mOffsetX;
		int y = mOffsetY;
		mOffsetX = 0;
		mOffsetY = 0;

		if (mOnFlyingEventListener != null) {
			mOnFlyingEventListener.onMove(this, -x, -y);
		}

		layoutAfterMoving();
	}

	private void layoutAfterMoving() {
		int n = getUseContainer() ? 1 : getChildCount();
		for (int i = 0; i < n; ++i) {
			View child = getChildAt(i);
			AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) child
					.getLayoutParams();
			lp.x = mOffsetX;
			lp.y = mOffsetY;
			child.setLayoutParams(lp);
		}
	}

	public boolean staysHome() {
		return mOffsetX == 0 && mOffsetY == 0;
	}

	public void rotate() {
		mOffsetX = Math.round(mOffsetX * 1f / getWidth() * getHeight());
		mOffsetY = Math.round(mOffsetY * 1f / getHeight() * getWidth());
	}

	private boolean insideOfContents(MotionEvent ev) {
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();

		boolean inside = false;
		int n = getUseContainer() ? 1 : getChildCount();
		for (int i = 0; i < n; ++i) {
			View child = getChildAt(i);
			boolean in = false;
			if (x >= child.getLeft() && x <= child.getRight()) {
				if (y >= child.getTop() && y <= child.getBottom()) {
					in = true;
				}
			}
			inside = (inside || in);
		}
		return inside;
	}

	public void onUnhandledClick(MotionEvent ev) {
		if (mOnFlyingEventListener != null) {
			if (!insideOfContents(ev)) {
				mOnFlyingEventListener.onOutsideClick(this, (int) ev.getX(),
						(int) ev.getY());
			}
		}
	}

	public void onMoveFinished() {
		if (mOnFlyingEventListener != null) {
			mOnFlyingEventListener.onMoveFinished(this);
		}
	}

	public interface OnFlyingEventListener {
		/**
		 * callback when onMove event.
		 * 
		 * @param deltaX
		 * @param deltaY
		 */
		public void onMove(FlyingLayoutA v, int deltaX, int deltaY);

		/**
		 * callback when a moving event is finished.
		 * 
		 * @param v
		 */
		public void onMoveFinished(FlyingLayoutA v);

		/**
		 * callback when happen click event at outside of contents.
		 * 
		 * @param v
		 * @param x
		 * @param y
		 */
		public void onOutsideClick(FlyingLayoutA v, int x, int y);
	}

	private OnFlyingEventListener mOnFlyingEventListener = null;

	public void setOnFlyingEventListener(OnFlyingEventListener listener) {
		mOnFlyingEventListener = listener;
	}

	public OnFlyingEventListener getOnFlyingEventListener() {
		return mOnFlyingEventListener;
	}
}