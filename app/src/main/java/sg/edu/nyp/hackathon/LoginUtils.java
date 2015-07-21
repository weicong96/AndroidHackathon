package sg.edu.nyp.hackathon;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;

/**
 * Created by admin on 15/7/15.
 */
public class LoginUtils {

    private String PREFS_LOGIN = "login_info";
    SharedPreferences preferences;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private static LoginUtils loginUtils;
    public static LoginUtils getInstance(Context context){
        if(loginUtils == null)
            loginUtils = new LoginUtils(context);
        return loginUtils;
    }
    public LoginUtils (Context context){
        preferences = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
        setupAPIS();
    }
    public void logout(String razerID){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("loggedin");
        editor.remove("razerID");
        editor.commit();
        this.user = null;
    }
    //This method attempts to login from device, if unsucessful
    public void loginFromDevice(){
        String razerID = preferences.getString("razerID", "");

        if(razerID != null && !razerID.equals("")){
            login(razerID);
        }
    }
    public void login(String razerID){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedin", true);
        editor.putString("razerID", razerID);
        editor.commit();

        try {
            this.user = new GetUser().execute(razerID).get();
            System.out.println("gg");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void refreshUser(){
        try {
            this.user = new GetUser().execute(this.user.getRazerID()).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private class GetUser extends AsyncTask<String, Void ,User> {

        @Override
        protected User doInBackground(String... id) {
            try {
                User user = api.get(id[0]).execute();
                System.out.println(user.getRazerID());
                return user;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private UserApi api = null;
    public void setupAPIS(){
        if(api == null) {
            UserApi.Builder endpoint = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            api = endpoint.build();
        }
    }
    public boolean isLoggedIn(){
        return preferences.getBoolean("loggedin", false);
    }

}
