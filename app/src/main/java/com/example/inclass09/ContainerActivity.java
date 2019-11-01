package com.example.inclass09;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class ContainerActivity extends AppCompatActivity implements SignupFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener,
        InboxFragment.OnFragmentInteractionListener,
        NewMailFragment.OnFragmentInteractionListener,
        DisplayEmailFragment.OnCloseButtonListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,new SignupFragment(),"signup_fragment")
                .commit();
    }

    @Override
    public void onSignupFragmentReturnCallback(User user) {

        Log.d("ContainerActivity", "onSignupFragmentReturnCallback: "+user.toString());

        saveToSharedPreferences(user,"user");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new InboxFragment(user),"inbox")
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

        SharedPreferences sp = getSharedPreferences("userPref",Context.MODE_PRIVATE);
        String token = sp.getString(key,"");
        Object object = gson.fromJson(token, classType);

        return object;
    }

    @Override
    public void onInboxUpdates(List emails) {


    }

    @Override
    public void onNewEmailClicked() {

        User user = (User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new NewMailFragment(user,this),"new_email")
                .commit();

    }

    @Override
    public void onCardClicked(Email email) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new DisplayEmailFragment(email),"display_email")
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
                .replace(R.id.container,new LoginFragment(),"login_fragment")
                .commit();
    }

    @Override
    public void onEmailSent(boolean isEmailSent) {

        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new InboxFragment(user),"inbox")
                .commit();

    }

    @Override
    public void onCancelClicked() {
        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new InboxFragment(user),"inbox")
                .commit();

    }

    @Override
    public void onCloseButtonClicked() {

        User user =(User) loadFromSharedPreferences("user",User.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new InboxFragment(user),"inbox")
                .commit();
    }

    @Override
    public void onLoginSuccesful(User user) {
        if(user!=null || user.getToken()!=null){
            saveToSharedPreferences(user,"user");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,new InboxFragment(user),"inbox")
                    .commit();


        }
        else {
            Toast.makeText(this,"Error while login!",Toast.LENGTH_LONG).show();
        }
    }
}
