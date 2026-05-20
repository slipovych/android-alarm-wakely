package com.wakely.wakely.qrscanner.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.wakely.wakely.App;
import com.wakely.wakely.R;
import com.wakely.wakely.interfaces.OnDataPass;
import com.wakely.wakely.qrscanner.model.Qr;
import com.wakely.wakely.screens.alarm.AlarmRedactor;

/**
 * Screen for scanning a QR code and saving it into the local database.
 *
 * <p>Uses {@link QrScannerFragment} to capture the QR contents, then saves {@link Qr} with a user-provided name.</p>
 */
public class QrCreatingActivity extends AppCompatActivity implements OnDataPass {

    private QrScannerFragment scannerFragment;
    private CodeScanner codeScanner;

    ImageButton setQrButton;
    EditText setQrNameField;

    private String qrData = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_qr);

        scannerFragment = new QrScannerFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        //TODO scanner view
        fragmentManager.beginTransaction()
                .add(R.id.qr_container, scannerFragment)
                .commit();


        setQrNameField = findViewById(R.id.set_qr_name);

        setQrButton = findViewById(R.id.set_qr_button);
        setQrButton.setOnClickListener(view -> {
            if (qrData != null && qrData.length() > 0) {
                Qr qr = new Qr();
                qr.qrName = setQrNameField.getText().toString();
                qr.qrCode = qrData;
                App.getInstance().getQrDao().insertAll(qr);
                finish();
                return;
            }
            // No scan yet.
            android.widget.Toast.makeText(this, "Scan a QR first", android.widget.Toast.LENGTH_SHORT).show();
        });


    }//TODO incorrect data passing

    @Override
    public void onDataPass(String data) {
        qrData = data;
    }

}
