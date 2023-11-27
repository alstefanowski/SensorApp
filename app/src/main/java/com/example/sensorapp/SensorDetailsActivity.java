package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private TextView sensorNameTextView;
    private TextView sensorValueTextView;
    public static String EXTRA_SENSOR = "EXTRA_SENSOR";
    @Override
    protected void onStart() {
        super.onStart();

        if (sensorLight != null) {
            sensorManager.registerListener(this,sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        sensorNameTextView = findViewById(R.id.detail_sensor_name);
        sensorValueTextView = findViewById(R.id.detail_sensor_value);
        int sensorType = getIntent().getIntExtra(EXTRA_SENSOR, Sensor.TYPE_LIGHT);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorLight = sensorManager.getDefaultSensor(sensorType);
        if (sensorLight == null) {
            sensorNameTextView.setText(R.string.missing_sensor);
        }
        else {
            sensorNameTextView.setText(sensorLight.getName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];
        //String string = getString(R.string.example_sensor_name, currentValue);
        switch (sensorType) {
            case Sensor.TYPE_GRAVITY:
                sensorValueTextView.setText(String.valueOf(currentValue));
                break;
            case Sensor.TYPE_LIGHT:
                sensorValueTextView.setText(String.valueOf(currentValue));
                break;
            default:
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}