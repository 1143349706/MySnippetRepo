package com.example.muitipointer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("MP", "����������" + event.getPointerCount());
		Log.v("MPLocX", "��һ���㴥������X��" + event.getX(0));
		Log.v("MPLocY", "��һ���㴥������Y��" + event.getY(0));
		return super.onTouchEvent(event);
	}

}
