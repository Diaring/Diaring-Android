package com.oss.diaring.presentation.signup

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.oss.diaring.R
import com.oss.diaring.presentation.base.BaseActivity
import com.oss.diaring.databinding.ActivitySignUpBinding

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up){

    private lateinit var auth: FirebaseAuth

    //private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize Firebase Auth
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        Toast.makeText(this,auth.currentUser?.uid.toString(), Toast.LENGTH_SHORT).show()

        //binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val signupBtnClicked = findViewById<Button>(R.id.signupBtn)
        signupBtnClicked.setOnClickListener{

            // 첫번째 방법
            val email = findViewById<EditText>(R.id.idArea)
            val pwd = findViewById<EditText>(R.id.pwdArea)
            val name = findViewById<EditText>(R.id.nameArea)

            // 두번째 방법
            //val email = binding.emailArea
            //val pwd = binding.pwdArea

            auth.createUserWithEmailAndPassword(
                email.text.toString(),
                pwd.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"ok", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,"no", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}