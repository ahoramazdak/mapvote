package com.imagelistview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.imagelistview.adapter.CustomImageAdapter;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.AlbumStorageDirFactory;
import net.behpardaz.voting.activities.BaseAlbumDirFactory;
import net.behpardaz.voting.activities.FroyoAlbumDirFactory;
import net.behpardaz.voting.activities.MainActivity;
import net.behpardaz.voting.activities.VoterReq;
import net.behpardaz.voting.mgmt.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class TakePicture extends AppCompatActivity {

    private static final int REQUEST_PERMISSION =1 ;
    private static final String TAG = "TakePicture";
    CustomImageAdapter customImageAdapter;
    ArrayList<VoterReq> getSets;
    ListView listView;
    SessionManager manager;
    // Temp save listItem position
    int position;

    int imageCount;
    String imageTempName;
    String[] imageFor;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updates_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        listView = (ListView) findViewById(R.id.listView);
        getSets = new ArrayList<VoterReq>();
        manager=new SessionManager(this);
        getSets.addAll(manager.getList());
        imageFor = getResources().getStringArray(R.array.imageFor);
//        if(getSets.isEmpty())
        newReq();

//        customImageAdapter = new CustomImageAdapter(getSets, TakePicture.this);
        listView.setAdapter(customImageAdapter);
    }


    /**
     * Capture Image and save into database
     */

    public void captureImage(int pos, String imageName) {
        position = pos;
        imageTempName = imageName;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            // Callback onRequestPermissionsResult interceptado na Activity MainActivity
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MainActivity.REQUEST_CAMERA);
        } else {
            startActivityForResult(intent, 100);
        }
    }

    /**
     * Set capture image to database and set to image preview
     *
     * @param data
     */
    private void onCaptureImageResult(Intent data) {

        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");

        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Explain to the user why we need to read the contacts
//            }

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant
            Log.e(TAG, "onCaptureImageResult: "+imageTempName );


        }else {
            Log.e(TAG, "onCaptureImageResult: "+imageTempName );
            Uri tempUri = getImageUri(getApplicationContext(), imageBitmap, imageTempName);

//            galleryAddPic();
            String picturePath = getRealPathFromURI(tempUri);
            customImageAdapter.setImageInItem(position, imageBitmap, picturePath);
            manager.saveList(getSets);
            if(getSets.size()<=position+1)
            newReq();
            Log.e(TAG, "onCaptureImageResult: list size is "+ getSets.size());

        }
    }
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.e("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.e(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }
    private File setUpPhotoFile() {
        File f=null;
        try {
            f = createImageFile();
            imageTempName = f.getAbsolutePath();
        }catch (IOException e){
            e.printStackTrace();
        }
        return f;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(imageTempName);
        Uri contentUri = Uri.fromFile(f);

        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void newReq() {
        VoterReq inflate = new VoterReq();
        // Global Values
        inflate.setUid(String.valueOf(getSets.size()));

        inflate.setLabel("رای دهنده");
        inflate.setHaveImage(false);
        inflate.setSubtext(String.valueOf(getSets.size()+1));
        inflate.setStatus(true);

        getSets.add(inflate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == 100 &&data!=null) {
                onCaptureImageResult(data);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String imageName) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } finally {
            cursor.close();
        }
    }

    public Bitmap convertSrcToBitmap(String imageSrc) {
        Bitmap myBitmap = null;
        File imgFile = new File(imageSrc);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }

}