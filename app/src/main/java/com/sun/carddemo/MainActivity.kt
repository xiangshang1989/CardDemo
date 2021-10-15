package com.sun.carddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sun.carddemo.adapter.CardCategoryAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var categoryList =  mutableListOf<CardCategory>()
    private var adapter: CardCategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        categoryList.add(CardCategory("card1", "推荐"))
        categoryList.add(CardCategory("card2", "流行电音"))
        categoryList.add(CardCategory("card3", "情感陪伴"))
        categoryList.add(CardCategory("card4", "动感地带"))
        categoryList.add(CardCategory("card5", "劲歌金曲"))

        adapter = CardCategoryAdapter(supportFragmentManager,categoryList)
        viewpager.removeAllViews()
        viewpager.removeAllViewsInLayout()
        viewpager.adapter = adapter
        tabLayout.setViewPager(viewpager)

    }
}