package com.example.bindingconcept.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bindingconcept.CreateGroupActivity;
import com.example.bindingconcept.MainActivity;
import com.example.bindingconcept.MessageActivity;
import com.example.bindingconcept.R;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.example.bindingconcept.models.MembersInfo;
import com.example.bindingconcept.models.UserModel;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class FollwersAdapter extends RecyclerView.Adapter<FollwersAdapter.ViewHolder> {

    Context context;
    ArrayList<UserModel> users = new ArrayList<>();
    MainActivity.FollwersAdapterToMainASctivity listener;
    int check;
    ChatRoot chatRoot;


    public FollwersAdapter(Context context, ArrayList<UserModel> users, MainActivity.FollwersAdapterToMainASctivity listener) {
        this.context = context;
        this.users = new ArrayList<>(users);
        this.listener = listener;
    }

    public FollwersAdapter(Context context, ArrayList<UserModel> users, ChatRoot chatRoot, int check) {
        this.context = context;
        this.users = users;
        this.check = check;
        this.chatRoot = chatRoot;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.followers_list_item, parent, false);
        FollwersAdapter.ViewHolder viewHolder = new FollwersAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (check == 1) {

            if (chatRoot == null) {
                holder.selectUser.setVisibility(View.VISIBLE);
                holder.diselectUser.setVisibility(View.GONE);
            } else {
                holder.selectUser.setVisibility(View.GONE);
                holder.diselectUser.setVisibility(View.GONE);
                ArrayList<MembersInfo> list = new ArrayList<>(chatRoot.getMembersInfo());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == users.get(position).getId()) {
                        CreateGroupActivity.selectedList.add(users.get(position));
                        holder.selectUser.setVisibility(View.GONE);
                        holder.diselectUser.setVisibility(View.VISIBLE);
                        break;
                    }else {
                        holder.selectUser.setVisibility(View.VISIBLE);
                        holder.diselectUser.setVisibility(View.GONE);
                    }
                }
            }

            holder.memberName.setText(users.get(position).getName());

            holder.selectUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.selectUser.setVisibility(View.GONE);
                    holder.diselectUser.setVisibility(View.VISIBLE);
                    CreateGroupActivity.selectedList.add(users.get(position));
                }
            });
            holder.diselectUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.selectUser.setVisibility(View.VISIBLE);
                    holder.diselectUser.setVisibility(View.GONE);

                    for (int i = 0; i < CreateGroupActivity.selectedList.size(); i++) {
                        if (CreateGroupActivity.selectedList.get(i).getId() == users.get(position).getId()) {
                            CreateGroupActivity.selectedList.remove(i);
                        }
                    }
                }
            });

        } else {

            holder.selectUser.setVisibility(View.GONE);
            holder.diselectUser.setVisibility(View.GONE);

            holder.memberName.setText(users.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.method1(users.get(position));

//                Gson gson = new Gson();
//                String myJson = gson.toJson(users.get(position));
//                Intent intent = new Intent(context, MainActivity.class);
//                intent.putExtra("user", myJson);
//                context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView memberName;
        ImageView selectUser, diselectUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            selectUser = itemView.findViewById(R.id.select_user);
            diselectUser = itemView.findViewById(R.id.diselect_user);
        }
    }
}
