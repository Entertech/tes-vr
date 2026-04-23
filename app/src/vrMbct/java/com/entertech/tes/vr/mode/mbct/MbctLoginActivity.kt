package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class MbctLoginActivity : AppCompatActivity() {

    private var etAccount: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnGoRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MbctUserStore.isLoggedIn(this)) {
            openHome()
            return
        }
        setContentView(R.layout.activity_mbct_login)
        etAccount = findViewById(R.id.etAccount)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.btnGoRegister)

        btnLogin?.setOnClickListener {
            val account = etAccount?.text?.toString()?.trim().orEmpty()
            val password = etPassword?.text?.toString().orEmpty()
            when {
                account.isEmpty() || password.isEmpty() -> {
                    toast("请输入账号和密码")
                }

                !MbctUserStore.isRegistered(this) -> {
                    toast("当前尚未注册账号，请先完成注册")
                }

                MbctUserStore.login(this, account, password) -> {
                    toast("登录成功")
                    openHome()
                }

                else -> {
                    toast("账号或密码错误，请重新输入")
                }
            }
        }

        btnGoRegister?.setOnClickListener {
            startActivity(Intent(this, MbctRegisterActivity::class.java))
        }
    }

    private fun openHome() {
        startActivity(Intent(this, MbctHomeActivity::class.java))
        finish()
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
