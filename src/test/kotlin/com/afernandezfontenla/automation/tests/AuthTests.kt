package com.afernandezfontenla.automation.tests

import com.afernandezfontenla.automation.base.BaseApiTest
import com.afernandezfontenla.automation.base.shouldHaveStatus
import com.afernandezfontenla.automation.config.ApiConfig
import com.afernandezfontenla.automation.dto.AuthRequest
import com.afernandezfontenla.automation.dto.AuthResponse
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

class AuthTests : BaseApiTest() {

    @Test(description = "Valid credentials return a non-empty token")
    fun validCredentialsReturnToken() = runApi {
        val response = authApi.login(AuthRequest(ApiConfig.username, ApiConfig.password))
            .shouldHaveStatus(200)

        assertThat(response.bodyAs<AuthResponse>().token).isNotBlank()
    }

    @Test(description = "Wrong credentials are rejected with a reason and no token")
    fun invalidCredentialsAreRejected() = runApi {
        // Restful-Booker quirk: bad credentials still answer HTTP 200, with a `reason` field.
        val response = authApi.login(AuthRequest(ApiConfig.username, "wrong-password"))
            .shouldHaveStatus(200)

        val body = response.bodyAs<AuthResponse>()
        assertThat(body.token).isNull()
        assertThat(body.reason).isEqualTo("Bad credentials")
    }
}
