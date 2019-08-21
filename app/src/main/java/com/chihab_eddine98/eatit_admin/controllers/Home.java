package com.chihab_eddine98.eatit_admin.controllers;

import android.content.Intent;
import android.os.Bundle;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.interfaces.ItemClickListener;
import com.chihab_eddine98.eatit_admin.model.Category;
import com.chihab_eddine98.eatit_admin.viewHolder.CategoryVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.TextView;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase bdd;
    DatabaseReference table_category;
    TextView txtPrenom;

    //Recycler View
    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, CategoryVH> adapter;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        recycler_category.setAdapter(adapter);


    }
}
