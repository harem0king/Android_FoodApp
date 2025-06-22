package com.example.cse441_project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cse441_project.Model.Table;
import com.example.cse441_project.R;

import java.util.List;

public class TableAdapter extends BaseAdapter {
    private Context context;
    private List<Table> tableList;

    public TableAdapter(Context context, List<Table> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    @Override
    public int getCount() {
        return tableList.size();
    }

    @Override
    public Object getItem(int position) {
        return tableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tableNameTextView = convertView.findViewById(R.id.tableNameTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu từ danh sách
        Table table = tableList.get(position);
        viewHolder.tableNameTextView.setText(table.getTableName());

        // Thay đổi màu nền theo trạng thái bàn
        if ("available".equals(table.getStatus())) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.red_500)); // Bàn trống
        } else if ("reserved".equals(table.getStatus())) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.green)); // Bàn chưa thanh toán
        } else if ("busy".equals(table.getStatus())) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Bàn đang có khách
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white)); // Mặc định
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tableNameTextView;
    }
}
