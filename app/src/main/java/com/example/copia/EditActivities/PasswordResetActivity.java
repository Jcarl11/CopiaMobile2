package com.example.copia.EditActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.copia.R;
import com.example.copia.Utilities;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordResetActivity extends AppCompatActivity {
    public static String RESPONSE = "RESPONSE";
    TextInputLayout passwordreset_email;
    Button passwordreset_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        getSupportActionBar().setTitle("Reset Password");
        passwordreset_email = (TextInputLayout)findViewById(R.id.passwordreset_email);
        passwordreset_submit = (Button)findViewById(R.id.passwordreset_submit);
    }

    public void resetSubmitClicked(View view) {
        String email = passwordreset_email.getEditText().getText().toString().trim();
        if(!email.isEmpty()) {

            if(Utilities.getInstance().isEmailValid(email)){
                passwordreset_email.setError(null);
                new PasswordResetTask(email).execute((Void)null);
            } else
                passwordreset_email.setError("Not a valid email format");
        } else
            passwordreset_email.setError("Email cannot be empty");
    }


    private class PasswordResetTask extends AsyncTask<Void, Void, String>
    {
        AlertDialog dialog;
        String email;
        String processResponse = null;
        public PasswordResetTask(String email) {
            this.email = email;
            dialog = Utilities.getInstance().showLoading(PasswordResetActivity.this, "Please wait", false);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                ParseUser.requestPasswordReset(email);
                processResponse = "Successful";
            } catch ( ParseException e ) {
                e.printStackTrace();
                processResponse = e.getMessage();
            }

            return processResponse;
        }

        @Override
        protected void onPreExecute() {dialog.show();}

        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            if(processResponse.equalsIgnoreCase("Successful")){
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Utilities.getInstance().showAlertBox("Response", response, PasswordResetActivity.this);
            }
        }
    }
}
