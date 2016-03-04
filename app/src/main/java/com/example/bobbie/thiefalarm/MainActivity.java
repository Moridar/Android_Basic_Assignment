package com.example.bobbie.thiefalarm;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Boolean activated;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private RadioButton alarmRBtn;
    private Button alarmBtn;
    private CoordinatorLayout mLayout;
    private Vibrator vib;
    private boolean alarm = false;
    private boolean developer = true;
    private TextView tvx,tvy,tvz,tvxsum, tvysum, tvzsum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        alarmBtn = (Button) findViewById(R.id.buttonActivate);
        alarmRBtn = (RadioButton) findViewById(R.id.radioButtonradioButtonActivate);
        activated = false;
        tvx = (TextView)findViewById(R.id.textViewX);
        tvy = (TextView)findViewById(R.id.textViewY);
        tvz = (TextView)findViewById(R.id.textViewZ);
        tvxsum = (TextView)findViewById(R.id.textViewXSum);
        tvysum = (TextView)findViewById(R.id.textViewYSum);
        tvzsum = (TextView)findViewById(R.id.textViewZSum);

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleSensor();
            }
        });
	
        mLayout = (CoordinatorLayout) findViewById(R.id.mLayout);

        toogleDeveloperMode(); //Deaktiverer developer mode
    }

    private void toogleDeveloperMode(){
        developer = !developer;

        if(!developer){
            tvx.setVisibility(View.VISIBLE);
            tvy.setVisibility(View.INVISIBLE);
            tvz.setVisibility(View.INVISIBLE);
            tvxsum.setVisibility(View.INVISIBLE);
            tvysum.setVisibility(View.INVISIBLE);
            tvzsum.setVisibility(View.INVISIBLE);
            
    }else{
            tvx.setVisibility(View.VISIBLE);
            tvy.setVisibility(View.VISIBLE);
            tvz.setVisibility(View.VISIBLE);
            tvxsum.setVisibility(View.VISIBLE);
            tvysum.setVisibility(View.VISIBLE);
            tvzsum.setVisibility(View.VISIBLE);
        }
    }
    private void toogleSensor(){
        TextView tv = (TextView) findViewById(R.id.textView);
        if(activated){
            stopAlarm();
            tv.setVisibility(View.GONE);
            alarmBtn.setText("Deactivated");
            mSensorManager.unregisterListener(this, mAccelerometer);
            tvxsum.setText("0");
            tvysum.setText("0");
            tvzsum.setText("0");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmRBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ff0400")));

            }

        }
        else{
            tv.setVisibility(View.VISIBLE);
            alarmBtn.setText("Activated");
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmRBtn.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#00ff04")));
            }
        }
        activated = !activated;
    }

    @Override
    public void onBackPressed() {
        if(activated) toogleSensor();
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem toogleDev = menu.getItem(0);
        toogleDev.setTitle("Developer Mode");
        toogleDev.setCheckable(developer);
        toogleDev.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toogleDeveloperMode();
                item.setChecked(developer);
                return true;
            }
        });
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(developer) {
            tvx.setText("x: " + event.values[0]);
            tvy.setText("y:" + event.values[1]);
            tvz.setText("z:" + event.values[2]);

            try {
                tvxsum.setText(event.values[0] + Float.parseFloat("" + tvxsum.getText()) + "");
                tvysum.setText(event.values[1] + Float.parseFloat("" + tvysum.getText()) + "");
                tvzsum.setText(event.values[2] + Float.parseFloat("" + tvzsum.getText()) + "");
            } catch (Exception e) {
                tvxsum.setText("0");
                tvysum.setText("0");
                tvzsum.setText("0");
            }
        }
        if(Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]) >= 3) {
            if(!alarm) startAlarm();
        }
        else{
            if(alarm) stopAlarm();
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startAlarm(){
        alarm = true;
        mLayout.setBackgroundResource(R.drawable.alarmanim);
        AnimationDrawable alarmAnimation = (AnimationDrawable) mLayout.getBackground();
        alarmAnimation.start();
        long[] vibPattern = {0,100,10};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vib.vibrate(vibPattern,0, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        }
        else vib.vibrate(vibPattern,0);
    }

    private void stopAlarm(){
        alarm = false;
        if(mLayout != null) mLayout.setBackgroundColor(Color.WHITE);
        vib.cancel();
    }
}
