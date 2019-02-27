package com.example.copia.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copia.Entities.UserEntity;
import com.example.copia.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<UserEntity> userEntities;

    public UserAdapter(Context context, List<UserEntity> userEntities) {
        this.context = context;
        this.userEntities = userEntities;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_row_layout, viewGroup, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        UserEntity userEntity = userEntities.get(i);
        userViewHolder.fullname.setText(userEntity.getFullname());
        userViewHolder.username.setText(userEntity.getUsername());
        if(userEntity.getEmail() != null)
            userViewHolder.email.setText(userEntity.getEmail());
        else
            userViewHolder.email.setText("N/A");
        if(userEntity.getPosition() != null && userEntity.getPosition().length() > 0)
            userViewHolder.position.setText(userEntity.getPosition());
        else
            userViewHolder.position.setText("None");
        if(userEntity.isVerified() == true) {
            userViewHolder.status.setTextColor(Color.GREEN);
            userViewHolder.status.setText("Verified");
        }
        else {
            userViewHolder.status.setTextColor(Color.RED);
            userViewHolder.status.setText("Unverified");
        }

    }

    @Override
    public int getItemCount() {return userEntities.size();}

    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView fullname, email, username, status, position;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.user_row_fullname);
            email = itemView.findViewById(R.id.user_row_email);
            username = itemView.findViewById(R.id.user_row_username);
            status = itemView.findViewById(R.id.user_row_verified);
            position = itemView.findViewById(R.id.user_row_position);
        }
    }
}
