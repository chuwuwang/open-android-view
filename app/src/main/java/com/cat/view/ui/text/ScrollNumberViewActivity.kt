package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextScrollNumberViewBinding
import com.cat.view.ui.ViewBindingActivity

class ScrollNumberViewActivity : ViewBindingActivity<ActivityTextScrollNumberViewBinding>() {

    override fun init() {
        binding.btnAdd.setOnClickListener { binding.textScrollNumber.increment() }
        binding.btnDelete.setOnClickListener { binding.textScrollNumber.decrement() }
    }

}