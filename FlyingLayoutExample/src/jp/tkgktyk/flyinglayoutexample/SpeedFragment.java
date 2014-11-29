package jp.tkgktyk.flyinglayoutexample;

import jp.tkgktyk.flyinglayout.FlyingLayout;
import jp.tkgktyk.flyinglayout.FlyingLayout.OnFlyingEventListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SpeedFragment extends Fragment {

	private FlyingLayout mFlyingLayout;
	private SeekBar mSeek;
	private TextView mText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_speed, container, false);
		mFlyingLayout = (FlyingLayout) view.findViewById(R.id.flying);
		mSeek = (SeekBar) view.findViewById(R.id.seek);
		mText = (TextView) view.findViewById(R.id.text);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		updateText(mSeek.getProgress());
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
		mSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// doing nothing
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// doing nothing
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updateText(progress);
			}
		});
	}

	private void updateText(int speed) {
		mFlyingLayout.setSpeed(speed);
		mText.setText(getString(R.string.speed_d1, speed));
	}
}
