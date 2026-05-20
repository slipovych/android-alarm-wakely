package com.wakely.wakely.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.wakely.wakely.model.Event;
import com.wakely.wakely.qrscanner.model.Qr;

/**
 * Room database definition for Wakely.
 *
 * <p>Holds two entities: {@link Event} and {@link Qr}.</p>
 */
@Database(version = 2, entities = {Event.class, Qr.class}, exportSchema = false)

public abstract class AppDataBase extends RoomDatabase {
    /**
     * @return DAO for {@link Event}.
     */
    public abstract EventDao eventDao();

    /**
     * @return DAO for {@link Qr}.
     */
    public abstract QrDao qrDao();


}
