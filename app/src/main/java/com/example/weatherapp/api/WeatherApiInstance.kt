package com.example.weatherapp.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object WeatherApiInstance {



    val api: WeatherApi by lazy{
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }







//    fun getCacheClient():OkHttpClient{
//        val cache = Cache(
//            File(
//                context.cacheDir,"http-cache"
//            ),10L*1024L*1024L
//        )
//        return OkHttpClient().newBuilder().addNetworkInterceptor(CacheInterceptor()).build()
//    }

}

//class CacheInterceptor:Interceptor{
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val response = chain.proceed(chain.request())
//        val cacheControl = CacheControl.Builder()
//            .maxAge(5,TimeUnit.MINUTES)
//            .build()
//
//        return response.newBuilder()
//            .header("Cache-Control",cacheControl.toString())
//            .build()
//
//    }
//}