package com.alirezasn80.learn_en.core.data.service

import com.alirezasn80.learn_en.core.domain.remote.Category
import com.alirezasn80.learn_en.core.domain.remote.RemoteModel
import com.alirezasn80.learn_en.feature.stories.model.BookResponse
import com.alirezasn80.learn_en.utill.toRB
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url


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
    suspend fun getBooks(
        @Query("id") id: Int,
        @Query("page") page: Int
    ): BookResponse


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
    suspend fun getCategories(): List<Category>

    //-----------------------------------------------------------------------------------

    //Webservice
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody
}