package com.in.timelinenested;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.in.timelinenested.activity.FragmentActivity;

import cn.bmob.v3.BmobUser;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;
import static com.zhihu.matisse.internal.utils.PathUtils.getDataColumn;

public class SetUpActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout background,contact_us,my_background;
    Button quit;
    private com.in.timelinenested.viewClass.LoadingDialog dialog;
    private Context context;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
            if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},0);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.main));
        init();
    }

    private void init() {
        context = this;
        background = findViewById(R.id.background);
        contact_us = findViewById(R.id.contact_us);
        quit = findViewById(R.id.bt_quit);

        background.setOnClickListener(this);
        contact_us.setOnClickListener(this);
        quit.setOnClickListener(this);
        tv_back = findViewById(R.id.tv_back);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.back);
        tv_back.setCompoundDrawables(drawable,null,null,null);
        tv_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.background: {

                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                }
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.contact_us: {
                dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                dialog.setSuccessful("请通过邮箱联系我们：\n"+"596114249.qq.com");
                dialog.show();
                handler.sendEmptyMessageDelayed(CLOSE, 5000);
                break;
            }
            case R.id.bt_quit: {
                BmobUser.logOut();
                startActivity(new Intent(SetUpActivity.this, LogActivity.class));
            }
            case R.id.tv_back:
                finish();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode==1 && data!=null) {
            Uri uri = data.getData();
            String path =  getPath(this,uri);
//
            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            iv_bg.setImageBitmap(bitmap);

            Drawable  drawable = new BitmapDrawable(getResources(),bitmap);
            // Drawable drawable = Drawable.createFromPath(path);

            EventManager.post(Appconfig.EVENT_MAIN1,drawable);

//            my_background.setBackground(drawable);
            dialog = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
            dialog.setSuccessful("背景更换成功！");
            dialog.show();
            handler.sendEmptyMessageDelayed(CLOSE, 2000);

        }
        Intent intent = new Intent(this, MainActivity.class);
        //intent.setClass();
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri)
    {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);

            }

        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {
            return getDataColumn(context, uri, null, null);

        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();

        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs)
    {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);

            }

        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }

        }
        return null;

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */

    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */

    public static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */

    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_OUT:
                    dialog.setRunTimeOut("超时了，检查一下网络哦");
                    handler.sendEmptyMessageDelayed(CLOSE,2000);
                    break;
                case  CLOSE:
                    dialog.close();
                    break;
            }        }
    };
}
