package com.example.inclass09;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Email> resources;

    public interface EmailCardInteractionListener {
        boolean onDeleteEmailCallback(Email email);
        boolean onCardClick(Email email);

    }


    EmailCardInteractionListener emailCardInteractionListener;

    public EmailRecyclerAdapter(List<Email> resources, EmailCardInteractionListener emailCardInteractionListener) {

        this.resources = resources;
        this.emailCardInteractionListener = emailCardInteractionListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.email_card_layout,parent,false);

        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Email email = resources.get(position);

        final MyHolder tempMyHolder = (MyHolder) holder;

        tempMyHolder.txt_subject.setText(email.getSubject());
        tempMyHolder.txt_date.setText(email.getCreated_at());

        tempMyHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean result = emailCardInteractionListener.
                        onDeleteEmailCallback(resources.get(tempMyHolder.getLayoutPosition()));
               /* if(result){
                    resources.remove(tempMyHolder.getLayoutPosition());
                    notifyItemRemoved(tempMyHolder.getLayoutPosition());
                    notifyItemRangeChanged(tempMyHolder.getLayoutPosition(),resources.size());
                }*/

            }
        });

        //Set onClickListner on whole card view to view emails
        tempMyHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailCardInteractionListener.onCardClick(resources.get(tempMyHolder.getLayoutPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    private static class MyHolder extends RecyclerView.ViewHolder{

        View cardView;

        TextView txt_subject;
        TextView txt_date;
        ImageView iv_delete;
        TextView txt_album;
        private final Context context;



        public MyHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();

            cardView = itemView;

            txt_subject = itemView.findViewById(R.id.lbl_subject);
            txt_date = itemView.findViewById(R.id.txt_date);
            iv_delete = itemView.findViewById(R.id.iv_delete);

        }
    }


    public String getDate(String inputdate){
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat output = new SimpleDateFormat("MM/dd/yyyy");
        Date outputDate = null;
        Date inputDate = null;
        try {
            inputDate=input.parse(inputdate);
            String formattedDateString=output.format(inputDate);

            Log.d("bagh",formattedDateString);
            outputDate=output.parse(formattedDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime prettyTime=new PrettyTime();
        String prettyTimeString = prettyTime.format(outputDate);
        return prettyTimeString;
    }
}
