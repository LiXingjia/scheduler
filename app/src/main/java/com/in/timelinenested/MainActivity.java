package com.in.timelinenested;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.in.timelinenested.Adapter.TimeLineAdapter;
import com.in.timelinenested.activity.FragmentActivity;
import com.in.timelinenested.bean.Note;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.db.NoteDao;
import com.in.timelinenested.sphelper.SPManager;
import com.in.timelinenested.ui.NewActivity;
import com.in.timelinenested.ui.NoteActivity;

import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import csy.menu.satellitemenulib.view.SatelliteMenu;

public class MainActivity extends Activity {
    ImageView head;
    RecyclerView rv;
    private SatelliteMenu mSatelliteMenuRightBottom;//菜单
    private List<Local_Record> recordList;
    TimeLineAdapter timeLineAdapter;
    User mUser;
    LinearLayout my_background;
    Integer i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventManager.register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        rv = (RecyclerView) findViewById(R.id.rv);
        mSatelliteMenuRightBottom = (SatelliteMenu) findViewById(R.id.mSatelliteMenuRightBottom);
        head=(ImageView) findViewById(R.id.head);
        my_background = (LinearLayout) findViewById(R.id.my_background);

        my_background.setBackground(SPManager.getDrawable(this, "drawable", null));

        headChange();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);



        //在时间轴上添加记录
        timeLineAdapter = new TimeLineAdapter(this);
        //点击时间轴，进入记录界面
        timeLineAdapter.setOnMyItemClickListener(new TimeLineAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, Local_Record record) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("flag",1);//自己笔记
                bundle.putSerializable("record_id", record.getLR_record_id());
           //     intent.putExtra("flag",1);//自己笔记
                bundle.putSerializable("user_id",mUser.getObjectId());
                intent.putExtra("data", bundle);
                startActivity(intent);
            }

            @Override
            public void myLongClick(View v, final Local_Record record) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除笔记？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dele_Local(record);//在本地litepal数据库中删除记录
                        findL_user(record);//将L_U中对应record删除
                        dele_Record(record.getLR_record_id());//将Record表中记录删除
                        refreshNoteList();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

            }
        });

        rv.setAdapter(timeLineAdapter);

        //菜单点击事件
        List<Integer> imageResourceRightBottom = new ArrayList<>();//菜单图片,可根据需要设置子菜单个数
        imageResourceRightBottom.add(R.drawable.icon_setting);
        imageResourceRightBottom.add(R.drawable.icon_add);
        imageResourceRightBottom.add(R.drawable.icon_addresslist);
        mSatelliteMenuRightBottom.getmBuilder()
                .setMenuImage(R.drawable.menu)
                .setMenuItemImageResource(imageResourceRightBottom)
                .setOnMenuItemClickListener(new SatelliteMenu.OnMenuItemClickListener() {
                    @Override
                    public void onClick(View view, int postion) {
                        switch (postion) {
                            case 0: {
                                Toast.makeText(MainActivity.this, "进入设置界面", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, SetUpActivity.class);
                                //intent.setClass();
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                Toast.makeText(MainActivity.this, "添加记录", Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(MainActivity.this, NewActivity.class);
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("nflag",1);
                                bundle.putSerializable("user_id",mUser.getObjectId());
                                intent.putExtra("data",bundle);
                                intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }
                            case 2: {
                                Toast.makeText(MainActivity.this, "进入通讯录界面", Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                                //intent.setClass();
                                startActivity(intent);
                            }

                        }
                    }
                })
                .creat();

        //头像点击事件
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MyModifyinfoActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"进入头像界面",Toast.LENGTH_LONG).show();
            }
        });
    }
    void headChange(){
        mUser = BmobUser.getCurrentUser(User.class);
        //获取头像
        final String IMAGE_URL;
        IMAGE_URL= mUser.getU_pic_url().getFileUrl();
        new Thread(new Runnable(){

            @Override
            public void run() {
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                // post() 特别关键，就是到UI主线程去更新图片
                head.post(new Runnable(){
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        head.setImageDrawable(drawable) ;
                    }}) ;
            }

        }).start()  ;
    }
    //刷新笔记列表
    private void refreshNoteList(){
        Log.i("refresh", "refreshNoteList: 刷新列表");
        headChange();
        Log.i("refresh", "refreshNoteList:修改头像 ");
        recordList = DataSupport.order("LR_date_seq asc").find(Local_Record.class);
        Log.i("LOcal","本地有几条：" + recordList.size());
        if (recordList.size() == 0) {
            getRecords();
        } else {
            timeLineAdapter.setmNotes(recordList);
            timeLineAdapter.notifyDataSetChanged();
        }
    }

    protected void getRecords() { //添加所有记录到本地
        final User user_Curr = BmobUser.getCurrentUser(User.class);

        BmobQuery<Link_user> userBmobQuery = new BmobQuery<>();
        userBmobQuery.addWhereEqualTo("L_user_id", user_Curr.getObjectId());
        userBmobQuery.findObjects(new FindListener<Link_user>() {
            @Override
            public void done(List<Link_user> list, BmobException e) {
                if (e == null) {
                    final List<String> list_record = list.get(0).getL_record_id();
                    Log.i("Link_user", "找到了Link_user:" + list_record.size());
                    final int[] j = {0};
                    for (i = 0; i < list_record.size(); i++) {
                        BmobQuery<Record> bmobQuery1 = new BmobQuery<>();
                        bmobQuery1.addWhereEqualTo("objectId", list_record.get(i));
                        bmobQuery1.findObjects(new FindListener<Record>() {
                            @Override
                            public void done(List<Record> list, BmobException e) {
                                Log.i("Object", "添加第几条：");
                                j[0]++;
                                if (list.size() == 0) {
                                    return;
                                }
                                long t = BmobDate.getTimeStamp(list.get(0).getR_date().getDate());
                                Local_Record local_record = new Local_Record();
                                local_record.setLR_author(user_Curr.getObjectId());
                                local_record.setLR_content(list.get(0).getR_text());

                                String date = getDateToString(t,"yyyy-MM-dd");
                                local_record.setLR_Date(date);
                                local_record.setLR_read(list.get(0).getR_read());
                                local_record.setLR_record_id(list.get(0).getObjectId());
                                local_record.setLR_title(list.get(0).getR_title());
                                String date_q = getDateToString(t, "yyyyMMdd");
                                local_record.setLR_date_seq(Integer.parseInt(date_q));
                                local_record.save();

                                if (j[0] == list_record.size()) {
                                    recordList = DataSupport.order("LR_date_seq asc").find(Local_Record.class);
                                    Log.i("Local", "获取了所有record:" + recordList.size());
                                    timeLineAdapter.setmNotes(recordList);
                                    timeLineAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                } else
                    Log.i("Link_user", "没能找到Link_user");
            }
        });
    }

    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNoteList();

    }
    protected void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void dele_Local(Local_Record record){
        DataSupport.deleteAll(Local_Record.class, "LR_record_id = ?", record.getLR_record_id());
    }

    protected void findL_user(final Local_Record record){

        BmobQuery<Link_user> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_user_id", record.getLR_author());
        bmobQuery.findObjects(new FindListener<Link_user>() {
            @Override
            public void done(List<Link_user> list, BmobException e) {
                if(e==null){
                    Log.i("bmob","成功找到L_user");
                    String link_user_Oid = list.get(0).getObjectId();//L_U_objectId
                    dele_Luser_record(link_user_Oid, record.getLR_record_id());
                }else{
                    Log.i("bmob","没能成功找到L_user"+e.getMessage());
                }
            }
        });
    }

    protected void dele_Luser_record(String link_user_Oid, String recordId){
        //User user = BmobUser.getCurrentUser(User.class);

        Link_user link_user = new Link_user();

        link_user.setObjectId(link_user_Oid);
        link_user.removeAll("L_record_id", Arrays.asList(recordId));
        link_user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","成功");
                    Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_LONG);
                }else{
                    Log.i("bmob", "失败");
                    Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_LONG);
                }
            }


        });


    }

    protected void dele_Record(String recordId){
        Record record = new Record();
        record.setObjectId(recordId);
        record.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","数据删除成功");
                }else{
                    Log.i("bmob","数据没能删除成功"+e.getMessage());
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

    /**
     * 消息分发接收
     */
    @Subscribe
    public void onEventMainThread(PostEvent postEvent) {
        if (postEvent != null) {
            switch (postEvent.what) {
                //接收第三个界面发送过来的消息
                case Appconfig.EVENT_MAIN1:
//                    String str = (String) postEvent.object;
                    Drawable drawable = (Drawable) postEvent.object;

                    SPManager.putDrawable(this, "drawable", drawable);

                    my_background.setBackground(drawable);

                    break;
            }
        }
    }


}
