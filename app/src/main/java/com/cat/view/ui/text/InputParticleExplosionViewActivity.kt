package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextInputParticleExplosionBinding
import com.cat.view.ui.ViewBindingActivity

class InputParticleExplosionViewActivity : ViewBindingActivity<ActivityTextInputParticleExplosionBinding>() {

    override fun init() {
        binding.inputParticleExplosionView.bind(binding.editText)
    }

}