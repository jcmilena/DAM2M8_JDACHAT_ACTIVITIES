package com.example.jcmilena.jdachat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    String loginID, loginEmail;

    RecyclerView chatRecyclerView;
    Toolbar miToolbar;

    List<ChatSummary> chatsList = new ArrayList<>();
    List<String> chatIDs = new ArrayList<>();
    ChatAdapter chatAdapter = new ChatAdapter(chatsList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        //Recojo los Extra que me vienen en bundle con el Intent
        loginID = getIntent().getStringExtra("loginID");
        loginEmail = getIntent().getStringExtra("loginEmail");

        //Colocamos un Listener para leer en cuantas conversaciones participa el usuario activo
        FirebaseDatabase.getInstance().getReference().child("users").child(loginID).child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatSummary item = dataSnapshot.getValue(ChatSummary.class);
                chatsList.add(item);
                chatIDs.add(dataSnapshot.getKey());
                chatAdapter.notifyDataSetChanged();
                Log.i("Firebase Chats", item.getName() );

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        miToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(miToolbar);


        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Añadimos un decorador para dibujar una linea de separacion entre los ViewHolders
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chatRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        chatRecyclerView.addItemDecoration(dividerItemDecoration);
        chatRecyclerView.setAdapter(chatAdapter);


    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView chatName, allReaded;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.chatNameTextView);
            allReaded = itemView.findViewById(R.id.allReadedtextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("Firebase CONVERSACIONES", String.valueOf(getAdapterPosition()));

                    //Escribimos para avisar que el usuario activo ha leido los mensajes de la conversacion
                    FirebaseDatabase.getInstance().getReference().child("users").child(loginID).child("chats").child(chatIDs.get(getAdapterPosition())).child("isallreaded").setValue(1);

                    //Abrimos una nueva Activity para ver los mensajes de la conversación
                    Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                    intent.putExtra("chatID", chatIDs.get(getAdapterPosition()));
                    intent.putExtra("loginID", loginID);
                    Log.i("FIREBASE USERID",chatsList.get(getAdapterPosition()).getTouid()+"?VACIO" );
                    intent.putExtra("toUserID", chatsList.get(getAdapterPosition()).getTouid() );
                    startActivity(intent);
                }
            });
        }
    }

    public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        List<ChatSummary> conversaciones;

        public ChatAdapter(List<ChatSummary> chats) {
            this.conversaciones = chats;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = getLayoutInflater().inflate(R.layout.chat_viewholder, viewGroup, false);


            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {

            chatViewHolder.chatName.setText(conversaciones.get(i).getName());
            Log.i("Firebase ALL READED =", String.valueOf(conversaciones.get(i).getIsallreaded()));
            if(conversaciones.get(i).getIsallreaded()== 1){
                chatViewHolder.allReaded.setVisibility(View.INVISIBLE);
            }else{
                chatViewHolder.allReaded.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return conversaciones.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.newChat:
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                intent.putExtra("loginID", loginID);
                intent.putExtra("loginEmail", loginEmail);
                startActivity(intent);
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class );
                startActivity(intent1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
