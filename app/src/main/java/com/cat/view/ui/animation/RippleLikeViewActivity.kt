package com.cat.view.ui.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.cat.view.R
import com.cat.view.customize.animation.RippleView
import com.cat.view.databinding.ActivityAnimationRippleLikeBinding
import com.cat.view.ui.ViewBindingActivity

class RippleLikeViewActivity : ViewBindingActivity<ActivityAnimationRippleLikeBinding>() {

    private var mSelected = false

    override fun init() {
        // 点赞与取消赞 like 按钮的 scale 变化 (1.0) -> (0.8) -> (1.0)
        val hx1 = PropertyValuesHolder.ofFloat("scaleX", 0.8f)
        val hy1 = PropertyValuesHolder.ofFloat("scaleY", 0.8f)
        val oa1 = ObjectAnimator.ofPropertyValuesHolder(binding.imgLike, hx1, hy1)
        oa1.interpolator = DecelerateInterpolator()
        oa1.duration = 300
        val likeListener = object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                if (mSelected) {
                    binding.imgLike.setImageResource(R.drawable.ic_messages_like_selected)
                    binding.rippleView.start(null)
                } else {
                    binding.imgLike.setImageResource(R.drawable.ic_messages_like_unselected)
                }
            }

        }
        val hx2 = PropertyValuesHolder.ofFloat("scaleX", 1.0f)
        val hy2 = PropertyValuesHolder.ofFloat("scaleY", 1.0f)
        val oa2 = ObjectAnimator.ofPropertyValuesHolder(binding.imgLike, hx2, hy2)
        oa2.interpolator = OvershootInterpolator()
        oa2.duration = 300
        oa2.addListener(likeListener)

        // shining 图标的 scale 变化 from (0.0) to (1.0)
        val shiningListener = object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                binding.imgLikeShining.alpha = 1.0f
            }

        }
        val hx3 = PropertyValuesHolder.ofFloat("scaleX", 1.0f)
        val hy3 = PropertyValuesHolder.ofFloat("scaleY", 1.0f)
        val oa3 = ObjectAnimator.ofPropertyValuesHolder(binding.imgLikeShining, hx3, hy3)
        oa3.addListener(shiningListener)
        oa2.duration = 300

        val set1 = AnimatorSet()
        set1.play(oa2).with(oa3).after(oa1)

        /**
         * 取消赞的动画 like 图标缩小, shining 图标变小, 同时变透明->like 图标变为灰色, like 图标放大, shining 图标变大, 透明->
         */
        val k1 = Keyframe.ofFloat(0f, 1f)
        val k2 = Keyframe.ofFloat(0.5f, 0.8f)
        val k3 = Keyframe.ofFloat(1f, 1f)
        val hx4 = PropertyValuesHolder.ofKeyframe("scaleX", k1, k2, k3)
        val hy4 = PropertyValuesHolder.ofKeyframe("scaleY", k1, k2, k3)

        val oa5: Animator = ObjectAnimator.ofPropertyValuesHolder(binding.imgLikeShining, hx4, hy4)
        oa5.duration = 300
        val os6 = ObjectAnimator.ofFloat(binding.imgLikeShining, "alpha", 1f, 0f)
        os6.duration = 300
        val set2 = AnimatorSet()
        set2.play(oa1).with(oa5).with(os6).before(oa2)

        binding.viewLike.setOnClickListener {
            mSelected = !mSelected
            if (mSelected) {
                set1.start()
                binding.scrollNumberView.increment()
            } else {
                set2.start()
                binding.scrollNumberView.decrement()
            }
        }

        binding.btnRipple.setOnClickListener { binding.rippleView.start(rippleCallback) }
    }

    private val rippleCallback = object : RippleView.Callback {

        override fun start() {
            binding.imgLikeShining.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .withEndAction {
                    binding.imgLikeShining.scaleX = 0f
                    binding.imgLikeShining.scaleY = 0f
                    binding.imgLikeShining.alpha = 0.0f
                }.setDuration(300).start()
        }

        override fun update(fraction: Float) {
        }

        override fun end() {
        }

    }

}