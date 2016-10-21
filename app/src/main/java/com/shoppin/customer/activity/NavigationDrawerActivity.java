package com.shoppin.customer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppin.customer.R;
import com.shoppin.customer.adapter.NavigationDrawerMenuAdapter;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.fragment.BaseFragment;
import com.shoppin.customer.fragment.CartFragment;
import com.shoppin.customer.fragment.ChangeSuburbFragment;
import com.shoppin.customer.fragment.HomeFragment;
import com.shoppin.customer.fragment.IUpdateFragment;
import com.shoppin.customer.fragment.MyAccountFragment;
import com.shoppin.customer.fragment.MyOrderListFragment;
import com.shoppin.customer.fragment.OfferFragment;
import com.shoppin.customer.fragment.StoreListFragment;
import com.shoppin.customer.fragment.UnderDevelopmentFragment;
import com.shoppin.customer.model.NavigationDrawerMenu;
import com.shoppin.customer.utils.IConstants.IPushNotification;
import com.shoppin.customer.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.shoppin.customer.database.IDatabase.IMap;
import static com.shoppin.customer.utils.IConstants.IDrawerMenu;

public class NavigationDrawerActivity extends BaseActivity {

    private static final String TAG = NavigationDrawerActivity.class.getSimpleName();

    @BindView(R.id.txtFragmentTitle)
    public TextView txtFragmentTitle;
    @BindView(R.id.txtCartCount)
    public TextView txtCartCount;

    /**
     * Container for all fragments
     */
    Fragment content;

    /**
     * For tool bar and left drawer
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.recyclerListNavigationDrawer)
    RecyclerView recyclerListNavigationDrawer;
    /**
     * Basically to change title in tool bar.
     * <p/>
     * First get the current fragment and then calling its onStart() method to
     * set title.
     */
    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {

        @Override
        public void onBackStackChanged() {
            // TODO Auto-generated method stub
            // Update your UI here.
            Log.i(TAG, "OnBackStackChangedListener");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager
                    .findFragmentById(R.id.contentFrame);
            if (currentFragment != null
                    && (currentFragment instanceof IUpdateFragment)) {
                Log.e(TAG, currentFragment.getClass().getSimpleName());
                ((BaseFragment) currentFragment).updateFragment();
            }
        }
    };
    /**
     * Menu drawer item click listener to set respective fragment
     */
    NavigationDrawerMenuAdapter.OnItemClickListener leftDrawerListItemClickListener = new NavigationDrawerMenuAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            if (view != null && view.getTag() != null) {
                Log.d(TAG, "menuName = " + view.getTag());

                int menuTagId = (int) view.getTag();
                Fragment newContent = null;
                switch (menuTagId) {

                    case IDrawerMenu.LOGIN_SIGNUP_ID:
                        login();
                        break;

                    case IDrawerMenu.WELCOME_ID:
                        newContent = new MyAccountFragment();
                        break;

                    case IDrawerMenu.CHANGE_SUBURB_ID:
//                        newContent = new ChangeSuburbFragment();
                        new ChangeSuburbFragment().show(getSupportFragmentManager(), "Change Suburb");
                        break;

                    case IDrawerMenu.NEAR_BY_STORES_ID:
                        newContent = new StoreListFragment();
                        break;

                    case IDrawerMenu.MY_ORDER_ID:
                        newContent = new MyOrderListFragment();
                        break;

                    case IDrawerMenu.CART_ID:
                        newContent = new CartFragment();
                        break;

                    case IDrawerMenu.OFFERS_ID:
                        newContent = new OfferFragment();
                        break;
                    case IDrawerMenu.ABOUT_US_ID:
                        newContent = new UnderDevelopmentFragment();
                        break;
                }
                if (newContent != null) {
                    switchContent(newContent, true);
                }
            }
        }
    };
    /**
     * To change suburb
     */
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<NavigationDrawerMenu> navigationDrawerMenuArrayList;
    private NavigationDrawerMenuAdapter drawerMenuAdapter;
    /**
     * For double back exit functionality
     */
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        ButterKnife.bind(this);

        Log.d(TAG, "suburb_id = " + DBAdapter.getMapKeyValueString(NavigationDrawerActivity.this, IMap.SUBURB_ID));
        Log.d(TAG, "suburb_name = " + DBAdapter.getMapKeyValueString(NavigationDrawerActivity.this, IMap.SUBURB_NAME));

        if (toolbar != null) {
//            toolbar.setNavigationIcon(R.drawable.menu_icon);
//            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.menu_icon));
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
        }
        initDrawer();

        onNewIntent(getIntent());

        // For the first time load it will display
        // Product fragment as default
        if (content == null) {
            Log.i(TAG, "content is null");
//            switchContent(new HomeFragment(), true);
            switchContent(new MyOrderListFragment(), true);
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                onBackStackChangedListener);

        updateCartCount();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_app_global, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_search_product) {
//            return true;
//        } else if (id == R.id.action_view_cart) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick(R.id.imgHome)
    public void launchHome() {
        switchContent(new HomeFragment(), true);
    }

    @OnClick(R.id.imgSearch)
    public void searchProduct() {
        Utils.showToastShort(NavigationDrawerActivity.this, getString(R.string.under_development));
    }

    @OnClick(R.id.imgCart)
    public void openCart() {
        switchContent(new CartFragment(), false);
    }

    private void setMenuAdapter() {
        navigationDrawerMenuArrayList = new ArrayList<>();
        if (DBAdapter.getMapKeyValueBoolean(NavigationDrawerActivity.this, IMap.IS_LOGIN)) {
            navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.WELCOME,
                    IDrawerMenu.WELCOME_ID, R.drawable.user));
        } else {
            navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.LOGIN_SIGNUP,
                    IDrawerMenu.LOGIN_SIGNUP_ID, R.drawable.user));
        }

        navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.CHANGE_SUBURB,
                IDrawerMenu.CHANGE_SUBURB_ID, R.drawable.street));
//        drawerMenuAdapter.add(new NavigationDrawerMenu(IDrawerMenu.HOME,
//                IDrawerMenu.HOME_ID, R.drawable.user));
        navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.NEAR_BY_STORES,
                IDrawerMenu.NEAR_BY_STORES_ID, R.drawable.storelist));

        if (DBAdapter.getMapKeyValueBoolean(NavigationDrawerActivity.this, IMap.IS_LOGIN)) {
            navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.MY_ORDERS,
                    IDrawerMenu.MY_ORDER_ID, R.drawable.myorder));
        }

        navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.CART,
                IDrawerMenu.CART_ID, R.drawable.mycart));
        navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.OFFERS,
                IDrawerMenu.OFFERS_ID, R.drawable.offers));
        navigationDrawerMenuArrayList.add(new NavigationDrawerMenu(IDrawerMenu.ABOUT_US,
                IDrawerMenu.ABOUT_US_ID, R.drawable.aboutus));
    }

    private void initDrawer() {
        setMenuAdapter();
        drawerMenuAdapter = new NavigationDrawerMenuAdapter(navigationDrawerMenuArrayList);
        drawerMenuAdapter.setOnItemClickListener(leftDrawerListItemClickListener);
        recyclerListNavigationDrawer.setLayoutManager(new LinearLayoutManager(NavigationDrawerActivity.this));
        recyclerListNavigationDrawer.setAdapter(drawerMenuAdapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.e(TAG, "onDrawerClosed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.e(TAG, "onDrawerOpened");
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle
                .setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "setToolbarNavigationClickListener");
                        toggleLeftDrawer();
                    }
                });

        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void hideDrawersAndShowContent() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.imgToggleDrawer)
    public void toggleLeftDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * For switching fragments
     */
    public void switchContent(Fragment fragment, boolean emptyStack) {
        FragmentManager manager = getSupportFragmentManager();
//        To hide keyboard on fragment change
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        content = fragment;
        String fragmentTag = fragment.getClass().getSimpleName();

        // if (backStateName.compareTo(CategoryListFragment.class.getSimpleName()
        // .toString()) == 0
        // || backStateName.compareTo(VideoListFragment.class.getSimpleName()
        // .toString()) == 0
        // || backStateName.compareTo(SettingsFragment.class.getSimpleName()
        // .toString()) == 0) {
        if (emptyStack) {
            // Remove all inner fragments of previous section
            boolean fragmentPopped = manager.popBackStackImmediate(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.i(TAG, "fragmentPopped = " + fragmentPopped);
        }

        Log.i(TAG, "fragmentTag = " + fragmentTag);
        FragmentTransaction ft = manager.beginTransaction();
//        ft.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter,
//                R.anim.exit);
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        Fragment tmpMyFragment = manager.findFragmentById(R.id.contentFrame);
        if (tmpMyFragment != null) {
            ft.hide(tmpMyFragment);
        }
        ft.add(R.id.contentFrame, fragment, fragmentTag);
        ft.addToBackStack(fragmentTag);
        ft.commit();

        hideDrawersAndShowContent();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back");

        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            int backStackEntryCount = manager.getBackStackEntryCount();
            Log.i(TAG, " getSupportFragmentManager backStackEntryCount = "
                    + backStackEntryCount);
            // hide left drawer
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                hideDrawersAndShowContent();
            }
            // logic for two time back press exit app
            else if (backStackEntryCount == 1) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                if (getApplicationContext() == null) {
                    return;
                } else {
                    Toast.makeText(this, "Please click BACK again to exit",
                            Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * Set title
     */
    public void setToolbarTitle(String toolbarTitle) {
        txtFragmentTitle.setText(toolbarTitle);
    }

    private void login() {
        Intent intent = new Intent(NavigationDrawerActivity.this, SigninActivity.class);
        startActivity(intent);
    }

    public int updateCartCount() {
        int cartCount = 0;
        ArrayList<Integer> productArrayList = DBAdapter.getAllProductIdFromCart(NavigationDrawerActivity.this);
        if (productArrayList != null) {
            cartCount = productArrayList.size();
        }
        txtCartCount.setText("" + cartCount);
        return cartCount;
    }

    @Override
    public void onNewIntent(Intent intent) {
        try {
            super.onNewIntent(intent);
            // getIntent() should always return the most recent
            setIntent(intent);
            Bundle extras = intent.getExtras();

            if (extras != null && extras.containsKey(IPushNotification.APP_LAUNCH_TYPE)
                    && extras.getString(IPushNotification.APP_LAUNCH_TYPE).equals(
                    IPushNotification.APP_LAUNCH_NOTIFICATION)) {
                if (extras.containsKey(IPushNotification.PUSHNOTIFICATION_DATA)) {
                    pushLaunch(extras.getString(IPushNotification.PUSHNOTIFICATION_DATA));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushLaunch(String push_data) {
        try {
            JSONObject pushDataJObject = new JSONObject(push_data);
            if (pushDataJObject.has(IPushNotification.NOTIFICATION_TYPE)) {
                // Pushnotification for new poll
                if (pushDataJObject.getString(IPushNotification.NOTIFICATION_TYPE).equals(IPushNotification.NOTIFICATION_TYPE_ORDER)) {
                    switchContent(new MyOrderListFragment(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
