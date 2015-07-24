package sg.edu.nyp.hackathon.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.UserReward;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;


public class RewardsListActivity extends ActionBarActivity {
    ListView lvRewards;
    List<UserReward> rewardItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_list);
        setupAPIS();

        lvRewards = (ListView) findViewById(R.id.lvRewards);
        try {
            rewardItems = new getRewardsList().execute(LoginUtils.getInstance(this).getUser().getRazerID()).get();
            if(rewardItems == null)
                rewardItems = new ArrayList<UserReward>();
            lvRewards.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return rewardItems.size();
                }

                @Override
                public Object getItem(int i) {
                    return rewardItems.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return i;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    View newView = getLayoutInflater().inflate(R.layout.single_incentive, null);

                    TextView tvImageName = (TextView) newView.findViewById(R.id.tvRewardName);
                    ImageView ivRewardImg = (ImageView) newView.findViewById(R.id.ivRewardImg);

                    tvImageName.setText(rewardItems.get(i).getReward().getName());

                    return newView;
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rewards_list, menu);
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
    private UserApi userApi = null;
    public void setupAPIS(){
        userApi = ApisProvider.getUserApi();
    }
    public class getRewardsList extends AsyncTask<String, Void, List<UserReward>>{

        @Override
        protected List<UserReward> doInBackground(String... razerID) {
            try {
                return userApi.getRewardsForUser(razerID[0]).execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

