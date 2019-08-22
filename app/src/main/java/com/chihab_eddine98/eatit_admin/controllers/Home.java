package com.chihab_eddine98.eatit_admin.controllers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.interfaces.ItemClickListener;
import com.chihab_eddine98.eatit_admin.model.Category;
import com.chihab_eddine98.eatit_admin.viewHolder.CategoryVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // BDD
    FirebaseDatabase bdd;
    DatabaseReference table_category;


    // GUI
    TextView txtPrenom;
    EditText edtNomCatego; // popup
    Button btnSelect,btnUpload;

    DrawerLayout drawer;

    //Recycler View
    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryVH> adapter;
    FirebaseStorage storage;
    StorageReference storageReference;

    // New Data
    Category newCatego;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddCategoryDialog();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //----------------------------
        // Our code
        //----------------------------

        bdd=FirebaseDatabase.getInstance();
        table_category=bdd.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();



        /* On bind le prénom De l'utilisateur dans la navigation Head*/

        View headerView=navigationView.getHeaderView(0);
        txtPrenom=(TextView)headerView.findViewById(R.id.txtPrenom);
        txtPrenom.setText(Common.currentUser.getPrenom());


        recycler_category=(RecyclerView)findViewById(R.id.recycler_category);
        recycler_category.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_category.setLayoutManager(layoutManager);


        loadCategories();
    }

    // Own Code
    // Add Catgory
    private void showAddCategoryDialog() {

        AlertDialog.Builder dialog=new AlertDialog.Builder(Home.this);
        dialog.setTitle(" Ajouter une nouvelle catégorie");
        dialog.setMessage(" Remplissez tout les champs svp");
        dialog.setIcon(R.drawable.ic_restaurant_black_24dp);


        LayoutInflater inflater=this.getLayoutInflater();
        View add_catego_layout=inflater.inflate(R.layout.activity_add_category,null);
        dialog.setView(add_catego_layout);

        edtNomCatego=add_catego_layout.findViewById(R.id.edtNomCatego);
        btnSelect=add_catego_layout.findViewById(R.id.btnSelect);
        btnUpload=add_catego_layout.findViewById(R.id.btnUpload);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImg();
            }
        });

        dialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                if(newCatego!=null)
                {
                    table_category.push().setValue(newCatego);
                    Snackbar.make(drawer," Nouveau Menu : "+newCatego.getNom(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });



        dialog.show();

    }

    private void uploadImg() {

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Téléchargement...");
        dialog.show();

        String nomImg= UUID.randomUUID().toString();

        final StorageReference imgFichier=storageReference.child("images/"+nomImg);
        imgFichier.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                dialog.dismiss();
                Toast.makeText(Home.this," Téléchargement terminé avec succès ",Toast.LENGTH_SHORT).show();
                imgFichier.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Si le upload est bien passé et on a bien un lien pour telecharger la photo apres
                        newCatego=new Category(edtNomCatego.getText().toString(),uri.toString());



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

             // Progress pourcent (transféré/total)*100
             double taux=100*(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

             dialog.setMessage(taux+"%"+" téléchrgés");




            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
            && data!=null && data.getData()!=null )
        {
            saveUri=data.getData();
            btnSelect.setText("Selectionnée");
        }

    }

    private void chooseImg() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selectionner une image"),PICK_IMAGE_REQUEST);
    }

    // Update && Delete Category

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateCategoryDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }


        return super.onContextItemSelected(item);
    }

    private void showUpdateCategoryDialog(final String key, final Category catego)
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(Home.this);
        dialog.setTitle(" Modifier une catégorie");
        dialog.setMessage(" Remplissez tout les champs svp");
        dialog.setIcon(R.drawable.ic_restaurant_black_24dp);



        LayoutInflater inflater=this.getLayoutInflater();
        View add_catego_layout=inflater.inflate(R.layout.activity_add_category,null);
        dialog.setView(add_catego_layout);

        edtNomCatego=add_catego_layout.findViewById(R.id.edtNomCatego);
        btnSelect=add_catego_layout.findViewById(R.id.btnSelect);
        btnUpload=add_catego_layout.findViewById(R.id.btnUpload);

        edtNomCatego.setText(catego.getNom());

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImgCatego(catego);
            }
        });

        dialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                catego.setNom(edtNomCatego.getText().toString());
                table_category.child(key).setValue(catego);
            }
        });

        dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });



        dialog.show();
    }

    private void changeImgCatego(final Category catego)  {

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Téléchargement...");
        dialog.show();

        String nomImg= UUID.randomUUID().toString();

        final StorageReference imgFichier=storageReference.child("images/"+nomImg);
        imgFichier.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                dialog.dismiss();
                Toast.makeText(Home.this," Téléchargement terminé avec succès ",Toast.LENGTH_SHORT).show();
                imgFichier.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Si le upload est bien passé et on a bien un lien pour telecharger la photo apres
                        catego.setImgUrl(uri.toString());



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                // Progress pourcent (transféré/total)*100
                double taux=100*(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                dialog.setMessage(taux+"%"+" téléchrgés");




            }
        });
    }

    // Init view (menus)

    private void loadCategories() {


        adapter=new FirebaseRecyclerAdapter<Category, CategoryVH>(Category.class,R.layout.category_item,CategoryVH.class,table_category) {
            @Override
            protected void populateViewHolder(CategoryVH categoryVH, Category category, int i) {

                categoryVH.category_nom.setText(category.getNom());
                Picasso.with(getBaseContext()).load(category.getImgUrl())
                        .into(categoryVH.category_img);
                final Category clickItem=category;

                categoryVH.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        Intent foodActivity=new Intent(Home.this,FoodList.class);
//                        foodActivity.putExtra("categoryId",adapter.getRef(position).getKey());
//
//                        startActivity(foodActivity);

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recycler_category.setAdapter(adapter);


    }












    // generated Code
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

//            Intent cartIntent=new Intent(Home.this,Cart.class);
//            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {

//            Intent mesCommandesIntent=new Intent(Home.this,MesCommandes.class);
//            startActivity(mesCommandesIntent);

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {

            Intent loginIntent=new Intent(Home.this,Login.class);

            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);



        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
