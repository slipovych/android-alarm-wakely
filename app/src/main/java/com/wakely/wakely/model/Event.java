package com.wakely.wakely.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Alarm/event entity stored in Room.
 *
 * <p>At the moment {@link #time} is stored as a display string; it is not used for rescheduling
 * after reboot.</p>
 */
@Entity
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "mission")
    public String mission;

    @ColumnInfo(name = "scheduled")
    public boolean scheduled;

    public int relatedQrId;

    public Event() {
    }

    protected Event(Parcel in) {
        uid = in.readInt();
        time = in.readString();
        mission = in.readString();
        scheduled = in.readByte() != 0;
        relatedQrId = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Event)) return false;
        Event other = (Event) obj;
        return uid == other.uid
                && scheduled == other.scheduled
                && relatedQrId == other.relatedQrId
                && Objects.equals(time, other.time)
                && Objects.equals(mission, other.mission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, time, mission, scheduled, relatedQrId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(time);
        parcel.writeString(mission);
        parcel.writeByte((byte) (scheduled ? 1 : 0));
        parcel.writeInt(relatedQrId);
    }


}
