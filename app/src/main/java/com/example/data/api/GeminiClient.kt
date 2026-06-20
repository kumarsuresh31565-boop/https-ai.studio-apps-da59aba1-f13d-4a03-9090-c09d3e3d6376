package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class MoshiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class MoshiContent(
    val parts: List<MoshiPart>
)

@JsonClass(generateAdapter = true)
data class MoshiGenerateContentRequest(
    val contents: List<MoshiContent>
)

@JsonClass(generateAdapter = true)
data class MoshiCandidate(
    val content: MoshiContent
)

@JsonClass(generateAdapter = true)
data class MoshiGenerateContentResponse(
    val candidates: List<MoshiCandidate>? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: MoshiGenerateContentRequest
    ): MoshiGenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiService {
    suspend fun generateText(prompt: String): String {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY" || key == "GEMINI_API_KEY") {
            return generateLocalFallback(prompt)
        }

        return try {
            val request = MoshiGenerateContentRequest(
                contents = listOf(
                    MoshiContent(parts = listOf(MoshiPart(text = prompt)))
                )
            )
            val response = RetrofitClient.service.generateContent(key, request)
            val generated = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (generated.isNullOrEmpty()) {
                generateLocalFallback(prompt)
            } else {
                generated.trim()
            }
        } catch (e: Exception) {
            generateLocalFallback(prompt)
        }
    }

    private fun generateLocalFallback(prompt: String): String {
        val cleanPrompt = prompt.lowercase()
        return when {
            cleanPrompt.contains("title") -> {
                listOf(
                    "🔥 Quantum Realms: Demystifying Infinite Dimensions",
                    "🚀 Speedrunning Custom View Layouts in Jetpack Compose",
                    "⚡ Ultimate Cyberpunk Study Beats to 10x Coding Speeds",
                    "🧠 Gemini Inside: Automating UI with Multi-Agent Systems"
                ).random()
            }
            cleanPrompt.contains("description") -> {
                "Witness the next major leap in technology. In this video, we explore core concepts, visual breakdowns, live demonstrations, and outline practical implementations for your modern production projects. Code files, details, and guides are fully documented below. Remember to Subscribe and Join the StreamView community!"
            }
            cleanPrompt.contains("caption") || cleanPrompt.contains("subtitle") -> {
                "Hello everyone! Welcome back to the channel. Today we are unpacking one of the most exciting advancements of the year... Let's dive straight in, make sure your audio is enabled!"
            }
            cleanPrompt.contains("thumbnail") || cleanPrompt.contains("image") -> {
                "A striking composition featuring high-contrast glowing neon typography overlaying a blurred dark futuristic dashboard. A vibrant play button casting dramatic blue and purple ambient light vectors."
            }
            else -> "Superb content auto-generated beautifully. Optimize your channel keywords to boost system recommendation rankings by 25%!"
        }
    }
}
