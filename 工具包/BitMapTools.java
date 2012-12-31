package com.cnki.client.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
/**
 * ��ȡbitmap�Ĺ����� �Լ�����bitmap��ָ��·��
 * @author WANGXIAOHONG
 *
 */
public class BitMapTools {
	/**
	 * ������������ȡ��Ӧ��λͼ����
	 * @param in
	 * @return
	 */
	public static Bitmap getBitmap(InputStream in){
		return BitmapFactory.decodeStream(in);
	}
	/**
	 * ������������ ��С�Ȼ�ȡλͼ����
	 * @param in
	 * @param scale
	 * @return
	 */
	public static Bitmap getBitmap(InputStream in,int scale){
		Bitmap _bitmap = null;
		Options _ops = new Options();
		_ops.inSampleSize = scale;
		_bitmap = BitmapFactory.decodeStream(in, null, _ops);
		return _bitmap;
	}
	/**
	 * ����ָ������Ŀ�ߣ������ݺ�ȣ���С��ȡλͼ����
	 * @param in
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes,int width,int height){
		Bitmap _bitmap = null;
		Options _ops = new Options();
		_ops.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length,_ops);
		
		_ops.inJustDecodeBounds = false;
		
		int scaleX = _ops.outWidth/width;
		int scaleY = _ops.outHeight/height;
		int scale = scaleX>scaleY?scaleX:scaleY;
		_ops.inSampleSize = scale;
		_bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,_ops);
		return _bitmap;
	}
	/**
	 * ����ָ�����ļ�·����ȡλͼ����
	 * @param path
	 * @return
	 */
	public static Bitmap getBitMap(String path){
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(path);
		return bitmap;
	}
	/**
	 * ��λͼ���浽ָ����·�� 
	 * @param path
	 * @param bitmap
	 * @throws IOException 
	 */
	public static void saveBitmap(String path,Bitmap bitmap) throws IOException{
		if(path != null && bitmap != null){
			File _file = new File(path);
			//����ļ��в������򴴽�һ���µ��ļ� 
			if(!_file.exists()){
				_file.getParentFile().mkdirs();
				_file.createNewFile();
			}
			//���������
			OutputStream write = new FileOutputStream(_file);
			//��ȡ�ļ���
			String fileName = _file.getName();
			//ȡ���ļ��ĸ�ʽ��
			String endName = fileName.substring(fileName.lastIndexOf(".")+1);
			if("png".equalsIgnoreCase(endName)){
				//bitmap��ѹ����ʽ
				bitmap.compress(CompressFormat.PNG, 100, write);
			}else {
				bitmap.compress(CompressFormat.JPEG, 100, write);
			}
		}
	}
}
