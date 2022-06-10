package com.oss.diaring.presentation.signup

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.oss.diaring.R
import com.oss.diaring.databinding.ActivityMainBinding
import com.oss.diaring.presentation.base.BaseActivity
import com.oss.diaring.databinding.ActivitySignUpBinding
import com.oss.diaring.presentation.login.LoginActivity
import com.oss.diaring.presentation.main.MainActivity
import timber.log.Timber

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up){

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth

        super.onCreate(savedInstanceState)

        val signupBtnClicked = findViewById<Button>(R.id.btn_signupbtn)
        signupBtnClicked.setOnClickListener {

            val email = findViewById<EditText>(R.id.et_idarea)
            val pwd = findViewById<EditText>(R.id.et_pwdarea)
            val name = findViewById<EditText>(R.id.et_namearea)

            if (pwd.text.toString().length < 6 || name.text.toString() == ""){
                Toast.makeText(this,"비밀번호 6자 이상, 닉네임 필요",Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(
                    email.text.toString(),
                    pwd.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"회원가입 성공", Toast.LENGTH_SHORT).show()
                            //로그인 액티비티로 이동
                            val intent = Intent(this,LoginActivity::class.java)
                            //realtime에 저장
                            val datamodel = DataModel(email.text.toString(),name.text.toString())
                            val database = Firebase.database
                            val myRef = database.getReference().child("users").child(Firebase.auth.currentUser!!.uid)

                            myRef.setValue(datamodel)

                            //intent putExtra
                            intent.putExtra("user_email",email.text.toString())
                            intent.putExtra("user_nickname",name.text.toString())

                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this,"회원가입 실패, 아이디 중복", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }
    }
}