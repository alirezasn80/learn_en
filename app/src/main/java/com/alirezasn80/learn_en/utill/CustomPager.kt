package com.alirezasn80.learn_en.utill

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow


class CustomPager<T : Any>(
    private val request: suspend CustomPager<T>.(page: Int) -> List<T>,
    private val handleException: (Exception) -> Unit,
) : PagingSource<Int, T>() {
    private lateinit var data: List<T>
    private var page = 1
    private var prevKey: Int? = null
    private var nextKey: Int? = null

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            page = params.key ?: 1
            data = request(this, page)
            getLoadResult()
        } catch (e: Exception) {
            handleException(e)
            LoadResult.Error(e)
        }

    }

    fun setKeys(isEmpty: Boolean?) {
        try {
            prevKey = if (page == 1) null else page - 1
            nextKey = if (isEmpty == true) null else page + 1
        } catch (e: Exception) {
            handleException(e)
        }

    }

    private fun getLoadResult(): LoadResult.Page<Int, T> {
        return LoadResult.Page(
            data = data,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    fun build(): Flow<PagingData<T>> {
        return Pager(config = PagingConfig(15), pagingSourceFactory = { this }).flow
    }
}