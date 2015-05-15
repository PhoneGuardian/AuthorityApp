package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TakeAJobActivity extends Activity implements View.OnClickListener{
    private ListView lvJobs;
    private List<Job> jObjList;
    private JobAdapter adapter;
    private User u;
    private String url_get_events_not_taken = "http://nemanjastolic.co.nf/guardian/get_events_not_taken.php";
    private String url_delete_a_job = "http://nemanjastolic.co.nf/guardian/delete_a_job.php";
    private Event events[];
    private final JSONParser jParser = new JSONParser();
    private TextView tv_list_is_empty;
    private JSONArray events_response = null;
    private int success=0;
    private Button btn_refresh;
    private Button btn_return_job;

    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_a_job);
        lvJobs = (ListView) findViewById(R.id.list_of_jobs);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(this);
        btn_return_job = (Button) findViewById(R.id.btn_return_job);
        btn_return_job.setOnClickListener(this);
        tv_list_is_empty = (TextView) findViewById(R.id.tv_list_is_empty);
        u = User.getInstance();
        jObjList = new ArrayList<>();
        adapter = new JobAdapter(this, this, lvJobs,jObjList);
        lvJobs.setAdapter(adapter);
        //php get
        getAvailableJobsFromServer();
        //To select manually the buttons with the trackball
        lvJobs.setItemsCanFocus(true);
        //And to disable the focus on the whole list items
        lvJobs.setFocusable(false);
        lvJobs.setFocusableInTouchMode(false);
        lvJobs.setClickable(false);
    }

    private void getAvailableJobsFromServer() {
        try {
            new GetAvailableJobs().execute().get();//starting of thread
            refreshListView();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
    }
    private void refreshListView() {
        adapter.updateJobs(jObjList);
        adapter.notifyDataSetChanged();
        lvJobs.invalidateViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
                getAvailableJobsFromServer();
                refreshListView();
                Toast.makeText(TakeAJobActivity.this, "The list has been refreshed.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_return_job:
                returnBackTheJob();
                refreshListView();
                break;
        }
    }

    private void returnBackTheJob() {
        //async task for deleting the job from job table with job.getevent.event_id.
        //it also deletes the file from phone memory and empties the currentJob instance
        new ReturnBackTheJob().execute();
    }

    class GetAvailableJobs extends AsyncTask<String, String, String>
    {
        JSONObject json;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //getting All events from url
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_type", u.getType()));
            json = jParser.makeHttpRequest(url_get_events_not_taken, "GET", params);

            try {
                success = json.getInt(Tags.TAG_SUCCESS); //1 if he doesn't exist, 0 otherwise
                if (success == 1) {                 //events found
                    // Getting Array of Events
                    events_response = json.getJSONArray(Tags.TAG_EVENTS);
                    if(events_response ==null)
                        Toast.makeText(TakeAJobActivity.this, "No events found!", Toast.LENGTH_SHORT).show();
                    else {
                        events = null;
                        events = new Event[events_response.length()];

                        for (int i = 0; i < events_response.length(); i++) {
                            JSONObject jObj = events_response.getJSONObject(i);

                            events[i] = new Event();
                            events[i].setId(jObj.getString(Tags.TAG_ID));
                            events[i].setAddress(jObj.getString(Tags.TAG_ADDRESS));
                            events[i].setUser_phone(jObj.getString(Tags.TAG_USER_PHONE));
                            events[i].setType_of_event(jObj.getString(Tags.TAG_TYPE_OF_EVENT));
                            events[i].setDescription(jObj.getString(Tags.TAG_DESC));
                            events[i].setEvent_time(jObj.getString(Tags.TAG_EVENT_TIME));
                            events[i].setLng(jObj.getDouble(Tags.TAG_LONG));
                            events[i].setLat(jObj.getDouble(Tags.TAG_LAT));
                            events[i].setAnonymous(jObj.getInt(Tags.TAG_ANONYMOUS));
                            events[i].setLocation_acc(jObj.getLong(Tags.TAG_ACCURACY));
                        }

                        jObjList.clear();
                        for(Event e: events) {
                            Job j = new Job(e);
                            jObjList.add(j);
                        }
                    }
                }
                else {
                    events = null;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    if( success == 0) {
                        Toast.makeText(TakeAJobActivity.this, "Currently there aren't any jobs!", Toast.LENGTH_SHORT).show();
                        //make textview visible
                        tv_list_is_empty.setVisibility(View.VISIBLE);
                    }
                    else {
                        //hide text view
                        tv_list_is_empty.setVisibility(View.INVISIBLE);
                        //update listview
                        jObjList.clear();
                        for(Event e: events) {
                            Job j = new Job(e);
                            jObjList.add(j);
                        }
                        //sort collected data based on user's location
                        myLocation = new LocationUtility(TakeAJobActivity.this).getLocationObject();
                        if(myLocation != null) {
                            Collections.sort(jObjList, new JobDistanceComparator(TakeAJobActivity.this, myLocation.getLatitude(), myLocation.getLongitude()));
                        }
                        refreshListView();
                    }
                }
            });
        }

    }
    class ReturnBackTheJob extends AsyncTask<String, String, String>
    {
        JSONObject jsonObj;
        CurrentJob currentJob = CurrentJob.getInstance();
        boolean jobExists = false;
        int deletedOnServer=0;
        String msgDeleted;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //sends a request to delete a job with event_id specified
        protected String doInBackground(String... args) {
            jobExists = currentJob.isSet();
            if(jobExists) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("event_id", currentJob.getJob().getEvent().getId()));
                jsonObj = jParser.makeHttpRequest(url_delete_a_job, "GET", params);

                try {
                    deletedOnServer = jsonObj.getInt(Tags.TAG_SUCCESS); //1 if he doesn't exist, 0 otherwise
                    msgDeleted = jsonObj.getString(Tags.TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "ok";
            }
            else return "job didn't exist";
        }

        @Override
        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    if( deletedOnServer == 0) {//failed to delete a job on server
                        Toast.makeText(TakeAJobActivity.this, "The job is not deleted. Or it is already deleted, or there was an error while deleting.", Toast.LENGTH_SHORT).show();

                    }
                    else {//job found and deleted
                        //now the returned job can be return back to list of available jobs
                        Job j = currentJob.getJob();
                        jObjList.add(j);
                        //empty the currentJob and delete a file with returnBack function
                        currentJob.returnBack();
                        //job was returned, refresh the list
                        refreshListView();
                        Toast.makeText(TakeAJobActivity.this, "Job returned back.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }
}
