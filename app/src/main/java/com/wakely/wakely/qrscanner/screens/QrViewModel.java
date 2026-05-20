package com.wakely.wakely.qrscanner.screens;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.wakely.wakely.App;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;

/**
 * ViewModel for QR list screen.
 */
public class QrViewModel extends ViewModel {
    private LiveData<List<Qr>> qrLiveData = App.getInstance().getQrDao().getAllLiveData();

    public LiveData<List<Qr>> getQrLiveData() {
        return qrLiveData;
    }
}
