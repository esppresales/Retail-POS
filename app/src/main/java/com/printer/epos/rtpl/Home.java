package com.printer.epos.rtpl;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.printer.epos.rtpl.Utility.CustomDialog;
import com.printer.epos.rtpl.Utility.DialogButtonListener;
import com.printer.epos.rtpl.Utility.FragmentUtils;
import com.printer.epos.rtpl.Utility.SavePreferences;
import com.printer.epos.rtpl.Utility.Util;
import com.printer.epos.rtpl.Utility.WebServiceCalling;
import com.printer.epos.rtpl.reports.DaySalesFragment;
import com.printer.epos.rtpl.reports.ProductThresholdFragment;
import com.printer.epos.rtpl.reports.ReportsFragment;
import com.printer.epos.rtpl.reports.StaffTimeTrackingFragment;
import com.printer.epos.rtpl.reports.StockInHandFragment;
import com.printer.epos.rtpl.reports.charts.ProductPerformanceFragment;
import com.printer.epos.rtpl.reports.charts.SalesChartFragment;
import com.printer.epos.rtpl.services.FetchSettingsService;
import com.printer.epos.rtpl.services.MinStockAlertNotificationService;
import com.printer.epos.rtpl.services.SettingsReceiver;

/**
 * Created by ranosys-puneet on 31/3/15.
 */
public class Home extends FragmentActivity implements ItemListFragment.Callbacks {

    private SlidingPaneLayout mSlidingPanel;

    private Button homeButton;
    public Button saveButton;
    private Button searchButton;
    public ImageButton backButton;
    public EditText SearchBarET;
    private TextView title;
    public ImageView mStatus;

    public static String STORE_NAME = "";
    private SettingsReceiver mReceiver;


    @Override
    protected void onStart() {
        super.onStart();


        /*float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 79,
                getResources().getDisplayMetrics());

        Log.i("Length in pixels---->",""+px);
*/
        //start service for min stock alert notification.
        startService(new Intent(this, MinStockAlertNotificationService.class));
        //start service for settings .
        startService(new Intent(this, FetchSettingsService.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        SavePreferences mSavePreferences = UiController.getInstance().getSavePreferences();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.slidingPanel);

        mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(00);

        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.getLayoutParams().height = (int) (deviceHeight * .1f);

        title = (TextView) findViewById(R.id.title);

        Button menu = (Button) findViewById(R.id.menu);
        RelativeLayout.LayoutParams menu_param = (RelativeLayout.LayoutParams) menu.getLayoutParams();
        menu_param.height = (int) (deviceHeight * .05f);
        menu_param.width = (int) (deviceHeight * .05f);
        menu_param.rightMargin = (int) (deviceWidth * .01f);
        menu.setLayoutParams(menu_param);

        homeButton = (Button) findViewById(R.id.home);
        RelativeLayout.LayoutParams home_param = (RelativeLayout.LayoutParams) homeButton.getLayoutParams();
        home_param.height = (int) (deviceHeight * .06f);
        home_param.width = (int) (deviceHeight * .06f);
        home_param.leftMargin = (int) (deviceWidth * .01f);
        homeButton.setLayoutParams(home_param);

        backButton = (ImageButton) findViewById(R.id.backButton);
        RelativeLayout.LayoutParams backButton_param = (RelativeLayout.LayoutParams) backButton.getLayoutParams();
        backButton_param.height = (int) (deviceHeight * .08f);
        backButton_param.width = (int) (deviceHeight * .08f);
        backButton_param.leftMargin = (int) (deviceWidth * .01f);
        backButton.setLayoutParams(backButton_param);

        mStatus = (ImageView) findViewById(R.id.statusIcon);
        RelativeLayout.LayoutParams mStatus_param = (RelativeLayout.LayoutParams) mStatus.getLayoutParams();
        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_offline);
        mStatus_param.height = bd.getBitmap().getHeight();
        mStatus_param.width = bd.getBitmap().getWidth();
        mStatus_param.leftMargin = (int) (deviceWidth * .08f);
        mStatus.setLayoutParams(mStatus_param);

        saveButton = (Button) findViewById(R.id.saveButton);
        RelativeLayout.LayoutParams saveButton_param = (RelativeLayout.LayoutParams) saveButton.getLayoutParams();
        saveButton_param.rightMargin = (int) (deviceWidth * .02f);
        saveButton.setLayoutParams(saveButton_param);

        searchButton = (Button) findViewById(R.id.search);
        RelativeLayout.LayoutParams search_param = (RelativeLayout.LayoutParams) searchButton.getLayoutParams();
        search_param.height = (int) (deviceHeight * .05f);
        search_param.width = (int) (deviceHeight * .05f);
        search_param.rightMargin = (int) (deviceWidth * .04f);
        searchButton.setLayoutParams(search_param);

        SearchBarET = (EditText) findViewById(R.id.SearchBarET);
        RelativeLayout.LayoutParams SearchET_param = (RelativeLayout.LayoutParams) SearchBarET.getLayoutParams();
        SearchET_param.height = (int) (deviceHeight * .08f);
        SearchET_param.width = (int) (deviceWidth * .25f);
        SearchET_param.rightMargin = (int) (deviceWidth * .04f);
        SearchBarET.setLayoutParams(SearchET_param);

        ((ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.item_list)).setActivateOnItemClick(true);


        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, "1");
        changeFragment(FragmentUtils.DashboardFragment, arguments, false, true/*, false*/);
        //}
    }

    public void setTitleText(String text) {
        title.setText(text);
    }

    public void setEnabledButtons(boolean home, boolean back, boolean save, boolean search) {
        if (home) {
            homeButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
        } else {
            homeButton.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
        }

        if (save) {
            saveButton.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.GONE);
        }

        if (search) {
            searchButton.setVisibility(View.VISIBLE);
        } else {
            searchButton.setVisibility(View.GONE);
        }


        if (SearchBarET.getVisibility() == View.VISIBLE) {
            SearchBarET.setVisibility(View.GONE);
            SearchBarET.setText("");
        }

    }

    private final SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener() {

        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub
            Util.hideSoftKeypad(Home.this);
//            getActionBar().setTitle(getString(R.string.app_name));
            homeButton.animate().rotation(0);
        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub
            Util.hideSoftKeypad(Home.this);
//            getActionBar().setTitle("Menu Titles");
            homeButton.animate().rotation(90);
        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub
            Util.hideSoftKeypad(Home.this);
        }

    };

//    public void backClick(View view) {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("AddCustomerFragment", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("AddCustomerFragment", "nothing on backstack, calling super");
//        }
//    }

    public void homeClick(View v) {
        Util.hideSoftKeypad(this);
        if (mSlidingPanel.isOpen()) {
            homeButton.animate().rotation(0);
            mSlidingPanel.closePane();
            // getActionBar().setTitle(getString(R.string.app_name));
        } else {
            homeButton.animate().rotation(90);
            mSlidingPanel.openPane();
//                    getActionBar().setTitle("Menu Titles");
        }
    }

    private void closePanel(){
        if (mSlidingPanel.isOpen()) {
            homeButton.animate().rotation(0);
            mSlidingPanel.closePane();
            // getActionBar().setTitle(getString(R.string.app_name));
        }
        Util.hideSoftKeypad(this);
    }

    public void SearchButtonCLick(View v) {

        if (SearchBarET.getVisibility() == View.GONE) {
            SearchBarET.setVisibility(View.VISIBLE);
            SearchBarET.setText("");
            SearchBarET.setFocusable(true);
        }
//        else {
//            SearchBarET.setVisibility(View.GONE);
//            SearchBarET.setText("");
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSlidingPanel.isOpen()) {
                    homeButton.animate().rotation(0);
                    mSlidingPanel.closePane();
//                    getActionBar().setTitle(getString(R.string.app_name));
                } else {
                    homeButton.animate().rotation(90);
                    mSlidingPanel.openPane();
//                    getActionBar().setTitle("Menu Titles");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        Util.hideSoftKeypad(this);
        // In two-pane mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a fragment transaction.
        homeClick(null);

        Bundle arguments = new Bundle();
        arguments.putString(OrderPreviewFragment.ARG_ITEM_ID, id);

        if (id.equals("1")) {
            changeFragment(FragmentUtils.DashboardFragment, arguments, false, true/*, false*/);
        } else if (id.equals("2")) {
            changeFragment(FragmentUtils.CustomerFragment, arguments, true, /*false, */true);
        } else if (id.equals("3")) {
            changeFragment(FragmentUtils.OrderListingFragment, arguments, true, /*false,*/ true);
        } else if (id.equals("4")) {
            changeFragment(FragmentUtils.ProductFragment, arguments, true, /*false, */true);
        } else if (id.equals("5")) {
            changeFragment(FragmentUtils.ProductCategoryFragment, arguments, true, /*false, */true);
        } else if (id.equals("6")) {
            changeFragment(FragmentUtils.StaffFragment, arguments, true, /*false, */true);
        } else if (id.equals("7")) {
            changeFragment(FragmentUtils.ReportsFragment, arguments, true, /*false, */true);
        } else if (id.equals("8")) {
            changeFragment(FragmentUtils.SettingsFragment, arguments, true, /*false, */true);
        } else if (id.equals("9")) {
            new CustomDialog().showTwoButtonAlertDialog(this, "Logout",
                    "Do you want to logout?", getString(R.string.ok_button), getString(R.string.cancel_button), android.R.drawable.ic_dialog_alert, new DialogButtonListener() {
                        @Override
                        public void onPositiveClick() {
                            WebServiceCalling.logout(Home.this);
                        }

                        @Override
                        public void onNegativeClick() {

                        }
                    });

        } else if (id.equals("10")) {
            shareLogFile();
        } else {
            // In single-pane mode, simply start the detail activity for the selected item ID.
            Intent detailIntent = new Intent(this, ItemDetailActivity.class);
            detailIntent.putExtra(OrderPreviewFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    private static BaseFragment fragment;

    public void changeImputMode(boolean isAdjustPan){
        if(isAdjustPan){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    public void changeFragment(final FragmentUtils fragmentUtils, final Bundle arguments,
                               final boolean isAddToBackStack, boolean clearBackStack) {
        closePanel();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        // Code for Animation
//        ft.setCustomAnimations(R.anim.fade_out, R.anim.fade_in, R.anim.fade_out, R.anim.fade_in);

//        if (fragment != null)
//            ft.detach(fragment);

        if (clearBackStack)
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i)
                getSupportFragmentManager().popBackStack();

        if(fragmentUtils == FragmentUtils.NewOrderFragment){
            changeImputMode(true);
        } else {
            changeImputMode(false);
        }

        switch (fragmentUtils) {
            case DashboardFragment:
                fragment = new DashboardFragment();
                break;
            case CustomerFragment:
                fragment = new CustomerFragment();
                break;
            case OrderListingFragment:
                fragment = new OrderListingFragment();
                break;
            case ProductFragment:
                fragment = new ProductFragment();
                break;
            case ProductCategoryFragment:
                fragment = new ProductCategoryFragment();
                break;
            case StaffFragment:
                fragment = new StaffFragment();
                break;
            case ReportsFragment:
                fragment = new ReportsFragment();
                break;
            case SettingsFragment:
                fragment = new SettingsFragment();
                break;
            case AddStaffFragment:
                fragment = new AddStaffFragment();
                break;
            case AddProductFragment:
                fragment = new AddProductFragment();
                break;
            case NewOrderFragment:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                fragment = new NewOrderFragment();
                ((NewOrderFragment)fragment).resetAllValues();
                break;
            case AddCustomerFragment:
                fragment = new AddCustomerFragment();
                break;
            case OrderPreviewFragment:
                fragment = new OrderPreviewFragment();
                break;
            case ReturnOrderFragment:
                fragment = new ReturnOrderFragment();
                break;
            case OrderDetailFragment:
                fragment = new OrderDetailFragment();
                break;
            case ReceiptHeaderFragment:
                fragment = new ReceiptHeaderFragment();
                break;
            case PrinterConfigurationFragment:
                fragment = new PrinterConfigurationFragment();
                break;
            case CouponCodeFragment:
                fragment = new CouponCodeFragment();
                break;
            case CustomerDetailFragment:
                fragment = new CustomerDetailFragment();
                break;
            case ProductDetailFragment:
                fragment = new ProductDetailFragment();
                break;
            case StaffDetailFragment:
                fragment = new StaffDetailFragment();
                break;
            case StaffTimeTrackingFragment:
                fragment = new StaffTimeTrackingFragment();
                break;
            case SalesChartFragment:
                fragment = new SalesChartFragment();
                break;
            case ProductPerformanceFragment:
                fragment = new ProductPerformanceFragment();
                break;
            case ProductThresholdFragment:
                fragment = new ProductThresholdFragment();
                break;
            case StockInHandFragment:
                fragment = new StockInHandFragment();
                break;
            case DaySalesFragment:
                fragment = new DaySalesFragment();
                break;
        }
        if (arguments != null)
            fragment.setArguments(arguments);

        if (isAddToBackStack)
            ft.addToBackStack(fragment.getClass().getName() + "");

        ft.replace(R.id.item_detail_container , fragment);
        ft.commit();
//        ft.attach(fragment).add(R.id.item_detail_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        closePanel();
//        FragmentManager fm = getSupportFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            System.out.println(1);
//            fm.popBackStack();
//        } else {
//            System.out.println(2);
        super.onBackPressed();
//        }
    }

    private void shareLogFile() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + RetailPosLoging.FILE_PATH));
        startActivity(Intent.createChooser(intent, "Share logs"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
