package com.example.bindingconcept;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bindingconcept.adapters.AllChatMembersAdapter;
import com.example.bindingconcept.databinding.ActivityMainBinding;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.example.bindingconcept.models.MessageModel;
import com.example.bindingconcept.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    AllChatMembersAdapter allChatMembersAdapter;
    private FirebaseAuth mAuth;
    FirebaseFirestore dbRoot;
    static ArrayList<ChatRoot> chatRoots = new ArrayList<ChatRoot>();
    UserModel user;
    ChatRoot chatRootFollower;
    static int check = 0;
    FollwersAdapterToMainASctivity follwersAdapterToMainASctivity;
    BottomSheetDialog bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        CommonlyMethods.MineUser = new UserModel();
        CommonlyMethods.MineUser.setName("ali");
        CommonlyMethods.MineUser.setId(2);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CommonlyMethods.users.clear();
        CommonlyMethods.users.add(new UserModel(1 , "shaban"));
        CommonlyMethods.users.add(new UserModel(2 , "ali"));
        CommonlyMethods.users.add(new UserModel(3 , "khan"));


        chatRoots.clear();
        dbRoot = FirebaseFirestore.getInstance();
        getChat();
        firebaseIntegeration();
        setListener();
        check = 1;

        follwersAdapterToMainASctivity = new FollwersAdapterToMainASctivity() {
            @Override
            public void method1(UserModel user1) {
                user = user1;
                startInent();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
//        insertionSort(chatRoots);
    }


    private void getChat() {
        dbRoot.collection("Conversation")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {

                                case ADDED:

                                    ChatRoot chatRoot = dc.getDocument().toObject(ChatRoot.class);
                                    for(int i=0 ; i<chatRoot.getMembersInfo().size(); i++){
                                        if(chatRoot.getMembersInfo().get(i).getId() == CommonlyMethods.MineUser.getId()){
                                            chatRoots.add(chatRoot);
                                        }
                                    }
                                    insertionSort(chatRoots);
                                    break;
                                case MODIFIED:
                                    ChatRoot chatRoot1 = dc.getDocument().toObject(ChatRoot.class);
                                    int check2 = 0;
                                    for (int i = 0; i < chatRoots.size(); i++) {

                                        if(chatRoots.get(i).getId().equals("0")){
                                            chatRoots.get(i).setId(chatRoot1.getId());
                                            check2 = 1;
                                        }
                                        if (chatRoot1.getId().equals(chatRoots.get(i).getId())) {
                                            chatRoots.get(i).setSenderName(CommonlyMethods.MineUser.getName());
                                            chatRoots.get(i).setSenderId(CommonlyMethods.MineUser.getId());
                                            chatRoots.get(i).setLastMessage(chatRoot1.getLastMessage());
                                            chatRoots.get(i).setSentAt(chatRoot1.getSentAt());
                                            chatRoots.get(i).setId(chatRoot1.getId());
                                            chatRoots.get(i).setTitle(chatRoot1.getTitle());
                                            chatRoots.get(i).setMembers(chatRoot1.getMembers());
                                            chatRoots.get(i).setMembersInfo(chatRoot1.getMembersInfo());
                                            check2 = 1;
                                        }
                                    }
                                    if(check2 == 0) {
                                        int check3 = 0;
                                        if (chatRoot1.getMembersInfo().size() == chatRoot1.getMembers().size()) {
                                            for(int i=0 ; i<chatRoot1.getMembersInfo().size(); i++){
                                                if(chatRoot1.getMembersInfo().get(i).getId() == CommonlyMethods.MineUser.getId()){
                                                    for(int j=0 ; j<chatRoots.size() ; j++){
                                                        if(chatRoots.get(j).getId().equals(chatRoot1.getId())){
                                                            check3 = 1;
                                                        }
                                                    }
                                                    if(check3 == 1){
                                                        chatRoots.add(chatRoot1);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    insertionSort(chatRoots);
                                    break;
                                case REMOVED:

                                    break;
                            }
                        }

                    }
                });
    }


    //    private void getChat() {
//        dbRoot.collection("Conversation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        ChatRoot chatRoot = document.toObject(ChatRoot.class);
//                        chatRoots.add(chatRoot);
//
//                        for (int i = 0; i < chatRoot.getMembersInfo().size(); i++) {
//                            if (user != null) {
//                                if (chatRoot.getMembersInfo().get(i).getId() == user.getId()) {
//                                    chatRootFollower = chatRoot;
//                                }
//                            }
//                        }
//
//                    }
//    startInent();
//                    insertionSort(chatRoots);
//                } else {
//
//                }
//            }
//        });
//    }

    private void startInent() {

        for (int j = 0; j < chatRoots.size(); j++) {
            for (int i = 0; i < chatRoots.get(j).getMembersInfo().size(); i++) {
                if (user != null) {
                    if (chatRoots.get(j).getMembersInfo().get(i).getId() == user.getId()) {
                        chatRootFollower = chatRoots.get(j);
                    }
                }
            }
        }

        if (user != null) {
            Gson gson = new Gson();
            if (chatRootFollower != null) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                String myJson = gson.toJson(chatRootFollower);
                intent.putExtra("chatRoot", myJson);
                String myJson1 = gson.toJson(user);
                intent.putExtra("user", myJson1);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                String myJson1 = gson.toJson(user);
                intent.putExtra("user", myJson1);
                startActivity(intent);
            }
        }
    }

    private void firebaseIntegeration() {
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        signInAnonymous();
    }

    private void signInAnonymous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                            myRef.child("users").push().setValue(mAuth.getUid());
                        } else {
                            Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setListener() {
        binding.includeAppHeader.plus1.setOnClickListener(this);
    }

    private void setAdapter() {
        allChatMembersAdapter = new AllChatMembersAdapter(this, chatRoots);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(allChatMembersAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plus1: {
                bottomSheet = new BottomSheetDialog(this, follwersAdapterToMainASctivity);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");

                break;
            }
        }
    }

    public void insertionSort(ArrayList<ChatRoot> chatRoots) {
        for (int j = 1; j < chatRoots.size(); j++) {
            ChatRoot current = chatRoots.get(j);
            int i = j - 1;
            while ((i > -1) && (chatRoots.get(i).getSentAt() < current.getSentAt())) {
                chatRoots.set(i + 1, chatRoots.get(i));
                i--;
            }
            chatRoots.set(i + 1, current);
        }
        setAdapter();
    }

    public interface FollwersAdapterToMainASctivity {
        public void method1(UserModel user);
    }


}