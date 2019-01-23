package com.example.copia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText username,password;
    private CheckBox checkBox;
    private Button register, login;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);
        register = (Button)findViewById(R.id.btn_register);
        login = (Button)findViewById(R.id.btn_login);
    }
    public void loginOnClick(View view)
    {
        String mUsername = username.getText().toString().trim();
        String mPassword = password.getText().toString().trim();
        if(!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword))
        {
            new TaskExecute(this, progressBar, mUsername, mPassword).execute((Void)null);
        }
        else
            Toast.makeText(this, "Don't leave blank fields", Toast.LENGTH_SHORT).show();
    }
    public void registerOnClick(View view)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
