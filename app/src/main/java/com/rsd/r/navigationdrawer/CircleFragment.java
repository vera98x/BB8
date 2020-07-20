package com.rsd.r.navigationdrawer;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orbotix.async.CollisionDetectedAsyncData;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;

import static com.rsd.r.navigationdrawer.Bluetooth._robot;
import static com.rsd.r.navigationdrawer.MainActivity.BT;
import static com.rsd.r.navigationdrawer.MainActivity.tV_velocity_Text;
import static com.rsd.r.navigationdrawer.MainActivity.velocity;
import static com.rsd.r.navigationdrawer.R.id.seekBarCircle;


/**
 * A simple {@link Fragment} subclass.
 */
public class CircleFragment extends Fragment implements RobotChangedStateListener, ResponseListener, SeekBar.OnSeekBarChangeListener {

    boolean startPos = false;
    float positionX_start;
    float positionY_start;
    float heading_start;
    float realDis;
    FloatingActionButton btn_circleStop;
    FloatingActionButton btn_circleStart;
    private SeekBar seekBar;
    public TextView tV_velocityC;
    private Context classContext;


    public CircleFragment(Context context) {
        // de ontvangen context wordt in een variable gestopt
        classContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView;
        // stopt de layout in een variabele
        myFragmentView = inflater.inflate(R.layout.fragment_circle, container, false);
        // haalt layout-onderdelen via id op en slaat op in een variabele
        btn_circleStart = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_circleStart);
        btn_circleStop = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_circleStop);
        tV_velocityC = (TextView) myFragmentView.findViewById(R.id.tV_velocityCircle);
        // zet de tekst die is opgehaald via MainActivity
        tV_velocityC.setText(tV_velocity_Text);
        // vind seekbar en set de progress
        seekBar = (SeekBar) myFragmentView.findViewById(seekBarCircle);
        seekBar.setProgress((int) (velocity * 10 - 1));
        // hang een listener aan de seekbar
        seekBar.setOnSeekBarChangeListener(this);
        listenButton();
        return myFragmentView;
    }

    private void listenButton() {
        Log.e("Sphero", "listen" + BT.online);

        // als op een van de buttons wordt gedrukt
        btn_circleStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // als online is
                if (BT.online) {
                    // sta stil
                    _robot.drive(0, 0);
                    // driveCircleStop wordt meerdere keren aangeroepen, zodat deze niet perongelijk wordt overgeslagen door de vele commandos
                    driveCircleStop();
                    driveCircleStop();
                    driveCircleStop();
                    Log.e("Sphero", "btn_stop is aangeklikt");
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_circleStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (BT.online) {
                    Log.e("Sphero", "btn_random is aangeklikt");
                    driveCircle();
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void driveCircle() {
        Log.e("Sphero", "randomDrive");
        // als robot niet null is
        if (_robot != null) {
            // rijd met een bepaalde snelheid
            _robot.drive(0, (float) velocity);
            // enable listeners
            _robot.enableCollisions(true);
            _robot.addResponseListener(this);
        }

    }

    public void driveCircleStop() {
        if (_robot != null) {
            // sta stil
            _robot.drive(0, 0);
            // haal listener weg
            _robot.removeResponseListener(this);
            startPos = false;
        }
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType robotChangedStateNotificationType) {

    }

    @Override
    public void handleResponse(DeviceResponse deviceResponse, Robot robot) {
        if (_robot != null) {
            _robot.drive(0, (float) 0.1);
        }
    }

    @Override
    public void handleStringResponse(String s, Robot robot) {

    }


    public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
        Log.e("Sphero", "handleAsyncMessage");
        if (asyncMessage instanceof CollisionDetectedAsyncData) {
            Log.e("Sphero", "colission!!");
            //er is een collision, wordt niets mee gedaan
        }

        if (asyncMessage instanceof DeviceSensorAsyncMessage) {
            if (!startPos) {
                // haal startposities op
                positionX_start = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
                positionY_start = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
                // haal current heading op (0)
                heading_start = _robot.getLastHeading();
                // zet startPos true zodat hier niet meer doorgeen gegaan wordt
                startPos = true;
            }
            // haal huigige x en y op
            float positionX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
            float positionY = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
            // bereken de diagonaal die wordt afgelegd
            realDis = (float) Math.sqrt(Math.pow(positionX_start - positionX, 2) + Math.pow(positionY_start - positionY, 2));
            // bereken maxDis
            float maxDis = (float) ((2 * Math.PI * 10) / 360);
            Log.e("sphero", "x: " + (positionX_start - positionX) + " y: " + (positionY_start - positionY) + " realDis " + realDis);

            // als realDis groter of gelijk is aan maxDis
            if (realDis >= maxDis) {
                Log.e("Sphero", "turn");
                // vervang startwaarden
                positionX_start = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
                positionY_start = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
                realDis = 0;
                // haal laatste richting op
                float heading_prev = _robot.getLastHeading();
                // doe er 3 bij
                float heading_cur = heading_prev + 3;
                // heading is een range van 0 - 360
                if (heading_cur > 360) {
                    heading_cur = heading_cur - 360;
                }
                Log.e("Sphero", "end of corner" + heading_start + " " + heading_cur);
                if (_robot != null) {
                    // rijd met de nieuwe heading en de ingestelde snelheid
                    _robot.drive(heading_cur, (float) velocity);
                }
            }

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // range 1-10, maar default minimum is 0
        progress = progress + 1;
        // bereken snelheid
        velocity = (double) progress / 10;
        // maak de nieuwe tekst en zet de tekst
        tV_velocity_Text = "Velocity = " + (progress * 10 + "% ");
        tV_velocityC.setText(tV_velocity_Text);
        Log.e("sphero", "this is the progress: " + velocity);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("sphero", "this is the start: ");

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e("sphero", "this is the stop: ");
    }

    @Override
    public void onStop() {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onStop();
        // zodra dit fragment gestopt wordt, moet de robot niet meer het parcours gaan lopen.
        // Doordat er veel commandos worden afgevuurd, worden er, zodat dit commando zeker wordt opgepakt, meerdere keren de driveCircleStop aangroepen.
        driveCircleStop();
        driveCircleStop();
        driveCircleStop();
        Log.e("sphero", "onstop Circle");

    }

}
