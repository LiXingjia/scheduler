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
import android.widget.Toast;

import com.in.timelinenested.Adapter.VitualFriendAdapter;
import com.in.timelinenested.AddVirtualFriend;
import com.in.timelinenested.FriendSquareActivity;
import com.in.timelinenested.Link_user_virtual;
import com.in.timelinenested.MainActivity;
import com.in.timelinenested.R;
import com.in.timelinenested.activity.SearchNewFriendActivity;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import csy.menu.satellitemenulib.view.SatelliteMenu;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;

public class ContactVirtualeFragment extends Fragment {
    private SatelliteMenu mSatelliteMenuRightBottom;//菜单
    private RecyclerView recycler;
    private VitualFriendAdapter vitualFriendAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<User_virtual> user_virtuals;
    int SPLASH_DISPLAY_LENGHT = 3000;
    private Context context;
    private com.in.timelinenested.viewClass.LoadingDialog dialog1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_contact_group, container,false);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);

        user_virtuals=new ArrayList<>();
        vitualFriendAdapter = new VitualFriendAdapter(this.getActivity());
        //获取虚拟用户列表
        context = this.getActivity();
      //  getU_V_List();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < user_virtuals.size(); i++) {
//                    Log.v("key", user_virtuals.get(i).getU_virtual_name());
//
//
//                }
//            }
//        }, SPLASH_DISPLAY_LENGHT);
        Log.i("contactVirtual", "onCreateView: "+user_virtuals.size());
        init(view);

        return view;
    }
    public void init(View view){

        vitualFriendAdapter.setUser_virtualList(user_virtuals);

        mSatelliteMenuRightBottom = (SatelliteMenu) view.findViewById(R.id.Contact_menu);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(vitualFriendAdapter);


        //菜单点击事件
        List<Integer> imageResourceRightBottom = new ArrayList<>();//菜单图片,可根据需要设置子菜单个数
        imageResourceRightBottom.add(R.drawable.icon_addvirtualperson);
        imageResourceRightBottom.add(R.drawable.icon_addrealperson);
        imageResourceRightBottom.add(R.drawable.main_page);
//
        mSatelliteMenuRightBottom.getmBuilder()
                .setMenuImage(R.drawable.menu)
                .setMenuItemImageResource(imageResourceRightBottom)
                .setOnMenuItemClickListener(new SatelliteMenu.OnMenuItemClickListener() {
                    @Override
                    public void onClick(View view, int postion) {
                        switch (postion) {
                            case 0: {
                                Toast.makeText(ContactVirtualeFragment.this.getActivity(), "添加虚拟好友", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactVirtualeFragment.this.getActivity(), AddVirtualFriend.class);
                                //intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                Toast.makeText(ContactVirtualeFragment.this.getActivity(), "添加真实用户", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactVirtualeFragment.this.getActivity(), SearchNewFriendActivity.class);
                                intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }
                            case 2:{
                                // Toast.makeText(ContactMainFragment.this.getActivity(), , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactVirtualeFragment.this.getActivity(), MainActivity.class);
                                //       intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }


                        }
                    }
                })
                .creat();

        //item监听事件，跳转到朋友空间界面
        vitualFriendAdapter.setOnMyItemClickListener(new VitualFriendAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, User_virtual user_virtual) {
                Intent intent=new Intent(ContactVirtualeFragment.this.getActivity(),FriendSquareActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",user_virtual);
                bundle.putSerializable("flag",0);
                intent.putExtra("data", bundle);
                startActivity(intent);
                //   Toast.makeText(ContactMainFragment.this.getActivity(),"点击了第"+pos,Toast.LENGTH_SHORT);
            }

            @Override
            public void myLongClick(View v,final User_virtual user_virtual) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactVirtualeFragment.this.getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定将好友移到小黑屋？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //在数据库删除联系人，删除好友空间，笔记,提示删除成功，并刷新联系人列表
                        black_UV(user_virtual);
//                        Toast.makeText(ContactMainFragment.this.getActivity(),"删除成功",Toast.LENGTH_SHORT);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

            }

        });

    }
private void getU_V_List(){
    BmobQuery<Link_user_virtual> eq1 = new BmobQuery<>();
    User user = BmobUser.getCurrentUser(User.class);
    eq1.addWhereEqualTo("L_user_id",user.getObjectId());
    BmobQuery<Link_user_virtual> eq2 = new BmobQuery<>();
    eq2.addWhereEqualTo("L_black",false);
    List<BmobQuery<Link_user_virtual>> andQuerys = new ArrayList<>();
    andQuerys.add(eq1);
    andQuerys.add(eq2);
    BmobQuery<Link_user_virtual> query = new BmobQuery<>();
    query.and(andQuerys);
    query.findObjects(new FindListener<Link_user_virtual>() {
        @Override
        public void done(List<Link_user_virtual> list, BmobException e) {
            if(e==null){
                List<String> u_v_ids = new ArrayList<>();
                for (int i = 0; i <list.size() ; i++) {
                    u_v_ids.add(list.get(i).getL_virtual_id());
                }
                //user_v_size = u_v_ids.size();
                if(user_virtuals!=null){user_virtuals.clear();}
                for (int j = 0; j < u_v_ids.size(); j++) {
                    Log.e("key", "执行到第"+j+"次循环");
                    BmobQuery<User_virtual> categoryBmobQuery = new BmobQuery<>();
                    categoryBmobQuery.addWhereEqualTo("objectId", u_v_ids.get(j));
                    categoryBmobQuery.setLimit(50).findObjects(new FindListener<User_virtual>() {
                        @Override
                        public void done(List<User_virtual> object, BmobException e) {
                            if (e == null) {
                                user_virtuals.add(object.get(0));
                                Log.e("key", "将虚拟用户价入user_virtuals表成功");
                                vitualFriendAdapter.setUser_virtualList(user_virtuals);
                               vitualFriendAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("error", "将虚拟用户加入user_virtuals表失败："+e.toString());
                            }
                        }
                    });
                }
            }else{
                Log.v("error","Link_user_virtual检索失败："+e.getMessage());
            }
        }
    });
}
    protected void black_UV(final User_virtual user_virtual){
        BmobQuery<Link_user_virtual> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("L_virtual_id", user_virtual.getObjectId());
        bmobQuery.findObjects(new FindListener<Link_user_virtual>() {
            @Override
            public void done(List<Link_user_virtual> list, BmobException e) {
                if(e == null){
                    Link_user_virtual link_user_virtual = new Link_user_virtual();
                    link_user_virtual.setL_black((Boolean.TRUE));//TODO 从true改为false就是从小黑屋拉出来
                    link_user_virtual.update(list.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Log.i("Black_UV", "拉入小黑屋成功");

                                dialog1 = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                                dialog1.setSuccessful("已将该好友锁到小黑屋！");
                                dialog1.show();
                                handler.sendEmptyMessageDelayed(CLOSE, 2000);
                                refreshVirtualList();

//                        }

                            }else{
                                Log.i("Black_UV", "拉入小黑屋失败");
                            }
                        }
                    });
                }else
                    Log.i("Find_L_Virtual_id", "没有找到L+virtual");
            }
        });
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            refreshVirtualList();
        } else {
            //相当于Fragment的onPause
        }
    }

    private void refreshVirtualList(){
        if(user_virtuals.size()!=0){
            user_virtuals.clear();
        }
        getU_V_List();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <user_virtuals.size() ; i++) {
//                    Log.v("key",user_virtuals.get(i).getU_virtual_name());
//                }
//                Log.i("contactVitual", "refreshNoteList: 刷新列表");
//                vitualFriendAdapter.setUser_virtualList(user_virtuals);
//       //         mContactScrollerAdapter.setUsers(user_virtuals);
//            }
//        },SPLASH_DISPLAY_LENGHT);


    }
}
