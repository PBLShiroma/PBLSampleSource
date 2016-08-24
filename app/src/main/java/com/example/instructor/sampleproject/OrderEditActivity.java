package com.example.instructor.sampleproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by instructor on 2016/08/18.
 */
public class OrderEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderedit);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_OrderEdit);

        ListView listView = (ListView) findViewById(R.id.orderEditItemListView);

        //遷移元画面から値の受取
        Intent intent = getIntent();
        ArrayList<OrderItemListViewValue> selectItemList = (ArrayList<OrderItemListViewValue>)intent.getSerializableExtra(DefineCode.orderItem_CheckItems);

        //注文ステータスSpinnerの生成
        ArrayAdapter<OrderStatusValue> spinnerAdapter = CreateOrderStatusSpinner();

        // アダプターを設定
        final OrderEditAdapter myAdapter = new OrderEditAdapter(this,android.R.layout.simple_list_item_multiple_choice,selectItemList,spinnerAdapter);
        listView.setAdapter(myAdapter);

        //更新ボタン
        SetUpdateButton();

        //戻るボタン
        SetBackOrderButton();

    }

    /*
    注文ステータスSpinnerの生成
     */
    private ArrayAdapter<OrderStatusValue> CreateOrderStatusSpinner()
    {
        MyOpenHelper helper = new MyOpenHelper(this);

        ArrayAdapter<OrderStatusValue> adapter = new ArrayAdapter<OrderStatusValue>(this, R.layout.activity_orderstatus_spinner);
        adapter.setDropDownViewResource(R.layout.activity_orderstatus_spinner_dropdown);
        SQLiteDatabase dbRead = null;
        Cursor cursor = null;

        try
        {
            dbRead = helper.getReadableDatabase();

            // select文
            String sql    = CreateSQLCodeM();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                adapter.add(new OrderStatusValue(cursor.getString(0), cursor.getString(1)));
                mov = cursor.moveToNext();
            }

        }catch(Exception e)
        {
            Toast.makeText( OrderEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            if(cursor != null)
            {
                cursor.close();
            }

            dbRead.close();

        }
        return adapter;
    }

    private String CreateSQLCodeM()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT KeySubCd,");
        sql.append("       CdValue ");
        sql.append("FROM  CodeM ");
        sql.append(CreateSQLOrderWhere());
        sql.append("ORDER BY KeyCd,KeySubCd ");

        return sql.toString();

    }

    private String CreateSQLOrderWhere()
    {
        String sqlWhere = "";

        switch(loginUser.getUserType()) {
            case DefineCode.CodeM_UserType_UketukeTanto:
                sqlWhere = "WHERE KeyCd='" + DefineCode.CodeM_OrderStatusType + "' " +
                           " AND (KeySubCD='" + DefineCode.CodeM_OrderStatus_Kakutei + "'" +
                           " OR KeySubCD='" + DefineCode.CodeM_OrderStatus_ZaikoNashi +  "') ";
                break;
            case DefineCode.CodeM_UserType_HachuTanto:
                sqlWhere = "WHERE KeyCd='" + DefineCode.CodeM_OrderStatusType + "' " +
                        " AND (KeySubCD='" + DefineCode.CodeM_OrderStatus_Kakutei + "'" +
                        " OR KeySubCD='" + DefineCode.CodeM_OrderStatus_HasouZumi +  "') ";
                break;
            case DefineCode.CodeM_UserType_SeikyuTanto:
                sqlWhere = "WHERE KeyCd='" + DefineCode.CodeM_OrderStatusType + "' " +
                        " AND (KeySubCD='" + DefineCode.CodeM_OrderStatus_HasouZumi + "'" +
                        " OR KeySubCD='" + DefineCode.CodeM_OrderStatus_Seikyu +  "') ";
                break;
            case DefineCode.CodeM_UserType_Admin:
                sqlWhere = "WHERE KeyCd='" + DefineCode.CodeM_OrderStatusType + "' ";
                break;
        }

        return sqlWhere;
    }

    private void SetUpdateButton()
    {
        Button btn = (Button) findViewById(R.id.OrderEdit_UpdateBtn);
        final AlertDialog.Builder kakuninBuilder = new AlertDialog.Builder(this);
        final MyOpenHelper helper = new MyOpenHelper(this);

        final ListView listView = (ListView) findViewById(R.id.orderEditItemListView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakuninBuilder.setTitle(DefineCode.Msg_Update_Kakunin);
                kakuninBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final SQLiteDatabase db = helper.getWritableDatabase();

                        OrderEditAdapter myAdapter = (OrderEditAdapter)listView.getAdapter();
                        ArrayList<OrderItemListViewValue> list = myAdapter.getOrderItemList();

                        Date date = new Date();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy'/'MM'/'dd'/' hh':'mm':'ss");
                        //DB保存
                        try {
                            db.beginTransaction();

                            for(int i = 0;i<=list.size()-1;i++)
                            {
                                //注文テーブルの更新
                                ContentValues updateValues = new ContentValues();
                                updateValues.put("OrderStatus", list.get(i).getOrderStatus());
                                updateValues.put("UpdateDate", sdf1.format(date));

                                long id1 = db.update("OrderTable", updateValues, "OrderID=?", new String[] { list.get(i).getOrderId().toString() });
                            }

                            db.setTransactionSuccessful();

                            Toast.makeText( OrderEditActivity.this, DefineCode.Msg_Success_Edit, Toast.LENGTH_SHORT ).show();

                            Intent dbIntent = new Intent(OrderEditActivity.this, OrderIchiranActivity.class);
                            startActivity(dbIntent);
                            finish();

                        } catch (Exception e) {

                            Toast.makeText( OrderEditActivity.this, DefineCode.Msg_Error_Edit, Toast.LENGTH_SHORT ).show();

                        }finally {
                            db.endTransaction();
                        }
                    }
                });
                kakuninBuilder.setNegativeButton("キャンセル",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                kakuninBuilder.show();
            }
        });
    }

    private void SetBackOrderButton()
    {
        Button btn = (Button) findViewById(R.id.OrderEdit_BackBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(OrderEditActivity.this, OrderIchiranActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }
}
