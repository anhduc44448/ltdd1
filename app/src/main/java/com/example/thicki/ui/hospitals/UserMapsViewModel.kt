package com.example.thicki.ui.hospitals

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.thicki.data.model.Place
import com.example.thicki.data.model.UserMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserMapsViewModel : ViewModel() {
    var userMaps by mutableStateOf<List<UserMap>>(emptyList())
    private val gson = Gson()
    private val PREFS_NAME = "user_maps_prefs"
    private val KEY_MAPS = "user_maps_key"

    fun loadMaps(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MAPS, null)
        if (json != null) {
            val type = object : TypeToken<List<UserMap>>() {}.type
            userMaps = gson.fromJson(json, type)
        } else {
            // Dữ liệu mẫu ban đầu
            userMaps = listOf(
                UserMap("Bệnh viện Đà Nẵng", listOf(
                    Place("Bệnh viện Đa khoa", "124 Hải Phòng", 16.0544, 108.2022)
                )),
                UserMap("Phòng khám tư", listOf(
                    Place("Nhi khoa", "Thanh Khê", 16.0674, 108.2132)
                ))
            )
            saveMaps(context)
        }
    }

    fun saveMaps(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(userMaps)
        prefs.edit().putString(KEY_MAPS, json).apply()
    }

    fun addMap(title: String, context: Context) {
        userMaps = userMaps + UserMap(title, emptyList())
        saveMaps(context)
    }

    fun addPlaceToMap(mapTitle: String, place: Place, context: Context) {
        userMaps = userMaps.map { map ->
            if (map.title == mapTitle) {
                map.copy(places = map.places + place)
            } else {
                map
            }
        }
        saveMaps(context)
    }
}
