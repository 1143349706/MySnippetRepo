package com.outman;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.outman.tools.DataRes;

public class MainActivity extends Activity implements OnGestureListener {

	static final int FLING_MIN_DISTANCE = 120;

	private String[][] subTitles = { { "���������Ϣ", "getEngineInfo" },
			{ "���ٺ��ͺ�", "getSpeedInfo" } };
	//ViewFlipperʵ��
	private ViewFlipper flipper;
	//��������
	private GestureDetector detector;

	private List<Car> cars;

	{
		cars = new ArrayList<Car>();
		Car car = new Car();
		car.setType("2011��8��12�գ� �������Ժ�ٿ����ŷ����ᣬͨ�����á��������Ժ��������<�л����񹲺͹�������>��������Ľ��ͣ��������й����������ָ�����Ը��˲Ʋ�֧���׸�������д������÷��޹�ͬ�Ʋ��������������Ǽ����׸���֧�������µģ�����Ժ�����о��ò��������Ȩ�Ǽ�һ�������������Ժ�������á��л����񹲺͹�����������������Ľ��ͣ�����������2011��7��4�����������Ժ����ίԱ���1525�λ���ͨ������2011��8��13����ʩ�С�");
		cars.add(car);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//��ʼ��GestureDetector
		detector = new GestureDetector(this);
		//��ʼ��ViewFlipper
		flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
			 Car car = cars.get(0);
			// ��Ӳ���
			LinearLayout frame = (LinearLayout) this.getLayoutInflater()
					.inflate(R.layout.frame, null);
			TextView textView = (TextView) frame.findViewById(R.id.TextView01);
			textView.setText(car.getType());
			//�����ҳ�浽ViewFlipper
			flipper.addView(frame);

		LinearLayout frame1 = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.main02, null);
		ListView listView = (ListView) frame1.findViewById(R.id.myList);
		//ListView������
		SimpleAdapter adapter = new SimpleAdapter(this, DataRes.getData(),
				R.layout.listview, new String[] { "title", "info", "image",
						"image1", "text" }, new int[] { R.id.title, R.id.info,
						R.id.image, R.id.image1, R.id.text });
		listView.setAdapter(adapter);
		//�����ҳ�浽ViewFlipper
		flipper.addView(frame1);
	}

	/**
	 * ����û��onTouchEvent(MotionEvent ev)����Ϊ��Ҫ�õ�ListView�ؼ�,ListView�Ѿ�ʵ��
	 * �����Ƽ��������Գ�ͻ��
	 */
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return detector.onTouchEvent(ev);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	/**
	 * ��������
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		ViewFlipper flipper2 = (ViewFlipper) this.flipper.getCurrentView()
				.findViewWithTag("flipper2");
		//����
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper.showNext();
			return true;
			//����
		} else if (e1.getX() - e2.getX() < -FLING_MIN_DISTANCE) {
			//���ö���Ч��
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
			return true;
		} 
		return false;

	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
