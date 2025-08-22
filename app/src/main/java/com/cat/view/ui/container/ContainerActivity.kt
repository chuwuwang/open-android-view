package com.cat.view.ui.container

import com.cat.view.databinding.ActivityContainerMainBinding
import com.cat.view.openActivity
import com.cat.view.ui.ViewBindingActivity

class ContainerActivity : ViewBindingActivity<ActivityContainerMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnMarqueeLight.setOnClickListener { openActivity<MarqueeLayoutActivity>() }
    }

}