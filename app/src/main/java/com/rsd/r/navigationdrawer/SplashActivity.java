package com.rsd.r.navigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

/**
 * Created by R on 13-12-2016.
 */

public class SplashActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onCreate(savedInstanceState);
        // wacht 2 seconden, zodat het splashscreen langer te zien is
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // haal MainActivity.class op
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}