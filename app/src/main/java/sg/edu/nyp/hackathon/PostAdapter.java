/*package sg.edu.nyp.hackathon;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.admin.myapplication.backend.userApi.model.Post;

import java.util.List;

public class PostAdapter extends BaseAdapter{
    List<Post> items;
    Context context;
    public PostAdapter(Context context, List<Post> posts){
        this.context = context;
        this.items = posts;
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
        View myView = null;
        TextView tvTitle = null;
        if(view == null){
            LayoutInflater li = LayoutInflater.from(context);
            myView = li.inflate(R.layout.layout_lvpost, null);
           tvTitle = (TextView)myView.findViewById(R.id.tvTitle);
        }else{
            myView = (View) view;
        }

        tvTitle.setText(items.get(i).getTitle());

        return myView;
    }
}*/
