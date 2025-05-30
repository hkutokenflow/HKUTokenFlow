package com.example.workshop1.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_DETAIL=
            "create table DETAIL("+
            "name varchar(32),"+
            "sex varchar(32),"+
            "num varchar(32),"+
            "birth varchar(32))";

    public static final String ADD_DETAIL=
            "INSERT INTO DETAIL VALUES('lkx','女','2020191228','2002年9月7日')";


    private Context mContext;

    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DETAIL);//创建数据库
//        db.execSQL(ADD_DETAIL);//添加一条默认的数据
        Toast.makeText(mContext,"数据库创建",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
