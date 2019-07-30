package com.printer.epos.rtpl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.printer.epos.rtpl.Utility.SavePreferences;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link OrderPreviewFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends FragmentActivity
        implements ItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    public int deviceWidth;
    public int deviceHeight;
    SavePreferences mSavePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mSavePreferences = UiController.getInstance().getSavePreferences();


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
        deviceHeight = displayMetrics.heightPixels;

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment)
                .commit();


        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, id);
            for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); ++i) {
                getFragmentManager().popBackStack();
            }

            if (id.equals("1")) {
                DashboardFragment fragment = new DashboardFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("2")) {
                StaffFragment fragment = new StaffFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("3")) {
                ProductFragment fragment = new ProductFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("4")) {
                ProductCategoryFragment fragment = new ProductCategoryFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("6")) {
                CustomerFragment fragment = new CustomerFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("5") || id.equals("7") || id.equals("8")) {
                OrderPreviewFragment fragment = new OrderPreviewFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("9")) {
                SettingsFragment fragment = new SettingsFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            } else if (id.equals("10")) {
             /*   new CustomDialog().showTwoButtonAlertDialog(this, "Logout",
                        "Do you want to logout?", getString(R.string.ok_button), getString(R.string.cancel_button), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                            @Override
                            public void onPositiveClick() {
                                mSavePreferences.removeAll();

                                startActivity(new Intent(ItemListActivity.this, LoginActivity.class));
                                finish();
                            }

                            @Override
                            public void onNegativeClick() {

                            }
                        });*/

            } else {
                // In single-pane mode, simply start the detail activity for the selected item ID.
                Intent detailIntent = new Intent(this, ItemDetailActivity.class);
                detailIntent.putExtra(OrderPreviewFragment.ARG_ITEM_ID, id);
                startActivity(detailIntent);
            }
        }
    }
}
