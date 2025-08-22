package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextMainBinding
import com.cat.view.openActivity
import com.cat.view.ui.ViewBindingActivity

class TextActivity : ViewBindingActivity<ActivityTextMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnScrollNumber.setOnClickListener { openActivity<ScrollNumberViewActivity>() }
    }

}