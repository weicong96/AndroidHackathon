package sg.edu.nyp.hackathon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.myapplication.backend.userApi.model.Achievements;

import org.w3c.dom.Text;

import java.util.ArrayList;

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
        TextView tvText;
        if(view == null){
            tvText = new TextView(context);
            tvText.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvText.setTextSize(28);
        }else{
            tvText = (TextView) view;
        }
        tvText.setText(items.get(i).getAchievementID().toString());
        return tvText;
    }
}
