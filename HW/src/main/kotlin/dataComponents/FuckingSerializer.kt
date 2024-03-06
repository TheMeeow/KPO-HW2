package dataComponents

import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class FuckingSerializer(var pathToFile : String) {
    val modifiedJson = Json { prettyPrint = true }

    inline fun <reified T> serializeListToFile(list: List<T>, fileName: String) {
        try {
            val jsonString = modifiedJson.encodeToString(list)
            File(fileName).writeText(jsonString)
        } catch (e: Exception) {
            println("Error occurred during serialization: ${e.message}")
        }
    }

    inline fun <reified T> deserializeListFromFile(fileName: String): List<T> {
        try {
            val jsonString = File(fileName).readText()
            return modifiedJson.decodeFromString(jsonString)
        } catch (e: Exception) {
            println("Error occurred during deserialization: ${e.message}")
        }
        return emptyList()
    }
}