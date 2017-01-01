package com.rentalphang.runj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.DynamicDetailsActivity;
import com.rentalphang.runj.activity.PersonProfileActivity;
import com.rentalphang.runj.model.bean.Dynamic;
import com.rentalphang.runj.model.bean.Like;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.ui.NineGridLayout;
import com.rentalphang.runj.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.onekeyshare.OnekeyShare;




public class DynamicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int Item_Type_Footer = 0;
    private static final int Item_Type_Normal = 1;

    private boolean isLoadMore = false;//是否显示footerview,默认不显示

    private Context context;
    private Fragment homeFragment;

    private User user;

    private String noticeStr = null;
    private List<Dynamic> likes = new ArrayList<>();

    private List<Dynamic> data = null;

    public int lastposition =-1;

    private DisplayImageOptions options;

    private DisplayImageOptions circleOptions;


    public DynamicRecyclerAdapter(Context context, Fragment homeFragment){
        this.context = context;
        this.homeFragment = homeFragment;
        user = BmobUser.getCurrentUser(context.getApplicationContext(),User.class);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.default_no_picture)
                .showImageOnFail(R.mipmap.default_no_picture)
                .showImageForEmptyUri(R.mipmap.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.mipmap.default_no_picture)
                .build();


        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_avatar)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    public void setData(List<Dynamic> data) {
        this.data = data;
    }

    public void setNoticeStr(String noticeStr) {
        this.noticeStr = noticeStr;
    }

    public boolean isLoadMore() {

        return isLoadMore;
    }

    public void setLikes(List<Dynamic> likes) {
        this.likes = likes;
    }


    /**
     * 设置是否显示上拉加载更多，默认不显示
     * @param isLoadMore
     */
    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;

        //刷新数据
        notifyDataSetChanged();
    }

    private void setAnimation(View view,int position) {
        if(position>lastposition) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(),R.anim.item_bottom_in);
            view.startAnimation(animation);
            lastposition = position;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        switch (viewType) {
            case Item_Type_Footer:
                View footView =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_foot_view_layout,parent,false);
                return new RecyclerFootViewHolder(footView);
            case Item_Type_Normal:
                View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dynamic_layout,parent,false);
                return new DynamicViewHolder(normalView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof DynamicViewHolder) {
            final DynamicViewHolder viewHolder = (DynamicViewHolder) holder;
            final Dynamic dynamic = data.get(position);

            ImageLoader.getInstance().displayImage(dynamic.getFromUser().getHeadImgUrl(), viewHolder.avatar, circleOptions);

            viewHolder.nickName.setText(dynamic.getFromUser().getNickName());
            viewHolder.time.setText(GeneralUtil.computeTime(dynamic.getCreatedAt()));

            viewHolder.nineGridLayout.setImageLoader(new NineGridLayout.ImageLoader() {
                @Override
                public void onDisplayImage(Context context, ImageView imageView, String url) {
                    ImageLoader.getInstance().displayImage(url, imageView, options);
                }
            });
            viewHolder.nineGridLayout.setImageUrls(dynamic.getImage());
            viewHolder.content.setText(dynamic.getContent());

            viewHolder.commentCount.setText(dynamic.getCommentCount().toString());
            viewHolder.likeCount.setText(dynamic.getLikesCount().toString());
            if (likes.contains(dynamic)) {
                viewHolder.likeImage.setImageResource(R.mipmap.ic_dynamic_alreadylike);
            } else {
                viewHolder.likeImage.setImageResource(R.mipmap.like);
            }

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DynamicDetailsActivity.class);
                    intent.putExtra("dynamicId", dynamic.getObjectId());
                    homeFragment.startActivityForResult(intent, 0x12);
                }
            });

            viewHolder.likeRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (viewHolder.isClickFinish) {
                        doLike(dynamic, viewHolder);
                        viewHolder.isClickFinish = false;
                    }
                }
            });
            viewHolder.shareRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnekeyShare oks = new OnekeyShare();

                    //关闭sso授权
                    oks.disableSSOWhenAuthorize();
                    // title标题：微信、QQ（新浪微博不需要标题）
                    oks.setTitle("动态");  //最多30个字符
                    // text是分享文本：所有平台都需要这个字段
                    if (dynamic.getContent() != null) {
                        int lengthStr = dynamic.getContent().length();
                        oks.setText(dynamic.getContent().substring(0, lengthStr > 40 ? 35 : lengthStr));  //最多40个字符
                    }
                    // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
                    //oks.setImagePath(Environment.getExternalStorageDirectory() + "/meinv.jpg");//确保SDcard下面存在此张图片

                    if (dynamic.getImage() != null && dynamic.getImage().size() > 0) {
                        //网络图片的url：所有平台
                        oks.setImageUrl(dynamic.getImage().get(0));//网络图片rul
                    }
//                // url：仅在微信（包括好友和朋友圈）中使用
                    oks.setUrl("http://runbang.bmob.cn");   //网友点进链接后，可以看到分享的详情
//                // Url：仅在QQ空间使用
                    oks.setTitleUrl("http://runbang.bmob.cn");  //网友点进链接后，可以看到分享的详情
                    // 启动分享GUI
                    oks.show(context);

                }
            });

            viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent avatarIntent = new Intent(context, PersonProfileActivity.class);
                    avatarIntent.putExtra("userId", dynamic.getFromUser().getObjectId());
                    homeFragment.startActivity(avatarIntent);
                }
            });

//            setAnimation(viewHolder.cardView, position);
        } else if (holder instanceof RecyclerFootViewHolder) {
            RecyclerFootViewHolder footViewHolder = (RecyclerFootViewHolder) holder;
            footViewHolder.loadMore.setText("加载更多");
//            setAnimation(footViewHolder.linearLayout,position);

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadMore&&position+1 == getItemCount()){
            return Item_Type_Footer;
        } else {
            return Item_Type_Normal;
        }
    }

    @Override
    public int getItemCount() {
        if (isLoadMore) {
            return data==null||data.size()==0?0:data.size()+1;
        }
        return data==null||data.size()==0?0:data.size();
    }

    /**
     * 点赞处理
     * @param dynamic
     * @param holder
     */
    private void doLike(final Dynamic dynamic, final DynamicViewHolder holder){

        if(!likes.contains(dynamic)) {//没有点赞
            Log.i("TAG","没有点赞");
            Like like = new Like();
            like.setFromUser(user);
            like.setToDynamic(dynamic);
            like.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    dynamic.setLikesCount(dynamic.getLikesCount()+1);
                    dynamic.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            likes.add(dynamic);
                            holder.isClickFinish = true;
                            notifyDataSetChanged();
                            Toast.makeText(context,"点赞成功",Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            holder.isClickFinish = true;
                            Toast.makeText(context,"点赞失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onFailure(int i, String s) {
                    holder.isClickFinish = true;
                    Toast.makeText(context,"点赞失败，请稍后重试",Toast.LENGTH_SHORT);
                }
            });
        } else {
            Log.i("TAG","点赞");

            BmobQuery<Like> query = new BmobQuery<>();
            query.addWhereEqualTo("fromUser",user);
            query.addWhereEqualTo("toDynamic",dynamic);
            query.findObjects(context, new FindListener<Like>() {
                @Override
                public void onSuccess(List<Like> list) {
                    if(list.size()==1) {
                        Like like = list.get(0);
                        like.delete(context, new DeleteListener() {
                            @Override
                            public void onSuccess() {

                                dynamic.setLikesCount(dynamic.getLikesCount()-1);
                                dynamic.update(context, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        likes.remove(dynamic);
                                        holder.isClickFinish = true;
                                        notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onFailure(int i, String s) {
                                        holder.isClickFinish = true;
                                        Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            @Override
                            public void onFailure(int i, String s) {
                                holder.isClickFinish = true;
                                Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        holder.isClickFinish = true;
                        Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError(int i, String s) {
                    holder.isClickFinish = true;
                    Toast.makeText(context,"取消点赞失败",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
