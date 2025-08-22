package common.ui.animation

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cat.view.R
import com.cat.view.animation.Spin3DLayout
import com.cat.view.databinding.ActivityAnimation3dSpinBinding
import com.cat.view.ui.ViewBindingActivity

class Spin3DActivity : ViewBindingActivity<ActivityAnimation3dSpinBinding>() {

    override fun init() {
        initView()
    }

    private fun initView() {
        val pagerAdapter = PagerAdapter()
        binding.viewPager2.adapter = pagerAdapter
        val transformer = Spin3DLayout.Spin3DTransformer()
        binding.viewPager2.setPageTransformer(transformer)
    }

    inner class PagerAdapter : RecyclerView.Adapter<PagerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerAdapter.ViewHolder {
            val view = View.inflate(parent.context, R.layout.item_animation_3d_spin_vp, null)
            view.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = 4

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: PagerAdapter.ViewHolder, position: Int) {
            when (position) {
                0 -> {
                    holder.pic.setImageResource(R.drawable.achievement_1)
                    holder.content.text = "最近刷B站看到一个比较有意思的图片切换效果，在查看一个用户发的图片的时候是平滑过渡，如果下一张图片是另一个用户发的，则会触发一个3D翻转的效果，不止是图片翻转，连带里面的布局也会一起翻转。"
                }
                1 -> {
                    holder.pic.setImageResource(R.drawable.achievement_2)
                    holder.content.text = "真就这么简单，先看第一个左右平移，这是一个很普通的ViewPage可以做到的效果，直接使用ViewPage就可以了，下面是使用ViewPage的代码"
                }
                2 -> {
                    holder.pic.setImageResource(R.drawable.achievement_3)
                    holder.content.text = "这些设置都要放在 super.dispatchDraw(canvas)之前，因为在super.dispatchDraw(canvas)调用后表示界面已经开始绘制的，所以要在绘制之前给它设置成我们需要变换的样子"
                }
                3 -> {
                    holder.pic.setImageResource(R.drawable.achievement_4)
                    holder.content.text = "再判断当前View是当前页，还是下一页，如果是当前页那么是相对布局的右边倾斜，如果是当前页的下一页那么应该相对布局的左边倾斜。"
                }
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val pic: ImageView = itemView.findViewById(R.id.pic_image)
            val content: TextView = itemView.findViewById(R.id.content_text)
        }

    }

}