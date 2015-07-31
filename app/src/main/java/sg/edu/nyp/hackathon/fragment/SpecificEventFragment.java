package sg.edu.nyp.hackathon.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.eventApi.model.Event;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 26/7/15.
 */
public class SpecificEventFragment extends Fragment{
    TextView tvTitle, tvDate, tvDescription, tvSupport, tvRequired;
    ToggleButton tbAttending;
    TextView tvContactInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_specific_events,null);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvSupport = (TextView) view.findViewById(R.id.tvSupport);
        tvRequired = (TextView) view.findViewById(R.id.tvRequired);
        tbAttending = (ToggleButton) view.findViewById(R.id.tbAttending);

        tvContactInfo = (TextView )view.findViewById(R.id.tvContactInfo);
        Bundle bundle = getArguments();
        String eventID = bundle.getString("eventID","");
        try {
            Event event = new GetSpecificEvent().execute(eventID).get();

            tvTitle.setText(event.getName());
            tvDescription.setText(event.getDescription());

            tvSupport.setText(String.valueOf(event.getAlreadyAttending()));
            tvRequired.setText(String.valueOf(event.getAttending()));
            tvContactInfo.setText(event.getContactInfo()+"/n"+event.getContactPerson());

            Date date = new Date();
            date.setTime(event.getDate().longValue());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            tvDate.setText(format.format(date));


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return view;
    }

    private class GetSpecificEvent extends AsyncTask<String, Void, Event>{

        @Override
        protected Event doInBackground(String... params) {
            try {
                return ApisProvider.getEventApi().get(Long.valueOf(params[0])).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
