package sg.edu.nyp.hackathon.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.util.ArrayMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;
import sg.edu.nyp.backend.userAchievementApi.model.Achievements;
import sg.edu.nyp.backend.userAchievementApi.model.JsonMap;
import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;
import sg.edu.nyp.backend.userApi.model.UserCollection;
import sg.edu.nyp.hackathon.AchievementsAdapter;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.CustomGridView;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;


public class MainActivity extends ActionBarActivity {
    SharedPreferences preferences;

    ProgressBar pgPoints;
    TextView tvProgress;
    TextView tvHelpedCount;

    public User user;
    private LinearLayout llAchievementsByMonth;

    //Allow the ui to refresh without reopening activity
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void refreshUI(){
        setupAPIS();
        tvHelpedCount = (TextView) findViewById(R.id.tvHelpedCount);

        llAchievementsByMonth.removeAllViews();


        try {
           // new TestNotification().execute().get();

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
                String monthText = String.valueOf(month.get("month"));
                ArrayList<ArrayMap<String, Object>> achievements =  (ArrayList<ArrayMap<String, Object>>)month.get("items");

                //Inflater layout instead?
                View template = LayoutInflater.from(this).inflate(R.layout.single_achievementmonth, null);
                TextView tvMonth = (TextView) template.findViewById(R.id.tvMonth);
                CustomGridView gridView = (CustomGridView) template.findViewById(R.id.gvAchievements);

                tvMonth.setText(monthText);
                ArrayList<Achievements> achievementsList = new ArrayList<Achievements>();
                for(ArrayMap<String, Object> userAch : achievements){
                    String id = (String)userAch.get("id");
                    String timeRecieved = (String) userAch.get("timeRecieved");
                    ArrayMap<String, String> achievementsRef = (ArrayMap<String, String>)userAch.get("achievements");

                    Achievements achievement = new Achievements();
                    achievement.setAchievementID(Long.valueOf(achievementsRef.get("achievementID")));
                    achievement.setAchievementTitle(achievementsRef.get("achievementID"));

                    achievementsList.add(achievement);
                }
                gridView.setAdapter(new AchievementsAdapter(this, achievementsList));

                llAchievementsByMonth.addView(template);
            }
            UserCollection collection = new GetHelpees().execute(user.getRazerID()).get();
            if(collection != null)
                if(collection.getItems() != null)
                    tvHelpedCount.setText("Helped "+String.valueOf(collection.getItems().size())+" people this month");
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
/*
        Intent intent = new Intent(this, PollingService.class);
        intent.putExtra("userID", user.getRazerID());
        startService(intent);
*/
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
           Intent intent = new Intent(this, WhatsHappeningActivity.class);
           startActivity(intent);
        }else if(id == R.id.rewards){
            Intent intent = new Intent(this, RewardsListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void shook(User user){
        try {
            new GivePoints().execute(user.getRazerID(), "HELPEEID").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        refreshUI();
    }
    private class GivePoints extends AsyncTask<String,Void, User> {
        @Override
        protected User doInBackground(String... user) {
            try {
                User newUser = api.addPoint(user[0], user[1]).execute();
                System.out.println("Insert data nw");
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
        api = ApisProvider.getUserApi();
        userAchApi = ApisProvider.getUserAchievementApi();
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
    private class GetHelpees extends AsyncTask<String,Void, UserCollection>{
        @Override
        protected UserCollection doInBackground(String... params) {
            try {
                UserCollection newUser = api.getHelpees(params[0]).execute();
                return newUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }
    }

    private class TestNotification extends  AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL("https://pushy.me/push?api_key=677903558b207ae13205462e1c7b20609bb6f87e6c5869f38945e6ac6ed8566b");
                JsonObject object = new JsonObject();
                JsonArray array = new JsonArray();
                array.add(new JsonPrimitive("91f8fcf4492af30bb03a9d"));
                JsonObject dataObject = new JsonObject();

                dataObject.addProperty("message" , "Hello World 2");

                object.add("registration_ids", array);
                object.add("data", dataObject);
                String content = URLEncoder.encode(object.toString(), "UTF-8");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", String.valueOf(content.length()));
                OutputStream stream = con.getOutputStream();
                stream.write(content.getBytes());
                stream.flush();

                String allLines = "";
                String line;
                InputStream response = con.getInputStream();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    allLines += line+"/n";

                    Logger.getAnonymousLogger().log(Level.SEVERE, line);
                }
                stream.close();
                reader.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
