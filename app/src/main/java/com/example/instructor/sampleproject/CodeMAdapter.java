package com.example.instructor.sampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by instructor on 2016/08/05.
 */
public class CodeMAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<CodeMValue> codeMList;

    public CodeMAdapter(Context context) {

        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCodeMList(ArrayList<CodeMValue> codeMList) {
        this.codeMList = codeMList;
    }

    @Override
    public int getCount() {
        return codeMList.size();
    }

    @Override
    public Object getItem(int position) {
        return codeMList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return codeMList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.activity_codemvalue,parent,false);

        ((TextView)convertView.findViewById(R.id.CodeMValue_KeyCd)).setText(codeMList.get(position).getKeyCd());
        ((TextView)convertView.findViewById(R.id.CodeMValue_KeySubCd)).setText(codeMList.get(position).getKeySubCd());
        ((TextView)convertView.findViewById(R.id.CodeMValue_CdValue)).setText(codeMList.get(position).getCdValue());

        return convertView;
    }
}
