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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
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
    String spinnerSelected;
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
                spinnerSelected = parent.getItemAtPosition(position).toString();
                if(spinnerSelected.equalsIgnoreCase("Pending users"))
                    new RetrievePendingUsers().execute((Void)null);
                else if(spinnerSelected.equalsIgnoreCase("Apprentices"))
                    new RetrieveApprenticeUsers().execute((Void)null);
                else if(spinnerSelected.equalsIgnoreCase("Administrator"))
                    new RetreieveAdminUsers().execute((Void)null);
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
                .setItemClickCallback(clickListener())
                .setIsVertical(false)
                .create();

        return listener;
    }
    private SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack clickListener()
    {
        SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack listener = new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
            @Override
            public void onClick(int i)
            {
                if(spinnerSelected.equalsIgnoreCase("Pending Users"))
                {
                    String[] menu = new String[]{"Accept"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setCancelable(true);
                    dialog.setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (which == 0)
                                new AcceptPendingUsers(userEntityList.get(pos).getObjectId()).execute((Void) null);
                        }
                    });
                    AlertDialog dialog1 = dialog.create();
                    dialog1.show();
                }
                else if(spinnerSelected.equalsIgnoreCase("Apprentices"))
                {
                    String[] menu = new String[]{"Remove user", "Make Admin"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setCancelable(true);
                    dialog.setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (which == 0)
                                new RemoveApprenticeUsers(userEntityList.get(pos).getObjectId(), "Apprentice").execute((Void)null);
                            else if(which == 1)
                                new ChangePosition(userEntityList.get(pos).getObjectId(), "Apprentice", "Administrator").execute((Void)null);
                        }
                    });
                    AlertDialog dialog1 = dialog.create();
                    dialog1.show();
                }
                else if(spinnerSelected.equalsIgnoreCase("Administrator"))
                {
                    String[] menu = new String[]{"Remove user", "Make Apprentice"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setCancelable(true);
                    dialog.setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (which == 0)
                                new RemoveApprenticeUsers(userEntityList.get(pos).getObjectId(), "Administrator").execute((Void)null);
                            else if(which == 1)
                                new ChangePosition(userEntityList.get(pos).getObjectId(), "Administrator", "Apprentice").execute((Void)null);
                        }
                    });
                    AlertDialog dialog1 = dialog.create();
                    dialog1.show();
                }
            }
        };
        return listener;
    }
    private class RetrievePendingUsers extends AsyncTask<Void, Void, List<UserEntity>>
    {
        ArrayList<String> pendingUsersID = new ArrayList<>();
        AlertDialog dialog;
        public RetrievePendingUsers() {
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
            userEntityList = new ArrayList<>();
        }

        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> pendingUser = ParseQuery.getQuery("UserInfo");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                pendingUser.whereEqualTo("Verified", false);
                List<ParseObject> users = pendingUser.find();
                for (ParseObject parseObject : users)
                    pendingUsersID.add(parseObject.getString("userid"));
                query.whereContainedIn("arrayData", Arrays.asList(pendingUsersID));
                List<ParseUser> parseUsers = query.find();
                for (ParseUser parseUser : parseUsers)
                {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setObjectId(parseUser.getObjectId());
                    userEntity.setFullname(parseUser.getString("Fullname"));
                    userEntity.setEmail(parseUser.getString("emailAlt"));
                    userEntity.setUsername(parseUser.getUsername());
                    pendingUser.whereEqualTo("userid", parseUser.getObjectId());
                    ParseObject userinfo = pendingUser.getFirst();
                    userEntity.setPosition(userinfo.getString("Position"));
                    userEntity.setVerified(userinfo.getBoolean("Verified"));
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

    private class DeletePendingUsers extends AsyncTask<Void, Void, Boolean> // test
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
            ParseQuery<ParseObject> pending = ParseQuery.getQuery("UserInfo");
            try {
                ParseUser user = query.get(objectId);
                if(user != null) {
                    user.delete();
                    pending.whereEqualTo("userid", objectId);
                    ParseObject parseObject = pending.getFirst();
                    parseObject.delete();
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

    private class AcceptPendingUsers extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        String objectId;
        boolean successful = false;
        public AcceptPendingUsers(String objectId) {
            this.objectId = objectId;
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseObject> pendinguser = ParseQuery.getQuery("UserInfo");
            ParseQuery<ParseRole> query = ParseRole.getQuery();
            query.whereEqualTo("name", "Apprentice");
            try {
                ParseRole parseRole = query.getFirst();
                ParseQuery<ParseUser> query1 = ParseUser.getQuery();
                ParseUser parseUser = query1.get(objectId);
                parseRole.getUsers().add(parseUser);
                parseRole.save();
                pendinguser.whereEqualTo("userid", objectId);
                ParseObject object = pendinguser.getFirst();
                object.put("Verified", true);
                object.put("Position", "Apprentice");
                object.save();
                successful = true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true)
            {
                userEntityList.remove(pos);
                users_recyclerview.getAdapter().notifyItemRemoved(pos);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "Operation failed, Please try again", getContext());
        }
    }

    private class RetrieveApprenticeUsers extends AsyncTask<Void, Void, List<UserEntity>>
    {
        AlertDialog dialog;
        ArrayList<String> apprenticeId = new ArrayList<>();
        public RetrieveApprenticeUsers() {
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
            userEntityList = new ArrayList<>();
        }

        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> getApprentice = ParseQuery.getQuery("UserInfo");
            getApprentice.whereEqualTo("Position", "Apprentice");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                List<ParseObject> apprenticesList = getApprentice.find();
                for(ParseObject parseObject : apprenticesList)
                    apprenticeId.add(parseObject.getString("userid"));
                query.whereContainedIn("arrayData", Arrays.asList(apprenticeId));
                List<ParseUser> users = query.find();
                for(ParseUser user : users)
                {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setObjectId(user.getObjectId());
                    userEntity.setUsername(user.getUsername());
                    userEntity.setFullname(user.getString("Fullname"));
                    userEntity.setEmail(user.getString("emailAlt"));
                    getApprentice.whereEqualTo("userid", user.getObjectId());
                    ParseObject obj = getApprentice.getFirst();
                    userEntity.setPosition(obj.getString("Position"));
                    userEntity.setVerified(obj.getBoolean("Verified"));
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
                Utilities.getInstance().showAlertBox("Response", "No records found", getContext());

        }
    }

    private class RemoveApprenticeUsers extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        String objectId;
        boolean successful = false;
        String currentPosition;
        public RemoveApprenticeUsers(String objectId, String currentPosition) {
            this.objectId = objectId;
            this.currentPosition = currentPosition;
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseRole> query = ParseRole.getQuery();
            ParseQuery<ParseObject> pendingUser = ParseQuery.getQuery("UserInfo");
            ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
            pendingUser.whereEqualTo("userid", objectId);
            query.whereEqualTo("name", currentPosition);
            try {
                ParseRole role = query.getFirst();
                role.getUsers().remove(userParseQuery.get(objectId));
                role.save();
                ParseObject pending = pendingUser.getFirst();
                pending.put("Verified", false);
                pending.put("Position", "");
                pending.save();
                successful = true;
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
                Utilities.getInstance().showAlertBox("Response", "This user was put to pending, you can delete the user from there", getContext());
            }
            else
                Utilities.getInstance().showAlertBox("Response", "Operation failed, Please try again", getContext());
        }
    }

    private class ChangePosition extends AsyncTask<Void, Void, Boolean>
    {
        AlertDialog dialog;
        String objectId;
        String oldPosition;
        String newPosition;
        boolean successful = false;

        public ChangePosition(String objectId, String oldPosition, String newPosition) {
            this.objectId = objectId;
            this.oldPosition = oldPosition;
            this.newPosition = newPosition;
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ParseQuery<ParseRole> apprentice = ParseRole.getQuery();
            ParseQuery<ParseRole> admin = ParseRole.getQuery();
            ParseQuery<ParseUser> user = ParseUser.getQuery();
            ParseQuery<ParseObject> alterInfo = ParseQuery.getQuery("UserInfo");
            alterInfo.whereEqualTo("userid", objectId);
            apprentice.whereEqualTo("name", oldPosition);
            try {
                ParseRole apprenticeRole = apprentice.getFirst();
                apprenticeRole.getUsers().remove(user.get(objectId));
                apprenticeRole.save();
                ParseRole adminRole = admin.getFirst();
                adminRole.getUsers().add(user.get(objectId));
                adminRole.save();
                ParseObject parseObject = alterInfo.getFirst();
                parseObject.put("Position", newPosition);
                parseObject.save();
                successful = true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return successful;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();
            if(aBoolean == true)
            {
                userEntityList.remove(pos);
                users_recyclerview.getAdapter().notifyItemRemoved(pos);
            }
            else
                Utilities.getInstance().showAlertBox("Response", "Operation failed, try again", getContext());
        }
    }

    private class RetreieveAdminUsers extends AsyncTask<Void, Void, List<UserEntity>>
    {
        AlertDialog dialog;
        ArrayList<String> userIdList = new ArrayList<>();
        public RetreieveAdminUsers() {
            dialog = Utilities.getInstance().showLoading(getContext(), "Please wait", false);
            userEntityList = new ArrayList<>();
        }
        @Override
        protected List<UserEntity> doInBackground(Void... voids) {
            ParseQuery<ParseObject> userInfo = ParseQuery.getQuery("UserInfo");
            userInfo.whereEqualTo("Position", "Administrator");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            try {
                List<ParseObject> userList = userInfo.find();
                for (ParseObject parseObject : userList)
                    userIdList.add(parseObject.getString("userid"));
                query.whereContainedIn("arrayData", Arrays.asList(userIdList));
                List<ParseUser> users = query.find();
                for(ParseUser user : users)
                {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setObjectId(user.getObjectId());
                    userEntity.setUsername(user.getUsername());
                    userEntity.setEmail(user.getString("emailAlt"));
                    userEntity.setFullname(user.getString("Fullname"));
                    userInfo.whereEqualTo("userid", user.getObjectId());
                    ParseObject object = userInfo.getFirst();
                    userEntity.setPosition(object.getString("Position"));
                    userEntity.setVerified(object.getBoolean("Verified"));
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
                Utilities.getInstance().showAlertBox("Response", "No records found", getContext());
        }
    }
}
