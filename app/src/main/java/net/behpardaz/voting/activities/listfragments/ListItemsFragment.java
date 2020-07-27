package net.behpardaz.voting.activities.listfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.MapsActivity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;


/**
 * Created by Ratan on 7/29/2015.
 */
public class ListItemsFragment extends ListFragment {

    LayoutInflater inflater;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        StrictMode.ThreadPolicy policy = new StrictMode.
//                ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        View inflate = inflater.inflate(R.layout.sample_main, null);

        setListAdapter(mListAdapter);


        return inflate;
    }

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position + 1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, container, false);

            }
//            TextView viewById = (TextView) convertView.findViewById(R.id.text1);
//            viewById.setText("داروخانه یاب");

            // Because the list item contains multiple touch targets, you should not override
            // onListItemClick. Instead, set a click listener for each target individually.

            convertView.findViewById(R.id.primary_target).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(position==0)
                            Toast.makeText(view.getContext(),
                                    R.string.voterFinder_text,
                                    Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(view.getContext(),
                                        R.string.voters_finder_text,
                                        Toast.LENGTH_SHORT).show();
                        }
                    });

            convertView.findViewById(R.id.secondary_action).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(position==0)
                            {
                                Fragment fragment = Fragment.instantiate(getActivity(), MapsActivity.class.getName());
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                                fragmentTransaction.replace(R.id.containerView,fragment).commit();
                            }
                            else
                                Toast.makeText(view.getContext(),
                                        R.string.voters_finder_text +" "+R.string.title_activity_maps,
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
            return convertView;
        }
    };
}
