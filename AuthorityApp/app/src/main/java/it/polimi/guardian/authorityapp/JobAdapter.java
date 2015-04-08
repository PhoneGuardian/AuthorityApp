package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.CursorAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nemanja on 07/04/2015.
 */
public class JobAdapter extends BaseAdapter implements Serializable{
    private static final long serialVersionUID = 1L;

    protected ListView lvAllJobs;
    private Context ctx;
    private List<Job> jobsList = new ArrayList<>();
    private static LayoutInflater inflater = null;

    protected static class RowViewHolder {
        public TextView tvDescription;
        public Button btnTakeAJob;
        public Button btnViewOnMap;
    }

    public JobAdapter(Context context, ListView lvJobs, List<Job> jObjList){
        ctx = context;
        lvAllJobs = lvJobs;
        jobsList = new ArrayList<>();
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
            Toast.makeText(ctx,"Take clicked, row "+ position,Toast.LENGTH_SHORT).show();
        }
    };

    private OnClickListener mOnBtnViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = lvAllJobs.getPositionForView((View) v.getParent());
            Toast.makeText(ctx, "View clicked, row " + position, Toast.LENGTH_SHORT).show();
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
    }
}
