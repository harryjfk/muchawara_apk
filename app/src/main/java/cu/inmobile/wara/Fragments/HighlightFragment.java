package cu.inmobile.wara.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This is a fragment class for the view pager showing the highlighted content.
 * Created by Anurag on 4/7/2017.
 */

public class HighlightFragment extends Fragment{

    public HighlightFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highlight , container, false);
        CircleImageView profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        Glide.with(getActivity()).load(WaraApp.imageUrl).dontAnimate().placeholder(R.drawable.profile_placeholder).into(profile_image);
        return view;
    }
}
