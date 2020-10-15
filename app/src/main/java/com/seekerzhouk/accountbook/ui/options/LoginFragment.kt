package com.seekerzhouk.accountbook.ui.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.viewmodel.LoginViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val TAG = "SignUpFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false).also {
//            it.isFocusableInTouchMode = true
//            it.requestFocus()
//            it.setOnKeyListener(object : View.OnKeyListener {
//                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                    if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                        this@LoginFragment.findNavController()
//                            .navigate(R.id.action_loginFragment_to_navigation_me)
//                        return true
//                    }
//                    return false
//                }
//            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.login)
        buttonLogin.setOnClickListener {
            if (loginUserName.text.isEmpty()) {
                Toast.makeText(context, R.string.user_name_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (loginPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.password_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                login(loginUserName.text.trim().toString(), loginPassword.text.trim().toString())
            }

            loginProgressBar.show(getString(R.string.is_logging))

        }

        textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun login(user: String, password: String) {
        AVUser.logIn(user, password).subscribe(object : Observer<AVUser> {
            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "onSubscribe")
            }

            override fun onNext(t: AVUser) {
                // 保存用户名
                loginViewModel.saveUserName(t.username)
                loginViewModel.savePhoneNumber(t.mobilePhoneNumber ?: "")
                // 重置spinner初始位置
                loginViewModel.saveFirstPosition(0)
                loginViewModel.saveSecondPosition(0)
                Log.i(TAG, "登陆成功")
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "onError")
            }

            override fun onComplete() {
                loginProgressBar.onJobFinished(getString(R.string.successfully_login))
                    .laterDismiss(1500) {
                        activity?.setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("isLogin", true)
                        })
                        activity?.finish()
                    }
                Log.i(TAG, "onComplete")
            }
        })
    }
}