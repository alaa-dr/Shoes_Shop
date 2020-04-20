package com.example.shoes_shop.ui.home.Recycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoes_shop.R;
import com.example.shoes_shop.database.DbContract;
import com.example.shoes_shop.database.dao.OrderHome;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<OrderHome> arrayList;

    public RecyclerAdapter(ArrayList<OrderHome> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_orders,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        holder.Name.setText(arrayList.get(position).getName());
        int sync_status = arrayList.get(position).getSync_status();
        if (sync_status == DbContract.SYNC_STATUS_SUCCESS){
            holder.Sync_Status.setImageResource(android.R.drawable.presence_online);
        }else if(sync_status == DbContract.SYNC_STATUS_FAILED){
            holder.Sync_Status.setImageResource(android.R.drawable.presence_offline);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Sync_Status;
        TextView Name;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Sync_Status= (ImageView) itemView.findViewById(R.id.imageSync);
            Name =(TextView) itemView.findViewById(R.id.name);

        }
    }
}
