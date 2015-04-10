package it.polimi.guardian.authorityapp;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Nemanja on 09/04/2015.
 */
public class CurrentJob {
    private static CurrentJob instance = null;
    Job job;
    Review review;
    boolean reviewSavedOnServer = false;

    private CurrentJob(){}

    public static CurrentJob getInstance(){
        if(instance  == null){
            instance = new CurrentJob();
        }
        return instance;
    }

    public boolean isSet() {
        if(job==null)//Authority is free to take a new job
            return false;
        else return true;//Authority already took a job
    }
    public Job getJob() {
        return job;
    }
    public boolean setJob(Job j) {
        if(!this.isSet()) {//Authority is free to take a new job
            job = j;
            return true;
        } else return false; //Authority already took a job, (s)he can't take a new one
    }
    public boolean isReviewed() {
        if(review==null)
            return false;
        else return true;
    }

    public Review getReview() {
        return review;
    }

    public boolean setReview(Review review) {
        if(this.isSet()) {
            this.review = review;
            return true;
        } else return false;
    }

    public boolean isReviewSavedOnServer() {
        if(reviewSavedOnServer)
            return true;
        else return false;
    }
    public void setReviewSavedOnServer(boolean value) {
        this.reviewSavedOnServer = value;
    }

    public boolean dismissJobAndReview() {
        if(isReviewed() && isReviewSavedOnServer()) {
            job=null;//dismiss current job
            review=null;//dismiss review for current job
            reviewSavedOnServer=false;
            return true;
        } else return false; //if review is not set, or review is set but it is not saved on server, don't dismiss
    }
    public boolean saveStateToFile() {
        String jsonStringCurrentJob="";
        // job to file
        try{
            FileHelper fileHelper = new FileHelper();
            //save currentJob attributes inside JSONObject
            JSONObject jobj = new JSONObject();
            Job job = this.getJob();
            jobj.put("job-event-id", job.getEvent().getId());//String
            jobj.put("job-event-description", job.getEvent().getDescription());//String
            jobj.put("job-event-address", job.getEvent().getAddress());//String
            jobj.put("job-event-anonymous", job.getEvent().getAnonymous());//int
            jobj.put("job-event-event_time", job.getEvent().getEvent_time());//String
            jobj.put("job-event-lat", job.getEvent().getLat());//double
            jobj.put("job-event-lng", job.getEvent().getLng());//double
            jobj.put("job-event-loc_accuracy", job.getEvent().getLocation_acc());//float
            jobj.put("job-event-type_of_event", job.getEvent().getType_of_event());//String
            jobj.put("job-event-user_phone", job.getEvent().getUser_phone());//String
            jobj.put("job-type", job.getType());//String
            jobj.put("job-status", job.getStatus());//String
            jobj.put("job-auth_username", job.getAuthUsername());//String


            jsonStringCurrentJob = jobj.toString();
            //write jsonStringCurrentJob into job file
            fileHelper.writeToFile(jsonStringCurrentJob, "currentJob.txt");
            Log.d("saveStateToFile", "currentJob.txt is added.");
            //return success
            return true;

        } catch(Exception e){
            e.printStackTrace();
            Log.d("saveStateToFile", "Error: " + e.getMessage());
            Log.d("saveStateToFile","jsonStringCurrentJob: "+jsonStringCurrentJob);
            return false;//return failure
        }
    }
    public boolean readStateFromFile() {
        String jsonStringCurrentJob="";
        // job from file
        try{
            FileHelper fileHelper = new FileHelper();
            jsonStringCurrentJob = fileHelper.readFromFile("currentJob.txt");
            if(!(jsonStringCurrentJob.equals("") || jsonStringCurrentJob.equals("{}"))) {//there is another job already in the file
                JSONObject jObj;
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(jsonStringCurrentJob);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing json data from credentials.txt" + e.toString());
                    jObj = null;
                }
                if(jObj != null) {
                    //reading of saved job
                    //Job job = new Job();
                    Event event = new Event();

                    event.setId(jObj.getString("job-event-id"));
                    event.setDescription(jObj.getString("job-event-description"));
                    event.setAddress(jObj.getString("job-event-address"));
                    event.setAnonymous(jObj.getInt("job-event-anonymous"));
                    event.setEvent_time(jObj.getString("job-event-event_time"));
                    event.setLat(jObj.getDouble("job-event-lat"));
                    event.setLng(jObj.getDouble("job-event-lng"));
                    event.setLocation_acc((float) jObj.getDouble("job-event-loc_accuracy"));
                    event.setType_of_event(jObj.getString("job-event-type_of_event"));
                    event.setUser_phone(jObj.getString("job-event-user_phone"));

                    Job job = new Job(event);
                    job.setType(jObj.getString("job-type"));
                    job.setStatus(jObj.getString("job-status"));
                    job.setAuthUsername(jObj.getString("job-auth_username"));

                    this.setJob(job);
                    Log.d("readStateFromFile", "CurrentJob successfully read from file.");
                    return true;
                } else {
                    Log.d("readStateFromFile", "CurrentJob.txt exists, but couldn't parse.");
                    return false;
                }
            } else return false;
        } catch(Exception e){
            e.printStackTrace();
            Log.d("readStateFromFile", "Error: " + e.getMessage());
            Log.d("readStateFromFile", "jsonStringCurrentJob: "+jsonStringCurrentJob);
            return false;
        }
    }
    public boolean fileExists() {
        FileHelper fileHelper = new FileHelper();
        boolean exists = fileHelper.fileExists("currentJob.txt");
        return exists;
    }
    public boolean fileDelete() {
        FileHelper fileHelper = new FileHelper();
        boolean deleted = fileHelper.fileDelete("currentJob.txt");
        return deleted;
    }
    public boolean returnBack() {
        boolean deleted = this.fileDelete();
        if(!deleted)
            return false;
        this.job=null;
        this.review=null;
        this.reviewSavedOnServer=false;
        return true;
    }
}
