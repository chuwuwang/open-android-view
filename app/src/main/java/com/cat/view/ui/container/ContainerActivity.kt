package com.cat.view.ui.container

import com.cat.view.databinding.ActivityContainerMainBinding
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.util.openActivity

class ContainerActivity : ViewBindingActivity<ActivityContainerMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnMarqueeLight.setOnClickListener { openActivity<MarqueeLayoutActivity>() }
        binding.btnFlipStereo.setOnClickListener { openActivity<FlipStereoLayoutActivity>() }
    }

}