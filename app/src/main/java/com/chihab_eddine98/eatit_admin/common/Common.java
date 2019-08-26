package com.chihab_eddine98.eatit_admin.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chihab_eddine98.eatit_admin.model.Order;
import com.chihab_eddine98.eatit_admin.model.User;
import com.chihab_eddine98.eatit_admin.remote.IGeoCoordinates;
import com.chihab_eddine98.eatit_admin.remote.RetroFitClient;

import retrofit2.Retrofit;

public class Common {

    public static User currentUser;
    public static Order currentOrder;


    public static String UPDATE="Modifier";
    public static String DELETE="Supprimer";


    // Geolocation managing

    private static final String baseUrl="https://maps.googleapis.com";

    public static IGeoCoordinates getGeoCodeService()
    {
        return RetroFitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }



    // Pour retailler l'icon de la commande dans la map
    public static Bitmap scaleBitMap(Bitmap bitmap,int newWidth,int newHeight)
    {

        Bitmap scaledBitMap=Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=20;

        Matrix scaleMatrix=new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas=new Canvas(scaledBitMap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));



        return scaledBitMap;

    }



























}
