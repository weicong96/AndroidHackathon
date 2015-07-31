package sg.edu.nyp.hackathon.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;
import sg.edu.nyp.backend.userAchievementApi.model.UserAchievement;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 25/7/15.
 */
public class WhatsHappeningFragment extends Fragment {
    ListView lvWhatsHappening;
    List<UserAchievement> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_whats_happening, null);

        lvWhatsHappening = (ListView) view.findViewById(R.id.lvWhatsHappening);
        setupAPIS();
        try {
            items = new GetLatestHappenings().execute().get();
            if(items == null)
                items = new ArrayList<UserAchievement>();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        lvWhatsHappening.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.size();
            }

            @Override
            public Object getItem(int position) {
                return items.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View row = inflater.inflate(R.layout.single_happening, parent, false);

                ImageView ivIcon = (ImageView) row.findViewById(R.id.ivIcon);
                TextView tvDescription = (TextView) row.findViewById(R.id.tvAchievementDescription);

                Drawable drawable = null;
                if(items.get(position).getAchievements().getAchievementID().longValue() == 1){
                    drawable = getResources().getDrawable(R.drawable.gold);
                }else if(items.get(position).getAchievements().getAchievementID().longValue() == 2){
                    drawable = getResources().getDrawable(R.drawable.silver);

                }else if(items.get(position).getAchievements().getAchievementID().longValue() == 3){
                    drawable = getResources().getDrawable(R.drawable.bronze);
                }
                ivIcon.setImageDrawable(drawable);


                tvDescription.setText(items.get(position).getUser().getName() + " has gained " + items.get(position).getAchievements().getAchievementTitle());
                return row;
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_whats_happening, menu);
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
    private UserAchievementApi userAchApi = null;
    public void setupAPIS(){
        userAchApi = ApisProvider.getUserAchievementApi();
    }
    public class GetLatestHappenings extends AsyncTask<String, Void, List<UserAchievement>> {

        @Override
        protected List<UserAchievement> doInBackground(String... params) {
            try {
                return userAchApi.getRecentAchievements().execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
