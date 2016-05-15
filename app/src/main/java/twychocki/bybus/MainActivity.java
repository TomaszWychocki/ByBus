package twychocki.bybus;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    GPSTracker gps;
    TextView bottom_text;
    TextView Middle_text;
    TextView direction;
    TextView lineText;
    TextView s;
    Location myLocation;
    AsyncTaskRunner runner;
    boolean status = false;

    List<BusStop> BusStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //107 -> Police osiedle Chemik
        BusStops.add(new BusStop("Kolonistów", 53.48815699999999, 14.585356000000047, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Nehringa Pomnik", 53.47944099999999, 14.58783399999993, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Pokoju", 53.475601, 14.589756999999963, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Bogumińska nż", 53.472016, 14.583276999999953, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Ogrody \"Przyjaźń\" nż", 53.468395594207685, 14.579444825649261, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Hoża", 53.46343599999999, 14.577307000000019, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Jana z Czarnolasu", 53.458658, 14.571942000000035, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Komuny Paryskiej", 53.45388999999999, 14.565382999999997, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Staw Brodowski", 53.450583, 14.56296199999997, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Wilcza Wiadukt", 53.446425, 14.56528000000003, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Sczanieckiej", 53.440234, 14.563472000000047, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Matejki", 53.43582699999999, 14.56066599999997, 107, "Police osiedle Chemik"));
        BusStops.add(new BusStop("Plac Rodła", 53.431542, 14.557007999999996, 107, "Police osiedle Chemik"));
        //5 -> Krzekowo
        BusStops.add(new BusStop("Plac Rodła", 53.431679, 14.555623999999966, 5, "Krzekowo"));
        BusStops.add(new BusStop("Plac Grunwaldzki", 53.432709, 14.547616000000062, 5, "Krzekowo"));
        BusStops.add(new BusStop("Plac Szarych Szeregów", 53.43304999999999, 14.541294999999991, 5, "Krzekowo"));
        BusStops.add(new BusStop("Piastów", 53.432184, 14.539132999999993, 5, "Krzekowo"));
        BusStops.add(new BusStop("Bohaterów Warszawy", 53.433069, 14.533323999999993, 5, "Krzekowo"));
        BusStops.add(new BusStop("Wawrzyniaka", 53.436342, 14.532293999999979, 5, "Krzekowo"));
        BusStops.add(new BusStop("Karłowicza", 53.439156, 14.520309999999995, 5, "Krzekowo"));
        BusStops.add(new BusStop("Poniatowskiego", 53.44089899999999, 14.512869000000023, 5, "Krzekowo"));
        BusStops.add(new BusStop("Konopnickiej", 53.442158, 14.508082000000059, 5, "Krzekowo"));
        BusStops.add(new BusStop("Brzozowskiego", 53.442985, 14.502617999999984, 5, "Krzekowo"));
        BusStops.add(new BusStop("Wernyhory", 53.444848, 14.49692600000003, 5, "Krzekowo"));
        BusStops.add(new BusStop("Zołnierska", 53.446842, 14.491829000000052, 5, "Krzekowo"));
        BusStops.add(new BusStop("Krzekowo", 53.448532, 14.488287000000014, 5, "Krzekowo"));


        bottom_text = (TextView) findViewById(R.id.distance);
        Middle_text = (TextView) findViewById(R.id.bus_stop_name);
        direction = (TextView) findViewById(R.id.direction);
        lineText = (TextView) findViewById(R.id.lineText);
        s = (TextView) findViewById(R.id.status_text);
        gps = new GPSTracker(MainActivity.this);
        runner = new AsyncTaskRunner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps.getLocation();
                if (gps.isGPSEnabled) {
                    findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                    Snackbar.make(view, "Śledzenie uruchomione", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    s.setText("Przystanek");
                    status = true;
                    runner.execute();
                }
                else
                    gps.showSettingsAlert();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            while(!isCancelled()) {
                if (gps.canGetLocation() && gps.isGPSEnabled) {
                    //if(gps.isGPSReady()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        myLocation = gps.getLocation();
                        int nearst = findNearstStop();
                        float dist = myLocation.distanceTo(BusStops.get(nearst).location);

                        String str = "Twoja lokalizacja:\nLat: " + latitude + "\nLong: " + longitude + "\nOdległość: " + String.format("%.0f", dist) + "m";
                        publishProgress(str, "Kierunek: " + BusStops.get(nearst).direction, BusStops.get(nearst).name, Integer.toString(BusStops.get(nearst).line), Boolean.toString(gps.isGPSReady()));
                    //}
                    //else
                    //{
                    //    publishProgress("x", "Oczekiwanie na sygnał GPS");
                    //}
                } else {
                    gps.showSettingsAlert();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            gps.stopUsingGPS();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            if(text[0] != "x") {
                bottom_text.setText(text[0]);
                direction.setText(text[1]);
                Middle_text.setText(text[2]);
                lineText.setText("Linia " + text[3]);
                s.setText("Przystanek " + text[4]);
            }
            else
                s.setText(text[1]);
        }
    }

    public int findNearstStop(){
        int n = 0;
        float d;
        Location myLocation = gps.getLocation();
        d = myLocation.distanceTo(BusStops.get(0).location);

        for(int i = 1; i<BusStops.size(); i++){
            float x = myLocation.distanceTo(BusStops.get(i).location);
            if (x < d){
                d = x;
                n = i;
            }
        }
        return  n;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.nav_gallery) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
