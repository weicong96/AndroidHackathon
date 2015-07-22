package sg.edu.nyp.hackathon;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import sg.edu.nyp.backend.userApi.model.JsonObject;

import org.json.JSONException;

import java.io.IOException;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.exceptions.PushyException;
import sg.edu.nyp.backend.userApi.UserApi;

/**
 * Created by wei cong on 7/22/2015.
 */
public class RegisterForPushy extends AsyncTask<Void, Void, Exception> {
    Context context;
    String razerID;
    public RegisterForPushy(Context context, String razerID){
        super();
        this.context = context;
        this.razerID = razerID;
    }


    @Override
    protected Exception doInBackground(Void... voids) {
        try {
            String registrationId = Pushy.register(context);
            registerBackend(registrationId);
        } catch (PushyException e) {
            e.printStackTrace();
        }

        return null;
    }
    private UserApi api = null;
    public void registerBackend(String registrationId){
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
        JsonObject object = new JsonObject();
        object.put("razerID",razerID);
        object.put("regID",registrationId);
        try {
            api.registerNotification(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
