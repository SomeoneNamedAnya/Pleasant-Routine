package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.domain.discovery.CreateSharingRequest
import com.coursework.pleasantroutineui.services.DiscoveryApi
import javax.inject.Inject

class DiscoveryRepository @Inject constructor(
    private val api: DiscoveryApi
) {
    suspend fun searchPeople(id: Long?, name: String?, page: Int, size: Int) =
        api.searchPeople(id, name, page, size)

    suspend fun searchRooms(id: Long?, number: String?, page: Int, size: Int) =
        api.searchRooms(id, number, page, size)

    suspend fun getNews(page: Int, size: Int) =
        api.getNews(page, size)

    suspend fun createSharing(req: CreateSharingRequest) = api.createSharing(req)
    suspend fun claimSharing(id: Long) = api.claimSharing(id)
    suspend fun deleteSharing(id: Long) = api.deleteSharing(id)

    suspend fun myCreated(page: Int, size: Int) = api.myCreated(page, size)
    suspend fun myClaimed(page: Int, size: Int) = api.myClaimed(page, size)
    suspend fun allActive(page: Int, size: Int) = api.allActive(page, size)
}