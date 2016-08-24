package com.example.instructor.sampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/12.
 */
public class OrderEditAdapter extends ArrayAdapter<OrderItemListViewValue> {

    LayoutInflater layoutInflater;
    ArrayList<OrderItemListViewValue> orderItemList;
    ArrayAdapter<OrderStatusValue> spinnerAdaptor;

    public OrderEditAdapter(Context context, int rowLayoutResourceId, ArrayList<OrderItemListViewValue> items, ArrayAdapter<OrderStatusValue> spinnerAdaptor) {
        super(context,rowLayoutResourceId,items);
        orderItemList = items;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.spinnerAdaptor = spinnerAdaptor;
    }

    @Override
    public int getCount() {
        return orderItemList.size();
    }

    @Override
    public OrderItemListViewValue getItem(int position) {
        return orderItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_ordereditlistviewvalue, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_ItemName)).setText(orderItemList.get(position).getItemName());
        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_Price)).setText(CommonUtil.KingakuFormat(orderItemList.get(position).getPrice()));
        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_Kosu)).setText(orderItemList.get(position).getKosu() + "個");
        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_OrderDate)).setText(orderItemList.get(position).getOrderDate());
        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_OrderStatus)).setText("");
        ((TextView)convertView.findViewById(R.id.OrderIchiranItem_kingaku)).setText(CommonUtil.KingakuFormat((orderItemList.get(position).getKosu() * orderItemList.get(position).getPrice())));

        Spinner orderStatusSpinner = (Spinner)convertView.findViewById(R.id.OrderEdit_OrderStatus);
        orderStatusSpinner.setAdapter(spinnerAdaptor);

        int index = 0;
        for (int i = 0; i < spinnerAdaptor.getCount(); i++) {

            if (spinnerAdaptor.getItem(i).getOrderStatusCd().equals(orderItemList.get(position).getOrderStatus())) {
                index = i;
                break;
            }
        }
        orderStatusSpinner.setSelection(index);

        final int p = position;
        orderStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView parent, View viw, int arg2, long arg3)
            {
                Spinner spinner = (Spinner)parent;
                OrderStatusValue selectedItem = (OrderStatusValue)spinner.getSelectedItem();
                orderItemList.get(p).setOrderStatusName(selectedItem.getOrderStatusName());
                orderItemList.get(p).setOrderStatus(selectedItem.getOrderStatusCd());
            }

            public void onNothingSelected(AdapterView parent) {

            }
        });

        return convertView;
    }

    public ArrayList<OrderItemListViewValue> getOrderItemList()
    {
        return orderItemList;
    }


}
