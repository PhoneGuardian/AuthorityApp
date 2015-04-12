package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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


public class HighscoresActivity extends Activity {
    User u;
    private ListView lvHighscores;
    private List<Highscore> jObjList;
    private HighscoreAdapter adapter;
    private String url_get_highscores = "http://nemanjastolic.co.nf/guardian/get_pairs_username_num_jobs.php";
    private final JSONParser jParser = new JSONParser();
    private JSONArray highscores_response = null;
    private int success=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);
        lvHighscores = (ListView) findViewById(R.id.list_highscores);
        u = User.getInstance();
        jObjList = new ArrayList<>();
        adapter = new HighscoreAdapter(this,this,lvHighscores,jObjList);
        //php get
        getHighscoresFromServer();
        lvHighscores.setAdapter(adapter);
    }

    private void getHighscoresFromServer() {
        try {
            new GetHighscoresTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void refreshListView() {
        adapter.updateHighscores(jObjList);
        adapter.notifyDataSetChanged();
        lvHighscores.invalidateViews();
    }

    class GetHighscoresTask extends AsyncTask<String, String, String>
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
            json = jParser.makeHttpRequest(url_get_highscores, "GET", params);

            try {
                success = json.getInt(Tags.TAG_SUCCESS);
                if (success == 1) {
                    // Getting Array of highscores
                    highscores_response = json.getJSONArray(Tags.TAG_HIGHSCORES);
                    if(highscores_response ==null)
                        Toast.makeText(HighscoresActivity.this, "No highscores found!", Toast.LENGTH_SHORT).show();
                    else {
                        jObjList.clear();

                        for (int i = 0; i < highscores_response.length(); i++) {
                            JSONObject jObj = highscores_response.getJSONObject(i);
                            Highscore h = new Highscore();
                            h.setAuthorityUsername(jObj.getString(Tags.TAG_USERNAME));
                            h.setScore(Integer.parseInt(jObj.getString(Tags.TAG_COUNT_JOBS)));
                            jObjList.add(h);
                        }
                        Collections.sort(jObjList);

                    }
                }
                else {
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
                        Toast.makeText(HighscoresActivity.this, "Couldn't retrieve highscores list. Please, check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        adapter.updateHighscores(jObjList);
                        refreshListView();
                    }
                }
            });
        }

    }
}
