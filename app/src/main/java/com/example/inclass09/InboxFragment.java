package com.example.inclass09;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InboxFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InboxFragment extends Fragment implements EmailRecyclerAdapter.EmailCardInteractionListener {

    private OnFragmentInteractionListener mListener;

    private User user;
    private final OkHttpClient client = new OkHttpClient();

    private List<Email> emails;

    private RecyclerView rcView;
    EmailRecyclerAdapter rcAdapter;

    public InboxFragment() {
        // Required empty public constructor
    }

    public InboxFragment(User user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Inbox");

        TextView tv_userName = getActivity().findViewById(R.id.txt_userName);

        tv_userName.setText(user.getUser_fname()+" "+user.getUser_lname());
        emails = new ArrayList<Email>();
        getEmails();
        loadRecyclerView();


        ImageView iv_newEMail = getActivity().findViewById(R.id.iv_newEmail);

        iv_newEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onNewEmailClicked();
            }
        });

        ImageView iv_logOut = getActivity().findViewById(R.id.iv_logout);

        iv_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLogOutClicked();
            }
        });
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
    public boolean onDeleteEmailCallback(final Email email) {

        Log.d("InboxFrag", "onDeleteEmailCallback: Gonna call - \"http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+ email.getId());

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+ email.getId())
                .header("Authorization","BEARER "+user.getToken())
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //Log.d("InboxFrag", "onResponse: "+response.body().string());
                    String responseJSON = response.body().string();

                    try {
                        Log.d("InboxFrag", "onResponse: "+responseJSON);
                        JSONObject responseObj = new JSONObject(responseJSON);

                        String status = responseObj.getString("status");
                        if(status.equals("ok"))
                        {
                            emails.remove(email);
                            Message message = handler.obtainMessage(200);
                            message.sendToTarget();
                        }


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }
            });
        }catch (Exception ex){

        }

        return true;
    }

    @Override
    public boolean onCardClick(Email email) {
        Log.d("InboxFrag", "onCardClick: "+email.toString());
        mListener.onCardClicked(email);
        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onInboxUpdates(List emails);
        void onNewEmailClicked();
        void onCardClicked(Email email);
        void onLogOutClicked();
    }


    /**
     * Private methods
     * */

    private void getEmails(){

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
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
                            JSONArray messages = jsonObject.getJSONArray("messages");

                            for(int i=0;i<messages.length();i++){

                                Email email = new Email();
                                JSONObject message = messages.getJSONObject(i);

                                email.setSubject(message.getString("subject"));
                                email.setCreated_at(message.getString("created_at"));
                                email.setId(message.getString("id"));
                                email.setSenderFname(message.getString("sender_fname"));
                                email.setSenderLname(message.getString("sender_lname"));
                                email.setMessage(message.getString("message"));
                                emails.add(email);
                            }

                            //Notify that emails have been loaded
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

    private void loadRecyclerView(){
        //Load recyclerView once emails are populated
        rcView = getActivity().findViewById(R.id.rcView_emailList);
        rcAdapter = new EmailRecyclerAdapter(emails,this);
        rcView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcView.setAdapter(rcAdapter);
        //rcAdapter.notifyDataSetChanged();
    }

    Handler handler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(@NonNull Message msg) {

            int code = msg.what;

            switch (code){
                case 200:
                    rcAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"Email deleted successfully!",Toast.LENGTH_LONG).show();
                    break;

                case 300:
                    rcAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(),"Emails retrieved successfully!",Toast.LENGTH_LONG).show();
                    break;

                case 400:

                    break;
            }

        }
    };
}
