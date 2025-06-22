package com.example.cse441_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cse441_project.FoodItem.EditFood;
import com.example.cse441_project.Model.FoodItem;
import com.example.cse441_project.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class EditFoodAdapter extends RecyclerView.Adapter<EditFoodAdapter.ViewHolder> {

    private List<FoodItem> foodItemList;
    private Context context;

    public EditFoodAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false); // Đổi 'your_linear_layout_layout' với tên layout của bạn
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.textView1.setText(foodItem.getFoodName());
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = numberFormat.format(foodItem.getPrice()) + "đ";

        holder.textView2.setText(formattedPrice);
        Glide.with(holder.imageView.getContext())
                .load(foodItem.getImageUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditFood.class);
            intent.putExtra("foodItem", foodItem); // Truyền FoodItem qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1;
        TextView textView2;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.myImageView);
            textView1 = itemView.findViewById(R.id.myTextView1);
            textView2 = itemView.findViewById(R.id.myTextView2);
        }
    }
}

