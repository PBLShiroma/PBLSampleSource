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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by instructor on 2016/08/04.
 */
public class UserEditActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useredit);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_UserEdit);

        //遷移元画面から値の受取
        Intent intent = getIntent();
        String btnKeyWord = intent.getStringExtra(DefineCode.userMEdit_BtnKeyWord);
        String callbackKeyword = intent.getStringExtra(DefineCode.userMEdit_CallKeyWord);

        MyOpenHelper helper = new MyOpenHelper(this);

        //UserTypeのSpinnerの生成
        CreateUserTypeSpinner(helper,callbackKeyword);

        //都道府県のSpinnerの生成
        CreateTodofukenSpinner(helper);

        //編集の場合、初期値を取得
        if(btnKeyWord.equals(DefineCode.userMEdit_EditBtn))
        {
            //選択した会員IDの取得
            String selectedKaiinId = intent.getStringExtra(DefineCode.userMEdit_SelectedKaiinId);
            //値の設定
            SetInitValue(helper,selectedKaiinId,callbackKeyword);
        }

        //ボタン押下不可設定
        SeEnable(btnKeyWord,callbackKeyword);

        //更新ボタン
        SetUserEdit_EditBtn(btnKeyWord,callbackKeyword);

        //削除ボタン
        SetUserEdit_DeleteBtn(callbackKeyword);

        //キャンセルボタン
        SetUserEdit_CancelBtn(callbackKeyword);
    }

    /*
        初期値設定
    */
    private void SetInitValue(MyOpenHelper helper,String selectedKaiinId,String callbackKeyword)
    {
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            TextView kaiinIdText = (TextView) findViewById(R.id.kaiinID);
            EditText userIdText = (EditText) findViewById(R.id.userID);
            EditText userNameText = (EditText) findViewById(R.id.userName);
            EditText userAddressText = (EditText) findViewById(R.id.userAddress);
            Spinner userTypeSpinner = (Spinner) findViewById(R.id.userType);
            Spinner todofukenSpinner = (Spinner) findViewById(R.id.TodofukenCd);
            EditText birthdayText = (EditText) findViewById(R.id.Birthday);

            TextView seikyuText = (TextView) findViewById(R.id.Seikyu);
            TextView pointText = (TextView) findViewById(R.id.Point);

            //会員IDの設定
            kaiinIdText.setText(selectedKaiinId);

            // select文
            String sql    = CreateSQLUserM();

            //sql実行
            cursor = dbRead.rawQuery(sql , new String[]{selectedKaiinId});

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                userIdText.setText(cursor.getString(1));
                userNameText.setText(cursor.getString(2));

                /* 都道府県の設定*/
                String todofukenType = cursor.getString(3);
                ArrayAdapter<TodofukenValue> myAdapTodofuken = (ArrayAdapter<TodofukenValue>)todofukenSpinner.getAdapter();
                int index = 0;
                for (int i = 0; i < myAdapTodofuken.getCount(); i++) {
                    if (myAdapTodofuken.getItem(i).getTodofukenCd().equals(todofukenType)) {
                        index = i; break;
                    }
                }
                todofukenSpinner.setSelection(index);

                //住所の設定
                userAddressText.setText(cursor.getString(4));

                //誕生日
                birthdayText.setText(cursor.getString(5));

                /* UserTypeの設定*/
                String userType = cursor.getString(6);
                ArrayAdapter<UserTypeValue> myAdap = (ArrayAdapter<UserTypeValue>)userTypeSpinner.getAdapter();
                index = 0;
                for (int i = 0; i < myAdap.getCount(); i++) {
                    if (myAdap.getItem(i).getUserTypeCd().equals(userType)) {
                        index = i; break;
                    }
                }
                userTypeSpinner.setSelection(index);

                //請求額の設定
                seikyuText.setText(String.valueOf(cursor.getInt(8)));

                //Pointの設定
                pointText.setText(String.valueOf(cursor.getInt(9)));

                mov = cursor.moveToNext();
            }


        }catch (Exception e)
        {
            Toast.makeText( UserEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();

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
    画面項目押下不可設定
     */
    private void SeEnable(String btnKeyWord,String callbackKeyword )
    {
        Button userDelButon = (Button) findViewById(R.id.UserEdit_DelBtn);
        Spinner userTypeSpinner = (Spinner) findViewById(R.id.userType);

        if(btnKeyWord.equals(DefineCode.userMEdit_EditBtn))
        {
            //ユーザタイプの押下不可設定
            if(callbackKeyword.equals(DefineCode.userMEdit_CallBackIchiran))
            {
                //会員一覧からの場合のみ変更可能
                userTypeSpinner.setEnabled(true);
                userDelButon.setEnabled(true);

            } else if(callbackKeyword.equals(DefineCode.userMEdit_CallBackMenu)){

                //遷移元がメニューかつ、ログインユーザタイプが一般以外の場合は変更可能
                if(!loginUser.getUserType().equals(DefineCode.CodeM_UserType_Ippan))
                {
                    userTypeSpinner.setEnabled(true);
                }
                userDelButon.setEnabled(true);
            }
            else
            {
                //上記以外は変更不可
                userTypeSpinner.setEnabled(false);
                userDelButon.setEnabled(false);
            }
        }
        else
        {
            userDelButon.setEnabled(false);
        }
    }

    /*
    ユーザタイプSpinner設定
     */
    private void CreateUserTypeSpinner(MyOpenHelper helper,String callbackKeyword)
    {
        ArrayAdapter<UserTypeValue> adapter = new ArrayAdapter<UserTypeValue>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner userTypeSpinner = (Spinner) findViewById(R.id.userType);

        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            // select文
            String sql    = CreateSQLCodeMUserType();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            //値の取得
            boolean mov = cursor.moveToFirst();
            while (mov) {
                adapter.add(new UserTypeValue(cursor.getString(0), cursor.getString(1)));
                mov = cursor.moveToNext();
            }

            userTypeSpinner.setAdapter(adapter);

            if(callbackKeyword.equals(DefineCode.userMEdit_CallBackIchiran))
            {
                userTypeSpinner.setEnabled(true);
            }
            else
            {
                userTypeSpinner.setEnabled(false);
            }

        }catch (Exception e)
        {
            Toast.makeText( UserEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
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
            Toast.makeText( UserEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
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
    コードマスタ検索SQL（ユーザタイプSpinner）
     */
    private String CreateSQLCodeMUserType()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT KeySubCd,");
        sql.append("       CdValue ");
        sql.append("FROM  CodeM ");
        sql.append("WHERE KeyCd='" + DefineCode.CodeM_UserType + "' ");
        sql.append("ORDER BY KeyCd,KeySubCd ");

        return sql.toString();

    }


    /*
    会員マスタ検索用SQL作成
     */
    private String CreateSQLUserM()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT UserM.KaiinID,");
        sql.append("       UserM.UserId, ");
        sql.append("       UserM.Name, ");
        sql.append("       UserM.UserAddress1, ");
        sql.append("       UserM.UserAddress2, ");
        sql.append("       UserM.Birthday, ");
        sql.append("       UserM.UserType, ");
        sql.append("       CodeM.CdValue, ");
        sql.append("       UserInfoTable.Seikyu, ");
        sql.append("       UserInfoTable.Point ");
        sql.append("FROM  UserM ");
        sql.append("LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '"+DefineCode.CodeM_UserType+"' AND UserM.UserType = CodeM.KeySubCd ");
        sql.append("LEFT OUTER JOIN UserInfoTable ON UserM.KaiinID = UserInfoTable.KaiinID ");
        sql.append("WHERE UserM.KaiinID=? ");

        return sql.toString();
    }

    /*
    会員マスタ検索用SQL作成
    */
    private String CreateSQLInputCheckUserId()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT KaiinID,");
        sql.append("       UserId, ");
        sql.append("       Name, ");
        sql.append("       UserAddress1, ");
        sql.append("       UserType ");
        sql.append("FROM  UserM ");
        sql.append("WHERE UserId = ? ");

        return sql.toString();
    }

    /*
    更新ボタン設定
     */
    private void SetUserEdit_EditBtn(final String keyword,final String callBack)
    {
        final MyOpenHelper helper = new MyOpenHelper(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.UserEdit_EditBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase dbReader;
            SQLiteDatabase db;
            @Override
            public void onClick(View v) {

                final TextView kaiinIdText = (TextView) findViewById(R.id.kaiinID);
                final EditText userIdText = (EditText) findViewById(R.id.userID);
                final EditText userNameText = (EditText) findViewById(R.id.userName);
                final EditText userAddressText = (EditText) findViewById(R.id.userAddress);
                final EditText userPasswordText = (EditText) findViewById(R.id.userPassword);
                final EditText userPasswordKakuninText = (EditText) findViewById(R.id.userPasswordKakunin);
                final Spinner userTypeSpinner = (Spinner) findViewById(R.id.userType);
                final Spinner todofukenSpinner = (Spinner) findViewById(R.id.TodofukenCd);
                final EditText birthdayText = (EditText) findViewById(R.id.Birthday);

                try
                {
                    dbReader = helper.getReadableDatabase();
                    db = helper.getWritableDatabase();

                    //入力値の取得
                    String kaiinId = kaiinIdText.getText().toString();
                    final String userId = userIdText.getText().toString();
                    final String userName = userNameText.getText().toString();
                    final String userAddress = userAddressText.getText().toString();
                    final String passWord = userPasswordText.getText().toString();
                    final String passWordKakunin = userPasswordKakuninText.getText().toString();
                    final UserTypeValue userTypeValue = (UserTypeValue)userTypeSpinner.getSelectedItem();
                    final TodofukenValue todofukenValue = (TodofukenValue)todofukenSpinner.getSelectedItem();
                    final String birthday = birthdayText.getText().toString();

                    if(!InputCheck(kaiinId,userId,userName,passWord,passWordKakunin,birthday))
                    {
                        return;
                    }

                    boolean editResult = false;
                    if(keyword.equals(DefineCode.userMEdit_AddBtn))
                    {
                        //追加処理
                        editResult = InputData(userId,userName,userAddress,passWord,userTypeValue,todofukenValue,birthday );
                    }else
                    {
                        //更新処理
                        editResult = updateData(kaiinId,userId,userName,userAddress,passWord,userTypeValue,todofukenValue,birthday);
                    }

                    if(editResult) {
                        builder.setMessage(DefineCode.Msg_Success_Edit)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(callBack == null){
                                            //戻り先不明のため何もしない
                                        }else switch(callBack){
                                            case DefineCode.userMEdit_CallBackLoginDisp:
                                                // ログイン画面へ戻る
                                                Intent dbIntent = new Intent(UserEditActivity.this, LoginActivity.class);
                                                startActivity(dbIntent);
                                                finish();
                                                break;

                                            case DefineCode.userMEdit_CallBackMenu :

                                                try
                                                {
                                                    LoginUser loginUser_new = CommonUtil.SelectLoginUser(helper,userId,passWord,loginUser);
                                                    loginUser = loginUser_new;

                                                }catch(Exception e)
                                                {
                                                    Toast.makeText( UserEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
                                                }

                                                // メニュー画面へ戻る
                                                Intent dbIntent1 = new Intent(UserEditActivity.this, MenuActivity.class);
                                                startActivity(dbIntent1);
                                                finish();
                                                break;

                                            case DefineCode.userMEdit_CallBackIchiran :
                                                // 会員一覧画面へ戻る
                                                Intent dbIntent2 = new Intent(UserEditActivity.this, UserIchiranActivity.class);
                                                startActivity(dbIntent2);
                                                finish();
                                                break;

                                            default:
                                                break;
                                        }
                                    }
                                });
                        builder.show();
                    }else
                    {
                        builder.setMessage(DefineCode.Msg_Error_Edit)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                        builder.show();
                    }

                }catch (Exception e) {
                    builder.setMessage(DefineCode.Msg_Error_Edit)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }finally {
                    dbReader.close();
                    db.close();
                }
            }

            /*
            入力チェック
             */
            private boolean InputCheck(String kaiinId,String userId,String userName,String passWord,String passWordKakunin,String birthday)
            {
                boolean ret = true;
                Cursor cursor = null;

                try
                {
                    StringBuilder errMsg = new StringBuilder();
                    //必須チェック
                    if(!CommonUtil.InputValueNullCheck(userId)
                            || !CommonUtil.InputValueNullCheck(userName)
                            || !CommonUtil.InputValueNullCheck(passWord)
                            || !CommonUtil.InputValueNullCheck(passWordKakunin)
                            )
                    {
                        errMsg.append(DefineCode.Msg_Error_UserEdit_InputCheck_NotInput + "\n");
                    }
                    //会員ID重複チェック
                    // select文
                    String sql = CreateSQLInputCheckUserId();

                    //sql実行
                    cursor = dbReader.rawQuery(sql, new String[]{userId});
                    // 表示用リストの作成
                    boolean mov = cursor.moveToFirst();
                    while (mov) {
                        if ("".equals(kaiinId)) {
                            errMsg.append(DefineCode.Msg_Error_UserEdit_InputCheck_UsedKaiinID + "\n");
                            break;

                        } else {
                            if (!String.valueOf(cursor.getInt(0)).equals(kaiinId)) {
                                errMsg.append(DefineCode.Msg_Error_UserEdit_InputCheck_UsedKaiinID + "\n");
                                break;
                            }
                        }
                        mov = cursor.moveToNext();
                    }

                    cursor.close();

                    //誕生日チェック
                    if(birthday != null && !"".equals(birthday))
                    {
                        try
                        {
                            SimpleDateFormat format = new SimpleDateFormat(DefineCode.DataFormat_yyyyMMdd2);
                            format.setLenient(false);
                            format.parse(birthday);
                        }
                        catch(Exception e)
                        {
                            errMsg.append(DefineCode.Msg_Error_UserEdit_InputCheck_InvalidBirthday + "\n");
                        }
                    }

                    //パスワード不正チェック
                    if(!passWord.equals(passWordKakunin))
                    {
                        errMsg.append(DefineCode.Msg_Error_UserEdit_InputCheck_InvalidPass + "\n");
                    }

                    if(!errMsg.toString().isEmpty())
                    {
                        builder.setMessage(errMsg.toString())
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.show();
                        ret = false;
                    }


                }catch (Exception e)
                {
                    Toast.makeText( UserEditActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
                }finally {

                    if(cursor != null)
                    {
                        cursor.close();
                    }
                }

                return ret;

            }

            /*
            登録処理
             */
            private boolean InputData(String userId,String userName,String userAddress,String passWord,UserTypeValue userTypeValue,
                                      TodofukenValue todofukenValue,String birthday)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //会員マスタの編集
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("UserId", userId);
                    insertValues.put("Name", userName);
                    insertValues.put("UserAddress1", todofukenValue.getTodofukenCd());
                    insertValues.put("UserAddress2", userAddress);
                    insertValues.put("Birthday", birthday);
                    insertValues.put("Password", passWord);
                    insertValues.put("UserType", userTypeValue.getUserTypeCd());

                    long id = db.insert("UserM", "KaiinId",insertValues);

                    //会員IDの取得
                    Cursor cursor = db.rawQuery("Select KaiinID from UserM where UserID=?",new String[]{userId} );
                    Integer kaiinID = 0;
                    boolean mov = cursor.moveToFirst();
                    while (mov) {
                        kaiinID = cursor.getInt(0);
                        mov = cursor.moveToNext();
                    }
                    cursor.close();

                    //会員詳細テーブルの編集
                    ContentValues insertValues1 = new ContentValues();
                    insertValues.put("KaiinID", kaiinID);
                    insertValues.put("Seikyu", 0);
                    insertValues.put("Point", 0);

                    long id1 = db.insert("UserInfoTable", "KaiinID",insertValues1);

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
            private boolean updateData(String kaiinId,String userId,String userName,String userAddress,
                                       String passWord,UserTypeValue userTypeValue,TodofukenValue todofukenValue,String birthday)
            {
                boolean ret = false;
                try {
                    db.beginTransaction();

                    //会員マスタの編集
                    ContentValues updateValues = new ContentValues();
                    updateValues.put("UserId", userId);
                    updateValues.put("Name", userName);
                    updateValues.put("UserAddress1", todofukenValue.getTodofukenCd());
                    updateValues.put("UserAddress2", userAddress);
                    updateValues.put("Birthday", birthday);
                    updateValues.put("Password", passWord);
                    updateValues.put("UserType", userTypeValue.getUserTypeCd());

                    long id = db.update("UserM", updateValues, "KaiinId=?", new String[] { kaiinId });

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
    private void SetUserEdit_DeleteBtn(final String callBack)
    {
       final MyOpenHelper helper = new MyOpenHelper(this);


        final TextView kaiinIdText = (TextView) findViewById(R.id.kaiinID);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btn = (Button) findViewById(R.id.UserEdit_DelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase db;
            @Override
            public void onClick(View v) {

                try
                {
                    db = helper.getWritableDatabase();

                    boolean editResult = false;
                    editResult = deleteData(kaiinIdText.getText().toString());

                    if(editResult) {
                        builder.setMessage(DefineCode.Msg_Success_Delete)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(callBack == null){

                                        }else switch(callBack){
                                            case DefineCode.userMEdit_CallBackMenu :
                                                // ログイン画面へ戻る
                                                Intent dbIntent1 = new Intent(UserEditActivity.this, LoginActivity.class);
                                                startActivity(dbIntent1);
                                                finish();
                                                break;

                                            case DefineCode.userMEdit_CallBackIchiran :
                                                // 会員一覧画面へ戻る
                                                Intent dbIntent2 = new Intent(UserEditActivity.this, UserIchiranActivity.class);
                                                startActivity(dbIntent2);
                                                finish();
                                                break;

                                            default:
                                                break;
                                        }
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
            private boolean deleteData(String kaiinId)
            {
                boolean ret = false;
                try
                {
                    db.beginTransaction();
                    //会員マスタの削除
                    long id = db.delete("UserM", "KaiinID=?",new String[] { kaiinId });

                    //会員詳細テーブルの削除
                    long id1 = db.delete("UserInfoTable", "KaiinID=?",new String[] { kaiinId });

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
    private void SetUserEdit_CancelBtn(final String callBack)
    {
        Button btn = (Button) findViewById(R.id.UserEdit_CancelBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBack == null){

                }else switch(callBack){
                    case DefineCode.userMEdit_CallBackLoginDisp:
                        // ログイン画面へ戻る
                        Intent dbIntent = new Intent(UserEditActivity.this, LoginActivity.class);
                        startActivity(dbIntent);
                        finish();
                        break;

                    case DefineCode.userMEdit_CallBackMenu :
                        // メニュー画面へ戻る
                        Intent dbIntent1 = new Intent(UserEditActivity.this, MenuActivity.class);
                        startActivity(dbIntent1);
                        finish();
                        break;

                    case DefineCode.userMEdit_CallBackIchiran :
                        // 会員一覧画面へ戻る
                        Intent dbIntent2 = new Intent(UserEditActivity.this, UserIchiranActivity.class);
                        startActivity(dbIntent2);
                        finish();
                        break;

                    default:
                        break;
                }
            }
        });
    }
}

