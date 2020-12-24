package com.in.timelinenested.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.in.timelinenested.Local_Record;
import com.in.timelinenested.R;
import com.in.timelinenested.oderRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by skyler on 2019/3/12.
 */

public class FriendTimeLineAdapter extends RecyclerView.Adapter<FriendTimeLineAdapter.RvViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private List<oderRecord> oderRecords;
    Context mContext;
    public FriendTimeLineAdapter(Context con) {
        mContext = con;
    }
    // TimeLineAdapter();
    public void setmNotes(List<oderRecord> oderRecords) {
        this.oderRecords = oderRecords;
    }


    //设置item监听事件
    private OnMyItemClickListener listener;
    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.myClick(view, (oderRecord) view.getTag());

        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(listener!=null){
            listener.myLongClick(view, (oderRecord) view.getTag());

        }
        return true;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, oderRecord record);
        void myLongClick(View v, final oderRecord record);
    }
    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item,parent,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new RvViewHolder(view);
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
    @Override
    public void onBindViewHolder(@NonNull FriendTimeLineAdapter.RvViewHolder holder, int position) {
        final oderRecord oderRecord = oderRecords.get(position);
        Log.i("FriendAdapter", "onBindViewHolder: "+oderRecord.getoR_date());
        long t = BmobDate.getTimeStamp(oderRecord.getoR_date().getDate());
        holder.tv.setText(getDateToString(t,"yyyy-MM-dd"));
//        holder.setLevel(note.getLevel());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(oderRecord);
        holder.title.setText(oderRecord.getoR_title());
        //note中author是个string，通过这个string在数据库中找到用户头像并加载

        //  holder.author1.setImageDrawable(note.getAuthor1());
        /*//点击事件
        if (listener!=null) {
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.myClick(v, position);
                }
            });
        }*/
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        if (oderRecords != null && oderRecords.size()>0){
            return oderRecords.size();
        }
        return 0;
    }



    public class RvViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        View itemView;
        View marker;
        TextView title;
        ImageView author1,author2;

        public RvViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv = (TextView) itemView.findViewById(R.id.rv_item_tv);
            marker =itemView.findViewById(R.id.marker);
            title=itemView.findViewById(R.id.rv_item_title);
            author1=itemView.findViewById(R.id.author1);
            author2=itemView.findViewById(R.id.author2);
    }
}
}
