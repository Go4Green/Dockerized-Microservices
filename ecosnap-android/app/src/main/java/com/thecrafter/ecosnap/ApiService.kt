package com.thecrafter.ecosnap

import com.thecrafter.ecosnap.data.Incident
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {

    companion object {
        val service: ApiService = Retrofit.Builder()
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://172.16.176.42:5000")
            .build()
            .create<ApiService>(ApiService::class.java)
    }

    @GET("/citizens/{uid}/incidents")
    fun listIncidents(@Path("uid") uid: String): Call<List<Incident>>

    @POST("/citizens/{uid}/incidents")
    fun createIncident(@Path("uid") uid: String, @Body incident: Incident): Call<String>

    @GET("/citizens/{uid}/ecoPoints")
    fun ecoPoints(@Path("uid") uid: String): Call<Int>
}