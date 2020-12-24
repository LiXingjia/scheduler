package com.in.timelinenested;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Adapter.FriendTimeLineAdapter;
import com.in.timelinenested.Adapter.TimeLineAdapter;
import com.in.timelinenested.bean.Note;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.db.NoteDao;
import com.in.timelinenested.mock.Contact;
import com.in.timelinenested.sphelper.SPManager;
import com.in.timelinenested.ui.NewActivity;
import com.in.timelinenested.ui.NoteActivity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import csy.menu.satellitemenulib.view.SatelliteMenu;

public class FriendSquareActivity extends Activity {

    ImageView head1,head2,author1,author2;
    RecyclerView rv;
    private SatelliteMenu mSatelliteMenuRightBottom;//菜单
    FriendTimeLineAdapter friendTimeLineAdapter;

    int flag;//1表示真实好友，0表示虚拟好友
    private String myTitle;
    private String myContent;
    User user;
    User_virtual user_virtual;
    String user_id;
//    private final int SPLASH_DISPLAY_LENGHT = 3000; //延迟6秒
    private List<oderRecord> u_v_recordList ;//传给前端的data:虚拟用户的记录数组
    List<String> u_ids = new ArrayList<>();
    User mUser;
    String TAG="FriendSquare";
    User user1 = BmobUser.getCurrentUser(User.class);
    LinearLayout my_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate:");
        mUser = BmobUser.getCurrentUser(User.class);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.square_friend);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        rv = (RecyclerView) findViewById(R.id.rv);
        mSatelliteMenuRightBottom = (SatelliteMenu) findViewById(R.id.Menu_square_friend);
        head1 = (ImageView) findViewById(R.id.head_me);
        head2 = (ImageView) findViewById(R.id.head_him);
        author1=(ImageView) findViewById(R.id.author1);

        my_background = findViewById(R.id.my_background);

        my_background.setBackground(SPManager.getDrawable(this,"drawable",null));
//

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        //获取通讯录传来的点击用户信息
        final String IMAGE_URL;
        final String IMAGE_URLmy;
        IMAGE_URLmy = mUser.getU_pic_url().getFileUrl();

        Intent intent = getIntent();
        Bundle bundleM = intent.getBundleExtra("data");
        flag = (Integer) bundleM.getSerializable("flag");
        if (flag == 1) {
            user = (User) bundleM.getSerializable("user");
            user_id = user.getObjectId();
            IMAGE_URL = user.getU_pic_url().getFileUrl();

        } else {
            user_virtual = (User_virtual) bundleM.getSerializable("user");
            user_id = user_virtual.getObjectId();
            IMAGE_URL = user_virtual.getU_pic_url().getFileUrl();
        }
        //获取头像


        new Thread(new Runnable() {

            @Override
            public void run() {
                final Drawable drawable1 = loadImageFromNetwork(IMAGE_URL);
                final Drawable drawable2 = loadImageFromNetwork(IMAGE_URLmy);

                // post() 特别关键，就是到UI主线程去更新图片
                head2.post(new Runnable() {
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        head2.setImageDrawable(drawable1);
                    }
                });
                head1.post(new Runnable() {
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        head1.setImageDrawable(drawable2);
                    }
                });

            }

        }).start();
        friendTimeLineAdapter = new FriendTimeLineAdapter(this);

//        initU_V_Records(user_id);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                for (int i = 0; i <u_v_recordList.size(); i++) {
////                    Log.v("key", u_v_recordList.get(i).getNickname());
//                Log.i("FriendSquare", "run: "+u_v_recordList.size());
//                init();
//
//
//            }
//        }, SPLASH_DISPLAY_LENGHT);

        init();

    }
    public void init(){
     //   head2.setImageDrawable(contact.getProfileImage());

        u_v_recordList=new ArrayList<>();
        friendTimeLineAdapter.setmNotes(u_v_recordList);
        //点击时间轴，进入记录界面

        friendTimeLineAdapter.setOnMyItemClickListener(new FriendTimeLineAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, oderRecord record) {
                Intent intent = new Intent(FriendSquareActivity.this, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("record_id", record.getRecordId());
                Log.i(TAG, "myClick: record_id"+record.getRecordId());
                bundle.putSerializable("flag",0);
                bundle.putSerializable("user_id",user_id);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }

            @Override
            public void myLongClick(View v, final oderRecord record) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendSquareActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除笔记？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                  //      dele_Local(record);//在本地litepal数据库中删除记录
                  //      findL_user(record);//将L_U中对应record删除
                  //      dele_Record(record.getRecordId());//将Record表中记录删除
                        if(flag==1){
                            removeU_R_inArray(user_id,record.getRecordId());
                        }else{
                         removeU_V_R_inArray(user_id,record.getRecordId());}
//                            refreshNoteList();
                        }

                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

            }
        });
        rv.setAdapter(friendTimeLineAdapter);


        //菜单点击事件
        List<Integer> imageResourceRightBottom = new ArrayList<>();//菜单图片,可根据需要设置子菜单个数
        imageResourceRightBottom.add(R.drawable.icon_add);
        imageResourceRightBottom.add(R.drawable.icon_task);
        mSatelliteMenuRightBottom.getmBuilder()
                .setMenuImage(R.drawable.menu)
                .setMenuItemImageResource(imageResourceRightBottom)
                .setOnMenuItemClickListener(new SatelliteMenu.OnMenuItemClickListener() {
                    @Override
                    public void onClick(View view, int postion) {
                        switch (postion) {
                            case 0: {
                                Toast.makeText(FriendSquareActivity.this, "添加记录", Toast.LENGTH_LONG).show();
                                Intent intent =new Intent(FriendSquareActivity.this, NewActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("nflag",0);
                                bundle.putSerializable("user_id",user_id);
                                intent.putExtra("data", bundle);
                                intent.putExtra("flag",0);
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                Toast.makeText(FriendSquareActivity.this, "进入地图界面", Toast.LENGTH_SHORT ).show();
                            }

                        }
                    }
                })
                .creat();

        //头像点击事件
        head2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FriendSquareActivity.this,"进入头像2界面",Toast.LENGTH_LONG).show();
                Intent intent1=new Intent(FriendSquareActivity.this,ModifyInfoActivity.class);
                Bundle bundle=new Bundle();
                if(flag==1){
                    bundle.putSerializable("user", user);
                    bundle.putSerializable("flag", 1);
                }
                else{
                    bundle.putSerializable("user", user_virtual);
                    bundle.putSerializable("flag", 0);
                }
                //contact是从点击的好友的信息，从contactActivity传来，same as budleM
                intent1.putExtra("data", bundle);
                startActivity(intent1);

            }
        });
    }


    //刷新笔记列表
    private void refreshNoteList() {
        if(u_v_recordList.size()!=0){
            u_v_recordList.clear();
        }
        Log.i(TAG, "refreshNoteList: "+u_v_recordList.size());
        if(flag==1){//真实好友
            removeU_Record(user_id);
        }else{//虚拟好友
            Log.i(TAG, "refreshNoteList: 即将进入removeUV");
            removeU_V_Record(user_id);
        }



    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");


        refreshNoteList();
    }
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }//TODO


    private void removeU_V_Record (String u_v_id) {
        BmobQuery<Link_user_virtual> eq1 = new BmobQuery<Link_user_virtual>();
        //User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user1.getObjectId());
        BmobQuery<Link_user_virtual> eq2 = new BmobQuery<Link_user_virtual>();
        eq2.addWhereEqualTo("L_virtual_id",u_v_id);
//        BmobQuery<Link_user_virtual> eq3 = new BmobQuery<Link_user_virtual>();
//        eq3.addWhereEqualTo("L_black",false);
        List<BmobQuery<Link_user_virtual>> andQuerys = new ArrayList<BmobQuery<Link_user_virtual>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
//        andQuerys.add(eq3);
        BmobQuery<Link_user_virtual> query = new BmobQuery<Link_user_virtual>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> object, BmobException e) {
                if (e == null) {
                    //当前用户为虚拟用户
                    if(object.size()!=0){
                        if(u_ids.size()!=0){
                            u_ids.clear();
                        }
                        u_ids.addAll(object.get(0).getL_record_id());//拿到虚拟用户的记录ID数组
                        Log.v("key", "虚拟用户的记录ID数组：" + u_ids);
                        if(u_ids.size()==0){friendTimeLineAdapter.setmNotes(u_v_recordList);
                            friendTimeLineAdapter.notifyDataSetChanged();}
                        final int[] doneNumber = {0};
                        for (int i = 0; i < u_ids.size(); i++) {
                            BmobQuery<Record> categoryBmobQuery = new BmobQuery<>();
                            categoryBmobQuery.addWhereEqualTo("objectId", u_ids.get(i));
                            categoryBmobQuery.findObjects(new FindListener<Record>() {
                                @Override
                                public void done(List<Record> object, BmobException e) {
                                    if (e == null) {
                                        if(object.size()!=0){
                                                Record rTest = object.get(0);
                                                oderRecord oRecord = new oderRecord();
                                                oRecord.setRecordId(rTest.getObjectId());
                                                oRecord.setoR_author(rTest.getR_author());
                                                oRecord.setoR_date(rTest.getR_date());
                                                oRecord.setoR_title(rTest.getR_title());
                                                oRecord.setoR_read(rTest.getR_read());
                                                oRecord.setoR_text(rTest.getR_text());
                                                oRecord.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String objectId, BmobException e) {
                                                        if (e == null) {
                                                            doneNumber[0]++;
                                                            if(doneNumber[0]==u_ids.size()){
                                                                oderByDate();
                                                            }
                                                            Log.e("key", "在oderRecord表中新建记录成功，新建记录的ID："+objectId);
                                                        } else {
                                                            Log.e("error", "在oderRecord表中新建记录失败："+e.toString());
                                                        }

                                                    }
                                                });

                                        }
                                    } else {
                                        Log.e("error", "虚拟用户查询Record表失败："+e.toString());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.e("error", "复合查询Link_user_virtual失败："+e.toString());
                }
            }
        });

    }
    private void removeU_Record (String u_v_id) {
        final String id = u_v_id;
        BmobQuery<Link_user2> eq1 = new BmobQuery<>();
        //User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user1.getObjectId());
        BmobQuery<Link_user2> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("L_linked_user_id",u_v_id);
//        BmobQuery<Link_user2> eq3 = new BmobQuery<>();
//        eq3.addWhereEqualTo("L_black",false);
        List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
//        andQuerys.add(eq3);
        BmobQuery<Link_user2> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> object, BmobException e) {
                if (e == null) {
                    //此人是真实用户
                    if(object.size()!=0){
                        if(u_ids.size()!=0){
                            u_ids.clear();
                        }
                        u_ids.addAll(object.get(0).getL_record_id());//拿到真实用户自己写的记录ID数组
                        Log.v("key", "真实用户自己写的记录ID数组：" + u_ids);
                        //拿到真实用户关联好友写的记录ID数组
                        BmobQuery<Link_user2> q1 = new BmobQuery<>();
                        //User user = BmobUser.getCurrentUser(User.class);
                        q1.addWhereEqualTo("L_linked_user_id",user1.getObjectId());
                        BmobQuery<Link_user2> q2 = new BmobQuery<>();
                        q2.addWhereEqualTo("L_user_id",id);
//                        BmobQuery<Link_user2> q3 = new BmobQuery<>();
//                        q3.addWhereEqualTo("L_user_id",id);
                        List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
                        andQuerys.add(q1);
                        andQuerys.add(q2);
                        BmobQuery<Link_user2> query = new BmobQuery<>();
                        query.and(andQuerys);
                        query.findObjects(new FindListener<Link_user2>() {
                            @Override
                            public void done(List<Link_user2> list, BmobException e){
                                if(e==null){
                                    if(list.size()!=0){//此人是真实用户&关联了好友
                                       u_ids.addAll(list.get(0).getL_record_id());
                                       Log.v("key", "真实用户和关联好友一起写了"+u_ids.size()+"记录：" + u_ids);
                                       if(u_ids.size()==0){
                                           friendTimeLineAdapter.setmNotes(u_v_recordList);
                                           friendTimeLineAdapter.notifyDataSetChanged();
                                       }
                                        final int[] doneNumber = {0};
                                        for (int j = 0; j < u_ids.size(); j++) {
                                            BmobQuery<Record> categoryBmobQuery = new BmobQuery<>();
                                            categoryBmobQuery.addWhereEqualTo("objectId", u_ids.get(j));
                                            categoryBmobQuery.findObjects(new FindListener<Record>() {
                                                @Override
                                                public void done(List<Record> object, BmobException e) {
                                                    if (e == null) {
                                                        if(object.size()!=0){
                                                                Record rTest = object.get(0);
                                                                oderRecord oRecord = new oderRecord();
                                                                oRecord.setRecordId(rTest.getObjectId());
                                                                oRecord.setoR_author(rTest.getR_author());
                                                                oRecord.setoR_date(rTest.getR_date());
                                                                oRecord.setoR_title(rTest.getR_title());
                                                                oRecord.setoR_read(rTest.getR_read());
                                                                oRecord.setoR_text(rTest.getR_text());
                                                                oRecord.save(new SaveListener<String>() {
                                                                    @Override
                                                                    public void done(String objectId, BmobException e) {
                                                                        if (e == null) {
                                                                            doneNumber[0]++;
                                                                            Log.i(TAG, "done: oderbydate 前"+doneNumber[0]);
                                                                            if(doneNumber[0]==u_ids.size()){
                                                                                Log.i(TAG, "done: oderbydate 后");
                                                                                oderByDate();
                                                                            }
                                                                            Log.e("key", "在oderRecord表中新建记录成功，新建记录的ID："+objectId);
                                                                        } else {
                                                                            Log.e("error", "在oderRecord表中新建记录失败："+e.toString());
                                                                        }

                                                                    }
                                                                });

                                                        }
                                                    } else {
                                                        Log.e("error", "真实用户&关联了好友查询Record表失败："+e.toString());
                                                    }
                                                }
                                            });
                                        }
                                    }else {
                                        final int[] doneNumber = {0};
                                        for (int j = 0; j < u_ids.size(); j++) {
                                            BmobQuery<Record> categoryBmobQuery = new BmobQuery<>();
                                            categoryBmobQuery.addWhereEqualTo("objectId", u_ids.get(j));
                                            categoryBmobQuery.findObjects(new FindListener<Record>() {
                                                @Override
                                                public void done(List<Record> object, BmobException e) {
                                                    if (e == null) {
                                                        if(object.size()!=0){
                                                                Record rTest = object.get(0);
                                                                oderRecord oRecord = new oderRecord();
                                                                oRecord.setRecordId(rTest.getObjectId());
                                                                oRecord.setoR_author(rTest.getR_author());
                                                                oRecord.setoR_date(rTest.getR_date());
                                                                oRecord.setoR_title(rTest.getR_title());
                                                                oRecord.setoR_read(rTest.getR_read());
                                                                oRecord.setoR_text(rTest.getR_text());
                                                                oRecord.save(new SaveListener<String>() {
                                                                    @Override
                                                                    public void done(String objectId, BmobException e) {
                                                                        if (e == null) {
                                                                            doneNumber[0]++;
                                                                            if(doneNumber[0]==u_ids.size()){
                                                                                oderByDate();
                                                                            }
                                                                            Log.e("key", "在oderRecord表中新建记录成功，新建记录的ID："+objectId);
                                                                        } else {
                                                                            Log.e("error", "在oderRecord表中新建记录失败："+e.toString());
                                                                        }

                                                                    }
                                                                });
                                                            }
                                                    } else {
                                                        Log.e("error", "对方没关联你&真实用户查询Record表失败："+e.toString());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }else{
                                    Log.e("error", "第二轮复合查询Link_user2失败："+e.toString());
                                }
                            }

                        });

                    }
                } else {
                    Log.e("error", "第一轮复合查询Link_user2失败："+e.toString());
                }
            }
        });

    }
//    private void addOderRecord(String authorId, BmobDate rDate, String rTitle, Boolean read, String rText, String id) {
//        oderRecord oRecord = new oderRecord();
//        oRecord.setRecordId(id);
//        oRecord.setoR_author(authorId);
//        oRecord.setoR_date(rDate);
//        oRecord.setoR_title(rTitle);
//        oRecord.setoR_read(read);
//        oRecord.setoR_text(rText);
//        oRecord.save(new SaveListener<String>() {
//            @Override
//            public void done(String objectId, BmobException e) {
//                if (e == null) {
//                    Log.e("key", "在oderRecord表中新建记录成功，新建记录的ID："+objectId);
//                } else {
//                    Log.e("error", "在oderRecord表中新建记录失败："+e.toString());
//                }
//            }
//        });
//    }
    private void oderByDate(){
        BmobQuery<oderRecord> r1 = new BmobQuery<>();
        r1.order("oR_date").setLimit(100)
                .findObjects(new FindListener<oderRecord>() {
                    @Override
                    public void done(List<oderRecord> list, BmobException e) {
                        if(e==null){

                            u_v_recordList.addAll(list);
                            //TODO 获取列表
                            Log.i(TAG, "获取列表成功"+u_v_recordList.size());
                            friendTimeLineAdapter.setmNotes(u_v_recordList);
                            friendTimeLineAdapter.notifyDataSetChanged();

                            deleteOderRecord(list);//前段拿到数据就清空oderRecord
                        }else{
                            Log.e("key2", e.toString());
                        }
                    }
                });
    }
    private void deleteOderRecord(List<oderRecord> oderRecords) {
        List<BmobObject> records = new ArrayList<>();
        for (int i = 0; i < oderRecords.size(); i++) {
            oderRecord record = new oderRecord();
            record.setObjectId(oderRecords.get(i).getObjectId());
            records.add(record);
        }
        new BmobBatch().deleteBatch(records).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> results, BmobException e) {
                if (e == null) {
                    Log.v("key","删除新的记录表中的全部数据成功！");
                } else {
                    Log.v("key","删除新的记录表中的全部数据失败！"+e.getMessage());
                }
            }
        });
    }
    //TODO *删除一条虚拟用户记录
    //1、去记录表删除
    private void delete_u_v_Record(String u_v_RecordId){
        Record record = new Record();
        record.setObjectId(u_v_RecordId);
        record.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","删除一条虚拟用户记录成功");
                }else{
                    Log.i("bmob","删除一条虚拟用户记录失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //TODO *删除一条虚拟用户记录
    //2、去U_V表的数组中移出
    private void removeU_V_R_inArray (String u_v_id, final String u_v_RecordId) {
//查询L_user_id是当前用户的ID，每一个查询条件都需要New一个BmobQuery对象
//--and条件1
        BmobQuery<Link_user_virtual> eq1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        //查询L_virtual_id是添加的虚拟用户的ID
//--and条件2
        BmobQuery<Link_user_virtual> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("L_virtual_id",u_v_id);           //传入虚拟用户ID
//最后组装完整的and条件
        List<BmobQuery<Link_user_virtual>> andQuerys = new ArrayList<BmobQuery<Link_user_virtual>>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
//查询符合整个and条件的人
        BmobQuery<Link_user_virtual> query = new BmobQuery<Link_user_virtual>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> object, BmobException e) {
                if (e == null) {
                    if(object.size()!=0){
                        Log.e("key", "查询记录数组所在那条数据成功");
                        Link_user_virtual link_user_virtual = new Link_user_virtual();
                        link_user_virtual.setObjectId(object.get(0).getObjectId());
                        link_user_virtual.removeAll("L_record_id", Arrays.asList(u_v_RecordId));
                        link_user_virtual.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    refreshNoteList();
                                    delete_u_v_Record(u_v_RecordId);
                                    Log.i("bmob","删除记录数组中的指定记录ID成功");
                                }else{
                                    Log.i("bmob","删除记录数组中的指定记录ID失败："+e.getMessage()+","+e.getErrorCode());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("error", "查询记录数组所在那条数据失败"+e.toString());
                }
            }
        });

    }
    private void removeU_R_inArray (String u_v_id, final String u_v_RecordId) {
//查询L_user_id是当前用户的ID，每一个查询条件都需要New一个BmobQuery对象
//--and条件1
        BmobQuery<Link_user2> eq1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        //查询L_virtual_id是添加的虚拟用户的ID
//--and条件2
        BmobQuery<Link_user2> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("L_linked_user_id",u_v_id);           //传入虚拟用户ID
//最后组装完整的and条件
        List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
//查询符合整个and条件的人
        BmobQuery<Link_user2> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> object, BmobException e) {
                if (e == null) {
                    if(object.size()!=0){
                        Log.e("key", "查询记录数组所在那条数据成功");
                        Link_user2 link_user2 = new Link_user2();
                        link_user2.setObjectId(object.get(0).getObjectId());
                        link_user2.removeAll("L_record_id", Arrays.asList(u_v_RecordId));
                        link_user2.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    refreshNoteList();
                                    delete_u_v_Record(u_v_RecordId);
                                    Log.i("bmob","删除记录数组中的指定记录ID成功");
                                }else{
                                    Log.i("bmob","删除记录数组中的指定记录ID失败："+e.getMessage()+","+e.getErrorCode());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("error", "查询记录数组所在那条数据失败"+e.toString());
                }
            }
        });

    }
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(),null);
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable ;
    }

}
