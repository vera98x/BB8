package com.rsd.r.navigationdrawer;

/**
 * Created by R on 15-12-2016.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class PlayGifView extends View{

    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private Movie mMovie;

    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;

    @SuppressLint("NewApi")
    public PlayGifView(Context context, AttributeSet attrs) {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super(context, attrs);

        // als de build versie hoger is dan Honeycomb is, er een andere LayerType geset worden
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setImageResource(int mvId){
        // id wordt doorgegeven naar een variable
        this.mMovieResourceId = mvId;
        // opent de file en docodeert de animated gif
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        // trigger de grafische interface om het scherm opnieuw op te bouwen
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // als er een mMovie is, set de afmetingen
        if(mMovie != null){
            setMeasuredDimension(mMovie.width(), mMovie.height());
        }else{
            // set de afmetingen van een de voorgestelde afmeting van het systeem zelf
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // als er een mMovie is
        if (mMovie != null){
            updateAnimtionTime();
            drawGif(canvas);
            // herteken het, zonder dat er een foutmelding gegeven wordt
            invalidate();
        }else{
            drawGif(canvas);
        }
    }

    private void updateAnimtionTime() {
        long now = android.os.SystemClock.uptimeMillis();
        // bepaal de start van de gif
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        // sla de duur van de gif op
        int dur = mMovie.duration();
        // als de duur 0 is
        if (dur == 0) {
            // zet de duur dan als de default duur
            dur = DEFAULT_MOVIEW_DURATION;
        }
        // bepaal de huidige tijd van hoever het gifje nu is met afspelen
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawGif(Canvas canvas) {
        // laat het fragment zien van de gif in die bepaalde tijd
        mMovie.setTime(mCurrentAnimationTime);
        // teken deze op het scherm
        mMovie.draw(canvas, 0, 0);
        canvas.restore();
    }

}
