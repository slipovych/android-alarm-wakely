package com.wakely.wakely.qrscanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * QR entry stored in Room.
 *
 * <p>{@link #qrCode} is stored as raw decoded text.</p>
 */
@Entity
public class Qr implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int qrId;

    @ColumnInfo(name = "qr_name")
    public String qrName;

    @ColumnInfo(name = "qr_code")
    public  String qrCode;

    public Qr() {

    }

    protected Qr(Parcel in) {
        qrId = in.readInt();
        qrName = in.readString();
        qrCode = in.readString();
    }

    public static final Creator<Qr> CREATOR = new Creator<Qr>() {
        @Override
        public Qr createFromParcel(Parcel in) {
            return new Qr(in);
        }

        @Override
        public Qr[] newArray(int size) {
            return new Qr[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(qrId);
        parcel.writeString(qrName);
        parcel.writeString(qrCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qrId, qrName, qrCode);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Qr)) return false;
        Qr other = (Qr) obj;
        return qrId == other.qrId
                && Objects.equals(qrName, other.qrName)
                && Objects.equals(qrCode, other.qrCode);
    }
}
