package com.example.lightdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private ImageView mImageView;
	private Switch mSwitch;
	private TextView mTextView;

	private volatile int isLightOn = 0;
	private volatile int isFlashOn = 0;
	// private boolean mLightOff = false;

	private static String TAG = "lightDemo..";
	private SoundPool sp;//
	private int music;//

	private Camera camera;
	Camera.Parameters params;

	private final int MESSAGE_LIGHT_OFF = 0;
	private final int MESSAGE_LIGHT_ON = 1;
	private final int MESSAGE_FLASH_LIGHT_ON = 3;

	private LightDemoWakeLock mLightDemoWakeLock = null;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGE_LIGHT_OFF:

				turnLightOff(camera);
				break;
			case MESSAGE_LIGHT_ON:

				turnLightOn(camera);
				// Log.d(TAG, "False...");
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

		intData();
	}

	private void intData() {
		// TODO Auto-generated method stub
		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//
		music = sp.load(this, R.raw.lightbuttonsound, 1); //

		mLightDemoWakeLock = new LightDemoWakeLock(); // get power control
		mLightDemoWakeLock.acquireCpuWakeLock(this);
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

		mTextView = (TextView) findViewById(R.id.tv_nava);

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
		sp.play(music, 1, 1, 0, 0, 1);
		switch (v.getId()) {
		case R.id.iv_light_show:

			if (isLightOn == 1) {

				mImageView.setImageResource(R.drawable.newoff);
				// mImageView.postInvalidate();
				if (mHandler != null) {
					isLightOn = 0;
					mTextView.setTextColor(Color.BLACK);
					mHandler.sendEmptyMessage(MESSAGE_LIGHT_OFF);
				}
			} else {
				mImageView.setImageResource(R.drawable.newon);

				if (mHandler != null) {
					isLightOn = 1;
					mTextView.setTextColor(Color.WHITE);
					if (isFlashOn == 1) {
						mHandler.sendEmptyMessage(MESSAGE_FLASH_LIGHT_ON);
					} else {
						mHandler.sendEmptyMessage(MESSAGE_LIGHT_ON);
					}

					// turnLightOn(camera);

				}

				// Log.d(TAG, "False...");
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
		sp.release();// Release SoundPoll
		mLightDemoWakeLock.releaseCpuWakeLock();

		android.os.Process.killProcess(android.os.Process.myPid());

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		// Log.d(TAG, "onStop...");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub

		if (null != mLightDemoWakeLock) {
			mLightDemoWakeLock.acquireCpuWakeLock(this);
		} else {
			Log.d(TAG, "mLightDemoWakeLock is null");
		}
		super.onRestart();

		// Log.d(TAG, "onRestart...");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (camera == null) {
			camera = Camera.open();
		}
		// Log.d(TAG, "onResume...");
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

	// (1) 首先定义SmartMapWakeLock类
	class LightDemoWakeLock {

		private PowerManager.WakeLock mCpuWakeLock = null;

		/**
		 * Acquire CPU wake lock
		 * 
		 * @param context
		 *            Getting lock context
		 */
		void acquireCpuWakeLock(Context context) {
			// Log.v(TAG, "Acquiring cpu wake lock");
			if (mCpuWakeLock != null) {
				return;
			}

			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

			mCpuWakeLock.acquire();
		}

		/**
		 * Release wake locks
		 */

		void releaseCpuWakeLock() {
			// Log.v(TAG, "Releasing cpu wake lock");
			if (mCpuWakeLock != null) {
				mCpuWakeLock.release();
				mCpuWakeLock = null;
			}
		}
	}

}
