package wychoniu.bybus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView position_text;
    TextView busStopName_text;
    TextView direction_text;
    TextView line_text;
    TextView s;
    Location myLocation;
    Timer timer;
    TimerTask timerTask;
    boolean isOn = false, status = false;
    Context context = this;
    String line = "", direction = "";
    int startIndex, stopIndex;
    float speed = 0;

    List<BusStop> BusStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!isOn) {
                            if (status) {
                                if(!line.isEmpty() && !direction.isEmpty()) {
                                    if (timerTask != null) {
                                        timerTask = null;
                                    }
                                    if (timer != null) {
                                        timer = null;
                                    }
                                    timer = new Timer();
                                    timerTask = createTimerTask();
                                    timer.schedule(timerTask, 0, 2000);
                                    isOn = true;
                                    fab.setImageResource(android.R.drawable.ic_media_pause);
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Nie wybrano linii. Przejdź do ustawień.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle("Location settings");
                                alertDialog.setMessage("GPS is not enabled. Do you want to go to the settings menu?");

                                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int which)
                                    {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                });

                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel();
                                    }
                                });

                                alertDialog.show();
                            }
                        }
                        else {
                            timerTask.cancel();
                            timer.purge();
                            timer.cancel();
                            isOn = false;
                            fab.setImageResource(android.R.drawable.ic_media_play);
                            position_text.setText("");
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //---------------------------------------------------------------------
        parseJSON();

        position_text = (TextView) findViewById(R.id.distance);
        busStopName_text = (TextView) findViewById(R.id.bus_stop_name);
        direction_text = (TextView) findViewById(R.id.direction);
        line_text = (TextView) findViewById(R.id.lineText);
        s = (TextView) findViewById(R.id.status_text);

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                status = true;
                myLocation = location;
                speed = location.getSpeed();
            }

            @Override
            public void onStatusChanged(String provider, int statuss, Bundle extras) {
                if (statuss != 2)
                    status = true;
                else
                    status = false;
            }

            @Override
            public void onProviderEnabled(String provider) {
                status = true;
            }

            @Override
            public void onProviderDisabled(String provider) {
                status = false;
            }
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);

        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            status = true;
        //---------------------------------------------------------------------
    }

    private void parseJSON(){
        try {
            JSONObject jsonRootObject = new JSONObject(readFromFile());
            JSONArray jsonArray = jsonRootObject.optJSONArray("busStops");

            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.optString("name");
                String dir = jsonObject.optString("direction");
                double lat = Double.parseDouble(jsonObject.optString("latitude"));
                double lon = Double.parseDouble(jsonObject.optString("longitude"));
                int id = Integer.parseInt(jsonObject.optString("line"));
                BusStops.add(new BusStop(name, lat, lon, id, dir));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.bybusdata);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    private TimerTask createTimerTask() {
        TimerTask t = new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (status) {
                                double latitude = myLocation.getLatitude();
                                double longitude = myLocation.getLongitude();
                                int nearst = findNearstStop(myLocation);
                                float dist = myLocation.distanceTo(BusStops.get(nearst).location);

                                String str = "Lat: " + latitude + "\nLong: " + longitude + "\nOdległość: " + String.format("%.0f", dist) + "m\nSpd: " + String.format("%.0f", speed);
                                position_text.setText(str);
                                busStopName_text.setText(BusStops.get(nearst).name);
                                s.setText("Przystanek");
                            } else {
                                busStopName_text.setText("");
                                position_text.setText("");
                                s.setText("Szukanie GPS");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        return t;
    }

    public int findNearstStop(Location l) {
        int n = startIndex;
        float d;
        d = l.distanceTo(BusStops.get(startIndex).location);

        for (int i = startIndex + 1; i <= stopIndex; i++) {
            float x = l.distanceTo(BusStops.get(i).location);
            if (x < d) {
                d = x;
                n = i;
            }
        }
        return n;
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
            startActivity(new Intent(MainActivity.this, AboutMe.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 1);
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, AboutMe.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                line = data.getStringExtra("line");
                direction = data.getStringExtra("direction");
                line_text.setText(line);
                direction_text.setText(direction);

                boolean x = false;
                for (int i = 0; i < BusStops.size(); i++) {
                    if(!x && BusStops.get(i).direction.equals(direction) && Integer.toString(BusStops.get(i).line).equals(line)) {
                        x = true;
                        startIndex = i;
                    }

                    if(x && (!BusStops.get(i).direction.equals(direction) || !Integer.toString(BusStops.get(i).line).equals(line))) {
                        stopIndex = --i;
                        break;
                    }
                }
            }
        }
    }
}
