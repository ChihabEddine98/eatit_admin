package com.chihab_eddine98.eatit_admin.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.chihab_eddine98.eatit_admin.model.FoodOrder;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME="eat_it.db";
    private static final int  DB_VERSION=1;

    public Database(Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }



    public List<FoodOrder> getCart()
    {

        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();


        String[] sqlSelect={"foodNom","foodId","qte","prix","reduction"};
        String sqlTable="OrderFood";

        qb.setTables(sqlTable);

        Cursor cursor=qb.query(db,sqlSelect,null,null,null,null,null);
        List<FoodOrder> result=new ArrayList<>();

        if (cursor.moveToFirst())
        {
            do {
                FoodOrder order=new FoodOrder(cursor.getString(cursor.getColumnIndex("foodId")),
                                      cursor.getString(cursor.getColumnIndex("foodNom")),
                                      cursor.getString(cursor.getColumnIndex("qte")),
                                      cursor.getString(cursor.getColumnIndex("prix")),
                                      cursor.getString(cursor.getColumnIndex("reduction")));


                result.add(order);

            }while (cursor.moveToNext());
        }

        return result;

    }

    public void addToCart(FoodOrder order)
    {
        SQLiteDatabase db=getReadableDatabase();

        String query=String.format("INSERT INTO OrderFood(foodId,foodNom,qte,prix,reduction) VALUES('%s','%s','%s','%s','%s');"
                ,order.getFoodId(),order.getFoodName(),order.getQte(),order.getPrix(),order.getReduction());


        db.execSQL(query);

    }

    public void cleanCart()
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderFood;");
        db.execSQL(query);

    }
}
