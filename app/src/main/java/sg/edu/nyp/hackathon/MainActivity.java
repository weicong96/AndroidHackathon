package sg.edu.nyp.hackathon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.admin.myapplication.backend.userApi.UserApi;
import com.example.admin.myapplication.backend.userApi.model.Achievements;
import com.example.admin.myapplication.backend.userApi.model.User;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {

    SharedPreferences preferences;
    String PREFS_LOGIN = "login_info";
    CustomGridView gvAchievements;
    public User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAPIS();
        gvAchievements = (CustomGridView) findViewById(R.id.gvAchievements);
        try {
            preferences = getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
            //Login if not already logged in
            if(!preferences.getBoolean("loggedin", false)){
                User user = new User();
                user.setName("Wei Cong Helper");
                user.setEmail("twentyeightbytes@gmail.com");
                user.setNeedy(false);
                user.setLevel("Helper");

                user.setRazerID("HELPERID");
                user.setPoints(1l);

                user = new InsertUser().execute(user).get();
                login(user.getRazerID(), user.getName(), user.getEmail());
            }
            user = new GetUser().execute(preferences.getString("razerID", "")).get();
            List<Achievements> fromWeb = new GetAchievements().execute(user).get();
            if(fromWeb == null)
                fromWeb = new ArrayList<Achievements>();
            ArrayList<Achievements> achievements = new ArrayList<Achievements>(fromWeb);
            gvAchievements.setAdapter(new AchievementsAdapter(getApplicationContext(),achievements));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void login(String razerID, String name, String email){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("loggedin", true);
        editor.putString("razerID", razerID);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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
        }else if(id == R.id.shake){
            shook(user);
        }else if(id == R.id.activity){
            Intent intent = new Intent(this, PostsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void shook(User user){//This method handles what happens when the other user shakes hand with you.
        //Add Points
        try {
            new GivePoints().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Add Activities
        //Share to facebook?
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
    private class GetUser extends AsyncTask<String, Void ,User>{

        @Override
        protected User doInBackground(String... id) {
            try {
                if(id[0].equals("")){
                    System.out.println("NULL ID");
                }
                return api.get(id[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class GetAchievements extends AsyncTask<User, Void, List<com.example.admin.myapplication.backend.userApi.model.Achievements>>{

        @Override
        protected List<com.example.admin.myapplication.backend.userApi.model.Achievements> doInBackground(User... users) {
            try {
                List<com.example.admin.myapplication.backend.userApi.model.Achievements> list = api.getAchievementForUser(users[0].getRazerID()).execute().getItems();

                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class GivePoints extends AsyncTask<User,Void, User>{
        @Override
        protected User doInBackground(User... params) {
            try {
                User newUser = api.addPoint(user.getRazerID(), "HELPEEID").execute();
                return newUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }
    }
    private class InsertUser extends AsyncTask<User, Void, User>{
        private UserApi api = null;
        @Override
        protected User doInBackground(User... params) {
            if(api == null){
                UserApi.Builder endpoint = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
                endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
                api = endpoint.build();

                try {
                    User newUser = api.insert(params[0]).execute();
                    return newUser;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
            //return null;
        }
    }
}
