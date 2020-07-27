package net.behpardaz.voting.mgmt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import net.behpardaz.voting.R;
import net.behpardaz.voting.activities.AccountInfo;
import net.behpardaz.voting.activities.VoterReq;
import net.behpardaz.voting.activities.auth.LoginActivity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amin on 28/09/16.
 */

public class SessionManager {
    private static final String TAG = "SessionManager";

    //method to save status
    public void setPreferences(Context context, String key, String value) {

        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.vote), Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();

    }
    public String getPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.vote),     Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");
        return position;
    }
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 1;

    // Sharedpref file name
    private static final String PREF_NAME = "vote";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String USR_UID = "uid";
    // Email address (make variable public to access from outside)
    public static final String USR_TOKEN = "token";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Email address (make variable public to access from outside)
    public static final String categoryId = "votereq";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String uid, String token,String nam){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);


        // Storing name in pref
        editor.putString(USR_UID, uid);

        // Storing email in pref
        editor.putString(USR_TOKEN, token);

        // Storing email in pref
        editor.putString(KEY_NAME, nam);

        // commit changes
        editor.commit();
    }
public void saveList(List<VoterReq> list){
    Gson gson = new Gson();
    String ItemsJson = gson.toJson(list);
    editor.putString(categoryId, ItemsJson);
    editor.commit();
}
    public List<VoterReq> getList(){
        List<VoterReq> list = new ArrayList<>();
        if (pref.contains(categoryId) ){
            String jsonItems = pref.getString(categoryId, null);
            Gson gson = new Gson();
            VoterReq[] favoriteItems = gson.fromJson(jsonItems, VoterReq[].class);
            list = Arrays.asList(favoriteItems);

        }
        return list;
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
//        // user name
//        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
//
//        // user email id
//        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(USR_UID,pref.getString(USR_UID, ""));
        user.put(USR_TOKEN,pref.getString(USR_TOKEN, ""));
        user.put(KEY_NAME,pref.getString(KEY_NAME, "ناشناس"));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean do_login(String tel, String pass) {
        AuthenticateResp authenticateResp=null;
        Map<String, String> map = new HashMap<>();
        try {
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestJson = "req= {\"method\": \"authenticate\",\n"
                    + "  \"clientId\": \"api@zamanak.ir\",\n"
                    + "  \"clientSecret\": \"9AmbEG61AgW3CQoSV1p3A4tS9CZ\",\n"
                    + "  \"username\": \"" + tel + "\",\n"
                    + "  \"password\": \"" + pass + "\" }";
            Log.e(TAG, "do_login: "+ requestJson);

            HttpEntity<String> entity = new HttpEntity<String>(new String(requestJson.getBytes("utf8")), headers);
//            rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rest.getMessageConverters().add(new StringHttpMessageConverter());

            String postForObject = rest.postForObject("http://www.zamanak.ir/api/json-v4", entity, String.class);
            Log.e(TAG, "do_login: "+ postForObject.toString());
            final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper


            // convert JSON string to Map
            map = mapper.readValue(postForObject, new TypeReference<Map<String, String>>() {
            });

            authenticateResp = mapper.convertValue(map, AuthenticateResp.class);



        } catch (Exception ex) {
            if(map.containsKey("error"))
                setErrorMsg((String) map.get("error"));
//            ex.printStackTrace();
        }
        if(authenticateResp!=null){
            System.out.println(authenticateResp);
            createLoginSession(authenticateResp.getuid(),authenticateResp.gettoken(),tel);
            return true;
        }
//        if(map.containsKey("error"))
//            setErrorMsg((String) map.get("error"));
//        else
//            setErrorMsg("خطا در ارسال");
        return false;
    }
    static String errorMsg = "";

    public static String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String error) {
        errorMsg = error;
    }

    public AccountInfo get_account(String uid, String token) {
        AccountInfo authenticateResp=null;
        Map<String, String> map = new HashMap<>();
        try {
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestJson = "req= {\"method\": \"getAccountInfo\",\n"
                    + "  \"clientId\": \"api@zamanak.ir\",\n"
                    + "  \"clientSecret\": \"9AmbEG61AgW3CQoSV1p3A4tS9CZ\",\n"
                    + "  \"uid\": \"" + uid + "\",\n"
                    + "  \"token\": \"" + token + "\" }";
            Log.e(TAG, "get_account: "+ requestJson);

            HttpEntity<String> entity = new HttpEntity<String>(new String(requestJson.getBytes("utf8")), headers);
//            rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rest.getMessageConverters().add(new StringHttpMessageConverter());

            String postForObject = rest.postForObject("http://www.zamanak.ir/api/json-v4", entity, String.class);
            Log.e(TAG, "do_login: "+ postForObject.toString());
            final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper


            // convert JSON string to Map
            map = mapper.readValue(postForObject, new TypeReference<Map<String, String>>() {
            });


            authenticateResp = mapper.convertValue(map, AccountInfo.class);
            System.out.println(authenticateResp.toString());


        } catch (Exception ex) {
            if(map.containsKey("error"))
                setErrorMsg((String) map.get("error"));
//            ex.printStackTrace();
        }
        if(authenticateResp!=null){
            System.out.println(authenticateResp.toString());
            return authenticateResp;
        }

        return null;
    }
}
