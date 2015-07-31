package sg.edu.nyp.hackathon.fragment;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 25/7/15.
 */
public class NeedyFragment extends Fragment {
    Button btnRequest;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_needy_request, null);
        btnRequest = (Button) view.findViewById(R.id.btnHelp);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                try {
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    boolean status = new RequestHelp().execute(location).get();

                    btnRequest.setBackgroundColor(Color.GREEN);
                    btnRequest.setText("You have requested for help, system is looking for someone to help u..");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        this.setHasOptionsMenu(true);

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_needy, menu);
        //return true;
    }
    private class RequestHelp extends AsyncTask<Location, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Location... params) {
            Location location = params[0];
            UserApi api = ApisProvider.getUserApi();
            try {
                api.requestHelp(LoginUtils.getInstance(getActivity()).getUser().getRazerID(), location.getLatitude(),location.getLongitude()).execute();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
