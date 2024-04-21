package com.display.photos.ui

import android.content.Intent
import android.os.Bundle
import com.display.photos.R
import com.display.photos.databinding.ActivityMainBinding
import com.display.photos.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            launchMainFragment()
        }
    }

    private fun launchMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitAllowingStateLoss()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchMainFragment()
    }
}
