package com.chihab_eddine98.eatit_admin.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinates
{
    // Cette interface fait appel aux service et api de google 'l'api key est donné dans les méthodes
    // Ceci est juste pour le test après c'est pas aussi safe :D


    @GET("maps/api/geocode/json")
    Call<String> getGeoCode(@Query("address") String address,@Query("key") String key);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin,@Query("destination") String destination,@Query("key") String k);



}
