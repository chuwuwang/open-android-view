package com.cat.view.ui.animation

import com.cat.view.databinding.ActivityAnimationMainBinding
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.util.openActivity

class AnimationActivity : ViewBindingActivity<ActivityAnimationMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnDragRestored.setOnClickListener { openActivity<DragRestoredViewActivity>() }
        binding.btnStereoBallSport.setOnClickListener { openActivity<StereoBallSportViewActivity>() }
    }

}