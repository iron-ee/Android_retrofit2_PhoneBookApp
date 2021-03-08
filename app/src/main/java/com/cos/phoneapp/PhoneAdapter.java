package com.cos.phoneapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// 어댑터와 RecyclerView와 연결 (MVVM 사용 안해도 됨)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneHolder> {

    private static final String TAG = "PhoneAdapter";
    private List<Phone> phones;
    private MainActivity mainActivity;

    public PhoneAdapter(MainActivity mainActivity, List<Phone> phones) {
        this.mainActivity = mainActivity;
        this.phones = phones;
    }

    @NonNull
    @Override
    public PhoneHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item, parent, false);
        return new PhoneHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class PhoneHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvTel;

        public PhoneHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.name);
            tvTel = itemView.findViewById(R.id.tel);

            itemView.setOnClickListener(v -> {
                Phone phone = phones.get(getAdapterPosition());
                mainActivity.update(getAdapterPosition(), phone);
            });
        }

        public void setItem(Phone phone) {
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());
        }
    }
}
