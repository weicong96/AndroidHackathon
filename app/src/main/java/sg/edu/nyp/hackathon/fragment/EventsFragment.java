package sg.edu.nyp.hackathon.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userEventApi.UserEventApi;
import sg.edu.nyp.backend.eventApi.model.Event;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 25/7/15.
 */
public class EventsFragment extends Fragment {
    UserEventApi api;
    List<Event> eventsList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_events_upcoming, null);

        ListView lvEntries = (ListView) view.findViewById(R.id.lvEntries);
        //Need to get data from events api
        try {
            eventsList = new GetEvents().execute().get();
            lvEntries.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return eventsList.size();
                }

                @Override
                public Object getItem(int position) {
                    return eventsList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.single_events, null);

                    TextView tvEventName = (TextView)view.findViewById(R.id.tvEventName);

                    tvEventName.setText(eventsList.get(position).getName());
                    return view;
                }
            });

            lvEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SpecificEventFragment fragment = new SpecificEventFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("eventID", String.valueOf(eventsList.get(position).getEventID()));
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();

                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return view;
    }

    private class GetEvents extends AsyncTask<Void,Void,List<Event>>{

        @Override
        protected List<Event> doInBackground(Void... params) {
            try {
                List<sg.edu.nyp.backend.eventApi.model.Event> eventList = ApisProvider.getEventApi().list().execute().getItems();
                return eventList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
