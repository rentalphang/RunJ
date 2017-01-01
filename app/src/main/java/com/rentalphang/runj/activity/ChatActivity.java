package com.rentalphang.runj.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.ChatRecyclerAdapter;
import com.rentalphang.runj.listener.OnRecyclerViewListener;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.ActivityManager;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;



public class ChatActivity extends BaseActivity implements View.OnClickListener, ObseverListener,MessageListHandler {

    private LinearLayout chatLayout;
    private ImageView backImg;
    private TextView titleText;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private EditText msgEdt;
    private Button sendBtn;

    private User user;

    private BmobRecordManager recordManager;
    private ChatRecyclerAdapter adapter;
    private BmobIMConversation c;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context,User.class);
        //在聊天页面的onCreate方法中，通过如下方法创建新的会话实例,这个obtain方法才是真正创建一个管理消息发送的会话
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(),
                (BmobIMConversation)getBundle().getSerializable("c"));
        initComponent();

        titleText.setText(c.getConversationTitle());
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initComponent() {
        chatLayout = (LinearLayout) findViewById(R.id.chat_layout);
        backImg = (ImageView) findViewById(R.id.chat_back_img);
        titleText = (TextView) findViewById(R.id.chat_title_text);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_sw_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        msgEdt = (EditText) findViewById(R.id.chat_msg_edt);
        sendBtn = (Button) findViewById(R.id.chat_send_btn);

        backImg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
    }

    private void initSwipeRefreshLayout(){

        chatLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                chatLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                refreshLayout.setRefreshing(true);
                //自动刷新
                queryMessages(null);


            }
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });
    }
    private void initRecyclerView(){

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager); //设置布局管理器
        adapter = new ChatRecyclerAdapter(context,c);
        recyclerView.setAdapter(adapter);
        adapter.setOnRcyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Log.i("TAG", position + "");
            }

            @Override
            public boolean onItemLongClick(final int position) {

                new AlertDialog.Builder(ChatActivity.this)
                        .setMessage("确定删除这条消息")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                c.deleteMessage(adapter.getItem(position));
                                adapter.remove(position);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                return true;
            }

            @Override
            public void onChildClick(int position, int childId) {

            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.chat_back_img:
                this.finish();
                break;
            case R.id.chat_send_btn:

                sendMessage();
                break;
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        String message = msgEdt.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(context,"请输入内容",Toast.LENGTH_SHORT).show();
            return;
        } else {
            BmobIMTextMessage textMessage = new BmobIMTextMessage();
            textMessage.setContent(message);
            c.sendMessage(textMessage,listener);
        }
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener =new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            msgEdt.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            msgEdt.setText("");
            scrollToBottom();
            if (e != null) {
            }
        }
    };
    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }


    /**首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg){
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                refreshLayout.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {

                }
            }
        });
    }
    /**添加消息到聊天界面中
     * @param event
     */
    private void addMessage2Chat(MessageEvent event){
        BmobIMMessage msg =event.getMessage();
        if(c!=null && event!=null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()){//并且不为暂态消息
            if(adapter.findPosition(msg)<0){//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
            }
            scrollToBottom();
        }else{

        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        //添加通知监听
        BmobNotificationManager.getInstance(this).addObserver(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }
    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage(){
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if(cache.size()>0){
            int size =cache.size();
            for(int i=0;i<size;i++){
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    @Override
    protected void onPause() {
        //取消通知栏监听
        BmobNotificationManager.getInstance(this).removeObserver(this);
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //清理资源
//        recordManager.clear();
        //更新此会话的所有消息为已读状态
        c.updateLocalCache();
        super.onDestroy();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i=0;i<list.size();i++){
            addMessage2Chat(list.get(i));
        }
    }
    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }
}
