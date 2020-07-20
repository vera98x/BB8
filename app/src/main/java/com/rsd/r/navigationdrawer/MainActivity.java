package com.rsd.r.navigationdrawer;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;

    public static Bluetooth BT;
    public static double velocity = 0.3;
    public static String tV_velocity_Text = "Velocity = 30% ";
    public static TextView tVGlobStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // de content van de layout wordt op het scherm getoont
        setContentView(R.layout.activity_main);

        // zet het hoofdfragment op het scherm, er wordt een nieuwe BluetoothFragment aangeroepen, waarmee de context van
        // deze pagina als parameter wordt meegegeven
        BluetoothFragment fragment = new BluetoothFragment(this);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // vervangt het huidige fragment met BluetoothFragment
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // stelt de transactie in
        fragmentTransaction.commit();

        // toolbar wordt opgevraagd via het id toolbar en wordt in een variable gestopt
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // de navigatiebar wordt via de variabele drawer_layout opgehaald en
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        // navigationView wordt gevult door een navigatieview en hierop wordt een listener geplaatst
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // aanroep methode robotCreate
        robotCreate();
    }

    private void robotCreate() {
        // bluetooth wordt aangeroepen, en de context wordt meegegeven als parameter
        BT = new Bluetooth(this);

    }

    // methode van de listener van de navigationView
    // als op het menu geklikt wordt, wordt de drawer_layout getoond of gesloten
    // dit ligt eraan of deze al open of dicht was
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // methode van de listener van de navigationView
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // haalt op welk id het aangeklikte item had
        int id = item.getItemId();

        // als het id met een van de onderstaande opties overeenkomt, wordt dit Fragment geopend
        // en vervangt het het huidige fragment
        if (id == R.id.nav_bluetooth) {
            BluetoothFragment fragment = new BluetoothFragment(this);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_front) {
            // set het fragment
            BacklightFragment fragment = new BacklightFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_driving) {
            DrivingFragment fragment = new DrivingFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_square) {
            SquareFragment fragment = new SquareFragment(this);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_circle) {
            CircleFragment fragment = new CircleFragment(this);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_infinity) {
            InfinityFragment fragment = new InfinityFragment(this);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_route) {
            RandomFragment fragment = new RandomFragment(this);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        }
        else if (id == R.id.nav_exit) {
            // roept het stopproces aan en sluit de app
            BT.Stop();

        }

        // zodra er is geklikt, wordt het menu weer gesloten
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
