package tests

import client.ApiClient
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import logger.CustomLogger
import org.testng.Assert
import org.testng.annotations.Test

class UserE2ETest {

    private val apiClient = ApiClient("https://reqres.in/api")

    @Test
    fun testGetUsersList() = runBlocking {
        CustomLogger.info("Iniciando prueba: Obtener lista de usuarios")

        val response: HttpResponse = apiClient.get("/users?page=2")  

        Assert.assertEquals(response.status.value, 200, "El código de estado HTTP debería ser 200")

        val responseBody = response.bodyAsText()
        Assert.assertTrue(responseBody.contains("data"), "La respuesta debe contener la clave 'data'")

        CustomLogger.success("¡Prueba completada con éxito!")
    }

    @Test
    fun testCreateUser() = runBlocking {
        CustomLogger.info("Iniciando prueba: Crear un nuevo usuario")

        val jsonPayload = """
            {
                "name": "QA Tester",
                "job": "Automation Engineer"
            }
        """.trimIndent()

        val response: HttpResponse = apiClient.post("/users", jsonPayload)

        Assert.assertEquals(response.status.value, 201, "El código de estado HTTP debería ser 201 Created")

        val responseBody = response.bodyAsText()
        Assert.assertTrue(responseBody.contains("QA Tester"), "La respuesta debe reflejar el nombre enviado")

        CustomLogger.success("¡Usuario creado y validado correctamente!")
    }
}