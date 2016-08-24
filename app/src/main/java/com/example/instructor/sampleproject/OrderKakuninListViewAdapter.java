package com.example.instructor.sampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/16.
 */
public class OrderKakuninListViewAdapter extends ArrayAdapter<OrderItemListViewValue> {

        private LayoutInflater mInflater;

        public OrderKakuninListViewAdapter(Context context, int resource, ArrayList<OrderItemListViewValue> objects,LayoutInflater inflater) {
            super(context, resource, objects);
            mInflater = inflater;
        }
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            OrderItemListViewValue orderItem = (OrderItemListViewValue)getItem(position);

            if (null == v) v = mInflater.inflate(R.layout.activity_ordereditkakuninlistview, null);

            ((TextView)v.findViewById(R.id.OrderKakunin_ItemName)).setText(orderItem.getItemName());
            ((TextView)v.findViewById(R.id.OrderKakunin_Price)).setText(CommonUtil.KingakuFormat(orderItem.getPrice()));
            ((TextView)v.findViewById(R.id.OrderKakunin_Kosu)).setText(String.valueOf(orderItem.getKosu()) + "個");

            return v;
        }

}
