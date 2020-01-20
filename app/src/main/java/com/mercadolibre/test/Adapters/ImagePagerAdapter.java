package com.mercadolibre.test.Adapters;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.mercadolibre.test.Utils.DownloadImageTask;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private final Context context;

    private final List<String> urls;

    public ImagePagerAdapter(Context context, List<String> urls) {
        super();

        this.context = context;

        this.urls = urls;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup parent, int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        final ImageView imageView = new ImageView(context);

        new DownloadImageTask(imageView).execute(urls.get(position));

        linearLayout.addView(imageView);

        imageView.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;

        parent.addView(linearLayout,0);

        return linearLayout;
    }

    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return urls.size();
    }
}
