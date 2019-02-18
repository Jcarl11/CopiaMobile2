package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    TextView textview_username, textview_email;
    private String objectId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
        ParseUser user = ParseUser.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            new RetrieveComboBoxTask().execute((Void) null);

        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWelcome()).commit();
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        textview_username = (TextView)headerView.findViewById(R.id.textview_username);
        textview_email = (TextView)headerView.findViewById(R.id.textview_email);
        textview_username.setText("Logged in as: " + user.getUsername());
        textview_email.setText(user.getEmail());

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
                new LogoutTask(this).execute((Void)null);
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
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    public String getObjectId()
    {
        return objectId;
    }
    private class RetrieveComboBoxTask extends AsyncTask<Void, Void, ArrayList<ComboboxEntity>>
    {
        boolean finished = false;
        AlertDialog dialog;
        public  RetrieveComboBoxTask()
        {
            dialog = new SpotsDialog.Builder()
                    .setMessage("Loading data")
                    .setContext(MainActivity.this)
                    .setCancelable(false)
                    .build();
        }
        ArrayList<ComboboxEntity> comboboxEntities = new ArrayList<>();
        @Override
        protected ArrayList<ComboboxEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ComboboxData");
            List<ComboboxEntity> check = ComboboxEntity.listAll(ComboboxEntity.class);
            if(check.size() <= 0)
            {
                query.findInBackground(new FindCallback<ParseObject>()
                {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e)
                    {
                        if(e == null && objects != null)
                        {
                            for(ParseObject object : objects)
                            {
                                ComboboxEntity comboboxEntity = new ComboboxEntity(object.getObjectId(),(String)object.get("Title"),(String)object.get("Category"),(String)object.get("Field"));
                                comboboxEntity.save();
                                comboboxEntities.add(comboboxEntity);
                            }
                            finished = true;
                        }
                        else
                            finished = true;
                    }
                });
            }
            else
            {
                for(ComboboxEntity entity : check)
                    comboboxEntities.add(entity);
                finished = true;
            }


            while(finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return comboboxEntities;
        }
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ComboboxEntity> comboboxEntities) {
            dialog.dismiss();
            if(comboboxEntities.size() <= 0)
                Utilities.getInstance().showAlertBox("Empty", "No data were retrieved", MainActivity.this);
        }


    }
}
