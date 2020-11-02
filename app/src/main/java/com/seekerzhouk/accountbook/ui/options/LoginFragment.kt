package com.seekerzhouk.accountbook.ui.options

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentLoginBinding
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.NetworkUtil
import com.seekerzhouk.accountbook.viewmodel.LoginViewModel
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    private val _tag = LoginFragment::class.java.name
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
//            .also {
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
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.login)

        binding.loginPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.buttonLogin.performClick()
            }
            false
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.loginUserName.text.isEmpty()) {
                Toast.makeText(context, R.string.user_name_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (binding.loginPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.password_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            NetworkUtil.doWithNetwork(requireContext()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    login(
                        binding.loginUserName.text.trim().toString(),
                        binding.loginPassword.text.trim().toString()
                    )
                }
                binding.loginProgressBar.show(getString(R.string.is_logging))
            }
        }

        binding.textViewSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun login(user: String, password: String) {
        AVUser.logIn(user, password).subscribe(object : Observer<AVUser> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(_tag, "login onSubscribe")
            }

            override fun onNext(t: AVUser) {
                // 保存用户名
                loginViewModel.saveUserName(t.username)
                loginViewModel.savePhoneNumber(t.mobilePhoneNumber ?: "")
                // 重置spinner初始位置
                loginViewModel.saveFirstPosition(0)
                loginViewModel.saveSecondPosition(0)
                MyLog.i(_tag, "login onNext")
            }

            override fun onError(e: Throwable) {
                binding.loginProgressBar.onJobError(getString(R.string.Incorrect_username_or_password))
                    .laterDismiss(1500) {}
                MyLog.i(_tag, "login onError ", e)
            }

            override fun onComplete() {
                binding.loginProgressBar.onJobFinished(getString(R.string.successfully_login))
                    .laterDismiss(1500) {
                        activity?.setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("isLogin", true)
                        })
                        activity?.finish()
                    }
                MyLog.i(_tag, "login onComplete")
            }
        })
    }
}