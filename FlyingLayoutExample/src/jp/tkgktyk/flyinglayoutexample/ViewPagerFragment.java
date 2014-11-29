package jp.tkgktyk.flyinglayoutexample;

import jp.tkgktyk.flyinglayout.FlyingLayout;
import jp.tkgktyk.flyinglayout.FlyingLayout.OnFlyingEventListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/*
 * section: 0 to 2
 * position: 0 to 2
 */
public class ViewPagerFragment extends Fragment {
	private static final int PAGE_COUNT = 3;

	private ViewPager mViewPager;

	public ViewPagerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_pager, container,
				false);

		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mViewPager.setAdapter(new Adapter(getActivity()
				.getSupportFragmentManager()));
	}

	private class Adapter extends FragmentPagerAdapter {

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public Fragment getItem(int position) {
			return new PageFragment();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Position: " + (position + 1);
		}
	}

	private class PageFragment extends Fragment {
		private FlyingLayout mFlyingLayout;
		private FrameLayout mContainer;
		private Drawable mBoundary;

		public PageFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.view_page, container, false);

			mFlyingLayout = (FlyingLayout) view.findViewById(R.id.flying);
			mContainer = (FrameLayout) view.findViewById(R.id.container);

			mBoundary = getResources().getDrawable(R.drawable.boundary);
			return view;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			mFlyingLayout.setOnFlyingEventListener(new OnFlyingEventListener() {
				@Override
				public void onOutsideClick(FlyingLayout v, int x, int y) {
					v.goHome(true);
				}

				@Override
				public void onDragStarted(FlyingLayout v) {
					mContainer.setForeground(mBoundary);
				}

				@Override
				public void onDragFinished(FlyingLayout v) {
					mContainer.setForeground(null);
				}
			});
		}
	}
}
