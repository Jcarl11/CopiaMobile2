package com.example.copia.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.copia.Adapters.UserAdapter;
import com.example.copia.Entities.UserEntity;
import com.example.copia.MainActivity;
import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.List;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentManageUsers extends Fragment {
    BetterSpinner users_spinner;
    RecyclerView users_recyclerview;
    List<UserEntity> userEntityList = new ArrayList<>();
    private static String[] menu = {"Pending users", "Apprentices", "Administrator"};
    int pos = -1;
    public FragmentManageUsers() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_manage_users, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Users");
        users_spinner = (BetterSpinner)view.findViewById(R.id.users_spinner);
        users_recyclerview = (RecyclerView)view.findViewById(R.id.users_recyclerview);
        users_recyclerview.setHasFixedSize(true);
        users_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        users_recyclerview.setOnTouchListener(listener());
        users_spinner.setAdapter(spinner_adapter());
        users_spinner.setOnItemClickListener(spinner_onclickListener());
        return view;
    }

    private ArrayAdapter<String> spinner_adapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, menu);
        return adapter;
    }

    private AdapterView.OnItemClickListener spinner_onclickListener()
    {
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equalsIgnoreCase("Pending users"))
                    new RetrievePendingUsers().execute((Void)null);
            }
        };
        return listener;
    }

    private SwipeDismissRecyclerViewTouchListener listener()
    {
        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(users_recyclerview, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int i) {
                pos = i;
                return true;
            }

            @Override
            public void onDismiss(View view) {
                DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                                new DeletePendingUsers(userEntityList.get(pos).getObjectId()).execute((Void)null);
                                break;
                        }
                    }
                };
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("This user will be deleted").setPositiveButton("Yes", dialogInterface)
                        .setNegativeButton("No", dialogInterface).show();
            }
        })
                .setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
                    @Override
                    public void onClick(int i) {
                        String[] menu = new String[]{"Accept"};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setCancelable(true);
                        dialog.setItems(menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog1 = dialog.create();
                        dialog1.show();
                    }
                })
                .setIsVertical(false)
                .create();

        return listener;
    }

    private class RetrievePendingUsers extends AsyncTask<Void, Void, List<UserEntity>>
    {
        AlertDialog dialog;
        public RetrievePendingUsers() {
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
            userEntityList = new ArrayList<>();
        }

        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("Verified", false);
            try {
                List<ParseUser> parseUsers = query.find();
                for (ParseUser parseUser : parseUsers)
                {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setObjectId(parseUser.getObjectId());
                    userEntity.setFullname(parseUser.getString("Fullname"));
                    userEntity.setEmail(parseUser.getString("emailAlt"));
                    userEntity.setUsername(parseUser.getUsername());
                    userEntity.setVerified(parseUser.getBoolean("Verified"));
                    userEntity.setPosition(parseUser.getString("Position"));
                    userEntityList.add(userEntity);
                }
            } catch (ParseException e) {e.printStackTrace();}
            return userEntityList;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(List<UserEntity> userEntities) {
            dialog.dismiss();
            if(userEntities.size() > 0){
                UserAdapter userAdapter = new UserAdapter(getContext(), userEntities);
                users_recyclerview.setAdapter(userAdapter);
            }else
                Utilities.getInstance().showAlertBox("Response", "No pending users as of now", getContext());
        }
    }

    private class DeletePendingUsers extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        String objectId;
        boolean successful = false;
        public DeletePendingUsers(String objectId) {
            this.objectId = objectId;
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                ParseUser user = query.get(objectId);
                if(user != null) {
                    user.delete();
                    successful = true;
                }

            } catch (ParseException e) {e.printStackTrace();}

            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true) {
                userEntityList.remove(pos);
                users_recyclerview.getAdapter().notifyItemRemoved(pos);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "Operation failed, Please try again", getContext());
        }
    }
}
