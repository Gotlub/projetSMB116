package com.smb116.project.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smb116.project.R;
import com.smb116.project.model.NContact;

import java.util.List;

public class NContactAdapter extends RecyclerView.Adapter<NContactAdapter.MyViewHolder> {

    private List<NContact> nContactList;
    private NContactAdapter.OnClickListener onClickListener;
    public NContactAdapter(List<NContact> nContactList) {
        this.nContactList = nContactList;
    }

    public void setNContactList(List<NContact> nContactList) {
        this.nContactList = nContactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ncontact_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NContactAdapter.MyViewHolder holder, int position) {
        NContact ncontact = nContactList.get(position);
        Log.d("RecyclerView", "bind position: " + position);
        holder.name.setText(ncontact.getName());
        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position, ncontact);
            }
        });
    }


    @Override
    public int getItemCount() {
        return nContactList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, NContact ncontact);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            name = itemView.findViewById(R.id.nomNContact);

        }
    }
}
