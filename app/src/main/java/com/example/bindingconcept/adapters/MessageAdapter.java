package com.example.bindingconcept.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bindingconcept.MessageActivity;
import com.example.bindingconcept.R;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.ChatRoot;
import com.example.bindingconcept.models.MessageModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    ArrayList<MessageModel> messageModels;
    ChatRoot chatRoot;
    ViewHolder holder1;
    MediaPlayer player1;
    FirebaseFirestore dbRoot;


    public MessageAdapter(Context context, ArrayList<MessageModel> messageModels, ChatRoot chatRoot) {
        this.context = context;
        this.messageModels = messageModels;
        this.chatRoot = chatRoot;
    }

    public MessageAdapter(ArrayList<MessageModel> messageModels, ChatRoot chatRoot) {
        this.messageModels = messageModels;
        this.chatRoot = chatRoot;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.user_layout, parent, false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.userMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.otherUserMenu);
                popup.getMenuInflater().inflate(R.menu.message_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                deleteMessage(messageModels.get(position));
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();//showing popup menu
            }
        });
        holder.otherUserMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        holder.userAudioMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.otherUserMenu);
                popup.getMenuInflater().inflate(R.menu.message_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                deleteMessage(messageModels.get(position));
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();//showing pop
            }
        });

        holder.otherUserAudioMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        if (messageModels.get(position).getSenderId() == CommonlyMethods.MineUser.getId()) {
            holder.relativeLayout1.setVisibility(View.GONE);
            holder.relativeLayout2.setVisibility(View.VISIBLE);
            holder.otherUserAudio.setVisibility(View.GONE);

            if (messageModels.get(position).getAttachmentType() == 0) {
                holder.userAudio.setVisibility(View.GONE);
                holder.userMessage.setVisibility(View.VISIBLE);
                holder.userSentImage.setVisibility(View.GONE);
                holder.userMessage.setText(messageModels.get(position).getMessage());
                holder.userMessageTime.setText(CommonlyMethods.MilisecondToTime(messageModels.get(position).getSentAt()));

            } else if (messageModels.get(position).getAttachmentType() == 1) {
                holder.userAudio.setVisibility(View.GONE);
                holder.userMessage.setVisibility(View.GONE);
                holder.userSentImage.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(messageModels.get(position).getAttachment())
                        .into(holder.userSentImage);
                holder.userMessageTime.setText(CommonlyMethods.MilisecondToTime(messageModels.get(position).getSentAt()));
            } else if (messageModels.get(position).getAttachmentType() == 2) {
                holder.relativeLayout2.setVisibility(View.GONE);
                holder.relativeLayout1.setVisibility(View.GONE);
                holder.userAudio.setVisibility(View.VISIBLE);
                holder.otherUserAudio.setVisibility(View.GONE);
                holder.userAudioTime.setText(CommonlyMethods.MilisecondToTime(messageModels.get(position).getSentAt()));
                audioMessage(position, holder.userSeekbar, holder.userAudioDuration, holder.userPlay, holder.userPause , holder);

            }
        } else {
            holder.relativeLayout1.setVisibility(View.VISIBLE);
            holder.relativeLayout2.setVisibility(View.GONE);
            holder.userAudio.setVisibility(View.GONE);

            for (int i = 0; i < chatRoot.getMembersInfo().size(); i++) {
                if (messageModels.get(position).getSenderId() == chatRoot.getMembersInfo().get(i).getId()) {
                    holder.otherUserName.setText(chatRoot.getMembersInfo().get(i).getName());
                }
            }

            if (messageModels.get(position).getAttachmentType() == 0) {
                holder.otherUserSentImage.setVisibility(View.GONE);
                holder.otherUserMessage.setVisibility(View.VISIBLE);
                holder.otherUserAudio.setVisibility(View.GONE);
                holder.otherUserMessage.setText(messageModels.get(position).getMessage());
                holder.otherUserMessageTime.setText(CommonlyMethods.MilisecondToTime(messageModels.get(position).getSentAt()));

            } else if (messageModels.get(position).getAttachmentType() == 1) {
                holder.otherUserMessage.setVisibility(View.GONE);
                holder.otherUserSentImage.setVisibility(View.VISIBLE);
                holder.otherUserAudio.setVisibility(View.GONE);
                Picasso.get()
                        .load(messageModels.get(position).getAttachment())
                        .into(holder.otherUserSentImage);
                holder.otherUserMessageTime.setText(CommonlyMethods.MilisecondToTime(messageModels.get(position).getSentAt()));
            } else if (messageModels.get(position).getAttachmentType() == 2) {
                holder.relativeLayout2.setVisibility(View.GONE);
                holder.relativeLayout1.setVisibility(View.GONE);
                holder.otherUserAudio.setVisibility(View.VISIBLE);
                holder.userAudio.setVisibility(View.GONE);
                audioMessage(position, holder.otherUserSeekBar, holder.otherUserAudioDuration, holder.otherUserPlay, holder.otherUserPause , holder);
            }

        }

    }

    private void deleteMessage(MessageModel messageModel) {
        dbRoot = FirebaseFirestore.getInstance();
        dbRoot.collection("Conversation").document(chatRoot.getId())
                .collection("Messages").document(messageModel.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertFormatt(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout1;
        RelativeLayout relativeLayout2;
        ImageView otherUserImage;
        TextView otherUserName;
        TextView otherUserMessage;
        TextView otherUserMessageTime;
        TextView userMessage;
        TextView userMessageTime;
        ImageView otherUserSentImage, userSentImage;
        RelativeLayout otherUserAudio, userAudio;
        SeekBar otherUserSeekBar, userSeekbar;
        ImageView otherUserPlay, userPlay, otherUserPause, userPause;
        TextView otherUserAudioDuration, userAudioDuration;
        TextView userAudioTime , otherUserAudioTime;
        ImageView userMenu , otherUserMenu , userAudioMenu , otherUserAudioMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout1 = itemView.findViewById(R.id.relativeLayout1);
            relativeLayout2 = itemView.findViewById(R.id.relativeLayout2);
            otherUserImage = itemView.findViewById(R.id.other_user_profile_image);
            otherUserName = itemView.findViewById(R.id.other_user_name);
            otherUserMessage = itemView.findViewById(R.id.other_user_message);
            otherUserMessageTime = itemView.findViewById(R.id.other_user_meessage_time);
            userMessage = itemView.findViewById(R.id.user_message);
            userMessageTime = itemView.findViewById(R.id.user_meessage_time);
            otherUserSentImage = itemView.findViewById(R.id.other_user_sent_image);
            userSentImage = itemView.findViewById(R.id.user_sent_image);
            otherUserAudio = itemView.findViewById(R.id.other_user_audio);
            userAudio = itemView.findViewById(R.id.user_audio);

            otherUserSeekBar = itemView.findViewById(R.id.other_user_seekbar);
            userSeekbar = itemView.findViewById(R.id.user_seekbar);

            otherUserPlay = itemView.findViewById(R.id.other_user_play);
            userPlay = itemView.findViewById(R.id.user_play);

            otherUserPause = itemView.findViewById(R.id.other_user_pause);
            userPause = itemView.findViewById(R.id.user_pause);

            otherUserAudioDuration = itemView.findViewById(R.id.other_user_duration);
            userAudioDuration = itemView.findViewById(R.id.user_duration);

            userAudioTime =  itemView.findViewById(R.id.user_audio_time);
            otherUserAudioTime =  itemView.findViewById(R.id.other_user_audio_time);

            userMenu =  itemView.findViewById(R.id.user_menu);
            otherUserMenu =  itemView.findViewById(R.id.other_user_menu);
            otherUserAudioMenu =  itemView.findViewById(R.id.other_user_audio_menu);
            userAudioMenu =  itemView.findViewById(R.id.user_audio_menu);


        }
    }

    private void audioMessage(int position, SeekBar seekBar, TextView audioDuration, ImageView play, ImageView pause , ViewHolder holder) {

        Handler handler = new Handler();
        Runnable runnable;
        MediaPlayer player= new MediaPlayer();

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(messageModels.get(position).getAttachment());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(player.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        String totalDuration = convertFormatt(player.getDuration());
        audioDuration.setText(totalDuration);

        Runnable finalRunnable = runnable;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder1 != null){
                    holder1.userPlay.setVisibility(View.VISIBLE);
                    holder1.userPause.setVisibility(View.GONE);
                    holder1.otherUserPlay.setVisibility(View.VISIBLE);
                    holder1.otherUserPause.setVisibility(View.GONE);
                    player1.pause();
                }

                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                player.start();
                seekBar.setMax(player.getDuration());
                handler.postDelayed(finalRunnable, 0);
                holder1 = holder;
                player1 = player;
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);

                player.pause();
//                handler.removeCallbacks(finalRunnable);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    player.seekTo(i);
                }
                audioDuration.setText(convertFormatt(player.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });
    }


}
