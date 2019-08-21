package com.chihab_eddine98.eatit_admin.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {



    // GUI componnents
    EditText edt_phone,edt_mdp;
    Button btnConnect;

    //Databases
    // Firebase stuff
    FirebaseDatabase bdd;
    DatabaseReference table_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // GUI componnents
        edt_phone=findViewById(R.id.edt_phone);
        edt_mdp=findViewById(R.id.edt_mdp);
        btnConnect=findViewById(R.id.btnConnect);





        // Databases

        bdd=FirebaseDatabase.getInstance();
        table_user=bdd.getReference("User");

        // Connect clicked event
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser(edt_phone.getText().toString(),edt_mdp.getText().toString());

            }
        });


    }

    private void loginUser(final String phone, String mdp) {

        final ProgressDialog dialog=new ProgressDialog(Login.this);
        dialog.setMessage("Vérification...");
        dialog.show();

        final String localPhone=phone;
        final String localMdp=mdp;



        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.child(localPhone).exists())
                {
                    dialog.dismiss();
                    User user=dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if (Boolean.parseBoolean(user.getIsStuff()))
                    {

                        if (user.getMdp().equals(localMdp))
                        {
                            //Do login
                            Intent homeAdmin=new Intent(Login.this,Home.class);
                            Common.currentUser=user;
                            startActivity(homeAdmin);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Login.this," Données incorrectes",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(Login.this," Acceès refusé!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(Login.this," Données incorrectes",Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
