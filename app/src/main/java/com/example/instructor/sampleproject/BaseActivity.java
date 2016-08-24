package com.example.instructor.sampleproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.KeyEvent;

/**
 * Created by instructor on 2016/08/09.
 */
public class BaseActivity extends AppCompatActivity {

    LoginUser loginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginUser = (LoginUser)this.getApplication();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Disable Back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("アプリを終了してもいいですか？");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // ボタンをクリックしたときの動作
                            finish();
                        }
                });
            builder.setNegativeButton("NG", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // ボタンをクリックしたときの動作
                        }
                });
            builder.show();

        }

        return super.onKeyDown(keyCode, event);
    }

    protected void setBaseTitle(String title)
    {
        TextView textTitle = (TextView) findViewById(R.id.BaseTitle);
        textTitle.setText(title);

        if("".equals(loginUser.getKaiinID()))
        {
            TextView loginUserName = (TextView) findViewById(R.id.BaseUserName);
            loginUserName.setText("");
        }
        else
        {
            TextView loginUserName = (TextView) findViewById(R.id.BaseUserName);
            loginUserName.setText(loginUser.getUserID() + ":" + loginUser.getUserName()+ ":" + loginUser.getPoint() + "pt");

        }
    }
}
