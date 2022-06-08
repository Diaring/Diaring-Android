package com.oss.diaring.presentation.mypage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.oss.licenses.OssLicensesActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.oss.diaring.DiaringApplication
import com.oss.diaring.R
import com.oss.diaring.data.sharedpreference.SharedPrefManagerImpl
import com.oss.diaring.databinding.FragmentMypageBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.main.MainActivity
import com.oss.diaring.presentation.signup.SignUpActivity
import com.oss.diaring.util.Constants


class MypageFragment : BaseFragment<FragmentMypageBinding>(R.layout.fragment_mypage) {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences : SharedPrefManagerImpl

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPrefManagerImpl(requireActivity())

        val nickname_tv = binding.tvNickname
        nickname_tv.setText(sharedPreferences.getNickName("user_nickname"))

        val id_tv = binding.tvEmail
        id_tv.setText(sharedPreferences.getEmail("user_email"))

        auth = Firebase.auth

        binding.btnOssbtn.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle("오픈소스 라이센스 목록")
            startActivity(Intent(requireActivity(), OssLicensesMenuActivity::class.java))
        }

        binding.btnLogoutbtn.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(),"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            activity?.finish()
            sharedPreferences.setNickName(Constants.USER_NICKNAME,"")
            sharedPreferences.setUserId(Constants.FIREBASE_UID,"")
            Toast.makeText(requireContext(),"이동 되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }

}