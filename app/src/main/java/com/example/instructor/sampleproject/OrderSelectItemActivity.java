package com.example.instructor.sampleproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by instructor on 2016/08/04.
 */
public class OrderSelectItemActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderselectitem);
        //タイトル設定
        super.setBaseTitle(DefineCode.title_OrderSelect);


        listView = (ListView) findViewById(R.id.orderSelectItemListView);
        // フォーカスが当たらないよう設定
        listView.setItemsCanFocus(false);

        //表示項目の設定
        SetInitList(listView);

        //購入ボタン
        SetBuyButton();

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
            String sql = CreateSQLItemM();

            //sql実行
            cursor = dbRead.rawQuery(sql , new String[]{loginUser.getUserAddress1(),loginUser.getUserAddress1()});

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                OrderItemListViewValue itemValue = new OrderItemListViewValue();
                itemValue.setItemCd(cursor.getString(0));
                itemValue.setItemName(cursor.getString(1));
                String itemPresentFlg = cursor.getString(7);

                if(loginUser.isBirthdayFlg() && itemPresentFlg.equals("1"))
                {
                    itemValue.setPrice(0);
                    itemValue.setPresentFlg("1");

                }
                else
                {
                    itemValue.setPrice(cursor.getInt(2));
                    itemValue.setPresentFlg("0");
                }

                itemValue.setZaiko(cursor.getInt(3));
                itemValue.setItemAddress(cursor.getString(6));
                itemValue.setItemAddressName(cursor.getString(8));
                itemValue.setOsusume(cursor.getString(9));

                list.add(itemValue);
                mov = cursor.moveToNext();
            }

            // アダプターを設定
            OrderSelectItemAdapter myAdapter = new OrderSelectItemAdapter(this,android.R.layout.simple_list_item_multiple_choice,list);
            listView.setAdapter(myAdapter);


            //表示する注文がない場合
            if(list.size() == 0)
            {
                Toast.makeText( OrderSelectItemActivity.this, DefineCode.Msg_Error_OrderSelectItem_NotItem, Toast.LENGTH_SHORT ).show();
            }

        }catch (Exception e)
        {
            Toast.makeText( OrderSelectItemActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();
        }
    }

    /*
    商品検索SQL作成
     */
    private String CreateSQLItemM() {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" ItemCd,");
        sql.append(" ItemName,");
        sql.append(" Price,");
        sql.append(" Zaiko,");
        sql.append(" ItemUpdateDate,");
        sql.append(" ZaikoUpdateDate, ");
        sql.append(" ItemAddress,");
        sql.append(" PresentFlg, ");
        sql.append(" ItemAddressNm, ");
        sql.append(" SortNo ");
        sql.append("FROM( ");
        sql.append("    SELECT ");
        sql.append("     ItemM.ItemCd,");
        sql.append("     ItemM.ItemName,");
        sql.append("     ItemM.Price,");
        sql.append("     ZaikoTable.Zaiko,");
        sql.append("     ItemM.UpdateDate AS ItemUpdateDate,");
        sql.append("     ZaikoTable.UpdateDate AS ZaikoUpdateDate, ");
        sql.append("     ItemM.ItemAddress,");
        sql.append("     ItemM.PresentFlg, ");
        sql.append("     CodeM.CdValue AS ItemAddressNm,");
        sql.append("     1 AS SortNo");
        sql.append("    FROM ItemM ");
        sql.append("    LEFT OUTER JOIN ZaikoTable ON ItemM.ItemCd = ZaikoTable.ItemCd ");
        sql.append("    LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '" + DefineCode.CodeM_Todofuken + "' AND ItemM.ItemAddress = CodeM.KeySubCd ");
        sql.append("    WHERE ZaikoTable.Zaiko<>0 AND ItemM.ItemAddress=?");
        sql.append("   UNION ");
        sql.append("    SELECT ");
        sql.append("     ItemM.ItemCd,");
        sql.append("     ItemM.ItemName,");
        sql.append("     ItemM.Price,");
        sql.append("     ZaikoTable.Zaiko,");
        sql.append("     ItemM.UpdateDate,");
        sql.append("     ZaikoTable.UpdateDate, ");
        sql.append("     ItemM.ItemAddress,");
        sql.append("     ItemM.PresentFlg, ");
        sql.append("     CodeM.CdValue, ");
        sql.append("     2 AS SortNo");
        sql.append("    FROM ItemM ");
        sql.append("    LEFT OUTER JOIN ZaikoTable ON ItemM.ItemCd = ZaikoTable.ItemCd ");
        sql.append("    LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '" + DefineCode.CodeM_Todofuken + "' AND ItemM.ItemAddress = CodeM.KeySubCd ");
        sql.append("    WHERE ZaikoTable.Zaiko<>0 AND ItemM.ItemAddress!=?");
        sql.append(") ");

        if(loginUser.isBirthdayFlg())
        {
            sql.append("Order by  PresentFlg desc,SortNo,ItemCd");
        }
        else
        {
            sql.append("Order by  SortNo,ItemCd");
        }


        return sql.toString();
    }

    private void SetBuyButton()
    {
        final AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.OrderSelectItem_BuyBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrderSelectItemAdapter myAdapter = (OrderSelectItemAdapter)listView.getAdapter();
                ArrayList<OrderItemListViewValue> list = myAdapter.getOrderSelectItemList();
                ArrayList<OrderItemListViewValue> checkedItemList = GetCheckedOrderList(list);

                if(checkedItemList.size() == 0)
                {
                    //注文がない場合、ダイアログを表示
                    errorBuilder.setTitle(DefineCode.Msg_Error_Order_NotSelectItem);
                    errorBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });

                    errorBuilder.show();
                    return;
                }

                Intent dbIntent = new Intent(OrderSelectItemActivity.this, OrderActivity.class);
                dbIntent.putExtra(DefineCode.orderSelectItem_CheckItems, checkedItemList);
                startActivity(dbIntent);
                finish();
            }
        });
    }


    private void SetBackOrderButton()
    {
        Button btn = (Button) findViewById(R.id.OrderSelectItem_backBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(OrderSelectItemActivity.this, MenuActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

    /*
    選択した注文の取得
     */
    private ArrayList<OrderItemListViewValue> GetCheckedOrderList(ArrayList<OrderItemListViewValue> list)
    {
        ArrayList<OrderItemListViewValue> checkItemList = new ArrayList<OrderItemListViewValue>();
        for (int i = 0; i < list.size(); i++) {
            // チェックされているアイテムの key の取得
            String key = list.get(i).getItemCd();
            boolean checked = list.get(i).getIsChecked();
            if(checked)
            {
                checkItemList.add(list.get(i));
            }
        }

        return checkItemList;
    }
}
