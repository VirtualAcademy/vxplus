package com.ndicson.vxplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements VideoFragment.OnFragmentInteractionListener {

    private ActionBar toolbar;

    private TextView mTextMessage;
    private int from_Where_I_Am_Coming = 0;
    public DBHelper mydb ;

    TextView vTitle ;
    TextView phone;
    TextView email;
    TextView street;
    TextView place;
    int id_To_Update = 0;
    ListView listView;
    ListAdapter adapter;
    private FragmentManager fragmentManager;

    private Fragment fragment1, fragment2;

    private List<Video> vlist;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    fragment = new LocalFragment();

//                    fragment1 = VideoFragment.newInstance("local");
                    loadFragment(fragment1,"A");

                    return true;
                case R.id.navigation_dashboard:
//                    from_Where_I_Am_Coming=1;
//                    fragment = new VideoFragment();

//                    fragment2 = VideoFragment.newInstance("online");

//                    fragment.fetchItems(vlist);
                    loadFragment(fragment2,"B");
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = DBHelper.getInstance(this);
        if(mydb.getVideosCount()==0){
            mydb.populateDB(getApplicationContext(),Video.ONLINEDATA);
            mydb.populateDB(getApplicationContext(),Video.LOCALDATA);
        }

        toolbar = getSupportActionBar();

        // load the store fragment by default
//        toolbar.setTitle(R.string.app_name);
        fragment1 = VideoFragment.newInstance("local");
        fragment2 = VideoFragment.newInstance("online");

        loadFragment(fragment1,"A");


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        Bundle extras = getIntent().getExtras();
//
//        if(from_Where_I_Am_Coming != 0) {
//            if(from_Where_I_Am_Coming == 1){
//                getMenuInflater().inflate(R.menu.main_menu, menu);
//            }
//            else{
//                getMenuInflater().inflate(R.menu.display_video, menu);
//            }
//        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.New:
//                Intent intent = new Intent(getApplicationContext(),DisplayVideo.class);
//                startActivity(intent);

//            case R.id.Edit_Video:
//                Button b = (Button)findViewById(R.id.button1);
//                b.setVisibility(View.VISIBLE);
//                return true;
//
//            case R.id.Delete_Video:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(R.string.deleteVideo)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                mydb.deleteVideo(id_To_Update);
//                                Toast.makeText(getApplicationContext(), "Deleted Successfully",
//                                        Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                                startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // User cancelled the dialog
//                            }
//                        });
//
//                AlertDialog d = builder.create();
//                d.setTitle("Are you sure");
//                d.show();
//
//                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadFragment(Fragment fragment,String letter) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) { // if the fragment is already in container
            ft.show(fragment);
        }else{ // fragment needs to be added to frame container
            ft.add(R.id.frame_container, fragment, letter);
        }
        // Hide fragment B
        if (letter.toUpperCase().equals("A")) { ft.hide(fragment2); }
        // Hide fragment C
        else if (letter.toUpperCase().equals("B")) { ft.hide(fragment1); }
        // Commit changes
        ft.commit();
        // load fragment
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
