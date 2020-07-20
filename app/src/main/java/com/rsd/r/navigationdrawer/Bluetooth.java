package com.rsd.r.navigationdrawer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.Sphero;
import com.orbotix.command.RGBLEDOutputCommand;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.le.DiscoveryAgentLE;
import com.orbotix.le.RobotRadioDescriptor;
import com.orbotix.subsystem.SensorControl;

import java.util.List;

import static com.rsd.r.navigationdrawer.MainActivity.tVGlobStatus;
import static java.lang.Thread.sleep;


/**
 * Created by R on 26-11-2016.
 */

public class Bluetooth implements RobotChangedStateListener {

    boolean online = false;
    boolean request = false;
    boolean foundRobot = false;

    public enum BTStatus{
        CONNECTED("BB-8 has been found and is connected"),
        FOUND("BB-8 has been found, but isn't connected. Could you put BB-8 closer to the device?"),
        ENABLED("Bluetooth is enabled"),
        NOTENABLED("Bluetooth is NOT enabled"),
        ERROR("Something didn't work");

        private String message;

        BTStatus(String s) {
            message = s;
        }
        // getter
        public String getMessage()
        {
            return message;
        }
    }

    BTStatus statusBTConnection;
    // Bluetooth adapter (om te blijven kijken of er veranderingen optreden)
    public BluetoothAdapter mBluetoothAdapter;

    // om BB-8 te vinden
    private DiscoveryAgentLE _discoveryAgent;

    // The uiteindelijk connected robot
    public static ConvenienceRobot _robot;

    // de context die meegeleverd wordt vanuit MainActivity
    private Context classContext = null;


    public Bluetooth(Context myContext) {
        // de ontvangen context wordt in een variable gestopt
        classContext = myContext;

        // Registreert de broadcasts op BluetoothAdapter als deze van staat verandert
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        Activity activity = (Activity) classContext;
        activity.registerReceiver(mReceiver, filter);

        // Checkt of Bluetooth of location enabled zijn
        reactivateBluetoothOrLocation();

        // van de Dicovery wordt er een RobotStateListener toegevoegd
        //DiscoveryAgentLE.getInstance().addRobotStateListener(this);

        // aanroep methode
        startDiscovery();

    }

    // houd in de gaten of er een verandering optreed met de bluetooth
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Sphero", "Start onReceive!!!!!!!");
            final String action = intent.getAction();
            // als de actie verandert
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // verander de tekst op het bluetooth scherm
                        getConnectionStatus();
                        tVGlobStatus.setText(statusBTConnection.getMessage());
                        break;
                    case BluetoothAdapter.STATE_ON:

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // verander de tekst op het bluetooth scherm
                        getConnectionStatus();
                        tVGlobStatus.setText(statusBTConnection.getMessage());
                        startDiscovery();
                        Log.e("Sphero", "Started Discovery again!!");
                        break;
                }
            }
        }
    };

    // om te kijken welke robots er beschikbaar zijn
    private DiscoveryAgentEventListener _discoveryAgentEventListener = new DiscoveryAgentEventListener() {
        @Override
        public void handleRobotsAvailable(List<Robot> robots) {
            Log.e("Sphero", "Found " + robots.size() + " robots");
            foundRobot = true;
            // verander de tekst op het bluetooth scherm
            getConnectionStatus();
            tVGlobStatus.setText(statusBTConnection.getMessage());

            for (Robot robot : robots) {
                // haal alle mogelijke robots op
                Log.e("Sphero", "Robot name: " + robot.getName());
            }
        }

    };

    // houd in de gaten of de situatie met de connecting met de robot verandert
    public RobotChangedStateListener _robotStateListener = new RobotChangedStateListener() {
        @Override
        public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType robotChangedStateNotificationType) {
            Log.e("Sphero", "Start handleRobotChangedState!!!!!!!");

            switch (robotChangedStateNotificationType) {
                case Online:
                    online = true;
                    // verander de tekst op het bluetooth scherm
                    getConnectionStatus();
                    tVGlobStatus.setText(statusBTConnection.getMessage());
                    Log.e("sphero", "is " + online);
                    // stop met zoeken naar andere robots
                    stopDiscovery();


                    Log.e("Sphero", "Robot " + robot.getName() + " is Online!");
                    // sla de gevonden robot op in een variabele
                    _robot = new Sphero(robot);

                    // verander het licht, zodat het duidelijk is dat BB-8 connected is
                    _robot.setLed(1f, 1f, 1f);
                    _robot.drive(320, 0);
                    _robot.sendCommand(new RGBLEDOutputCommand(0.5f, 0.5f, 0.5f));
                    _robot.drive(0, 0);

                    // kijkt of er een botsing optreed
                    _robot.enableCollisions(true);
                    long sensorFlag = SensorFlag.VELOCITY.longValue() | SensorFlag.LOCATOR.longValue();
                    // zet de sensoren aan
                    _robot.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE10);
                    break;

                case Offline:
                    online = false;
                    getConnectionStatus();
                    tVGlobStatus.setText(statusBTConnection.getMessage());
                    Log.e("Sphero", "Robot " + robot.getName() + " is now Offline!");
                    startDiscovery();
                    break;
                case Connecting:
                    Log.e("Sphero", "Connecting to " + robot.getName());
                    break;
                case Connected:
                    Log.e("Sphero", "Connected to " + robot.getName());
                    break;
                case Disconnected:
                    Log.e("Sphero", "Disconnected from " + robot.getName());
                    foundRobot = false;
                    online = false;
                    getConnectionStatus();
                    tVGlobStatus.setText(statusBTConnection.getMessage());

                    if (!mBluetoothAdapter.isEnabled()) {
                        String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                        Activity activity = (Activity) classContext;
                        activity.startActivityForResult(new Intent(actionRequestEnable), 0);
                    }
                    startDiscovery();
                    break;
                case FailedConnect:
                    Log.e("Sphero", "Failed to connect to " + robot.getName());
                    startDiscovery();
                    break;
            }
        }
    };

    // RobotChangedStateListener, doet weinig, maar is verplicht om erin te hebben
    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType robotChangedStateNotificationType) {

    }


    private void startDiscovery(){
        Log.e("sphero", "startDiscovery");
        // als de bluetooth aan staat wordt hiervan een melding op het bluetoothscherm gemaakt
        if (mBluetoothAdapter.isEnabled()) {
            Toast.makeText(classContext, getConnectionStatus(),
                    Toast.LENGTH_SHORT).show();

        } else {

            //zo niet, maak hiervan ook een melding op het bluetoothscherm
            // geef een popup met de vraag of bluetooth aan mag. Zo ja, bluetooth wordt automatisch aangezet
            Toast.makeText(classContext, getConnectionStatus(),
                    Toast.LENGTH_SHORT).show();
            getConnectionStatus();
            if (!online && !request) {
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                Activity activity = (Activity) classContext;
                activity.startActivityForResult(new Intent(actionRequestEnable), 0);

            }
        }
        // zodat het niet 2x gevraagd wordt, alleen bij 1ste aanroep discovery:
        request = true;

        // even een pauze, zodat bluetooth aankan, voordat het systeem verder gaat
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // er wordt een DiscoveryAgentLE aangelegd
        _discoveryAgent = DiscoveryAgentLE.getInstance();

        // er wordt een listener hiervan toegevoegd, die melding maakt wanneer er een robot gevonden wordt
        _discoveryAgent.addDiscoveryListener(_discoveryAgentEventListener);

        // er wordt een _robotStateListener toegevoegd, deze houd in de gaten of de connectie met de robot verandert
        _discoveryAgent.addRobotStateListener(_robotStateListener);

        // Dit is een beschrijving waaraan de bluetoothnaam aan moet worden voldoen, zo vindt het alleen maar
        // BB-8 robots
        RobotRadioDescriptor robotRadioDescriptor = new RobotRadioDescriptor();
        robotRadioDescriptor.setNamePrefixes(new String[]{"BB-"});
        _discoveryAgent.setRadioDescriptor(robotRadioDescriptor);

        // Om een robot te zoeken is _discoveryAgent.startDiscovery(classContext) nodig
        // dit wordt via try/catch gedaan om een error op te vangen, wat kan optreden als Bluetooth uit staat
        try {
            _discoveryAgent.startDiscovery(classContext);
            Log.e("Sphero", String.valueOf(_discoveryAgent.getOnlineRobots()));
            Log.e("Sphero", String.valueOf(_discoveryAgent.getMaxConnectedRobots()));
            Log.e("Sphero", String.valueOf(_discoveryAgent.getConnectStrategy()));

        } catch (DiscoveryException e) {
            Log.e("Sphero", "Discovery Error: " + e);
            e.printStackTrace();
        }
    }

    private void stopDiscovery() {
        // als o.a een robot is gevonden, is er geen discovery meer nodig en wordt deze uitgezet
        _discoveryAgent.stopDiscovery();

        // ook de listeners mogen uit
        _discoveryAgent.removeDiscoveryListener(_discoveryAgentEventListener);
        _discoveryAgent.removeRobotStateListener(_robotStateListener);
        _discoveryAgent = null;
    }

    private void reactivateBluetoothOrLocation() {

        // Checkt of Bluetooth nog aan staat
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            // er wordt een toast op het scherm gezet als bluetooth uit is gegaan
            Toast.makeText(classContext, "Bluetooth is NOT enabled anymore",
                    Toast.LENGTH_SHORT).show();
            if (online) {
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                Activity activity = (Activity) classContext;
                activity.startActivityForResult(new Intent(actionRequestEnable), 0);
            }
        }
    }

    public void sleepRobotStuff() {
        if (_discoveryAgent != null) {
            // als de app wordt afgesloten, mag de removeRobotStateListener uit
            _discoveryAgent.removeRobotStateListener(this);

            // alle robots die zijn connect, worden in slaapstand gezet
            for (Robot r : _discoveryAgent.getConnectedRobots()) {
                // disconnect de robot en zet het standby
                r.sleep();
            }
        }
    }

    public void Stop() {
        // methode aanroep
        sleepRobotStuff();

        // schakelt broadcast listeners uit
        Activity activity = (Activity) classContext;
        if (mReceiver != null) {
            activity.unregisterReceiver(mReceiver);
        }
        // stopt het systeem
        System.exit(0);
    }

    public String getConnectionStatus() {
        // als de robot online en dus connected is, geef deze melding:
        if (online) {
            statusBTConnection = BTStatus.CONNECTED; //"BB-8 has been found and is connected";
        // als de robot gevonden, maar nog neit connected is, geef deze melding:
        } else if (foundRobot) {
            statusBTConnection = BTStatus.FOUND; //"BB-8 has been found, but isn't connected. Could you put BB-8 closer to the device?";
        // als de bluetooth aan staat, geef deze melding:
        } else if (mBluetoothAdapter.isEnabled()) {
            statusBTConnection = BTStatus.ENABLED;  //"Bluetooth is enabled";
        // als de bluetooth uit staat, geef deze melding:
        } else if (!mBluetoothAdapter.isEnabled()) {
            statusBTConnection = BTStatus.NOTENABLED;  //"Bluetooth is NOT enabled";
        // als er iets anders optreedt (just in case), geef deze melding:
        } else {
            statusBTConnection = BTStatus.ERROR; //"something didn't work";
        }
        return statusBTConnection.getMessage();
    }

}
