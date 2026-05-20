package com.wakely.wakely.screens.main;


import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wakely.wakely.App;
import com.wakely.wakely.R;
import com.wakely.wakely.screens.alarm.AlarmRedactor;
import com.wakely.wakely.services.AlarmScheduler;

/**
 * Main screen: lists stored {@link com.wakely.wakely.model.Event} entries and provides entry point
 * to create a new one.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        final MainAdapter adapter = new MainAdapter(
                (event, enabled) -> {
                    // UI delegates to non-UI alarm scheduler + Room DAO.
                    event.scheduled = enabled;
                    App.getInstance().getEventDao().update(event);

                    if (enabled) {
                        AlarmScheduler.schedule(getApplicationContext(), event);
                    } else {
                        AlarmScheduler.cancel(getApplicationContext(), event);
                    }
                },
                event -> {
                    // If it was scheduled, cancel the system alarm first.
                    if (event.scheduled) {
                        AlarmScheduler.cancel(getApplicationContext(), event);
                    }
                    App.getInstance().getEventDao().delete(event);
                }
        );
        recyclerView.setAdapter(adapter);


        button = findViewById(R.id.button);
        button.setOnClickListener(view -> {

            AlarmRedactor.start(MainActivity.this, null);

        });

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getEventLiveData().observe(this, adapter::setItems);

    }

}
