package com.rentalphang.runj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.ImageFloder;


import java.io.File;
import java.util.List;



/**
 * Created by 洋 on 2016/5/15.
 */
public class AlbumListAdapter extends BaseAdapter{

    private Context context;

    private List<ImageFloder> data;

    private LayoutInflater layoutInflater;


    public AlbumListAdapter (Context context,List<ImageFloder> data){

        this.context = context;

        this.data = data;

        this.layoutInflater = LayoutInflater.from(context);



    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_listview_album_layout,parent,false);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_listview_img);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.album_listview_name_count);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        File file = new File(data.get(position).getFirstImagePath());

        Glide.with(context)
                .load(file)
                .into(viewHolder.imageView);


        viewHolder.textView.setText(data.get(position).getName()+"("+data.get(position).getCount()+")");

        return convertView;
    }

    private class ViewHolder{
        private ImageView imageView;

        private TextView textView;
    }

}
