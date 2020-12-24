package com.in.timelinenested.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Administrator on 2018/5/30.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "myDatabase.db";
    private static final String TBL_RECORD = "MessageRecord"; //消息记录表
    private static final String TBL_MESSAGE = "FriendMessage" ;   //好友消息表
    private static final String TBL_FRIEND = "MyFriend";          //好友表
    private static final String TBL_GROUP = "FriendGroup";        //分组表
    private static final String TBL_NOTICE = "FriendNotice";      //好友通知
    public static final int UPDATE_NAME = 0;            //更新好友昵称
    public static final int UPDATE_REMARK = 1;          //更新好友备注
    public static final int UPDATE_GROUP = 2;           //修改好友分组
    public static final int UPDATE_FLAG = 3;            //同意后的状态
    public static final int STATE_UNTREATED = 0;        //未处理好友通知
    public static final int STATE_ACCEPT = 1;         //已同意好友
    public static final int STATE_REFUSE = 2;         //已拒绝好友
    public static final int STATE_WAIT = 3;           //等待对方同意
    public static final int STATE_ACCEPTED = 4;       //对方已同意
    public static final int STATE_REFUSED = 5;        //对方已拒绝

    private SQLiteDatabase db;
    public MyDBHelper(Context context){
        super(context,DB_NAME,null,2);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;
        String CREATE_TBL_GROUP = "create table FriendGroup(" +
                "_id integer primary key autoincrement," +
                "friendGroup String not null," +
                "groupOrder integer)";
        db.execSQL(CREATE_TBL_GROUP);
        String CREATE_TBL_FRIEND = "create table MyFriend(" +
                "_id integer primary key autoincrement," +
                "id String not null unique," +
                "name String," +
                "remark String," +
                "friendGroup String," +
                "flag integer," +
                "foreign key(friendGroup) references FriendGroup(friendGroup));" ;
        db.execSQL(CREATE_TBL_FRIEND);
        String CREATE_TBL_RECORD = "create table MessageRecord(" +
                "_id integer primary key autoincrement," +
                "id String Not Null," +
                "type integer," +
                "content text," +
                "time String," +
                "foreign key(id) references MyFriend(id));" ;
        db.execSQL(CREATE_TBL_RECORD);
        String CREATE_TBL_MESSAGE = "create table FriendMessage(" +
                "_id integer primary key autoincrement," +
                "id String unique," +
                "name String," +
                "content text," +
                "time String," +
                "unreadCount integer," +
                "foreign key(id) references MyFriend(id)," +
                "foreign key(name) references MyFriend(name)," +
                "foreign key(content) references MessageRecord(content));" ;
        db.execSQL(CREATE_TBL_MESSAGE);
        String CREATE_TBL_NOTICE = "create table FriendNotice(" +
                "_id integer primary key autoincrement," +
                "id String unique not null," +
                "name String," +
                "verify String," +
                "state Integer)";
        db.execSQL(CREATE_TBL_NOTICE);
        ContentValues values = new ContentValues();
        values.put("friendGroup","好友");
        values.put("groupOrder",0);
        db.insert(TBL_GROUP,null,values);
        values = new ContentValues();
        values.put("friendGroup","家人");
        values.put("groupOrder",1);
        db.insert(TBL_GROUP,null,values);
        values = new ContentValues();
        values.put("friendGroup","同学");
        values.put("groupOrder",2);
        db.insert(TBL_GROUP,null,values);
    }

    //消息记录表操作
    public void insertRecord(String id, int type, String content, String time){
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("type",type);
        values.put("content",content);
        values.put("time",time);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_RECORD,null,values);
        if(!insertMessage(id,getFriendName(id),content,time,1)){
            updateMessage(id,getFriendName(id),content,time);
        }
        db.close();

    }
    public Cursor queryRecordById(String id){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TBL_RECORD,new String[]{"type","content","time","_id"},
                "id = ?",new String[]{id},null,null,"_id asc",null);
    }
    public void deleteRecord(int _id){
        if(db == null)
            db = getWritableDatabase();
        db.delete(TBL_RECORD,"_id = ?",new String[]{String.valueOf(_id)});
    }

    //消息表操作
    public boolean insertMessage(String id,String name, String content, String time,int unreadCount){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("content", content);
            values.put("time", time);
            values.put("unreadCount", unreadCount);
            db.insertOrThrow(TBL_MESSAGE, null, values);
            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            return false;
        }finally {
            db.endTransaction();
        }
    }
    public void updateMessage(String id,String name,String content,String time){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name",name);
            values.put("content", content);
            values.put("time", time);
            db.update(TBL_MESSAGE, values, "id = ?", new String[]{id});
            String sql = "UPDATE " + TBL_MESSAGE + " SET unreadCount = unreadCount + 1 WHERE id = '" + id + "'";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
    public void deleteMessage(String id){
        if(db == null)
            db = getWritableDatabase();
        db.delete(TBL_MESSAGE,"id = "+id,null);
    }
    public Cursor queryMessage(){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TBL_MESSAGE,new String[]{"_id","id","name","content","time","unreadCount"},null,null,null,null,"time desc");
    }
    public void resetCount(String id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("unreadCount",0);
        db.update(TBL_MESSAGE,values,"id = ?",new String[]{id});
        db.close();
    }

    //好友表操作
    public boolean insertFriend(String id,String name,String remark,String group,int flag){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("remark", remark);
            values.put("friendGroup", group);
            values.put("flag", flag);
            db.insertOrThrow(TBL_FRIEND, null, values);
            db.setTransactionSuccessful();
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return false;
        }finally {
            db.endTransaction();
        }
        return true;
    }
    private String getFriendName(String id){
        String name;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TBL_FRIEND,new String[]{"name","remark"},"id = ?",new String[]{id},null,null,null);
        if(cursor.getCount() == 0){
            name = "陌生人";
        } else {
            cursor.moveToFirst();
            if (cursor.getString(1).equals(""))
                name = cursor.getString(0);
            else
                name = cursor.getString(1);
        }
        cursor.close();
        return name;
    }
    public void updateFriend(String id,String value,int type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        switch (type){
            case UPDATE_NAME:
                values.put("name",value);
                break;
            case UPDATE_REMARK:
                values.put("remark",value);
                break;
            case UPDATE_GROUP:
                values.put("friendGroup",value);
                break;
            case UPDATE_FLAG:
                values.put("flag",1);
        }
        db.update(TBL_FRIEND,values,"id = ?",new String[]{id});
    }
    public void insertOrUpdateFriend(String id,String name,String remark,String group,int flag){
        if(!insertFriend(id, name, remark, group, flag)){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("remark", remark);
            values.put("friendGroup", group);
            values.put("flag", flag);
            db.update(TBL_FRIEND,values,"id = ?",new String[]{id});
            db.close();
        }
    }
    public void deleteFriend(String id){
        if(db == null)
            db = getWritableDatabase();
        db.delete(TBL_FRIEND,"id = "+id,null);
    }
    public Cursor queryFriendByGroup(String group){
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TBL_FRIEND,new String[]{"_id","id","name","remark","friendGroup","flag"},"friendGroup = ? and flag = ?",new String[]{group,"1"},null,null,null);

    }

    //分组表操作
    public Cursor queryGroup(){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT FriendGroup.friendGroup,COUNT(id) " +
                "FROM FriendGroup LEFT OUTER JOIN MyFriend ON(FriendGroup.friendGroup = MyFriend.friendGroup) " +
                "GROUP BY FriendGroup.friendGroup " +
                "ORDER BY groupOrder;" ;
        return db.rawQuery(sql,null);
    }

    //通知表操作
    public void insertNotice(String id, String name, String remark, String myGroup, String verify, int state){
        db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put("id",id );
            values.put("name", name);
            values.put("verify", verify);
            values.put("state",state);
            db.insertOrThrow(TBL_NOTICE, null, values);
            insertFriend(id,name,remark,myGroup,0);
            db.setTransactionSuccessful();
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
    public void updateNotice(String id,String remark,String group,int state){
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state",state);
        db.update(TBL_NOTICE,values,"id = ?",new String[]{id});
        switch (state){
            case STATE_ACCEPT:
                updateFriend(id,remark,UPDATE_REMARK);
                updateFriend(id,group,UPDATE_GROUP);
                updateFriend(id,"",UPDATE_FLAG);
                break;
            case STATE_ACCEPTED:
                updateFriend(id,"",UPDATE_FLAG);
                break;
            case STATE_REFUSE:
                db.delete(TBL_FRIEND,"id = ?",new String[]{id});
                break;
            case STATE_REFUSED:
                db.delete(TBL_FRIEND,"id = ?",new String[]{id});
                break;
        }
        db.close();
    }
    public boolean insertOrAcceptNotice(String id, String name, String verify, int state){
        db = getReadableDatabase();
        Cursor cursor = db.query(TBL_NOTICE,new String[]{"state"},"id = ?",new String[]{id},null,null,null);
        if(cursor == null || cursor.getCount() == 0) {
            Cursor cursor1 = db.query(TBL_GROUP,new String[]{"friendGroup"},null,null,null,null,null);
            cursor1.moveToFirst();
            insertNotice(id, name, null,cursor1.getString(0),verify, state);
            cursor1.close();
            db.close();
            return true;
        } else if(cursor.moveToFirst() && cursor.getInt(0)==STATE_WAIT){
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }
    public void deleteNotice(String id){
        db = getWritableDatabase();
        db.delete(TBL_NOTICE,"id = ",new String[]{id});
        db.close();
    }
    public Cursor queryNotice(){
        db = getReadableDatabase();
        return db.query(TBL_NOTICE,new String[]{"_id","id","name","verify","state"},null,null,null,null,"_id desc");
    }

    //关闭数据库
    public void close(){
        if(db != null)
            db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
