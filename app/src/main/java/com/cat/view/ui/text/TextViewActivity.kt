package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextMainBinding
import com.cat.view.openActivity
import common.ui.ViewBindingActivity

class TextViewActivity : ViewBindingActivity<ActivityTextMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnScrollNumber.setOnClickListener { openActivity<ScrollNumberViewActivity>() }
    }

}