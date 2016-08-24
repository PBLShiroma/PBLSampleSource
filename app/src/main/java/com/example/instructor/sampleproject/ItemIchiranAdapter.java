package com.example.instructor.sampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/12.
 */
public class ItemIchiranAdapter extends ArrayAdapter<OrderItemListViewValue> {

    LayoutInflater layoutInflater;
    ArrayList<OrderItemListViewValue> itemIchiranList;

    public ItemIchiranAdapter(Context context, int rowLayoutResourceId, ArrayList<OrderItemListViewValue> items) {
        super(context,rowLayoutResourceId,items);
        itemIchiranList = items;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemIchiranList.size();
    }

    @Override
    public OrderItemListViewValue getItem(int position) {
        return itemIchiranList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.activity_itemlistviewvalue, parent, false);
        } else
        {
            view = convertView;
        }

        ((TextView)view.findViewById(R.id.OrderItem_ItemId)).setText(itemIchiranList.get(position).getItemCd());
        ((TextView)view.findViewById(R.id.OrderItem_Osusume)).setText(itemIchiranList.get(position).getItemAddressName());
        TextView txtView = (TextView)view.findViewById(R.id.OrderItem_Present);
        if(itemIchiranList.get(position).getPresentFlg().equals("1"))
        {
            txtView.setText("プ");
        }else
        {
            txtView.setText("");
        }

        ((TextView)view.findViewById(R.id.OrderItem_ItemName)).setText(itemIchiranList.get(position).getItemName());
        ((TextView)view.findViewById(R.id.OrderItem_Price)).setText(CommonUtil.KingakuFormat(itemIchiranList.get(position).getPrice()));
        ((TextView)view.findViewById(R.id.OrderItem_Zaiko)).setText(itemIchiranList.get(position).getZaiko() + "個");


        return view;
    }

    public ArrayList<OrderItemListViewValue> getItemIchiranList()
    {
        return itemIchiranList;
    }
}
