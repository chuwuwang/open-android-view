package common.ui.animation

import com.cat.view.databinding.ActivityAnimationBinding
import com.cat.view.openActivity
import com.cat.view.ui.ViewBindingActivity
import com.cat.view.ui.container.MarqueeLayoutActivity
import com.cat.view.ui.container.FlipStereoLayoutActivity

class AnimationActivity : ViewBindingActivity<ActivityAnimationBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        binding.spin3dBtn.setOnClickListener { openActivity<FlipStereoLayoutActivity>() }
        binding.marqueeBtn.setOnClickListener { openActivity<MarqueeLayoutActivity>() }
        binding.qqDragEffectBtn.setOnClickListener { openActivity<QQDragEffectActivity>() }
        binding.sphere3dBtn.setOnClickListener { openActivity<Spherical3DMotionActivity>() }
        binding.graphicsVerifyBtn.setOnClickListener { openActivity<GraphicsVerifyActivity>() }
    }

}