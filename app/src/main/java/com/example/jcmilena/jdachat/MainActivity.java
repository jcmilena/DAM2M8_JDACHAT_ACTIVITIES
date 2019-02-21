package com.example.jcmilena.jdachat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email, password;
    Button login , signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("LOGIN", "signInWithEmail:success");

                                    //Abrimos la Activity responsable de mostrar las conversaciones
                                    Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
                                    intent.putExtra("loginID", mAuth.getCurrentUser().getUid());
                                    intent.putExtra("loginEmail", email.getText().toString());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    //Escribimos en la BBDD de Firebase la informacion del nuevo usuario
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    write_user(user.getUid(), email.getText().toString(), password.getText().toString());

                                    Log.i("SIGNUP", "Creado usuario con email y password");

                                }else{
                                    Log.i("SIGNUP", "Usuario no registrado, fallo en createUserWithEmailandPassword");
                                }
                            }
                        });
            }
        });


    }

    private void write_user(String uid, String email, String password) {

        //User newUser = new User(email, password);
        Map<String, String> newUser = new HashMap<>();
        newUser.put("email", email);
        newUser.put("password", password);

        //Escribo en Firebase Database utilitzando HashMap o Objeto User
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(newUser);



    }
}
