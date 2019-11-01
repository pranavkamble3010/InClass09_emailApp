package com.example.inclass09;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewMailFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private final OkHttpClient client = new OkHttpClient();
    private User user;
    private List<User> recievers;
    private User receiver;
    private Email email;

    private Spinner sp_sendTo;
    private EditText txt_subject;
    private EditText txt_message;
    private Button btn_send;
    private Button btn_cancel;

    private ArrayAdapter<User> receviersAdapter;

    public NewMailFragment() {
        // Required empty public constructor
        this.user = (User) loadFromSharedPreferences("user",User.class);
    }

    public NewMailFragment(User user, OnFragmentInteractionListener mListener) {
        // Required empty public constructor
        this.user = user;
        this.mListener = mListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_mail, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle("Create New Email");

        sp_sendTo = getActivity().findViewById(R.id.sp_sendTo);

        btn_send = getActivity().findViewById(R.id.btn_send);
        btn_cancel = getActivity().findViewById(R.id.btn_close);

        txt_subject = getActivity().findViewById(R.id.txt_subject);
        txt_message = getActivity().findViewById(R.id.txt_message);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendEmail();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancelClicked();
            }
        });

        getRecievers();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEmailSent(boolean isEmailSent);

        void onCancelClicked();
    }


    /**
     * Private methods
     * */

    Object loadFromSharedPreferences(String key, Type classType){
        Gson gson = new Gson();

        SharedPreferences sp = Objects.requireNonNull(getActivity()).getSharedPreferences("userPref",Context.MODE_PRIVATE);
        String token = sp.getString(key,"");
        Object object = gson.fromJson(token, classType);

        return object;
    }

    private void sendEmail(){

        email.setSubject(txt_subject.getText().toString());
        email.setMessage(txt_message.getText().toString());

        Log.d("NewEMailFrag", "sendEmail: "+user.getUser_id());

        RequestBody formBody = new FormBody.Builder()
                .add("receiver_id", email.getId())  //Using email.id for storing receiver's ID
                .add("subject",email.getSubject())
                .add("message",email.getMessage())
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Authorization","BEARER "+user.getToken())
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
                    Log.d("NewEmailFrag", "onResponse: "+response.body().string());

                    Message message = handler.obtainMessage(200);
                    message.sendToTarget();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getRecievers(){

        recievers = new ArrayList<>();

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                .header("Authorization","BEARER "+user.getToken())
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //Log.d("InboxFrag", "onResponse: "+response.body().string());
                    String responseJSON = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(responseJSON);

                        String status = jsonObject.getString("status");
                        if(status.equals("ok")){
                            JSONArray users = jsonObject.getJSONArray("users");

                            for(int i=0;i<users.length();i++){

                                User user = new User();
                                JSONObject userObj = users.getJSONObject(i);
                                user.setUser_id(userObj.getString("id"));
                                user.setUser_fname(userObj.getString("fname"));
                                user.setUser_lname(userObj.getString("lname"));

                                recievers.add(user);

                            }

                            //Notify that recievers list has been loaded
                            Message message = handler.obtainMessage(300);
                            message.sendToTarget();
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });
        }catch (Exception ex){

        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {

            int code = msg.what;

            switch (code){

                case 200:
                    //Email sent
                    Toast.makeText(getActivity(),"Email sent successfully!",Toast.LENGTH_LONG).show();
                    mListener.onEmailSent(true);
                    break;

                case 300:
                    Log.d("NewMailFrag", "handleMessage: "+code);

                    receviersAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,recievers);
                    sp_sendTo.setAdapter(receviersAdapter);

                    sp_sendTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            email = new Email();
                            User user = recievers.get(i);
                            //Using email.id for storing receiver's ID
                            email.setId(user.getUser_id());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    break;
            }
        }
    };
}
