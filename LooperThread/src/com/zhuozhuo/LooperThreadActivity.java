package com.zhuozhuo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class LooperThreadActivity extends Activity{
    /** Called when the activity is first created. */
	
	private final int MSG_HELLO = 0;
    private Handler mHandler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new CustomThread().start();//�½�������CustomThreadʵ��
        
        findViewById(R.id.send_btn).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//�������ʱ������Ϣ
				String str = "hello";
		        Log.d("Test", "MainThread is ready to send msg:" + str);
				mHandler.obtainMessage(MSG_HELLO, str).sendToTarget();//������Ϣ��CustomThreadʵ��
				
			}
		});
        
    }
    
    
    
    
    
    class CustomThread extends Thread {
    	@Override
    	public void run() {
    		//������Ϣѭ���Ĳ���
    		Looper.prepare();//1����ʼ��Looper
    		mHandler = new Handler(){//2����handler��CustomThreadʵ����Looper����
    			public void handleMessage (Message msg) {//3�����崦����Ϣ�ķ���
    				switch(msg.what) {
    				case MSG_HELLO:
    					Log.d("Test", "CustomThread receive msg:" + (String) msg.obj);
    				}
    			}
    		};
    		Looper.loop();//4��������Ϣѭ��
    	}
    }
}