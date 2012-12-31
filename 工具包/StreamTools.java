package com.cnki.client.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;
import android.os.Message;

public class StreamTools {
	/**
	 * ��������������ȡ������Ϣ��������
	 * @param in
	 * @param out
	 * @param handler
	 * @throws Exception
	 */
	public static void readData(InputStream in,OutputStream out,Handler handler) throws Exception{
		if(in != null && out != null && handler != null){
			//������Ϣ��ʼ��������
			handler.sendEmptyMessage(Constant.FILE_LOAD_START);
			//�ӷ������˶�ȡ��������
			BufferedInputStream bis = new BufferedInputStream(in);
			//BufferedOutputStream bos = new BufferedOutputStream(out);
			//����ȡ�����������ݷ��͵�ָ��·���洢
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = -1;
			//��ʼ����ʱ�����ؽ���Ϊ0
			int loaded = 0;
			byte[] bytes = new byte[1024];
			while((len = bis.read(bytes)) != -1){
				out.write(bytes, 0, len);
				//ÿ����1kb�����Ⱦͼ�1
				loaded++;
				//������ص�100kb���ͷ�����Ϣ��֪ͨ����ÿ100kb����һ�����ؽ���
				if(loaded%100 == 0){
					//���͵���Ϣ����Ϊ����������
					Message msg = Message.obtain(handler, Constant.FILE_LOAD_UPDATE);
					//�����ؽ���ֵloadedЯ������Ϣ�д��ݸ�֪ͨ��
					msg.obj = loaded;
					msg.sendToTarget();			
				}
			}
			//�ر����е�IO��
			out.close();
			bis.close();
			bos.close();
			in.close();
			//������Ϣ����ʶ�������
			handler.sendEmptyMessage(Constant.FILE_LOAD_END);
		}		
	}
	/**
	 * ��ȡ��������������Ϣ���浽ָ����·��
	 * @param in
	 * @param savepath
	 * @param handler
	 * @throws Exception
	 */
	public static void saveTo(InputStream in,String savepath,Handler handler) throws Exception{
		if(in != null && savepath != null){
			//��������Ϣ���浽ָ��·��
			OutputStream out = new FileOutputStream(savepath);
			readData(in, out, handler);
		}
	}
}
