package com.rentalphang.runj.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.rentalphang.runj.R;
import com.rentalphang.runj.adapter.AlbumGridAdapter;
import com.rentalphang.runj.adapter.AlbumListAdapter;
import com.rentalphang.runj.model.bean.ImageFloder;
import com.rentalphang.runj.utils.GeneralUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AlbumActivity extends BaseActivity implements View.OnClickListener {


    private static final int SCAN_PICTURE_FINISH = 0X11; //扫描图片完成标识

    private static final int TAKE_PHOTO_REQUEST_CODE =0X12;

    public static final int RESULT_CODE =0X13; //返回结果码

    private ImageView backImg;

    private TextView floderName;

    private TextView finishText;

    private GridView mGridView;

    private ListView mListView;

    private TextView selectAlbumText;

    private ImageView takePhotoImag;

    private PopupWindow popupWindow;

    /**
     * 选择图片path
     */
    private List<String> mSelectedDate =new ArrayList<>();

    /**
     * 所有图片文件夹
     */
    private List<ImageFloder> imageFloders = new ArrayList<>();

    /**
     * 辅助set类
     */
    private HashSet<String> mDirPaths = new HashSet<>();

    /**
     * 当前展示图片路径
     */
    private List<String>  mImages;

    private int mPicSize;

    /**
     * 当前展示的文件夹
     */
    private File mImgFile;

    private AlbumGridAdapter gridAdapter;

    private AlbumListAdapter listAdapter;




    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SCAN_PICTURE_FINISH: //扫描图片完成

                    closeProgressDialog();

                    floderName.setText(mImgFile.getName());

                    bindDataForGrid();



                    break;
            }

            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        initComponent();

        getImage();

        setListener();


    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.album_back_img);

        floderName = (TextView) findViewById(R.id.album_imagefloder_name);

        finishText = (TextView) findViewById(R.id.album_finish_text);

        mGridView = (GridView) findViewById(R.id.album_gridview);

        selectAlbumText = (TextView) findViewById(R.id.albume_select_album);



        backImg.setOnClickListener(this);

        finishText.setOnClickListener(this);

        selectAlbumText.setOnClickListener(this);


    }

    /**
     * 绑定数据到gridview
     */
    private void bindDataForGrid(){

        mImages = Arrays.asList(mImgFile.list());

        gridAdapter = new AlbumGridAdapter(context,mImages,mImgFile.getAbsolutePath(),mSelectedDate);

        mGridView.setAdapter(gridAdapter);
    }

    /**
     * 绑定数据到listview
     */
    private void bindDataForList(){

        listAdapter = new AlbumListAdapter(context,imageFloders);

        mListView.setAdapter(listAdapter);
    }

    /**
     * 设置监听器
     */
    private void setListener(){
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = mImgFile.getAbsolutePath() + "/" + mImages.get(position);

                if (mSelectedDate.contains(path)) { //已选中

                    mSelectedDate.remove(path);
                } else { //未选中
                    mSelectedDate.add(path);
                }

                Log.i("TAG", position + "位置");
                gridAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 初始化PopuWindow
     */
    private void initPopuWindow(){

        View view = getLayoutInflater().inflate(R.layout.layout_pupopwindow_album,null);

        popupWindow = new PopupWindow(AlbumActivity.this);
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        mListView = (ListView) view.findViewById(R.id.album_listview_popup);

        RelativeLayout popupLayout = (RelativeLayout) view.findViewById(R.id.album_popup_layout);
        bindDataForList();

        popupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();

                mSelectedDate.clear();
                mImgFile = new File(imageFloders.get(position).getDir());

                bindDataForGrid();

                floderName.setText(imageFloders.get(position).getName());

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()) {
            case R.id.album_back_img: //返回

                this.finish();
                break;

            case R.id.album_finish_text: // 完成

                Intent intent = new Intent ();

                intent.putStringArrayListExtra("selectData",(ArrayList)mSelectedDate);

                setResult(RESULT_CODE,intent);

                this.finish();
                break;

            case R.id.albume_select_album: //选择相册

                Log.i("TAG","选择相册");

                //显示popupWindow

                initPopuWindow();

                popupWindow.showAsDropDown(findViewById(R.id.album_action_bar_layout));

                break;

//            case R.id.album_take_photo: //拍照

//                if(GeneralUtil.isSDCard()){
//                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if(cameraIntent.resolveActivity(getPackageManager())!=null){
//                        //判断系统是否有能处理cameraIntent的activity
//                        startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST_CODE);
//                    }
//                }else{
//                    Toast.makeText(context,"没有检测到SD卡",Toast.LENGTH_SHORT).show();
//                }

//                break;

        }
    }


    /**
     * 扫描图片
     */
    private void getImage(){

        if ( ! GeneralUtil.isSDCard()) {
            Toast.makeText(context,"没有外部SD卡",Toast.LENGTH_SHORT).show();
            return ;

        }
        showProgressDialog(this,"正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firseImage = null;

                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                ContentResolver resolver = AlbumActivity.this.getContentResolver();

                Cursor cursor = resolver.query(uri,null,MediaStore.Images.Media.MIME_TYPE +"= ? or "
                        +MediaStore.Images.Media.MIME_TYPE + "= ?",new String[]{"image/jpeg","image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (cursor == null) {
                    return;
                }

                while (cursor.moveToNext()) {
                    //获取图片路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (firseImage == null) {
                        firseImage = path;
                    }

                    //父文件夹
                    File parentFilr = new File(path).getParentFile();

                    if (parentFilr == null) {
                        continue;
                    }

                    //父文件夹的绝对路径
                    String dirPath = parentFilr.getAbsolutePath();

                    ImageFloder imageFloder = null;

                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);

                        imageFloder = new ImageFloder();

                        imageFloder.setDir(dirPath);

                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFilr.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {

                            if (filename.endsWith(".jpeg")||
                                    filename.endsWith(".png")||
                                    filename.endsWith(".jpg"))  {
                                return true;
                            }
                            return false;
                        }
                    }).length;

                    imageFloder.setCount(picSize);

                    imageFloders.add(imageFloder);

                    if (mPicSize < picSize) {
                        mPicSize = picSize;

                        mImgFile = parentFilr;
                    }
                }

                cursor.close();

                mDirPaths = null;

                Message msg = new Message();
                msg.what = SCAN_PICTURE_FINISH;
                handler.sendMessage(msg);

            }
        }).start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_CODE: //拍照后返回

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
