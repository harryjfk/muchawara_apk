package cu.inmobile.wara.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import cu.inmobile.wara.Models.userDetail;

import java.util.ArrayList;import cu.inmobile.wara.R;

/**
 * Created by amal on 21/01/17.
 */
public class CardsAdapter extends ArrayAdapter<userDetail> {

    private final ArrayList<userDetail> userDetailArrayList;
    private final LayoutInflater layoutInflater;
    private Context mContext;

    public CardsAdapter(Context context, ArrayList<userDetail> cards) {
        super(context, -1);
        mContext = context;
        this.userDetailArrayList = cards;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        userDetail card = userDetailArrayList.get(position);
        View view = layoutInflater.inflate(R.layout.cardview_swipe_user, parent, false);
        ImageView user_image = (ImageView) view.findViewById(R.id.user_image);
        TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
        TextView tvAboutMe = (TextView) view.findViewById(R.id.tv_about_me);
        //TextView user_age = (TextView) view.findViewById(R.id.user_age);

        Glide.with(mContext).load(card.getPicture()).placeholder(R.drawable.profile_placeholder).into(user_image);
        tvInfo.setText(card.getName() + ", " + card.getAge());
        tvAboutMe.setText(card.getAboutMe());
        //user_age.setText(card.getAge());

        return view;
    }

    @Override
    public userDetail getItem(int position) {
        return userDetailArrayList.get(position);
    }

    @Override
    public int getCount() {
        return userDetailArrayList.size();
    }


}
