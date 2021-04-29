package com.ten.lifecat.phone.view.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton

import com.ten.lifecat.phone.R
import org.jetbrains.anko.startActivity

/**
 * 主背景界面
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "lifecat MainActivity"
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var uploadIntentButton: Button
    private lateinit var webIntentButton: Button
    private lateinit var photoIntentButton: Button
    private lateinit var userIntentButton: ImageButton
    private lateinit var navigationView: NavigationView
    private var actionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        initView()

        // navigation
        initNavigation()

    }

    private fun initNavigation() {
        navigationView = findViewById<NavigationView>(R.id.nav_view)
        actionBar = supportActionBar
        actionBar.let {
            actionBar?.setDisplayHomeAsUpEnabled(true)
            actionBar?.setHomeAsUpIndicator(R.mipmap.navigation)
        }

        // 默认激活按钮
        navigationView.setCheckedItem(R.id.nav_profile)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> startActivity<UserProfileActivity>()
                R.id.nav_web -> startActivity<WebActivity>()
                R.id.nav_photo -> startActivity<PhotoActivity>()
                R.id.nav_upload -> startActivity<UploadActivity>()
                R.id.nav_settings -> startActivity<SettingsActivity>()
            }
            true
        }
    }

    private fun initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
        uploadIntentButton = findViewById(R.id.background_button_upload)
        webIntentButton = findViewById(R.id.background_button_web)
        photoIntentButton = findViewById(R.id.background_button_photo)
        userIntentButton = findViewById(R.id.background_button)
        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.background_toolbar)
        setSupportActionBar(toolbar)

        uploadIntentButton.setOnClickListener {
            startActivity<UploadActivity>()
        }
        webIntentButton.setOnClickListener {
            startActivity<WebActivity>()
        }
        photoIntentButton.setOnClickListener {
            startActivity<PhotoActivity>()
        }
        userIntentButton.setOnClickListener {
            startActivity<UserProfileActivity>()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}
