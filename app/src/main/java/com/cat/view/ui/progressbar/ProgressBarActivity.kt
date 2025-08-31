package com.cat.view.ui.progressbar

import com.cat.view.databinding.ActivityProgressbarMainBinding
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.utils.openActivity

class ProgressBarActivity : ViewBindingActivity<ActivityProgressbarMainBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.btnCometProgressbar.setOnClickListener { openActivity<CometProgressBarActivity>() }
        binding.btnInfiniteScrollProgressbar.setOnClickListener { openActivity<InfiniteScrollProgressBarActivity>() }
    }

}