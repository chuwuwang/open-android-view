package com.cat.view.ui.progressbar

import com.cat.view.databinding.ActivityProgressbarInfiniteScrollBinding
import com.cat.view.ui.ViewBindingActivity

class InfiniteScrollProgressBarActivity : ViewBindingActivity<ActivityProgressbarInfiniteScrollBinding>() {

    override fun init() {
        binding.infiniteScrollProgressbar.progress = 0.3f
    }

}