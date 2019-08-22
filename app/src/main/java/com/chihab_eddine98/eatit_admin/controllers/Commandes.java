package com.chihab_eddine98.eatit_admin.controllers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chihab_eddine98.eatit_admin.R;
import com.chihab_eddine98.eatit_admin.common.Common;
import com.chihab_eddine98.eatit_admin.interfaces.ItemClickListener;
import com.chihab_eddine98.eatit_admin.model.Order;
import com.chihab_eddine98.eatit_admin.viewHolder.OrderVH;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class Commandes extends AppCompatActivity {



    RecyclerView recycler_commandes;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Order, OrderVH> adapter;

    FirebaseDatabase bdd;
    DatabaseReference table_order;


    // Update
    MaterialSpinner statusSpinner;
    TextView txtOrderId;



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

    // Menu ( Update & Delete )
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateOrderDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            showDeleteOrderDialog(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);
    }

    // Update Order
    private void showUpdateOrderDialog(final String key, final Order order)
    {

        AlertDialog.Builder dialog=new AlertDialog.Builder(Commandes.this);
        dialog.setTitle(" Modifier une commande");
//        dialog.setMessage(" Remplissez tout les champs svp");
        dialog.setIcon(R.drawable.ic_av_timer_black_24dp);


        LayoutInflater inflater=this.getLayoutInflater();
        View update=inflater.inflate(R.layout.update_order_form,null);
        dialog.setView(update);

        statusSpinner=update.findViewById(R.id.statusSpinner);
        statusSpinner.setItems("En préparation","En route","Livrée","Rembourssement");
        statusSpinner.setSelectedIndex(Integer.parseInt(order.getStatus()));

        txtOrderId=update.findViewById(R.id.txtOrderId);
        txtOrderId.setText("#"+key);




        dialog.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();


                order.setStatus(String.valueOf(statusSpinner.getSelectedIndex()) );

                table_order.child(key).setValue(order);
                Snackbar.make(recycler_commandes,"Commande modifiée avec succès ",Snackbar.LENGTH_SHORT).show();
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

    // Delete Catégo
    private void showDeleteOrderDialog(final String key)
    {

        AlertDialog.Builder dialog=new AlertDialog.Builder(Commandes.this);
        dialog.setTitle(" Supprimer une Commande");
        dialog.setMessage(" Etes vous sur de vouloir supprimer cette commende ?");
        dialog.setIcon(R.drawable.ic_menu_manage);





        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                table_order.child(key).removeValue();
                Snackbar.make(recycler_commandes,"Commande Supprimée avec succès ",Snackbar.LENGTH_SHORT).show();

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


                orderVH.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }
        };


        adapter.notifyDataSetChanged();
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
