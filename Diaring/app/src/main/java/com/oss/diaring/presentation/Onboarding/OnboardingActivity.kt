package com.oss.diaring.presentation.Onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oss.diaring.R
import com.oss.diaring.databinding.ActivityOnboardingBinding
import com.oss.diaring.presentation.base.BaseActivity
import com.oss.diaring.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnStart.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val list = listOf(OnboardingFragment1(), OnboardingFragment2(), OnboardingFragment3(), OnboardingFragment4(), OnboardingFragment5())

        val pagerAdapter = FragmentPagerAdapter(list, this)

        binding.viewPager2.adapter = pagerAdapter
    }

}

class FragmentPagerAdapter(val fragmentList:List<Fragment>, fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity){
    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList.get(position)

}