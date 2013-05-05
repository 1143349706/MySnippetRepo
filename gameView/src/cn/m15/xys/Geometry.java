package cn.m15.xys;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

public class Geometry extends Activity {
    public int mScreenWidth = 0;
    public int mScreenHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	setContentView(new GeometryView(this));
	super.onCreate(savedInstanceState);

    }

    class GeometryView extends View {
	Paint mPaint = null;

	public GeometryView(Context context) {
	    super(context);
	    mPaint = new Paint();
	    mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	
	    //���û�����ɫ Ҳ���Ǳ�����ɫ
	    canvas.drawColor(Color.WHITE);
	   
	    mPaint.setColor(Color.BLACK);
	    canvas.drawText("�����޹��򼸺�ͼ��ร�����", 150, 30, mPaint);
	    
	    //����һ����
	    mPaint.setColor(Color.BLACK);
	    mPaint.setStrokeWidth(4);
	    canvas.drawLine(0, 0, 100, 100, mPaint);
	    
	    //����һ������
	    mPaint.setColor(Color.YELLOW);
	    canvas.drawRect(0, 120, 100, 200, mPaint);
	    
	    //����һ��Բ��
	    mPaint.setColor(Color.BLUE);
	    canvas.drawCircle(80, 300, 50, mPaint);
	    
	    //����һ����Բ
	    mPaint.setColor(Color.CYAN);
	    canvas.drawOval(new RectF(300,370,120,100), mPaint);
	    
	    //���ƶ����
	    mPaint.setColor(Color.BLACK);
	    Path path = new Path();
	    path.moveTo(150+5 , 400 -50);
	    path.lineTo(150+45, 400 - 50);
	    path.lineTo(150+30, 460 - 50);
	    path.lineTo(150+20, 460 - 50);
	    path.close();
	    canvas.drawPath(path, mPaint);
	    
	}
    }
}