package net.behpardaz.voting.activities.tabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.VoterReq;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class SwipeListAdapter extends BaseAdapter{
    private static final String TAG ="SwipeListAdapter" ;
    private Activity activity;
    private LayoutInflater inflater;
    private List<VoterReq> movieList;
    private String[] bgColors;

    public SwipeListAdapter(Activity activity, List<VoterReq> movieList) {
        this.activity = activity;
        this.movieList = movieList;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
    }

    public ArrayList<VoterReq> getList() {
        return new ArrayList<VoterReq>(movieList);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int location) {
        return movieList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_row, parent,false);
        }
        Log.e(TAG, "getView: position "+position );
        ImageView serial = (ImageView) view.findViewById(R.id.originID);
        TextView title = (TextView) view.findViewById(R.id.pharmacyTitle);

//        serial.setImageBitmap(BitmapFactory.decodeFile(movieList.get(position).getImagePath(),setPic(serial)));
        title.setText(movieList.get(position).getTitle());

        String color = bgColors[position % bgColors.length];
        serial.setBackgroundColor(Color.parseColor(color));
        // Set data in listView
        final VoterReq dataSet = (VoterReq) movieList.get(position);

        dataSet.setListItemPosition(position);

        if (!dataSet.isHaveImage()) {
//            Picasso.with(activity).load(R.mipmap.ic_launcher).resize(50, 50).into(serial);

//            Bitmap icon = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
//            serial.setImageBitmap(icon);
//            if(icon!=null)
//            {
//                icon.recycle();
//                icon=null;
//            }
        } else {
//            Picasso.with(activity).load(new File(dataSet.getImagePath())).resize(50, 50).into(serial);

//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//
//            Bitmap icon =BitmapFactory.decodeFile(dataSet.getImagePath(),options);
//            serial.setImageBitmap(icon);
//            if(icon!=null)
//            {
//                icon.recycle();
//                icon=null;
//            }
        }
        Log.e(TAG, "getView: dataSet.getImagePath() "+dataSet.getImagePath() );

        title.setText(dataSet.getLabel());
        title.setText(dataSet.getSubtext());
        serial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call parent method of activity to click image
//                ((TakePicture) activity).captureImage(dataSet.getListItemPosition(), dataSet.getLabel() + "" + dataSet.getSubtext());
            }
        });

        return view;
    }

    private BitmapFactory.Options setPic(ImageView mImageView) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		return bmOptions;
    }



}