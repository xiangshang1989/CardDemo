package com.sun.carddemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.carddemo.SongInfo
import com.sun.carddemo.TestApplication
import com.sun.carddemo.util.forEach
import com.sun.carddemo.util.getArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedInputStream

/**
 * Author: sunhaitao
 * Date: 2021/10/15 11:47
 * Description:
 */
class MusicViewModel :ViewModel() {
    companion object {
        val baseUrl = "http://espoir.aifield.biz/"
    }

    var cardLiveData = MutableLiveData<MutableList<SongInfo>>()
    fun getCardMusicList(type: String?) {
        if (type.isNullOrEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            var json: String?
            val asset = TestApplication.context?.assets
            asset?.open("card.json").use { it ->
                BufferedInputStream(it).use {
                    json = it.reader().readText()
                }
            }
            if (json.isNullOrEmpty()) {
                cardLiveData.postValue(mutableListOf())
            }else{
                val arrayJson = JSONObject(json).getJSONObject(type)
                    .getJSONArray("songlist")
                val list = mutableListOf<SongInfo>()
                arrayJson.forEach<JSONObject> {
                    val info = SongInfo()
                    info.songName = it?.getString("songname").orEmpty()
                    info.songId = it?.getString("songmid").orEmpty()
                    info.artist = it?.getArray("singer")?.getJSONObject(0)?.getString("name").orEmpty()
                    val albumid = it?.getString("albummid").orEmpty()
                    info.songCover = "https://y.gtimg.cn/music/photo_new/T002R300x300M000${albumid}.jpg"
                    list.add(info)
                }
                cardLiveData.postValue(list)
            }


        }
    }
}