package com.seekerzhouk.accountbook.ui.me

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentMeBinding
import com.seekerzhouk.accountbook.ui.options.SetBackgroundActivity
import com.seekerzhouk.accountbook.ui.customize.CommonDialog
import com.seekerzhouk.accountbook.ui.options.LoginActivity
import com.seekerzhouk.accountbook.ui.options.SetAvatarActivity
import com.seekerzhouk.accountbook.ui.options.SetPhoneActivity
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.NetworkUtil
import com.seekerzhouk.accountbook.utils.SDCardHelper
import com.seekerzhouk.accountbook.viewmodel.MeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MeFragment : Fragment() {

    private lateinit var binding: FragmentMeBinding

    private val meViewModel: MeViewModel by viewModels()

    private var isLogin = false

    private val toLoginCode = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarBgImage.setOnClickListener {
            startActivity(Intent(requireActivity(), SetBackgroundActivity::class.java))
        }

        binding.clAvatar.setOnClickListener {
            startActivity(Intent(requireContext(), SetAvatarActivity::class.java))
        }

        binding.clUsername.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_support_username_modify),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.clPhone.setOnClickListener {
            startActivity(Intent(requireContext(), SetPhoneActivity::class.java))
        }

        binding.clLogout.setOnClickListener {
            CommonDialog.showDialog(
                requireActivity(),
                getString(R.string.title_logout),
                getString(R.string.dialog_message)
            ) {
                // 点击logout按钮，实际上没有登出账号。SDK已经记录了登录的用户。除非使用AVUser.logOut()才会登出。
                meViewModel.saveIsLogin(false)
            }
        }

        binding.clAbout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_me_to_aboutActivity)
        }

        binding.clSync.setOnClickListener {
            CommonDialog.showDialog(
                requireActivity(),
                getString(R.string.sync_data),
                getString(R.string.sync_data_message)
            ) {
                NetworkUtil.doWithNetwork(requireContext()) {
                    meViewModel.saveIsNeedSync(true)
                }
            }
        }

        binding.clHelpAndFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_me_to_feedBackActivity)
        }

        binding.textViewClickToLogin.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), LoginActivity::class.java), toLoginCode
            )
        }

        meViewModel.getIsLogin().observe(requireActivity(), Observer {
            isLogin = it
            if (isLogin) {
                binding.clickToLoginLayout.visibility = View.GONE
                binding.appBarLayout.visibility = View.VISIBLE
                binding.nestedScrollView.visibility = View.VISIBLE
                binding.tvUserUsername.text = meViewModel.getUserName()
                binding.tvUserPhone.text = meViewModel.getPhoneNumber()
            } else {
                binding.clickToLoginLayout.visibility = View.VISIBLE
                binding.appBarLayout.visibility = View.GONE
                binding.nestedScrollView.visibility = View.GONE
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (!isLogin) {
            return
        }
        Glide.with(this).load(
            requireContext().externalCacheDir?.absolutePath + "/${meViewModel.getUserName()}"
                    + getString(R.string.bg_pic_suffix)
        ).placeholder(R.drawable.src_avatar).into(binding.toolbarBgImage)
        Glide.with(this).load(
            requireContext().externalCacheDir?.absolutePath + "/${meViewModel.getUserName()}"
                    + getString(R.string.avatar_pic_suffix)
        ).placeholder(R.drawable.ic_me).into(binding.ivAvatar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == toLoginCode && resultCode == Activity.RESULT_OK) {
            if (data?.extras?.getBoolean("isLogin")!!) {
                meViewModel.saveIsLogin(true)
                meViewModel.cloudAndLocalDataInit()
                lifecycleScope.launch {
                    delay(5_000)
                    onResume()
                }
            }
        }
    }

}