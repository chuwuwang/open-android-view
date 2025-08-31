package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextGradientBinding
import com.cat.view.ui.ViewBindingActivity

class GradientTextViewActivity : ViewBindingActivity<ActivityTextGradientBinding>() {

    override fun init() {
        binding.gradientText.slidingPercent = 0.3f
    }

}