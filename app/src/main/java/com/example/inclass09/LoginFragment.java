package com.example.inclass09;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private final OkHttpClient client = new OkHttpClient();

    Button btn_login,btn_signUp;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCloseButtonListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Mailer - login");

        btn_login = getActivity().findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });


        btn_signUp = getActivity().findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ContainerActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginSuccesful(User user);
    }


    /**
     * Private methods
     * **/

    private void login(){

        EditText email = getActivity().findViewById(R.id.txt_loginEmail);
        EditText password = getActivity().findViewById(R.id.txt_loginpassword);

        RequestBody formBody = new FormBody.Builder()
                .add("email", email.getText().toString())
                .add("password",password.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                .header("Content-Type","application/x-www-form-urlencoded")
                .post(formBody)
                .build();


        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("Main", "onFailure: "+e.getMessage());

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //Log.d("Main", "onResponse: "+response.body().string());
                    String responseJSON = response.body().string();
                    JSONObject json = null;
                    try {
                        json = new JSONObject(responseJSON);
                        if(json.getString("status").equals("ok"))
                        {
                            Log.d("Main", "onResponse: "+responseJSON);
                            Gson gson = new Gson();
                            User user = gson.fromJson(responseJSON,User.class);
                            Log.d("Main", "onResponse: "+user.toString());

                            if(user.getToken()!=null || !user.getToken().equals("")){
                                //saveToSharedPreferences(user,"user");
                                mListener.onLoginSuccesful(user);
                            }
                        }

                        else if(json.getString("status").equals("error")){
                            Message message = handler.obtainMessage(200,json.getString("message"));
                            message.sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {

            int code = msg.what;

            switch (code){

                case 200:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    };


}
