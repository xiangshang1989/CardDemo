package com.sun.carddemo.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sun.carddemo.CardCategory
import com.sun.carddemo.CardFragment

/**
 * Author: sunhaitao
 * Date: 2021/10/14 17:32
 * Description:
 */
class CardCategoryAdapter(fm:FragmentManager, private var categoryList: MutableList<CardCategory>) :FragmentStatePagerAdapter(fm) {
    private val fragmentMap = hashMapOf<String,Fragment>()

    override fun getCount(): Int = categoryList.size

    override fun getItem(position: Int): Fragment {
        val category = categoryList[position]
        if(fragmentMap[category.cardType]!=null){
            return fragmentMap[category.cardType]!!
        }
        val fragment = CardFragment.newInstance(category.cardType, category.cardTitle)
        fragmentMap[category.cardType] = fragment
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return categoryList[position].cardTitle
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        val category = categoryList[position]
        if (fragmentMap[category.cardType] != null) {
            fragmentMap.remove(category.cardType)
        }
    }
    /**
     * 每次viewPager滑动后都会被调用 而 object参数就是显示的Fragment
     * 有一个缺陷 setPrimaryItem()是在 viewPager的滑动监听执行完后才会调用的；
     * 所以在 换的个滑动监听中获取当前显示的Fragment 是不对的
     */
    var currFragment: CardFragment? = null
    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if ( `object` is CardFragment) {
            currFragment =  `object`
        }
        super.setPrimaryItem(container, position, `object`)
    }
}