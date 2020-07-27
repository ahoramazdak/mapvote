package net.behpardaz.voting.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.dummy.DummyContent;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * An activity representing a list of pharmacies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link pharmacyDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class VoterListFragments extends Fragment {

    private static final String TAG ="VoterListFragments" ;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = inflater.inflate(R.layout.activity_voter_list, null);


        FloatingActionButton fab = (FloatingActionButton) inflate.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "مشخصات کامل رای دهنده", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = inflate.findViewById(R.id.pharmacy_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (inflate.findViewById(R.id.pharmacy_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return inflate;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Voter> mValues;

        public SimpleItemRecyclerViewAdapter(List<Voter> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pharmacy_list_content, parent, false);
            return new ViewHolder(view);
        }

        public String getPhoneNumberFormat(String yourStringPhone) {
            Log.e(TAG, "getPhoneNumberFormat: "+yourStringPhone );
        if(yourStringPhone==null ||yourStringPhone.isEmpty())
            return "";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)

        {
            return PhoneNumberUtils.formatNumber(yourStringPhone, "IR").replaceAll(" ","");
        }

        else

        {
//Deprecated method
            return PhoneNumberUtils.formatNumber(yourStringPhone);
        }
    }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getOriginId());
            String contView="";
                        String[] split = mValues.get(position).mytoString().split("\n");
                        for (final String s: split) {
                            Log.e(TAG, "onBindViewHolder: " + s);


                            if ((s.contains("تلفن :"))  || (s.contains("موبایل : ") )) {
                                final String phoneNumberFormat = getPhoneNumberFormat(s.replaceAll("[^0-9|\\+]", ""));
                                if (s.contains("تلفن :")) {
                                    if(phoneNumberFormat.isEmpty())
                                        holder.callButton.setVisibility(View.GONE);
                                    Log.e(TAG, "onBindViewHolder: " + s.contains("تلفن :"));
                                    holder.callButton.setText("تماس با تلفن ثابت");
                                    holder.callButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.e(TAG, "onClick: " + s);

                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            Log.e(TAG, "onClick: "+phoneNumberFormat);

                                            callIntent.setData(Uri.parse("tel: " +(phoneNumberFormat.startsWith("0")?phoneNumberFormat:"021"+phoneNumberFormat)));

                                            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                Log.e(TAG, "onClick: "+"no premission" );
                                                ActivityCompat.requestPermissions(getActivity(),
                                                        new String[]{Manifest.permission.CALL_PHONE},
                                                        1);
                                                return;
                                            }

                                            startActivity(callIntent);
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "onBindViewHolder: "  + s.contains("موبایل :"));
                                    if(phoneNumberFormat.isEmpty())
                                        holder.callMobileButton.setVisibility(View.GONE);
                                    holder.callMobileButton.setText("تماس با موبایل");
                                    holder.callMobileButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.e(TAG, "onClick: " + s);

                                            Intent callIntent = new Intent(Intent.ACTION_CALL);

                                            if(phoneNumberFormat.matches("(0)"))
                                                Log.e(TAG, "onClick: "+0 );;
                                            Log.e(TAG, "onClick: "+ phoneNumberFormat);
                                            callIntent.setData(Uri.parse("tel: " +(phoneNumberFormat.startsWith("0")?phoneNumberFormat:"0"+phoneNumberFormat)));

                                            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                Log.e(TAG, "onClick: "+"no premission" );
                                                ActivityCompat.requestPermissions(getActivity(),
                                                        new String[]{Manifest.permission.CALL_PHONE},
                                                        1);
                                                return;
                                            }

                                            startActivity(callIntent);
                                        }
                                    });

                                }

                            }else {
                                contView+=s+"\n";
                            }
                        }
            holder.mContentView.setText(contView);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(pharmacyDetailFragment.ARG_ITEM_ID, holder.mItem.getOriginId());
                        pharmacyDetailFragment fragment = new pharmacyDetailFragment();
                        fragment.setArguments(arguments);
                        fragment.getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.pharmacy_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, pharmacyDetailActivity.class);
                        intent.putExtra(pharmacyDetailFragment.ARG_ITEM_ID, holder.mItem.getOriginId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final Button callButton;
            public final Button callMobileButton;
            public Voter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                callButton = (Button) view.findViewById(R.id.callbutton);
                callMobileButton = (Button) view.findViewById(R.id.callmobilebutton);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
