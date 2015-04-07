package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class TakeAJobActivity extends Activity {
    private ListView lvJobs;
    private List<Job> jObjList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_a_job);
        lvJobs = (ListView) findViewById(R.id.list_of_jobs);
        ////////replace with php get//////////////////
        jObjList = new ArrayList<>(2);
        Job j1 = new Job();
        j1.setAuthId(201);
        j1.setEventId(101);
        j1.setJobDescription("job1");
        j1.setJobId(1);
        j1.setStatus("unresolved");
        jObjList.add(j1);
        Job j2 = new Job();
        j2.setAuthId(202);
        j2.setEventId(102);
        j2.setJobDescription("job2");
        j2.setJobId(2);
        j2.setStatus("unresolved");
        jObjList.add(j2);
        //////////////////////////////
        lvJobs.setAdapter(new JobAdapter(this, lvJobs,jObjList));
        //To select manually the buttons with the trackball
        lvJobs.setItemsCanFocus(true);
        //And to disable the focus on the whole list items
        lvJobs.setFocusable(false);
        lvJobs.setFocusableInTouchMode(false);
        lvJobs.setClickable(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_ajob, menu);
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
}
