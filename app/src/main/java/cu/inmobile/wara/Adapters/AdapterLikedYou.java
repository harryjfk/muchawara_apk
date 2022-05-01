package cu.inmobile.wara.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.enrique.stackblur.StackBlurManager;

import java.util.ArrayList;

import cu.inmobile.wara.Activities.ProfileViewActivity;
import cu.inmobile.wara.Models.userDetail;
import cu.inmobile.wara.R;

/**
 * Created by amal on 30/08/16.
 */
public class AdapterLikedYou extends RecyclerView.Adapter<AdapterLikedYou.MyViewHolder> {

    private ArrayList<userDetail> userDetailArrayList;
    private Activity mContext;

    public AdapterLikedYou(ArrayList<userDetail> userDetailArrayList, Activity mContext) {
        this.userDetailArrayList = userDetailArrayList;
        this.mContext = mContext;
    }

    @Override
    public AdapterLikedYou.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_liked_you, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onBindViewHolder" , " CLIKING ! ");
            }
        });

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterLikedYou.MyViewHolder holder, final int position) {
        final userDetail user = userDetailArrayList.get(position);

        holder.imgItemProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AdapterLikedYou" , "onBindViewHolder CLIKING ! ");
                userDetail user = userDetailArrayList.get(position);
                Intent intent = new Intent(mContext, ProfileViewActivity.class);
                intent.putExtra("target_id", user.getId());
                intent.putExtra("from", "AdapterLikedYou1");
                //intent.putExtra("receiverName", user.getName());
                //intent.putExtra("receiverImageUrl", user.getPicture());
                //intent.putExtra("contact_id", user.getId());
                //intent.putExtra("aboutme", user.getAboutMe());
                intent.putExtra("blur_image", true);
                mContext.startActivity(intent);
            }
        });

        // holder.nearby_ppl_layout.setPadding(HelperMethods.getPixelInDp(8,mContext),HelperMethods.getPixelInDp(8,mContext),HelperMethods.getPixelInDp(8,mContext),HelperMethods.getPixelInDp(14,mContext));
        Glide.with(mContext)
                .load(user.getPicture())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // Bitmap bluredBitmap = HelperMethods.blurBitmap(resource,mContext);
                        //if (!user.isShould_show()) {
                            StackBlurManager _stackBlurManager = new StackBlurManager(resource);
                            _stackBlurManager.process(50);
                            holder.imgItemProfile.setImageBitmap(_stackBlurManager.returnBlurredImage());
                            holder.rlItem.setOnClickListener(null);
                            holder.tvName.setText(user.getName());
                        /*} else {
                            holder.imgItemProfile.setImageBitmap(resource);
                            holder.tvName.setText(user.getName());
                        }*/
                    }
                });

        //  Glide.with(mContext).load(user.getPicture()).placeholder(R.drawable.profile_placeholder).dontAnimate().into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return userDetailArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return  Long.getLong(userDetailArrayList.get(position).getId());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgItemProfile;
        public TextView tvName;
        public RelativeLayout rlItem;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_item_name);
            imgItemProfile = (ImageView) view.findViewById(R.id.img_item_profile);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_people_liked_you);

            imgItemProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("AdapterLikedYou" , "MyViewHolder CLIKING ! ");
                    userDetail user = userDetailArrayList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ProfileViewActivity.class);
                    intent.putExtra("target_id", user.getId());
                    intent.putExtra("from", "AdapterLikedYou");
                    //intent.putExtra("receiverName", user.getName());
                    //intent.putExtra("receiverImageUrl", user.getPicture());
                    //intent.putExtra("contact_id", user.getId());
                    //intent.putExtra("aboutme", user.getAboutMe());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
