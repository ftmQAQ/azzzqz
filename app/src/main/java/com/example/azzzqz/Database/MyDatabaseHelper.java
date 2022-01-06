package com.example.azzzqz.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;

public class MyDatabaseHelper {
    private Context context;
    private MyDatabase dbOpen;
    private SQLiteDatabase db;
    public MyDatabaseHelper(Context context) {this.context=context;}

    private class MyDatabase extends SQLiteOpenHelper {
        public static final String CREATE_FRIEND = "create table friend(" +
                "account integer primary key autoincrement," +
                "username text," +
                "flag integer)";

        public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FRIEND);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    //读取或创建数据库
    public void open(String dbname)throws SQLiteException {
        dbOpen = new MyDatabase(context,dbname+".db",null,1);
        try{
            db=dbOpen.getWritableDatabase();
        } catch (Exception e) {
            db=dbOpen.getReadableDatabase();
        }
    }

    /**
     * 对个人好友的操作
     */
    public long inserfriend(String account,User user){
        open(account);
        ContentValues values = new ContentValues();
        values.put("account",user.getAccount());
        values.put("username",user.getUsername());
        values.put("flag","0");
        return db.insert("friend",null,values);
    }

    public long updatafriend(String account,User user){
        open(account);
        ContentValues values = new ContentValues();
        values.put("account",user.getAccount());
        values.put("username",user.getUsername());
        return db.update("friend",values,"account=?",new String[]{String.valueOf(user.getAccount())});
    }

    public User[] querryfriendAll(){//寻找好友表中的所有好友
        Cursor result=db.query("friend",null,null,null,null,null,null);
        return ConverToUser(result);
    }

    public User[] querryfriend(int account){//寻找好友表中的所有好友
        Cursor result=db.query("friend",null,"account=?",new String[]{String.valueOf(account)},null,null,null);
        return ConverToUser(result);
    }

    //获取flag是1的好友列表
    public User[] querryshowmin(){
        Cursor result=db.query("friend",null,"flag=?",new String[]{String.valueOf(1)},null,null,null);
        return ConverToUser(result);
    }

    /**
     *
     * @param account 聊天对象的账号
     * @param flag 是否有必要显示消息记录
     * @return
     */
    public long updatafriendflag(String account,String friaccount,int flag){//更新flag值
        open(account);
        ContentValues values = new ContentValues();
        Log.i("friaccount",friaccount);
        values.put("flag",flag);
        return db.update("friend",values,"account=?",new String[]{friaccount});
    }

    //将返回集，转换为User列表
    private User[] ConverToUser(Cursor result) {
        int resultsCount=result.getCount();
        if(resultsCount==0||!result.moveToFirst()){
            return null;
        }
        User[] users=new User[resultsCount];
        for(int i=0;i<resultsCount;i++){
            users[i]=new User();
            users[i].setUsername(result.getString(result.getColumnIndex("username")));
            users[i].setAccount(result.getInt(result.getColumnIndex("account")));
            result.moveToNext();
        }
        return users;
    }


    /**
     * 对好友聊天记录的操作
     * @param proposer
     * @throws SQLiteException
     */
    //读取或创建好友个人聊天数据数据表
    public void creatfritable(String proposer)throws SQLiteException {
        final String CREATE_FRIEND_MSG = "create table msg"+proposer+"(" +
                "id integer primary key autoincrement," +
                "msg text," +
                "type integer,"+
                "date text)";
        Log.i("CREATE_FRIEND_MSG",CREATE_FRIEND_MSG);
        try{
            db.execSQL(CREATE_FRIEND_MSG);
        }catch (Exception e) {
            Log.i("数据表创建","表已存在");
        }
    }

    //添加好友聊天内容
    public long insertMsg(Msg msg){
        String proposer=String.valueOf(msg.getProposer());
        open(String.valueOf(msg.getRecipient()));
        ContentValues values = new ContentValues();
        values.put("msg",msg.getMsg());
        values.put("date",msg.getDate());
        values.put("type",msg.getType());
        return db.insert("msg"+proposer,null,values);
    }

    //获取好友聊天内容
    public Msg[] querryMsgAll(String proposer){
        Cursor result=db.query("msg"+proposer,null,null,null,null,null,null);
        return ConverTomsgs(result);
    }

    public Msg querryNewMsg(String proposer){
        Msg msg=new Msg();
        Cursor result=db.query("msg"+proposer,null,null,null,null,null,null);
        result.moveToLast();
        msg.setMsg(result.getString(result.getColumnIndex("msg")));
        msg.setDate(result.getString(result.getColumnIndex("date")));
        return msg;
    }

    //将返回集，转换为Msg列表
    private Msg[] ConverTomsgs(Cursor result) {
        int resultsCount=result.getCount();
        if(resultsCount==0||!result.moveToFirst()){
            return null;
        }
        Msg[] msgs=new Msg[resultsCount];
        for(int i=0;i<resultsCount;i++){
            msgs[i]=new Msg();
            msgs[i].setMsg(result.getString(result.getColumnIndex("msg")));
            msgs[i].setDate(result.getString(result.getColumnIndex("date")));
            msgs[i].setType(result.getInt(result.getColumnIndex("type")));
            result.moveToNext();
        }
        return msgs;
    }

}
