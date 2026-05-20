package com.wakely.wakely.screens.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.wakely.wakely.App;
import com.wakely.wakely.model.Event;

import java.util.List;

/**
 * ViewModel for {@link com.wakely.wakely.screens.main.MainActivity}.
 *
 * <p>Exposes events from Room as LiveData.</p>
 */
public class MainViewModel extends ViewModel {
    private final LiveData<List<Event>> eventLiveData = App.getInstance().getEventDao().getAllLiveData();

    public LiveData<List<Event>> getEventLiveData() {
        return eventLiveData;
    }
}
