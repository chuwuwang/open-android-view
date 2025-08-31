package com.cat.view.ui.text

import com.cat.view.databinding.ActivityTextInputBoxBinding
import com.cat.view.ui.ViewBindingActivity

class InputBoxActivity : ViewBindingActivity<ActivityTextInputBoxBinding>() {

    override fun init() {
        binding.inputBox.apply { }
        binding.inputBox.setText("3245")
    }

}