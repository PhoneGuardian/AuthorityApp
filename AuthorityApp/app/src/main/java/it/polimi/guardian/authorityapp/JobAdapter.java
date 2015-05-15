package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Nemanja on 07/04/2015.
 */
public class JobAdapter extends BaseAdapter implements Serializable{
    private static final long serialVersionUID = 1L;

    protected ListView lvAllJobs;
    private Context ctx;
    Activity activity;
    private List<Job> jobsList = new ArrayList<>();
    private static LayoutInflater inflater = null;

    protected static class RowViewHolder {
        public TextView tvDescription;
        public Button btnTakeAJob;
        public Button btnViewOnMap;
    }
    public void refresh() {
        this.notifyDataSetChanged();
    }
    public JobAdapter(Context context,Activity act, ListView lvJobs, List<Job> jObjList){
        ctx = context;
        activity = act;
        lvAllJobs = lvJobs;
        jobsList = new ArrayList<>();
        //sort the jobs based on lat lng of job.getEvent().getLat() job.getEvent().getLng()
        for(Job j: jObjList){
            jobsList.add(j);
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(jobsList == null)
            return 0;
        return jobsList.size();
    }

    @Override
    public Object getItem(int position) {
        return jobsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.list_of_jobs_row, null);
        //find items in row-view

        RowViewHolder holder = new RowViewHolder();
        holder.tvDescription = (TextView) view.findViewById(R.id.tv_job_description);
        holder.btnTakeAJob = (Button) view.findViewById(R.id.btn_row_take);
        holder.btnViewOnMap = (Button) view.findViewById(R.id.btn_row_view);
        //set their values
        holder.tvDescription.setText(jobsList.get(position).getEvent().getDescription());
        holder.btnTakeAJob.setOnClickListener(mOnBtnTakeClickListener);
        holder.btnViewOnMap.setOnClickListener(mOnBtnViewClickListener);
        //tag holder object to specific row-view object
        view.setTag(holder);

        return view;
    }


    private OnClickListener mOnBtnTakeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = lvAllJobs.getPositionForView((View) v.getParent());
            //Toast.makeText(ctx,"Take clicked, row "+ position,Toast.LENGTH_SHORT).show();
            Job jobToTake = jobsList.get(position);
            //read job from file if the file exists
            CurrentJob currentJob = CurrentJob.getInstance();
            if(currentJob.fileExists())
                currentJob.readStateFromFile();

            if(!currentJob.isSet()) {
                new AddJob(activity, jobToTake).execute();
                currentJob.setJob(jobToTake);
                //save job to txt file
                currentJob.saveStateToFile();
                jobsList.remove(position);
                JobAdapter.this.refresh();
            }
            else
                Toast.makeText(ctx,"You already took one job. You need to review it first before taking another one.",Toast.LENGTH_SHORT).show();

        }
    };

    private OnClickListener mOnBtnViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = lvAllJobs.getPositionForView((View) v.getParent());
            //Toast.makeText(ctx, "View clicked, row " + position, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ctx,ViewJobDetailsActivity.class);
            Job jobToSee = jobsList.get(position);
            i.putExtra("eventDescription", jobToSee.getEvent());
            ctx.startActivity(i);
        }
    };

    @Override
    public boolean  areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void updateJobs(List<Job> jObjList) {
        jobsList.clear();
        for(Job j: jObjList){
            jobsList.add(j);
        }
        this.refresh();
    }

    class AddJob extends AsyncTask<String, String, String> {

        private String url_add_job = "http://nemanjastolic.co.nf/guardian/add_job.php";
        private final JSONParser jParser = new JSONParser();
        Activity parent;
        int success;
        String msgAdd;
        Job job;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        public AddJob(Activity act, Job j) {
            parent = act;
            job=j;
        }
        protected String doInBackground(String... argss) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //auth_username, event_id,job_description,job_type, job_status, job_creation_time, job_ending_time
            params.add(new BasicNameValuePair("auth_username", job.getAuthUsername()));
            params.add(new BasicNameValuePair("event_id", job.getEvent().getId()));
            params.add(new BasicNameValuePair("job_description", job.getEvent().getDescription()));
            params.add(new BasicNameValuePair("job_type", job.getType()));
            params.add(new BasicNameValuePair("job_status", job.getStatus()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.add(new BasicNameValuePair("job_creation_time", dateFormat.format(Calendar.getInstance().getTime())));

            JSONObject json = jParser.makeHttpRequest(url_add_job, "GET", params);

            try
            {
                success = json.getInt(Tags.TAG_SUCCESS);
                msgAdd = json.getString(Tags.TAG_MESSAGE);
                //had to pass activity to constructor in order to show toast inside async task
                parent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(parent.getBaseContext(), msgAdd, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

    }
}
