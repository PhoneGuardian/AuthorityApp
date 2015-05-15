package it.polimi.guardian.authorityapp;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ViewJobDetailsActivity extends Activity implements View.OnClickListener {
    private TextView tv_details_description;
    private TextView tv_details_address;
    private TextView tv_details_phone;
    private TextView tv_details_time;
    private Button btn_view_on_map;
    private Event eventToShow;
    private int anonymous;
    private double lat;
    private double lng;
    private float accuracy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_details);
        tv_details_description = (TextView) findViewById(R.id.tv_details_description);
        tv_details_address = (TextView) findViewById(R.id.tv_details_address);
        tv_details_phone = (TextView) findViewById(R.id.tv_details_phone);
        tv_details_time = (TextView) findViewById(R.id.tv_details_time);
        btn_view_on_map = (Button) findViewById(R.id.btn_view_on_map);
        btn_view_on_map.setOnClickListener(this);

        eventToShow = (Event) getIntent().getSerializableExtra("eventDescription");

        tv_details_description.setText(eventToShow.getDescription());
        tv_details_address.setText(eventToShow.getAddress());
        tv_details_time.setText(eventToShow.getEvent_time());
        anonymous = eventToShow.getAnonymous();
        lat = eventToShow.getLat();
        lng = eventToShow.getLng();
        accuracy = eventToShow.getLocation_acc();

        if(anonymous==0) {
            tv_details_phone.setText(eventToShow.getUser_phone());
        } else {
            tv_details_phone.setText("Anonymous");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_on_map:
                Intent i1 = new Intent(this,MapActivity.class);
                i1.putExtra("lat", eventToShow.getLat()); // latitude
                i1.putExtra("lng", eventToShow.getLng()); // longitude
                i1.putExtra("eventDescription", eventToShow);//event instance
                startActivity(i1);
                //Toast.makeText(this, "View clicked. MapPreview not implemented", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
