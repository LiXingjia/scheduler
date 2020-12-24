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

import com.in.timelinenested.Adapter.ContactAdapter;
import com.in.timelinenested.Adapter.ContactScrollerAdapter;
import com.in.timelinenested.AddVirtualFriend;
import com.in.timelinenested.Link_user2;
import com.in.timelinenested.Local_Record;
import com.in.timelinenested.MainActivity;
import com.in.timelinenested.activity.FragmentActivity;
import com.in.timelinenested.FriendSquareActivity;
import com.in.timelinenested.R;
import com.in.timelinenested.activity.SearchNewFriendActivity;
import com.in.timelinenested.bean.User;
import com.in.timelinenested.mock.Contact;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cdflynn.android.library.scroller.BubbleScroller;
import cdflynn.android.library.scroller.ScrollerListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import csy.menu.satellitemenulib.view.SatelliteMenu;

import static android.icu.lang.UCharacter.BidiPairedBracketType.CLOSE;
import static android.net.sip.SipErrorCode.TIME_OUT;

public class ContactMainFragment extends Fragment {
    private SatelliteMenu mSatelliteMenuRightBottom;//菜单

  //  private BubbleScroller scroller;
    private RecyclerView recycler;

  //  private ContactScrollerAdapter mContactScrollerAdapter;
    private ContactAdapter mContactAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean mProgrammaticScroll = true;
    //  private List<Contact> contactList;
    private List<User> users;
    int SPLASH_DISPLAY_LENGHT = 3000;
    private Context context;
    private com.in.timelinenested.viewClass.LoadingDialog dialog1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contact_main, container, false);
    //    scroller = (BubbleScroller) view.findViewById(R.id.bubble_scroller);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
    //    mContactScrollerAdapter = new ContactScrollerAdapter();
        mContactAdapter = new ContactAdapter(this.getActivity());
        context = this.getActivity();


        users = new ArrayList<>();
         //获取用户列表
//        getU_List();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < users.size(); i++) {
//                    Log.v("key", users.get(i).getNickname());
//
//                    init(view);
//                }
//            }
//        }, SPLASH_DISPLAY_LENGHT);
//        Log.i("contactMain", "onCreateView: "+users.size());
//   //     init(view);
        init(view);
        return view;
    }

    public void init(View view){

        mContactAdapter.setUserList(users);
   //     mContactScrollerAdapter.setUsers(users);

        mSatelliteMenuRightBottom = (SatelliteMenu) view.findViewById(R.id.Contact_menu);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
  //      scroller.setScrollerListener(mScrollerListener);
  //      scroller.setSectionScrollAdapter(mContactScrollerAdapter);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setAdapter(mContactAdapter);
     //   scroller.showSectionHighlight(0);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mProgrammaticScroll) {
                    mProgrammaticScroll = false;
                    return;
                }
                final int firstVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
       //         scroller.showSectionHighlight(
                    //    mContactScrollerAdapter.sectionFromPosition(firstVisibleItemPosition));
            }
        });

        //菜单点击事件
        List<Integer> imageResourceRightBottom = new ArrayList<>();//菜单图片,可根据需要设置子菜单个数
        imageResourceRightBottom.add(R.drawable.icon_addvirtualperson);
        imageResourceRightBottom.add(R.drawable.icon_addrealperson);
        imageResourceRightBottom.add(R.drawable.main_page);
//        imageResourceRightBottom.add(R.drawable.icon_group);
        mSatelliteMenuRightBottom.getmBuilder()
                .setMenuImage(R.drawable.menu)
                .setMenuItemImageResource(imageResourceRightBottom)
                .setOnMenuItemClickListener(new SatelliteMenu.OnMenuItemClickListener() {
                    @Override
                    public void onClick(View view, int postion) {
                        switch (postion) {
                            case 0: {
                                Toast.makeText(ContactMainFragment.this.getActivity(), "添加虚拟好友", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactMainFragment.this.getActivity(), AddVirtualFriend.class);
                                //intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                Toast.makeText(ContactMainFragment.this.getActivity(), "添加真实用户", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactMainFragment.this.getActivity(), SearchNewFriendActivity.class);
                                intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }
                            case 2:{
                               // Toast.makeText(ContactMainFragment.this.getActivity(), , Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ContactMainFragment.this.getActivity(), MainActivity.class);
                         //       intent.putExtra("flag", 0);
                                startActivity(intent);
                                break;
                            }

//

                        }
                    }
                })
                .creat();

        //item监听事件，跳转到朋友空间界面
        mContactAdapter.setOnMyItemClickListener(new ContactAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, User user) {
                Intent intent=new Intent(ContactMainFragment.this.getActivity(),FriendSquareActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",user);
                bundle.putSerializable("flag",1);//传进朋友空间一个user类并表示是真实好友

                intent.putExtra("data", bundle);
                startActivity(intent);
             //   Toast.makeText(ContactMainFragment.this.getActivity(),"点击了第"+pos,Toast.LENGTH_SHORT);
            }
            @Override
            public void myLongClick(View v, final  User user) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactMainFragment.this.getActivity());
                builder.setTitle("提示");
                builder.setMessage("确定将好友移到小黑屋？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //在数据库删除联系人，删除好友空间，笔记,提示删除成功，并刷新联系人列表
                        findL_UU_id(user.getObjectId());
//                        Toast.makeText(ContactMainFragment.this.getActivity(),"删除成功",Toast.LENGTH_SHORT);
                        dialog1 = new com.in.timelinenested.viewClass.LoadingDialog(context, "");
                        dialog1.setSuccessful("已将该好友锁到小黑屋！");
                        dialog1.show();
                        handler.sendEmptyMessageDelayed(CLOSE, 2000);
                        //int ret = noteDao.deleteNote(note.getId());
//                        if (ret > 0){
//                            Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT);
//                            //TODO 删除笔记成功后，记得删除图片（分为本地图片和网络图片）
//                            //获取笔记中图片的列表 StringUtils.getTextFromHtml(note.getContent(), true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshcontactList();
                            }
                        }, 2000);
//                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

            }
        });



    }
    protected void findL_UU_id(String linked_id){
        BmobQuery<Link_user2> bmobQuery1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        bmobQuery1.addWhereEqualTo("L_user_id", user.getObjectId());
        BmobQuery<Link_user2> bmobQuery2 = new BmobQuery<>();
        bmobQuery2.addWhereEqualTo("L_linked_user_id", linked_id);
        List<BmobQuery<Link_user2>> andQuery = new ArrayList<BmobQuery<Link_user2>>();
        andQuery.add(bmobQuery1);
        andQuery.add(bmobQuery2);

        BmobQuery<Link_user2> query = new BmobQuery<Link_user2>();
        query.and(andQuery);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> list, BmobException e) {
                if(e == null){
                    String L_UU_id = list.get(0).getObjectId();
                    Log.i("L_UU_black", "查询L_UU成功"+L_UU_id);
                    black_UU(L_UU_id);
                }else{
                    Log.i("L_UU_black","查询L_UU失败");
                }
            }
        });
    }
    protected void black_UU(String L_UU_id){
        Link_user2 link_user2 = new Link_user2();
        link_user2.setL_black(Boolean.TRUE);
        link_user2.update(L_UU_id, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.i("Black_UU", "拉入小黑屋成功");
                }else{
                    Log.i("Black_UU", "拉入小黑屋失败"+e.getMessage());
                }
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

//
//    private final ScrollerListener mScrollerListener = new ScrollerListener() {
//            @Override
//            public void onSectionClicked(int sectionPosition) {
//                recycler.smoothScrollToPosition(
//       //                 mContactScrollerAdapter.positionFromSection(sectionPosition));
//                mProgrammaticScroll = true;
//            }
//
//
//            @Override
//            public void onScrollPositionChanged(float percentage, int sectionPosition) {
//                recycler.smoothScrollToPosition(
//      //                  mContactScrollerAdapter.positionFromSection(sectionPosition));
//      //          mProgrammaticScroll = true;
//            }
//        };
    private void getU_List(){
        BmobQuery<Link_user2> eq1 = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        eq1.addWhereEqualTo("L_user_id",user.getObjectId());
        BmobQuery<Link_user2> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("L_black",false);
        List<BmobQuery<Link_user2>> andQuerys = new ArrayList<>();
        andQuerys.add(eq1);
        andQuerys.add(eq2);
        BmobQuery<Link_user2> query = new BmobQuery<>();
        query.and(andQuerys);
        query.findObjects(new FindListener<Link_user2>() {
            @Override
            public void done(List<Link_user2> list, BmobException e) {
                if(e==null){
                    List<String> u_ids = new ArrayList<>();
                    for (int i = 0; i <list.size() ; i++) {
                        u_ids.add(list.get(i).getL_linked_user_id());
                    }
                    if(users!=null){users.clear();}
                    for (int j = 0; j < u_ids.size(); j++) {
                        //Log.e("key", "执行到第"+j+"次循环");
                        BmobQuery<User> categoryBmobQuery = new BmobQuery<>();
                        categoryBmobQuery.addWhereEqualTo("objectId", u_ids.get(j));
                        categoryBmobQuery.setLimit(50).findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> object, BmobException e) {
                                if (e == null) {
                                    if (object.size()!=0){
                                        users.add(object.get(0));
                                    }
                                    mContactAdapter.setUserList(users);
                                    mContactAdapter.notifyDataSetChanged();
                                    //Log.e("key", "将用户加入users表成功");
                                } else {
                                    Log.e("error", "将用户加入users表失败："+e.toString());
                                }
                            }
                        });
                    }
                }else{
                    Log.v("error","Link_user2检索失败："+e.getMessage());
                }
            }
        });
    }
    private void refreshcontactList(){



        getU_List();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <users.size() ; i++) {
//                    Log.v("key",users.get(i).getNickname());
//                }
//                Log.i("refresh", "refreshNoteList: 刷新列表");
//
//           //     mContactScrollerAdapter.setUsers(users);
//            }
//        },SPLASH_DISPLAY_LENGHT);

        //timeLineAdapter.setmNotes(recordList);
       // timeLineAdapter.notifyDataSetChanged();

    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        refreshcontactList();
//    }
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
}
