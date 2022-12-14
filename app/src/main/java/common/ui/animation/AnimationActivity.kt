package common.ui.animation

import com.cat.view.databinding.ActivityAnimationBinding
import com.cat.view.openActivity
import common.ui.ViewBindingActivity

class AnimationActivity : ViewBindingActivity<ActivityAnimationBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.spin3dBtn.setOnClickListener { openActivity<Spin3DActivity>() }
        binding.marqueeBtn.setOnClickListener { openActivity<MarqueeActivity>() }
        binding.qqDragEffectBtn.setOnClickListener { openActivity<QQDragEffectActivity>() }
        binding.graphicsVerifyBtn.setOnClickListener { openActivity<GraphicsVerifyActivity>() }
    }

}