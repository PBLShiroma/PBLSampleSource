package com.example.instructor.sampleproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by instructor on 2016/08/03.
 */
public class MenuActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_Menu);

        //Menuボタン一覧の設定
        SetBtnEnabled();

        //会員編集ボタン処理
        SetUserEditButton();

        //商品編集ボタン処理
        SetItemEditButton();

        //コード編集ボタン処理
        SetCodeIchiranButton();

        //会員一覧編集ボタン処理
        SetUserIchiranButton();

        //注文画面ボタン処理
        SetOrderButton();

        //注文一覧画面ボタン処理
        SetOrderIchiranBtn();

        //ログアウトボタン処理
        SetLogoutBtn();

    }

    /*
    Menuボタン一覧の設定
     */
    private void SetBtnEnabled()
    {
        //ログインユーザ情報からメニュー情報を取得
        String[] element = loginUser.getMenuIchiran().split(":");

        //ボタンを押下不可
        Button btn_UserEdit = (Button) findViewById(R.id.UserEditBtn);
        Button btn_ItemEdit = (Button) findViewById(R.id.ItemEditBtn);
        Button btn_CodeIchiran = (Button) findViewById(R.id.CodeIchiranBtn);
        Button btn_UserIchiran = (Button) findViewById(R.id.UserIchiranBtn);
        Button btn_Order = (Button) findViewById(R.id.OrderBtn);
        Button btn_OrderIchiran = (Button) findViewById(R.id.OrderIchiranBtn);
        Button btn_Logout = (Button) findViewById(R.id.LogoutBtn);

        btn_UserEdit.setEnabled(false);
        btn_ItemEdit.setEnabled(false);
        btn_CodeIchiran.setEnabled(false);
        btn_UserIchiran.setEnabled(false);
        btn_Order.setEnabled(false);
        btn_OrderIchiran.setEnabled(false);
        btn_Logout.setEnabled(false);

        //ボタンの押下設定
        for(int i=0;i<=element.length-1;i++)
        {
            switch(element[i]) {
                case DefineCode.CodeM_MenuType_UserEdit:
                    btn_UserEdit.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_ItemEdit:
                    btn_ItemEdit.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_CodeEdit:
                    btn_CodeIchiran.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_UserIchiran:
                    btn_UserIchiran.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_Order:
                    btn_Order.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_OrderIchiran:
                    btn_OrderIchiran.setEnabled(true);
                    break;
                case DefineCode.CodeM_MenuType_Logout:
                    btn_Logout.setEnabled(true);
                    break;
            }
        }
    }

    /*
    会員一覧ボタン設定
     */
    private void SetUserIchiranButton()
    {
        Button btn = (Button) findViewById(R.id.UserIchiranBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(MenuActivity.this, UserIchiranActivity.class);
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }

    //商品編集ボタン処理
    private void SetItemEditButton()
    {
        Button btn = (Button) findViewById(R.id.ItemEditBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(MenuActivity.this, ItemIchiranActivity.class);
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }

    //コード編集ボタン処理
    private void SetCodeIchiranButton()
    {
        Button btn = (Button) findViewById(R.id.CodeIchiranBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(MenuActivity.this, CodeMIchiranActivity.class);
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }

    /*
    会員編集ボタン設定
     */
    private void SetUserEditButton()
    {
        Button btn = (Button) findViewById(R.id.UserEditBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent dbIntent = new Intent(MenuActivity.this, UserEditActivity.class);
                dbIntent.putExtra(DefineCode.userMEdit_BtnKeyWord, DefineCode.userMEdit_EditBtn);
                dbIntent.putExtra(DefineCode.userMEdit_CallKeyWord,DefineCode.userMEdit_CallBackMenu);
                dbIntent.putExtra(DefineCode.userMEdit_SelectedKaiinId, loginUser.getKaiinID());
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }


    //注文画面ボタン処理
    private void SetOrderButton()
    {
        Button btn = (Button) findViewById(R.id.OrderBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(MenuActivity.this, OrderSelectItemActivity.class);
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }

    //注文一覧画面ボタン処理
    private void SetOrderIchiranBtn()
    {
        Button btn = (Button) findViewById(R.id.OrderIchiranBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(MenuActivity.this, OrderIchiranActivity.class);
                startActivity(dbIntent);

                //画面を閉じる
                finish();

            }
        });
    }

    //ログアウトボタン処理
    private void SetLogoutBtn()
    {
        Button btn = (Button) findViewById(R.id.LogoutBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser.globalsAllInit();
                Intent dbIntent = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(dbIntent);
                //画面を閉じる
                finish();

            }
        });
    }


}
