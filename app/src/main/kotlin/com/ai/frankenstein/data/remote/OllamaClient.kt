package com.ai.frankenstein.data.remote

import android.util.Log
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.Model
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OllamaClient {
    
    companion object {
        private const val TAG = "OllamaClient"
        private const val TIMEOUT_SECONDS = 120L // Local models can be slower
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
    
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }
    
    // Send request to local Ollama server
    suspend fun sendOllamaRequest(
        model: Model,
        endpoint: String,
        messages: List<Message>,
        stream: Boolean = true,
        temperature: Float = 0.7f,
        maxTokens: Int? = null,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
            // Use the model's endpoint or default to localhost
            val baseUrl = endpoint.ifEmpty { "http://localhost:11434" }
            val url = "$baseUrl/api/chat"
            
            val requestMessages = messages.map { message ->
                JSONObject().apply {
                    put("role", message.role.name.lowercase())
                    put("content", message.content)
                }
            }
            
            val requestBody = JSONObject().apply {
                put("model", model.id)
                put("messages", JSONArray(requestMessages))
                put("stream", stream)
                put("options", JSONObject().apply {
                    put("temperature", temperature)
                    maxTokens?.let { put("max_tokens", it) }
                })
            }
            
            val request = Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                onComplete(Result.failure(Exception("Ollama API error: ${response.code} - ${response.message}")))
                return
            }
            
            if (stream) {
                // For streaming, we'd need to handle SSE
                // This is a simplified version
                val responseBody = response.body?.string() ?: throw Exception("Empty response body")
                onComplete(Result.success(responseBody))
            } else {
                val responseBody = response.body?.string() ?: throw Exception("Empty response body")
                val json = JSONObject(responseBody)
                
                if (json.has("message") && json.getJSONObject("message").has("content")) {
                    val content = json.getJSONObject("message").getString("content")
                    onComplete(Result.success(content))
                } else {
                    onComplete(Result.failure(Exception("Invalid response format: $responseBody")))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send Ollama request", e)
            onComplete(Result.failure(e))
        }
    }
    
    // Check if Ollama server is running
    suspend fun checkOllamaHealth(endpoint: String = "http://localhost:11434"): Result<Boolean> {
        return try {
            val request = Request.Builder()
                .url("$endpoint/api/tags")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get available models from Ollama
    suspend fun getOllamaModels(endpoint: String = "http://localhost:11434"): Result<List<Model>> {
        return try {
            val request = Request.Builder()
                .url("$endpoint/api/tags")
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ollama API error: ${response.code}"))
            }
            
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            val json = JSONObject(responseBody)
            
            if (json.has("models") && json.getJSONArray("models").length() > 0) {
                val models = mutableListOf<Model>()
                val modelsArray = json.getJSONArray("models")
                
                for (i in 0 until modelsArray.length()) {
                    val modelObj = modelsArray.getJSONObject(i)
                    val model = Model(
                        id = modelObj.getString("name"),
                        name = modelObj.getString("name"),
                        provider = "ollama",
                        description = "Local model via Ollama",
                        maxTokens = modelObj.optInt("max_tokens", 8192),
                        contextWindow = modelObj.optInt("context_window", 8192),
                        inputCost = 0.0,
                        outputCost = 0.0,
                        isCustom = false,
                        isActive = true,
                        category = "text",
                        capabilities = listOf("text", "streaming"),
                        endpoint = endpoint,
                        sortOrder = 100 + i
                    )
                    models.add(model)
                }
                
                Result.success(models)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get Ollama models", e)
            Result.failure(e)
        }
    }
    
    // Pull a model from Ollama
    suspend fun pullModel(modelName: String, endpoint: String = "http://localhost:11434"): Result<String> {
        return try {
            val request = Request.Builder()
                .url("$endpoint/api/pull")
                .post("""{"name": "$modelName"}""".toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return Result.failure(Exception("Ollama API error: ${response.code} - ${response.message}"))
            }
            
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            Result.success(responseBody)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pull model $modelName", e)
            Result.failure(e)
        }
    }
}
