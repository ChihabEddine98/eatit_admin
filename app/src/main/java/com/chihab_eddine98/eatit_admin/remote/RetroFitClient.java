package com.chihab_eddine98.eatit_admin.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroFitClient
{

    private static Retrofit retrofit=null;


    public static Retrofit getClient(String baseUrl)
    {
        if (retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                     .baseUrl(baseUrl)
                     .addConverterFactory(ScalarsConverterFactory.create())
                     .build();
        }

        return retrofit;

    }


}
