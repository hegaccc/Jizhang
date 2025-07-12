package com.zyjcy.accounting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "account.db";
    private static final String TABLE_NAME = "table_account";
    private static final int DB_VERSION = 1;
    private static AccountDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;
    private Cursor cursor;
    private List<Account> list = new ArrayList<>();

    private AccountDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //利用单例模式获取数据库帮助器的唯一实例
    public static AccountDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new AccountDBHelper(context);
        }
        return mHelper;
    }

    //打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    //打开数据库的读连接
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    //关闭数据库连接
    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    //创建数据库表
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "date VARCHAR ," +
                "item VARCHAR ,"+
                "type VARCHAR,"+
                "amount DOUBLE ,"+
                "remark VARCHAR );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //增加一条数据
    public long insert(Account account) {
        ContentValues values = new ContentValues();
        //此处可以不用插入id，id让数据库自动生成
        values.put("date", account.getDate());
        values.put("item", account.getItem());
        values.put("type", account.getType());
        values.put("amount", account.getAmount());
        values.put("remark", account.getRemark());

        return mWDB.insert(TABLE_NAME, null, values);
    }

    //删除一条数据
    public long deleteById(int id){
        return mWDB.delete(TABLE_NAME,"id=?",new String[]{String.valueOf(id)});
    }

    //查询所有数据
    public List<Account> queryAll() {
        list.clear();
        cursor = mRDB.query(TABLE_NAME, null, null, null,
                null, null, "id desc");//asc是从小到大排序

        while (cursor.moveToNext()) {
            Account account = new Account();
            account.setId(cursor.getInt(0));
            account.setDate(cursor.getString(1));
            account.setItem(cursor.getString(2));
            account.setType(cursor.getString(3));
            account.setAmount(cursor.getDouble(4));
            account.setRemark(cursor.getString(5));

            list.add(account);
        }
        return list;
    }

    //通过收支类型type查询”收入“”支出“的情况
    public double querySUM(String type) {
    //通过type筛选数据，然后对金额求和，然后将数据返回
        cursor = mRDB.query(TABLE_NAME, new String[] {"SUM(amount)"}, "type=?",
                new String[]{type}, null, null,null );
        double d = 0.00;
        while (cursor.moveToNext()) {
         d=cursor.getDouble(0);
        }
        return d;
    }
    //获得数据库的元数据（列名称）
    public String[] getColumnNames(){
       return cursor.getColumnNames();
    }
}
