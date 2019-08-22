package com.chihab_eddine98.eatit_admin.controllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.model.Order;
import com.chihab_eddine98.eatit_admin.viewHolder.OrderVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Commandes extends AppCompatActivity {



    RecyclerView recycler_commandes;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Order, OrderVH> adapter;



    FirebaseDatabase bdd;
    DatabaseReference table_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_commandes);

        // Firebase

        bdd=FirebaseDatabase.getInstance();
        table_order=bdd.getReference("FoodOrder");


        // GUI components
        recycler_commandes=findViewById(R.id.recycler_commandes);
        recycler_commandes.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recycler_commandes.setLayoutManager(layoutManager);

        // show mes commandes

        loadCommandes();


    }

    private void loadCommandes()
    {
        adapter=new FirebaseRecyclerAdapter<Order, OrderVH>(
                Order.class,
                R.layout.commande_item,
                OrderVH.class,
                table_order
        ) {
            @Override
            protected void populateViewHolder(OrderVH orderVH, Order order, int position)
            {

                orderVH.order_item_id.setText("#"+adapter.getRef(position).getKey());


                // Design Status
                // Préparation: rouge
                if(order.getStatus().equals("0"))
                {
                    orderVH.order_item_status.setTextColor(getResources().getColor(R.color.status_danger));
                    orderVH.order_item_status_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_20_black_24dp));
//                    Picasso.with(getBaseContext()).load(R.drawable.ic_battery_20_black_24dp)
//                            .into(orderVH.order_item_status_img);
                }
                // En route : jaune / bg noir
                else if(order.getStatus().equals("1"))
                {
                    orderVH.status_layout.setBackgroundColor(getResources().getColor(R.color.bg_color_gris_fonce));
                    orderVH.order_item_status.setTextColor(getResources().getColor(R.color.status_warning));
                    orderVH.order_item_status_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_50_black_24dp));


                }
                // Livrée: vert
                else if(order.getStatus().equals("2"))
                {
                    orderVH.order_item_status.setTextColor(getResources().getColor(R.color.status_succes));
                    orderVH.order_item_status_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_full_black_24dp));
                }
                // Rembourssement: jaune / bg noir
                else if(order.getStatus().equals("3"))
                {
                    orderVH.status_layout.setBackgroundColor(getResources().getColor(R.color.bg_color_gris_fonce));
                    orderVH.order_item_status.setTextColor(getResources().getColor(R.color.status_warning));
                    orderVH.order_item_status_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_alert_black_24dp));

                }

                orderVH.order_item_status.setText(statusConverted(order.getStatus()));
                orderVH.order_item_phone.setText(order.getPhone());
                orderVH.order_item_nomComplet.setText(order.getNom());
                orderVH.order_item_adresse.setText(order.getAdresse());

            }
        };


        recycler_commandes.setAdapter(adapter);

    }

    private String statusConverted(String status) {

        String result="";

        // "En préparation";
        if(status.equals("0"))
        {
            result="En préparation";
        }
        // En route : jaune / bg noir
        else if(status.equals("1"))
        {
            result="En route";
        }
        // Livrée: vert
        else if(status.equals("2"))
        {
            result="Livrée";
        }
        // Rembourssement: jaune / bg noir
        else if(status.equals("3"))
        {
            result="Rembourssement";
        }


        return result;
    }
}
