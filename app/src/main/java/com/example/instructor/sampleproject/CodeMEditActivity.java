package com.example.instructor.sampleproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by instructor on 2016/08/05.
 */
public class CodeMEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //レイアウト設定
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeedit);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_CodeEdit);

        //遷移元画面から値の受取
        Intent intent = getIntent();
        String keyword = intent.getStringExtra(DefineCode.codeMEdit_BtnKeyWord);

        //編集の場合、初期値を取得
        if(keyword.equals(DefineCode.codeMEdit_EditBtn))
        {
            String selectedKeyCd = intent.getStringExtra(DefineCode.codeMEdit_SelectedKeyCd);
            String selectedKeySubCd = intent.getStringExtra(DefineCode.codeMEdit_SelectedKeySubCd);

            SetInitValue(selectedKeyCd,selectedKeySubCd);
        }
        //更新ボタン
        SetCodeEdit_EditBtn(keyword);

        //削除ボタン
        SetCodeEdit_DeleteBtn();

        //キャンセルボタン
        SetCodeEdit_CancelBtn();
    }

    /*
    初期値設定
     */
    private void SetInitValue(String selectedKeyCd,String selectedKeySubCd)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor =null;

        EditText keyCdText = (EditText) findViewById(R.id.KeyCd);
        EditText keySubText = (EditText) findViewById(R.id.KeySubCd);
        EditText valueText = (EditText) findViewById(R.id.Value);

        try
        {
            //キーコードは編集不可
            keyCdText.setFocusable(false);
            keySubText.setFocusable(false);

            // select文
            String sql    = CreateSQLCodeM();

            //sql実行
            cursor = dbRead.rawQuery(sql , new String[]{selectedKeyCd,selectedKeySubCd});

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                keyCdText.setText(cursor.getString(0));
                keySubText.setText(cursor.getString(1));
                valueText.setText(cursor.getString(2));
                mov = cursor.moveToNext();
            }

        }catch (Exception e)
        {
            Toast.makeText( CodeMEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();

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
    コードマスタ検索用SQL作成
     */

    private String CreateSQLCodeM()
    {
        // select文
        StringBuilder sql =new StringBuilder();
        sql.append("SELECT ");
        sql.append("      KeyCd,");
        sql.append("      KeySubCd,");
        sql.append("      CdValue ");
        sql.append("FROM CodeM ");
        sql.append("WHERE KeyCd = ? AND KeySubCd = ?");

        return sql.toString();
    }

    /*
    更新ボタン設定
     */
    private void SetCodeEdit_EditBtn(final String keyword)
    {
        final MyOpenHelper helper = new MyOpenHelper(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.CodeEdit_EditBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = helper.getWritableDatabase();

                //入力項目の取得
                CodeMValue inputData = getInputData();

                //入力チェック
                if(!InputCheck(inputData))
                {
                    builder.setMessage(DefineCode.Msg_Error_CodeMEdit_NotInput)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return;
                }

                boolean editResult = false;
                if(keyword.equals(DefineCode.codeMEdit_AddBtn))
                {
                    //追加処理
                    editResult = InputData(db,inputData);
                }else
                {
                    //更新処理
                    editResult = updateData(db,inputData);
                }

                if(editResult) {
                    builder.setMessage(DefineCode.Msg_Success_Edit)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // コード一覧へ戻る
                                    Intent dbIntent = new Intent(CodeMEditActivity.this, CodeMIchiranActivity.class);
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
            private boolean InputData(SQLiteDatabase db,CodeMValue inputData)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //コードマスタの編集
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("KeyCd", inputData.getKeyCd());
                    insertValues.put("KeySubCd", inputData.getKeySubCd());
                    insertValues.put("CdValue", inputData.getCdValue());

                    long id = db.insert("CodeM", "KeyCd",insertValues);

                    db.setTransactionSuccessful();
                    ret = true;

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    db.endTransaction();
                    db.close();
                }
                return ret;
            }

            /*
            更新処理
             */
            private boolean updateData(SQLiteDatabase db,CodeMValue inputData)
            {
                boolean ret = false;
                try {
                    db.beginTransaction();

                    //コードマスタの編集
                    ContentValues updateValues = new ContentValues();
                    updateValues.put("CdValue", inputData.getCdValue());

                    long id = db.update("CodeM", updateValues, "KeyCd=? AND KeySubCd=?", new String[] { inputData.getKeyCd(),inputData.getKeySubCd() });

                    db.setTransactionSuccessful();
                    ret = true;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                    db.close();
                }
                return ret;
            }

        });
    }

    /*
    削除ボタン設定
    */
    private void SetCodeEdit_DeleteBtn()
    {
        final MyOpenHelper helper = new MyOpenHelper(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.CodeEdit_DelBtn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SQLiteDatabase db = helper.getWritableDatabase();

                try
                {
                    //入力項目の取得
                    CodeMValue inputData = getInputData();

                    //入力チェック
                    if(!InputCheck(inputData))
                    {
                        builder.setMessage(DefineCode.Msg_Error_CodeMEdit_NotInput)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.show();
                        return;
                    }

                    boolean editResult = false;
                    editResult = deleteData(db,inputData.getKeyCd(),inputData.getKeySubCd());

                    if(editResult) {
                        builder.setMessage(DefineCode.Msg_Success_Delete)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // コード一覧画面へ戻る
                                        Intent dbIntent = new Intent(CodeMEditActivity.this, CodeMIchiranActivity.class);
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
            private boolean deleteData(SQLiteDatabase db,String keyCd,String keySubCd)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //コードマスタの削除
                    long id = db.delete("CodeM", "KeyCd=? and KeySubCd=?",new String[] { keyCd,keySubCd });

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
    private void SetCodeEdit_CancelBtn()
    {
        Button btn = (Button) findViewById(R.id.CodeEdit_CancelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //コード一覧へ戻る
                Intent dbIntent = new Intent(CodeMEditActivity.this, CodeMIchiranActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

    //入力項目の取得
    private CodeMValue getInputData()
    {
        CodeMValue retValue = new CodeMValue();

        //入力項目の取得
        EditText keyCdText = (EditText) findViewById(R.id.KeyCd);
        EditText keySubText = (EditText) findViewById(R.id.KeySubCd);
        EditText valueText = (EditText) findViewById(R.id.Value);

        //入力値の取得
        retValue.setKeyCd(keyCdText.getText().toString());
        retValue.setKeySubCd(keySubText.getText().toString());
        retValue.setCdValue(valueText.getText().toString());

        return retValue;
    }

    /*
    必須チェック
    */
    public boolean InputCheck(CodeMValue inputData)
    {
        if(!CommonUtil.InputValueNullCheck(inputData.getKeyCd())
                || !CommonUtil.InputValueNullCheck(inputData.getKeySubCd())
                || !CommonUtil.InputValueNullCheck(inputData.getCdValue())
                )
        {
            return false;
        }

        return true;
    }


}
