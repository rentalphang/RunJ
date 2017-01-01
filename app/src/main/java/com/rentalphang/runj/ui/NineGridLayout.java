package com.rentalphang.runj.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.rentalphang.runj.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格图片布局
 *
 */
public class NineGridLayout extends ViewGroup {


    private Context mContext;
    private int rows; //行数
    private int columns; //列数
    private int gridWidth;  //宫格宽度
    private int gridHeight; //宫格高度

    private int spacing = 3; //间距，默认3，单位dp
    private int maxImageSize = 9; //最大显示图片数
    private int singleImageSize = 250; //单个图片的最大大小，单位dp
    private float singleImageRatio = 1.0f; //单张图片的宽高比

    private List<ImageView> imageViews;
    private List<String> imageUrls;

    private ImageLoader imageLoader;


    public NineGridLayout(Context context) {
        this(context,null);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public NineGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,spacing,metrics);
        singleImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,singleImageSize,metrics);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);
        spacing = (int) typedArray.getDimension(R.styleable.NineGridLayout_spacing,spacing);
        singleImageSize = typedArray.getDimensionPixelSize(R.styleable.NineGridLayout_singleImageSize,singleImageSize);
        singleImageRatio = typedArray.getFloat(R.styleable.NineGridLayout_singleImageRadio,singleImageRatio);

        typedArray.recycle();

        imageViews = new ArrayList<>();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int totalWidth = sizeWidth-getPaddingLeft()-getPaddingRight();
        if(imageUrls!=null && imageUrls.size()>0) {

            if (imageUrls.size()==1){
                gridWidth = singleImageSize>totalWidth?totalWidth:singleImageSize;
                gridHeight = (int) (gridWidth/singleImageRatio);

                //矫正图片显示区域大小，不允许超过最大显示范围
                if(gridHeight>singleImageSize){
                    float ratio = singleImageSize/gridHeight;

                    gridWidth = (int) (getWidth()*ratio);
                    gridHeight = singleImageSize;

                }
            } else {

                gridWidth = gridHeight = (totalWidth-spacing*2)/3;
            }

            sizeWidth = gridWidth*columns+spacing*(columns-1)+getPaddingRight()+getPaddingLeft();
            sizeHeight = gridHeight*rows+spacing*(rows-1)+getPaddingBottom()+getPaddingTop();
        }

        setMeasuredDimension(sizeWidth,sizeHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (imageUrls==null) return;
        int childCount = imageUrls.size();//子项的数量
        for(int i = 0;i<childCount;i++){
            ImageView childView = (ImageView) getChildAt(i);
            childView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if(imageLoader!=null) {
                imageLoader.onDisplayImage(getContext(),childView,imageUrls.get(i));
            }

            int rowNum = i/columns;
            int columnNum = i%columns;

            int left = (gridWidth+spacing)*columnNum+getPaddingLeft();
            int top = (gridHeight+spacing)*rowNum+getPaddingTop();
            int right = left+gridWidth;
            int bottom = top+gridHeight;
            childView.layout(left,top,right,bottom);
        }

    }

    public void setAdapter(){

    }

    public void setImageUrls(List<String> imageList) {

        List<String> imageData = imageList;

        if(imageData==null||imageData.size()==0) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }

        int imageCount = imageData.size();
        if(maxImageSize>0 && imageCount>maxImageSize) {
            imageData = imageData.subList(0,maxImageSize);
            imageCount = imageData.size();
        }

        columns = 3;
        rows = imageCount/3+(imageCount%3==0?0:1);

        if(imageCount == 4){
            rows = 2;
            columns = 2;
        }

        //对View重用，避免重复创建
        if(imageUrls == null) {
            for(int i=0;i<imageCount;i++){
                ImageView imageView = getImageView(i);
                if (imageView == null) return;
                addView(imageView,generateDefaultLayoutParams());

            }
        } else {
            int oldViewCount = imageUrls.size();
            int newViewCount = imageCount;
            if(oldViewCount>newViewCount) {
                removeViews(newViewCount,oldViewCount-newViewCount);
            } else if (oldViewCount<newViewCount) {
                for (int i= oldViewCount;i<newViewCount;i++){
                    ImageView imageView = getImageView(i);
                    if(imageView == null) return;
                    addView(imageView,generateDefaultLayoutParams());
                }
            }
        }
        if(imageList.size()>maxImageSize) {
            View child = getChildAt(maxImageSize-1);
            if(child instanceof ImageViewMore) {
                ImageViewMore imageViewMore = (ImageViewMore) child;
                imageViewMore.setMoreNum(imageList.size()-maxImageSize);
            }
        }
        this.imageUrls = imageData;
        requestLayout();
    }

    /**
     * 获取imageview,保证imageview的重用
     * @param position
     * @return
     */
    private ImageView getImageView(int position){
        ImageView imageView;

        if(position<imageViews.size()){
            imageView = imageViews.get(position);


        } else {

            imageView = new ImageView(mContext);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            imageViews.add(imageView);
        }

        return imageView;
    }

    /**
     * 设置宫格间距
     * @param spacing
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    /**
     * 设置单个图片的尺寸
     * @param singleImageSize
     */
    public void setSingleImageSize(int singleImageSize) {
        this.singleImageSize = singleImageSize;
    }

    /**
     * 设置单个图片的宽高比
     * @param singleImageRatio
     */
    public void setSingleImageRatio(float singleImageRatio) {
        this.singleImageRatio = singleImageRatio;
    }

    /**
     * 设置最大图片个数
     * @param maxImageSize
     */
    public void setMaxImageSize(int maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public interface ImageLoader {

        /**
         * 加载显示图片
         * @param context
         * @param imageView
         * @param url
         */
        void onDisplayImage(Context context, ImageView imageView, String url);
    }
}
