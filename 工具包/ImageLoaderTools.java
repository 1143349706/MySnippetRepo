package com.cnki.client.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ImageLoaderTools {
	
	private HttpTools httptool;
	private Context mContext;
	//�߳���ѯ�Ŀ��Ʊ���
	private boolean isLoop = true;
	//ͼƬ���漯��
	private HashMap<String, SoftReference<Bitmap>> mHashMap_caches;
	//�����������
	private ArrayList<ImageLoadTask> maArrayList_taskQueue;
	//���ڻص�callback�еķ��������½���
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			ImageLoadTask _task = (ImageLoadTask) msg.obj;
			Log.i("info", ""+_task);
			//����callback�е�imageloaded������֪ͨ�����������
			_task.callback.imageloaded(_task.path, _task.bitmap);
		};
	};
	//���������̣߳�������ѯ������дӶ�����ͼƬ
	private Thread mThread = new Thread(){
		public void run() {
			while(isLoop){
				//�����������������ʱ��ʼִ������
				while(maArrayList_taskQueue.size() >0){
					try {
						//������������Ƴ�����ʱ�᷵��������󼴵õ��������ض���
						ImageLoadTask task = maArrayList_taskQueue.remove(0);
						
						//������ص���Сͼ�꣬�ͽ����ص�ͼƬ��һ���ı���������Ӧ����С			
						if(Constant.LOADPICTYPE == 1) {	
							//��ʼ���أ���ȡͼƬ�ļ����ֽ�����
							byte[] bytes = httptool.getByte(task.path, null, HttpTools.METHOD_GET);
							//��ȡ�̶���С��ͼƬ�����Ǿ������ź��ͼƬ���ݺ�Ȳ���,���Ϊ40����������ͼ��ͼƬ
							task.bitmap = BitMapTools.getBitmap(bytes, 40, 40);
						//��ȡͼƬ��ԭʼ��С
						} else if(Constant.LOADPICTYPE == 2) {
							//��ʼ���أ���ȡͼƬ�ļ����ֽ�����
							InputStream in = httptool.getStream(task.path, null, HttpTools.METHOD_GET);							
							//��ȡ�̶���С��ͼƬ�����Ǿ������ź��ͼƬ���ݺ�Ȳ���,���Ϊ40����������ͼ��ͼƬ
							task.bitmap = BitMapTools.getBitmap(in, 1);
							//Log.i("info", "task.bitmap============"+task.bitmap);
							
						}
						
						
						//���ͼƬ���سɹ������ڴ滺����ļ��з��û�����Ϣ���Ա�֮���˫�����ж�ȡͼƬ��Ϣ
						if(task.bitmap != null){
							//�򼯺ϻ�������ӻ���
							mHashMap_caches.put(task.path,new SoftReference<Bitmap>(task.bitmap) );
							//���ļ�����ӻ�����Ϣ
							//��ȡ�ļ��洢·��
							File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
							//����ļ�·�������� ,�򴴽���·��
							if(!dir.exists()){
								dir.mkdirs();
							}
							//����ͼƬ�洢·��
							File file = new File(dir, task.path);
							//����ļ�·���д洢ͼƬ
							BitMapTools.saveBitmap(file.getAbsolutePath(), task.bitmap);
							//������ɺ�����Ϣ�����߳�
							Message msg = Message.obtain();
							msg.obj = task;
							mHandler.sendMessage(msg);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//��ǰ�̴߳��ڵȴ�״̬
					synchronized (this) {
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
	};
	
	//���췽����
	public ImageLoaderTools(Context context){
		this.mContext = context;
		httptool = new HttpTools(context);
		mHashMap_caches = new HashMap<String, SoftReference<Bitmap>>();
		maArrayList_taskQueue = new ArrayList<ImageLoaderTools.ImageLoadTask>();
		mThread.start();
	}
	/**
	 * ͼƬ����������
	 * @author 3gtarena
	 *
	 */
	private class ImageLoadTask{
		String path;
		Bitmap bitmap;
		Callback callback;
	}
	/**
	 * �ص��ӿڡ��ڵ���loadimage����ʱ����Ҫ����ص��ӿڵ�ʵ�������
	 * @author 3gtarena
	 *
	 */
	public interface Callback{
		void imageloaded(String path,Bitmap bitmap);
	}
	//���������̣߳���ֹ�߳���ѯ
	public void quit(){
		isLoop = false;
	}
	/**
	 * ����ͼƬ·������ͼƬ �����ڴ滺����ļ�����˫��������Ż�����
	 * @param path
	 * @param callback
	 * @return
	 */
	public Bitmap imageLoad(String path,Callback callback){
		Bitmap bitmap = null;
		//����ڴ滺���д��ڸ�·��������ڴ���ֱ�ӻ�ȡ��ͼƬ
		if(mHashMap_caches.containsKey(path)){
			bitmap = mHashMap_caches.get(path).get();
			//��������е�ͼƬ�Ѿ����ͷţ���Ӹû������Ƴ�ͼƬ·��
			if(bitmap == null){
				mHashMap_caches.remove(path);
			}else {
				return bitmap;
			}
		}
		
		//���������δ�õ�������Ҫ��ͼƬ������ļ��ж�ȡ��ͼƬ
		//��ȡ���ļ���ͼƬ����ļ��洢·��
		File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		//Log.i("info", "dir=============="+dir);
		//����Ҫ��ȡ��ͼƬ·��
		File file = new File(dir, path);
		//���ļ��ж�ȡָ��·����ͼƬ
		bitmap = BitMapTools.getBitMap(file.getAbsolutePath()); 
		//����ļ��д��ڸ�ͼƬ����ֱ�Ӵ��ļ��л�ȡͼƬ
		if(bitmap != null){
			return bitmap;
		}
		
		//������������ж�δ��ȡͼƬ����ֱ�Ӵӷ�����������ͼƬ
		//��������������
		ImageLoadTask task = new ImageLoadTask();
		//��������·��
		task.path = path;
		//�������������callback����
		task.callback = callback;
		//������������ӵ�������� ����������ѯ״̬
		maArrayList_taskQueue.add(task);
		//�����̣߳���ʼ����
		synchronized (mThread) {
			mThread.notify();
		}
		return null;
	}
	
}
