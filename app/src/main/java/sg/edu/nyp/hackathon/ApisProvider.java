package sg.edu.nyp.hackathon;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import sg.edu.nyp.backend.eventApi.EventApi;
import sg.edu.nyp.backend.helpRequestApi.HelpRequestApi;
import sg.edu.nyp.backend.rewardApi.RewardApi;
import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;
import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.backend.userEventApi.UserEventApi;
import sg.edu.nyp.backend.volunteerLocationsApi.VolunteerLocationsApi;

/**
 * Created by admin on 23/7/15.
 */
public class ApisProvider {
    //private static final String ENDPOINT = "http://192.168.44.215:8080/_ah/api";
    private static final String ENDPOINT = null;

    private static UserApi userApi = null;
    private static UserAchievementApi userAchievementApi = null;
    private static UserEventApi userEventApi = null;
    private static VolunteerLocationsApi volunteerLocationsApi = null;
    private static EventApi eventApi = null;
    private static RewardApi rewardApi = null;
    private static HelpRequestApi helpRequestApi = null;

    public static HelpRequestApi getHelpRequestApi(){
        if(helpRequestApi == null) {
            HelpRequestApi.Builder endpoint = new HelpRequestApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            helpRequestApi = endpoint.build();
        }
        return helpRequestApi;
    }
    public static RewardApi getRewardApi(){
        if(rewardApi == null) {
            RewardApi.Builder endpoint = new RewardApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            rewardApi = endpoint.build();
        }
        return rewardApi;
    }
    public static EventApi getEventApi(){
        if(eventApi == null) {
            EventApi.Builder endpoint = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            eventApi = endpoint.build();
        }
        return eventApi;
    }
    public static VolunteerLocationsApi getVolunteerLocationsApi(){
        if(volunteerLocationsApi == null) {
            VolunteerLocationsApi.Builder endpoint = new VolunteerLocationsApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            volunteerLocationsApi = endpoint.build();
        }
        return volunteerLocationsApi;
    }
    public static UserEventApi getUserEventApi(){
        if(userEventApi == null) {
            UserEventApi.Builder endpoint = new UserEventApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            if(ENDPOINT != null)
                endpoint.setRootUrl(ENDPOINT);

            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            userEventApi = endpoint.build();
        }
        return userEventApi;
    }
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
