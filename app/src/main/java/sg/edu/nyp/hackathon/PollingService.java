package sg.edu.nyp.hackathon;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.razer.android.nabuopensdk.NabuOpenSDK;
import com.razer.android.nabuopensdk.interfaces.Hi5Listener;
import com.razer.android.nabuopensdk.models.Hi5Data;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userApi.model.User;

/**
 * Created by admin on 18/7/15.
 */
public class PollingService extends IntentService {
    static NabuOpenSDK nabuOpenSDK = null;
    String nabuAPPID;
    String userID;
    public PollingService(){
        super("HelloPollingService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        setupAPIs();
        userID = intent.getExtras().getString("userID");

        System.out.println("Intent Running!");
        nabuOpenSDK = nabuOpenSDK.getInstance(this);
        nabuAPPID = getResources().getString(R.string.NABU_APP_ID);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

        nabuOpenSDK.getHi5Data(PollingService.this, c.getTimeInMillis(), System.currentTimeMillis(), new Hi5Listener() {
            @Override
            public void onReceiveData(Hi5Data[] hi5Datas) {
                System.out.println("Polling for data");
                for(Hi5Data hi5 : hi5Datas){
                    try {
                        new GivePoints().execute(userID, hi5.userID).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReceiveFailed(String s) {
                System.out.println(s);
            }
        });
    }

    private UserApi api = null;
    public void setupAPIs(){
        if(api == null) {
            UserApi.Builder endpoint = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            //endpoint.setRootUrl("http://192.168.1.4:8080/_ah/api");
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            api = endpoint.build();
        }
    }

    private class GivePoints extends AsyncTask<String,Void, User> {
        @Override
        protected User doInBackground(String... user) {
            try {
                User newUser = api.addPoint(user[0], user[1]).execute();
                System.out.println("Insert data nw");
                return newUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //return null;
        }
    }
}
