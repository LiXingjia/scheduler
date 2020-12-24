package com.in.timelinenested.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.in.timelinenested.R;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.mock.Section;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by skyler on 2019/3/11.
 */

public class VitualFriendAdapter extends RecyclerView.Adapter<VitualFriendAdapter.ContactViewHolder> implements View.OnClickListener, View.OnLongClickListener{

    private LayoutInflater mInflater;
    private Context mContext;
    private User_virtual user_virtual;
    private List<User_virtual> user_virtuals;
    private OnMyItemClickListener listener;
    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }
    @Override
    public void onClick(View view) {
        if (listener != null) {
            //注意这里使用getTag方法获取数据
            listener.myClick(view, (User_virtual) view.getTag());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (listener != null) {
            //注意这里使用getTag方法获取数据
            listener.myLongClick(view, (User_virtual) view.getTag());
        }
        return true;
    }
    public interface OnMyItemClickListener {
        void myClick(View v, User_virtual user);
        void myLongClick(View v, User_virtual user);
    }
    public void setUser_virtualList(List<User_virtual> user_virtuals) {
        this.user_virtuals = user_virtuals;
    }
    public VitualFriendAdapter(Context c) {
        //     mContacts = contacts;
        mInflater = LayoutInflater.from(c);
    //    mContactScrollerAdapter = contactScrollerAdapter;
    }

    @NonNull
    @Override
    public VitualFriendAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View contact = mInflater.inflate(R.layout.view_contact, parent, false);//view_contact是单个联系人组件
        contact.setOnClickListener(this);
        contact.setOnLongClickListener(this);
        return new ContactViewHolder(contact);
    }

    @Override
    public void onBindViewHolder( ContactViewHolder holder, int position) {
        User_virtual user_virtual=user_virtuals.get(position);
        holder.itemView.setTag(user_virtual);
        holder.mName.setText(user_virtual.getU_virtual_name());
        final ImageView mImageView=holder.pic;
        final String IMAGE_URL;
        if(user_virtual.getU_pic_url()==null){
            IMAGE_URL="http://bmob-cdn-23983.b0.upaiyun.com/2019/03/12/e761ed3d1b9141bb9c70fec302d8b8a1.jpg";
        }else{
        IMAGE_URL=user_virtual.getU_pic_url().getFileUrl();}
        Log.i("vitualFiAdapter", "onBindViewHolder: "+user_virtual.getU_virtual_name()+user_virtual.getU_pic_url());

        new Thread(new Runnable(){

            @Override
            public void run() {
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                // post() 特别关键，就是到UI主线程去更新图片
                mImageView.post(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mImageView.setImageDrawable(drawable) ;
                    }}) ;
            }

        }).start()  ;

    }


    @Override
    public int getItemCount() {
        return user_virtuals.size();
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder{
        private ImageView pic;
        private TextView mName;
        private RelativeLayout whole_person;

        public ContactViewHolder(View itemView) {
            super(itemView);
          //  title = (TextView) itemView.findViewById(R.id.title_index);
            pic = (ImageView) itemView.findViewById(R.id.contact_img);
            mName = (TextView) itemView.findViewById(R.id.contact_name);
            whole_person=itemView.findViewById(R.id.contact_person);


        }
    }
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
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
