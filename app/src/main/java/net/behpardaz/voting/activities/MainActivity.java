package net.behpardaz.voting.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.dummy.DummyContent;
import net.behpardaz.voting.activities.listfragments.ListItemsFragment;
import net.behpardaz.voting.activities.tabs.TabFragment;
import net.behpardaz.voting.mgmt.SessionManager;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    public static int i = 0;
    public static SessionManager manager;
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CAMERA = 1;
    private Fragment mContent;
    private Double lat;
    private Double lon;

    public void onRestoreInstanceState(Bundle inState){
        mContent = getSupportFragmentManager().getFragment(inState,"mContent");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        Log.e(TAG, "onSaveInstanceState: mContent and outState"+mContent+" "+outState );
        if(mContent!=null)
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.activity_main);
        manager = new SessionManager(this);

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        TextView viewById = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.textView_user);
        viewById.setText(manager.getUserDetails().get(manager.KEY_NAME));


        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */
        Log.e(TAG, "onCreate: savedInstanceState"+savedInstanceState );
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        }else {
            mContent = mFragmentManager.getFragment(savedInstanceState, "mContent");
            Log.e(TAG, "onCreate: mContent"+mContent );
            if(mContent!=null){
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, mContent).commit();
        }}
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(this);
//        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                mDrawerLayout.closeDrawers();
//
//
//
//                if (menuItem.getItemId() == R.id.nav_item_sent) {
//                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.containerView,new SentFragment()).commit();
//
//                }
//
//                if (menuItem.getItemId() == R.id.nav_item_inbox) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
//
//                }
//
//                return false;
//            }
//
//        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
    }
    private boolean doubleBackToExitPressedOnce;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, this.getResources().getString(R.string.exit_toast), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mDrawerLayout.closeDrawers();
        if (id == R.id.nav_camera) {
            FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
            xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        } else if (id == R.id.nav_gallery) {
            FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
            xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

//            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.containerView,new SentFragment()).commit();
        } else if (id == R.id.nav_slideshow) {
//            Fragment fragment = Fragment.instantiate(this, MapsActivity.class.getName());
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.containerView, new MapsActivity()).commit();

//            startActivity(new Intent(this, MapsActivity.class));
        } else if (id == R.id.nav_manage) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new VoterListFragments()).commit();

//            startActivity(new Intent(this, pharmacyListActivity.class));

        } else if (id == R.id.nav_share) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new ListItemsFragment()).commit();

        } else if (id == R.id.nav_send) {
//            startActivity(new Intent(this, PhotoIntentActivity.class));
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView,new VoterFragment()).commit();

//            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.containerView,new MyMapActivity()).commit();

        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }


    public void logout_btn(View view) {
//        manager.setPreferences(MainActivity.this, "status", "0");
        manager.logoutUser();
        finish();
//        startActivity(new Intent(this, LoginActivity.class));
    }

    //    public void show_drugstore(View view) {
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.containerView,new pharmacyListActivity()).commit();
//
//    }   public void show_drug(View view) {
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.containerView,new pharmacyListActivity()).commit();
//
//    }
    static ArrayList<Marker> markers = new ArrayList<>();
    HttpRequestTask httpRequestTask;

    public void showVotersMap(View view) {
        Fragment fragment = Fragment.instantiate(this, MapsActivity.class.getName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.containerView, fragment).commit();
    }

    public void showVoterList(View view) {
//        startActivity(new Intent(this, DrugFinderActivity.class));
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView,new VoterFragment()).commit();
//        startActivity(new Intent(this, PhotoIntentActivity.class));
    }

    public void showVoter(View view) {


        ImageButton viewById1 = (ImageButton) view.findViewById(R.id.imageButton3);
        viewById1.setEnabled(false);

        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        if (httpRequestTask != null)
            httpRequestTask.cancel(true);

        TextView viewById = (TextView) findViewById(R.id.latlongLocation);
        String[] split = viewById.getText().toString().split(" ");
        TextView viewByIdDist = (TextView) findViewById(R.id.textViewDistance);
        String strDist = viewByIdDist.getText().toString();


        if (split.length == 2) {
            if (Integer.parseInt(strDist) <= 0)
                strDist = "1000";
//            MapsActivity.mMap.addCircle(new CircleOptions().radius(Integer.parseInt(strDist)));
//            MapsActivity.mMap.animateCamera(CameraUpdateFactory.zoomTo(calculateZoomLevel(Integer.parseInt(strDist))));
            httpRequestTask = new HttpRequestTask(strDist);
            httpRequestTask.execute(split);
        }


    }



    public static String getmsg() {
        return exceptionMsg;
    }

    private class HttpRequestTask extends AsyncTask<String, Void, ArrayList<Voter>> {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        private final String strDist;

        public HttpRequestTask(String strDist) {
            this.strDist = strDist;
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // actually could set running = false; right here, but I'll
                    // stick to contract.
                    ImageButton viewById1 = (ImageButton) findViewById(R.id.imageButton3);
                    if (viewById1 != null)
                        viewById1.setEnabled(true);
                    cancel(true);
                }
            });
        }

        @Override
        protected void onPreExecute() {
//            MapsActivity.mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("در حال جستجو...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Voter> doInBackground(String... paramss) {
            return getData(paramss[0], paramss[1], strDist);
        }

        @Override
        protected void onPostExecute(ArrayList<Voter> data) {
            DummyContent.ITEMS.clear();
            for (Voter voter : data) {
                DummyContent.addItem(voter);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_show_loc);
                MapsActivity.mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.containerView, new VoterListFragments()).commit();
                    }
                });
                MapsActivity.mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {

                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        if(marker.getPosition().latitude==lat && marker.getPosition().longitude==lon)
                            return null;
                        LinearLayout info = new LinearLayout(getApplicationContext());
                        info.setPadding(16,16,16,16);
                        info.getRootView().setBackgroundResource(R.drawable.side_nav_bar);
                        info.setOrientation(LinearLayout.VERTICAL);
                        info.setVerticalGravity(Gravity.CENTER_VERTICAL);
                        TextView title = new TextView(getApplicationContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        int distance = HaversineAlgorithm.HaversineInM(marker.getPosition().latitude, marker.getPosition().longitude, lat, lon);
                        title.setText(marker.getTitle());
                        TextView snippet = new TextView(getApplicationContext());
                        snippet.setTextColor(Color.WHITE);
                        String[] split = marker.getSnippet().split("\n");
                       String snip=" در فاصله "+distance+"متری \n ";
                        snip+="\n"+split[2];
                        snippet.setText(snip);


                        info.addView(title);
                        info.addView(snippet);
//                        String[] split = snippet.getText().toString().split("\n");
//                        for (final String s: split) {
//                            Log.e(TAG, "getInfoContents: "+s );
//                            if((s.startsWith("تلفن:")&& Double.parseDouble(s.substring("تلفن:".length()).trim())>0)||(s.startsWith("موبایل :")&& Double.parseDouble(s.substring("موبایل :".length()))>0)){
//                            Button callButton = new Button(getApplicationContext());
//
//                                if(s.startsWith("تلفن :"))
//                                callButton.setText("تماس با تلفن ثابت");
//                                else
//                                    callButton.setText("تماس با موبایل");
//                            callButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Log.e(TAG, "onClick: "+s );
//                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                                    if(s.startsWith("تلفن :"))
//                                    callIntent.setData(Uri.parse("tel:"+Double.parseDouble(s.substring("تلفن :".length()))));
//                                    else
//                                        callIntent.setData(Uri.parse("tel:"+Double.parseDouble(s.substring("موبایل :".length()))));
//
//                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                        // TODO: Consider calling
//                                        //    ActivityCompat#requestPermissions
//                                        // here to request the missing permissions, and then overriding
//                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                        //                                          int[] grantResults)
//                                        // to handle the case where the user grants the permission. See the documentation
//                                        // for ActivityCompat#requestPermissions for more details.
//                                        return;
//                                    }
//                                    startActivity(callIntent);
//                                }
//                            });
//                            info.addView(callButton);
//                        }}

                        return info;
                    }
                });

                markers.add(MapsActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(voter.getLatitude()), Double.parseDouble(voter.getLongitude()))).title(voter.getVoterTitle()).snippet(voter.mytoString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))));

            }
            if (data.isEmpty())
                Toast.makeText(getApplicationContext(), getmsg(), Toast.LENGTH_LONG).show();

            ImageButton viewById1 = (ImageButton) findViewById(R.id.imageButton3);
            if (viewById1 != null)
                viewById1.setEnabled(true);
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {


            progressDialog.dismiss();

            super.onCancelled();
        }
    }

    //    public static String SERVER = "http://172.16.4.74:8888/dispatch";
    public static String SERVER = "http://5.160.70.162:8888/dispatch";
    public static String version = "1.0.0";
    public static String timeout = "15";

    private static ArrayList<Voter> getPharmacies(Map<String, Object> response) {
        ArrayList<Voter> t = new ArrayList<>();
        for (Map.Entry<String, Object> entry : response.entrySet()) {
//            System.out.println(entry.toString());
//            System.out.println(entry.getKey());
            if (entry.getKey().equals("payload")) {
                Map<String, Object> payload = (Map) entry.getValue();
                for (Map.Entry<String, Object> entry1 : payload.entrySet()) {
//                    System.out.println(entry1.toString());
//                    System.out.println(entry1.getKey());
//                    System.out.println(entry1.getValue());
                    if (entry1.getKey().equals("smdrPharmacies")) {
                        if (entry1.getValue() instanceof ArrayList) {
                            ArrayList smdrPharmacies = (ArrayList) entry1.getValue();
                            for (Iterator iterator = smdrPharmacies.iterator(); iterator.hasNext(); ) {
//                                Object next = iterator.next();
//                                System.out.println(next.toString());
//                                Map<String, Object> smdrPharmacy = (Map) iterator.next();
//                                for (Map.Entry<String, Object> entry2 : smdrPharmacy.entrySet()) {
////                                    System.out.println(entry2.toString());
//                                    System.out.print("String " + entry2.getKey() + ";");
//                                    System.out.println();
//                                }
                                final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
                                final Voter voter = mapper.convertValue(iterator.next(), Voter.class);
                                t.add(voter);
//                                Pharmacy pharmacy=(Pharmacy) iterator.next();
//                                System.out.println(pharmacy);
                            }

                        }
                    } else {
                        if (entry1.getKey().equals("exceptionMessage")) {
                            Log.e(TAG, "getData: " + entry1.getValue());
                            Log.e(TAG, "getData: " + entry1.toString());
                            setMsg(entry1.getValue().toString());
                            break;
                        }
                    }
                }
            } else {
                Log.e(TAG, "getData: " + entry.getKey() + " " + entry.getValue());
            }

        }
//        }
        return t;
    }

    public static String SERVER_enc = "http://46.209.222.148/hixsec/rest/sec/pharmacy-support";

    public ArrayList<Voter> getData(String lat, String lon, String stDist) {
        this.lat=Double.parseDouble(lat);
        this.lon=Double.parseDouble(lon);
        ArrayList<Voter> t = new ArrayList<>();
        try {
            RestTemplate restsec = new RestTemplate();
            restsec.getMessageConverters().add(new StringHttpMessageConverter());
            String getForObject = restsec.getForObject(SERVER_enc, String.class);
            Log.e(TAG, "getData: " + getForObject);
            if (getForObject != null && getForObject.length() > 17) {
                String HixSenderId = "pharmacy-support";
                String plainId = getForObject.substring(0, 17);//new SimpleDateFormat("yyyyMMddHHmmssS").format(new java.util.Date());
                String encryptId = getForObject.substring(17);//jniBPJWSSec.passGen2(plainId.toString(), HixSenderId);
                RestTemplate rest = new RestTemplate();
                Map<String, Object> params = new HashMap<String, Object>();
                Map<String, Object> payloads = new HashMap<String, Object>();

                payloads.put("latitude", lat);
                payloads.put("longitude", lon);
                payloads.put("radious", stDist);
//                payloads.put("hixCode", "15000000123");

                params.put("payload", payloads);
//        params.put("payloadClass", "ix.microserver.healthpm.messages.pharmacy.GetPharmacyByHixCodeActionRequest");
                params.put("payloadClass", "ix.microserver.healthpm.messages.pharmacy.GetNearPharmaciesByLocationActionRequest");
                params.put("senderId", HixSenderId);

                params.put("plainId", plainId);
                params.put("encryptedId", encryptId);
                params.put("receiverId", "hix");
                params.put("service", "healthprm");
//          params.put("action", "getPharmacyByHix");
                params.put("action", "getNearPharmaciesByLocation");
                params.put("version", version);
                params.put("timeout", timeout);
//        System.out.println(params.toString());
//        for (int i = 0; i < 5; i++) {
//
                try {
                    rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    Map<String, Object> response = rest.postForObject(SERVER, params, Map.class);
                    Log.e(TAG, "getData: " + response.toString());
                    t = getPharmacies(response);
//                for (Iterator<Pharmacy> iterator = t.iterator(); iterator.hasNext(); ) {
//                    System.out.println(iterator.next().toString());
//
//                }
                } catch (RestClientException e) {
                    Log.e(TAG, "getData: " + "no response " + e.getCause().getMessage());
                }
            } else {

            }
        } catch (RestClientException e

                ) {
            setMsg(e.getMessage());
        }

        return t;
    }

    static String exceptionMsg = "";

    private static void setMsg(String toString) {
        exceptionMsg = toString;
    }

}
