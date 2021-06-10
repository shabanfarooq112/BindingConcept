package com.example.bindingconcept;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bindingconcept.adapters.FollwersAdapter;
import com.example.bindingconcept.databinding.ShowAllFollowersBinding;
import com.example.bindingconcept.methods.CommonlyMethods;
import com.example.bindingconcept.models.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    ShowAllFollowersBinding binding;
    Context context;
    ArrayList<UserModel> users;
    MainActivity.FollwersAdapterToMainASctivity listener;

    public BottomSheetDialog(Context context){
        this.context = context;
    }

    public BottomSheetDialog(Context context , MainActivity.FollwersAdapterToMainASctivity listener){
        this.context = context;
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShowAllFollowersBinding.inflate(inflater,
                container, false);

        users = new ArrayList<>(CommonlyMethods.users);


        setAdapter();

        binding.includeAppHeader.plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context , CreateGroupActivity.class);
                intent.putExtra("users" , users);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
    private void setAdapter() {
        FollwersAdapter follwersAdapter = new FollwersAdapter(context , users , listener);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setAdapter(follwersAdapter);
    }
}