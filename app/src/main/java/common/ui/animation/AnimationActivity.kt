package common.ui.animation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cat.view.databinding.ActivityAnimationBinding
import com.cat.view.openActivity

class AnimationActivity : AppCompatActivity() {

    private val binding: ActivityAnimationBinding by lazy { ActivityAnimationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle ? ) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.mb3dSpin.setOnClickListener { openActivity<Spin3DActivity>() }
        binding.mbGraphicsVerify.setOnClickListener { openActivity<GraphicsVerifyActivity>() }
    }

}