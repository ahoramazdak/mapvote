package net.behpardaz.voting.activities.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.UpdatesFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Ratan on 7/27/2015.
 */
public class TabFragment extends Fragment {

    private static final String TAG = "TabFragment";
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.tab_layout,null);
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        viewPager.setOffscreenPageLimit(3);
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        viewPager.arrowScroll(View.LAYOUT_DIRECTION_RTL);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                   }
        });
//        viewPager.setCurrentItem(0);

        //here is your arguments
        Bundle bundle=getArguments();

        //here is your list array
        if(bundle!=null) {
            String myStrings = bundle.getString("path");
            Log.e(TAG, "onCreateView: "+myStrings );
            if (myStrings != null && !myStrings.isEmpty()) {

            }
        }
        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 : return new ProfileFragment();
              case 1 : return new ServicesFragment();
              case 2 : return new UpdatesFragment();
          }
        return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return getString(R.string.profileTitle);
                case 1 :
                    return getString(R.string.servicetitle);
                case 2 :
                    return getString(R.string.messages);
            }
                return null;
        }
    }

}
