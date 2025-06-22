package com.example.cse441_project.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse441_project.R;

public class FoodConfirmDelete {
    private Dialog dialog;
    private OnDialogActionListener listener;

    // Constructor nhận context và listener
    public FoodConfirmDelete(Context context, OnDialogActionListener listener) {
        this.dialog = new Dialog(context);
        this.listener = listener;
        dialog.setContentView(R.layout.dialog_confim_food);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ các thành phần trong Dialog
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button buttonCancel = dialog.findViewById(R.id.button_cancel);
        Button buttonOk = dialog.findViewById(R.id.button_ok);

        // Thiết lập nội dung
        dialogTitle.setText("Xóa món ăn");
        dialogMessage.setText("Bạn có chắc chắn muốn xóa món ăn này không?");

        // Sự kiện khi nhấn "Hủy"
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Sự kiện khi nhấn "OK"
        buttonOk.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmDelete();
            }
            dialog.dismiss();
        });
    }

    // Hiển thị Dialog
    public void show() {
        dialog.show();
    }

    // Định nghĩa một interface để xử lý sự kiện xóa
    public interface OnDialogActionListener {
        void onConfirmDelete();
    }
}
