package com.example.inclass09;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCloseButtonListener} interface
 * to handle interaction events.
 */
public class DisplayEmailFragment extends Fragment {

    private OnCloseButtonListener mListener;

    private Email email;

    public DisplayEmailFragment() {
        // Required empty public constructor
    }

    public DisplayEmailFragment(Email email) {
        // Required empty public constructor

        Log.d("DisplayEmailFrag", "DisplayEmailFragment: "+email);

        this.email = email;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Display Email");

        TextView txt_sentBy = getActivity().findViewById(R.id.lbl_sentBy);
        txt_sentBy.setText(this.email.getSenderFname()+" "+this.email.getSenderLname());

        TextView txt_subject = getActivity().findViewById(R.id.lbl_subject);
        txt_subject.setText(this.email.getSubject());

        TextView txt_createdAt = getActivity().findViewById(R.id.lbl_createdAt);
        txt_createdAt.setText(this.email.getCreated_at());

        TextView txt_message = getActivity().findViewById(R.id.lbl_message);
        txt_message.setText(this.email.getMessage());

        Button btn_close = getActivity().findViewById(R.id.btn_signUpCancel);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCloseButtonClicked();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_email, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCloseButtonListener) {
            mListener = (OnCloseButtonListener) context;
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

    public interface OnCloseButtonListener {
        // TODO: Update argument type and name
        void onCloseButtonClicked();
    }
}
