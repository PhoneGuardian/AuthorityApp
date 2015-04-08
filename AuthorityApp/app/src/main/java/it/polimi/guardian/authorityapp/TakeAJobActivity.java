package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class TakeAJobActivity extends Activity {
    private ListView lvJobs;
    private List<Job> jObjList;
    private JobAdapter adapter;
    private User u;
    private String url_get_events_not_taken = "http://nemanjastolic.co.nf/guardian/get_events_not_taken.php";
    private Event events[];
    private final JSONParser jParser = new JSONParser();
    private TextView tv_list_is_empty;
    private JSONArray events_response = null;
    private int success=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_a_job);
        lvJobs = (ListView) findViewById(R.id.list_of_jobs);
        tv_list_is_empty = (TextView) findViewById(R.id.tv_list_is_empty);
        u = User.getInstance();
        jObjList = new ArrayList<>();
        adapter = new JobAdapter(this, this, lvJobs,jObjList);
        //php get
        getAvalableJobsFromServer();
        lvJobs.setAdapter(adapter);
        //To select manually the buttons with the trackball
        lvJobs.setItemsCanFocus(true);
        //And to disable the focus on the whole list items
        lvJobs.setFocusable(false);
        lvJobs.setFocusableInTouchMode(false);
        lvJobs.setClickable(false);
    }

    private void getAvalableJobsFromServer() {
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
                        Toast.makeText(TakeAJobActivity.this, "No events found!", Toast.LENGTH_LONG).show();
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
                    else
                    {
                        //hide text view
                        tv_list_is_empty.setVisibility(View.INVISIBLE);
                        //update listview
                        jObjList.clear();
                        for(Event e: events) {
                            Job j = new Job(e);
                            jObjList.add(j);
                        }
                        refreshListView();

                    }



                }
            });

        }


    }
}
