package com.zgy.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.Toast;

public class Activity01 extends Activity {
	/** Called when the activity is first created. */
	private Gallery myGallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myGallery = (Gallery) findViewById(R.id.myGallery);
		// ��δ��������ʢ��ʦ�ġ�android�������ء�������д��
		// myGallery.setBackgroundResource(R.drawable.bg0);
		ImageAdapter adapter = new ImageAdapter(this);
		// ���ñ������Gallery�����������attrs.xml��
		TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
		adapter.setmGalleryItemBackground(typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0));
		myGallery.setAdapter(adapter);

		String[] string = new String[] { "Afghanistan", "Albania", "Algeria",
				"American Samoa", "Andorra", "Angola", "Anguilla",
				"Antarctica", "Antigua and Barbuda", "Argentina", "Armenia",
				"Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain",
				"Bangladesh", "Barbados", "Belarus", "Belgium", "Belize",
				"Benin", "Bermuda", "Bhutan", "Bolivia",
				"Bosnia and Herzegovina", "Botswana", "Bouvet Island" };
		HorizontalListView lv = (HorizontalListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(context, "����˵�" + arg2, 0).show();
			}
		});
		lv.setAdapter(adapter);
	}

	private Context context = this;
}