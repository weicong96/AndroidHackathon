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

import sg.edu.nyp.backend.rewardApi.RewardApi;
import sg.edu.nyp.backend.rewardApi.model.Reward;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 25/7/15.
 */
public class RewardsListFragment extends Fragment {
    ListView lvRewards;
    List<Reward> rewardItems;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rewards_list, null);
        setupAPIS();

        lvRewards = (ListView) view.findViewById(R.id.lvRewards);
        try {
            rewardItems = new getRewardsList().execute(LoginUtils.getInstance(getActivity()).getUser().getRazerID()).get();
            if(rewardItems == null)
                rewardItems = new ArrayList<Reward>();
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
                    View newView = ((LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.single_incentive, null);

                    TextView tvImageName = (TextView) newView.findViewById(R.id.tvRewardName);
                    ImageView ivRewardImg = (ImageView) newView.findViewById(R.id.ivRewardImg);

                    ivRewardImg.setAdjustViewBounds(true);
                    Drawable drawable = null;
                    tvImageName.setText(rewardItems.get(i).getName());
                    switch(i){
                        case 0 :
                            drawable = getResources().getDrawable(R.drawable.braidtalk);
                            break;
                        case 1 :
                            drawable = getResources().getDrawable(R.drawable.burger_queen);
                            break;
                        case 2:
                            drawable = getResources().getDrawable(R.drawable.matsons);
                            break;
                        case 3:
                            drawable = getResources().getDrawable(R.drawable.buardian);
                            break;
                        case 4:
                            drawable = getResources().getDrawable(R.drawable.megasoft);
                            break;
                    }
                    ivRewardImg.setImageDrawable(drawable);

                    return newView;
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        this.setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_rewards_list, menu);
    }

    private RewardApi rewardApi = null;
    public void setupAPIS(){
        rewardApi = ApisProvider.getRewardApi();
    }
    public class getRewardsList extends AsyncTask<String, Void, List<Reward>> {

        @Override
        protected List<Reward> doInBackground(String... razerID) {
            try {
                return rewardApi.list().execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
