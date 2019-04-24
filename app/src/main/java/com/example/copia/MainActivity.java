package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.copia.Entities.ComboboxEntity;
import com.example.copia.Fragments.FragmentClient;
import com.example.copia.Fragments.FragmentConsultants;
import com.example.copia.Fragments.FragmentContractors;
import com.example.copia.Fragments.FragmentDeletedFiles;
import com.example.copia.Fragments.FragmentManageUsers;
import com.example.copia.Fragments.FragmentRestricted;
import com.example.copia.Fragments.FragmentSearch;
import com.example.copia.Fragments.FragmentSpecifications;
import com.example.copia.Fragments.FragmentSuppliers;
import com.example.copia.Fragments.FragmentWelcome;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarRecord;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    TextView textview_username, textview_email, textview_fullname, textView_position;
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
        if(null == user)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if(user != null) {

            new RetrieveComboBoxTask().execute((Void) null);
            Fresco.initialize(this);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            navigationView.setNavigationItemSelectedListener(this);
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWelcome()).commit();
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawer = findViewById(R.id.drawer_layout);
            textview_username = (TextView) headerView.findViewById(R.id.textview_username);
            textview_email = (TextView) headerView.findViewById(R.id.textview_email);
            textview_fullname = (TextView)headerView.findViewById(R.id.textview_fullname);
            textView_position = (TextView)headerView.findViewById(R.id.textview_position);

            textview_username.setText("Logged in as: " + user.getUsername());
            textview_email.setText(user.getEmail());
            textview_fullname.setText(user.getString("Fullname"));
            textView_position.setText(user.getString("Position"));
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
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
            case R.id.category_manageusers:
                if(ParseUser.getCurrentUser().getString("Position").equalsIgnoreCase("Administrator"))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentManageUsers()).commit();
                else
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentRestricted()).commit();
                break;
            case R.id.category_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSearch()).commit();
                break;
            case R.id.deleted_files:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDeletedFiles()).commit();
                break;
            case R.id.logout:
                SugarRecord.deleteAll(ComboboxEntity.class);
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
        ArrayList<ComboboxEntity> comboboxEntities = new ArrayList<>();
        AlertDialog dialog;
        public  RetrieveComboBoxTask(){
            dialog = Utilities.getInstance().showLoading(MainActivity.this, "Please wait", false);
        }

        @Override
        protected ArrayList<ComboboxEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ComboboxData");
            List<ComboboxEntity> check = ComboboxEntity.listAll(ComboboxEntity.class);
            if( check.size() <= 0 ){
                try {
                    List<ParseObject> objects = query.find();
                    if(objects != null)
                    {
                        for(ParseObject object : objects)
                        {
                            ComboboxEntity comboboxEntity = new ComboboxEntity(object.getObjectId(),(String)object.get("Title"),(String)object.get("Category"),(String)object.get("Field"));
                            comboboxEntity.save();
                            comboboxEntities.add(comboboxEntity);
                        }
                    }
                } catch (ParseException e) {e.printStackTrace();}
            }
            else{
                for(ComboboxEntity entity : check)
                    comboboxEntities.add(entity);
            }


            return comboboxEntities;
        }
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<ComboboxEntity> comboboxEntities) {
            Log.d(TAG, "onPostExecute: comboboxEntities: " + comboboxEntities.size());
            dialog.dismiss();
            if(comboboxEntities.size() <= 0)
                Utilities.getInstance().showAlertBox("Empty", "No data were retrieved", MainActivity.this);
        }
    }

    private class RetrieveRoleTask extends AsyncTask<Void, Void, String>
    {
        AlertDialog dialog;
        boolean finished = false;
        String role = null;
        public RetrieveRoleTask() {
            finished = false;
            dialog = new SpotsDialog.Builder()
                    .setMessage("Retrieving user's role")
                    .setContext(MainActivity.this)
                    .setCancelable(false)
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            ParseQuery<ParseRole> query = ParseRole.getQuery();
            query.include("users");
            query.whereEqualTo("users", ParseUser.getCurrentUser());
            query.getFirstInBackground(new GetCallback<ParseRole>() {
                @Override
                public void done(ParseRole object, ParseException e) {
                    if(e == null && object != null)
                    {
                        role = object.getName();
                    }
                    finished = true;
                }
            });
            while (finished == false)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return role;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.length() > 0)
            {
                Log.d("Result", "RESULT IS: " + s);
            }
            else
                Log.d("Result", "RESULT IS: SOMETHING WENT WRONG");
        }
    }
}
