package sg.edu.nyp.hackathon.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.NabuAuthListener;
import com.razer.android.nabuopensdk.interfaces.UserIDListener;
import com.razer.android.nabuopensdk.interfaces.UserProfileListener;
import com.razer.android.nabuopensdk.models.Scope;
import com.razer.android.nabuopensdk.models.UserProfile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import me.pushy.sdk.Pushy;
import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;
import sg.edu.nyp.hackathon.RegisterForPushy;
import sg.edu.nyp.hackathon.activity.MainActivity;


public class LoginActivity extends ActionBarActivity {
    static NabuOpenSDK nabuOpenSDK = null;
    String nabuAPPID;
    LoginUtils loginUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupAPIS();
        loginUtils = LoginUtils.getInstance(this);
        loginUtils.loginFromDevice();

        Pushy.listen(this);

        if(loginUtils.isLoggedIn()){

            //If logged in , continue with rest of app
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        nabuOpenSDK = nabuOpenSDK.getInstance(this);
        nabuAPPID = getResources().getString(R.string.NABU_APP_ID);

        TextView tvLoginProgress = (TextView) findViewById(R.id.tvLoginProgress);
        nabuOpenSDK.initiate(this, nabuAPPID, new String[]{Scope.COMPLETE},new NabuAuthListener() {
            @Override
            public void onAuthSuccess(String s) {
                nabuOpenSDK.getCurrentUserID(LoginActivity.this, new UserIDListener() {
                    @Override
                    public void onReceiveData(String s) {
                        try {
                            User user = new GetUser().execute(s).get();
                            if(user == null){
                                //Means user does not exist on database side, create here.
                                register();
                            }else{
                                loginUtils.login(s);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReceiveFailed(String s) {

                    }
                });


            }

            @Override
            public void onAuthFailed(String s) {

            }
        });
    }
    public void register(){
        //Check if new user, if not new user , get and create user profile
        nabuOpenSDK.getUserProfile(LoginActivity.this, new UserProfileListener() {
            @Override
            public void onReceiveData(UserProfile userProfile) {
            //For some reason CurrentProfile does not get me the information about ID
                final User user = new User();
                user.setPoints(0l);
                //user.(userProfile.avatarUrl);
                user.setName(userProfile.firstname+ " "+userProfile.lastname);

                nabuOpenSDK.getCurrentUserID(getBaseContext(), new UserIDListener() {
                    @Override
                    public void onReceiveData(String s) {
                        user.setRazerID(s);
                        try {
                            User newUser = new RegisterUser().execute(user).get();
                            loginUtils.login(newUser.getRazerID());

                            new RegisterForPushy(getApplicationContext(), newUser.getRazerID()).execute().get();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onReceiveFailed(String s) {

                    }
                });

            }

            @Override
            public void onReceiveFailed(String s) {

            }
        });
    }
    private class RegisterUser extends AsyncTask<User, Void, User>{

        @Override
        protected User doInBackground(User... params) {

            try {
                return api.insert(params[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class GetUser extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            try {
                return api.get(params[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }
    }
    private UserApi api = null;
    public void setupAPIS(){
        if(api == null) {
            UserApi.Builder endpoint = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            //endpoint.setRootUrl("http://192.168.1.4:8080/_ah/api");
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });

            api = endpoint.build();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
