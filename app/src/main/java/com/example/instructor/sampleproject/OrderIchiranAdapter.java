package com.example.instructor.sampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ntsol on 2016/08/12.
 */
public class OrderIchiranAdapter extends ArrayAdapter<OrderItemListViewValue> {

    LayoutInflater layoutInflater;
    ArrayList<OrderItemListViewValue> orderIchiranItemList;

    public OrderIchiranAdapter(Context context, int rowLayoutResourceId, ArrayList<OrderItemListViewValue> items) {
        super(context,rowLayoutResourceId,items);
        orderIchiranItemList = items;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderIchiranItemList.size();
    }

    @Override
    public OrderItemListViewValue getItem(int position) {
        return orderIchiranItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.activity_orderichiranitemlistviewvalue, parent, false);
        } else
        {
            view = convertView;
        }

        
        ((TextView)view.findViewById(R.id.OrderIchiranItem_ItemName)).setText(orderIchiranItemList.get(position).getItemName());
        ((TextView)view.findViewById(R.id.OrderIchiranItem_Price)).setText(CommonUtil.KingakuFormat(orderIchiranItemList.get(position).getPrice()));
        ((TextView)view.findViewById(R.id.OrderIchiranItem_Kosu)).setText(orderIchiranItemList.get(position).getKosu() + "個");
        ((TextView)view.findViewById(R.id.OrderIchiranItem_kingaku)).setText(CommonUtil.KingakuFormat((orderIchiranItemList.get(position).getKosu() * orderIchiranItemList.get(position).getPrice())));
        ((TextView)view.findViewById(R.id.OrderIchiranItem_OrderDate)).setText(orderIchiranItemList.get(position).getOrderDate());
        ((TextView)view.findViewById(R.id.OrderIchiranItem_OrderStatus)).setText(orderIchiranItemList.get(position).getOrderStatusName());


        CheckBox checkBox = (CheckBox) view.findViewById(R.id.OrderIchiranItem_CheckBox);
        final int p = position;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                orderIchiranItemList.get(p).setIsChecked(isChecked);
            }
        });
        checkBox.setChecked(orderIchiranItemList.get(p).getIsChecked());

        return view;
    }

    public ArrayList<OrderItemListViewValue> getOrderIchiranItemList()
    {
        return orderIchiranItemList;
    }
}
