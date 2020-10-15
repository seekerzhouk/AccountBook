package com.seekerzhouk.accountbook.ui.me

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.ui.options.SetBackgroundActivity
import com.seekerzhouk.accountbook.ui.customize.CommonDialog
import com.seekerzhouk.accountbook.ui.options.LoginActivity
import com.seekerzhouk.accountbook.utils.SDCardHelper
import com.seekerzhouk.accountbook.viewmodel.MeViewModel
import kotlinx.android.synthetic.main.fragment_me.*


class MeFragment : Fragment() {

    private val meViewModel: MeViewModel by viewModels()

    private var isLogin = false

    private val toLoginCode = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar_imageView.setOnClickListener {
            startActivity(Intent(requireActivity(), SetBackgroundActivity::class.java))
        }

        cl_logout.setOnClickListener {
            CommonDialog.showDialog(
                requireActivity(),
                getString(R.string.title_logout),
                getString(R.string.dialog_message)
            ) {
                // 点击logout按钮，实际上没有登出账号。SDK已经记录了登录的用户。除非使用AVUser.logOut()才会登出。
                meViewModel.saveIsLogin(false)
            }
        }

        cl_sync.setOnClickListener {
            CommonDialog.showDialog(
                requireActivity(),
                getString(R.string.sync_data),
                getString(R.string.sync_data_message)
            ) {
                meViewModel.saveIsNeedSync(true)
            }
        }

        textViewClickToLogin.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), LoginActivity::class.java),
                toLoginCode
            )
        }

        meViewModel.getIsLogin().observe(requireActivity(), Observer {
            isLogin = it
            if (isLogin) {
                clickToLoginLayout.visibility = View.GONE
                appBarLayout.visibility = View.VISIBLE
                nestedScrollView.visibility = View.VISIBLE
                tv_user_username.text = meViewModel.getUserName()
                tv_user_phone.text = meViewModel.getPhoneNumber()
            } else {
                clickToLoginLayout.visibility = View.VISIBLE
                appBarLayout.visibility = View.GONE
                nestedScrollView.visibility = View.GONE
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (!isLogin) {
            return
        }
        SDCardHelper.loadBitmapFromSDCard(requireContext().externalCacheDir?.absolutePath + "/background_pic.png")
            ?.let {
                toolbar_imageView.setImageBitmap(it)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == toLoginCode && resultCode == Activity.RESULT_OK) {
            if (data?.extras?.getBoolean("isLogin")!!) {
                meViewModel.saveIsLogin(true)
                meViewModel.cloudAndLocalDataInit()
            }
        }
    }

}