package com.example.mainactivity.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mainactivity.Model.Item;
import com.example.mainactivity.R;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {

    private Activity activity;
    private ArrayList<Item> adapter_list;


    public ItemAdapter(Activity act, int resource, ArrayList<Item> data) {
        super(act, resource, data);
        activity = act;
        adapter_list = data;
    }

    public class viewHolder{
        Item item;
        TextView list_row_TextView;
    }

    @Override
    public int getCount() {
        return adapter_list.size();
    }

    @Override
    public Item getItem(int position) {
        return adapter_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listRowView = convertView;
        viewHolder holder = null;

        if (listRowView == null || listRowView.getTag() == null){
            LayoutInflater inflater = LayoutInflater.from(activity);
            listRowView = inflater.inflate(R.layout.list_row, parent, false);

            holder = new viewHolder();


            holder.list_row_TextView = (TextView) listRowView.findViewById(R.id.list_row_TextViewID);

            listRowView.setTag(holder);

        } else {
            holder = (viewHolder) listRowView.getTag();
        }

        holder.item = getItem(position);

        holder.list_row_TextView.setText(String.valueOf(holder.item.getInput()));

        final viewHolder finalviewHolder = holder;

        return listRowView;


    }
}
