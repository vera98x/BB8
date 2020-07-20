package com.rsd.r.navigationdrawer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static com.rsd.r.navigationdrawer.MainActivity.BT;

/**
 * Created by R on 3-12-2016.
 */

public class ViewCircle_backlight extends View {

    public static interface OnSliderMovedListener {

        public void onSliderMoved(double pos);
    }

    private Paint mPaint = new Paint();
    private OnSliderMovedListener mListener;
    private int mThumbX;
    private int mThumbY;

    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;

    private Drawable mThumbImage;
    private int mPadding;
    private int mThumbSize;
    private int mThumbColor;
    private int mBorderColor;
    private int mBorderThickness;
    private double mStartAngle;
    private double mAngle = mStartAngle;
    private boolean mIsThumbSelected = false;
    public Context myContext;
    public Context classContext;

    public double angle = 0;
    private float angle_default;

    public ViewCircle_backlight(Context context) {

        this(context, null);

    }

    public ViewCircle_backlight(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewCircle_backlight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // de context die wordt meegegeven, wordt in een variable opgeslagen
        classContext = context;
        // set deze view
        init(context, attrs, defStyleAttr);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // margin is 60
        int margin = 60;
        // als w > h dan geeft h de parent size, geld ook andersom
        int smallerDim = w > h ? h : w;


        // vind de grootst mogelijke zijden van het vierkant van het scherm
        int largestCenteredSquareLeft = (w - smallerDim) / 2;
        int largestCenteredSquareTop = (h - smallerDim) / 2;
        int largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        int largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        // bepalen en opslaan van mCircleCenterX en mCircleCenterY
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2 + margin;
        // radius cirkel berekenen
        mCircleRadius = smallerDim / 2 - mBorderThickness / 2 - mPadding - margin / 2;

        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // haal alle stylatributen op van de style CircularSlider
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0);

        // lees de array uit
        float startAngle = a.getFloat(R.styleable.CircularSlider_start_angle, (float) Math.PI / 2);
        float angle = a.getFloat(R.styleable.CircularSlider_angle, (float) Math.PI / 2);
        angle_default = angle;
        int thumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_thumb_size, 50);
        int thumbColor = a.getColor(R.styleable.CircularSlider_thumb_color, Color.GRAY);
        int borderThickness = a.getDimensionPixelSize(R.styleable.CircularSlider_border_thickness, 20);
        int borderColor = a.getColor(R.styleable.CircularSlider_border_color, Color.RED);
        Drawable thumbImage = a.getDrawable(R.styleable.CircularSlider_thumb_image);

        // sla deze variabelen in globale variabelen op
        mStartAngle = startAngle;
        mAngle = angle;
        mThumbSize = thumbSize;
        mBorderThickness = borderThickness;
        mBorderColor = borderColor;
        mThumbImage = thumbImage;
        mThumbColor = thumbColor;
        myContext = context;


        // padding toekennen- check build versie, vanwege een layout compatibility
        int padding;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int all = getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop() + getPaddingEnd() + getPaddingStart();
            padding = all / 6;
        } else {
            // padding berekenen
            padding = (getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop()) / 4;
        }
        mPadding = padding;
        // zodat lokale variablen dit niet in het memory blijven, is anders zonde van het geheugen
        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onDraw(canvas);

        // bepaalt de style van de buitenste ring
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderThickness);
        mPaint.setAntiAlias(true);
        // tekent de cirkel op de berekende posities
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mPaint);

        // berekend positie van je vinger, deze waarden zijn via updateSliderState() verkregen
        if (mIsThumbSelected) {
            mThumbX = (int) (mCircleCenterX + mCircleRadius * Math.cos(mAngle));
            mThumbY = (int) (mCircleCenterY - mCircleRadius * Math.sin(mAngle));
        } else {
            // als de knop niet wordt aangeraakt, zet de knop onderaan de cirkel
            mThumbX = (int) (mCircleCenterX + mCircleRadius * Math.cos(angle_default));
            mThumbY = (int) (mCircleCenterY - mCircleRadius * Math.sin(angle_default));
        }

        // set de style van de knop
        mPaint.setColor(mThumbColor);
        mPaint.setStyle(Paint.Style.FILL);
        // tekent de knop op de bepaalde positie
        canvas.drawCircle(mThumbX, mThumbY, mThumbSize, mPaint);

    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // als de duim het scherm begint met aanraken
                // haal x en y op
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                // als de duim op de positie van de knop zit
                if (x < mThumbX + mThumbSize && x > mThumbX - mThumbSize && y < mThumbY + mThumbSize && y > mThumbY - mThumbSize) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    // maak mIsThumbSelected true
                    mIsThumbSelected = true;
                    // roep updateSliderState aan met de bijbehorende x en y
                    updateSliderState(x, y);
                    // als de robot online is (gedefineerd in Bluetooth file)
                    if (BT.online) {
                        // doe de lampjes uit
                        BT._robot.setLed(0, 0, 0);
                        // zet backlight aan
                        BT._robot.setBackLedBrightness(1);
                    } else {
                        // geef een toast met een melding dat BB-8 niet geconnect is
                        // doe verder niets
                        Toast.makeText(classContext, "You are not connected with BB-8!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // als de duim beweegt
                if (mIsThumbSelected) {
                    // haal x en y op
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    // roep updateSliderState aan met de bijbehorende x en y
                    updateSliderState(x, y);
                    // dit is voor de berekening nodig, anders deel je door 0 en crasht het programma
                    if ((x - mCircleCenterX) != 0) {
                        // berekent de hoek
                        angle = Math.atan2(y - mCircleCenterY, x - mCircleCenterX) * (180.0 / Math.PI);
                        // verschuift de graden op het scherm zo, dat 0grader onder aan de cirkel is
                        angle = angle + 270;
                    } else {
                        // berekent de hoek
                        angle = Math.atan2(y - mCircleCenterY, 0.0001) * (180.0 / Math.PI);
                        // verschuift de graden op het scherm zo, dat 0grader onder aan de cirkel is
                        angle = angle + 270;
                    }

                    if (angle < 0)  // verander -1 tot -180 naar 180 tot 360
                        angle = 360 + angle;

                    Log.e("sphero", "drive " + angle);
                    // als de robot niet null is
                    // verplaats met de beijbehorende hoek, snelheid 0. Alleen draaien op eigen positie
                    if (BT._robot != null) {
                        BT._robot.drive((float) angle, 0);
                    }

                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                // als de duim losgelaten wordt
                getParent().requestDisallowInterceptTouchEvent(false);
                // zet mIsThumbSelected op false
                mIsThumbSelected = false;
                // als de robot niet null is
                if (BT._robot != null) {
                    // stel het nulpunt in
                    // zet backlight uit
                    // doe de lampjes van BB-8 aan
                    BT._robot.setZeroHeading();
                    BT._robot.setBackLedBrightness(0);
                    BT._robot.setLed(0, (float) 0.5, (float) 0.5);
                }
                break;
            }
        }

        // hertekent het hele scherm, zonder een foutmelding te geven
        invalidate();
        return true;
    }

    private void updateSliderState(int touchX, int touchY) {
        // berekent distanceX en distanceY
        int distanceX = touchX - mCircleCenterX;
        int distanceY = mCircleCenterY - touchY;
        // berekent afstand van de diagonaal
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        // berekent hoek
        mAngle = Math.acos(distanceX / c);
        // als distanceY > 0
        if (distanceY < 0) {
            // geen negatieve getallen
            mAngle = -mAngle;
        }
        // als listener niet null is
        if (mListener != null) {
            // meld nieuwe positie
            mListener.onSliderMoved((mAngle - mStartAngle) / (2 * Math.PI));
        }
    }
}
