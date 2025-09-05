package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextMainBinding
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.utils.openActivity

class TextActivity : ViewBindingActivity<ActivityTextMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnInputBox.setOnClickListener { openActivity<InputBoxActivity>() }
        binding.btnScrollNumber.setOnClickListener { openActivity<ScrollNumberViewActivity>() }
        binding.btnGradientScroll.setOnClickListener { openActivity<GradientTextViewActivity>() }
        binding.btnInputParticleExplosion.setOnClickListener { openActivity<InputParticleExplosionViewActivity>() }
    }

}