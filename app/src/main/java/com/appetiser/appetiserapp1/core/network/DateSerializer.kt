package com.appetiser.appetiserapp1.core.network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateSerializer : JsonSerializer<Date>, JsonDeserializer<Date> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.getDefault())
        return try {
            formatter.parse(json?.asString)
        } catch (e: ParseException) {
            Date()
        }
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return JsonPrimitive(formatter.format(src))
    }
}