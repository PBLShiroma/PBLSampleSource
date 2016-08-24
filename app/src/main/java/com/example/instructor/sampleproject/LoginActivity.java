package com.example.instructor.sampleproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //グローバル変数の初期化
        loginUser.globalsAllInit();

        //タイトル設定
        super.setBaseTitle(DefineCode.title_Login);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final MyOpenHelper helper = new MyOpenHelper(this);

        //ログイン処理
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力チェック
                EditText userIdText = (EditText) findViewById(R.id.userIdTxt);
                EditText pwText = (EditText) findViewById(R.id.pwTxt);

                if (!CommonUtil.InputValueNullCheck(userIdText.getText().toString()) || !CommonUtil.InputValueNullCheck(pwText.getText().toString())) {
                    builder.setMessage(DefineCode.Msg_Error_Menu_NotInput)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return;
                }

                //ユーザ検索処理
                SelectUser(userIdText.getText().toString(),pwText.getText().toString());


                 //ログインチェック
                 if (loginUser.getKaiinID().equals("")) {
                     builder.setMessage(DefineCode.Msg_Error_Menu_InvalidUser)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    }
                             });
                      builder.show();
                  }
                 else
                 {

                     //誕生日設定
                     if(loginUser.isBirthdayFlg())
                     {
                         Toast.makeText( LoginActivity.this, DefineCode.Msg_Happy_Birthday, Toast.LENGTH_SHORT ).show();
                     }

                     //メニュー画面へ
                     Intent dbIntent = new Intent(LoginActivity.this, MenuActivity.class);
                     startActivity(dbIntent);

                     //画面を閉じる
                     finish();
                  }
            }
        });

        //キャンセル処理
        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //画面を閉じる
                finish();

            }
        });

        //新規登録処理
        Button addUserBtn = (Button) findViewById(R.id.addUserBtn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dbIntent = new Intent(LoginActivity.this, UserEditActivity.class);
                dbIntent.putExtra(DefineCode.userMEdit_BtnKeyWord, DefineCode.userMEdit_AddBtn);
                dbIntent.putExtra(DefineCode.userMEdit_CallKeyWord,DefineCode.userMEdit_CallBackLoginDisp);
                startActivity(dbIntent);
                //画面を閉じる
                finish();

            }
        });
    }

    /*
    ユーザ検索処理
     */
    private void SelectUser(String userId,String pwText)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        try
        {
            LoginUser loginUser_new = CommonUtil.SelectLoginUser(helper,userId,pwText,loginUser);
            loginUser = loginUser_new;

        }catch(Exception e)
        {
            Toast.makeText( LoginActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
    }


}
