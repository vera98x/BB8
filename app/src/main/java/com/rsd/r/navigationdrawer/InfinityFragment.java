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
import static com.rsd.r.navigationdrawer.R.id.seekBarInfinity;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfinityFragment extends Fragment implements RobotChangedStateListener, ResponseListener, SeekBar.OnSeekBarChangeListener {

    boolean startPos = false;
    float positionX_start;
    float positionY_start;
    float heading_start;
    float realDis;
    float heading_cur = 0;
    boolean turnRight = true;
    FloatingActionButton btn_infinityStop;
    FloatingActionButton btn_infinityStart;
    private SeekBar seekBar;
    public TextView tV_velocityIF;
    private Context classContext;

    public InfinityFragment(Context context) {
        // de ontvangen context wordt in een variable gestopt
        classContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView;
        // stopt de layout in een variabele
        myFragmentView = inflater.inflate(R.layout.fragment_infinity, container, false);
        btn_infinityStop = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_infinityStop);
        btn_infinityStart = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_infinityStart);
        tV_velocityIF = (TextView) myFragmentView.findViewById(R.id.tV_velocityInfinity);
        // zet de tekst die is opgehaald via MainActivity
        tV_velocityIF.setText(tV_velocity_Text);
        // vind seekbar en set de progress
        seekBar = (SeekBar) myFragmentView.findViewById(seekBarInfinity);
        seekBar.setProgress((int) (velocity * 10 - 1));
        // hang een listener aan de seekbar
        seekBar.setOnSeekBarChangeListener(this);
        listenButton();
        return myFragmentView;
    }

    private void listenButton() {
        Log.e("Sphero", "listen" + BT.online);

        // als op een van de buttons wordt gedrukt
        btn_infinityStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // als online is
                if (BT.online) {
                    // sta stil
                    _robot.drive(0, 0);
                    // driveInfinityStop wordt meerdere keren aangeroepen, zodat deze niet perongelijk wordt overgeslagen door de vele commandos
                    driveInfinityStop();
                    driveInfinityStop();
                    driveInfinityStop();
                    Log.e("Sphero", "btn_stop is aangeklikt");
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_infinityStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (BT.online) {
                    Log.e("Sphero", "btn_random is aangeklikt");
                    driveInfinity();
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void driveInfinity() {
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

    public void driveInfinityStop() {
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
            _robot.drive(0, (float) velocity);
        }
    }

    @Override
    public void handleStringResponse(String s, Robot robot) {

    }


    public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
        Log.e("Sphero", "handleAsyncMessage");
        if (asyncMessage instanceof CollisionDetectedAsyncData) {
            Log.e("Sphero", "colission!!");
            //Collision occurred.
            // als robot niet null is
            if (_robot != null) {
                // sta stil
                _robot.drive(_robot.getLastHeading(), 0);
                // geef een andere kleur in BB-8
                _robot.setLed((float) 0.7, (float) 0.3, (float) 0.2);
                // wacht 0.1 sec
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // zet led weer normaal
                _robot.setLed(0.5f, 0.5f, 0.5f);
                // rij weer met dezelfde heading en snelheid
                _robot.drive(_robot.getLastHeading(), (float) velocity);
            }
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
            float maxDis = (float) ((2 * Math.PI * 8) / 360);
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
                // als heading_cur groter dan 360 is, turnRight is false
                if (heading_cur >= 360) {
                    turnRight = false;
                    // als heading_cur kleiner is dan 0, is turnRicht true
                } else if (heading_cur <= 0) {
                    turnRight = true;
                }

                if (turnRight) {
                    // tel er 3 graden bij op
                    heading_cur = heading_prev + 3;
                } else {
                    // haal er 3 graden af
                    heading_cur = heading_prev - 3;
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
        tV_velocityIF.setText(tV_velocity_Text);
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
        // Doordat er veel commandos worden afgevuurd, worden er, zodat dit commando zeker wordt opgepakt, meerdere keren de driveInfinityStop aangroepen.
        driveInfinityStop();
        driveInfinityStop();
        driveInfinityStop();
        Log.e("sphero", "onstop Infinity");

    }

}
