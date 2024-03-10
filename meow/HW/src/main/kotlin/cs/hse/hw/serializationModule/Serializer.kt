package cs.hse.hw.serializationModule

import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class Serializer(val pathToFile: String) {
    val modifiedJson = Json { prettyPrint = true }

    inline fun <reified T> serializeListToFile(list: List<T>, path: String) {
        try {
            val jsonString = modifiedJson.encodeToString(list)
            File(path).writeText(jsonString)
        } catch (e: Exception) {
            throw SerializationException("Serialization... FAIL\n"
                    + "ERROR: ${e.message}")
        }
    }

    inline fun <reified T> serializeListToFile(list: List<T>) {
        return serializeListToFile(list, pathToFile)
    }

    inline fun <reified T> deserializeListFromFile(path: String): List<T> {
        try {
            val jsonString = File(path).readText()
            return modifiedJson.decodeFromString(jsonString)
        } catch (e: Exception) {
            println("Deserialization... FAIL\n" +
                    "ERROR: ${e.message}")
        }
        return emptyList()
    }

    inline fun <reified T> deserializeListFromFile(): List<T> {
        return deserializeListFromFile(pathToFile)
    }

}