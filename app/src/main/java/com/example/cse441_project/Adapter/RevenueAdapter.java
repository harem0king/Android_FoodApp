package com.example.cse441_project.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.Model.Order;
import com.example.cse441_project.R;

import java.util.List;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.ViewHolder> {

    private final List<Order> orderList;

    public RevenueAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã hóa đơn: " + order.getOrderId());
        holder.tvTotalAmount.setText("Tổng tiền: " + order.getTotalAmount() + "đ");
        holder.tvOrderDate.setText("Ngày: " + order.getOrderDate());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvTotalAmount, tvOrderDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}

