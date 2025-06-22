package com.example.cse441_project.Order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cse441_project.R;

public class SuccessDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_dialog, container, false);

        // Thiết lập nội dung cho dialog
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        messageTextView.setText("In thành công");

        // Nút OK để đóng dialog
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang Activity mới
            Intent intent = new Intent(getActivity(), TableListActivity.class); // Thay NewActivity bằng tên Activity bạn muốn mở
            startActivity(intent);

            // Đóng dialog nếu là dialog fragment
            dismiss();
        });


        return view;
    }
}
