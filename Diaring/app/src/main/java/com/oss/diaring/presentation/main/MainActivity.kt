package com.oss.diaring.presentation.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.oss.diaring.R
import com.oss.diaring.databinding.ActivityMainBinding
import com.oss.diaring.presentation.base.BaseActivity
import com.oss.diaring.presentation.diary.DiaryListData
import com.oss.diaring.presentation.signup.SignUpActivity

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBottomNavigationView()
        setBottomNavigationVisibility()
    }

    private fun initBottomNavigationView() {
        binding.bnvMain.setupWithNavController(findNavController())
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment

        return navHostFragment.navController
    }

    private fun setBottomNavigationVisibility() {
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            binding.bnvMain.visibility = when (destination.id) {
                R.id.navigation_calendar, R.id.navigation_diary_list, R.id.navigation_dashboard, R.id.navigation_mypage -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}