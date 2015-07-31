package sg.edu.nyp.hackathon;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import sg.edu.nyp.hackathon.fragment.EventsFragment;
import sg.edu.nyp.hackathon.fragment.HelperNeedyViewFragment;
import sg.edu.nyp.hackathon.fragment.MainFragment;
import sg.edu.nyp.hackathon.fragment.NeedyFragment;
import sg.edu.nyp.hackathon.fragment.RewardsListFragment;
import sg.edu.nyp.hackathon.fragment.WhatsHappeningFragment;


public class MainDrawerActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener {


    private String[] drawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private BaseAdapter mArrayAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle, mTitle;



    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        mDrawerTitle = mTitle = getTitle();

        LoginUtils.getInstance(getApplicationContext()).loginFromDevice();
        if(!isMyServiceRunning(PollingService.class)){
            getBaseContext().startService(new Intent(getBaseContext(), PollingService.class));
        }

        getSupportActionBar().setTitle("Helper");

        Bundle fromBundle = getIntent().getExtras();
        boolean isNeedy = fromBundle.getBoolean("NEEDY");

        drawerItems = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this, drawerItems));

        //Use any of one of them that works, one of them will have error
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7851A9")));
        // getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7851A9")));

        Intent intent = getIntent();
        if(intent.getStringExtra("LAT") != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new HelperNeedyViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("LAT", intent.getStringExtra("LAT"));
            bundle.putString("LNG", intent.getStringExtra("LNG"));

            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }else if(isNeedy){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new NeedyFragment();

            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
        }


        drawerItems = this.getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this , mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mArrayAdapter = new DrawerAdapter(this, drawerItems);
        mDrawerList.setAdapter(mArrayAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch(position){
                    //case 0:
                    //    fragment = new ListDataSourceFragment();
                    //    break;
                    case 0 :
                        fragment = new MainFragment();
                        break;
                    case 1:
                        fragment = new WhatsHappeningFragment();
                        break;
                    case 2:
                       fragment = new RewardsListFragment();
                        break;
                    case 3:
                        fragment = new EventsFragment();
                        break;
                }
                if(fragment != null){

                    for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                        getSupportFragmentManager().popBackStack();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();

                    mDrawerList.setItemChecked(position, true);
                    mDrawerList.setSelection(position);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                        setTitle(drawerItems[position]);
                    }
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void shouldDisplayHomeUp(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }
}
