package com.in.timelinenested.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Local_Record;
import com.in.timelinenested.R;
import com.in.timelinenested.Record;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.oderRecord;
import com.in.timelinenested.utils.StringUtils;
import com.sendtion.xrichtext.RichTextView;


import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 笔记详情
 */
public class NoteActivity extends BaseActivity {
    private static final String TAG = "NoteActivity";

    private TextView tv_note_title,tv_author;//笔记标题
    private RichTextView tv_note_content;//笔记内容
    private TextView tv_note_time_new;//笔记创建时间
    private String myTitle;
    private String myContent;

    private Local_Record local_record;
    private oderRecord oderRord;
    private  String record_id;
    Record record = new Record();
    String user_id;
    String author;

    private ProgressDialog loadingDialog;
    private Disposable mDisposable;
    int flag;//0表示其他用户笔记id,1表示我的用户笔记id
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  //      requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_note);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        currentUser= BmobUser.getCurrentUser(User.class);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_note);
        toolbar.setTitle("笔记详情");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //toolbar.setNavigationIcon(R.drawable.ic_dialog_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tv_note_title = (TextView) findViewById(R.id.tv_note_title);//标题
        tv_note_title.setTextIsSelectable(true);
        tv_note_content = (RichTextView) findViewById(R.id.tv_note_content);//内容
        tv_note_time_new = (TextView) findViewById(R.id.tv_note_time_new);
        tv_author=(TextView)findViewById(R.id.tv_author);


        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        flag=(Integer)bundle.getSerializable("flag");
        user_id=(String)bundle.getSerializable("user_id");
        record_id=(String) bundle.getSerializable("record_id");
        if (flag == 1) {//1自己，0空间
         //   record_id = (String) bundle.getSerializable("record_id");
            local_record = DataSupport.where("LR_record_id = ?", record_id).find(Local_Record.class).get(0);
            myTitle = local_record.getLR_title();
            myContent = local_record.getLR_content();
            //       Group group = groupDao.queryGroupById(note.getGroupId());


            tv_note_title.setText(myTitle);
            tv_note_content.post(new Runnable() {
                @Override
                public void run() {
                    dealWithContent();
                }
            });
            tv_note_time_new.setText(local_record.getLR_Date());
        }

        else {

                Log.i(TAG, "initView: recordid"+record_id);
            getRecordItem(record_id);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run:record_id "+record_id);
                        Log.i("FriendSquare+record", "run: "+record);
     //                   friendTimeLineAdapter.setmNotes(u_v_recordList);
       //                 friendTimeLineAdapter.notifyDataSetChanged();
                        author=record.getR_author();
                        Log.i(TAG, "author"+author);
                        myTitle = record.getR_title();
                        myContent = record.getR_text();
                        //       Group group = groupDao.queryGroupById(note.getGroupId());
                        Log.i(TAG, "tv_author"+tv_author);
                        tv_author.setText(author);
                        tv_note_title.setText(myTitle);
                        tv_note_content.post(new Runnable() {
                            @Override
                            public void run() {
                                dealWithContent();
                            }
                        });
                        //时间转换为年月日
                        long t = BmobDate.getTimeStamp(record.getR_date().getDate());
                        tv_note_time_new.setText(getDateToString(t,"yyyy-MM-dd"));

                    }
                }, 1000);

        }


    }
    //时间转化方法
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private void dealWithContent(){
        //showEditData(myContent);
        tv_note_content.clearAllLayout();
        showDataSync(myContent);

        // 图片点击事件
        tv_note_content.setOnRtImageClickListener(new RichTextView.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(String imagePath) {
                ArrayList<String> imageList = StringUtils.getTextFromHtml(myContent, true);
                int currentPosition = imageList.indexOf(imagePath);
                showToast("点击图片："+currentPosition+"："+imagePath);

                //点击图片预览
//                PhotoPreview.builder()
//                        .setPhotos(imageList)
//                        .setCurrentItem(currentPosition)
//                        .setShowDeleteButton(false)
//                        .start(NoteActivity.this);
            }
        });
    }

    /**
     * 异步方式显示数据
     * @param html
     */
    private void showDataSync(final String html){

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                showEditData(emitter, html);
            }
        })
        //.onBackpressureBuffer()
        .subscribeOn(Schedulers.io())//生产事件在io
        .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
        .subscribe(new Observer<String>() {
            @Override
            public void onComplete() {
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
                showToast("解析错误：图片不存在或已损坏");
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onNext(String text) {
                if (tv_note_content !=null) {
                    if (text.contains("<img") && text.contains("src=")) {
                        //imagePath可能是本地路径，也可能是网络地址
                        String imagePath = StringUtils.getImgSrc(text);
                        tv_note_content.addImageViewAtIndex(tv_note_content.getLastIndex(), imagePath);
                    } else {
                        tv_note_content.addTextViewAtIndex(tv_note_content.getLastIndex(), text);
                    }
                }
            }
        });

    }

    /**
     * 显示数据
     * @param html
     */
    private void showEditData(ObservableEmitter<String> emitter, String html) {
        try {
            List<String> textList = StringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        } catch (Exception e){
            e.printStackTrace();
            emitter.onError(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_note_edit://编辑笔记
                    Intent intent = new Intent(NoteActivity.this, NewActivity.class);
                    Bundle bundle = new Bundle();
                    if (flag == 1) {
                        bundle.putSerializable("nflag", 1);
                        bundle.putSerializable("record_id", local_record.getLR_record_id());
                        intent.putExtra("data", bundle);
                        intent.putExtra("flag", 1);//编辑笔记
                        startActivity(intent);
                        finish();
                        //      bundle.putSerializable("user_id",user_id);
                    } else {//空间笔记
                        if(author.equals(currentUser.getNickname())){
                            bundle.putSerializable("nflag", 0);
                            bundle.putSerializable("record_id", record_id);
                            Log.i(TAG, "onOptionsItemSelected: record_id " + record_id);
                            intent.putExtra("data", bundle);
                            intent.putExtra("flag", 1);//编辑笔记
                            startActivity(intent);
                            finish();
                        }
                            else {
                            Toast.makeText(this,"不能编辑对方笔记",Toast.LENGTH_LONG).show();
                        }
                        //          bundle.putSerializable("user_id",user_id);
                    }

                    break;


//            case R.id.action_note_share://分享笔记
//                CommonUtil.shareTextAndImage(this, local_record.getLR_title(), local_record.getLR_content(), null);//分享图文
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    private void getRecordItem(String id){
        BmobQuery<Record> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("objectId",id);
        eq1.findObjects(new FindListener<Record>() {
            @Override
            public void done(List<Record> list, BmobException e) {
                if(e==null){
                    record = list.get(0);
                }else {
                    Log.v("error","查询Record表失败："+e.getMessage());
                }
            }
        });
    }
}
