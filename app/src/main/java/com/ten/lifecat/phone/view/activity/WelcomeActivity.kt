package com.ten.lifecat.phone.view.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView

import com.ten.lifecat.phone.view.fragment.GuildFragment
import com.ten.lifecat.phone.R

/**
 * 首次启动欢迎界面
 */
class WelcomeActivity : AppCompatActivity() {

    private val TAG = "lifecat WelcomeActivity"
    /**
     * 翻页视图容器
     */
    private lateinit var vp: ViewPager
    /**
     * 欢迎动画 第一屏
     */
    private lateinit var iv1: ImageView
    /**
     * 欢迎动画 第二屏
     */
    private lateinit var iv2: ImageView
    /**
     * 欢迎动画 第三屏
     */
    private lateinit var iv3: ImageView
    /**
     * 进入按钮 :进入登录注册界面
     */
    private lateinit var btStart: Button

    /**
     * 欢迎动画集合
     */
    private lateinit var fragments: MutableList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 判断是否已经登录过
        val pref = getSharedPreferences("data", MODE_PRIVATE)
        val hasLogin = pref.getBoolean("has_login", false)
        Log.d(TAG, "hasLogin: $hasLogin")

        // 首次登录 -> 继续 WelcomeActivity
        if (!hasLogin) {
            requestWindowFeature(Window.FEATURE_NO_TITLE) // 隐藏标题栏
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) // 隐藏状态栏
            setContentView(R.layout.activity_welcome)
            initView() // 注册组件
            regisAnimation() // 注册动画
            slideAnimation() // 动画滑动效果
        }
        // 已登录 -> 跳转到 MainActivity
        else {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            this.finish()
        }
    }


    /**
     * Register component
     */
    private fun initView() {
        vp = findViewById(R.id.vp)
        iv1 = findViewById(R.id.iv1)
        iv2 = findViewById(R.id.iv2)
        iv3 = findViewById(R.id.iv3)
        btStart = findViewById(R.id.bt_start)

        btStart.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /** Add Fragments to WelcomeActivity */
    private fun regisAnimation() {
        fragments = ArrayList()
        intArrayOf(1, 2, 3).forEach { n ->
            val fragment = GuildFragment()
            val bundle = Bundle()
            bundle.putInt("index", n)
            fragment.arguments = bundle
            fragments.add(fragment)
        }
    }

    /** 设置ViewPager的适配器和滑动监听 */
    private fun slideAnimation() {
        vp.offscreenPageLimit = 3
        vp.adapter = MyPageAdapter(supportFragmentManager)
        vp.addOnPageChangeListener(MyPageChangeListener())
    }

    /** ViewPage adapte */
    private inner class MyPageAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

    /**
     * ViewPager滑动页面监听器
     */
    private inner class MyPageChangeListener : ViewPager.OnPageChangeListener {

        /**
         * @param position 当前位置
         * @param positionOffset 位置偏移
         * @param positionOffsetPixels 位置偏移像素
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        /**
         * 根据页面不同动态改变红点和在最后一页显示立即体验按钮
         */
        override fun onPageSelected(position: Int) {
            btStart.visibility = View.GONE
            iv1.setImageResource(R.mipmap.dot_normal)
            iv2.setImageResource(R.mipmap.dot_normal)
            iv3.setImageResource(R.mipmap.dot_normal)

            when (position) {
                0 -> iv1.setImageResource(R.mipmap.dot_focus)
                1 -> iv2.setImageResource(R.mipmap.dot_focus)
                2 -> {
                    iv3.setImageResource(R.mipmap.dot_focus)
                    btStart.visibility = View.VISIBLE
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}
