package com.ipromise.utils

import com.google.gson.JsonObject

class JSONBuilder {
    var json = JsonObject()

    fun append(key: String, value: String) = apply { this.json.addProperty(key, value) }

    fun build(): JsonObject {
        return json
    }
}