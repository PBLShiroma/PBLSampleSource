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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by instructor on 2016/08/04.
 */
public class ItemEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //レイアウト設定
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemedit);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_ItemEdit);

        //遷移元画面から値の受取
        Intent intent = getIntent();
        String keyword = intent.getStringExtra(DefineCode.itemEdit_BtnKeyword);

        MyOpenHelper helper = new MyOpenHelper(this);

        //都道府県のSpinnerの生成
        CreateTodofukenSpinner(helper);

        //編集の場合、初期値を取得
        if(keyword.equals(DefineCode.itemEdit_EditBtn))
        {
            String selectedItemId = intent.getStringExtra(DefineCode.itemEdit_SelectedItemId);
            SetInitValue(selectedItemId);
        }

        //更新ボタン
        SetItemEdit_EditBtn(keyword);

        //削除ボタン
        SetItemEdit_DeleteBtn();

        //キャンセルボタン
        SetItemEdit_CancelBtn();
    }

    /*
都道府県Spinner設定
*/
    private void CreateTodofukenSpinner(MyOpenHelper helper)
    {
        ArrayAdapter<TodofukenValue> adapter = new ArrayAdapter<TodofukenValue>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner todofukenSpinner = (Spinner) findViewById(R.id.TodofukenCd);

        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            // select文
            String sql    = CommonUtil.CreateSQLCodeMTodofuken();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                adapter.add(new TodofukenValue(cursor.getString(0), cursor.getString(1)));
                mov = cursor.moveToNext();
            }

            todofukenSpinner.setAdapter(adapter);

        }catch (Exception e)
        {
            Toast.makeText( ItemEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            if(cursor != null)
            {
                cursor.close();

            }
            dbRead.close();
        }
    }

    /*
    初期値設定
     */
    private void SetInitValue(String selectedItemId)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            EditText itemIdText = (EditText) findViewById(R.id.itemID);
            EditText itemNameText = (EditText) findViewById(R.id.itemName);
            EditText tankaText = (EditText) findViewById(R.id.tanka);
            EditText zaikoText = (EditText) findViewById(R.id.zaiko);
            CheckBox PresentFlg = (CheckBox) findViewById(R.id.PresentFlg);
            Spinner todofukenSpinner = (Spinner) findViewById(R.id.TodofukenCd);

            itemIdText.setFocusable(false);

            // select文
            String sql    = CommonUtil.CreateSQLItemM(true);

            //sql実行
            cursor = dbRead.rawQuery(sql , new String[]{selectedItemId});

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                itemIdText.setText(cursor.getString(0));
                itemNameText.setText(cursor.getString(1));
                tankaText.setText(cursor.getString(2));
                zaikoText.setText(cursor.getString(3));


                /* 都道府県の設定*/
                String todofuken = cursor.getString(6);
                ArrayAdapter<TodofukenValue> myAdapTodofuken = (ArrayAdapter<TodofukenValue>)todofukenSpinner.getAdapter();
                int index = 0;
                for (int i = 0; i < myAdapTodofuken.getCount(); i++) {
                    if (myAdapTodofuken.getItem(i).getTodofukenCd().equals(todofuken)) {
                        index = i; break;
                    }
                }
                todofukenSpinner.setSelection(index);

                //プレゼントフラグ設定
                String flg = cursor.getString(7);
                Log.d("ItemEditActiv","flg:"+flg);
                if(flg.equals("1"))
                {
                    PresentFlg.setChecked(true);
                }
                else
                {
                    PresentFlg.setChecked(false);
                }

                mov = cursor.moveToNext();
            }


        }catch (Exception e)
        {
            Log.e("ItemEditActivity","Error",e);
            Toast.makeText( ItemEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();

        }finally {
            //カーソル、DBをクローズ
            if(cursor != null)
            {
                cursor.close();
            }

            dbRead.close();
        }
    }

    /*
    更新ボタン設定
     */
    private void SetItemEdit_EditBtn(final String keyword)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        final SQLiteDatabase db = helper.getWritableDatabase();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.ItemEdit_EditBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力項目の取得
                OrderItemListViewValue inputData = getInputData();

                //システム日付の取得
                Date date = new Date();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy'/'MM'/'dd'/' hh':'mm':'ss");

                //入力チェック
                if(!InputCheck(inputData))
                {
                    builder.setMessage(DefineCode.Msg_Error_ItemEdit_NotInput)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return;
                }

                boolean editResult = false;
                if(keyword.equals(DefineCode.itemEdit_AddBtn))
                {
                    //追加処理
                    editResult = InputData(inputData,sdf1.format(date));
                }else
                {
                    //更新処理
                    editResult = updateData(inputData,sdf1.format(date));
                }

                if(editResult) {
                    builder.setMessage(DefineCode.Msg_Success_Edit)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 商品一覧へ戻る
                                    Intent dbIntent = new Intent(ItemEditActivity.this, ItemIchiranActivity.class);
                                    startActivity(dbIntent);
                                    finish();
                                }
                            });
                    builder.show();
                }else
                {
                    builder.setMessage(DefineCode.Msg_Error_Edit);
                }
            }

            /*
            登録処理
             */
            private boolean InputData(OrderItemListViewValue inputData,String sysDate)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //商品マスタの編集
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("ItemCd", inputData.getItemCd());
                    insertValues.put("ItemName", inputData.getItemName());
                    insertValues.put("Price", inputData.getPrice());
                    insertValues.put("ItemAddress",inputData.getItemAddress());
                    insertValues.put("PresentFlg",inputData.getPresentFlg());

                    insertValues.put("UpdateDate", sysDate);

                    long id = db.insert("ItemM", "ItemCd",insertValues);

                    //在庫マスタの編集
                    ContentValues insertValuesZaiko = new ContentValues();
                    insertValuesZaiko.put("ItemCd", inputData.getItemCd());
                    insertValuesZaiko.put("Zaiko", inputData.getZaiko());
                    insertValuesZaiko.put("UpdateDate", sysDate);

                    long id1 = db.insert("ZaikoTable", "ItemCd",insertValuesZaiko);

                    db.setTransactionSuccessful();
                    ret = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
                return ret;
            }

            /*
            更新処理
             */
            private boolean updateData(OrderItemListViewValue inputData,String sysDate)
            {
                boolean ret = false;
                try {
                    db.beginTransaction();

                    //商品マスタの編集
                    ContentValues updateValues = new ContentValues();
                    updateValues.put("ItemName", inputData.getItemName());
                    updateValues.put("Price", inputData.getPrice());
                    updateValues.put("ItemAddress",inputData.getItemAddress());
                    updateValues.put("PresentFlg",inputData.getPresentFlg());

                    updateValues.put("UpdateDate", sysDate);

                    long id = db.update("ItemM", updateValues, "ItemCd=?", new String[] { inputData.getItemCd() });


                    //在庫マスタの編集
                    ContentValues updateValuesZaiko = new ContentValues();
                    updateValuesZaiko.put("Zaiko", inputData.getZaiko());
                    updateValuesZaiko.put("UpdateDate", sysDate);

                    long id1 = db.update("ZaikoTable", updateValuesZaiko, "ItemCd=?", new String[] { inputData.getItemCd() });

                    db.setTransactionSuccessful();
                    ret = true;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }


                return ret;
            }
        });
    }

    /*
    削除ボタン設定
    */
    private void SetItemEdit_DeleteBtn()
    {
        final MyOpenHelper helper = new MyOpenHelper(this);

        final EditText itemIdText = (EditText) findViewById(R.id.itemID);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.ItemEdit_DelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase db;
            @Override
            public void onClick(View v) {

                try
                {
                    db = helper.getWritableDatabase();

                    boolean editResult = false;
                    editResult = deleteData(itemIdText.getText().toString());

                    if(editResult) {
                        builder.setMessage(DefineCode.Msg_Success_Delete)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 商品一覧画面へ戻る
                                        Intent dbIntent = new Intent(ItemEditActivity.this, ItemIchiranActivity.class);
                                        startActivity(dbIntent);
                                        finish();
                                    }
                                });
                        builder.show();
                    }else
                    {
                        builder.setMessage(DefineCode.Msg_Error_Delete)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.show();
                    }
                }catch (Exception e) {
                    builder.setMessage(DefineCode.Msg_Error_Delete)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
                finally {
                    db.close();
                }

            }

            /*
            削除処理
             */
            private boolean deleteData(String itemId)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //商品マスタの削除
                    long id = db.delete("ItemM","ItemCd=?", new String[] { itemId });

                    db.setTransactionSuccessful();
                    ret = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                }
                return ret;
            }
        });
    }

    /*
    キャンセルボタン設定
     */
    private void SetItemEdit_CancelBtn()
    {
        Button btn = (Button) findViewById(R.id.ItemEdit_CancelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //商品一覧へ戻る
                Intent dbIntent = new Intent(ItemEditActivity.this, ItemIchiranActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

    public OrderItemListViewValue getInputData()
    {
        OrderItemListViewValue value = new OrderItemListViewValue();

        //入力値の取得
        EditText itemCdText = (EditText) findViewById(R.id.itemID);
        EditText itemNameText = (EditText) findViewById(R.id.itemName);
        EditText tankaText = (EditText) findViewById(R.id.tanka);
        EditText zaikoText = (EditText) findViewById(R.id.zaiko);
        CheckBox presentFlgCheck = (CheckBox) findViewById(R.id.PresentFlg);
        Spinner TodofukenCdSpinner = (Spinner) findViewById(R.id.TodofukenCd);

        value.setItemCd(itemCdText.getText().toString());
        value.setItemName(itemNameText.getText().toString());
        if(!tankaText.getText().toString().isEmpty())
        {
            value.setPrice(Integer.parseInt(tankaText.getText().toString()));
        }
        if(!zaikoText.getText().toString().isEmpty()) {
            value.setZaiko(Integer.parseInt(zaikoText.getText().toString()));
        }

        //都道府県
        TodofukenValue todofukenValue = (TodofukenValue)TodofukenCdSpinner.getSelectedItem();
        value.setItemAddress(todofukenValue.getTodofukenCd());

        //プレゼントフラグ
        if(presentFlgCheck.isChecked())
        {
            value.setPresentFlg("1");
        }else
        {
            value.setPresentFlg("0");
        }

        return value;
    }

    /*
        必須チェック
    */
    public boolean InputCheck(OrderItemListViewValue inputData)
    {

        if(!CommonUtil.InputValueNullCheck(inputData.getItemCd())
                || !CommonUtil.InputValueNullCheck(inputData.getItemName())
                )
        {
            return false;
        }

        if(inputData.getPrice() == null
                || inputData.getZaiko() == null)
        {
            return false;
        }

        return true;
    }

}
