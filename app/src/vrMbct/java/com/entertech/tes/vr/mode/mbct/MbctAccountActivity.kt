package com.entertech.tes.vr.mode.mbct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.entertech.tes.vr.R

class MbctAccountActivity : AppCompatActivity() {

    private var tvAccountName: TextView? = null
    private var tvAccountUsername: TextView? = null
    private var tvAccountPhone: TextView? = null
    private var tvAccountOrganization: TextView? = null
    private var tvAccountStatus: TextView? = null
    private var btnLogout: Button? = null
    private var btnReRegister: Button? = null
    private var btnBackHome: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mbct_account)
        tvAccountName = findViewById(R.id.tvAccountName)
        tvAccountUsername = findViewById(R.id.tvAccountUsername)
        tvAccountPhone = findViewById(R.id.tvAccountPhone)
        tvAccountOrganization = findViewById(R.id.tvAccountOrganization)
        tvAccountStatus = findViewById(R.id.tvAccountStatus)
        btnLogout = findViewById(R.id.btnLogout)
        btnReRegister = findViewById(R.id.btnReRegister)
        btnBackHome = findViewById(R.id.btnBackHome)

        btnLogout?.setOnClickListener {
            MbctUserStore.logout(this)
            startActivity(Intent(this, MbctLoginActivity::class.java))
            finishAffinity()
        }
        btnReRegister?.setOnClickListener {
            startActivity(Intent(this, MbctRegisterActivity::class.java))
        }
        btnBackHome?.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        renderAccount()
    }

    private fun renderAccount() {
        val user = MbctUserStore.getUser(this)
        tvAccountName?.text = "姓名：${user?.fullName ?: "未注册"}"
        tvAccountUsername?.text = "账号：${user?.username ?: "未注册"}"
        tvAccountPhone?.text = "手机号：${user?.phone ?: "未注册"}"
        tvAccountOrganization?.text = "机构：${user?.organization ?: "未注册"}"
        tvAccountStatus?.text = if (MbctUserStore.isLoggedIn(this)) {
            "登录状态：已登录，可直接从主界面进入连接与训练流程。"
        } else {
            "登录状态：未登录，请先登录后再开始训练。"
        }
    }
}
