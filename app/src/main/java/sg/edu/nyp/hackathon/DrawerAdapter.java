package sg.edu.nyp.hackathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 25/7/15.
 */
public class DrawerAdapter extends BaseAdapter {
    Context context;
    List<String> items;

    public DrawerAdapter(Context context, String[] items){
        this.context = context;
        this.items = Arrays.asList(items);
    }
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
        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_drawer_list_item,null);

        TextView tvName = (TextView) view.findViewById(R.id.tvName);

        tvName.setText(items.get(position));

        return view;
    }
}
