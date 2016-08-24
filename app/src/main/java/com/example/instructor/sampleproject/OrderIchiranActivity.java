package com.example.instructor.sampleproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by instructor on 2016/08/04.
 */
public class OrderIchiranActivity extends BaseActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderichiran);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_OrderIchiran);

        listView = (ListView) findViewById(R.id.OrderIchiranItemListView);

        // フォーカスが当たらないよう設定
        listView.setItemsCanFocus(false);

        //表示項目の設定
        SetInitList(listView);

        //更新ボタン
        SetUpdateButton();

        //削除ボタン
        SetDeleteButton();

        //戻るボタン
        SetBackOrderButton();

    }

    /*
    表示項目の設定
*/
    private void SetInitList(ListView listView)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<OrderItemListViewValue> list = new ArrayList<OrderItemListViewValue>();

        try
        {
            // select文
            String sql = CreateSQLOrder();

            //sql実行
            cursor = dbRead.rawQuery(sql ,CreateSQLOrderArg());

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                OrderItemListViewValue itemValue = new OrderItemListViewValue();
                itemValue.setOrderId(cursor.getInt(0));
                itemValue.setItemCd(cursor.getString(1));
                itemValue.setItemName(cursor.getString(2));
                itemValue.setPrice(cursor.getInt(3));
                itemValue.setKosu(cursor.getInt(4));
                itemValue.setOrderDate(cursor.getString(5));
                itemValue.setOrderStatus(cursor.getString(6));
                itemValue.setOrderStatusName(cursor.getString(7));
                list.add(itemValue);
                mov = cursor.moveToNext();
            }

            // アダプターを設定
            OrderIchiranAdapter myAdapter = new OrderIchiranAdapter(this,android.R.layout.simple_list_item_multiple_choice,list);
            listView.setAdapter(myAdapter);

            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();

            // アイテムクリック時のイベントを設定
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                }
            });

            //表示する注文がない場合
            if(list.size() == 0)
            {
                Toast.makeText( OrderIchiranActivity.this, DefineCode.Msg_Error_Order_NotOrder, Toast.LENGTH_SHORT ).show();
            }

        }catch (Exception e)
        {
            Toast.makeText( OrderIchiranActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();
        }
    }

    /*
    OrderTable検索SQL作成
     */
    private String CreateSQLOrder() {
        String sql    = "SELECT " +
                "OrderTable.OrderID," +
                "OrderTable.ItemCD," +
                "OrderTable.ItemName," +
                "OrderTable.Price," +
                "OrderTable.Kosu," +
                "OrderTable.OrderDate," +
                "OrderTable.OrderStatus, " +
                "CodeM.CdValue "+
                "FROM OrderTable " +
                "LEFT OUTER JOIN CodeM ON CodeM.KeyCd='003' and OrderTable.OrderStatus=CodeM.KeySubCd "+
                CreateSQLOrderWhere() +
                "ORDER BY OrderTable.OrderID";

        return sql;
    }

    /*
    OrderTable検索条件SQL作成
     */
    private String CreateSQLOrderWhere()
    {
        String sqlWhere = "";

        switch(loginUser.getUserType()) {
            case DefineCode.CodeM_UserType_Ippan:
                sqlWhere = "WHERE OrderTable.UserID=? ";
                break;
            case DefineCode.CodeM_UserType_KaiinTorokuTanto:
                break;
            case DefineCode.CodeM_UserType_SeihinTanto:
                break;
            case DefineCode.CodeM_UserType_UketukeTanto:
                sqlWhere = "WHERE OrderTable.OrderStatus='" + DefineCode.CodeM_OrderStatus_New + "' " + "OR OrderTable.OrderStatus='" + DefineCode.CodeM_OrderStatus_Kakutei + "' ";
                break;
            case DefineCode.CodeM_UserType_HachuTanto:
                sqlWhere = "WHERE OrderTable.OrderStatus='" + DefineCode.CodeM_OrderStatus_Kakutei + "' " + "OR OrderTable.OrderStatus='" + DefineCode.CodeM_OrderStatus_HasouZumi + "' ";
                break;
            case DefineCode.CodeM_UserType_SeikyuTanto:
                sqlWhere = "WHERE OrderTable.OrderStatus='" + DefineCode.CodeM_OrderStatus_HasouZumi + "' ";
                break;
            case DefineCode.CodeM_UserType_Admin:
                break;
        }

        return sqlWhere;
    }

    /*
        OrderTable検索条件作成
     */
    private String[] CreateSQLOrderArg()
    {
        String[] arg = null;

        switch(loginUser.getUserType()) {
            case DefineCode.CodeM_UserType_Ippan:
                arg = new String[]{loginUser.getUserID()};
                break;
            default:
        }

        return arg;
    }


    /*
    更新ボタン設定
     */
    private void SetUpdateButton()
    {
        Button btn = (Button) findViewById(R.id.OrderIchiranUpdateBtn);
        if(loginUser.getUserType().equals(DefineCode.CodeM_UserType_Ippan))
        {
            btn.setEnabled(false);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderIchiranAdapter myAdapter = (OrderIchiranAdapter)listView.getAdapter();
                ArrayList<OrderItemListViewValue> list = myAdapter.getOrderIchiranItemList();
                ArrayList<OrderItemListViewValue> sendList = GetCheckedOrderList(list);
                //注文選択チェック
                if(sendList.size()==0)
                {
                    return;
                }

                Intent dbIntent = new Intent(OrderIchiranActivity.this, OrderEditActivity.class);
                dbIntent.putExtra(DefineCode.orderItem_CheckItems, sendList);
                startActivity(dbIntent);
                finish();
            }
        });
    }


    /*
        削除ボタン設定
     */
    private void SetDeleteButton()
    {
        Button btn = (Button) findViewById(R.id.OrderIchiranDeleteBtn);

        if(loginUser.getUserType().equals(DefineCode.CodeM_UserType_Ippan) || loginUser.getUserType().equals(DefineCode.CodeM_UserType_Admin))
        {
            btn.setEnabled(true);
        }else{
            btn.setEnabled(false);
        }

        final AlertDialog.Builder kakuninBuilder = new AlertDialog.Builder(this);
        final MyOpenHelper helper = new MyOpenHelper(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderIchiranAdapter myAdapter = (OrderIchiranAdapter)listView.getAdapter();
                ArrayList<OrderItemListViewValue> list = myAdapter.getOrderIchiranItemList();
                //チェックした注文の取得
                final ArrayList<OrderItemListViewValue> sendList = GetCheckedOrderList(list);

                //注文選択チェック
                if(sendList.size()==0)
                {
                    return;
                }

                kakuninBuilder.setTitle(DefineCode.Msg_Delete_Kakunin);
                kakuninBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        SQLiteDatabase db = helper.getWritableDatabase();
                        SQLiteDatabase db_reader = helper.getReadableDatabase();

                        //DB保存
                        try {
                            db.beginTransaction();

                            int priceSum = 0;

                            for(int i = 0;i<=sendList.size()-1;i++)
                            {
                                //最新の在庫の取得
                                int nowZaiko = GetNewZaiko(db_reader,sendList.get(i));

                                //注文テーブルの削除
                                db.delete("OrderTable", "OrderID=?", new String[] { sendList.get(i).getOrderId().toString() });

                                ContentValues updateValues = new ContentValues();
                                updateValues.put("Zaiko", nowZaiko + (sendList.get(i).getKosu()));

                                //在庫テーブルの更新
                                long id1 = db.update("ZaikoTable", updateValues, "ItemCd=?", new String[] { sendList.get(i).getItemCd() });

                                priceSum += sendList.get(i).getPrice()*sendList.get(i).getKosu();

                            }

                            ContentValues updateValues = new ContentValues();
                            updateValues.put("Seikyu", loginUser.getSeikyu()-priceSum);
                            updateValues.put("Point", loginUser.getPoint() - (priceSum/100));

                            db.update("UserInfoTable",updateValues,"KaiinID=?",new String[]{loginUser.getKaiinID()});

                            loginUser.setSeikyu(loginUser.getSeikyu() - priceSum);
                            loginUser.setPoint(loginUser.getPoint() - (priceSum/100));

                            db.setTransactionSuccessful();

                            Toast.makeText( OrderIchiranActivity.this, DefineCode.Msg_Success_Delete, Toast.LENGTH_SHORT ).show();

                            Intent dbIntent = new Intent(OrderIchiranActivity.this, OrderIchiranActivity.class);
                            startActivity(dbIntent);
                            finish();

                        } catch (Exception e) {

                            Toast.makeText( OrderIchiranActivity.this, DefineCode.Msg_Error_Delete, Toast.LENGTH_SHORT ).show();

                        }finally {
                            db.endTransaction();
                            db_reader.close();
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

    /*
    選択した注文の取得
     */
    private ArrayList<OrderItemListViewValue> GetCheckedOrderList(ArrayList<OrderItemListViewValue> list)
    {
        ArrayList<OrderItemListViewValue> sendList = new ArrayList<OrderItemListViewValue>();

        for (int i = 0; i < list.size(); i++) {
            boolean checked = list.get(i).getIsChecked();
            String OrderStatus = list.get(i).getOrderStatus();
            if(checked)
            {
                sendList.add(list.get(i));
            }
        }

        //注文選択チェック
        if(sendList.size()==0)
        {
            Toast.makeText( OrderIchiranActivity.this, DefineCode.Msg_Error_OrderEdit_InputCheck_NotSelectedOrder, Toast.LENGTH_SHORT ).show();

        }
        return sendList;

    }

    private int GetNewZaiko(SQLiteDatabase db_reader,OrderItemListViewValue item)
    {
        int nowZaiko = 0;
        Cursor cursor = null;
        try
        {
            //最新の在庫の取得
            cursor = db_reader.rawQuery(CommonUtil.CreateSQLItemM(true),new String[]{item.getItemCd()});
            boolean mov = cursor.moveToFirst();
            while (mov) {
                nowZaiko = cursor.getInt(3);
                mov = cursor.moveToNext();
            }

        }catch (Exception e)
        {
            throw e;
        }
        finally {
            if(cursor != null)
            {
                cursor.close();
            }
        }

        return nowZaiko;
    }
    /*
    戻るボタン設定
     */
    private void SetBackOrderButton()
    {
        Button btn = (Button) findViewById(R.id.OrderIchiranBackBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(OrderIchiranActivity.this, MenuActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }
}
