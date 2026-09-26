package com.ai.frankenstein.data.remote

import android.util.Log
import com.ai.frankenstein.domain.model.Message
import com.ai.frankenstein.domain.model.Model
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient {
    
    companion object {
        private const val TAG = "ApiClient"
        private const val TIMEOUT_SECONDS = 60L
        
        // Media types
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
        
        // Endpoints
        private const val OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"
        private const val ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages"
        private const val MISTRAL_API_URL = "https://api.mistral.ai/v1/chat/completions"
        private const val GOOGLE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }
    
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(LoggingInterceptor())
            .build()
    }
    
    // Send chat completion request
    suspend fun sendChatCompletion(
        model: Model,
        apiKey: String,
        messages: List<Message>,
        stream: Boolean = true,
        temperature: Float = 0.7f,
        maxTokens: Int? = null,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
            when (model.provider.lowercase()) {
                "openai" -> sendOpenAIRequest(model, apiKey, messages, stream, temperature, maxTokens, onStream, onComplete)
                "anthropic" -> sendAnthropicRequest(model, apiKey, messages, stream, temperature, maxTokens, onStream, onComplete)
                "mistral" -> sendMistralRequest(model, apiKey, messages, stream, temperature, maxTokens, onStream, onComplete)
                "google" -> sendGoogleRequest(model, apiKey, messages, stream, temperature, maxTokens, onStream, onComplete)
                else -> onComplete(Result.failure(Exception("Unsupported provider: ${model.provider}")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send chat completion", e)
            onComplete(Result.failure(e))
        }
    }
    
    // OpenAI API
    private fun sendOpenAIRequest(
        model: Model,
        apiKey: String,
        messages: List<Message>,
        stream: Boolean,
        temperature: Float,
        maxTokens: Int?,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
            val requestMessages = JSONArray().apply {
                messages.forEach { message ->
                    put(JSONObject().apply {
                        put("role", message.role.name.lowercase())
                        put("content", message.content)
                    })
                }
            }
            
            val requestBody = JSONObject().apply {
                put("model", model.id)
                put("messages", requestMessages)
                put("stream", stream)
                put("temperature", temperature)
                maxTokens?.let { put("max_tokens", it) }
            }
            
            val request = Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            if (stream) {
                // Handle streaming response
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onComplete(Result.failure(e))
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            onComplete(Result.failure(Exception("OpenAI API error: ${response.code} - ${response.message}")))
                            return
                        }
                        
                        try {
                            val body = response.body ?: throw IOException("Empty response body")
                            val source = body.source()
                            val buffer = okio.Buffer()
                            
                            while (source.read(buffer, 8192L) != -1L) {
                                val chunk = buffer.readUtf8()
                                buffer.clear()
                                
                                // Parse SSE chunks
                                chunk.split("\n\n").forEach { line ->
                                    if (line.startsWith("data: ")) {
                                        val data = line.substring(6).trim()
                                        if (data != "[DONE]") {
                                            try {
                                                val json = JSONObject(data)
                                                if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                                                    val choice = json.getJSONArray("choices").getJSONObject(0)
                                                    if (choice.has("delta") && choice.getJSONObject("delta").has("content")) {
                                                        val content = choice.getJSONObject("delta").getString("content")
                                                        onStream(content)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Failed to parse OpenAI stream chunk", e)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            onComplete(Result.success(""))
                            body.close()
                        } catch (e: Exception) {
                            onComplete(Result.failure(e))
                        }
                    }
                })
            } else {
                // Non-streaming response
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onComplete(Result.failure(Exception("OpenAI API error: ${response.code} - ${response.message}")))
                    return
                }
                
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val json = JSONObject(responseBody)
                
                if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                    val choice = json.getJSONArray("choices").getJSONObject(0)
                    if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                        val content = choice.getJSONObject("message").getString("content")
                        onComplete(Result.success(content))
                    } else {
                        onComplete(Result.failure(Exception("Invalid response format")))
                    }
                } else {
                    onComplete(Result.failure(Exception("Invalid response format")))
                }
            }
        } catch (e: Exception) {
            onComplete(Result.failure(e))
        }
    }
    
    // Anthropic API
    private fun sendAnthropicRequest(
        model: Model,
        apiKey: String,
        messages: List<Message>,
        stream: Boolean,
        temperature: Float,
        maxTokens: Int?,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
            // Anthropic uses a different message format
            val requestMessages = messages.map { message ->
                JSONObject().apply {
                    put("role", when (message.role) {
                        Message.MessageRole.USER -> "user"
                        Message.MessageRole.ASSISTANT -> "assistant"
                        Message.MessageRole.SYSTEM -> "system"
                    })
                    put("content", message.content)
                }
            }
            
            val requestBody = JSONObject().apply {
                put("model", model.id)
                put("messages", JSONArray(requestMessages))
                put("stream", stream)
                put("temperature", temperature)
                maxTokens?.let { put("max_tokens", it) }
            }
            
            val request = Request.Builder()
                .url(ANTHROPIC_API_URL)
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .header("anthropic-version", "2023-06-01")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            if (stream) {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onComplete(Result.failure(e))
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            onComplete(Result.failure(Exception("Anthropic API error: ${response.code} - ${response.message}")))
                            return
                        }
                        
                        try {
                            val body = response.body ?: throw IOException("Empty response body")
                            val source = body.source()
                            val buffer = okio.Buffer()
                            
                            while (source.read(buffer, 8192L) != -1L) {
                                val chunk = buffer.readUtf8()
                                buffer.clear()
                                
                                // Parse SSE chunks
                                chunk.split("\n\n").forEach { line ->
                                    if (line.startsWith("data: ")) {
                                        val data = line.substring(6).trim()
                                        if (data != "[DONE]") {
                                            try {
                                                val json = JSONObject(data)
                                                if (json.has("delta") && json.getJSONObject("delta").has("text")) {
                                                    val content = json.getJSONObject("delta").getString("text")
                                                    onStream(content)
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Failed to parse Anthropic stream chunk", e)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            onComplete(Result.success(""))
                            body.close()
                        } catch (e: Exception) {
                            onComplete(Result.failure(e))
                        }
                    }
                })
            } else {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onComplete(Result.failure(Exception("Anthropic API error: ${response.code} - ${response.message}")))
                    return
                }
                
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val json = JSONObject(responseBody)
                
                if (json.has("content") && json.getJSONArray("content").length() > 0) {
                    val content = json.getJSONArray("content").getJSONObject(0).getString("text")
                    onComplete(Result.success(content))
                } else {
                    onComplete(Result.failure(Exception("Invalid response format")))
                }
            }
        } catch (e: Exception) {
            onComplete(Result.failure(e))
        }
    }
    
    // Mistral API
    private fun sendMistralRequest(
        model: Model,
        apiKey: String,
        messages: List<Message>,
        stream: Boolean,
        temperature: Float,
        maxTokens: Int?,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
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
                put("temperature", temperature)
                maxTokens?.let { put("max_tokens", it) }
            }
            
            val request = Request.Builder()
                .url(MISTRAL_API_URL)
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            if (stream) {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onComplete(Result.failure(e))
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            onComplete(Result.failure(Exception("Mistral API error: ${response.code} - ${response.message}")))
                            return
                        }
                        
                        try {
                            val body = response.body ?: throw IOException("Empty response body")
                            val source = body.source()
                            val buffer = okio.Buffer()
                            
                            while (source.read(buffer, 8192L) != -1L) {
                                val chunk = buffer.readUtf8()
                                buffer.clear()
                                
                                // Parse SSE chunks
                                chunk.split("\n\n").forEach { line ->
                                    if (line.startsWith("data: ")) {
                                        val data = line.substring(6).trim()
                                        if (data != "[DONE]") {
                                            try {
                                                val json = JSONObject(data)
                                                if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                                                    val choice = json.getJSONArray("choices").getJSONObject(0)
                                                    if (choice.has("delta") && choice.getJSONObject("delta").has("content")) {
                                                        val content = choice.getJSONObject("delta").getString("content")
                                                        onStream(content)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Failed to parse Mistral stream chunk", e)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            onComplete(Result.success(""))
                            body.close()
                        } catch (e: Exception) {
                            onComplete(Result.failure(e))
                        }
                    }
                })
            } else {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onComplete(Result.failure(Exception("Mistral API error: ${response.code} - ${response.message}")))
                    return
                }
                
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val json = JSONObject(responseBody)
                
                if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                    val choice = json.getJSONArray("choices").getJSONObject(0)
                    if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                        val content = choice.getJSONObject("message").getString("content")
                        onComplete(Result.success(content))
                    } else {
                        onComplete(Result.failure(Exception("Invalid response format")))
                    }
                } else {
                    onComplete(Result.failure(Exception("Invalid response format")))
                }
            }
        } catch (e: Exception) {
            onComplete(Result.failure(e))
        }
    }
    
    // Google API (Gemini)
    private fun sendGoogleRequest(
        model: Model,
        apiKey: String,
        messages: List<Message>,
        stream: Boolean,
        temperature: Float,
        maxTokens: Int?,
        onStream: (String) -> Unit,
        onComplete: (Result<String>) -> Unit
    ) {
        try {
            // Google uses a different endpoint format
            val endpoint = "$GOOGLE_API_URL/${model.id}:generateContent"
            
            val requestMessages = messages.map { message ->
                JSONObject().apply {
                    put("role", when (message.role) {
                        Message.MessageRole.USER -> "user"
                        Message.MessageRole.ASSISTANT -> "model"
                        Message.MessageRole.SYSTEM -> "system"
                    })
                    put("parts", JSONArray().put(JSONObject().apply {
                        put("text", message.content)
                    }))
                }
            }
            
            val requestBody = JSONObject().apply {
                put("contents", JSONArray(requestMessages))
                put("generationConfig", JSONObject().apply {
                    put("temperature", temperature)
                    maxTokens?.let { put("maxOutputTokens", it) }
                })
            }
            
            val request = Request.Builder()
                .url(endpoint + if (stream) "?alt=sse" else "")
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()
            
            if (stream) {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onComplete(Result.failure(e))
                    }
                    
                    override fun onResponse(call: Call, response: Response) {
                        if (!response.isSuccessful) {
                            onComplete(Result.failure(Exception("Google API error: ${response.code} - ${response.message}")))
                            return
                        }
                        
                        try {
                            val body = response.body ?: throw IOException("Empty response body")
                            val source = body.source()
                            val buffer = okio.Buffer()
                            
                            while (source.read(buffer, 8192L) != -1L) {
                                val chunk = buffer.readUtf8()
                                buffer.clear()
                                
                                // Parse SSE chunks
                                chunk.split("\n\n").forEach { line ->
                                    if (line.startsWith("data: ")) {
                                        val data = line.substring(6).trim()
                                        if (data.isNotEmpty() && data != "[DONE]") {
                                            try {
                                                val json = JSONObject(data)
                                                if (json.has("candidates") && json.getJSONArray("candidates").length() > 0) {
                                                    val candidate = json.getJSONArray("candidates").getJSONObject(0)
                                                    if (candidate.has("content") && candidate.getJSONObject("content").has("parts")) {
                                                        val parts = candidate.getJSONObject("content").getJSONArray("parts")
                                                        for (i in 0 until parts.length()) {
                                                            val part = parts.getJSONObject(i)
                                                            if (part.has("text")) {
                                                                onStream(part.getString("text"))
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Failed to parse Google stream chunk", e)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            onComplete(Result.success(""))
                            body.close()
                        } catch (e: Exception) {
                            onComplete(Result.failure(e))
                        }
                    }
                })
            } else {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onComplete(Result.failure(Exception("Google API error: ${response.code} - ${response.message}")))
                    return
                }
                
                val responseBody = response.body?.string() ?: throw IOException("Empty response body")
                val json = JSONObject(responseBody)
                
                if (json.has("candidates") && json.getJSONArray("candidates").length() > 0) {
                    val candidate = json.getJSONArray("candidates").getJSONObject(0)
                    if (candidate.has("content") && candidate.getJSONObject("content").has("parts")) {
                        val parts = candidate.getJSONObject("content").getJSONArray("parts")
                        val sb = StringBuilder()
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            if (part.has("text")) {
                                sb.append(part.getString("text"))
                            }
                        }
                        onComplete(Result.success(sb.toString()))
                    } else {
                        onComplete(Result.failure(Exception("Invalid response format")))
                    }
                } else {
                    onComplete(Result.failure(Exception("Invalid response format")))
                }
            }
        } catch (e: Exception) {
            onComplete(Result.failure(e))
        }
    }
    
    // Check API health
    suspend fun checkApiHealth(provider: String, apiKey: String): Result<Boolean> {
        return try {
            val request = when (provider.lowercase()) {
                "openai" -> Request.Builder()
                    .url("https://api.openai.com/v1/models")
                    .header("Authorization", "Bearer $apiKey")
                    .get()
                    .build()
                "anthropic" -> Request.Builder()
                    .url("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .get()
                    .build()
                "mistral" -> Request.Builder()
                    .url("https://api.mistral.ai/v1/models")
                    .header("Authorization", "Bearer $apiKey")
                    .get()
                    .build()
                "google" -> Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models")
                    .header("Authorization", "Bearer $apiKey")
                    .get()
                    .build()
                else -> throw Exception("Unsupported provider")
            }
            
            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Logging interceptor
    private class LoggingInterceptor : okhttp3.Interceptor {
        override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
            val request = chain.request()
            Log.d(TAG, "Request: ${request.method} ${request.url}")
            
            val response = chain.proceed(request)
            Log.d(TAG, "Response: ${response.code} ${response.message}")
            
            return response
        }
    }
}
