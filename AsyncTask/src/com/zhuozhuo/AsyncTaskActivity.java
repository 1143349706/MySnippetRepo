package com.zhuozhuo;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AsyncTaskActivity extends Activity {
    
	private ImageView mImageView;
	private Button mButton;
	private ProgressBar mProgressBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mImageView= (ImageView) findViewById(R.id.imageView);
        mButton = (Button) findViewById(R.id.button);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GetCSDNLogoTask task = new GetCSDNLogoTask();
				task.execute("http://www.linuxidc.com/pic/logo.gif");
			}
		});
    }
    
    class GetCSDNLogoTask extends AsyncTask<String,Integer,Bitmap> {//�̳�AsyncTask

		@Override
		protected Bitmap doInBackground(String... params) {//�����ִ̨�е������ں�̨�߳�ִ��
			publishProgress(0);//�������onProgressUpdate(Integer... progress)����
			HttpClient hc = new DefaultHttpClient();
			publishProgress(30);
			HttpGet hg = new HttpGet(params[0]);//��ȡcsdn��logo
			final Bitmap bm;
			try {
				HttpResponse hr = hc.execute(hg);
				bm = BitmapFactory.decodeStream(hr.getEntity().getContent());
			} catch (Exception e) {
				
				return null;
			}
			publishProgress(100);
			//mImageView.setImageBitmap(result); �����ں�̨�̲߳���ui
			return bm;
		}
		
		protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��
			mProgressBar.setProgress(progress[0]);//���½������Ľ���
	     }

	     protected void onPostExecute(Bitmap result) {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��
	    	 if(result != null) {
	    		 Toast.makeText(AsyncTaskActivity.this, "�ɹ���ȡͼƬ", Toast.LENGTH_LONG).show();
	    		 mImageView.setImageBitmap(result);
	    	 }else {
	    		 Toast.makeText(AsyncTaskActivity.this, "��ȡͼƬʧ��", Toast.LENGTH_LONG).show();
	    	 }
	     }
	     
	     protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��
	    	 mImageView.setImageBitmap(null);
	    	 mProgressBar.setProgress(0);//��������λ
	     }
	     
	     protected void onCancelled () {//��ui�߳�ִ��
	    	 mProgressBar.setProgress(0);//��������λ
	     }
    	
    }
    

}