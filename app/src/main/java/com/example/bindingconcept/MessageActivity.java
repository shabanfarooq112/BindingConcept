package com.example.bindingconcept;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.example.bindingconcept.adapters.MessageAdapter;
import com.example.bindingconcept.databinding.ActivityMessageBinding;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.example.bindingconcept.models.MembersInfo;
import com.example.bindingconcept.models.MessageModel;
import com.example.bindingconcept.models.UserModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MessageActivity extends AppCompatActivity implements View.OnClickListener {


    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    String AudioSavePathInDevice = null;
    ActivityMessageBinding binding;
    MessageAdapter messageAdapter;
    UserModel user;
    FirebaseFirestore dbRoot;
    ChatRoot chatRoot;
    ArrayList<MessageModel> messageModels = new ArrayList<MessageModel>();

    Uri imageUri;
    private StorageReference storageReference;
    String imagePath;
    String audioPath;
    Boolean isScrolling = false;
    int currentItem, totalItem, scrolloutItem;
    int pageSize = 10;
    int startAfter = 0;
    int doesMessages = 0;
    DocumentSnapshot firstMessage;
    int iteration = 0;
    Query first;
    int callAdapter = 0;
    int messagesContinue = 0;
    int deleteMessageCheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbRoot = FirebaseFirestore.getInstance();

        Gson gson = new Gson();
        if (getIntent().hasExtra("chatRoot")) {
            user = gson.fromJson(getIntent().getStringExtra("user"), UserModel.class);
            chatRoot = gson.fromJson(getIntent().getStringExtra("chatRoot"), ChatRoot.class);
            if (chatRoot.getTitle() != null) {
                binding.include.plus1.setVisibility(View.VISIBLE);
            } else {
                binding.include.plus1.setVisibility(View.INVISIBLE);
            }

            getAllMessage();
//            setAdapter();
        } else if (getIntent().hasExtra("user")) {
            user = gson.fromJson(getIntent().getStringExtra("user"), UserModel.class);
            binding.include.plus1.setVisibility(View.INVISIBLE);
        }

        if (getIntent().hasExtra("chatRoote")) {
            chatRoot = gson.fromJson(getIntent().getStringExtra("chatRoote"), ChatRoot.class);
            if(chatRoot.getCreatedBy() == CommonlyMethods.MineUser.getId()) {
                binding.include.plus1.setVisibility(View.VISIBLE);
            }
        }

        setListener();

    }

    private void setListener() {
        binding.showIcon.setOnClickListener(this);
        binding.yourMessage.setOnClickListener(this);
        binding.sendMessage.setOnClickListener(this);
        binding.galleryIcon.setOnClickListener(this);
        binding.recordButton.setOnClickListener(this);
        binding.cancelTextView.setOnClickListener(this);
        binding.sendTextView.setOnClickListener(this);
        binding.include.plus1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_icon: {
                showIcon();
                break;
            }
            case R.id.your_message: {
                hideIcon();
                break;
            }
            case R.id.send_message: {
                if (chatRoot != null) {
                    sendMessage(chatRoot.getId(), 0);
                } else {
                    sendMessageFirstTime(0);
                }
                break;
            }
            case R.id.gallery_icon: {
                selectImage();
                break;
            }
            case R.id.record_button: {
                startRecording();
                break;
            }
            case R.id.cancel_textView: {
                binding.chronometer.stop();
                binding.chronometer.setBase(SystemClock.elapsedRealtime());
                binding.relativeLayout0.setVisibility(View.GONE);
                binding.relativeLayout1.setVisibility(View.VISIBLE);
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
                mediaRecorder.stop();
                break;
            }
            case R.id.send_textView: {
                mediaRecorder.stop();
                binding.chronometer.stop();
                binding.chronometer.setBase(SystemClock.elapsedRealtime());
                binding.relativeLayout0.setVisibility(View.GONE);
                binding.relativeLayout1.setVisibility(View.VISIBLE);
                Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();
                uploadAudio();
                break;
            }
            case R.id.plus1: {
                Intent intent = new Intent(MessageActivity.this, CreateGroupActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(chatRoot);
                intent.putExtra("chatRoote", json);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    private void startRecording() {
        binding.relativeLayout0.setVisibility(View.VISIBLE);
        binding.relativeLayout1.setVisibility(View.GONE);
        binding.chronometer.setBase(SystemClock.elapsedRealtime());
        binding.chronometer.start();

        startAudioRecord();
    }

    private void startAudioRecord() {
        random = new Random();

        if (checkPermission()) {

            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(MessageActivity.this, "Recording started",
                    Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    private void selectImage() {
        ImagePicker.Companion.with(this)
                .crop(16f, 16f)                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(256, 256)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadAudio() {
        if (AudioSavePathInDevice != null) {
            Uri uriAudio = Uri.fromFile(new File(AudioSavePathInDevice).getAbsoluteFile());
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("audio/" + UUID.randomUUID().toString());
            ref.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            audioPath = uri.toString();
                            if (chatRoot != null) {
                                sendMessage(chatRoot.getId(), 2);
                            } else {
                                sendMessageFirstTime(2);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            binding.yourMessage.setText("");
                            imagePath = uri.toString();
                            if (chatRoot != null) {
                                sendMessage(chatRoot.getId(), 1);
                            } else {
                                sendMessageFirstTime(1);
                            }
                        }
                    });
                }
            });
        }
    }


    private void sendMessageFirstTime(int check) {
        Toast.makeText(this, "send", Toast.LENGTH_SHORT).show();

        List<MembersInfo> membersInfoList = new ArrayList<>();
        membersInfoList.add(new MembersInfo(user.getName(), user.getId(), "http", true));
        membersInfoList.add(new MembersInfo(CommonlyMethods.MineUser.getName(), CommonlyMethods.MineUser.getId(), "http", true));

        List<Integer> members = new ArrayList<>();
        members.add(user.getId());
        members.add(CommonlyMethods.MineUser.getId());

        chatRoot = new ChatRoot();
        chatRoot.setId("0");
        chatRoot.setCreatedAt(System.currentTimeMillis());
        chatRoot.setCreatedBy(CommonlyMethods.MineUser.getId());
        chatRoot.setLastMessage(binding.yourMessage.getText().toString());
        chatRoot.setMembers(members);
        chatRoot.setMembersInfo(membersInfoList);

        chatRoot.setSenderId(CommonlyMethods.MineUser.getId());
        chatRoot.setSenderName(CommonlyMethods.MineUser.getName());
        Date date = new Date();
        Timestamp ts = new Timestamp(date);
        chatRoot.setSentAt(System.currentTimeMillis());
        chatRoot.setTitle(null);

        dbRoot.collection("Conversation").add(chatRoot)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dbRoot.collection("Conversation").document(documentReference.getId().toString()).update("id", (documentReference.getId()));
                        chatRoot.setId(documentReference.getId());
                        sendMessage(chatRoot.getId(), check);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }

                });
    }

    private void sendMessage(String id, int check) {
        MessageModel messageModel;
        if (check == 0) {
            messageModel = new MessageModel(null, binding.yourMessage.getText().toString(), CommonlyMethods.MineUser.getId(), System.currentTimeMillis(), 0, "0");
        } else if (check == 1) {
            messageModel = new MessageModel(imagePath, "", CommonlyMethods.MineUser.getId(), System.currentTimeMillis(), 1, "0");
        } else {
            messageModel = new MessageModel(audioPath, "", CommonlyMethods.MineUser.getId(), System.currentTimeMillis(), 2, "0");
        }


        deleteMessageCheck = 1;
        dbRoot.collection("Conversation").document(id+"")
                .collection("Messages").add(messageModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                dbRoot.collection("Conversation").document(id+"")
                        .collection("Messages").document(documentReference.getId().toString()).update("id",documentReference.getId());

                Toast.makeText(MessageActivity.this, "send", Toast.LENGTH_SHORT).show();
                FirebaseFirestore dbRoot1 = FirebaseFirestore.getInstance();

                deleteMessageCheck = 0;

                if (messageModels.size() == 0) {
                    getAllMessage();
                }

                Map<String, Object> map = new HashMap<>();

                if (check == 0) {
                    map.put("lastMessage", binding.yourMessage.getText().toString());
                } else if (check == 1) {
                    map.put("lastMessage", "image");
                } else if (check == 2) {
                    map.put("lastMessage", "audio");
                }
                map.put("senderId", CommonlyMethods.MineUser.getId());
                map.put("senderName", CommonlyMethods.MineUser.getName());
                map.put("sentAt", System.currentTimeMillis());


                dbRoot1.collection("Conversation").document(chatRoot.getId()).update(map);
                binding.yourMessage.setText("");
            }
        });
    }

    public void getAllMessage() {

        dbRoot.collection("Conversation").document(chatRoot.getId())
                .collection("Messages").orderBy("sentAt", Query.Direction.ASCENDING).limitToLast(8)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        iteration = 0;
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {

                                case ADDED:
                                    if (iteration == 0) {
                                        iteration++;
                                        firstMessage = dc.getDocument();
                                    }
                                    if (snapshots.getDocuments().size() < 8) {
                                        messagesContinue = 1;
                                    }
                                    MessageModel messageModel = dc.getDocument().toObject(MessageModel.class);

                                    if (messageModel.getId().equals("0")) {
                                        messageModels.add(messageModel);
                                    } else {
                                        messageModels.add(messageModel);
                                        setAdapter();
                                    }
                                    break;
                                case MODIFIED:
                                    MessageModel message1 = dc.getDocument().toObject(MessageModel.class);
                                    for (int i = 0; i < messageModels.size(); i++) {
                                        if (messageModels.get(i).getId().equals("0")) {
                                            messageModels.get(i).setId(message1.getId());
                                        }
                                    }
                                    setAdapter();
                                    break;
                                case REMOVED:
                                    if(deleteMessageCheck == 0) {
                                        MessageModel message = dc.getDocument().toObject(MessageModel.class);
                                        for (int i = 0; i < messageModels.size(); i++) {

                                            if (message.getId().equals(messageModels.get(i).getId())) {
                                                messageModels.get(i).setMessage("This message was deleted");
                                                messageModels.get(i).setAttachmentType(0);
                                                break;
                                            }
                                        }
                                        setAdapter();
                                    }
                                    break;
                            }
                        }


                    }
                });
    }


    public void reversePaginate() {
        if (messagesContinue == 0) {

            dbRoot.collection("Conversation").document(chatRoot.getId())
                    .collection("Messages").orderBy("sentAt", Query.Direction.ASCENDING).endBefore(firstMessage).limitToLast(8)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    iteration = 0;
                    for (DocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        MessageModel messageModel = queryDocumentSnapshot.toObject(MessageModel.class);
                        messageModels.add(messageModel);
                        if (iteration == 0) {
                            iteration++;
                            firstMessage = queryDocumentSnapshot;
                            int m = queryDocumentSnapshots.getDocuments().size();
                            if (m < 8) {
                                messagesContinue = 1;
                                firstMessage = null;
                            }
                        }
                    }
                    insertionSort(messageModels);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }


//                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    iteration = 0;
//                    for (QueryDocumentSnapshot document : task.getResult()) {
////                        Log.d(TAG, document.getId() + " => " + document.getData());
//                        if(iteration == 0){
//                            firstMessage = task.getResult().getDocuments().get(task.getResult().)
//                        }
//                        MessageModel messageModel = document.toObject(MessageModel.class);
//                        messageModels.add(messageModel);
//                    }
//                    messageAdapter.notifyDataSetChanged();
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });

    }


    private void hideIcon() {
        binding.showIcon.setVisibility(View.VISIBLE);
        binding.galleryIcon.setVisibility(View.GONE);
        binding.recordButton.setVisibility(View.GONE);
        binding.galleryIcon3.setVisibility(View.GONE);
    }

    private void showIcon() {
        binding.showIcon.setVisibility(View.GONE);
        binding.galleryIcon.setVisibility(View.VISIBLE);
        binding.recordButton.setVisibility(View.VISIBLE);
        binding.galleryIcon3.setVisibility(View.VISIBLE);
    }

    private void setAdapter() {
        messageAdapter = new MessageAdapter(this, messageModels, chatRoot);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(messageAdapter);
//        binding.recyclerView.scrollToPosition(lastScrollPosition);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScrolling = true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem = layoutManager.getChildCount();
                totalItem = layoutManager.getItemCount();
                scrolloutItem = layoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItem + scrolloutItem == totalItem) && firstMessage != null) {
                    isScrolling = false;
                    reversePaginate();
                }



            }
        });
    }


    public void insertionSort(ArrayList<MessageModel> messageModels) {
        for (int j = 1; j < messageModels.size(); j++) {
            MessageModel current = messageModels.get(j);
            int i = j - 1;
            while ((i > -1) && (messageModels.get(i).getSentAt() > current.getSentAt())) {
                messageModels.set(i + 1, messageModels.get(i));
                i--;
            }
            messageModels.set(i + 1, current);
        }
        if (callAdapter == 0) {
            callAdapter++;
            setAdapter();
        } else {
            messageAdapter.notifyDataSetChanged();
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MessageActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MessageActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MessageActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void fetchData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 5000);
    }


}