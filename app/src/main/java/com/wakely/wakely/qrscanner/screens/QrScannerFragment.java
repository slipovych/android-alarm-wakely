package com.wakely.wakely.qrscanner.screens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import com.wakely.wakely.R;
import com.wakely.wakely.interfaces.OnDataPass;

/**
 * QR scanner fragment based on {@code CodeScanner}.
 *
 * <p>Decoded text is sent to the hosting Activity via {@link OnDataPass}.</p>
 */
public class QrScannerFragment extends Fragment {
    private CodeScanner mCodeScanner;
    private String scanResult;

    public String getScanResult() {
        return scanResult;
    }

    public QrScannerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(result ->
                activity.runOnUiThread(() -> {
                    scanResult = result.getText();
                    Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    if (dataPasser != null) {
                        dataPasser.onDataPass(scanResult);
                    }
                }));

        scannerView.setOnClickListener(view ->
                mCodeScanner.startPreview());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }



    OnDataPass dataPasser;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

}


