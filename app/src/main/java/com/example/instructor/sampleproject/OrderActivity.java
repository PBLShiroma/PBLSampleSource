package com.example.instructor.sampleproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

;

/**
 * Created by instructor on 2016/08/04.
 */
public class OrderActivity extends BaseActivity {

    AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        //タイトル設定
        super.setBaseTitle(DefineCode.title_Order);

        ListView listView = (ListView) findViewById(R.id.orderItemListView);

        //遷移元画面から値の受取
        Intent intent = getIntent();
        ArrayList<OrderItemListViewValue> selectItemList = (ArrayList<OrderItemListViewValue>)intent.getSerializableExtra(DefineCode.orderSelectItem_CheckItems);

        // アダプターを設定
        OrderAdapter myAdapter = new OrderAdapter(this,android.R.layout.simple_list_item_multiple_choice,selectItemList);
        listView.setAdapter(myAdapter);

        //注文ボタン
        SetOrderButton(listView);

        //戻るボタン
        SetBackOrderButton();

        //アイテムクリック時のイベントを設定
        SetListViewListener(listView,myAdapter);

    }

    /*
    商品選択時のイベントを設定
     */
    private void SetListViewListener(ListView listView,final OrderAdapter myAdapter)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                ListView listView = (ListView)parent;
                final OrderItemListViewValue item = (OrderItemListViewValue)listView.getItemAtPosition(pos);
                builder.setTitle(DefineCode.Msg_Error_Order_NotInputKosu);
                // LayoutInflaterにより動的にレイアウト生成
                final View viewDialog = inflater.inflate(R.layout.activity_ordereditkosudialog, null);

                builder.setView(viewDialog);
                builder.setPositiveButton("OK",null);
                builder.setNegativeButton("キャンセル",null);

                ad = builder.show();
                Button buttonOK = ad.getButton( DialogInterface.BUTTON_POSITIVE );
                Button buttonCancel = ad.getButton( DialogInterface.BUTTON_NEGATIVE );

                buttonOK.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        EditText et = (EditText) viewDialog.findViewById(R.id.OrderEditKosu_Kosu);

                        if( et.getText().toString().equals( "" ) )
                        { // エディットボックスが空の場合
                            Toast.makeText( OrderActivity.this, DefineCode.Msg_Error_Order_NotInputKosu, Toast.LENGTH_SHORT ).show();
                        }
                        else if(Integer.valueOf(et.getText().toString()) > item.getZaiko())
                        {
                            // エディットボックスの値が在庫より大きいの場合
                            Toast.makeText( OrderActivity.this, DefineCode.Msg_Error_Order_NotZaiko, Toast.LENGTH_SHORT ).show();
                        }
                        else
                        {
                            ad.dismiss();
                            ad = null;
                            // エディットボックスが空でなければ、ダイアログを閉じる。
                            item.setKosu(Integer.valueOf(et.getText().toString()));
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });

                buttonCancel.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        ad.dismiss();
                        ad = null;
                    }
                });
            }
        });


    }

    private void SetOrderButton(final ListView listView)
    {
        final AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
        final AlertDialog.Builder kakuninBuilder = new AlertDialog.Builder(this);
        final MyOpenHelper helper = new MyOpenHelper(this);
        final ListView kakuninListView = new ListView(this);

        Button btn = (Button) findViewById(R.id.Order_OrderBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderAdapter myAdapter = (OrderAdapter)listView.getAdapter();
                ArrayList<OrderItemListViewValue> list = myAdapter.getOrderItemList();
                //注文個数が０以外の商品Listを取得
                final ArrayList<OrderItemListViewValue> saveList = getOrderItemList(list);

                //注文個数が０以外の商品がない場合、ダイアログを表示
                if(saveList.size() == 0)
                {
                    ErrorMsgShow(errorBuilder);
                    return;
                }

                //合計データの作成
                OrderItemListViewValue goukeiValue = getGoukeiLine(saveList);
                saveList.add(goukeiValue);

                //請求額チェック
                int goukeiKingaku = goukeiValue.getPrice();
                if(goukeiKingaku + loginUser.getSeikyu() > getSeikyuMax(helper))
                {
                    ErrorMsgShowSeikyuGakuOrver(errorBuilder);
                    return;
                }

                //注文がある場合、確認ダイアログを表示
                ViewGroup viewGroup = (ViewGroup) kakuninListView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(kakuninListView);
                }

                OrderKakuninListViewAdapter kakuninAdapter = new OrderKakuninListViewAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, saveList,getLayoutInflater());
                kakuninListView.setAdapter(kakuninAdapter);

                //確認ダイアログ表示
                KakuninDialogShow(kakuninBuilder,kakuninListView,helper,saveList);

            }

        });
    }

    /*
コードM検索（請求額Max取得）
*/
    private Integer getSeikyuMax(MyOpenHelper helper)
    {
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;
        String seikyuMax = "0";
        try
        {
            // select文
            String sql    = CreateSQLCodeMSeikyuMax();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            //値の取得
            boolean mov = cursor.moveToFirst();

            while (mov) {
                seikyuMax = cursor.getString(1);
                mov = cursor.moveToNext();
            }

        }catch (Exception e)
        {
            Toast.makeText( OrderActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            if(cursor != null)
            {
                cursor.close();

            }
            dbRead.close();
        }

        return Integer.valueOf(seikyuMax);
    }

    private String CreateSQLCodeMSeikyuMax()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT KeySubCd,");
        sql.append("       CdValue ");
        sql.append("FROM  CodeM ");
        sql.append("WHERE KeyCd='" + DefineCode.CodeM_SeikyuMax_Key + "' AND " + "KeySubCd='" + DefineCode.CodeM_SeikyuMax_keySub + "' ");

        return sql.toString();

    }

    /*
    エラーメッセージ表示
     */
    private void ErrorMsgShow(AlertDialog.Builder errorBuilder)
    {
        errorBuilder.setTitle(DefineCode.Msg_Error_Order_NotInputKosu);
        errorBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        errorBuilder.show();
    }

    /*
    エラーメッセージ表示
     */
    private void ErrorMsgShowSeikyuGakuOrver(AlertDialog.Builder errorBuilder)
    {
        errorBuilder.setTitle(DefineCode.Msg_Error_Order_Seikyugaku_Over);
        errorBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        errorBuilder.show();
    }

    /*
    確認ダイアログ表示
     */
    private void KakuninDialogShow(AlertDialog.Builder kakuninBuilder,ListView kakuninListView,final MyOpenHelper helper,final ArrayList<OrderItemListViewValue> saveList)
    {
        //確認ダイアログ
        kakuninBuilder.setView(kakuninListView).create();
        kakuninBuilder.setTitle("注文します、よろしいですか？");
        kakuninBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final SQLiteDatabase db = helper.getWritableDatabase();
                final SQLiteDatabase db_reader = helper.getReadableDatabase();


                try {
                    db.beginTransaction();

                    //注文データの追加
                    CreateOrderData(saveList,db_reader,db);

                    db.setTransactionSuccessful();

                    Toast.makeText( OrderActivity.this, DefineCode.Msg_Success_Order, Toast.LENGTH_SHORT ).show();

                    Log.d("OrderActivity","Order OK");

                    Intent dbIntent = new Intent(OrderActivity.this, OrderSelectItemActivity.class);
                    startActivity(dbIntent);
                    finish();



                } catch (Exception e) {
                    Log.d("OrderActivity","Order Error");
                    Log.e("OrderActivity","Order Error",e);
                    Toast.makeText( OrderActivity.this, DefineCode.Msg_Error_Order, Toast.LENGTH_SHORT ).show();

                }finally {
                    Log.d("OrderActivity","Order finally");
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

    /*
    注文個数が０以外の商品Listを取得
     */
    private ArrayList<OrderItemListViewValue> getOrderItemList(ArrayList<OrderItemListViewValue> list)
    {
        ArrayList<OrderItemListViewValue> saveList = new ArrayList<OrderItemListViewValue>();
        for (int i = 0; i < list.size(); i++) {
            Integer kosuValue = list.get(i).getKosu();
            //個数が０以外の商品を取得
            if(kosuValue!=null && kosuValue != 0)
            {
                saveList.add(list.get(i));
            }
        }

        return saveList;
    }

    /*
    合計データの作成
     */
    private OrderItemListViewValue getGoukeiLine(ArrayList<OrderItemListViewValue> saveList)
    {
        //合計の計算
        int sum_Price = 0;
        int sum_kosu = 0;
        for(int i = 0;i<=saveList.size()-1 ; i++)
        {
            sum_Price += saveList.get(i).getPrice() * saveList.get(i).getKosu();
            sum_kosu  += saveList.get(i).getKosu();
        }
        OrderItemListViewValue goukeiValue = new OrderItemListViewValue();
        goukeiValue.setItemName(DefineCode.OrderKakuninSumTitle);
        goukeiValue.setPrice(sum_Price);
        goukeiValue.setKosu(sum_kosu);

        return goukeiValue;
    }

    /*
    注文データの登録
     */
    private void CreateOrderData(ArrayList<OrderItemListViewValue> saveList,SQLiteDatabase db_reader,SQLiteDatabase db)
    {
        Date sysDate = new Date();
        int priceSum = 0;

        try
        {
            for(int i = 0;i<saveList.size()-1;i++) {
                //在庫の取得
                int newZaiko = getZaiko(db_reader,saveList.get(i).getItemCd());

                //注文テーブルの追加
                long id = db.insert("OrderTable", "OrderCd", createInsertData(saveList.get(i),sysDate));

                //在庫の編集
                ContentValues updateValues = new ContentValues();
                updateValues.put("Zaiko", newZaiko - (saveList.get(i).getKosu()));

                long id1 = db.update("ZaikoTable", updateValues, "ItemCd=?", new String[]{saveList.get(i).getItemCd()});

                priceSum += saveList.get(i).getPrice()*saveList.get(i).getKosu();

            }

            //請求額、Pointの編集
            ContentValues updateValues = new ContentValues();
            updateValues.put("Seikyu", loginUser.getSeikyu()+priceSum);
            updateValues.put("Point", loginUser.getPoint() + (priceSum/100));

            db.update("UserInfoTable",updateValues,"KaiinID=?",new String[]{loginUser.getKaiinID()});

            loginUser.setSeikyu(loginUser.getSeikyu()+priceSum);
            loginUser.setPoint(loginUser.getPoint() + (priceSum/100));



        }
        catch(Exception e) {
            Log.e("OrderActivity","Order Error CreateOrderData",e);
            throw e;
        }
    }

    /*
    在庫数量の取得
     */
    private int getZaiko(SQLiteDatabase db_reader,String ItemCd)
    {
        int newZaiko = 0;
        Cursor cursor = null;

        try
        {
            cursor = db_reader.rawQuery(CommonUtil.CreateSQLItemM(true), new String[]{ItemCd});
            boolean mov = cursor.moveToFirst();
            while (mov) {
                newZaiko = cursor.getInt(3);
                mov = cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }

        return newZaiko;
    }

    /*
    注文テーブル保存データの生成
     */
    private ContentValues createInsertData(OrderItemListViewValue saveData,Date sysDate)
    {
        ContentValues insertValues = new ContentValues();
        insertValues.put("KaiinID", loginUser.getKaiinID());
        insertValues.put("UserID", loginUser.getUserID());
        insertValues.put("Name", loginUser.getUserName());
        insertValues.put("UserAddress1", loginUser.getUserAddress1());
        insertValues.put("UserAddress2", loginUser.getUserAddress2());
        insertValues.put("ItemCd", saveData.getItemCd());
        insertValues.put("ItemName", saveData.getItemName());
        insertValues.put("Price", saveData.getPrice());
        insertValues.put("Kosu", saveData.getKosu());
        insertValues.put("OrderStatus", DefineCode.CodeM_OrderStatus_New);
        insertValues.put("OrderDate", CommonUtil.formatDate(sysDate,DefineCode.DataFormat_yyyyMMdd));
        insertValues.put("UpdateDate", CommonUtil.formatDate(sysDate,DefineCode.DataFormat_yyyyMMddhhmmss));

        return insertValues;

    }

    /*
    戻るボタン設定
     */
    private void SetBackOrderButton()
    {
        Button btn = (Button) findViewById(R.id.Order_BackBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(OrderActivity.this, OrderSelectItemActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

}
