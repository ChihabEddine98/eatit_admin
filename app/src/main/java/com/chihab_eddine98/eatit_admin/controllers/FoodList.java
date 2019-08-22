package com.chihab_eddine98.eatit_admin.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.interfaces.ItemClickListener;
import com.chihab_eddine98.eatit_admin.model.Food;
import com.chihab_eddine98.eatit_admin.viewHolder.FoodVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FoodList extends AppCompatActivity {

    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodVH> adapter;

    // Firebase
    FirebaseDatabase bdd;
    DatabaseReference table_food;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";


    // Search food by name
    MaterialSearchBar searchBar;
    FirebaseRecyclerAdapter<Food, FoodVH> searchAdapter;
    List<String> suggList=new ArrayList<>();

    //Add new Food
    // GUI
    FloatingActionButton fab;
    EditText edtNomFood,edtDescFood,edtPrixFood,edtReductionFood;
    Button btnSelect,btnUpload;

    // Model
    Food newFood;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        recycler_food=findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);

        recycler_food.setLayoutManager(layoutManager);

        fab=findViewById(R.id.fab_food);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAddFoodDialog(categoryId);
            }
        });



        bdd=FirebaseDatabase.getInstance();
        table_food=bdd.getReference("Food");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();




        if(getIntent()!=null)
        {
            categoryId=getIntent().getStringExtra("categoryId");

        }
        if(!categoryId.isEmpty() && categoryId!=null)
        {
            loadListFood(categoryId);
        }

        //Search

        searchBar=findViewById(R.id.searchBar);
        searchBar.setHint(" Entrez votre plat !");

        loadSuggest();
        searchBar.setLastSuggestions(suggList);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                List<String> suggest=new ArrayList<>();

                for (String search:suggList)
                {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }

                searchBar.setLastSuggestions(suggest);


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    recycler_food.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });




    }

    // Add new Food
    private void showAddFoodDialog(String categoryId) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(FoodList.this);
        dialog.setTitle(" Ajouter un nouveau produit");
//        dialog.setMessage(" Remplissez tout les champs svp");
        dialog.setIcon(R.drawable.ic_restaurant_black_24dp);


        LayoutInflater inflater=this.getLayoutInflater();
        View add_food_layout=inflater.inflate(R.layout.update_food_form,null);
        dialog.setView(add_food_layout);

        edtNomFood=add_food_layout.findViewById(R.id.edtNomFood);
        edtDescFood=add_food_layout.findViewById(R.id.edtDescFood);
        edtPrixFood=add_food_layout.findViewById(R.id.edtPrixFood);
        edtReductionFood=add_food_layout.findViewById(R.id.edtReductionFood);
        btnSelect=add_food_layout.findViewById(R.id.btnSelect);
        btnUpload=add_food_layout.findViewById(R.id.btnUpload);


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
                if(newFood!=null)
                {
                    table_food.push().setValue(newFood);
                    Snackbar.make(recycler_food," Nouveau Menu : "+newFood.getNom(),Snackbar.LENGTH_SHORT).show();
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

    private void chooseImg() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selectionner une image"),PICK_IMAGE_REQUEST);
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
                Toast.makeText(FoodList.this," Téléchargement terminé avec succès ",Toast.LENGTH_SHORT).show();
                imgFichier.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Si le upload est bien passé et on a bien un lien pour telecharger la photo apres
                        newFood=new Food(edtNomFood.getText().toString(),uri.toString(),
                                        edtDescFood.getText().toString(),
                                        edtPrixFood.getText().toString(),
                                        edtReductionFood.getText().toString(),
                                        categoryId);



                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


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

    // Update && Delete Food

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            showDeleteFoodDialog(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);
    }

    private void showUpdateFoodDialog(final String key, final Food food)
    {

        AlertDialog.Builder dialog=new AlertDialog.Builder(FoodList.this);
        dialog.setTitle(" Modifier un produit");
//        dialog.setMessage(" Remplissez tout les champs svp");
        dialog.setIcon(R.drawable.ic_restaurant_black_24dp);


        LayoutInflater inflater=this.getLayoutInflater();
        View add_food_layout=inflater.inflate(R.layout.update_food_form,null);
        dialog.setView(add_food_layout);

        edtNomFood=add_food_layout.findViewById(R.id.edtNomFood);
        edtDescFood=add_food_layout.findViewById(R.id.edtDescFood);
        edtPrixFood=add_food_layout.findViewById(R.id.edtPrixFood);
        edtReductionFood=add_food_layout.findViewById(R.id.edtReductionFood);
        btnSelect=add_food_layout.findViewById(R.id.btnSelect);
        btnUpload=add_food_layout.findViewById(R.id.btnUpload);

        edtNomFood.setText(food.getNom());
        edtDescFood.setText(food.getDescription());
        edtPrixFood.setText(food.getPrix());
        edtReductionFood.setText(food.getReduction());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImgFood(food);

            }
        });

        dialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                food.setNom(edtNomFood.getText().toString());
                food.setDescription(edtDescFood.getText().toString());
                food.setPrix(edtPrixFood.getText().toString());
                food.setReduction(edtReductionFood.getText().toString());


                table_food.child(key).setValue(food);
                Snackbar.make(recycler_food,"Produit modifié avec succès ",Snackbar.LENGTH_SHORT).show();
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

    private void changeImgFood(final Food food)  {

        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Téléchargement...");
        dialog.show();

        String nomImg= UUID.randomUUID().toString();

        final StorageReference imgFichier=storageReference.child("images/"+nomImg);
        imgFichier.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                dialog.dismiss();
                Toast.makeText(FoodList.this," Téléchargement terminé avec succès ",Toast.LENGTH_SHORT).show();
                imgFichier.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Si le upload est bien passé et on a bien un lien pour telecharger la photo apres
                        food.setImgUrl(uri.toString());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                dialog.dismiss();

                Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


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

    // Delete Catégo
    private void showDeleteFoodDialog(final String key)
    {

        AlertDialog.Builder dialog=new AlertDialog.Builder(FoodList.this);
        dialog.setTitle(" Supprimer un produit");
        dialog.setMessage(" Etes vous sur de vouloir supprimer ce produit ?");
        dialog.setIcon(R.drawable.ic_menu_manage);





        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                table_food.child(key).removeValue();
                Snackbar.make(recycler_food,"Produit Supprimé avec succès ",Snackbar.LENGTH_SHORT).show();

            }
        });

        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });



        dialog.show();
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


    // Search Méthodes
    private void startSearch(CharSequence text) {


        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodVH>(
                Food.class,
                R.layout.food_item,
                FoodVH.class,
                table_food.orderByChild("nom").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodVH foodVH, Food food, int i) {


                foodVH.food_nom.setText(food.getNom());
                Picasso.with(getBaseContext()).load(food.getImgUrl())
                        .into(foodVH.food_img);


                final Food local=food;

                foodVH.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

//                        Intent foodDetailActivity=new Intent(FoodList.this,FoodDetail.class);
//                        foodDetailActivity.putExtra("foodId",searchAdapter.getRef(position).getKey());
//
//                        startActivity(foodDetailActivity);
                    }
                });

            }
        };


        recycler_food.setAdapter(searchAdapter);
    }

    private void loadSuggest() {

        table_food.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postData:dataSnapshot.getChildren())
                {
                    Food itemSugg=postData.getValue(Food.class);
                    suggList.add(itemSugg.getNom());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    private void loadListFood(String categoryId) {

        adapter=new FirebaseRecyclerAdapter<Food, FoodVH>(Food.class,R.layout.food_item,FoodVH.class,
                table_food.orderByChild("categoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodVH foodVH, Food food, int i) {

                foodVH.food_nom.setText(food.getNom());
                Picasso.with(getBaseContext()).load(food.getImgUrl())
                        .into(foodVH.food_img);


                final Food local=food;

                foodVH.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

//                        Intent foodDetailActivity=new Intent(FoodList.this,FoodDetail.class);
//                        foodDetailActivity.putExtra("foodId",adapter.getRef(position).getKey());
//
//                        startActivity(foodDetailActivity);
                    }
                });



            }
        };

        adapter.notifyDataSetChanged();
        recycler_food.setAdapter(adapter);



    }
}