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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.helpRequestApi.model.HelpRequest;
import sg.edu.nyp.hackathon.ApisProvider;
import sg.edu.nyp.hackathon.LoginUtils;
import sg.edu.nyp.hackathon.R;

/**
 * Created by admin on 25/7/15.
 */
public class HelperNeedyViewFragment extends Fragment {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double lat, lng;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_helper_needy_view, null);
        Bundle intent = getArguments();
        lat = Double.parseDouble(intent.getString("LAT"));
        lng = Double.parseDouble(intent.getString("LNG"));

        LoginUtils.getInstance(getActivity()).loginFromDevice();
        if(LoginUtils.getInstance(getActivity()).isLoggedIn()){
            setUpMapIfNeeded();
        }
        return view;
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHelperNeedy))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    Long userID = -1l;
    HashMap<Marker, HelpRequest> hashMap = new HashMap<Marker,HelpRequest>();
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker"));
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.infowindow_template_needy, null);
                view.setBackgroundColor(Color.WHITE);
                Button btnHelp = (Button) view.findViewById(R.id.btnHelp);
                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);

                tvName.setText(hashMap.get(marker).getNeedyUser().getName());
                tvDistance.setText(String.valueOf(hashMap.get(marker).getDistanceFromU()));
                btnHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Post a request to backend for accepting here

                        try {
                            AcceptHelpRequest request = new AcceptHelpRequest();
                            request.razerID = LoginUtils.getInstance(getActivity()).getUser().getRazerID();
                            request.requestID = hashMap.get(marker).getRequestID();
                            request.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        try {
            LocationManager  locationManager = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            List<HelpRequest> requests = new GetNearbyHelp().execute(location.getLatitude(), location.getLatitude()).get();
            if(requests == null)
                requests = new ArrayList<HelpRequest>();
            for(HelpRequest request : requests){
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(request.getLat(), request.getLng())));
                hashMap.put(marker, request);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class GetNearbyHelp extends AsyncTask<Double, Void, List<HelpRequest>> {
        @Override
        protected List<HelpRequest> doInBackground(Double... params) {
            try {
                return ApisProvider.getHelpRequestApi().getNearbyHelpRequests(params[0], params[1]).execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class AcceptHelpRequest extends AsyncTask<Void, Void, Void>{
        public Long requestID;
        public String razerID;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                ApisProvider.getHelpRequestApi().acceptHelpRequest(requestID, razerID).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
