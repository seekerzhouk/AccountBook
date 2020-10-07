package com.seekerzhouk.accountbook.ui.me

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private val TAG = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false).also {
            it.isFocusableInTouchMode = true
            it.requestFocus()
            it.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        this@SignUpFragment.findNavController()
                            .navigate(R.id.action_signUpFragment_to_loginFragment)
                        return true
                    }
                    return false
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSignUp.setOnClickListener {
            if (signUpUserName.text.isEmpty()) {
                Toast.makeText(context, R.string.user_name_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (signUpPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.password_cannot_be_null, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }
            if (signUpConfirmPassword.text.isEmpty()) {
                Toast.makeText(context, R.string.please_confirm_password, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            if (signUpPassword.text.trim().toString()
                != signUpConfirmPassword.text.trim().toString()
            ) {
                Toast.makeText(context, R.string.inconsistent_passwords, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            if (!userNameCanUse(signUpUserName.text.trim().toString())) {
                Toast.makeText(context, R.string.user_name_already_exists, Toast.LENGTH_SHORT)
                    .also {
                        it.setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                signUp(signUpUserName.text.trim().toString(), signUpPassword.text.trim().toString())
            }
        }
    }

    fun userNameCanUse(userName: String): Boolean {
        return true
    }

    private fun signUp(signUserName: String, signPassword: String) {
        val user = AVUser().apply {
            this.username = signUserName
            this.password = signPassword
        }.also {
            it.signUpInBackground().subscribe(object : Observer<AVUser> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "onSubscribe")
                }

                override fun onNext(t: AVUser) {

                    Log.i(TAG, "注册成功")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.i(TAG, "onError")
                }

                override fun onComplete() {
                    Toast.makeText(context, R.string.successfully_registered, Toast.LENGTH_SHORT)
                        .also { toast ->
                            toast.setGravity(Gravity.CENTER, 0, 0)
                        }.show()
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                    Log.i(TAG, "onComplete")
                }

            })
        }

    }
}