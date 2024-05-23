package com.alirezasn80.learn_en.core.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Book----------------------------------------------------------

    @POST("add_book")
    fun addBook(
        @Part("category_id") categoryId: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part,
    ): String

    @POST("active_book")
    fun deleteBook(
        @Part("book_id") id: RequestBody,
    ): String


    @POST("update_book")
    fun editBook(
        @Part("category_id") categoryId: RequestBody,
        @Part("book_id") bookId: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part,
    ): String

    @GET("get_books/{page}")
    fun getBooks(
        @Path("page") page: Int
    ): Unit//todo()


    // Category ----------------------------------------------------------

    @POST("create_category")
    fun addCategory(
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
    ): String

    @POST("update_category")
    fun editCategory(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
    ): String


    @POST("active_category")
    fun deleteCategory(
        @Part("id") id: RequestBody,
    ): String

    @GET("get_categories")
    fun getCategories(): Unit//todo()

    //-----------------------------------------------------------------------------------
}