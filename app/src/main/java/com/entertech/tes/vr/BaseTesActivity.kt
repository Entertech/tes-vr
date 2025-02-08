package com.entertech.tes.vr

import androidx.lifecycle.ViewModelProvider
import cn.entertech.base.BaseActivity
import java.lang.reflect.ParameterizedType

abstract class BaseTesActivity<ViewModel : BaseTesViewModel> : BaseActivity() {

    protected val viewModel: ViewModel by lazy {
        try {
            val viewModelClass =
                (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<ViewModel>
            ViewModelProvider(this)[viewModelClass]
        } catch (e: Exception) {
            throw RuntimeException("ViewModel is not specified.")
        }
    }

    override fun initActivityData() {
        super.initActivityData()
        viewModel.initPermission(this) {
            viewModel.initTesDeviceManager(true, this, intent)
        }
    }
}