package sg.edu.nyp.hackathon.activity;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sg.edu.nyp.backend.userAchievementApi.UserAchievementApi;
import sg.edu.nyp.backend.userAchievementApi.model.UserAchievement;
import sg.edu.nyp.backend.userApi.UserApi;
import sg.edu.nyp.hackathon.R;


public class WhatsHappeningActivity extends ActionBarActivity {
    ListView lvWhatsHappening;
    List<UserAchievement> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_whats_happening);
        lvWhatsHappening = (ListView) findViewById(R.id.lvWhatsHappening);
        setupAPIS();
        try {
            items = new GetLatestHappenings().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        lvWhatsHappening.setAdapter(new BaseAdapter() {
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
                LayoutInflater inflater = getLayoutInflater();
                View row = inflater.inflate(R.layout.single_happening, parent, false);

                ImageView ivIcon = (ImageView) row.findViewById(R.id.ivIcon);
                TextView tvDescription = (TextView) row.findViewById(R.id.tvDescription);

                tvDescription.setText(items.get(position).getUser().getName()+" has gained "+items.get(position).getAchievements().getAchievementTitle());
                return row;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_whats_happening, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private UserAchievementApi userAchApi = null;
    public void setupAPIS(){
        if(userAchApi == null) {
            UserAchievementApi.Builder endpoint = new UserAchievementApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
            //endpoint.setRootUrl("http://192.168.1.4:8080/_ah/api");
            endpoint.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
            userAchApi = endpoint.build();
        }
    }
    public class GetLatestHappenings extends AsyncTask<String, Void, List<UserAchievement>>{

        @Override
        protected List<UserAchievement> doInBackground(String... params) {
            try {
                return userAchApi.getRecentAchievements().execute().getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
