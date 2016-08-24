package com.example.instructor.sampleproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by instructor on 2016/08/08.
 */
public class CommonUtil {

    /*
      フォーマット指定日付取得
     */
    static public String formatDate(Date baseDate, String dateFormat)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(baseDate);
    }

    /*
    入力チェック
     */
    static public boolean InputValueNullCheck(String value)
    {
        boolean ret = true;

        if(value != null && value.length() > 0)
        {
            ret = true;
        }else
        {
            ret = false;
        }

        return ret;
    }
    /*
    商品検索用SQL
     */
    static public String CreateSQLItemM(boolean condFlg) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" ItemM.ItemCd,");
        sql.append(" ItemM.ItemName,");
        sql.append(" ItemM.Price,");
        sql.append(" ZaikoTable.Zaiko,");
        sql.append(" ItemM.UpdateDate,");
        sql.append(" ZaikoTable.UpdateDate, ");
        sql.append(" ItemM.ItemAddress,");
        sql.append(" ItemM.PresentFlg, ");
        sql.append(" CodeM.CdValue ");
        sql.append("FROM ItemM ");
        sql.append("LEFT OUTER JOIN ZaikoTable ON ItemM.ItemCd = ZaikoTable.ItemCd ");
        sql.append("LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '" + DefineCode.CodeM_Todofuken + "' AND ItemM.ItemAddress = CodeM.KeySubCd ");

        if(condFlg)
        {
            sql.append("WHERE ItemM.ItemCd=? ");
        }

        sql.append("ORDER BY ItemM.ItemCd");

        return sql.toString();
    }

    /*
コードマスタ検索SQL（都道府県Spinner）
 */
    static public String CreateSQLCodeMTodofuken()
    {
        // select文
        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT KeySubCd,");
        sql.append("       CdValue ");
        sql.append("FROM  CodeM ");
        sql.append("WHERE KeyCd='" + DefineCode.CodeM_Todofuken + "' ");
        sql.append("ORDER BY KeyCd,KeySubCd ");

        return sql.toString();

    }

    /*
    金額フォーマット
     */
    static public String KingakuFormat(Integer value)
    {
        String ret = "";

        if(value != null)
        {
                ret = String.format(DefineCode.Kingaku_Format,value) + "円";
        }

        return ret;
    }

    static public LoginUser SelectLoginUser(MyOpenHelper helper,String userId,String pwText,LoginUser loginUser)
    {
        SQLiteDatabase dbRead = helper.getReadableDatabase();
        Cursor cursor = null;
        try
        {
            // select文
            String sql = CreateSQLUserM();

            //sql実行
            cursor = dbRead.rawQuery(sql, new String[]{userId, pwText});

            // 表示用リストの作成
            boolean mov = cursor.moveToFirst();
            while (mov) {
                loginUser.setKaiinID(cursor.getString(0));
                loginUser.setUserID(cursor.getString(1));
                loginUser.setUserName(cursor.getString(2));
                loginUser.setUserAddress1(cursor.getString(3));
                loginUser.setUserAddress2(cursor.getString(4));
                loginUser.setBirthday(cursor.getString(5));
                loginUser.setUserType(cursor.getString(6));
                loginUser.setUserTypeName(cursor.getString(7));
                loginUser.setMenuIchiran(cursor.getString(8));
                loginUser.setSeikyu(cursor.getInt(9));
                loginUser.setPoint(cursor.getInt(10));

                mov = cursor.moveToNext();
            }

            String nowDate = CommonUtil.formatDate(new Date(),DefineCode.DataFormat_yyyyMMdd2);
            if(nowDate.substring(4,8).equals(loginUser.getBirthday().substring(4,8)))
            {
                loginUser.setBirthdayFlg(true);
            }else
            {
                loginUser.setBirthdayFlg(false);
            }

        }finally {

            if(cursor != null)
            {
                cursor.close();
            }
            dbRead.close();
        }

        return loginUser;
    }
    /*
    ログイン用SQL作成
 */
    static private String CreateSQLUserM() {

        StringBuilder sql    = new StringBuilder();
        sql.append("SELECT UserM.KaiinId,");
        sql.append("       UserM.UserId, ");
        sql.append("       UserM.Name, ");
        sql.append("       UserM.UserAddress1, ");
        sql.append("       UserM.UserAddress2, ");
        sql.append("       UserM.Birthday, ");
        sql.append("       UserM.UserType, ");
        sql.append("       CodeM.CdValue, ");
        sql.append("       CodeM1.CdValue, ");
        sql.append("       UserInfoTable.Seikyu, ");
        sql.append("       UserInfoTable.Point ");
        sql.append("FROM  UserM ");
        sql.append("LEFT OUTER JOIN CodeM ON CodeM.KeyCd = '"+DefineCode.CodeM_UserType + "' AND UserM.UserType = CodeM.KeySubCd ");
        sql.append("LEFT OUTER JOIN CodeM as CodeM1 ON CodeM1.KeyCd = '"+DefineCode.CodeM_MenuType + "' AND UserM.UserType = CodeM1.KeySubCd ");
        sql.append("LEFT OUTER JOIN UserInfoTable ON UserM.KaiinId = UserInfoTable.KaiinId ");
        sql.append("WHERE UserId=? AND Password=?");

        return sql.toString();
    }

}
