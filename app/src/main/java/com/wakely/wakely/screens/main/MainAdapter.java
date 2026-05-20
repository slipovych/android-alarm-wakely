package com.wakely.wakely.screens.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.wakely.wakely.R;
import com.wakely.wakely.model.Event;
import com.wakely.wakely.screens.alarm.AlarmRedactor;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    public interface EventToggleListener {
        void onEventToggled(Event event, boolean enabled);
    }

    public interface EventDeleteListener {
        void onEventDeleted(Event event);
    }

    private final SortedList<Event> sortedList;
    private final EventToggleListener toggleListener;
    private final EventDeleteListener deleteListener;

    public MainAdapter(EventToggleListener toggleListener, EventDeleteListener deleteListener) {
        this.toggleListener = toggleListener;
        this.deleteListener = deleteListener;
        sortedList = new SortedList<>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                // Scheduled alarms first.
                if (o1.scheduled != o2.scheduled) {
                    return o1.scheduled ? -1 : 1;
                }
                // Then by id (stable-ish order).
                return Integer.compare(o2.uid, o1.uid);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return item1.uid == item2.uid;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false),
                toggleListener,
                deleteListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }


    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Event> events) {
        sortedList.replaceAll(events);
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {

        TextView alarmTime;
        SwitchCompat switchAlarm;
        ImageButton deleteEvent;

        Event event;
        private final EventToggleListener toggleListener;
        private final EventDeleteListener deleteListener;

        public MainViewHolder(View itemView, EventToggleListener toggleListener, EventDeleteListener deleteListener) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.AlarmTime);
            switchAlarm = itemView.findViewById(R.id.switchAlarm);
            deleteEvent = itemView.findViewById(R.id.deleteEvent);
            this.toggleListener = toggleListener;
            this.deleteListener = deleteListener;


            itemView.setOnClickListener(view -> AlarmRedactor.start((android.app.Activity) itemView.getContext(), event));

            switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (event != null && toggleListener != null) {
                    toggleListener.onEventToggled(event, isChecked);
                }
            });

            deleteEvent.setOnClickListener(v -> {
                if (event != null && deleteListener != null) {
                    deleteListener.onEventDeleted(event);
                }
            });

        }

        public void bind(Event event) {
            this.event = event;


            alarmTime.setText(event.time);
            // Avoid firing listener during RecyclerView rebind.
            switchAlarm.setOnCheckedChangeListener(null);
            switchAlarm.setChecked(event.scheduled);
            switchAlarm.setEnabled(true);
            switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (this.event != null && toggleListener != null) {
                    toggleListener.onEventToggled(this.event, isChecked);
                }
            });

        }


    }
}
