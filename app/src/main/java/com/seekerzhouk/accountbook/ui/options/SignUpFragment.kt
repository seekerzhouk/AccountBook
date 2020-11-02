package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentSignUpBinding
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.NetworkUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val _tag = SignUpFragment::class.java.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
//            .also {
//            it.isFocusableInTouchMode = true
//            it.requestFocus()
//            it.setOnKeyListener(object : View.OnKeyListener {
//                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                    if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                        this@SignUpFragment.findNavController()
//                            .navigate(R.id.action_signUpFragment_to_loginFragment)
//                        return true
//                    }
//                    return false
//                }
//            })
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.sign_up)

        binding.signUpConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.buttonSignUp.performClick()
            }
            false
        }

        binding.buttonSignUp.setOnClickListener {
            if (binding.signUpUserName.text.isEmpty()) {
                Toast.makeText(context, R.string.user_name_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (binding.signUpPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.password_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (binding.signUpConfirmPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.please_confirm_password, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            if (binding.signUpPassword.text.trim().toString()
                != binding.signUpConfirmPassword.text.trim().toString()
            ) {
                Toast.makeText(context, R.string.inconsistent_passwords, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            if (!userNameCanUse(binding.signUpUserName.text.trim().toString())) {
                Toast.makeText(context, R.string.user_name_already_exists, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            NetworkUtil.doWithNetwork(requireContext()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    signUp(
                        binding.signUpUserName.text.trim().toString(),
                        binding.signUpPassword.text.trim().toString()
                    )
                }
                binding.signUpProgressBar.show(getString(R.string.is_signing_up))
            }
        }

        binding.returnToLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun userNameCanUse(userName: String): Boolean {
        return true
    }

    private fun signUp(signUserName: String, signPassword: String) {
        AVUser().apply {
            this.username = signUserName
            this.password = signPassword
        }.also {
            it.signUpInBackground().subscribe(object : Observer<AVUser> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(_tag, "signUp onSubscribe")
                }

                override fun onNext(t: AVUser) {

                    MyLog.i(_tag, "signUp onNext")
                }

                override fun onError(e: Throwable) {
                    binding.signUpProgressBar.onJobError(getString(R.string.user_name_already_exists))
                        .laterDismiss(1500) {}
                    MyLog.i(_tag, "signUp onError ", e)
                }

                override fun onComplete() {
                    binding.signUpProgressBar.onJobFinished(getString(R.string.successfully_sign_up))
                        .laterDismiss(1500) {
                            binding.signUpLayout.visibility = View.GONE
                            binding.succeedLayout.visibility = View.VISIBLE
                        }
                    MyLog.i(_tag, "signUp onComplete")
                }

            })
        }

    }
}