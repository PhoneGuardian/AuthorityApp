package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends Activity implements OnClickListener {
    private Button btn_takeAjob;
    private Button btn_reviewAjob;
    private Button btn_highscores;

    private Context context;
    private GcmHelper gcmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_takeAjob = (Button) findViewById(R.id.btn_take_a_job);
        btn_takeAjob.setOnClickListener(this);
        btn_reviewAjob = (Button) findViewById(R.id.btn_review_a_job);
        btn_reviewAjob.setOnClickListener(this);
        btn_highscores = (Button) findViewById(R.id.btn_highscores);
        btn_highscores.setOnClickListener(this);

        //read job from file if the file exists
        CurrentJob currentJob = CurrentJob.getInstance();
        if (currentJob.fileExists())
            currentJob.readStateFromFile();

        gcmHelper = new GcmHelper(this);
        gcmHelper.initialGcmCheck();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_a_job:
                Intent i1 = new Intent(this, TakeAJobActivity.class);
                startActivity(i1);
                break;
            case R.id.btn_review_a_job:
                Intent i2 = new Intent(this, ReviewAJobActivity.class);
                startActivity(i2);
                break;
            case R.id.btn_highscores:
                Intent i3 = new Intent(this, HighscoresActivity.class);
                startActivity(i3);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        gcmHelper.checkPlayServices();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
