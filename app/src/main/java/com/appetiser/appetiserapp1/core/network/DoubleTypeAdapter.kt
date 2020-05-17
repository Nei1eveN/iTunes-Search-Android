package com.appetiser.appetiserapp1.core.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

class DoubleTypeAdapter : TypeAdapter<Double?>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Double? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val stringValue = reader.nextString()
        return try {
            java.lang.Double.valueOf(stringValue)
        } catch (e: NumberFormatException) {
            null
        }
    }

    @Throws(IOException::class)
    override fun write(
        writer: JsonWriter,
        value: Double?
    ) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.value(value)
    }
}