package cn.m15.xys;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

public class Font extends Activity {
    public int mScreenWidth = 0;
    public int mScreenHeight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	setContentView(new FontView(this));
	// ��ȡ��Ļ���
	Display display = getWindowManager().getDefaultDisplay();
	mScreenWidth  = display.getWidth();
	mScreenHeight = display.getHeight();
	super.onCreate(savedInstanceState);

    }

    class FontView extends View {
        public final static String STR_WIDTH = "��ȡ�ַ�����Ϊ��"; 
        public final static String STR_HEIGHT = "��ȡ����߶�Ϊ��"; 
        Paint mPaint = null;
        
	public FontView(Context context) {
	    super(context);
	    mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    //�����ַ�����ɫ
	    mPaint.setColor(Color.WHITE);
	    canvas.drawText("��ǰ��Ļ��" + mScreenWidth, 0, 30, mPaint);
	    canvas.drawText("��ǰ��Ļ��"+ mScreenHeight, 0, 60, mPaint);
	    //���������С
	    mPaint.setColor(Color.RED);
	    mPaint.setTextSize(18);
	    canvas.drawText("�����СΪ18", 0, 90, mPaint);
	    //����������
	    mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    canvas.drawText("���������ݺ�", 0, 120, mPaint);
	    //��ȡ�ַ������
	    canvas.drawText(STR_WIDTH + getStringWidth(STR_WIDTH), 0, 150, mPaint);
	    //��ȡ����߶�
	    canvas.drawText(STR_HEIGHT + getFontHeight(), 0, 180, mPaint);
	    //��string.xml��ȡ�ַ�������
	    mPaint.setColor(Color.YELLOW);
	    canvas.drawText(getResources().getString(R.string.string_font), 0, 210, mPaint);
	    super.onDraw(canvas);
	}
	
	/**
	 * ��ȡ�ַ�����
	 * @param str
	 * @return
	 */
	private int getStringWidth(String str) {
	    return (int) mPaint.measureText(STR_WIDTH); 
	}
	/*
	 * ��ȡ����߶�
	 */
	private int getFontHeight() {
	    FontMetrics fm = mPaint.getFontMetrics();
	    return (int)Math.ceil(fm.descent - fm.top) + 2;
	}
    }
}
