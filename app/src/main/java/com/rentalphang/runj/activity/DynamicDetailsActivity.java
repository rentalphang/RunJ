package com.rentalphang.runj.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.ViewPagerAdapter;
import com.rentalphang.runj.model.bean.Comment;
import com.rentalphang.runj.model.bean.Dynamic;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.adapter.CommentListAdapter;
import com.rentalphang.runj.ui.NoScrollListView;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DynamicDetailsActivity extends BaseActivity implements View.OnClickListener,View.OnLayoutChangeListener {


    private static final int Query_Dynamic_Success = 0x11;

    private static final int Query_Dynamic_Failure = 0x12;

    private static final int Get_Comment_Success = 0x13;

    private static final int Send_Comment_Success = 0x14;

    private static final int Send_Comment_Failure = 0x15;



    private LinearLayout allLayout;
    private ImageView backImg;

    private ImageView headImg;

    private TextView nameText;

    private TextView timeText;

    private ViewPager mViewPager;

    private TextView contentText;

    private TextView themeText;

    private NoScrollListView commentListVeiw;

    private EditText commentEdt;

    private Button sendBtn;

    private TextView commnetNumber;


    private User user;

    private ImageLoader imageLoader;

    private DisplayImageOptions options;

    private DisplayImageOptions circleOptions;

    private String dynamicId = null;

    private Dynamic dynamic = null;

    private List<Comment> comments = null;

    private CommentListAdapter adapter;

    private InputMethodManager inputMethodManager;

    private Comment comment  = null;
    private String commentContent = null;
    private User toUser = null;


    private int screenHeight; //屏幕高度
    private int softKeyHeight; //软键盘弹起时占高度

    private boolean isToUser = false; //是否是对用户进行的评论，true是，false不是

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Query_Dynamic_Success:
                    dynamic = (Dynamic) msg.obj;
                    setDateToView();
                    queryComment();
                    break;

                case Get_Comment_Success:
                    setAdapter();
                    commentEdt.setText("");
                    commnetNumber.setText(comments.size()+"");
                    break;

                case Send_Comment_Success: //发送评论成功

                    commentEdt.setText("");

                    Toast.makeText(DynamicDetailsActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                    queryComment();

                    break;

                case Send_Comment_Failure: //发送评论失败

                    commentEdt.setText("");
                    Toast.makeText(DynamicDetailsActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_details);

        user = BmobUser.getCurrentUser(context,User.class);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imageLoader = ImageLoader.getInstance();
        dynamicId = getIntent().getStringExtra("dynamicId");
        Log.i("TAG",dynamicId);

        initComponent();
        setListener();

        initOptions();

        if (dynamicId != null) {
            queryDynamicById();
        }

        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        softKeyHeight = screenHeight/3;


    }


    private void initOptions(){

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_no_picture)
                .showImageOnFail(R.mipmap.default_no_picture)
                .showImageForEmptyUri(R.mipmap.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        allLayout = (LinearLayout) findViewById(R.id.dynamic_details_layout);
        backImg = (ImageView) findViewById(R.id.dynamic_details_back_img);
        headImg = (ImageView) findViewById(R.id.dynamic_details_head_img);
        nameText = (TextView) findViewById(R.id.dynamic_details_name_text);
        timeText = (TextView) findViewById(R.id.dynamic_details_time_text);
        mViewPager = (ViewPager) findViewById(R.id.dynamic_details_mViewpager);
        contentText = (TextView) findViewById(R.id.dynamic_details_content);
//        themeText = (TextView) findViewById(R.id.dynamic_details_theme_text);
        commnetNumber = (TextView) findViewById(R.id.text_comment_number);
        commentListVeiw = (NoScrollListView) findViewById(R.id.dynamic_comment_listview);
        commentEdt = (EditText) findViewById(R.id.dynamic_comment_edt);
        sendBtn = (Button) findViewById(R.id.dynamic_comment_send_btn);


        backImg.setOnClickListener(this);
        headImg.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        allLayout.addOnLayoutChangeListener(this);

    }

    /**
     * 设置适配器
     */
    private void setAdapter(){
        if (comments != null) {
            adapter = new CommentListAdapter(context,comments);
            commentListVeiw.setAdapter(adapter);
        }
    }

    /**
     * 设置监听
     */
    private void setListener(){
        commentListVeiw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                isToUser = true;
                commentEdt.setHint("回复"+comments.get(position).getFromUser().getNickName()+"：");
                inputMethodManager.showSoftInput(commentEdt,InputMethodManager.SHOW_FORCED);
                toUser = comments.get(position).getFromUser();

            }
        });
    }

    /**
     * 根据id,查询动态
     */
    private  void queryDynamicById(){

        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.include("fromUser");
        query.getObject(context, dynamicId, new GetListener<Dynamic>() {
            @Override
            public void onSuccess(Dynamic dynamic) {
                Message msg = new Message();
                msg.what = Query_Dynamic_Success;
                msg.obj = dynamic;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
            }
        });
    }

    /**
     * 填充数据显示视图
     */
    private void setDateToView(){

        if (dynamic != null) {

            imageLoader.displayImage(dynamic.getFromUser().getHeadImgUrl(), headImg, circleOptions);
            Log.i("TAG", dynamic.getFromUser().getHeadImgUrl() + "url");

            nameText.setText(dynamic.getFromUser().getNickName());

            timeText.setText(dynamic.getCreatedAt());

            if (dynamic.getImage()== null || dynamic.getImage().size() <= 0) {
               mViewPager.setVisibility(View.GONE);
            } else {
                mViewPager.setVisibility(View.VISIBLE);
                mViewPager.setAdapter(new ViewPagerAdapter(context, dynamic.getImage()));
            }

            contentText.setText(dynamic.getContent());
//            themeText.setText(dynamic.getTheme());

            commnetNumber.setText(dynamic.getCommentCount()+"");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dynamic_details_back_img:
                this.finish();
                break;
            case R.id.dynamic_details_head_img: //点击头像

                break;

            case R.id.dynamic_comment_send_btn: //发送评论

                commentContent = commentEdt.getText().toString();

                 if (TextUtils.isEmpty(commentContent)) {
                     Toast.makeText(DynamicDetailsActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                 } else {
                     sendComment();
                 }
                break;

        }
    }


    /**
     * 查询动态的全部评论
     */
    private void queryComment(){
        BmobQuery<Comment> query = new BmobQuery<>();

        query.addWhereEqualTo("dynamic",dynamic);
        query.order("-createdAt");
        query.include("fromUser,toUser");
        query.findObjects(context, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {

                comments = list;
                Message msg = new Message();
                msg.what = Get_Comment_Success;
                handler.sendMessage(msg);


            }

            @Override
            public void onError(int i, String s) {

                Log.i("TAG",i+s);
            }
        });
    }


    /**
     * 发送评论
     */
    public void sendComment(){

        comment = null;
        comment = new Comment();
        comment.setFromUser(user);
        comment.setContent(commentContent);
        comment.setDynamic(dynamic);
        comment.setToUser(toUser);

        comment.save(context, new SaveListener() {
            @Override
            public void onSuccess() {

                Message msg = new Message();
                msg.what = Send_Comment_Success;
                handler.sendMessage(msg);

                //修改动态评论数
                increaseCommentCount();
            }

            @Override
            public void onFailure(int i, String s) {

                Message msg = new Message();
                msg.what = Send_Comment_Failure;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 修改动态评论数，+1
     */
    private void increaseCommentCount() {

         if ( dynamic != null) {
             dynamic.increment("commentCount",1);
             dynamic.update(context, new UpdateListener() {
                 @Override
                 public void onSuccess() {

                     Log.i("TAG","评论数加一");
                 }

                 @Override
                 public void onFailure(int i, String s) {


                 }
             });
         }

    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (bottom!=0 && oldBottom != 0 && (oldBottom-bottom>softKeyHeight)) { //弹起
            Toast.makeText(DynamicDetailsActivity.this,"弹起",Toast.LENGTH_SHORT).show();

        } else if (bottom!=0 && oldBottom != 0 && (bottom-oldBottom>softKeyHeight)){//关闭
            Toast.makeText(DynamicDetailsActivity.this,"关闭",Toast.LENGTH_SHORT).show();

            if (isToUser) { //是对用户
                toUser = null;
                commentEdt.setHint("评论......");
                isToUser = false;
            }

        }
    }
}
