package cu.inmobile.wara.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import cu.inmobile.wara.Activities.ProfileActivity;
import cu.inmobile.wara.R;
import cu.inmobile.wara.Utils.ImageHelper;

import java.util.List;

/**
 * Created by amal on 30/08/16.
 */
public class MyImageListAdapter extends RecyclerView.Adapter<MyImageListAdapter.MyViewHolder> {


    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 5874;
    private List<String> imageList;
    private Activity mContext;
    private CallbackInterface mCallback;

    public MyImageListAdapter(List<String> imageList, Activity mContext) {
        this.imageList = imageList;
        this.mContext = mContext;
        try {
            mCallback = (CallbackInterface) mContext;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
            Log.e("MyAdapter", "Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @Override
    public MyImageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_image_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyImageListAdapter.MyViewHolder holder, final int position) {

        Log.d("IMG_ADAPTER/Bind" , "pos: " + position);

        if(imageList.get(position) == null){
            holder.add_photo_layout.setVisibility(View.VISIBLE);
            holder.add_photo_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(mContext);

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        mContext.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                    } else {
                        mCallback.onHandleSelection(0, "");
                    }*/
                }
            });
        }else {
            holder.add_photo_layout.setVisibility(View.GONE);
            Glide.with(mContext).load(imageList.get(position)).into(holder.my_image);
            holder.my_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ProfileActivity)mContext).setProfileImage(position);
                    ((ProfileActivity)mContext).alertSave();



                }
            });
            //ImageHelper.getRoundedCornerBitmap(holder.my_image , 5);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position - the position
         * @param text     - the text to pass back
         */
        void onHandleSelection(int position, String text);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView my_image;
        public LinearLayout add_photo_layout;

        public MyViewHolder(View view) {
            super(view);
            my_image = (ImageView) view.findViewById(R.id.img_gallery_item);
            add_photo_layout = (LinearLayout) view.findViewById(R.id.ll_add_photo);
        }
    }
}
