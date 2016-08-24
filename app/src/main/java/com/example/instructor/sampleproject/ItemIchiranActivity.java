package com.example.instructor.sampleproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by instructor on 2016/08/04.
 */
public class ItemIchiranActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemichiran);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_ItemIchiran);

        //表示項目の設定
        ListView listView = (ListView) findViewById(R.id.itemListView);
        SetInitList(listView);

        // 商品一覧のイベントを設定
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                // 選択アイテムを取得
                ListView listView = (ListView)parent;
                OrderItemListViewValue item = ( OrderItemListViewValue)listView.getItemAtPosition(pos);
                Intent dbIntent = new Intent(ItemIchiranActivity.this, ItemEditActivity.class);
                dbIntent.putExtra(DefineCode.itemEdit_BtnKeyword, DefineCode.itemEdit_EditBtn);
                dbIntent.putExtra(DefineCode.itemEdit_SelectedItemId, item.getItemCd());
                startActivity(dbIntent);
                finish();
            }
        });

        //新規登録ボタン
        SetAddItemButton();

        //戻るボタン
        SetBackItemButton();

    }

    /*
    表示項目の設定
     */
    private void SetInitList(ListView listView)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            ArrayList<OrderItemListViewValue> list = new ArrayList<OrderItemListViewValue>();

            // select文
            String sql = CommonUtil.CreateSQLItemM(false);

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                OrderItemListViewValue itemValue = new OrderItemListViewValue();
                itemValue.setItemCd(cursor.getString(0));
                itemValue.setItemName(cursor.getString(1));
                itemValue.setPrice(cursor.getInt(2));
                itemValue.setZaiko(cursor.getInt(3));
                itemValue.setItemAddress(cursor.getString(6));
                itemValue.setPresentFlg(cursor.getString(7));
                itemValue.setItemAddressName(cursor.getString(8));
                list.add(itemValue);
                mov = cursor.moveToNext();
            }

            //ListViewの設定
            ItemIchiranAdapter myAdapter = new ItemIchiranAdapter(this,android.R.layout.simple_list_item_multiple_choice,list);
            listView.setAdapter(myAdapter);

        }catch (Exception e)
        {
            Toast.makeText( ItemIchiranActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();
        }
    }

    /*
    新規作成ボタン設定
     */
    private void SetAddItemButton()
    {
        Button btn = (Button) findViewById(R.id.addItemBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(ItemIchiranActivity.this, ItemEditActivity.class);
                dbIntent.putExtra(DefineCode.itemEdit_BtnKeyword, DefineCode.itemEdit_AddBtn);
                startActivity(dbIntent);
                finish();
            }
        });
    }


    /*
    戻るボタン設定
     */

    private void SetBackItemButton()
    {
        Button btn = (Button) findViewById(R.id.backItemBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(ItemIchiranActivity.this, MenuActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

}
