package com.example.zegovideocall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    Context context;
    ArrayList<User> arrayList;
    OnItemClickListener onItemClickListener;

    public UsersAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userNameTV.setText(arrayList.get(position).getUserName());
        holder.userIdTV.setText(arrayList.get(position).getUserID());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTV, userIdTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.list_item_user_name);
            userIdTV = itemView.findViewById(R.id.list_item_user_id);
        }
    }

    public interface OnItemClickListener {
        void onClick(User user);
    }
}
