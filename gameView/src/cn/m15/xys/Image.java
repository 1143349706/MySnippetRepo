package cn.m15.xys;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class Image extends Activity {
    ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	imageView = new ImageView(this);
	setContentView(R.layout.image);
	LinearLayout ll = (LinearLayout) findViewById(R.id.iamgeid);
	ll.addView(imageView);
	// �����ƶ�
	Button botton0 = (Button) findViewById(R.id.buttonLeft);
	botton0.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		imageView.setPosLeft();
	    }
	});

	// �����ƶ�
	Button botton1 = (Button) findViewById(R.id.buttonRight);
	botton1.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		imageView.setPosRight();
	    }
	});
	// ����ת
	Button botton2 = (Button) findViewById(R.id.buttonRotationLeft);
	botton2.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		imageView.setRotationLeft();
	    }
	});

	// ����ת
	Button botton3 = (Button) findViewById(R.id.buttonRotationRight);
	botton3.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View arg0) {
		imageView.setRotationRight();
	    }
	});

	// ��С
	Button botton4 = (Button) findViewById(R.id.buttonNarrow);
	botton4.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		imageView.setNarrow();
	    }
	});

	// �Ŵ�
	Button botton5 = (Button) findViewById(R.id.buttonEnlarge);
	botton5.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		imageView.setEnlarge();
	    }
	});

	super.onCreate(savedInstanceState);

    }

    class ImageView extends View {
	Paint mPaint = null;
	Bitmap bitMap = null;
	Bitmap bitMapDisplay = null;
	int m_posX = 120;
	int m_posY = 50;
	int m_bitMapWidth = 0;
	int m_bitMapHeight = 0;
	Matrix mMatrix = null;
	float mAngle = 0.0f;
	float mScale = 1f;//1Ϊԭͼ�Ĵ�С

	public ImageView(Context context) {
	    super(context);
	    mPaint = new Paint();
	    mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
	    bitMap = BitmapFactory.decodeResource(this.getResources(),
		    R.drawable.image);
	    bitMapDisplay = bitMap;
	    mMatrix = new Matrix();
	    // ��ȡͼƬ���
	    m_bitMapWidth = bitMap.getWidth();
	    m_bitMapHeight = bitMap.getHeight();
	}

	// �����ƶ�
	public void setPosLeft() {
	    m_posX -= 10;
	}

	// �����ƶ�
	public void setPosRight() {
	    m_posX += 10;
	}

	// ������ת
	public void setRotationLeft() {
	    mAngle--;
	    setAngle();
	}

	// ������ת
	public void setRotationRight() {
	    mAngle++;
	    setAngle();
	}

	// ��СͼƬ
	public void setNarrow() {
	    if (mScale > 0.5) {
		mScale -= 0.1;
		setScale();
	    }
	}

	// �Ŵ�ͼƬ
	public void setEnlarge() {
	    if (mScale < 2) {
		mScale += 0.1;
		setScale();
	    }
	}

	// �������ű���
	public void setAngle() {
	    mMatrix.reset();
	    mMatrix.setRotate(mAngle);
	    bitMapDisplay = Bitmap.createBitmap(bitMap, 0, 0, m_bitMapWidth,
		    m_bitMapHeight, mMatrix, true);
	}

	// ������ת����
	public void setScale() {
	    mMatrix.reset();
	    //float sx X������ 
	    //float sy Y������
	    mMatrix.postScale(mScale, mScale);
	    bitMapDisplay = Bitmap.createBitmap(bitMap, 0, 0, m_bitMapWidth,
		    m_bitMapHeight, mMatrix, true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.drawBitmap(bitMapDisplay, m_posX, m_posY, mPaint);
	    invalidate();
	}
    }
}