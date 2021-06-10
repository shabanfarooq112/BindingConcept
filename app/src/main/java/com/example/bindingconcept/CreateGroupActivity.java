package com.example.bindingconcept;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Toast;

import com.example.bindingconcept.adapters.AllChatMembersAdapter;
import com.example.bindingconcept.adapters.FollwersAdapter;
import com.example.bindingconcept.databinding.ActivityCreateGroupBinding;
import com.example.bindingconcept.databinding.ActivityMainBinding;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.example.bindingconcept.models.MembersInfo;
import com.example.bindingconcept.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityCreateGroupBinding binding;
    FollwersAdapter adapter;
    ArrayList<UserModel> users = new ArrayList<>();
    public static ArrayList<UserModel> selectedList = new ArrayList<>();
    ChatRoot chatRoot;
    FirebaseFirestore dbRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        dbRoot = FirebaseFirestore.getInstance();

        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectedList.clear();

        if (getIntent().hasExtra("users")) {
            users = (ArrayList<UserModel>) getIntent().getSerializableExtra("users");
            binding.createGroupButton.setVisibility(View.VISIBLE);
            binding.updateGroupButton.setVisibility(View.GONE);
        } else if (getIntent().hasExtra("chatRoote")) {
            binding.createGroupButton.setVisibility(View.GONE);
            binding.updateGroupButton.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            chatRoot = gson.fromJson(getIntent().getStringExtra("chatRoote"), ChatRoot.class);
            binding.groupName.setText(chatRoot.getTitle());
        }

        setAdapter();
        setListener();


    }

    private void setListener() {
        binding.createGroupButton.setOnClickListener(this);
        binding.updateGroupButton.setOnClickListener(this);
    }

    private void setAdapter() {
        adapter = new FollwersAdapter(this, CommonlyMethods.users, chatRoot, 1);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_group_button: {
                if (!binding.groupName.getText().toString().isEmpty()) {
                    createGroup(binding.groupName.getText().toString());
                }
                break;
            }
            case R.id.update_group_button: {
                if (!binding.groupName.getText().toString().isEmpty()) {
                    updateGroup(binding.groupName.getText().toString());
                }
                break;
            }
        }
    }

    private void updateGroup(String groupName) {
        ChatRoot chatRoot1 = new ChatRoot();
        List<MembersInfo> membersInfoList = new ArrayList<>();
        List<Integer> members = new ArrayList<>();

        chatRoot1.setMembersInfo(null);
        chatRoot1.setMembers(null);
        chatRoot1.setTitle(null);
        for (int i = 0; i < selectedList.size(); i++) {
            members.add(selectedList.get(i).getId());
            membersInfoList.add(new MembersInfo(selectedList.get(i).getName(), selectedList.get(i).getId(), "http", true));
        }
        membersInfoList.add(new MembersInfo(CommonlyMethods.MineUser.getName(), CommonlyMethods.MineUser.getId(), "http", true));
        members.add(CommonlyMethods.MineUser.getId());

        chatRoot1.setMembers(members);
        chatRoot1.setMembersInfo(membersInfoList);
        chatRoot1.setTitle(groupName);

        dbRoot.collection("Conversation").document(chatRoot.getId()).update("membersInfo", chatRoot1.getMembersInfo());
        dbRoot.collection("Conversation").document(chatRoot.getId()).update("members", chatRoot1.getMembers());
        dbRoot.collection("Conversation").document(chatRoot.getId()).update("title", chatRoot1.getTitle());

        Intent intent = new Intent(CreateGroupActivity.this , MainActivity.class);
        startActivity(intent);

    }

    private void createGroup(String groupName) {
        Toast.makeText(this, "Group Created", Toast.LENGTH_SHORT).show();

        ChatRoot chatRoot = new ChatRoot();
        List<MembersInfo> membersInfoList = new ArrayList<>();
        List<Integer> members = new ArrayList<>();
        for (int i = 0; i < selectedList.size(); i++) {
            members.add(selectedList.get(i).getId());
            membersInfoList.add(new MembersInfo(selectedList.get(i).getName(), selectedList.get(i).getId(), "http", true));
        }
        membersInfoList.add(new MembersInfo(CommonlyMethods.MineUser.getName(), CommonlyMethods.MineUser.getId(), "http", true));
        members.add(CommonlyMethods.MineUser.getId());

        chatRoot.setMembers(members);
        chatRoot.setMembersInfo(membersInfoList);
        chatRoot.setTitle(groupName);

        chatRoot.setId("0");
        chatRoot.setCreatedAt(System.currentTimeMillis());
        chatRoot.setCreatedBy(CommonlyMethods.MineUser.getId());
        chatRoot.setLastMessage(CommonlyMethods.MineUser.getName() + " has created this group");


        chatRoot.setSenderId(CommonlyMethods.MineUser.getId());
        chatRoot.setSenderName(CommonlyMethods.MineUser.getName());
        Date date = new Date();
        Timestamp ts = new Timestamp(date);
        chatRoot.setSentAt(System.currentTimeMillis());

        dbRoot.collection("Conversation").add(chatRoot)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Gson gson = new Gson();
                        dbRoot.collection("Conversation").document(documentReference.getId().toString()).update("id", documentReference.getId());
                        chatRoot.setId(documentReference.getId());
                        Intent intent = new Intent(CreateGroupActivity.this, MessageActivity.class);
                        String myJson = gson.toJson(chatRoot);
                        intent.putExtra("chatRoote", myJson);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
