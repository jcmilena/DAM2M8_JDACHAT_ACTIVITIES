package com.example.jcmilena.jdachat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView messagesRecycler;
    Button newMsgButton;


    List<String> missatges = new ArrayList<>();
    String chatID, loginID, loginEmail, toUserID;
    MessagesAdapter messagesAdapter = new MessagesAdapter(missatges);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        //Volvemos a leer la información Extra que viene en bundle con la Activity para saber
        //de que conversación queremos leer los mensajes
        chatID = getIntent().getStringExtra("chatID");
        loginID = getIntent().getStringExtra("loginID");
        loginEmail = getIntent().getStringExtra("loginEmail");
        toUserID = getIntent().getStringExtra("toUserID");


        //Leemos todos los mensajes de la conversación escogida
        FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                missatges.add(dataSnapshot.getValue(String.class));
                messagesAdapter.notifyDataSetChanged();
                Log.i("Firebase MISSATGES", dataSnapshot.getValue(String.class));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        messagesRecycler = findViewById(R.id.messagesRecyclerView);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(messagesRecycler.getContext(), DividerItemDecoration.VERTICAL);
        messagesRecycler.addItemDecoration(dividerItemDecoration);
        messagesRecycler.setAdapter(messagesAdapter);

        newMsgButton = findViewById(R.id.newMessageButton);

        //Añadimos un listener al boton para poder escribir nuevos mensajes
        newMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                intent.putExtra("loginID", loginID);
                intent.putExtra("chatID", chatID);
                Log.i("FIREBASE USERID", toUserID);
                intent.putExtra("toUserID", toUserID);
                startActivity(intent);
            }
        });



    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView msgText;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            msgText = itemView.findViewById(R.id.msgTextView);
        }
    }

    public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder>{

        List<String> msg;

        public MessagesAdapter(List<String> msg) {

            this.msg = msg;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View itemview = getLayoutInflater().inflate(R.layout.message_viewholder, viewGroup, false);

            return new MessageViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

            messageViewHolder.msgText.setText(msg.get(i));

        }

        @Override
        public int getItemCount() {
            return msg.size();
        }
    }
}
