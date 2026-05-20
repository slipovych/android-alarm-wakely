package com.wakely.wakely.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.wakely.wakely.model.Event;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;

/**
 * Data access object for {@link Event} entity.
 */
@Dao
public interface EventDao {

    /**
     * @return all events (blocking call).
     */
    @Query("SELECT * FROM Event")
    List<Event> getAll();

    /**
     * @return all events as LiveData stream.
     */
    @Query("SELECT * FROM Event")
    LiveData<List<Event>> getAllLiveData();

    @Query("SELECT * FROM Event WHERE uid IN (:userIds)")
    List<Event> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Event WHERE uid = :uid LIMIT 1")
    Event findById(int uid);

    @Query("DELETE FROM Event")
    void deleteAll();

    /**
     * Insert or replace a single event.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAll(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);
}


