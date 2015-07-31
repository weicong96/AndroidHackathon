package sg.edu.nyp.hackathon.fragment;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.util.ArrayMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

/**
 * Created by admin on 25/7/15.
 */
public class MainFragment extends Fragment {
    SharedPreferences preferences;

    ProgressBar pgPoints;
    TextView tvProgress;
    TextView tvHelpedCount;
    TextView tvName;

    public User user;
    private LinearLayout llAchievementsByMonth;

    View view;
    //Allow the ui to refresh without reopening activity
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void refreshUI(){
        setupAPIS();
        tvHelpedCount = (TextView) view.findViewById(R.id.tvHelpedCount);

        llAchievementsByMonth.removeAllViews();


        try {
            // new TestNotification().execute().get();

            LoginUtils.getInstance(getActivity()).refreshUser();
            user = LoginUtils.getInstance(getActivity()).getUser();

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
                View template = LayoutInflater.from(getActivity()).inflate(R.layout.single_achievementmonth, null);
                TextView tvMonth = (TextView) template.findViewById(R.id.tvMonth);
                CustomGridView gridView = (CustomGridView) template.findViewById(R.id.gvAchievements);

                tvMonth.setText(monthText);
                ArrayList<Achievements> achievementsList = new ArrayList<Achievements>();
                for(ArrayMap<String, Object> userAch : achievements){
                    String id = (String)userAch.get("id");
                    String timeRecieved = (String) userAch.get("timeRecieved");
                    ArrayMap<String, String> achievementsRef = (ArrayMap<String, String>)userAch.get("achievements");
                    if(achievementsRef != null) {
                        Achievements achievement = new Achievements();
                        achievement.setAchievementID(Long.valueOf(achievementsRef.get("achievementID")));
                        achievement.setAchievementTitle(achievementsRef.get("achievementID"));

                        achievementsList.add(achievement);
                    }
                }
                gridView.setAdapter(new AchievementsAdapter(getActivity(), achievementsList));

                llAchievementsByMonth.addView(template);
            }
            UserCollection collection = new GetHelpees().execute(user.getRazerID()).get();
            if(collection != null)
                if(collection.getItems() != null)
                    tvHelpedCount.setText("Helped "+String.valueOf(collection.getItems().size())+" people this month");
            pgPoints.setProgress((int)(user.getPoints() / 100));
            tvProgress.setText(user.getPoints()+" / 100 points");
            tvName.setText(user.getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_main, null);

        llAchievementsByMonth = (LinearLayout) view.findViewById(R.id.llAchievementsByMonth);
        pgPoints = (ProgressBar) view.findViewById(R.id.pbPoints);
        tvProgress = (TextView) view.findViewById(R.id.tvProgress);
        tvName = (TextView) view.findViewById(R.id.tvName);

        refreshUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.shake){
            shook(user);
        }else if(id == R.id.activity){

        }else if(id == R.id.rewards){
            //Intent intent = new Intent(this, RewardsListActivity.class);
            //startActivity(intent);
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
}
