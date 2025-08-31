package com.cat.view.ui.animation

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.cat.view.databinding.ActivityAnimationCircleStickIndicatorBinding
import com.cat.view.ui.ViewBindingActivity

class CircleStickIndicatorActivity : ViewBindingActivity<ActivityAnimationCircleStickIndicatorBinding>() {

    private val pageAdapter = MyPageAdapter()

    override fun init() {
        initView()
    }

    private fun initView() {
        itemList.add(Color.RED)
        itemList.add(Color.BLUE)
        itemList.add(Color.CYAN)
        itemList.add(Color.GREEN)
        itemList.add(Color.BLACK)
        itemList.add(Color.YELLOW)
        itemList.add(Color.LTGRAY)
        itemList.add(Color.MAGENTA)

        val listener = object : OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                binding.indicator.setCurrentIndex(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        }
        binding.indicator.setCount(itemList.size)
        binding.viewPager.adapter = pageAdapter
        binding.viewPager.addOnPageChangeListener(listener)
    }

    var itemList: ArrayList<Int> = ArrayList()

    private inner class MyPageAdapter() : PagerAdapter() {

        override fun getCount(): Int {
            return itemList.size
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view === any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = generateItem(position, itemList[position])
            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

    }

    private fun generateItem(pos: Int, color: Int): View {
        val itemView = View(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        itemView.layoutParams = params
        itemView.setBackgroundColor(color)
        itemView.setOnClickListener {
            // itemList.remove(pos)
            // binding.indicator.setCount(itemList.size)
        }
        return itemView
    }

}