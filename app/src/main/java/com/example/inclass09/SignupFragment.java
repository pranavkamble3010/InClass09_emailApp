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


public class SignupFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    private OnFragmentInteractionListener mListener;

    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText password;
    EditText r_password;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
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

        getActivity().setTitle("Sign Up");


        fname = getActivity().findViewById(R.id.txt_fname);
        lname = getActivity().findViewById(R.id.txt_lname);
        email = getActivity().findViewById(R.id.txt_email);
        password = getActivity().findViewById(R.id.txt_pw);
        r_password = getActivity().findViewById(R.id.txt_rpw);

        final Button signup = getActivity().findViewById(R.id.btn_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    signUp();
                }
            }
        });


        Button btn_signUpCancel = getActivity().findViewById(R.id.btn_signUpCancel);

        btn_signUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSignupFragmentReturnCallback(User user);
    }

    /**Private methods*/
    private boolean validate() {

        boolean flag = true;

        if(fname.getText().toString().equals("") || fname.getText().toString().equals(null)){
            fname.setError("First name field cannot be empty!");
            flag = false;
        }

        if(lname.getText().toString().equals("") || lname.getText().toString().equals(null)){
            lname.setError("Last name field cannot be empty!");
            flag = false;
        }

        if(email.getText().toString().equals("") || email.getText().toString().equals(null)){
            email.setError("Email field cannot be empty!");
            flag = false;
        }

        if(password.getText().toString().equals("") || password.getText().toString().equals(null)){
            password.setError("Password field cannot be empty!");
            flag = false;
        }

        if(r_password.getText().toString().equals("") || r_password.getText().toString().equals(null)){
            r_password.setError("Please repeat the password!");
            flag = false;
        }

        if(!r_password.getText().toString().equals(password.getText().toString())) {
            password.setError("");
            r_password.setError("");
            Toast.makeText(getActivity(),"Passwords do not match!",Toast.LENGTH_LONG).show();
            flag = false;
        }

        return flag;
    }

    private void signUp(){

        RequestBody formBody = new FormBody.Builder()
                .add("email", email.getText().toString())
                .add("password",password.getText().toString())
                .add("fname",fname.getText().toString())
                .add("lname",lname.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
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

                    try {
                        JSONObject json = new JSONObject(responseJSON);
                        if(json.getString("status").equals("ok")){
                            Log.d("signUpFragment", "onResponse: "+json.getString("token"));
                            //saveToSharedPreferences(json.getString("token"));
                            Gson gson = new Gson();

                            User user = gson.fromJson(responseJSON,User.class);
                            Log.d("signUpFragment", "onResponse: "+user.toString());
                            mListener.onSignupFragmentReturnCallback(user);
                        }

                        else if(json.getString("status").equals("error")){
                            Log.d("signUpFragment", "onResponse: "+json.getString("status"));

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
