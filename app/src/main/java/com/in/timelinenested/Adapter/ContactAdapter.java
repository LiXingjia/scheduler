package com.in.timelinenested.Adapter;


import com.in.timelinenested.R;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.mock.Section;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
        implements View.OnClickListener, View.OnLongClickListener{
    private LayoutInflater mInflater;
    private ContactScrollerAdapter mContactScrollerAdapter;
    private Context mContext;
    private User user;

    private List<User> users;



    //设置item监听事件
    private OnMyItemClickListener listener;

    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {
            if (listener != null) {
                //注意这里使用getTag方法获取数据
                listener.myClick(view, (User) view.getTag());
            }

        }


    @Override
    public boolean onLongClick(View view) {
            if (listener != null) {
                //注意这里使用getTag方法获取数据
                listener.myLongClick(view, (User) view.getTag());
            }

        return true;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, User user);
        void myLongClick(View v, User user);
    }

    public ContactAdapter(Context c ) {
  //     mContacts = contacts;
      mInflater = LayoutInflater.from(c);
   //     mContactScrollerAdapter = contactScrollerAdapter;
    }
    public void setUserList(List<User> users) {
        this.users = users;
    }




    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View contact = mInflater.inflate(R.layout.view_contact, parent, false);//view_contact是单个联系人组件
        contact.setOnClickListener(this);
        contact.setOnLongClickListener(this);
        return new ContactViewHolder(contact);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        //Contact contact = mContacts.get(position);
        User user=users.get(position);
        holder.itemView.setTag(user);
        holder.mName.setText(user.getNickname());
   //     Log.i("Contactadapter", "onBindViewHolder: "+user.getNickname());
        final ImageView mImageView=holder.pic;
  //      Log.i("Contactadapter", "onBindViewHolder: "+user.getU_pic_url());
        final String IMAGE_URL=user.getU_pic_url().getFileUrl();
                //"http://bmob-cdn-23983.b0.upaiyun.com/2019/03/12/89c1d8827ef34dc6bea35f0a19b89d03.png";
                //user.getU_pic_url().getFileUrl();

        new Thread(new Runnable(){

            @Override
            public void run() {
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                // post() 特别关键，就是到UI主线程去更新图片
                mImageView.post(new Runnable(){
                    @Override
                    public void run() {
                        //  Auto-generated method stub
                        mImageView.setImageDrawable(drawable) ;
                    }}) ;
            }

        }).start()  ;

     //   holder.pic=mImageView;
    //    holder.mName.setText(String.format(Locale.US, NAME_FORMAT, contact.getFirstName()));
 //       Section s = mContactScrollerAdapter.fromItemIndex(position);
//        if (s.getIndex() == position) {
//            holder.title.setText(s.getTitle());
//        } else {
//            holder.title.setText("");


        //点击事件

    }




    @Override
    public int getItemCount() {
        return users.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView title;//首字母
        private ImageView pic;
        private TextView mName;
        private RelativeLayout whole_person;

        public ContactViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_index);
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
