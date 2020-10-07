package com.seekerzhouk.accountbook.ui.me

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.SetBackgroundActivity
import com.seekerzhouk.accountbook.utils.SDCardHelper
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.fragment_me_login.*


class MeFragment : Fragment() {

    private lateinit var meViewModel: MeViewModel

    private var isLogin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        meViewModel =
            ViewModelProvider(this).get(MeViewModel::class.java)
        val root = if (isLogin) {
            inflater.inflate(R.layout.fragment_me, container, false)
        } else {
            inflater.inflate(R.layout.fragment_me_login, container, false)
        }
        meViewModel.text.observe(requireActivity(), Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isLogin) {
            textViewClickToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_me_to_loginFragment)
            }
            return
        }
        toolbar_imageView.setOnClickListener {
            startActivity(Intent(requireActivity(), SetBackgroundActivity::class.java))
        }
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