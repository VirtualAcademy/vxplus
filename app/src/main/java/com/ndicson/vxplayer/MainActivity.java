package com.ndicson.vxplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.support.v7.widget.Toolbar;



public class MainActivity extends AppCompatActivity implements OnlineFragment.OnFragmentInteractionListener ,LocalFragment.OnFragmentInteractionListener{

    private Toolbar mytoolbar;

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
    public TextView textView;

    private Fragment fragment1, fragment2;

    private List<Video> vlist;
    public String loc;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loc = "local";
//                    fragment = new LocalFragment();

//                    textView.setText("Local Video List");
                    fragment1 = LocalFragment.newInstance("local");
                    loadFragment(fragment1,"A");

                    return true;
                case R.id.navigation_dashboard:
                    loc = "online";
//                    from_Where_I_Am_Coming=1;
//                    fragment = new VideoFragment();

                    fragment2 = OnlineFragment.newInstance("online");

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
            mydb.populateDB(getApplicationContext(),"local");
            mydb.populateDB(getApplicationContext(),"online");
        }

        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mytoolbar);


        // load the store fragment by default
//        toolbar.setTitle(R.string.app_name);
        fragment1 = LocalFragment.newInstance("local");


        loadFragment(fragment1,"A");
//        textView = (TextView) fragment1.getView().findViewById(R.id.displaytxt);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater myInflater = getMenuInflater();
        Intent actIntent = getIntent();
        if(loc!=null){
            if(loc.equals("local")) {
                myInflater.inflate(R.menu.main_menu, menu);
            }
            else{
                myInflater.inflate(R.menu.online_menu, menu);
            }
        }else {
            myInflater.inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.addVideo:
//                readSD();

            case R.id.rdSD:
                readSD();
                return true;
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

    private void readSD() {
        VideoManager vmgr = new VideoManager();
        ArrayList<HashMap<String, String>> vls = vmgr.getPlayList();
        Video mySDvid = new Video();
        ArrayList<Video> li=new ArrayList<>();
        ArrayList<String>g=new ArrayList<>();
        for (HashMap<String, String> arr:vls
             ) {
            g.add(arr.get("videoTitle"));
            g.add(arr.get("videoPath"));
            mySDvid.setTitle(arr.get("videoTitle"));
            mySDvid.setTitle(arr.get("videoPath"));
            li.add(mySDvid);
        }
        playlistAdapter pls = new playlistAdapter(getApplicationContext(),li);
        Intent dle = new Intent(this,SDcard.class);
        dle.putStringArrayListExtra("data",g);
        startActivity(dle);
    }

    private void loadFragment(Fragment fragment,String letter) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);

//        // Commit changes
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
