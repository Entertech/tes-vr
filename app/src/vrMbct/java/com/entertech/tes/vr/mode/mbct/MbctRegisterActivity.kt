package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class MbctRegisterActivity : AppCompatActivity() {

    private var etFullName: EditText? = null
    private var etUsername: EditText? = null
    private var etPhone: EditText? = null
    private var etOrganization: EditText? = null
    private var etPassword: EditText? = null
    private var etConfirmPassword: EditText? = null
    private var btnRegister: Button? = null
    private var btnBackToLogin: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_register)
        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etPhone = findViewById(R.id.etPhone)
        etOrganization = findViewById(R.id.etOrganization)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)

        btnRegister?.setOnClickListener {
            val fullName = etFullName?.text?.toString()?.trim().orEmpty()
            val username = etUsername?.text?.toString()?.trim().orEmpty()
            val phone = etPhone?.text?.toString()?.trim().orEmpty()
            val organization = etOrganization?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString().orEmpty()
            val confirmPassword = etConfirmPassword?.text?.toString().orEmpty()

            when {
                fullName.isEmpty() || username.isEmpty() || phone.isEmpty() || organization.isEmpty() -> {
                    toast("请完整填写注册信息")
                }

                password.length < 6 -> {
                    toast("密码长度至少为 6 位")
                }

                password != confirmPassword -> {
                    toast("两次输入的密码不一致")
                }

                else -> {
                    MbctUserStore.register(
                        context = this,
                        fullName = fullName,
                        username = username,
                        phone = phone,
                        organization = organization,
                        password = password
                    )
                    toast("注册成功，已进入主界面")
                    startActivity(Intent(this, MbctHomeActivity::class.java))
                    finishAffinity()
                }
            }
        }

        btnBackToLogin?.setOnClickListener {
            finish()
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
