package com.wakely.wakely.qrscanner.screens;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.wakely.wakely.App;
import com.wakely.wakely.R;
import com.wakely.wakely.qrscanner.model.Qr;

import java.util.List;

public class QrAdapter extends RecyclerView.Adapter<QrAdapter.QrViewHolder> {

    private final SortedList<Qr> sortedList;
    private Qr selected;

    public QrAdapter() {
        sortedList = new SortedList<>(Qr.class, new SortedList.Callback<Qr>() {
            @Override
            public int compare(Qr o1, Qr o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Qr oldItem, Qr newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Qr item1, Qr item2) {
                return item1.qrId == item2.qrId;
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
    public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QrViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr, parent, false),
                this
        );
    }

    @Override
    public void onBindViewHolder(@NonNull QrViewHolder holder, int position) {
        Qr item = sortedList.get(position);
        holder.bind(item, selected != null && selected.qrId == item.qrId);
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }


    public void setItems(List<Qr> qrs) {
        sortedList.replaceAll(qrs);
    }

    public Qr getSelected() {
        return selected;
    }

    private void select(Qr qr) {
        selected = qr;
        notifyDataSetChanged();
    }

    static class QrViewHolder extends RecyclerView.ViewHolder {

        public final static String QR_EXTRA = "QrViewHolder.QR_EXTRA";

        RadioButton qrPicked;
        View deleteQrButton;

        Qr qr;


        public QrViewHolder(@NonNull View itemView, QrAdapter adapter) {
            super(itemView);

            qrPicked = itemView.findViewById(R.id.qr_picked);
            qrPicked.setOnClickListener(view -> adapter.select(qr));

            deleteQrButton = itemView.findViewById(R.id.delete_qr);
            deleteQrButton.setOnClickListener(view -> App.getInstance().getQrDao().delete(qr));
        }


        public void bind(Qr qr, boolean isSelected) {
            this.qr = qr;

            qrPicked.setText(qr.qrName);
            qrPicked.setChecked(isSelected);
            //TODO What qr shall I use (AlarmRedactor)
        }
    }
}
