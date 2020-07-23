package com.google.moviestvsentiments.usecase.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.service.account.AccountViewModel;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * An activity that manages switching between the Home, Liked and Disliked tabs.
 */
@AndroidEntryPoint
public class SentimentsNavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @Inject
    AccountViewModel viewModel;

    private String accountName;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiments_navigation);

        accountName = getIntent().getStringExtra(SigninActivity.EXTRA_ACCOUNT_NAME);
        bindHamburgerMenu();
        setupNavigationUI();

        viewModel.getCurrentAccount().observe(this, account -> {
            if (account == null) {
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Sets up the hamburger menu so that it displays the correct account name and will call this
     * Activity to handle clicks on menu items.
     */
    private void bindHamburgerMenu() {
        NavigationView hamburgerNav = findViewById(R.id.hamburger_nav);
        hamburgerNav.setNavigationItemSelectedListener(this);

        TextView accountNameView = hamburgerNav.getHeaderView(0)
                .findViewById(R.id.hamburgerAccountName);
        accountNameView.setText(accountName);

        TextView accountIconInitial = hamburgerNav.getHeaderView(0)
                .findViewById(R.id.hamburgerAccountIconText);
        accountIconInitial.setText(accountName.substring(0, 1).toUpperCase());
    }

    /**
     * Sets up the user interface so that it will display the bottom navigation view and the
     * action bar.
     */
    private void setupNavigationUI() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.container);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_liked, R.id.navigation_disliked)
                .setDrawerLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.signout) {
            viewModel.setIsCurrent(accountName, false);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}