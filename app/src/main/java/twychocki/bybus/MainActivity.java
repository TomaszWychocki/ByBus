package twychocki.bybus;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    GPSTracker gps;
    TextView position_text;
    TextView busStopName_text;
    TextView direction_text;
    TextView lineText;
    TextView s;
    Location myLocation;
    Timer timer;
    TimerTask timerTask;
    boolean isOn = false;

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


        position_text = (TextView) findViewById(R.id.distance);
        busStopName_text = (TextView) findViewById(R.id.bus_stop_name);
        direction_text = (TextView) findViewById(R.id.direction);
        lineText = (TextView) findViewById(R.id.lineText);
        s = (TextView) findViewById(R.id.status_text);
        gps = new GPSTracker(MainActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    if (!isOn)
                    {
                        if(gps.canGetLocation)
                        {
                            gps.getLocation();
                            if (timerTask != null)
                            {
                                timerTask = null;
                            }
                            if (timer != null)
                            {
                                timer = null;
                            }
                            timer = new Timer();
                            timerTask = createTimerTask();
                            timer.schedule(timerTask, 0, 2000);
                            isOn = true;
                            fab.setImageResource(android.R.drawable.ic_media_pause);
                        }
                        else
                        {
                            gps.getLocation();
                            gps.showSettingsAlert();
                            gps.getLocation();
                        }
                    }
                    else
                    {
                        timerTask.cancel();
                        timer.purge();
                        timer.cancel();
                        isOn = false;
                        fab.setImageResource(android.R.drawable.ic_media_play);
                        position_text.setText("");
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

    private TimerTask createTimerTask()
    {
        TimerTask t = new TimerTask()
        {
            public void run()
            {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        try {
                            if(gps.canGetLocation() && gps.getLocation() != null) {
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                myLocation = gps.getLocation();
                                int nearst = findNearstStop();
                                float dist = myLocation.distanceTo(BusStops.get(nearst).location);

                                String str = "Twoja lokalizacja:\nLat: " + latitude + "\nLong: " + longitude + "\nOdległość: " + String.format("%.0f", dist) + "m";
                                position_text.setText(str);
                                direction_text.setText("Kierunek: " + BusStops.get(nearst).direction);
                                busStopName_text.setText(BusStops.get(nearst).name);
                                lineText.setText("Linia " + Integer.toString(BusStops.get(nearst).line));
                                s.setText("Przystanek");
                            }
                            else
                            {
                                busStopName_text.setText("");
                                position_text.setText("");
                                s.setText("Szukanie GPS");
                            }
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        return t;
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
