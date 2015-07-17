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
import android.widget.ProgressBar;
import android.widget.TextView;

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

    CustomGridView gvAchievements;
    ProgressBar pgPoints;
    TextView tvProgress;

    public User user;

    //Allow the ui to refresh without reopening activity
    public void refreshUI(){
        setupAPIS();

        try {
            LoginUtils.getInstance(getBaseContext()).refreshUser();
            user = LoginUtils.getInstance(getBaseContext()).getUser();

            List<Achievements> fromWeb = new GetAchievements().execute(user).get();
            if(fromWeb == null)
                fromWeb = new ArrayList<Achievements>();
            ArrayList<Achievements> achievements = new ArrayList<Achievements>(fromWeb);
            gvAchievements.setAdapter(new AchievementsAdapter(getApplicationContext(),achievements));


            pgPoints.setProgress((int)(user.getPoints() / 100));
            tvProgress.setText(user.getPoints()+" / 100 points");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gvAchievements = (CustomGridView) findViewById(R.id.gvAchievements);
        pgPoints = (ProgressBar) findViewById(R.id.pbPoints);
        tvProgress = (TextView) findViewById(R.id.tvProgress);

        refreshUI();
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

        //find some way to refresh UI here
        refreshUI();
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

}
