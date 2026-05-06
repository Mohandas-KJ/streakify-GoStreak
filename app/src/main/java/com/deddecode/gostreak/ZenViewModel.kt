package com.deddecode.gostreak

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class ZenViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("zen_prefs", Context.MODE_PRIVATE)

    private val _blocks = mutableStateListOf<FocusBlock>()
    val blocks: List<FocusBlock> = _blocks

    // 5, 10, 15, 20 ... 120
    val maxMinutes = 120

    var sliderPosition = mutableFloatStateOf(5f)

    private val _streak = mutableStateOf(0)
    val streak = _streak

    init {
        checkAndResetIfNewDay()
        loadBlocks()
    }

    fun addFocusBlock() {

        checkAndResetIfNewDay()

        val selectedMinutes = sliderPosition.floatValue.toInt()

        val nextNumber = _blocks.size + 1

        val block = FocusBlock(
            blockNumber = nextNumber,
            durationMinutes = selectedMinutes
        )

        _blocks.add(0, block)

        _streak.value = _blocks.size

        saveBlocks()
    }

    private fun saveBlocks() {

        val jsonArray = JSONArray()

        _blocks.forEach { block ->

            val obj = JSONObject()

            obj.put("id", block.id)
            obj.put("blockNumber", block.blockNumber)
            obj.put("durationMinutes", block.durationMinutes)
            obj.put("timeLabel", block.timeLabel)
            obj.put("timestamp", block.timestamp)

            jsonArray.put(obj)
        }

        prefs.edit()
            .putString("blocks", jsonArray.toString())
            .putInt("streak", _streak.value)
            .apply()
    }

    private fun loadBlocks() {

        val saved = prefs.getString("blocks", null) ?: return

        val jsonArray = JSONArray(saved)

        for (i in 0 until jsonArray.length()) {

            val obj = jsonArray.getJSONObject(i)

            _blocks.add(
                FocusBlock(
                    id = obj.getString("id"),
                    blockNumber = obj.getInt("blockNumber"),
                    durationMinutes = obj.getInt("durationMinutes"),
                    timeLabel = obj.getString("timeLabel"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }

        _streak.value = prefs.getInt("streak", _blocks.size)
    }

    private fun checkAndResetIfNewDay() {

        val currentCalendar = Calendar.getInstance()

        val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        val savedDay = prefs.getInt("saved_day", -1)
        val savedYear = prefs.getInt("saved_year", -1)

        if (savedDay == -1) {

            prefs.edit()
                .putInt("saved_day", currentDay)
                .putInt("saved_year", currentYear)
                .apply()

            return
        }

        if (savedDay != currentDay || savedYear != currentYear) {

            _blocks.clear()
            _streak.value = 0

            prefs.edit()
                .remove("blocks")
                .putInt("streak", 0)
                .putInt("saved_day", currentDay)
                .putInt("saved_year", currentYear)
                .apply()
        }
    }
}