package net.behpardaz.voting.activities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amin on 28/10/16.
 */

public class AccountInfo implements Parcelable{
    public AccountInfo() {
    }

    public void setFname(String fname) {

        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    private String fname;

    @Override
    public String toString() {
        return "AccountInfo{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }

    private String lname;

    public String getBalance() {
        return balance;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    private String mobile;
    private String email;
    private String balance;
    public AccountInfo(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.fname = data[0];
        this.lname = data[1];
        this.mobile = data[2];
        this.email = data[3];
        this.balance = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {this.fname,
                this.lname,
                this.mobile,
                this.email,
                this.balance});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AccountInfo createFromParcel(Parcel in) {
            return new AccountInfo(in);
        }

        public AccountInfo[] newArray(int size) {
            return new AccountInfo[size];
        }
    };

}
