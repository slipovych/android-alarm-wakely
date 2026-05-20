package com.wakely.wakely.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.wakely.wakely.model.Event;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;

/**
 * Helper POJO for Room relation: QR -> Events.
 *
 * <p>Not currently used by the UI, but kept as a model for future query expansion.</p>
 */
public class EventsWithQr {

    @Embedded
    public Qr qr;

    @Relation(
            parentColumn = "qrId",
            entityColumn = "relatedQrId"
    )
    public List<Event> eventsWithQr;
}
