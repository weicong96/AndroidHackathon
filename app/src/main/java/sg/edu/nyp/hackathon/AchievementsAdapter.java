package sg.edu.nyp.hackathon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import sg.edu.nyp.backend.userAchievementApi.model.Achievements;


/**
 * Created by Administrator on 7/12/15.
 */
public class AchievementsAdapter extends BaseAdapter {
    ArrayList<Achievements> items;
    Context context;

    public AchievementsAdapter(Context context, ArrayList<Achievements> items){
        this.context = context;
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView ivItems;
        if(view == null){
            ivItems = new ImageView(context);

            ivItems.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        }else{
            ivItems = (ImageView) view;
        }
        if(items.get(i).getAchievementID().longValue() == 1){
            ivItems.setImageDrawable(context.getResources().getDrawable(R.drawable.gold));
        }

        if(items.get(i).getAchievementID().longValue() == 2){
            ivItems.setImageDrawable(context.getResources().getDrawable(R.drawable.silver));
        }
        if(items.get(i).getAchievementID().longValue() == 3){
            ivItems.setImageDrawable(context.getResources().getDrawable(R.drawable.bronze));
        }

        return ivItems;
    }
}
