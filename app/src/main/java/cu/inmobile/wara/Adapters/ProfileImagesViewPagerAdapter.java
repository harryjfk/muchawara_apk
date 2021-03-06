package cu.inmobile.wara.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.enrique.stackblur.StackBlurManager;

import cu.inmobile.wara.R;

import java.util.ArrayList;

/**
 * Created by amal on 17/08/16.
 */
public class ProfileImagesViewPagerAdapter extends PagerAdapter {
    private Activity mContext;
    private ArrayList<String> cat_images;
    private boolean blurImage;

    public ProfileImagesViewPagerAdapter(Activity mContext, ArrayList<String> cat_images , boolean blurImage) {
        this.mContext = mContext;
        this.cat_images = cat_images;
        this.blurImage = blurImage;
    }

    @Override
    public int getCount() {
        return cat_images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.profile_image_pager_item, container, false);

        final ImageView imageView = (ImageView) itemView.findViewById(R.id.img_item);

        //Glide.with(mContext).load(cat_images.get(position)).into(imageView);
        Glide.with(mContext)
                .load(cat_images.get(position))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // Bitmap bluredBitmap = HelperMethods.blurBitmap(resource,mContext);
                        if (blurImage) {
                            StackBlurManager _stackBlurManager = new StackBlurManager(resource);
                            _stackBlurManager.process(50);
                            imageView.setImageBitmap(_stackBlurManager.returnBlurredImage());
                        } else {
                            imageView.setImageBitmap(resource);
                        }
                    }
                });


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
