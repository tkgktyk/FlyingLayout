package jp.tkgktyk.flyinglayoutexample;

import jp.tkgktyk.flyinglayout.FlyingLayout;
import jp.tkgktyk.flyinglayout.FlyingLayout.OnFlyingEventListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class PaddingFragment extends Fragment {

	private FlyingLayout mFlyingLayout;
	private EditText mPaddingH;
	private EditText mPaddingV;
	private View mLeft;
	private View mTop;
	private View mRight;
	private View mBottom;

	private float mDensity = 1.0f;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mDensity = activity.getResources().getDisplayMetrics().density;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_padding, container,
				false);
		mFlyingLayout = (FlyingLayout) view.findViewById(R.id.flying);
		mPaddingH = (EditText) view.findViewById(R.id.h_pad);
		mPaddingV = (EditText) view.findViewById(R.id.v_pad);

		mLeft = view.findViewById(R.id.left);
		mTop = view.findViewById(R.id.top);
		mRight = view.findViewById(R.id.right);
		mBottom = view.findViewById(R.id.bottom);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		int h = mFlyingLayout.getHorizontalPadding();
		int v = mFlyingLayout.getVerticalPadding();
		updateHorizontalPading(h);
		updateVerticalPading(v);
		mPaddingH.setText(Integer.toString((int) (h / mDensity)));
		mPaddingV.setText(Integer.toString((int) (v / mDensity)));

		mFlyingLayout.setOnFlyingEventListener(new OnFlyingEventListener() {
			@Override
			public void onOutsideClick(FlyingLayout v, int x, int y) {
				mFlyingLayout.goHome(true);
			}

			@Override
			public void onDragStarted(FlyingLayout v) {
				// doing nothing
			}

			@Override
			public void onDragFinished(FlyingLayout v) {
				// doing nothing
			}
		});
		mPaddingH.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// doing nothing
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// doing nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					updateHorizontalPading((int) (Integer.parseInt(s.toString()) * mDensity));
				}
			}
		});
		mPaddingV.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// doing nothing
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// doing nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					updateVerticalPading((int) (Integer.parseInt(s.toString()) * mDensity));
				}
			}
		});
	}

	private void updateHorizontalPading(int padding) {
		mFlyingLayout.setHorizontalPadding(padding);
		LayoutParams lp = mLeft.getLayoutParams();
		lp.width = padding;
		mLeft.setLayoutParams(lp);
		lp = mRight.getLayoutParams();
		lp.width = padding;
		mRight.setLayoutParams(lp);
	}

	private void updateVerticalPading(int padding) {
		mFlyingLayout.setVerticalPadding(padding);
		LayoutParams lp = mTop.getLayoutParams();
		lp.height = padding;
		mTop.setLayoutParams(lp);
		lp = mBottom.getLayoutParams();
		lp.height = padding;
		mBottom.setLayoutParams(lp);
	}
}
