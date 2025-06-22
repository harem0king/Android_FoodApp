package com.example.cse441_project.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.TextView;

import com.example.cse441_project.FoodItem.HomeActivity;
import com.example.cse441_project.R;

public class FoodAddSuccess {
    private Context context;

    public FoodAddSuccess(Context context) {
        this.context = context;
    }

    public void showSuccessDialog() {
        Dialog successDialog = new Dialog(context);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_scuccess_food);
        successDialog.setCancelable(false);
        successDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = successDialog.findViewById(R.id.dialog_message);
        message.setText("Thêm món ăn thành công");

        successDialog.show();

        // Đợi 1 giây, sau đó chuyển sang Activity khác
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            successDialog.dismiss();
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
        }, 1000);
    }
}
