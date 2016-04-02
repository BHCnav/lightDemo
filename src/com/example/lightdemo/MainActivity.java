package com.example.lightdemo;

import android.app.Activity;
import android.hardware.Camera;
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
	private boolean mLightOff = false;
	
	private String TAG = "lightDemo..";

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
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.iv_light_show);
		mSwitch = (Switch) findViewById(R.id.sw_flash_light);

	}

	// Turning On flash
	private void turnOnFlash() {

		if (camera == null || params == null) {
			return;
		}
		params = camera.getParameters();
		params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		camera.setParameters(params);
		camera.startPreview();
		// isLightOn = true;

	}

	// Turning Off flash
	private void turnOffFlash() {

		if (camera == null || params == null) {
			return;
		}
		params = camera.getParameters();
		params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		camera.setParameters(params);
		camera.stopPreview();
		// isLightOn = false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "onClick: "+v.getId()+"  R.id.iv_light_show:"+R.id.iv_light_show+ isLightOn);
		switch (v.getId()) {
		case R.id.iv_light_show:
			
			if (isLightOn) {

				mImageView.setBackgroundResource(R.drawable.newoff_200);
				turnOffFlash();
				isLightOn = false;
			} else {
				mImageView.setBackgroundResource(R.drawable.newon_200);
				turnOnFlash();
				isLightOn = true;
				
				Log.d(TAG, "False...");
			}
			break;

		default:
			break;
		}

	}

}
