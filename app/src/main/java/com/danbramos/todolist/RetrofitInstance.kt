package com.danbramos.todolist

import com.danbramos.todolist.service.TaskApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient is a singleton object responsible for providing a configured instance of Retrofit
 * for making network requests to the task management API.
 *
 * It uses a lazy-initialized `apiService` to provide a `TaskApiService` instance, ensuring that
 * the Retrofit instance is only created when it's first needed.
 */
object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.4:8080/"

    val apiService: TaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApiService::class.java)
    }
}