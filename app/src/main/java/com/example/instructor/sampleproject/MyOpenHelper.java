package com.example.instructor.sampleproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by instructor on 2016/07/28.
 */
public class MyOpenHelper extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        super(context, "SampleProjectDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //マスタテーブル作成
        db.execSQL(CreateTableSQLItemM());

        //ユーザマスタテーブル作成
        db.execSQL(CreateTableSQLUserM());

        //会員詳細テーブル作成
        db.execSQL(CreateTableSQLUserInfoTable());

        //adminユーザ作成
        CreateAdminUser(db);

        //注文テーブル作成
        db.execSQL(CreateTableSQLOrder());

        //在庫テーブル作成
        db.execSQL(CreateTableSQLZaiko());

        //コードマスタ作成
        db.execSQL(CreateTableSQLCodeM());


        //コードマスタ値作成
        ArrayList<CodeMValue> codeMstList = createCodeMList();
        for(int i=0;i<=codeMstList.size()-1;i++)
        {
            CodeMValue codeM = codeMstList.get(i);
            db.execSQL(InsertSQLCodeM(codeM));
        }

        //商品マスタ作成

        for(int s=1;s<=15;s++)
        {
            db.execSQL(CreateItemSQL(s));
            db.execSQL(CreateZaikoSQL(s));
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*
    　　製品マスタテーブル作成SQL
     */
    private String CreateTableSQLItemM()
    {
        String sql = "create table ItemM("
                      + " ItemCd text(6) PRIMARY KEY,"
                      + " ItemName text(20),"
                      + " Price integer(4),"
                      + " ItemAddress text(3),"
                      + " PresentFlg text(1),"
                      + " UpdateDate text(19)"
                      + ");";
        return sql;
    }

    /*
    　　会員マスタテーブル作成SQL
     */

    private String CreateTableSQLUserM()
    {
        String sql = "create table UserM("
                + " KaiinID integer PRIMARY KEY AUTOINCREMENT,"
                + " UserID text(6),"
                + " Name text(20),"
                + " UserAddress1 text(3),"
                + " UserAddress2 text(100),"
                + " Password text(8),"
                + " Birthday text(8),"
                + " UserType text(3),"
                + " UpdateDate text(19)"
                + ");";
        return sql;
    }

    /*
    　　管理者ユーザ作成SQL
     */

    private void CreateAdminUser(SQLiteDatabase db)
    {
        db.execSQL(CreateAdminUserSQL());

        Cursor cursor = db.rawQuery(SelectAdminUserSQL(),null);
        boolean mov = cursor.moveToFirst();
        while (mov) {
            db.execSQL(CreatetAdminUserInfoSQL(String.valueOf(cursor.getInt(0))));
            mov = cursor.moveToNext();
        }
        cursor.close();

    }

    private String CreateAdminUserSQL()
    {
        String sql = "INSERT INTO  UserM("
                + " UserID,"
                + " Name,"
                + " UserAddress1,"
                + " UserAddress2,"
                + " Password,"
                + " Birthday,"
                + " UserType,"
                + " UpdateDate) "
                + " VALUES( "
                + " 'admin', "
                + " '管理者', "
                + " '001', "
                + " 'aaaa', "
                + " 'admin', "
                + " '20160101', "
                + " '006', "
                + " '2000/01/01 00:00:00' "
                + " ); ";

        return sql;
    }

    private String SelectAdminUserSQL()
    {
        String sql = "Select * from UserM"
                + " Where UserID='admin'";

        return sql;
    }

    private String CreatetAdminUserInfoSQL(String kaiinID)
    {
        String sql = "INSERT INTO  UserInfoTable("
                + " KaiinID,"
                + " Seikyu,"
                + " Point) "
                + " VALUES( "
                + kaiinID + ", "
                + " 0, "
                + " 0 "
                + " ); ";

        return sql;
    }

    private String CreateItemSQL(Integer i)
    {

        String Todofuken = "";
        String PresentFlg = "";

        if(i<5)
        {
            Todofuken="001";
            PresentFlg="0";
        }
        else if(i<10)
        {
            Todofuken="002";
            PresentFlg="1";
        }
        else if(i<=15)
        {
            Todofuken="003";
            PresentFlg="0";
        }

        String sql = "INSERT INTO  ItemM("
                + " ItemCd,"
                + " ItemName,"
                + " Price,"
                + " ItemAddress,"
                + " PresentFlg"
                +") "
                + " VALUES( "
                + " 'item"+String.format("%1$02d", i) +"', "
                + " '商品"+String.format("%1$03d", i) +"',  "
                + " ' "+i.toString() +"00',"
                + " '" + Todofuken + "',"
                + " '" + PresentFlg +"'"
                + " ); ";

        return sql;
    }

    private String CreateZaikoSQL(Integer i)
    {

        String sql = "INSERT INTO  ZaikoTable("
                + " ItemCd,"
                + " Zaiko) "
                + " VALUES( "
                + " 'item"+String.format("%1$02d", i) +"', "
                + " ' "+i.toString() +"00'"
                + " ); ";

        return sql;
    }

    /*
      注文テーブル作成SQL
     */
    private String CreateTableSQLOrder()
    {
        String sql = "create table OrderTable("
                + " OrderID integer PRIMARY KEY AUTOINCREMENT,"
                + " KaiinID integer,"
                + " UserID text(6),"
                + " Name text(20),"
                + " UserAddress1 text(3),"
                + " UserAddress2 text(100),"
                + " ItemCd text(6),"
                + " ItemName text(20),"
                + " Price integer(4),"
                + " Kosu integer(4),"
                + " OrderStatus text(1),"
                + " OrderDate text(10),"
                + " UpdateDate text(19)"
                + ");";
        return sql;
    }

    /*
    　　在庫テーブル
     */
    private String CreateTableSQLZaiko()
    {
        String sql = "create table ZaikoTable("
                + " ItemCd text(6) PRIMARY KEY,"
                + " Zaiko integer(4),"
                + " UpdateDate text(19)"
                + ");";
        return sql;
    }

    /*
    　　コードマスタテーブル
     */
    private String CreateTableSQLCodeM()
    {
        String sql = "create table CodeM("
                + " KeyCd text(3),"
                + " KeySubCd text(3),"
                + " CdValue text(20),"
                + " primary key(KeyCd,KeySubCd)"
                + ");";
        return sql;
    }

    /*
　　会員詳細テーブル
 */
    private String CreateTableSQLUserInfoTable()
    {
        String sql = "create table UserInfoTable("
                + " KaiinID integer PRIMARY KEY,"
                + " Seikyu integer(5),"
                + " Point integer(5)"
                + ");";
        return sql;
    }

    /*
    　　コードマスタデータ登録用SQL
     */
    private String InsertSQLCodeM(CodeMValue codeM)
    {
        String sql = "INSERT INTO CodeM("
                + " KeyCd,"
                + " KeySubCd,"
                + " CdValue) "
                + " VALUES(\""
                + codeM.getKeyCd() +"\",\""
                + codeM.getKeySubCd() +"\",\""
                + codeM.getCdValue() +"\""
                + ");";
        return sql;
    }

    /*
    コードマスタの値作成
     */
    private ArrayList<CodeMValue> createCodeMList()
    {
        ArrayList<CodeMValue> ret = new ArrayList<CodeMValue>();
        ret.add(CreateCodeMValue("001","001","一般"));
        ret.add(CreateCodeMValue("001","002","製品担当"));
        ret.add(CreateCodeMValue("001","003","会員登録担当"));
        ret.add(CreateCodeMValue("001","004","受付担当"));
        ret.add(CreateCodeMValue("001","005","発注担当"));
        ret.add(CreateCodeMValue("001","006","管理者"));
        ret.add(CreateCodeMValue("001","007","請求担当"));
        ret.add(CreateCodeMValue("002","001","01:05:06:07"));
        ret.add(CreateCodeMValue("002","002","01:02:07"));
        ret.add(CreateCodeMValue("002","003","01:04:07"));
        ret.add(CreateCodeMValue("002","004","01:06:07"));
        ret.add(CreateCodeMValue("002","005","01:06:07"));
        ret.add(CreateCodeMValue("002","006","01:02:03:04:05:06:07"));
        ret.add(CreateCodeMValue("002","007","01:06:07"));
        ret.add(CreateCodeMValue("003","1","注文新規"));
        ret.add(CreateCodeMValue("003","2","注文確定"));
        ret.add(CreateCodeMValue("003","3","注文在庫なし"));
        ret.add(CreateCodeMValue("003","4","注文発送済"));
        ret.add(CreateCodeMValue("003","5","注文請求"));
        ret.add(CreateCodeMValue("004","001","北海道"));
        ret.add(CreateCodeMValue("004","002","東京"));
        ret.add(CreateCodeMValue("004","003","大阪"));
        ret.add(CreateCodeMValue("004","004","福岡"));
        ret.add(CreateCodeMValue("004","005","沖縄"));
        ret.add(CreateCodeMValue("005","001","10000"));

        return ret;

    }

    /*
        コードマスタの値設定
         */
    private CodeMValue CreateCodeMValue(String keyCd,String keySubCd,String cdValue)
    {
        CodeMValue value = new CodeMValue();
        value.setKeyCd(keyCd);
        value.setKeySubCd(keySubCd);
        value.setCdValue(cdValue);
        return value;
    }

}
