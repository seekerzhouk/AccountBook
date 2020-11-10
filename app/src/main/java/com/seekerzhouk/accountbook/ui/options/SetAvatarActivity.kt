package com.seekerzhouk.accountbook.ui.options

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.ActivitySetAvatarBinding
import com.seekerzhouk.accountbook.room.me.UserAddInfo
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.SDCardHelper
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SetAvatarActivity : OptionActivity() {
    private lateinit var binding: ActivitySetAvatarBinding
    private val fromSetAvatar = 3
    private val tag = SetAvatarActivity::class.java.simpleName
    private lateinit var avatarPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avatarPath = this.externalCacheDir?.absolutePath +
                "/${SharedPreferencesUtil.getUserName(this)}" + getString(R.string.avatar_pic_suffix)
        binding = ActivitySetAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(avatarPath)
            .signature(ObjectKey(SharedPreferencesUtil.getAvatarSignature(this)))
            .error(R.drawable.ic_me)
            .into(binding.avatarImage)
        registerForContextMenu(binding.avatarImage)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.set_avatar_menu, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.set_avatar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_avatar -> toPickPicture()
        }
        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_avatar -> toPickPicture()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toPickPicture() {
        Intent().apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }.also {
            startActivityForResult(it, fromSetAvatar)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            fromSetAvatar -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        binding.avatarImage.setImageURI(uri)
                        lifecycleScope.launch(Dispatchers.IO) {
                            getBitmapFromUri(uri)?.let {
                                SDCardHelper.saveBitmapToPrivateCache(
                                    it, this@SetAvatarActivity,
                                    SharedPreferencesUtil.getUserName(this@SetAvatarActivity) + getString(
                                        R.string.avatar_pic_suffix
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    saveToGlideCache()
                                }
                                savePicToCloud()
                            }
                        }
                    }
                }
            }
        }
    }

    // 将图片加到Glide缓存
    @SuppressLint("CheckResult")
    private fun saveToGlideCache() {
        val sign = SharedPreferencesUtil.getAvatarSignature(this) + 1
        SharedPreferencesUtil.saveAvatarSignature(this, sign)
        Glide.with(this)
            .load(avatarPath)
            .signature(ObjectKey(sign))
    }

    private fun getBitmapFromUri(uri: Uri) =
        contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun savePicToCloud() {
        val file = AVFile.withAbsoluteLocalPath(
            SharedPreferencesUtil.getUserName(this) + getString(R.string.avatar_pic_suffix),
            this.externalCacheDir?.absolutePath +
                    "/${SharedPreferencesUtil.getUserName(this)}" + getString(R.string.avatar_pic_suffix)
        )
        file.saveInBackground(true).subscribe(object : Observer<AVFile> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "savePicToCloud onSubscribe()")
            }

            override fun onNext(t: AVFile) {
                MyLog.i(tag, "savePicToCloud onNext()")
                saveAvatarPicUrl(t.url)
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "savePicToCloud onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "savePicToCloud onComplete()")
            }

        })
    }

    private fun saveAvatarPicUrl(url: String) {
        // 先查询
        AVQuery<AVObject>(UserAddInfo::class.java.simpleName).apply {
            whereEqualTo("userName", AVUser.getCurrentUser().username)
        }.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "saveAvatarPicUrl onSubscribe()")
            }

            override fun onNext(t: AVObject) {
                MyLog.i(tag, "saveAvatarPicUrl onNext()")

                CoroutineScope(Dispatchers.IO).launch {
                    // 再更新
                    AVObject.createWithoutData(UserAddInfo::class.simpleName, t.objectId)
                        .apply {
                            put("avatarUrl", url)
                        }.saveInBackground().subscribe(object : Observer<AVObject> {
                            override fun onSubscribe(d: Disposable) {
                                MyLog.i(tag, "saveAvatarPicUrl ---saveInBackground onSubscribe()")
                            }

                            override fun onNext(t: AVObject) {
                                MyLog.i(tag, "saveAvatarPicUrl ---saveInBackground onNext()")
                            }

                            override fun onError(e: Throwable) {
                                MyLog.i(tag, "saveAvatarPicUrl ---saveInBackground onError()", e)
                            }

                            override fun onComplete() {
                                MyLog.i(tag, "saveAvatarPicUrl ---saveInBackground onComplete()")
                            }

                        })
                }
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "saveAvatarPicUrl onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "saveAvatarPicUrl onComplete()")
            }

        })

    }
}