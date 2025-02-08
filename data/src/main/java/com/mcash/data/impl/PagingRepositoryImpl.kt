package com.mcash.data.impl

import com.mcash.domain.repository.ClientRepository
import com.mcash.domain.repository.LocalRepository
import com.mcash.domain.repository.PagingRepository
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val client: ClientRepository,
    private val local: LocalRepository,
): PagingRepository {

//    @OptIn(ExperimentalPagingApi::class)
//    override fun getCategoryPagingData(): Flow<PagingData<CategoryEntity>> {
//
//        val pagingSourceFactory = { local.getAllCategories() }
//
//        return Pager( 
//            config = PagingConfig(pageSize = 40),
//            remoteMediator = CategoryRemoteMediator(categoryDao, client),
//            pagingSourceFactory = pagingSourceFactory
//        ).flow
//    }

//    override fun getRelatedProductsPagingData(productId: String): Flow<PagingData<Product>> {
//        return Pager(
//            PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false)
//        ) {
//            RelatedProductsPagingSource(client, productId)
//        }.flow
//    }
}