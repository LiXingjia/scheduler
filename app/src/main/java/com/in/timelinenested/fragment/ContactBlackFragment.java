package com.in.timelinenested.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.in.timelinenested.Adapter.BlackFriendAdapter;
import com.in.timelinenested.FriendSquareActivity;
import com.in.timelinenested.Link_user2;
import com.in.timelinenested.Link_user_virtual;
import com.in.timelinenested.R;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.tempUser;

import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;

public class ContactBlackFragment extends Fragment {

    private RecyclerView recycler;
    private BlackFriendAdapter blackFriendAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<tempUser> mTempUsersList ;//黑屋用户的个人信息数组
    private List<tempUser> mTempUsersList1 ;//黑屋用户的个人信息数组
    private List<tempUser> mTempUsersList2 ;//黑屋用户的个人信息数组
    List<String> u_v_ids = new ArrayList<>();//全局黑屋用户ID数组
    int user_v_size = 0;// 黑屋中虚拟用户有多少
    private final int SPLASH_DISPLAY_LENGHT = 2000; //延迟6秒
    private com.in.timelinenested.viewClass.LoadingDialog dialog1;
    private Context context;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_black, container,false);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        blackFriendAdapter = new BlackFriendAdapter(this.getActivity());
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        context = this.getActivity();
        mTempUsersList = new ArrayList<>();
//        getUserInBlackList();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getU_B_inTempUser();
//            }
//        },SPLASH_DISPLAY_LENGHT);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                blackFriendAdapter.setUserList(mTempUsersList);
//                recycler.setLayoutManager(mLayoutManager);
//                recycler.setAdapter(blackFriendAdapter);
//
//            }
//        },SPLASH_DISPLAY_LENGHT);
        blackFriendAdapter.setUserList(mTempUsersList);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(blackFriendAdapter);


     //   item监听事件，跳转到朋友空间界面
        blackFriendAdapter.setOnMyItemClickListener(new BlackFriendAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, tempUser user) {

            }

            @Override
            public void myLongClick(View v, final tempUser user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactBlackFragment.this.getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定将好友移出小黑屋？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //在数据库删除联系人，删除好友空间，笔记,提示删除成功，并刷新联系人列表
                        //   findL_UU_id(user.getObjectId());
//                        Toast.makeText(ContactMainFragment.this.getActivity(),"删除成功",Toast.LENGTH_SHORT);
                        //TODO 在此添加方法
                        Nblack(user);

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        return view;

    }
    protected void Nblack_UV(tempUser tempFriend){
        BmobQuery<Link_user_virtual> bmobQuery1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        bmobQuery1.addWhereEqualTo("L_user_id", user.getObjectId());
        BmobQuery<Link_user_virtual> bmobQuery2 = new BmobQuery<>();
        bmobQuery2.addWhereEqualTo("L_virtual_id", tempFriend.getUserId());
        List<BmobQuery<Link_user_virtual>> andQuery = new ArrayList<BmobQuery<Link_user_virtual>>();
        andQuery.add(bmobQuery1);
        andQuery.add(bmobQuery2);

        BmobQuery<Link_user_virtual> query = new BmobQuery<Link_user_virtual>();
        query.and(andQuery);
        query.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> list, BmobException e) {
                if(e==null){
                    Log.i("L_UV_black", "查询L_UV成功");
                    Link_user_virtual link_user2 = new Link_user_virtual();
                    link_user2.setL_black(Boolean.FALSE);
                    link_user2.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Log.i("Black_UV", "拉出小黑屋成功");

                                dialog1 = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                                dialog1.setSuccessful("已将该好友移出小黑屋！");
                                dialog1.show();
                                handler.sendEmptyMessageDelayed(CLOSE, 2000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshcontactList();
                                    }
                                }, 2000);

                            }else{
                                Log.i("Black_UV", "拉出小黑屋失败"+e.getMessage());
                            }
                        }
                    });
                }else
                    Log.i("Nblack_UV","没有成功找到L_UV");
            }
        });

    }
    protected void Nblack_UU(tempUser tempFriend){
        BmobQuery<Link_user2> bmobQuery1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        bmobQuery1.addWhereEqualTo("L_user_id", user.getObjectId());
        BmobQuery<Link_user2> bmobQuery2 = new BmobQuery<>();
        bmobQuery2.addWhereEqualTo("L_linked_user_id", tempFriend.getUserId());
        List<BmobQuery<Link_user2>> andQuery = new ArrayList<BmobQuery<Link_user2>>();
        andQuery.add(bmobQuery1);
        andQuery.add(bmobQuery2);

        BmobQuery<Link_user2> query = new BmobQuery<Link_user2>();
        query.and(andQuery);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> list, BmobException e) {
                if(e==null){
                    Log.i("L_UU_black", "查询L_UU成功");
                    Link_user2 link_user2 = new Link_user2();
                    link_user2.setL_black(Boolean.FALSE);
                    link_user2.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Log.i("Black_UU", "拉出小黑屋成功");

                                dialog1 = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                                dialog1.setSuccessful("已将该好友移出小黑屋！");
                                dialog1.show();
                                handler.sendEmptyMessageDelayed(CLOSE, 2000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshcontactList();
                                    }
                                }, 2000);

                            }else{
                                Log.i("Black_UU", "拉出小黑屋失败"+e.getMessage());
                            }
                        }
                    });
                }else
                    Log.i("Nblack_UU","没有成功找到L_UU");
            }
        });

    }
    protected void Nblack(final tempUser temFriend){
        BmobQuery<Link_user2> bmobQuery = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        bmobQuery.addWhereEqualTo("L_user_id", user.getObjectId());
        BmobQuery<Link_user2> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.addWhereEqualTo("L_linked_user_id", temFriend.getUserId());
        List<BmobQuery<Link_user2>> andQuery = new ArrayList<BmobQuery<Link_user2>>();
        andQuery.add(bmobQuery);
        andQuery.add(bmobQuery1);
        BmobQuery<Link_user2> query = new BmobQuery<Link_user2>();
        query.and(andQuery);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> list, BmobException e) {
                if(e == null){
                    if(list.size() == 0){
                        Nblack_UV(temFriend);
                    }else
                        Nblack_UU(temFriend);

                }
            }
        });
    }

    private void getUserInBlackList(){
        BmobQuery<Link_user_virtual> eq1 = new BmobQuery<>();
        final User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        BmobQuery<Link_user_virtual> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("L_black",true);
        List<BmobQuery<Link_user_virtual>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Link_user_virtual> query = new BmobQuery<>();
        query.and(andQuerys);
        query.setLimit(50).findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> list, BmobException e) {
                if(e==null){
                    user_v_size = list.size();
                    if(u_v_ids.size()!=0){
                        u_v_ids.clear();
                    }
                    for (int i = 0; i <list.size() ; i++) {
                        u_v_ids.add(list.get(i).getL_virtual_id());
                    }
                    BmobQuery<Link_user2> eq1 = new BmobQuery<>();
                    final User user = BmobUser.getCurrentUser(User.class);
                    eq1.addWhereEqualTo("L_user_id",user.getObjectId());
                    BmobQuery<Link_user2> eq2 = new BmobQuery<>();
                    eq2.addWhereEqualTo("L_black",true);
                    List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
                    andQuerys.add(eq1);
                    andQuerys.add(eq2);
                    BmobQuery<Link_user2> query = new BmobQuery<>();
                    query.and(andQuerys);
                    query.setLimit(50).findObjects(new FindListener<Link_user2>() {
                        @Override
                        public void done(List<Link_user2> list, BmobException e) {
                            if(e==null){
                                for (int i = 0; i <list.size(); i++) {
                                    u_v_ids.add(list.get(i).getL_linked_user_id());
                                }
                                for (int j = 0; j < u_v_ids.size(); j++) {
                                    if(j < user_v_size){
                                        BmobQuery<User_virtual> categoryBmobQuery = new BmobQuery<>();
                                        categoryBmobQuery.addWhereEqualTo("objectId", u_v_ids.get(j));
                                        categoryBmobQuery.findObjects(new FindListener<User_virtual>() {
                                            @Override
                                            public void done(List<User_virtual> object, BmobException e) {
                                                if (e == null) {
                                                    if(object.size()!=0){
                                                        User_virtual userVirtual = object.get(0);
                                                        tempUser tempuser1=new tempUser();
                                                        tempuser1.setName(userVirtual.getU_virtual_name());

                                                        tempuser1.setBirth(userVirtual.getU_birth());
                                                        tempuser1.setUserId(userVirtual.getObjectId());
                                                        tempuser1.setPic(userVirtual.getU_pic_url());
                                                        Log.i("temp1", "done: "+tempuser1.getName()+" "+tempuser1.getUserId()+" "+tempuser1.getPic());
                                                        mTempUsersList.add(tempuser1);
                                                        blackFriendAdapter.setUserList(mTempUsersList);
                                                        blackFriendAdapter.notifyDataSetChanged();
                                                    }

                                                } else {
                                                    Log.e("error", "查询User_virtual表失败："+e.toString());
                                                }
                                            }
                                        });
                                    }else{
                                        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
                                        categoryBmobQuery.addWhereEqualTo("objectId", u_v_ids.get(j));
                                        categoryBmobQuery.findObjects(new FindListener<User>() {
                                            @Override
                                            public void done(List<User> object, BmobException e) {
                                                if (e == null) {
                                                    if(object.size()!=0){
                                                        User user = object.get(0);
                                                        tempUser tempuser2=new tempUser();
                                                       tempuser2.setName(user.getNickname());
                                                        tempuser2.setBirth(user.getU_birth());
                                                        tempuser2.setPic(user.getU_pic_url());
                                                        tempuser2.setUserId(user.getObjectId());
                                                        Log.i("temp2", "done: "+tempuser2.getName()+" "+tempuser2.getUserId()+" "+tempuser2.getPic());
                                                        mTempUsersList.add(tempuser2);
                                                        blackFriendAdapter.setUserList(mTempUsersList);
                                                        blackFriendAdapter.notifyDataSetChanged();
                                                    }
                                                } else {
                                                    Log.e("error", "查询User表失败："+e.toString());
                                                }
                                            }
                                        });
                                    }
                                }
                            }else{
                                Log.v("error","Link_user2检索失败："+e.getMessage());
                            }
                        }
                    });
                }else{
                    Log.v("error","Link_user_virtual检索失败："+e.getMessage());
                }
            }
        });
    }
    private void addtempUser(String id, String name, BmobDate time, BmobFile pic) {
        tempUser tempuser = new tempUser();
        tempuser.setUserId(id);
        tempuser.setName(name);
        tempuser.setBirth(time);
        tempuser.setPic(pic);
        tempuser.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.e("key", "在tempUser表中新建用户成功，新建用户的ID："+objectId);
                } else {
                    Log.e("error", "在tempUser表中新建用户失败："+e.toString());
                }
            }
        });
    }
    private void getU_B_inTempUser(){
        BmobQuery<tempUser> r1 = new BmobQuery<>();
        r1.order("name").setLimit(100)
                .findObjects(new FindListener<tempUser>() {
                    @Override
                    public void done(List<tempUser> list, BmobException e) {
                        if(e==null){
                            if(mTempUsersList.size()!=0){
                                mTempUsersList.clear();}
                                mTempUsersList.addAll(list);
                            for (int i = 0; i < list.size() ; i++) {
                                Log.e("key", list.get(i).getName());
                            }
                            deleteTempUser(list);
                        }else{
                            Log.e("error", "查询tempUser表失败："+e.toString());
                        }
                    }
                });
    }
    private void deleteTempUser(List<tempUser> t_u_list) {
        List<BmobObject> tempUsers = new ArrayList<>();
        for (int i = 0; i < t_u_list.size(); i++) {
            tempUser t_u = new tempUser();
            t_u.setObjectId(t_u_list.get(i).getObjectId());
            tempUsers.add(t_u);
        }
        new BmobBatch().deleteBatch(tempUsers).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> results, BmobException e) {
                if (e == null) {
                    Log.v("key","删除新的tempUser表中的全部数据成功！");
                } else {
                    Log.v("error","删除新的tempUser表中的全部数据失败:"+e.getMessage());
                }
            }
        });
    }
    private void refreshcontactList(){


        if(mTempUsersList.size()!=0){
            mTempUsersList.clear();
        }
        getUserInBlackList();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <mTempUsersList.size() ; i++) {
//                    Log.v("1st handler",mTempUsersList.get(i).getName());
//                }
//                getU_B_inTempUser();
//
//            }
//        },SPLASH_DISPLAY_LENGHT);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <mTempUsersList.size() ; i++) {
//                    Log.v("2nd handler",mTempUsersList.get(i).getName());
//                }
//                Log.i("刷新", "run: "+mTempUsersList.size());
//                blackFriendAdapter.setUserList(mTempUsersList);
//                blackFriendAdapter.notifyDataSetChanged();
//            }
//        },4000);

        //timeLineAdapter.setmNotes(recordList);
        // timeLineAdapter.notifyDataSetChanged();

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            refreshcontactList();
        } else {
            //相当于Fragment的onPause
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_OUT:
                    dialog1.setRunTimeOut("超时了，检查一下网络哦");
                    handler.sendEmptyMessageDelayed(CLOSE,2000);
                    break;
                case  CLOSE:
                    dialog1.close();
                    break;
            }        }
    };


}
