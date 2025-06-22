package com.example.cse441_project.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse441_project.R;

public class EditSuccessDialog extends Dialog {

    public EditSuccessDialog(Context context, String thôngBáo, String sửaThưMụcThànhCông) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cucess_category_edit); // Thay đổi đường dẫn nếu cần

        // Lấy các view trong dialog
        TextView dialogTitle = findViewById(R.id.dialog_title);
        TextView dialogMessage = findViewById(R.id.dialog_message);
        Button buttonOk = findViewById(R.id.button_ok);

        // Thiết lập tiêu đề và nội dung thông báo
        dialogTitle.setText("Thông báo");
        dialogMessage.setText("Sửa thư mục thành công");

        // Thiết lập sự kiện cho nút OK
        buttonOk.setOnClickListener(v -> dismiss()); // Đóng dialog khi nhấn OK
    }
}
