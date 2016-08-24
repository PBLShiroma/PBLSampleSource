package com.example.instructor.sampleproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/12.
 */
public class OrderSelectItemAdapter extends ArrayAdapter<OrderItemListViewValue> {

    LayoutInflater layoutInflater;
    ArrayList<OrderItemListViewValue> orderSelectItemList;

    public OrderSelectItemAdapter(Context context, int rowLayoutResourceId, ArrayList<OrderItemListViewValue> items) {
        super(context,rowLayoutResourceId,items);
        orderSelectItemList = items;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderSelectItemList.size();
    }

    @Override
    public OrderItemListViewValue getItem(int position) {
        return orderSelectItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.activity_orderselectitemvalue, parent, false);
        } else
        {
            view = convertView;
        }

        ((TextView)view.findViewById(R.id.OrderItem_ItemId)).setText(orderSelectItemList.get(position).getItemCd());

        TextView txtViewOsusume = (TextView)view.findViewById(R.id.OrderItem_Osusume);

        if(orderSelectItemList.get(position).getOsusume().equals("1"))
        {
            txtViewOsusume.setText("おすすめ");
            txtViewOsusume.setTextColor(Color.parseColor("red"));
        }
        else
        {
            txtViewOsusume.setText("");
            txtViewOsusume.setTextColor(Color.parseColor("black"));
        }

        TextView txtViewPresent = (TextView)view.findViewById(R.id.OrderItem_Present);
        if(orderSelectItemList.get(position).getPresentFlg().equals("1"))
        {
            txtViewPresent.setText("プ");
            txtViewPresent.setTextColor(Color.parseColor("magenta"));
        }else
        {
            txtViewPresent.setText("");
            txtViewPresent.setTextColor(Color.parseColor("black"));
        }

        ((TextView)view.findViewById(R.id.OrderItem_ItemName)).setText(orderSelectItemList.get(position).getItemName());
        ((TextView)view.findViewById(R.id.OrderItem_Price)).setText(CommonUtil.KingakuFormat(orderSelectItemList.get(position).getPrice()));
        ((TextView)view.findViewById(R.id.OrderItem_Zaiko)).setText(orderSelectItemList.get(position).getZaiko() + "個");


        CheckBox checkBox = (CheckBox) view.findViewById(R.id.OrderSelectItem_CheckBox);
        final int p = position;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                orderSelectItemList.get(p).setIsChecked(isChecked);
            }
        });
        checkBox.setChecked(orderSelectItemList.get(p).getIsChecked());

        return view;
    }

    public ArrayList<OrderItemListViewValue> getOrderSelectItemList()
    {
        return orderSelectItemList;
    }
}
