package com.rentalphang.runj.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.rentalphang.runj.R;
import java.util.List;



public class GuiderViewPagerAdapter extends PagerAdapter {


    private List<Integer> views;
    private Context context;
    private LayoutInflater layoutInflater;

    public GuiderViewPagerAdapter(Context context, List<Integer> views){

        this.context = context;
        this.views = views;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = layoutInflater.inflate(R.layout.item_viewpager_layout,container,false);
        ImageView imageView = (ImageView) view.findViewById(R.id.viewpager_item_img);
        imageView.setImageResource(views.get(position));

        container.addView(view,0);

        return view;
    }
}
