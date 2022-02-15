package common.ui.animation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cat.view.databinding.ActivityTouchGraphicsVerifyBinding

class GraphicsVerifyActivity : AppCompatActivity() {

    private val binding: ActivityTouchGraphicsVerifyBinding by lazy { ActivityTouchGraphicsVerifyBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle ? ) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }

}