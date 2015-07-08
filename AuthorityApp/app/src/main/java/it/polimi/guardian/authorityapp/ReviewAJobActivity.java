package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ReviewAJobActivity extends Activity implements View.OnClickListener{
    private User u;
    private String url_add_review = "http://nemanjastolic.co.nf/guardian/add_review.php";
    private Button btn_review_the_job;
    private Spinner dropdown;
    private final JSONParser jParser = new JSONParser();
    private CurrentJob currentJob;
    private String jobStatus = "";
    private String reviewComment = "";
    private EditText et_review_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_a_job);
        //set on click listener for button
        btn_review_the_job = (Button) findViewById(R.id.btn_review_the_job);
        btn_review_the_job.setOnClickListener(this);
        dropdown = (Spinner) findViewById(R.id.spinner_job_status);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                jobStatus = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        u  = User.getInstance();
        currentJob  = CurrentJob.getInstance();

        et_review_comment = (EditText) findViewById(R.id.et_review_comment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_review_the_job:
                //do something
                reviewComment = et_review_comment.getText().toString();
                jobStatus = dropdown.getSelectedItem().toString();
                if(!reviewComment.equals("")) {
                    new ReviewTheJob(reviewComment, jobStatus).execute();
                }
                else {
                    Toast.makeText(ReviewAJobActivity.this,"Please, type the review comment.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class ReviewTheJob extends AsyncTask<String, String, String>
    {
        private JSONObject jsonReviewObj;
        private String reviewComment = "";
        private String jobStatus = "";
        private boolean jobExists = false;
        private int reviewSaved = 0;
        private String msgReviewed = "";

        public ReviewTheJob(String comment, String status) {
            reviewComment = comment;
            jobStatus = status;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        //getting All events from url
        protected String doInBackground(String... args) {
            jobExists = currentJob.isSet();
            if(jobExists) {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("auth_username", u.getUsername()));
                params.add(new BasicNameValuePair("event_id", currentJob.getJob().getEvent().getId()));
                params.add(new BasicNameValuePair("job_type", currentJob.getJob().getType()));
                params.add(new BasicNameValuePair("job_status", ReviewAJobActivity.this.jobStatus));///spinner
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.add(new BasicNameValuePair("review_creation_time", dateFormat.format(Calendar.getInstance().getTime())));
                jsonReviewObj = jParser.makeHttpRequest(url_add_review, "GET", params);

                try {
                    reviewSaved = jsonReviewObj.getInt(Tags.TAG_SUCCESS);
                    msgReviewed = jsonReviewObj.getString(Tags.TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "ok";
            }  else return "job didn't exist";
        }

        @Override
        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                     if( reviewSaved == 0 &&  !currentJob.isSet()) {
                        Toast.makeText(ReviewAJobActivity.this, "First you have to take a job in order to review it", Toast.LENGTH_SHORT).show();
                    } else if (reviewSaved == 0){
                        Toast.makeText(ReviewAJobActivity.this, "You have to first take a job to review itThe review is not saved!", Toast.LENGTH_SHORT).show();
                    }else {
                        //review is saved on server.
                        currentJob.setReviewSavedOnServer(true);
                        //set review inside currentJob
                        currentJob.setReview(new Review(Integer.parseInt(currentJob.getJob().getEvent().getId()), reviewComment));
                        //currentJob instance now can be emptied
                        currentJob.dismissJobAndReview();
                        //delete currentJob.txt
                        currentJob.fileDelete();
                        Toast.makeText(ReviewAJobActivity.this, "The review has been saved. Now you can take another job.", Toast.LENGTH_SHORT).show();
                        et_review_comment.setText("");
                    }
                }
            });
        }

    }
}
