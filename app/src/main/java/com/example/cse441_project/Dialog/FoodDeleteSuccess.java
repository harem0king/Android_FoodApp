package com.example.cse441_project.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.TextView;

import com.example.cse441_project.R;

public class FoodDeleteSuccess {
    private Context context;

    public FoodDeleteSuccess(Context context) {
        this.context = context;
    }

    public void showSuccessDialog() {
        Dialog successDialog = new Dialog(context);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.dialog_scuccess_food);
        successDialog.setCancelable(false);
        successDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = successDialog.findViewById(R.id.dialog_message);
        message.setText("Xoá món ăn thành công!");

        successDialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(successDialog::dismiss, 1000);
    }
}
