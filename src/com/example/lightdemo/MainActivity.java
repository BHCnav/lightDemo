package com.example.lightdemo;

import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Switch;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView mImageView;
	private Switch mSwitch;

	private boolean isLightOn = false;
	// private boolean mLightOff = false;

	private static String TAG = "lightDemo..";

	private Camera camera;
	Camera.Parameters params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		mImageView.setOnClickListener(this);

		if (camera == null) {
			camera = Camera.open();
		}
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.iv_light_show);
		mSwitch = (Switch) findViewById(R.id.sw_flash_light);

	}

	public static void turnLightOn(Camera mCamera) {

		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}

		// Turn on the flash

		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(parameters);

	}

	public static void turnLightOff(Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		if (parameters == null) {
			return;
		}

		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCamera.setParameters(parameters);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.iv_light_show:

			if (isLightOn) {

				mImageView.setImageResource(R.drawable.newoff);
				// mImageView.postInvalidate();
				turnLightOff(camera);
				isLightOn = false;
			} else {
				mImageView.setImageResource(R.drawable.newon);
				// mImageView.postInvalidate();
				turnLightOn(camera);
				isLightOn = true;

				Log.d(TAG, "False...");
			}
			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		camera.release();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		Log.d(TAG, "onStop...");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		Log.d(TAG, "onRestart...");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (camera == null) {
			camera = Camera.open();
		}
		Log.d(TAG, "onResume...");
	}

}
