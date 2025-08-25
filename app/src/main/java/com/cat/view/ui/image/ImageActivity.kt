package com.cat.view.ui.image

import com.cat.view.databinding.ActivityImageMainBinding
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.util.openActivity

class ImageActivity : ViewBindingActivity<ActivityImageMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnGraphicsVerify.setOnClickListener { openActivity<GraphicsVerifyViewActivity>() }
    }

}