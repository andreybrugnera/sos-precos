package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import br.edu.ifspsaocarlos.sosprecos.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        this.drawerLayout = findViewById(R.id.drawer_layout);

        configureToolbar();
        configureDrawer();

        MainFragment mainFragment = new MainFragment();
        changeFragment(mainFragment, null);
    }

    private void configureDrawer() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                MainFragment mainFragment = new MainFragment();
                                changeFragment(mainFragment, "main");
                                break;
                            case R.id.nav_categories:
                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                changeFragment(categoryListFragment, "categories");
                                break;
                            case R.id.nav_places:
                                PlaceListFragment placeListFragment = new PlaceListFragment();
                                changeFragment(placeListFragment, "places");
                                break;
                            case R.id.nav_logout:
                                logout();
                        }
                        return true;
                    }
                });
    }

    /**
     * Configure toolbar to access drawer
     */
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    /**
     * Change current fragment
     *
     * @param fragment
     * @param tag
     */
    private void changeFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        if (tag != null) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Logs out if there are no fragment entries in the stack
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            int backEntries = getSupportFragmentManager().getBackStackEntryCount();
            if (backEntries == 0) {
                logout();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void logout() {
        //Show dialog to confirm logout
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }
}
