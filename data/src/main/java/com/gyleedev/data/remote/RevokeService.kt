package com.gyleedev.data.remote

import com.gyleedev.data.remote.request.RevokeRequest
import com.gyleedev.data.remote.response.RevokeResponse
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Path

interface RevokeService {
    @HTTP(method = "DELETE", path = "applications/{client_id}/grant", hasBody = true)
    suspend fun revoke(@Path("client_id") clientId: String, @Body accessToken: RevokeRequest): RevokeResponse
}
