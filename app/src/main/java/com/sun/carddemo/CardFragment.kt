package com.sun.carddemo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.sun.carddemo.adapter.CardAdapter
import com.sun.carddemo.base.BaseFragment
import com.sun.carddemo.util.getSelfViewModel
import com.sun.carddemo.util.orDef
import com.sun.carddemo.util.showToast
import com.sun.carddemo.viewmodel.MusicViewModel
import com.sun.carddemo.widget.GalleryItemDecoration
import com.sun.carddemo.widget.OnViewPagerListener
import com.sun.carddemo.widget.ViewPagerLayoutManager
import kotlinx.android.synthetic.main.fragment_card.*

/**
 * Author: sunhaitao
 * Date: 2021/10/14 18:57
 * Description:
 */
class CardFragment:BaseFragment() {

    private var viewModel: MusicViewModel? = null
    private var cardType: String? = null
    private var cardName: String? = null
    private var linearLayoutManager: ViewPagerLayoutManager? = null
    private var cardAdapter: CardAdapter? = null
    var curPlayPos = 0
    private var isVisibleToUser: Boolean = false

    companion object{
        fun newInstance(cardType: String, cardTitle: String):Fragment{
            return CardFragment().apply {
                arguments = Bundle().apply {
                    putString("cardType", cardType)
                    putString("cardName", cardTitle)
                }

            }
        }
    }

    override fun getResourceId(): Int = R.layout.fragment_card

    override fun initView(view: View?) {
        cardType = arguments?.getString("cardType")
        cardName = arguments?.getString("cardName")
        viewModel = getSelfViewModel {
            cardLiveData.observe(this@CardFragment, {
                cardAdapter?.submitList(it, true)
            })
        }
        initRecycleView()
        viewModel?.getCardMusicList(cardType)
    }

    private fun initRecycleView() {
        linearLayoutManager = ViewPagerLayoutManager(activity)
        linearLayoutManager?.recycleChildrenOnDetach = true
        recycleView.layoutManager = linearLayoutManager
        recycleView.addItemDecoration(GalleryItemDecoration())
        recycleView.adapter = CardAdapter(activity).also { cardAdapter = it  }
        linearLayoutManager?.setOnViewPagerListener(object : OnViewPagerListener {
            override fun onInitComplete() {
                val position = linearLayoutManager?.getCurrPosition().orDef()
                if (position >= 0 && position <= cardAdapter?.getList()?.lastIndex ?: 0) {
                    curPlayPos = position
                }
                playCurVoice(curPlayPos)
            }

            override fun onPageRelease(isNext: Boolean, position: Int) {

            }

            override fun onPageSelected(position: Int, isBottom: Boolean) {
                if (curPlayPos == position) return
                playCurVoice(position)
            }
        })
    }
    fun playCurVoice(position: Int) {
        if (!isVisibleToUser) return
        curPlayPos = position
        val songInfo = cardAdapter?.getItem(position) ?: return
        activity?.showToast("当前播放：" + songInfo.songName)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (isVisibleToUser) {
            val position = linearLayoutManager?.getCurrPosition().orDef()
            if (position >= 0 && position <= cardAdapter?.getList()?.lastIndex ?: 0) {
                curPlayPos = position
            }
            playCurVoice(curPlayPos)
        }
    }

    override fun unInitView() {
    }
}