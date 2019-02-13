package com.example.copia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseUser;

public class FragmentWelcome extends Fragment
{
    TextView welcome_textview_username, welcome_textview_email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        welcome_textview_username = (TextView)view.findViewById(R.id.welcome_textview_name);
        welcome_textview_email = (TextView)view.findViewById(R.id.welcome_textview_email);
        ParseUser user = ParseUser.getCurrentUser();
        welcome_textview_username.setText(user.getUsername());
        welcome_textview_email.setText(user.getEmail());
        return view;
    }
}
