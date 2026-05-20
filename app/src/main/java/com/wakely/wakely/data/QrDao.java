package com.wakely.wakely.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wakely.wakely.model.Event;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;

/**
 * Data access object for {@link Qr} entity.
 */
@Dao
public interface QrDao {
    /**
     * @return all QR entries (blocking call).
     */
    @Query("SELECT * FROM Qr")
    List<Qr> getAll();

    /**
     * @return all QR entries as LiveData stream.
     */
    @Query("SELECT * FROM Qr")
    LiveData<List<Qr>> getAllLiveData();

    @Query("SELECT * FROM Qr WHERE qrId IN (:userIds)")
    List<Qr> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM Qr WHERE qrId = :uid LIMIT 1")
    Qr findById(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Qr qr);

    @Update
    void update(Qr qr);

    @Delete
    void delete(Qr qr);

}
