package cu.inmobile.wara.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cu.inmobile.wara.Fragments.SuperPowerHighlightFragment;

/**
 * Created by Anurag on 4/10/2017.
 */

public class SuperPowerPagerAdapter extends FragmentPagerAdapter{

    public SuperPowerPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = new SuperPowerHighlightFragment();
        return frag;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
