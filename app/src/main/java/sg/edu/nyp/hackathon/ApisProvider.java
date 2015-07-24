package sg.edu.nyp.hackathon;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;
import sg.edu.nyp.backend.userApi.UserApi;

/**
 * Created by admin on 23/7/15.
 */
public class ApisProvider {
    //private static final String ENDPOINT = "http://192.168.44.215:8080/_ah/api";
    private static final String ENDPOINT = null;

    private static UserApi userApi = null;
    private static UserAchievementApi userAchievementApi = null;
    public static UserApi getUserApi(){
        if(userApi == null) {
            UserApi.Builder endpoint = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            userApi = endpoint.build();
        }
        return userApi;
    }

    public static UserAchievementApi getUserAchievementApi(){
        if(userAchievementApi == null) {
            UserAchievementApi.Builder endpoint = new UserAchievementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            userAchievementApi = endpoint.build();
        }
        return userAchievementApi;
    }
}
