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

/**
 * Created by instructor on 2016/08/05.
 */
public class CodeMIchiranActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeichiran);

        //タイトル設定
        super.setBaseTitle(DefineCode.title_CodeIchiran);

        ListView listView = (ListView) findViewById(R.id.cdListView);

        //表示項目の設定
        SetInitList(listView);


        // アイテムクリック時のイベントを設定 (テキスト 2 行表示)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                // 選択アイテムを取得
                ListView listView = (ListView)parent;
                CodeMValue item = (CodeMValue)listView.getItemAtPosition(pos);
                Intent dbIntent = new Intent(CodeMIchiranActivity.this, CodeMEditActivity.class);
                dbIntent.putExtra(DefineCode.codeMEdit_BtnKeyWord, DefineCode.codeMEdit_EditBtn);
                dbIntent.putExtra(DefineCode.codeMEdit_SelectedKeyCd, item.getKeyCd());
                dbIntent.putExtra(DefineCode.codeMEdit_SelectedKeySubCd, item.getKeySubCd());

                startActivity(dbIntent);
                finish();


            }
        });

        //新規登録ボタン
        SetAddItemButton();

        //戻るボタン
        SetBackItemButton();

    }

    private void SetInitList(ListView listView)
    {
        MyOpenHelper helper = new MyOpenHelper(this);
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;

        try
        {
            // select文
            String sql = CreateSQLCodeM();

            //sql実行
            cursor = dbRead.rawQuery(sql , null);

            ArrayList<CodeMValue> list = new ArrayList<>();
            CodeMAdapter myAdapter = new CodeMAdapter(CodeMIchiranActivity.this);

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                CodeMValue codeValue = new CodeMValue();
                codeValue.setKeyCd(cursor.getString(0));
                codeValue.setKeySubCd(cursor.getString(1));
                codeValue.setCdValue(cursor.getString(2));

                list.add(codeValue);
                mov = cursor.moveToNext();
            }

            // アダプターを設定します
            myAdapter.setCodeMList(list);
            listView.setAdapter(myAdapter);

        }catch (Exception e)
        {
            Toast.makeText( CodeMIchiranActivity.this, DefineCode.Msg_Error, Toast.LENGTH_SHORT ).show();
        }
        finally {
            //カーソル、DBをクローズ
            cursor.close();
            dbRead.close();
        }
    }

    /*
    コードマスタ検索SQL
     */

    private String CreateSQLCodeM() {
        String sql = "SELECT " +
                        "KeyCd," +
                        "KeySubCd," +
                        "CdValue " +
                    "FROM CodeM " +
                    "ORDER BY KeyCd,KeySubCd";

        return sql;
    }

    /*
    新規登録ボタン設定
     */
    private void SetAddItemButton()
    {
        Button btn = (Button) findViewById(R.id.addCdBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(CodeMIchiranActivity.this, CodeMEditActivity.class);
                dbIntent.putExtra(DefineCode.codeMEdit_BtnKeyWord, DefineCode.codeMEdit_AddBtn);
                dbIntent.putExtra(DefineCode.userMEdit_CallKeyWord,DefineCode.userMEdit_CallBackIchiran);
                startActivity(dbIntent);
                finish();
            }
        });
    }

   /*
    更新ボタン設定
    */

    private void SetBackItemButton()
    {
        Button btn = (Button) findViewById(R.id.backCdBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbIntent = new Intent(CodeMIchiranActivity.this, MenuActivity.class);
                startActivity(dbIntent);
                finish();
            }
        });
    }

}
