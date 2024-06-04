package com.gyleedev.githubsearch.data.remote

import com.gyleedev.githubsearch.data.remote.response.RevokeResponse
import com.gyleedev.githubsearch.domain.model.RevokeRequestBody
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Path

interface RevokeService {
    @HTTP(method = "DELETE", path = "applications/{client_id}/grant", hasBody = true)
    suspend fun revoke(
        @Path("client_id") clientId: String,
        @Body accessToken: RevokeRequestBody,
    ): RevokeResponse
}
