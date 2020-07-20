package com.rsd.r.navigationdrawer;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomFragment extends Fragment implements RobotChangedStateListener, ResponseListener {

    int randomNr = 0;
    FloatingActionButton btn_randomStart;
    FloatingActionButton btn_randomStop;
    private Context classContext;

    public RandomFragment(Context context) {
        classContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView;
        // stopt de layout in een variabele
        myFragmentView = inflater.inflate(R.layout.fragment_random, container, false);
        // haalt layout-onderdelen via id op en slaat op in een variabele
        btn_randomStop = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_randomStop);
        btn_randomStart = (FloatingActionButton) myFragmentView.findViewById(R.id.btn_randomStart);
        listenButton();
        return myFragmentView;

    }


    private void listenButton(){
        Log.e("Sphero", "listen" + BT.online);

        // als op een van de buttons wordt gedrukt
        btn_randomStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // als online is
                if (BT.online) {
                    // randomDriveStop wordt meerdere keren aangeroepen, zodat deze niet perongelijk wordt overgeslagen door de vele commandos
                    randomDriveStop();
                    randomDriveStop();
                    randomDriveStop();
                    Log.e("Sphero", "btn_stop is aangeklikt");
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_randomStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (BT.online) {
                    Log.e("Sphero", "btn_random is aangeklikt");
                    randomDrive();
                } else {
                    // geef een toast met een melding dat BB-8 niet geconnect is
                    Toast.makeText(classContext, "You are not connected with BB-8!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void randomDrive() {
        Log.e("Sphero", "randomDrive");
        // als robot niet null is
        if(_robot != null) {
            // rijd met een bepaalde snelheid
            _robot.drive(0, (float) 0.3);
            // enable listeners
            _robot.enableCollisions(true);
            _robot.addResponseListener(this);
        }

    }
    public void randomDriveStop() {
        if(_robot != null) {
            // sta stil
            _robot.drive(0, 0);
            // haal listener weg
            _robot.removeResponseListener(this);
        }
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType robotChangedStateNotificationType) {

    }

    @Override
    public void handleResponse(DeviceResponse deviceResponse, Robot robot) {
        if(_robot != null) {
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
            //Als er een botsing is
            // haal een random getal op
            java.util.Random rand = new java.util.Random();
            int randomNr = rand.nextInt(361); // range 0 - 360
            // random snelheid bepalen
            float randomVl_int = (float) (rand.nextInt(4) + 2); // range 2 - 5
            float randomVl = randomVl_int / 10; // velocity tussen 0.2 and 0.5
            Log.e("Sphero", "btn_random, " + randomNr + " " + randomVl);
            // als robot niet null is
            if(_robot != null) {
                //rij met de bijbehorende richting en snelheid
                _robot.drive(randomNr, randomVl);
            }
        }

        if (asyncMessage instanceof DeviceSensorAsyncMessage) {
            // haal snelheden op
            float velocityX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getVelocity().x;
            float velocityY = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getVelocity().y;

            if (velocityX < 0.001 && velocityY < 0.001) {
                Log.e("Sphero", "too slow");

                //robot zit vast/ staat stil
                // bepaal nieuwe richting
                int randomNr_old = randomNr;
                java.util.Random rand = new java.util.Random();
                int extraDeg = (rand.nextInt(41) + 20); // max 60 degrees
                randomNr = randomNr_old + extraDeg;
                // als richting > 360, haal er 360 af
                if (randomNr >= 360) {
                    randomNr = randomNr - 360;
                }

                // bepaal nieuwe snelheid
                float randomVl_int = (float) (rand.nextInt(4) + 2); // range 2 - 5
                float randomVl = randomVl_int / 10; // velocity between 0.2 and 0.5
                Log.e("Sphero", "btn_random, " + randomNr + " " + randomVl);
                // als robot niet null is
                if(_robot != null) {
                    // rij met de bijbehorende richting en snelheid
                    _robot.drive(randomNr, randomVl);
                }
            }
        }

    }

    @Override
    public void onStop()
    {
        // roept de superclasse hiervan aan omdat dit een parent constructor heeft
        super.onStop();
        // zodra dit fragment gestopt wordt, moet de robot niet meer het parcours gaan lopen.
        // Doordat er veel commandos worden afgevuurd, worden er, zodat dit commando zeker wordt opgepakt, meerdere keren de randomDriveStop aangroepen.
        randomDriveStop();
        randomDriveStop();
        randomDriveStop();
        Log.e("sphero", "onstop Random" );

    }

}
