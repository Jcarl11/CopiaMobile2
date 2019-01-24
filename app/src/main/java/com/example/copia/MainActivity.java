package com.example.copia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if defined
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWelcome()).commit();
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.category_client:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentClient()).commit();
                break;
            case R.id.category_suppliers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSuppliers()).commit();
                break;
            case R.id.category_contractors:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentContractors()).commit();
                break;
            case R.id.category_consultants:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentConsultants()).commit();
                break;
            case R.id.category_specifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSpecifications()).commit();
                break;
            case R.id.category_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSearch()).commit();
                break;
            case R.id.logout:
                ParseUser.logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
