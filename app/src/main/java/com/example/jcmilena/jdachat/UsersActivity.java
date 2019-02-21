package com.example.jcmilena.jdachat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<User> users = new ArrayList<>();
    List<String> UserIDs = new ArrayList<>();
    String loginID = "";
    String loginEmail="";
    UsersAdapter adapter = new UsersAdapter(users);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        loginID = getIntent().getStringExtra("loginID");
        loginEmail = getIntent().getStringExtra("loginEmail");


        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserIDs.add(dataSnapshot.getKey());
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                users.add(new User(map.get("email"), map.get("password")));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        recyclerView = findViewById(R.id.usersRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


    }

    public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder>{


        List<User> usuarios;

        public UsersAdapter(List<User> usuarios) {
            this.usuarios = usuarios;
        }

        @NonNull
        @Override
        public UsersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = getLayoutInflater().inflate(R.layout.user_viewholder, viewGroup, false);
            return new UsersHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UsersHolder usersHolder, int i) {

            usersHolder.nick.setText(usuarios.get(i).getPassword());
            usersHolder.email.setText(usuarios.get(i).getEmail());
            usersHolder.UserID = UserIDs.get(i);

        }


        @Override
        public int getItemCount() {
            return usuarios.size();
        }

        public class UsersHolder extends RecyclerView.ViewHolder{

            TextView nick , email;
            String UserID;

            public UsersHolder(@NonNull View itemView) {
                super(itemView);

                nick = itemView.findViewById(R.id.nicktextView);
                email = itemView.findViewById(R.id.emailTextView);
                
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        DatabaseReference userChatReference;

                        String chatID = FirebaseDatabase.getInstance().getReference().child("users").child(UserID).child("chats").push().getKey();


                        //Creamos Chat en el Receptor
                        userChatReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserID)
                                .child("chats").child(chatID);
                        userChatReference.child("name").setValue("Chat con "+ loginEmail);
                        userChatReference.child("isallreaded").setValue(0);
                        userChatReference.child("touid").setValue(loginID);


                        //Creamos Chat en el Emisor
                        userChatReference = FirebaseDatabase.getInstance().getReference().child("users").child(loginID)
                                .child("chats").child(chatID);
                        userChatReference.child("name").setValue("Chat con "+email.getText().toString());
                        userChatReference.child("isallreaded").setValue(1);
                        userChatReference.child("touid").setValue(UserID);

                        Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                        intent.putExtra("chatID", chatID);
                        intent.putExtra("loginID", loginID);
                        intent.putExtra("toUserID", UserID);
                        startActivity(intent);

                    }
                });
            }
        }
    }


}
