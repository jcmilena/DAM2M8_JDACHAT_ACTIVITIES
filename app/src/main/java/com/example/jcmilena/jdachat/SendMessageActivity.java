package com.example.jcmilena.jdachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

public class SendMessageActivity extends AppCompatActivity {

    EditText message;
    Button send;

    String chatID, loginID, toUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        //Volvemos a leer la información Extra que viene en bundle con la Activity para saber
        //en que conversación queremos escribir el mensaje
        chatID = getIntent().getStringExtra("chatID");
        loginID = getIntent().getStringExtra("loginID");
        toUserID = getIntent().getStringExtra("toUserID");


        message = findViewById(R.id.messageEditText);
        send = findViewById(R.id.sendButton);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Escribimos el mensaje en  la Firebase Database y avisamos que hay mensajes nuevos
                FirebaseDatabase.getInstance().getReference().child("chats").child(chatID).push().setValue(message.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(toUserID).child("chats").child(chatID).child("isallreaded").setValue(0);


                Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                intent.putExtra("loginID", loginID);
                startActivity(intent);

            }
        });



    }
}
