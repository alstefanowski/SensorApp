package com.example.sensorapp;

import static com.example.sensorapp.SensorDetailsActivity.EXTRA_SENSOR;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private SensorAdapter adapter;
    private RecyclerView recyclerView;
    private static String SENSOR_TAG = "sensor";
    //private Sensor sensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for(int i = 0; i < sensorList.toArray().length; i++) {
            Log.d(SENSOR_TAG, "Sensor name: " + sensorList.get(i).getName());
            Log.d(SENSOR_TAG, "Sensor vendor: " + sensorList.get(i).getVendor());
            Log.d(SENSOR_TAG, "Sensor max range: " + sensorList.get(i).getMaximumRange());
        }
        if (adapter == null) {
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String string = getString(R.string.sensors_count, sensorList.size());
        getSupportActionBar().setSubtitle(string);
        return super.onOptionsItemSelected(item);
    }

    private class SensorHolder extends RecyclerView.ViewHolder  {
        private ImageView sensorIconImageView;
        private TextView sensorNameTextView;
        private Sensor sensor;
        public SensorHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            super(layoutInflater.inflate(R.layout.sensor_list_item, viewGroup, false));
            sensorIconImageView = itemView.findViewById(R.id.sensor_icon);
            sensorNameTextView = itemView.findViewById(R.id.sensor_name);
        }

        public void bind(Sensor sensor) {
            this.sensor = sensor;
            sensorIconImageView.setImageResource(R.drawable.ic_action_name);
            sensorNameTextView.setText(sensor.getName());
            View item = itemView.findViewById(R.id.sensor_list_item);
            if(sensor.getType() == Sensor.TYPE_GRAVITY || sensor.getType() == Sensor.TYPE_LIGHT) {
                item.setBackgroundColor(getResources().getColor(R.color.teal_700));
                item.setOnClickListener(v -> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR, sensor.getType());
                    startActivity(intent);
                });
            }
            if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                item.setBackgroundColor(getResources().getColor(R.color.purple_200));
                item.setOnClickListener(v -> {
                    Intent intent  = new Intent (SensorActivity.this, LocationActivity.class);
                    startActivity(intent);
                });
            }
            /*
                sensorIconImageView.setBackgroundColor(getResources().getColor(R.color.purple_200));
                sensorNameTextView.setBackgroundColor(getResources().getColor(R.color.purple_200));
            }
            else {
                sensorIconImageView.setBackgroundColor(getResources().getColor(R.color.teal_700));
                sensorNameTextView.setBackgroundColor(getResources().getColor(R.color.teal_700));
            }
            */
        }
    }
    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {
        private final List<Sensor> sensorList;
        public SensorAdapter(List<Sensor> items) {
            sensorList = items;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new SensorHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }


        @Override
        public int getItemCount() {
            return sensorList.size();
        }
    }
}
