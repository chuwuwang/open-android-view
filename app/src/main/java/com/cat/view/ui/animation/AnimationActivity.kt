package com.cat.view.ui.animation

import com.cat.view.databinding.ActivityAnimationMainBinding
import com.cat.view.openActivity
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.ui.image.GraphicsVerifyViewActivity
import common.ui.animation.QQDragEffectActivity

class AnimationActivity : ViewBindingActivity<ActivityAnimationMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.qqDragEffectBtn.setOnClickListener { openActivity<QQDragEffectActivity>() }
        binding.btnStereoBallSport.setOnClickListener { openActivity<StereoBallSportViewActivity>() }
        binding.graphicsVerifyBtn.setOnClickListener { openActivity<GraphicsVerifyViewActivity>() }
    }

}