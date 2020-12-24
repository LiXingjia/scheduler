package com.in.timelinenested.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.Link_user;
import com.in.timelinenested.Link_user2;
import com.in.timelinenested.Link_user_virtual;
import com.in.timelinenested.Local_Record;
import com.in.timelinenested.R;
import com.in.timelinenested.Record;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.oderRecord;
import com.in.timelinenested.utils.CommonUtil;
import com.in.timelinenested.utils.ImageUtils;
import com.in.timelinenested.utils.MyGlideEngine;
import com.in.timelinenested.utils.SDCardUtil;
import com.in.timelinenested.utils.StringUtils;
import com.sendtion.xrichtext.RichTextEditor;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 新建笔记
 */
public class NewActivity extends BaseActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量

    private EditText et_new_title;
    private RichTextEditor et_new_content;
    private TextView tv_new_group;

    private EditText edit_time;
    private Button btn_choose_time;
    private DatePickerDialog dateDialog;
    private int year, monthOfYear, dayOfMonth;
    private String time_text; //选择的时间

    private String record_id;
    private Local_Record local_record;
    private Record record;
    User currentUser ;
    String user_id;

 //   private Note note;//笔记对象
    private String myTitle;
    private String myContent;
    private String myGroupName;
    private String myNoteTime;
    private int flag;//区分是新建笔记还是编辑笔记
    private  int nflag;//区分自己笔记还是朋友空间笔记，1表示自己笔记，0表示朋友空间笔记
    private oderRecord oderRecord;

    private static final int cutTitleLength = 20;//截取的标题长度

    private ProgressDialog loadingDialog;
    private ProgressDialog insertDialog;
    private int screenWidth;
    private int screenHeight;
    private Disposable subsLoading;
    private Disposable subsInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        currentUser= BmobUser.getCurrentUser(User.class);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //toolbar.setNavigationIcon(R.drawable.ic_dialog_info);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealwithExit();
            }
        });

      //  groupDao = new GroupDao(this);
   //     noteDao = new NoteDao(this);
  //      note = new Note();
        local_record =new Local_Record();
        record = new Record();
        oderRecord=new oderRecord();

        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);

        insertDialog = new ProgressDialog(this);
        insertDialog.setMessage("正在插入图片...");
        insertDialog.setCanceledOnTouchOutside(false);

        et_new_title = (EditText) findViewById(R.id.et_new_title);
        et_new_content = (RichTextEditor) findViewById(R.id.et_new_content);
        tv_new_group = (TextView) findViewById(R.id.tv_new_group);

        btn_choose_time = (Button)findViewById(R.id.btn_choose_date);
        edit_time = (EditText) findViewById(R.id.edit_time);

        //通过Calendar对象来获取年月日的信息
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(calendar.YEAR);
        monthOfYear = calendar.get(calendar.MONTH);
        dayOfMonth = calendar.get(calendar.DAY_OF_MONTH);

        //实例化DatePickerDialog
        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int datOfMonth) {
                //把获取的日期显示在文本框内，月份从0开始计数，所以要+1
                time_text = year + "-" + (monthOfYear + 1) + "-" + datOfMonth;
                local_record.setLR_date_seq(year*10000 + monthOfYear*100 + datOfMonth);
                edit_time.setText(time_text);
            }
        },year,monthOfYear,dayOfMonth );

        //对日期选择器按钮设置监听事件
        btn_choose_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击日期选择器按钮时显示出日期对话框
                dateDialog.show();
            }
        });

        openSoftKeyInput();//打开软键盘显示

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        nflag=(Integer) bundle.getSerializable("nflag");//1表示自己的笔记，0表示空间笔记
        flag = intent.getIntExtra("flag", 0);//0新建，1编辑
        user_id=(String)bundle.getSerializable("user_id");
        if (flag == 1){//编辑
            setTitle("编辑笔记");
            record_id = (String) bundle.getSerializable("record_id");

            if(nflag==1) {

                local_record = DataSupport.where("LR_record_id = ?", record_id).find(Local_Record.class).get(0);

                myTitle = local_record.getLR_title();
                myContent = local_record.getLR_content();
                Log.i("NewActivity", "run:local content "+myContent);
                myNoteTime = local_record.getLR_Date();
                edit_time.setText(myNoteTime);
                et_new_title.setText(myTitle);
                et_new_content.post(new Runnable() {
                    @Override
                    public void run() {
                        dealWithContent();
                    }
                });
            }
            else{

                    Log.i("NewActivity", "initView:user_id ");
                    getRecordItem(record_id);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("FriendSquare", "run: "+record);
                            myTitle = record.getR_title();
                            myContent = record.getR_text();
                            Log.i("NewActivity", "run:network content "+myContent);
                            long t = BmobDate.getTimeStamp(record.getR_date().getDate());
                            myNoteTime = getDateToString(t,"yyyy-MM-dd");
                            edit_time.setText(myNoteTime);
                            et_new_title.setText(myTitle);
                            et_new_content.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("NewActivity", "second run ");
                                    dealWithContent();
                                }
                            });
                        }
                    }, 1000);


            }
                loadingDialog = new ProgressDialog(this);
                loadingDialog.setMessage("数据加载中...");
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.show();
        } else {
            setTitle("新建笔记");
            myNoteTime = time_text;
            edit_time.setText(myNoteTime);
        }

    }
    //时间转化方法
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    private void dealWithContent(){
        //showEditData(note.getContent());
        et_new_content.clearAllLayout();
        showDataSync(myContent);

        // 图片删除事件
        et_new_content.setOnRtImageDeleteListener(new RichTextEditor.OnRtImageDeleteListener() {

            @Override
            public void onRtImageDelete(String imagePath) {
                if (!TextUtils.isEmpty(imagePath)) {
                    boolean isOK = SDCardUtil.deleteFile(imagePath);
                    if (isOK) {
                        showToast("删除成功：" + imagePath);
                    }
                }
            }
        });
        // 图片点击事件
        et_new_content.setOnRtImageClickListener(new RichTextEditor.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(String imagePath) {
                myContent = getEditData();
                if (!TextUtils.isEmpty(myContent)){
                    List<String> imageList = StringUtils.getTextFromHtml(myContent, true);
                    if (!TextUtils.isEmpty(imagePath)) {
                        int currentPosition = imageList.indexOf(imagePath);
                        showToast("点击图片：" + currentPosition + "：" + imagePath);
                    }
                }
            }
        });
    }

    /**
     * 关闭软键盘
     */
    private void closeSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && imm.isActive() && getCurrentFocus() != null){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //imm.hideSoftInputFromInputMethod();//据说无效
            //imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); //强制隐藏键盘
            //如果输入法在窗口上已经显示，则隐藏，反之则显示
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开软键盘
     */
    private void openSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && !imm.isActive() && et_new_content != null){
            et_new_content.requestFocus();
            //第二个参数可设置为0
            //imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);//强制显示
            imm.showSoftInputFromInputMethod(et_new_content.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
        }
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
                if (et_new_content != null) {
                    //在图片全部插入完毕后，再插入一个EditText，防止最后一张图片后无法插入文字
                    et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), "");
                }
            }

            @Override
            public void onError(Throwable e) {
                if (loadingDialog != null){
                    loadingDialog.dismiss();
                }
                showToast("解析错误：图片不存在或已损坏");
            }

            @Override
            public void onSubscribe(Disposable d) {
                subsLoading = d;
            }

            @Override
            public void onNext(String text) {
                if (et_new_content != null) {
                    if (text.contains("<img") && text.contains("src=")) {
                        //imagePath可能是本地路径，也可能是网络地址
                        String imagePath = StringUtils.getImgSrc(text);
                        //插入空的EditText，以便在图片前后插入文字
                        et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), "");
                        et_new_content.addImageViewAtIndex(et_new_content.getLastIndex(), imagePath);
                    } else {
                        et_new_content.addEditTextAtIndex(et_new_content.getLastIndex(), text);
                    }
                }
            }
        });
    }

    /**
     * 显示数据
     */
    protected void showEditData(ObservableEmitter<String> emitter, String html) {
        try{
            List<String> textList = StringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        }catch (Exception e){
            e.printStackTrace();
            emitter.onError(e);
        }
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private String getEditData() {
        List<RichTextEditor.EditData> editList = et_new_content.buildEditData();
        StringBuilder content = new StringBuilder();
        for (RichTextEditor.EditData itemData : editList) {
            if (itemData.inputStr != null) {
                content.append(itemData.inputStr);
            } else if (itemData.imagePath != null) {
                content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
            }
        }
        return content.toString();
    }

    /**
     * 保存数据,=0销毁当前界面，=1不销毁界面，为了防止在后台时保存笔记并销毁，应该只保存笔记
     */
    private void saveNoteData(boolean isBackground) throws Exception {
        String noteTitle = et_new_title.getText().toString();
        String noteContent = getEditData();
     //   String groupName = tv_new_group.getText().toString();
        String noteTime = edit_time.getText().toString();

        Log.i("saved time", "saveNoteData: "+noteTime);
        Log.i("saved time", "saveNoteData: "+noteTitle);

//        Group group = groupDao.queryGroupByName(myGroupName);

            if (noteTitle.length() == 0 ){//如果标题为空，则截取内容为标题
                if (noteContent.length() > cutTitleLength){
                    noteTitle = noteContent.substring(0,cutTitleLength);
                } else if (noteContent.length() > 0){
                    noteTitle = noteContent;
                }
            }
   //         int groupId = group.getId();
        if(nflag==1) {//修改的自己的笔记
            local_record.setLR_author(currentUser.getObjectId());
            local_record.setLR_title(noteTitle);
            local_record.setLR_content(noteContent);
            local_record.setLR_record_id(record_id);
            local_record.setLR_Date(noteTime);
            if (flag == 0) {//新建笔记
                if (noteTitle.length() == 0 && noteContent.length() == 0) {
                    if (!isBackground) {
                        Toast.makeText(NewActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    saveRecord();

                    flag = 1;//插入以后只能是编辑
                    if (!isBackground) {
//                        Intent intent = new Intent();
//                        setResult(RESULT_OK, intent);
//                        finish();
                    }
                }
            } else if (flag == 1) {//编辑笔记
                if (!noteTitle.equals(myTitle) || !noteContent.equals(myContent)
                        || !noteTime.equals(myNoteTime)) {
                    update_Record();
                    update_Local();
                    //   noteDao.updateNote(note);
                }
                if (!isBackground) {
                    finish();
                }
            }
        }else{//修改和其他用户的笔记

            if (flag == 0) {//新建笔记
                if (noteTitle.length() == 0 && noteContent.length() == 0) {
                    if (!isBackground) {
                        Toast.makeText(NewActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //saveRecord();
                    addu_v_record(noteTime,noteTitle,noteContent,true,user_id);
                    flag = 1;//插入以后只能是编辑
                    if (!isBackground) {
//                        Intent intent = new Intent();
//                        setResult(RESULT_OK, intent);
//                        finish();
                    }
                }
            } else if (flag == 1) {//编辑笔记
                if (!noteTitle.equals(myTitle) || !noteContent.equals(myContent)
                        || !noteTime.equals(myNoteTime)) {
                //    update_Record();
             //       update_Local();
                    upload_u_v_Record(noteTime,noteTitle,noteContent,true,record_id);
                }
                if (!isBackground) {
                    finish();
                }
            }
        }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_image:
                closeSoftKeyInput();//关闭软键盘
                callGallery();
                break;
            case R.id.action_new_save:
                try {
                    saveNoteData(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 调用图库选择
     */
    private void callGallery(){
//        //调用系统图库
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");// 相片类型
//        startActivityForResult(intent, 1);

        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(3)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true,"com.sendtion.matisse.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == 1){
                    //处理调用系统图库
                } else if (requestCode == REQUEST_CODE_CHOOSE){
                    //异步方式插入图片
                    insertImagesSync(data);
                }
            }
        }
    }

    /**
     * 异步方式插入图片
     * @param data
     */
    private void insertImagesSync(final Intent data){
        insertDialog.show();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                try{
                    et_new_content.measure(0, 0);
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    // 可以同时插入多张图片
                    for (Uri imageUri : mSelected) {
                        String imagePath = SDCardUtil.getFilePathFromUri(NewActivity.this,  imageUri);
                        //Log.e(TAG, "###path=" + imagePath);
                        Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath, screenWidth, screenHeight);//压缩图片
                        //bitmap = BitmapFactory.decodeFile(imagePath);
                        imagePath = SDCardUtil.saveToSdCard(bitmap);
                        //Log.e(TAG, "###imagePath="+imagePath);
                        emitter.onNext(imagePath);
                    }

                    // 测试插入网络图片 http://p695w3yko.bkt.clouddn.com/18-5-5/44849367.jpg
                    //subscriber.onNext("http://p695w3yko.bkt.clouddn.com/18-5-5/30271511.jpg");

                    emitter.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        })
        //.onBackpressureBuffer()
        .subscribeOn(Schedulers.io())//生产事件在io
        .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
        .subscribe(new Observer<String>() {
            @Override
            public void onComplete() {
                if (insertDialog != null && insertDialog.isShowing()) {
                    insertDialog.dismiss();
                }
                showToast("图片插入成功");
            }

            @Override
            public void onError(Throwable e) {
                if (insertDialog != null && insertDialog.isShowing()) {
                    insertDialog.dismiss();
                }
                showToast("图片插入失败:"+e.getMessage());
            }

            @Override
            public void onSubscribe(Disposable d) {
                subsInsert = d;
            }

            @Override
            public void onNext(String imagePath) {
                et_new_content.insertImage(imagePath, et_new_content.getMeasuredWidth());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            //如果APP处于后台，或者手机锁屏，则保存数据
            if (CommonUtil.isAppOnBackground(getApplicationContext()) ||
                    CommonUtil.isLockScreeen(getApplicationContext())){
                saveNoteData(true);//处于后台时保存数据
            }

            if (subsLoading != null && subsLoading.isDisposed()){
                subsLoading.dispose();
            }
            if (subsInsert != null && subsInsert.isDisposed()){
                subsInsert.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出处理
     */
    private void dealwithExit(){
        try {
            String noteTitle = et_new_title.getText().toString();
            String noteContent = getEditData();
            String groupName = tv_new_group.getText().toString();
            String noteTime = edit_time.getText().toString();
            if (flag == 0) {//新建笔记
                if (noteTitle.length() > 0 || noteContent.length() > 0) {
                    saveNoteData(false);
                }
            }else if (flag == 1) {//编辑笔记
                if (!noteTitle.equals(myTitle) || !noteContent.equals(myContent)
                        || !groupName.equals(myGroupName) || !noteTime.equals(myNoteTime)) {
                    saveNoteData(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        dealwithExit();
    }

    protected void saveRecord(){//上传到Record表的操作
        BmobDate bmobDate = BmobDate.createBmobDate("yyyy-MM-dd", local_record.getLR_Date());
        Log.i("Bmob","current");
        record.setR_author(currentUser.getObjectId());
        record.setR_date(bmobDate);
        record.setR_text(local_record.getLR_content());
        //record.setR_read(local_record.getLR_read()); 是否可读
        record.setR_title(local_record.getLR_title());

        record.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null) {
                    Log.i("Record", "成功保存到record表中");
                    record_id = objectId;
                    //将本条Record保存到本地
                    local_record.setLR_record_id(record_id);
                    toLocal();
                    //将R_id保存到Link_user表中
                    findL_id();

                }else{
                    Log.i("Record", "没能成功保存到Record表中");
                }
            }
        });
    }

    protected void findL_id(){
        //查找本用户在Link_user表中的L_id
        BmobQuery<Link_user> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_user_id",currentUser.getObjectId());
        bmobQuery.findObjects(new FindListener<Link_user>() {
            @Override
            public void done(List<Link_user> link_user_list, BmobException e) {
                if(e == null){
                    Log.i("bmob", "找L_user_id呀，进来了");
                    String link_id = link_user_list.get(0).getObjectId();
                    //该条记录的record数组中添加record
                    updateLinkUser(link_id);
                }else{
                    Log.i("bmob", "未能找到L_user_id，失败"+e.getMessage());
                }
            }
        });
    }

    protected void updateLinkUser(String link_id){
        //添加记录到Link_user表的record记录中
        Link_user link_user = new Link_user();
        link_user.setObjectId(link_id);
        link_user.addUnique("L_record_id", record_id);
        link_user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.i("bmob","成功加入Link_user");
                }else{
                    Log.i("bmob","失败加入Link_user"+e.getMessage());
                }
            }
        });
    }

    protected void toLocal() {
        local_record.save();
    }

    protected void update_Local(){
        local_record.updateAll("LR_record_id = ?", local_record.getLR_record_id());
    }

    protected void update_Record(){
        record.setR_title(local_record.getLR_title());
        record.setR_text(local_record.getLR_content());
        BmobDate bmobDate = BmobDate.createBmobDate("yyyy-MM-dd", local_record.getLR_Date());
        record.setR_date(bmobDate);
        record.update(record_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null)
                    Log.i("update_Record", "Record表更新成功");
                else{
                    Log.i("update_Record", "Record更新失败");
                }

            }


        });

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
    //TODO *为虚拟用户or真实用户添加一条记录
    private void addu_v_record(String date, String title, String content, Boolean flag, final String u_v_id){
        Record record = new Record();
        User user = BmobUser.getCurrentUser(User.class);
        record.setR_author(user.getNickname());
        BmobDate time = BmobDate.createBmobDate("yyyy-MM-dd",date);
        record.setR_date(time);
        record.setR_title(title);
        record.setR_text(content);
        record.setR_read(flag);
        record.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    //u_v_RecordId = objectId;
                    //TODO
                    add_rec_inArray_1(u_v_id,objectId);
                    add_rec_inArray_2(u_v_id,objectId);
                }else{
                    Log.i("bmob1","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    //复合条件and查询
    //TODO *在link_user_virtual中的记录数组加入单条记录
    private void add_rec_inArray_1(String u_v_id, final String r_id){
        BmobQuery<Link_user_virtual> eq1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        BmobQuery<Link_user_virtual> eq2 = new BmobQuery<>();
        eq1.addWhereEqualTo("L_virtual_id",u_v_id);
        List<BmobQuery<Link_user_virtual>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Link_user_virtual> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> object, BmobException e) {
                if(e==null){
                    if(object.size()!=0){//此用户是虚拟用户
                        String id = object.get(0).getObjectId();
                        Link_user_virtual link_user_virtual = new Link_user_virtual();
                        link_user_virtual.add("L_record_id",r_id);//传入记录ID
                        link_user_virtual.update(id,new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.i("newActivity", "virtual加入单条记录done");
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    Log.i("key","在link_user_virtual中的记录数组加入item成功");
                                }else{
                                    Log.i("error","在link_user_virtual中的记录数组加入item失败："+e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.i("error","查询link_user_virtual表失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }
    private void add_rec_inArray_2(String u_v_id, final String r_id){
        BmobQuery<Link_user2> eq1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        BmobQuery<Link_user2> eq2 = new BmobQuery<>();
        eq1.addWhereEqualTo("L_linked_user_id",u_v_id);
        List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Link_user2> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> object, BmobException e) {
                if(e==null){
                    if(object.size()!=0){//此用户是真实用户
                        String id = object.get(0).getObjectId();
                        Link_user2 link_user2 = new Link_user2();
                        link_user2.add("L_record_id",r_id);//传入记录ID
                        link_user2.update(id,new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Log.i("newActivity", "real加入单条记录done");
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    finish();
                                    Log.i("key","在link_user2中的记录数组加入item成功");
                                }else{
                                    Log.i("error","在link_user2中的记录数组加入item失败："+e.getMessage());
                                }
                            }
                        });
                    }
                }else{
                    Log.i("error","查询Link_user2表失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    //TODO *更新记录
    private void upload_u_v_Record(String birth, String title, String content, Boolean flag,final String RecordId){
        Record record = new Record();
        BmobDate time = BmobDate.createBmobDate("yyyy-MM-dd",birth);
        record.setR_date(time);
        record.setR_title(title);
        record.setR_text(content);
        record.setR_read(flag);
        record.update(RecordId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("key","更新成功");
                }else{
                    Log.i("error","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


}
