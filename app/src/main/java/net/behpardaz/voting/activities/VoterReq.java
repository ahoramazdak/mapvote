package net.behpardaz.voting.activities;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amin on 16/11/16.
 */

public class VoterReq implements Parcelable{
    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String imagePath="/data/user/0/behpardaz.net.shamim/cache/IMG_20161123_164128_759395190.jpg";
    private  String title="ثبت رای دهنده";
    private String id;
    String label;
    transient Bitmap image;

    public VoterReq() {

    }


    String imageSrc;

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public int getListItemPosition() {
        return listItemPosition;
    }

    public void setListItemPosition(int listItemPosition) {
        this.listItemPosition = listItemPosition;
    }

    int listItemPosition;

    public boolean isHaveImage() {
        return haveImage;
    }

    public void setHaveImage(boolean haveImage) {
        this.haveImage = haveImage;
    }

    boolean haveImage;

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    String subtext;
    boolean status;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public VoterReq(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        this.uid = data[0];
        this.listItemPosition=Integer.parseInt(data[1]);
        this.imagePath = data[2];
        this.title = data[3];
    }


    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.uid,
                this.imagePath,
                this.title});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public VoterReq createFromParcel(Parcel in) {
            return new VoterReq(in);
        }

        public VoterReq[] newArray(int size) {
            return new VoterReq[size];
        }
    };
}
