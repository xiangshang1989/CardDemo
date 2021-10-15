package com.sun.carddemo.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcssloop.widget.RCImageView
import com.sun.carddemo.R
import com.sun.carddemo.SongInfo
import com.sun.carddemo.util.loadImage

/**
 * Author: sunhaitao
 * Date: 2021/10/15 11:10
 * Description:
 */
class CardAdapter(private val context: Activity?):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = mutableListOf<SongInfo>()

    fun submitList(list: MutableList<SongInfo>, isRefresh: Boolean) {
        if (isRefresh) {
            this.list.clear()
        }
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    fun getList() = list

    fun getItem(position: Int): SongInfo? = list.getOrNull(position)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val info = list.getOrNull(position)
        val cardHolder = holder as CardHolder
        cardHolder.cover.loadImage(info?.songCover)
        val head="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F50a5e3fa7feb094a0f9cf644d9231e1b3fa2cfb3133bb-XF2bdj_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1636860580&t=171710121aa2cb02243b06833491c7ce"
        cardHolder.headerImg.loadImage(head)
        cardHolder.songName.text = info?.songName
        cardHolder.singer.text = info?.artist
    }

    class CardHolder(holder: View) : RecyclerView.ViewHolder(holder) {
        val cover: RCImageView = holder.findViewById(R.id.cover)
        val headerImg: RCImageView = holder.findViewById(R.id.headerImg)
        val songName: TextView = holder.findViewById(R.id.songName)
        val singer: TextView = holder.findViewById(R.id.singer)

    }

}