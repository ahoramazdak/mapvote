
/**
 * Created by amin on 23/11/16.
 */

package com.imagelistview.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.ImageDetailActivity;
import net.behpardaz.voting.activities.VoterFragment;
import net.behpardaz.voting.activities.VoterReq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aneh 3 on 25-06-2015.
 */
public class CustomImageAdapter extends BaseAdapter {

    private static final String TAG ="CustomImageAdapter";
    List<VoterReq> _data;
    VoterFragment _c;
    ViewHolder v;
    public ArrayList<VoterReq> getList() {
        return new ArrayList<VoterReq>(_data);
    }

    public CustomImageAdapter(List<VoterReq> getData, VoterFragment context) {
        _data = getData;
        _c = context;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater li = (LayoutInflater) _c.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.newlistlayout, parent,false);
            v = new ViewHolder();
            v.clickImage = (ImageButton) view.findViewById(R.id.capture);
            v.removeImage = (ImageButton) view.findViewById(R.id.cancel);
            v.parcelName = (TextView) view.findViewById(R.id.parcelName);
            v.label = (TextView) view.findViewById(R.id.imageFor);
            v.imageView = (ImageView) view.findViewById(R.id.imgPrv);

            view.setTag(v);
        } else {
            view = convertView;
        }
        v=(ViewHolder)view.getTag();

        // Set data in listView
        final VoterReq dataSet = (VoterReq) _data.get(position);

        dataSet.setListItemPosition(position);

        if (!dataSet.isHaveImage()) {
//            Bitmap icon = BitmapFactory.decodeResource(_c.getResources(), R.mipmap.ic_launcher);
//            v.imageView.setImageBitmap(icon);
//            Picasso.with(_c.getContext()).load(R.mipmap.ic_launcher).into(v.imageView);
//            if(icon!=null)
//            {\
//                icon.recycle();
//                icon=null;
//            }
        } else {
            final File file=new File(dataSet.getImagePath());

            Picasso.get().load(file).resize(50, 50).into(v.imageView);



            v.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent i = new Intent(_c.getActivity(), ImageDetailActivity.class);
                    i.putExtra(ImageDetailActivity.EXTRA_IMAGE, dataSet.getImagePath());
                    _c.startActivity(i);

//                    Intent intent=new Intent(Intent.ACTION_VIEW);
//                    Log.e(TAG, "onClick: imagePath "+dataSet.getImagePath() );
//                    Uri data = Uri.parse(file.getPath());
//                    intent.setDataAndType(data, "image/*");
//                    _c.getContext().startActivity(intent); /** replace with your own uri */
                }
            });
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//
//            Bitmap icon =BitmapFactory.decodeFile(dataSet.getImagePath(),options);
//            v.imageView.setImageBitmap(icon);

//            if(icon!=null)
//            {
//                icon.recycle();
//                icon=null;
//            }
//            v.imageView.setImageBitmap(dataSet.getImage());
        }
        v.parcelName.setText(dataSet.getLabel());
        v.label.setText(dataSet.getSubtext());
        if (dataSet.isStatus()) {
            v.clickImage.setVisibility(View.VISIBLE);
            v.removeImage.setVisibility(View.GONE);

        } else {
            v.removeImage.setVisibility(View.VISIBLE);
            v.clickImage.setVisibility(View.GONE);
        }

        v.clickImage.setFocusable(false);
        v.removeImage.setFocusable(false);


        v.clickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call parent method of activity to click image
                ((VoterFragment) _c).captureImage(dataSet.getListItemPosition(), dataSet.getLabel() + "" + dataSet.getSubtext());
            }
        });

        v.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.setStatus(true);
                dataSet.setHaveImage(false);
                notifyDataSetChanged();
            }
        });


        return view;
    }

    /**
     * @param position Get position of of object
     * @param imageSrc set image in imageView
     */
    public void setImageInItem(int position, Bitmap imageSrc, String imagePath) {
        VoterReq dataSet = (VoterReq) _data.get(position);
        dataSet.setImage(imageSrc);
        dataSet.setImagePath(imagePath);
        dataSet.setStatus(false);
        dataSet.setHaveImage(true);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView label, parcelName;
        ImageButton clickImage, removeImage;
    }

}