package com.example.cse441_project.Order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.Model.OrderDetail;
import com.example.cse441_project.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderFoodAdapter extends RecyclerView.Adapter<OrderFoodAdapter.ViewHolder> {
    private List<Integer> quantityList = new ArrayList<>();
    private List<FoodItem> foodItemList;
    private List<Float> orderDetails = new ArrayList<>();

    public OrderFoodAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
        for (int i = 0; i < 50; i++) {
            quantityList.add(0);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_order_food, parent, false); // Use the correct layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);


        holder.textView1.setText(foodItem.getFoodName());
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = numberFormat.format(foodItem.getPrice()) + "đ";
        holder.textView2.setText(formattedPrice);

        // Hiển thị hình ảnh món ăn
        Glide.with(holder.imageView.getContext())
                .load(foodItem.getImageUrl())
                .into(holder.imageView);

        //holder.editTextQuantity.setText(String.valueOf(quantityList.get(position)));
        holder.buttonMinus.setOnClickListener(v -> {
            int quantity = quantityList.get(position);
            if (quantity > 0) { // Giảm nếu số lượng lớn hơn 0
                quantityList.set(position, --quantity);
                holder.editTextQuantity.setText(String.valueOf(quantity));
            }
        });
        holder.buttonPlus.setOnClickListener(v -> {
            int quantity = quantityList.get(position);
            quantityList.set(position, ++quantity);
            holder.editTextQuantity.setText(String.valueOf(quantity));
        });
    }
    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1, textView2;
        Button buttonMinus, buttonPlus;
        TextView editTextQuantity; // Changed to TextView
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.myImageView);
            textView1 = itemView.findViewById(R.id.myTextView1);
            textView2 = itemView.findViewById(R.id.myTextView2);
            buttonMinus = itemView.findViewById(R.id.button_minus);
            buttonPlus = itemView.findViewById(R.id.button_plus);
            editTextQuantity = itemView.findViewById(R.id.edittext_quantity);

        }
    }
    public List<OrderDetail> getOrderDetails(String idOder) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (int i = 0; i < foodItemList.size(); i++) {
            if (quantityList.get(i) > 0) {
                FoodItem foodItem = foodItemList.get(i);
                int quantity = quantityList.get(i);
                orderDetails.add(new OrderDetail(idOder,foodItem.getItemFoodID(), quantity));
            }
        }
        return orderDetails;
    }
}