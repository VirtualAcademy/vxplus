package com.ndicson.vxplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import android.widget.Toast;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
//
//import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OnlineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Video> videoList;
    private VideoAdapter mAdapter;
    private TextView textView;
    List<Video> vlist;
    //    private Context vFContext;
//
    private DBHelper dbHelper; //= new DBHelper(getContext());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "videolocation";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String vLocation;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OnlineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment OnlineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineFragment newInstance(String param1) {
        OnlineFragment fragment = new OnlineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoList = new ArrayList<>();
        mAdapter = new VideoAdapter(getActivity(), videoList);
        dbHelper = DBHelper.getInstance(getContext());
        if (getArguments() != null) {
            vLocation = getArguments().getString(ARG_PARAM1);
        }
//        List<Video> vList = dbHelper.getAllVideos();
//        fetchItems(vLocation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        textView = view.findViewById(R.id.displaytxt);

        textView.setText(vLocation.toUpperCase()+" VIDEOS");
        textView.setTextColor(getResources().getColor(R.color.bgBottomNavigation));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setClickable(true);
//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(),"id clicked: "+String.valueOf(v.getId()),Toast.LENGTH_SHORT).show();
//            }
//        });
////        vlist = dbHelper.getAllVideos();
        fetchItems(vLocation);
        return view;
    }

    public void fetchItems(String loc) {
        vlist = dbHelper.getGivenVideos(loc);
        videoList.clear();
        videoList.addAll(vlist);

        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onClick(View v) {
//        Log.i("view click","view");
//        if(v.getId()==R.id.thumbnail){
//            Toast.makeText(getContext(),"view click",Toast.LENGTH_SHORT).show();
//        }
//        Bundle bundle = new Bundle();
//        bundle.putInt("position", v.getId());
//        Intent intent = new Intent(getContext(),VideoPlayerActivity.class);
//        startActivity(intent,bundle);
//
//    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
