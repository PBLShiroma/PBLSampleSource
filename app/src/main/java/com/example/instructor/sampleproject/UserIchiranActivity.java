package com.example.instructor.sampleproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
public class UserIchiranActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userichiran);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_UserIchiran);

        ListView listView = (ListView) findViewById(R.id.userListView);

        //表示項目の設定
        SetInitList(listView);

        // アイテムクリック時のイベントを設定 (テキスト 2 行表示)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                // 選択アイテムを取得
                ListView listView = (ListView)parent;
                HashMap<String, String> item = ( HashMap<String, String>)listView.getItemAtPosition(pos);
                String selectData = item.get("main");
                String[] element = selectData.split(":", 0);
                Intent dbIntent = new Intent(UserIchiranActivity.this, UserEditActivity.class);
                dbIntent.putExtra(DefineCode.userMEdit_BtnKeyWord, DefineCode.userMEdit_EditBtn);
                dbIntent.putExtra(DefineCode.userMEdit_CallKeyWord,DefineCode.userMEdit_CallBackIchiran);
                dbIntent.putExtra(DefineCode.userMEdit_SelectedKaiinId, element[0]);
                startActivity(dbIntent);
                finish();
            }
        });

        //新規登録ボタン
        SetAddUserMButton();

        //戻るボタン
        SetBackUserMButton();

    }

    private String CreateSQLUserM() {
        String sql    = "SELECT " +
                            "UserM.KaiinID,"+
                            "UserM.UserID," +
                            "UserM.Name," +
                            "UserM.UserAddress1," +
                            "UserM.UserAddress2," +
                            "UserM.Password," +
                            "UserM.UserType,"+
                            "CodeM.CdValue "+
                        "FROM UserM " +
                        "LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '001' AND UserM.UserType = CodeM.KeySubCd "+
                        "LEFT OUTER JOIN UserInfoTable ON UserM.KaiinID = UserInfoTable.KaiinID "+
                        "ORDER BY UserM.KaiinID";

        return sql;
    }

    /*
    新規登録ボタン
     */
    private void SetAddUserMButton()
    {
        Button btn = (Button) findViewById(R.id.addUserBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(UserIchiranActivity.this, UserEditActivity.class);
                dbIntent.putExtra(DefineCode.userMEdit_BtnKeyWord, DefineCode.userMEdit_AddBtn);
                dbIntent.putExtra(DefineCode.userMEdit_CallKeyWord,DefineCode.userMEdit_CallBackIchiran);
                startActivity(dbIntent);
                finish();
            }
        });
    }

    /*
    戻るボタン
    */
    private void SetBackUserMButton()
    {
        Button btn = (Button) findViewById(R.id.backUserBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(UserIchiranActivity.this, MenuActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

    /*
    表示項目の設定
     */
    private void SetInitList(ListView listView)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();


        try
        {
            // select文
            String sql = CreateSQLUserM();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("main", String.format("%d:%s",cursor.getInt(0), cursor.getString(1)));
                map.put("sub", String.format("%s:%s",cursor.getString(2),cursor.getString(7)));
                list.add(map);
                mov = cursor.moveToNext();
            }
            SimpleAdapter adapter = new SimpleAdapter(
                    this,
                    list,
                    android.R.layout.simple_list_item_2,
                    new String[] {"main", "sub"},
                    new int[] {android.R.id.text1, android.R.id.text2}
            );
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();

            // アダプターを設定します
            listView.setAdapter(adapter);

        }catch (Exception e)
        {
            Toast.makeText( UserIchiranActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();
        }
    }
}
