package com.in.timelinenested.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.in.timelinenested.Local_Record;
import com.in.timelinenested.R;
import com.in.timelinenested.oderRecord;

import java.util.List;

/**
 * Created by ishratkhan on 24/02/16.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.RvViewHolder> implements View.OnClickListener, View.OnLongClickListener {

  //  List<DataModal> data = new ArrayList<>();
    Context mContext;
 //   private List<Note> mNotes;
    private List<Local_Record> local_records;


    public TimeLineAdapter(Context con) {
        mContext = con;
    }
   // TimeLineAdapter();
   public void setmNotes(List<Local_Record> records) {
       this.local_records = records;
   }


    //设置item监听事件
    private OnMyItemClickListener listener;
    public void setOnMyItemClickListener(OnMyItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.myClick(view, (Local_Record) view.getTag());

        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(listener!=null){
            listener.myLongClick(view, (Local_Record) view.getTag());

        }
        return true;
    }

    public interface OnMyItemClickListener {
        void myClick(View v, Local_Record record);
        void myLongClick(View v, Local_Record record);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item,parent,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new RvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, final int position) {
        final Local_Record record = local_records.get(position);
        holder.tv.setText(record.getLR_Date());
//        holder.setLevel(note.getLevel());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(record);
        holder.title.setText(record.getLR_title());
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
//        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(30,
//                30);
//
//        holder.author1.setLayoutParams(params2);
//        holder.author2.setLayoutParams(params2);
    }

    @Override
    public int getItemCount() {
        if (local_records != null && local_records.size()>0){
            return local_records.size();
        }
        return 0;
    }

    public void addItem(Local_Record item) {
        local_records.add(item);
    }

    class RvViewHolder extends RecyclerView.ViewHolder {
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
