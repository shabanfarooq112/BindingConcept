package com.example.bindingconcept.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bindingconcept.MainActivity;
import com.example.bindingconcept.MessageActivity;
import com.example.bindingconcept.R;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.google.android.material.circularreveal.CircularRevealHelper;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.sql.DataTruncation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AllChatMembersAdapter extends RecyclerView.Adapter<AllChatMembersAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ChatRoot> chatRoots;

    public AllChatMembersAdapter(Context context, ArrayList<ChatRoot> chatRoots) {
        this.context = context;
        this.chatRoots = chatRoots;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.all_chat_member_list_item, parent, false);
        AllChatMembersAdapter.ViewHolder viewHolder = new AllChatMembersAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        for (int i = 0; i < chatRoots.get(position).getMembersInfo().size(); i++) {
            if (chatRoots.get(position).getMembersInfo().get(i).getId() != CommonlyMethods.MineUser.getId()) {
                if(chatRoots.get(position).getTitle() == null){
                    holder.memberName.setText(chatRoots.get(position).getMembersInfo().get(i).getName());
                }else {
                    holder.memberName.setText(chatRoots.get(position).getTitle());
                }
                holder.lastMessage.setText(chatRoots.get(position).getLastMessage());
                holder.time.setText(CommonlyMethods.MilisecondToTime(chatRoots.get(position).getSentAt()));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String myJson = gson.toJson(chatRoots.get(position));
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("chatRoot", myJson);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView memberName, lastMessage;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.all_chat_member_list_item_image);
            memberName = itemView.findViewById(R.id.member_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            time = itemView.findViewById(R.id.all_chat_member_list_item_time);
        }
    }


}
