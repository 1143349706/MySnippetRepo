package com.zgy.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	// ��������ImageView�ķ��
	int mGalleryItemBackground;
	private Context context;
	// ͼƬ����ԴID
	private Integer[] mImageIds = { R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6,
			R.drawable.img7, R.drawable.img8 };

	// ���캯��
	public ImageAdapter(Context context) {
		this.context = context;
	}

	// ��������ͼƬ�ĸ���
	@Override
	public int getCount() {
		return mImageIds.length;
	}

	// ����ͼƬ����Դ��λ��
	@Override
	public Object getItem(int position) {
		return position;
	}

	// ����ͼƬ����Դ��λ��
	@Override
	public long getItemId(int position) {
		return position;
	}

	// �˷���������Ҫ�ģ������úõ�ImageView���󷵻ظ�Gallery
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		// ͨ���������ͼƬ�����ø�ImageView
		imageView.setImageResource(mImageIds[position]);
		// ����ImageView��������������Դ�������ֵ
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		// ���ò��ֲ���
		imageView.setLayoutParams(new Gallery.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// ���÷�񣬴˷�����������xml��
		imageView.setBackgroundResource(mGalleryItemBackground);
		return imageView;
	}

	public int getmGalleryItemBackground() {
		return mGalleryItemBackground;
	}

	public void setmGalleryItemBackground(int mGalleryItemBackground) {
		this.mGalleryItemBackground = mGalleryItemBackground;
	}

}
