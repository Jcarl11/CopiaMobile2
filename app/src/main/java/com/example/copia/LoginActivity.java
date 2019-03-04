package com.example.copia;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copia.EditActivities.PasswordResetActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout username,password;
    private Button register, login;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        username = (TextInputLayout) findViewById(R.id.edittext_username);
        password = (TextInputLayout) findViewById(R.id.edittext_password);
        register = (Button)findViewById(R.id.btn_register);
        login = (Button)findViewById(R.id.btn_login);
        /*password.setOnEditorActionListener(listener());
        username.setOnEditorActionListener(listener());*/
    }
    private TextView.OnEditorActionListener listener(){
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    loginOnClick(null);
                }
                return false;
            }
        };
        return listener;
    }
    public void loginOnClick(View view){
        if ( !checkUsernameForNull() | !checkPasswordForNull() ) {
            return;
        }
        String mUsername = username.getEditText().getText().toString().trim();
        String mPassword = password.getEditText().getText().toString().trim();
        new TaskExecute(this, mUsername, mPassword).execute((Void)null);
    }
    public void registerOnClick(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }
    private boolean checkUsernameForNull()
    {
        if(username.getEditText().getText().toString().isEmpty()) {
            username.setError("Username cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }
    private boolean checkPasswordForNull()
    {
        if(password.getEditText().getText().toString().isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
    public void resetOnClick(View view) {
        Intent intent = new Intent(this, PasswordResetActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 2 ){
            if( resultCode == RESULT_OK ) {
                Utilities.getInstance().showAlertBox("Response", "Check your email to reset your password", this);
            }
        }
    }

    private class TaskExecute extends AsyncTask<Void, Void, String> {
        LoginActivity context;
        String userName;
        String passWord;
        String response = null;
        boolean finished = false;
        AlertDialog dialog;
        String pending = "PENDING";
        String success = "SUCCESSFUL";
        String exception = "EXCEPTION";
        String wrong_credentials = "ERROR";
        public TaskExecute(LoginActivity context, String userName, String passWord)
        {
            this.context = context;
            this.userName = userName;
            this.passWord = passWord;
            finished = false;
            dialog = Utilities.getInstance().showLoading(context, "Logging in", false);
        }
        @Override
        protected String doInBackground(Void... strings) {

            ParseQuery<ParseObject> isPending = ParseQuery.getQuery("UserInfo");
            ParseUser user = null;
            try {user = ParseUser.logIn(userName, passWord);}
                catch (ParseException e) {
                e.printStackTrace();
                response = wrong_credentials;
            }
            try {
                if (user != null) {
                    isPending.whereEqualTo("userid", user.getObjectId());
                    ParseObject object = isPending.getFirst();
                    boolean isVerified = object.getBoolean("Verified");
                    if(isVerified == false)
                        response = pending;
                    else {
                        user.put("Verified", isVerified);
                        user.put("Position", object.getString("Position"));
                        user.save();
                        response = success;
                    }
                }
            }catch(ParseException e){e.printStackTrace();}

            return response;
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            if(response.equalsIgnoreCase(success)) {
                context.startActivity(new Intent(context.getBaseContext(), MainActivity.class));
                context.finish();
            }
            else if(response.equalsIgnoreCase(pending)){
                Utilities.getInstance().showAlertBox("Pending", "Your account is still pending", LoginActivity.this);
                ParseUser.logOut();
            }
            else if(response.equalsIgnoreCase(wrong_credentials))
                Utilities.getInstance().showAlertBox("Wrong credentials", "Account does not exist", LoginActivity.this);
            else
                Utilities.getInstance().showAlertBox("Response", response, LoginActivity.this);

        }
    }
}
