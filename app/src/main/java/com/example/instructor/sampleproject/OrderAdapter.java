package com.example.instructor.sampleproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/12.
 */
public class OrderAdapter extends ArrayAdapter<OrderItemListViewValue> {

    LayoutInflater layoutInflater;
    ArrayList<OrderItemListViewValue> orderItemList;

    public OrderAdapter(Context context, int rowLayoutResourceId, ArrayList<OrderItemListViewValue> items) {
        super(context,rowLayoutResourceId,items);
        orderItemList = items;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = inflater.inflate(R.layout.activity_orderlistviewvalue, parent, false);
        }

        final OrderItemListViewValue value = orderItemList.get(position);

        ((TextView)convertView.findViewById(R.id.OrderItem_ItemId)).setText(orderItemList.get(position).getItemCd());
        TextView txtViewOsusume = (TextView)convertView.findViewById(R.id.OrderItem_Osusume);

        if(orderItemList.get(position).getOsusume().equals("1"))
        {
            txtViewOsusume.setText("おすすめ");
            txtViewOsusume.setTextColor(Color.parseColor("red"));
        }
        else
        {
            txtViewOsusume.setText("");
            txtViewOsusume.setTextColor(Color.parseColor("black"));
        }

        TextView txtViewPresent = (TextView)convertView.findViewById(R.id.OrderItem_Present);
        if(orderItemList.get(position).getPresentFlg().equals("1"))
        {
            txtViewPresent.setText("プ");
            txtViewPresent.setTextColor(Color.parseColor("magenta"));
        }else
        {
            txtViewPresent.setText("");
            txtViewPresent.setTextColor(Color.parseColor("black"));
        }

        ((TextView)convertView.findViewById(R.id.OrderItem_ItemName)).setText(orderItemList.get(position).getItemName());
        ((TextView)convertView.findViewById(R.id.OrderItem_Price)).setText(CommonUtil.KingakuFormat(orderItemList.get(position).getPrice()));
        ((TextView)convertView.findViewById(R.id.OrderItem_Zaiko)).setText(orderItemList.get(position).getZaiko() + "個");
        if(orderItemList.get(position).getKosu()==null)
        {
            orderItemList.get(position).setKosu(0);
        }

        ((TextView)convertView.findViewById(R.id.Order_Kosu)).setText(orderItemList.get(position).getKosu() + "個");

        return convertView;
    }

    public ArrayList<OrderItemListViewValue> getOrderItemList()
    {
        return orderItemList;
    }



}
