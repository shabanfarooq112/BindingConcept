package com.example.bindingconcept.methods;

import com.example.bindingconcept.models.UserModel;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CommonlyMethods {

    public static UserModel MineUser;
    public static ArrayList<UserModel> users = new ArrayList<>();

    public static String MilisecondToTime(Long miliSeconds) {
        String dateFormat = "hh:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(miliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

}
