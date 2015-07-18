package sg.edu.nyp.hackathon;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.Hi5Listener;
import com.razer.android.nabuopensdk.models.Hi5Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userAchievementApi.model.Achievements;
import sg.edu.nyp.backend.userAchievementApi.model.JsonMap;
import sg.edu.nyp.backend.userAchievementApi.model.UserAchievement;
import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;
import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;


public class MainActivity extends ActionBarActivity {
    static NabuOpenSDK nabuOpenSDK = null;
    String nabuAPPID;

    SharedPreferences preferences;

    ProgressBar pgPoints;
    TextView tvProgress;

    public User user;
    private LinearLayout llAchievementsByMonth;

    //Allow the ui to refresh without reopening activity
    public void refreshUI(){
        setupAPIS();

        try {
            LoginUtils.getInstance(getBaseContext()).refreshUser();
            user = LoginUtils.getInstance(getBaseContext()).getUser();

            List<Map<String, Object>> fromWeb = new GetAchievements().execute(user).get();
            if(fromWeb == null)
                fromWeb = new ArrayList<Map<String, Object>>();
            ArrayList<Map<String, Object>> achGrpByMonth = new ArrayList<Map<String, Object>>(fromWeb);
            //gvAchievements.setAdapter(new AchievementsAdapter(getApplicationContext(),achievements));
            //Populate Achievements view with a list of items
            for(Map<String,Object> month : achGrpByMonth){
                //Months will never repeat because it has been handled on server side
                String monthText = (String)month.get("month");
                UserAchievement[] achievements = (UserAchievement[]) month.get("items");

                //Inflater layout instead?
                View template = LayoutInflater.from(this).inflate(R.layout.single_achievementmonth, null);
                TextView tvMonth = (TextView) template.findViewById(R.id.tvMonth);
                CustomGridView gridView = (CustomGridView) template.findViewById(R.id.gvAchievements);

                tvMonth.setText(monthText);
                ArrayList<Achievements> achievementsList = new ArrayList<Achievements>();
                for(UserAchievement userAch : achievements){
                    achievementsList.add(userAch.getAchievements());
                }
                gridView.setAdapter(new AchievementsAdapter(this, achievementsList));

                llAchievementsByMonth.addView(tvMonth);
                llAchievementsByMonth.addView(gridView);
            }

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

        llAchievementsByMonth = (LinearLayout) findViewById(R.id.llAchievementsByMonth);
        pgPoints = (ProgressBar) findViewById(R.id.pbPoints);
        tvProgress = (TextView) findViewById(R.id.tvProgress);

        refreshUI();

        nabuOpenSDK = nabuOpenSDK.getInstance(this);
        nabuAPPID = getResources().getString(R.string.NABU_APP_ID);

        //Handhsake can be done here or in a new thread
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){

            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH, -1);
                nabuOpenSDK.getHi5Data(MainActivity.this, c.getTimeInMillis(), System.currentTimeMillis(), new Hi5Listener() {
                    @Override
                    public void onReceiveData(Hi5Data[] hi5Datas) {
                        if(hi5Datas.length != 0)
                            System.out.println("Recieved");
                    }

                    @Override
                    public void onReceiveFailed(String s) {
                        System.out.println(s);
                    }
                });
            }
        }, 0, 5000);
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
           // Intent intent = new Intent(this, PostsActivity.class);
            //startActivity(intent);
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


    private class GetAchievements extends AsyncTask<User, Void, List<Map<String, Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(User... users) {
            try {
                List<JsonMap> listMap = userAchApi.getAchievementsForUser(users[0].getRazerID()).execute().getItems();
                ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                if(listMap != null)
                    for(JsonMap item : listMap){
                        Map<String,Object> single = new HashMap<String, Object>();
                        single.put("month", item.get("month"));
                        single.put("items", item.get("items"));
                        list.add(single);
                    }

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
    private UserAchievementApi userAchApi = null;
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
        if(userAchApi == null) {
            UserAchievementApi.Builder endpoint = new UserAchievementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            //endpoint.setRootUrl("http://192.168.1.4:8080/_ah/api");
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            userAchApi = endpoint.build();
        }
    }

}
