package com.wakely.wakely.qrscanner.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wakely.wakely.R;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;


/**
 * Lists stored QR entries and allows selecting one.
 *
 * <p>If started with {@code startActivityForResult}, the selected QR id is returned via
 * {@link #EXTRA_SELECTED_QR_ID}.</p>
 */
public class QrActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_QR_ID = "QrActivity.EXTRA_SELECTED_QR_ID";

    RecyclerView recyclerView;
    Button addQrButton;
    ImageButton applyQrButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);


        recyclerView = findViewById(R.id.qr_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        final QrAdapter adapter = new QrAdapter();
        recyclerView.setAdapter(adapter);

        addQrButton = findViewById(R.id.add_qr_button);
        addQrButton.setOnClickListener(view -> {
            startActivity(new Intent(this, QrCreatingActivity.class));
        });

        applyQrButton = findViewById(R.id.apply_qr);
        applyQrButton.setOnClickListener(view -> {
            Qr selected = adapter.getSelected();
            if (selected == null) {
                android.widget.Toast.makeText(this, "Pick a QR", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            Intent result = new Intent();
            result.putExtra(EXTRA_SELECTED_QR_ID, selected.qrId);
            setResult(RESULT_OK, result);
            finish();
        });

        QrViewModel qrViewModel = new ViewModelProvider(this).get(QrViewModel.class);
        qrViewModel.getQrLiveData().observe(this, new Observer<List<Qr>>() {
            @Override
            public void onChanged(List<Qr> qrs) {
                adapter.setItems(qrs);
            }
        });
    }
}
