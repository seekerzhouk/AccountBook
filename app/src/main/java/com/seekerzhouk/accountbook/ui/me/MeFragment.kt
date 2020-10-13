package com.seekerzhouk.accountbook.ui.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.SetBackgroundActivity
import com.seekerzhouk.accountbook.ui.CommonDialog
import com.seekerzhouk.accountbook.utils.SDCardHelper
import kotlinx.android.synthetic.main.fragment_me.*


class MeFragment : Fragment() {

    private val meViewModel: MeViewModel by viewModels()

    private var isLogin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_user_username.text = meViewModel.getUserName()
        tv_user_phone.text = meViewModel.getPhoneNumber()
        meViewModel.isLogin().observe(requireActivity(), Observer {
            isLogin = it
            if (!isLogin) {
                clickToLoginLayout.visibility = View.VISIBLE
                appBarLayout.visibility = View.GONE
                nestedScrollView.visibility = View.GONE
                textViewClickToLogin.setOnClickListener {
                    findNavController().navigate(R.id.action_navigation_me_to_loginActivity)
                }
                return@Observer
            }
            clickToLoginLayout.visibility = View.GONE
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
                    meViewModel.saveLogin(false)
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

            meViewModel.cloudAndLocalDataInit()
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


}