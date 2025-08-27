package com.cat.view.ui.progressbar

import com.cat.view.customize.progressbar.CometProgressBar
import com.cat.view.databinding.ActivityProgressbarCometBinding
import com.cat.view.ui.ViewBindingActivity

class CometProgressBarActivity : ViewBindingActivity<ActivityProgressbarCometBinding>() {

    private var rotate: Boolean = false

    override fun init() {
        initView()
    }

    private fun initView() {
        val listener = object : CometProgressBar.OnUpdateListener {

            override fun update(value: Float) {
                binding.cometView.start(value)
            }

            override fun start() {
            }

            override fun end() {
                binding.cometView.end()
            }

        }
        binding.progressBar.setUpdateListener(listener)
        binding.btnStart.setOnClickListener {
            rotate = ! rotate
            if (rotate) {
                binding.progressBar.start()
            } else {
                binding.progressBar.end()
            }
        }
    }

}