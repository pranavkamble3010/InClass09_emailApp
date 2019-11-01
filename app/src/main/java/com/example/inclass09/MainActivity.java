package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Inclass 09
 * Group 1 43
 * Members:
 * Pranav V. Kamble
 * Venky S. Hegde
 * */

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        InboxFragment.OnFragmentInteractionListener,
        NewMailFragment.OnFragmentInteractionListener,
DisplayEmailFragment.OnCloseButtonListener{




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainActivity_container,new LoginFragment(),"login_fragment")
                .commit();


    }

    void saveToSharedPreferences(Object object, String tag){
        SharedPreferences sp = getSharedPreferences("userPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson json = new Gson();
        String objectJSON = json.toJson(object);

        editor.putString(tag,objectJSON);
        editor.apply();
    }


    Object loadFromSharedPreferences(String key, Type classType){
        Gson gson = new Gson();

        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        String token = sp.getString(key,"");
        Object object = gson.fromJson(token, classType);

        return object;
    }

    @Override
    public void onLoginSuccesful(User user) {

        if(user!=null){
            saveToSharedPreferences(user,"user");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainActivity_container,new InboxFragment(user),"inbox")
                    .commit();


        }
        else {
            Toast.makeText(this,"Incorrect email and/or password!",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onInboxUpdates(List emails) {

    }

    @Override
    public void onNewEmailClicked() {

        User user = (User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new NewMailFragment(user,this),"new_email")
                .commit();

    }

    @Override
    public void onCardClicked(Email email) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new DisplayEmailFragment(email),"display_email")
                .commit();
    }

    @Override
    public void onLogOutClicked() {

        SharedPreferences sp = getSharedPreferences("userPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove("user");
        editor.apply();

        Toast.makeText(this,"User logged out successfully.",Toast.LENGTH_LONG).show();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new LoginFragment(),"login_fragment")
                .commit();

    }

    @Override
    public void onEmailSent(boolean isEmailSent) {


        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new InboxFragment(user),"inbox")
                .commit();

    }

    @Override
    public void onCancelClicked() {

        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new InboxFragment(user),"inbox")
                .commit();

    }

    @Override
    public void onCloseButtonClicked() {

        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivity_container,new InboxFragment(user),"inbox")
                .commit();

    }
}
