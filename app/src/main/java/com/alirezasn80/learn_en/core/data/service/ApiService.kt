package com.alirezasn80.learn_en.core.data.service

import androidx.annotation.Keep
import com.alirezasn80.learn_en.utill.toRB
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
data class RemoteModel(
    @SerializedName("success")
    val success: Boolean
)

interface ApiService {

    // Book----------------------------------------------------------

    @Multipart
    @POST("add_book")
    suspend fun addBook(
        @Part("category_id") categoryId: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part,
        @Part("key") key: RequestBody = "13800831".toRB(),

    ): RemoteModel

    @POST("active_book")
    suspend fun deleteBook(
        @Part("book_id") id: RequestBody,
    ): String


    @Multipart
    @POST("update_book")
    suspend fun editBook(
        @Part("category_id") categoryId: RequestBody,
        @Part("book_id") bookId: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part,

        ): String

    @GET("get_books")
    fun getBooks(
        @Query("id") id: Int
    ): Unit//todo()


    // Category ----------------------------------------------------------

    @Multipart
    @POST("create_category")
    suspend fun addCategory(
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
    ): String

    @Multipart
    @POST("update_category")
    suspend fun editCategory(
        @Part("id") id: RequestBody,
        @Part("name") name: RequestBody,
        @Part cover: MultipartBody.Part?,
    ): String


    @POST("active_category")
    suspend fun deleteCategory(
        @Part("id") id: RequestBody,
    ): String

    @GET("get_categories")
    suspend fun getCategories(): Unit//todo()

    //-----------------------------------------------------------------------------------
}