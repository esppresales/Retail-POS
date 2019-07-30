package com.printer.epos.rtpl;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.Utility.WebServiceHandler;
import com.printer.epos.rtpl.adapter.MenuAdapter;
import com.printer.epos.rtpl.dummy.DummyContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A list fragment representing a list of Items. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link OrderPreviewFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends Fragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemListFragment() {
    }

    public int deviceWidth;
    public int deviceHeight;

    ListView list;
    LinearLayout headerView;

    public TextView name;
    public TextView date;
//    ImageView Img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_item_list, container, false);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        list = (ListView) rootView.findViewById(R.id.list);
        name = (TextView) rootView.findViewById(R.id.name);
        date = (TextView) rootView.findViewById(R.id.date);

//        name.setText(Home.STORE_NAME);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM");
        date.setText(sdf.format(new Date()));


        headerView = (LinearLayout) rootView.findViewById(R.id.headerView);
        LinearLayout.LayoutParams headerView_param = (LinearLayout.LayoutParams) headerView.getLayoutParams();
        headerView_param.width = (int) (deviceWidth * .27f);
        headerView_param.height = (int) (deviceHeight * .1f);
        headerView.setLayoutParams(headerView_param);

        list.getLayoutParams().width = (int) (deviceWidth * .27f);

//        Img = (ImageView) rootView.findViewById(R.id.Img);
//        RelativeLayout.LayoutParams Img_param = (RelativeLayout.LayoutParams) Img.getLayoutParams();
//        Img_param.width = (int) (deviceHeight * .12f);
//        Img_param.height = (int) (deviceHeight * .12f);
//        Img.setLayoutParams(Img_param);

//        RelativeLayout ImgLayout = (RelativeLayout) rootView.findViewById(R.id.ImgLayout);
//        RelativeLayout.LayoutParams ImgLayout_param = (RelativeLayout.LayoutParams) ImgLayout.getLayoutParams();
//        ImgLayout_param.topMargin = (int) (deviceHeight * .03f);
//        ImgLayout_param.bottomMargin = (int) (deviceHeight * .015f);
//        ImgLayout.setLayoutParams(ImgLayout_param);

//        // TODO: replace with a real list adapter.
//        list.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(
//                getActivity(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                DummyContent.ITEMS));

        final MenuAdapter adapter = new MenuAdapter(getActivity(), DummyContent.getItems());

        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
                adapter.setSelection(position);
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        list.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            list.setItemChecked(mActivatedPosition, false);
        } else {
            list.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public void logoutClick(View v) {
        Util.hideSoftKeypad(getActivity());
        try {

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("access_token", "");

            new WebServiceCalling().callWS(getActivity(), UiController.appUrl + "logout", map,
                    new WebServiceHandler() {
                        @Override
                        public void onSuccess(String response) {
                            try {

                                JSONObject responseObj = new JSONObject(response);

                                JSONObject data = responseObj.getJSONObject("data");
                                String first_name = data.getString("first_name");
                                getActivity().finish();

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                RetailPosLoging.getInstance().registerLog(ItemListFragment.class.getName(), e);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (error != null)
                                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception ex) {

            ex.printStackTrace();
            RetailPosLoging.getInstance().registerLog(ItemListFragment.class.getName(), ex);
        }
    }
}
