package wychoniu.bybus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int group_position, int child_position, long id)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("line", listDataHeader.get(group_position));
                returnIntent.putExtra("direction", listDataChild.get(listDataHeader.get(group_position)).get(child_position));
                setResult(Activity.RESULT_OK,returnIntent);
                SettingsActivity.this.finish();
                return true;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        List<String> stops;

        try {
            JSONObject jsonRootObject = new JSONObject(readFromFile());
            JSONArray jsonArray = jsonRootObject.optJSONArray("lines");

            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int number = Integer.parseInt(jsonObject.optString("number"));
                String dir1 = jsonObject.optString("direction1");
                String dir2 = jsonObject.optString("direction2");

                listDataHeader.add(Integer.toString(number));
                listDataChild.put(listDataHeader.get(i),  new ArrayList<>(Arrays.asList(dir1, dir2)));
            }

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
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

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
