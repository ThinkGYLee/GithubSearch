package com.gyleedev.data.remote

import com.gyleedev.data.BuildConfig
import com.gyleedev.data.remote.request.RevokeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Path

interface RevokeService {
    @HTTP(method = "DELETE", path = "applications/{client_id}/grant", hasBody = true)
    suspend fun revoke(
        @Path("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Body request: RevokeRequest,
    ): Response<Unit>
}
