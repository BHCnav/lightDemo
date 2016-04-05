package com.example.lightdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;

public class MainActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private ImageView mImageView;
	private Switch mSwitch;

	private volatile int isLightOn = 0;
	private volatile int isFlashOn = 0;
	// private boolean mLightOff = false;

	private static String TAG = "lightDemo..";

	private Camera camera;
	Camera.Parameters params;

	private final int MESSAGE_LIGHT_OFF = 0;
	private final int MESSAGE_LIGHT_ON = 1;
	private final int MESSAGE_FLASH_LIGHT_ON = 3;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_LIGHT_OFF:

				turnLightOff(camera);
				break;
			case MESSAGE_LIGHT_ON:

				turnLightOn(camera);
				Log.d(TAG, "False...");

				break;

			case MESSAGE_FLASH_LIGHT_ON:

				FlashTask flashTask = new FlashTask();
				flashTask.execute();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initView();

		initEvent();
	}

	private void initEvent() {
		// TODO Auto-generated method stub
		mImageView.setOnClickListener(this);
		mSwitch.setOnCheckedChangeListener(this);

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

			if (isLightOn == 1) {

				mImageView.setImageResource(R.drawable.newoff);
				// mImageView.postInvalidate();
				if (mHandler != null) {
					isLightOn = 0;

					mHandler.sendEmptyMessage(MESSAGE_LIGHT_OFF);
				}
			} else {
				mImageView.setImageResource(R.drawable.newon);

				if (mHandler != null) {
					isLightOn = 1;

					if (isFlashOn == 1) {
						mHandler.sendEmptyMessage(MESSAGE_FLASH_LIGHT_ON);
					} else {
						mHandler.sendEmptyMessage(MESSAGE_LIGHT_ON);
					}

					// turnLightOn(camera);

				}

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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		if (isChecked) {
			isFlashOn = 1;
			mHandler.sendEmptyMessage(MESSAGE_FLASH_LIGHT_ON);
		} else {
			isFlashOn = 0;
		}
	}

	class FlashTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			while ((isLightOn == 1) && (isFlashOn == 1)) {
				try {
					turnLightOff(camera);

					Thread.sleep(100);
					turnLightOn(camera);

					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

	}

}
