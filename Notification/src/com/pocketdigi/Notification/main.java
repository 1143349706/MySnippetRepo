package com.pocketdigi.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class main extends Activity {
    /** Called when the activity is first created. */
	int notification_id=19172439;
	NotificationManager nm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Button bt1=(Button)findViewById(R.id.bt1);
        bt1.setOnClickListener(bt1lis);
        Button bt2=(Button)findViewById(R.id.bt2);
        bt2.setOnClickListener(bt2lis);
        
    }
    OnClickListener bt1lis=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showNotification(R.drawable.home,"ͼ��ߵ�����","����","����");
		}
    	
    };
    OnClickListener bt2lis=new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//showNotification(R.drawable.home,"ͼ��ߵ�����","����","����");
			nm.cancel(notification_id);
		}
    	
    };
    public void showNotification(int icon,String tickertext,String title,String content){
    	//����һ��Ψһ��ID���������
    	
    	//Notification������
    	Notification notification=new Notification(icon,tickertext,System.currentTimeMillis());
    	//����Ĳ����ֱ�����ʾ�ڶ���֪ͨ����Сͼ�꣬Сͼ���Ե����֣�������ʾ���Զ���ʧ��ϵͳ��ǰʱ�䣨�����������ʲô�ã�
    	notification.defaults=Notification.DEFAULT_ALL; 
    	//��������֪ͨ�Ƿ�ͬʱ�����������񶯣�����ΪNotification.DEFAULT_SOUND
    	//��ΪNotification.DEFAULT_VIBRATE;
    	//LightΪNotification.DEFAULT_LIGHTS�����ҵ�Milestone�Ϻ���ûʲô��Ӧ
    	//ȫ��ΪNotification.DEFAULT_ALL
    	//������񶯻���ȫ����������AndroidManifest.xml������Ȩ��
    	PendingIntent pt=PendingIntent.getActivity(this, 0, new Intent(this,main.class), 0);
    	//���֪ͨ��Ķ�����������ת��main ���Acticity
    	notification.setLatestEventInfo(this,title,content,pt);
    	nm.notify(notification_id, notification);
    	
    }
}