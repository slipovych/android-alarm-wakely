package com.wakely.wakely;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wakely.wakely.data.AppDataBase;
import com.wakely.wakely.data.EventDao;
import com.wakely.wakely.data.QrDao;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Application singleton that owns the Room database and exposes DAOs.
 *
 * <p>Note: this is legacy code and currently uses {@code allowMainThreadQueries()},
 * which is convenient for a prototype but not suitable for production apps.</p>
 */
public class App extends Application {

    private AppDataBase dataBase;
    private EventDao eventDao;
    private QrDao qrDao;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        dataBase = Room.databaseBuilder(getApplicationContext(),
                AppDataBase.class, "app-database")
                .allowMainThreadQueries()
                .addMigrations(new Migration(1,2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        database.execSQL("CREATE TABLE `qr` (`qrId` INTEGER, "
                                + "`qrName` TEXT,"+" 'qrCode' TEXT, PRIMARY KEY(`qrId`))");
                    }
                })
                .build();


        eventDao = dataBase.eventDao();
        qrDao = dataBase.qrDao();
    }

    /**
     * Override the {@link EventDao} instance (primarily useful for tests).
     */
    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    /**
     * @return DAO for {@link com.wakely.wakely.model.Event} entity.
     */
    public EventDao getEventDao() {
        return eventDao;
    }

    /**
     * @return DAO for {@link com.wakely.wakely.qrscanner.model.Qr} entity.
     */
    public QrDao getQrDao() {
        return qrDao;
    }

    /**
     * Override the {@link QrDao} instance (primarily useful for tests).
     */
    public void setQrDao(QrDao qrDao) { this.qrDao = qrDao; }

    public void setDataBase(AppDataBase dataBase) {
        this.dataBase = dataBase;
    }

    public AppDataBase getDataBase() {
        return dataBase;
    }
}


