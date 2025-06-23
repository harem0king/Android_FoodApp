package com.example.cse441_project.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse441_project.R;

public class ConfirmationDialog {

    private Dialog dialog;
    private TextView dialogTitle, dialogMessage;
    private Button buttonCancel, buttonOk;

    public ConfirmationDialog(Context context) {
        // Khởi tạo dialog
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confim_category_delete); // Đường dẫn đến layout XML

        // Ánh xạ các view trong layout
        dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogMessage = dialog.findViewById(R.id.dialog_message);
        buttonCancel = dialog.findViewById(R.id.button_cancel);
        buttonOk = dialog.findViewById(R.id.button_ok);

        // Thiết lập dialog không thể bị tắt bằng cách nhấn ra ngoài
        dialog.setCancelable(false);
    }

    // Thiết lập tiêu đề cho dialog
    public void setTitle(String title) {
        dialogTitle.setText(title);
    }

    // Thiết lập nội dung cho dialog
    public void setMessage(String message) {
        dialogMessage.setText(message);
    }

    // Thiết lập sự kiện cho nút OK
    public void setOkButtonClickListener(View.OnClickListener listener) {
        buttonOk.setOnClickListener(v -> {
            listener.onClick(v); // Gọi hàm listener khi nút OK được nhấn
            dismiss(); // Đóng dialog sau khi nhấn OK
        });
    }

    // Thiết lập sự kiện cho nút Hủy
    public void setCancelButtonClickListener(View.OnClickListener listener) {
        buttonCancel.setOnClickListener(v -> {
            listener.onClick(v); // Gọi hàm listener khi nút Hủy được nhấn
            dismiss(); // Đóng dialog sau khi nhấn Hủy
        });
    }

    // Hiển thị dialog
    public void show() {
        dialog.show();
    }

    // Ẩn dialog
    public void dismiss() {
        dialog.dismiss();
    }
}