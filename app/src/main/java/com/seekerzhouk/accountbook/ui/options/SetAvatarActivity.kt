package com.seekerzhouk.accountbook.ui.options

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cn.leancloud.AVFile
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.ActivitySetAvatarBinding
import com.seekerzhouk.accountbook.room.UserAddInfo
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.SDCardHelper
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetAvatarActivity : OptionActivity() {
    private lateinit var binding: ActivitySetAvatarBinding
    private val fromSetAvatar = 3
    private val tag = SetAvatarActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAvatarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SDCardHelper.loadBitmapFromSDCard(
            this.externalCacheDir?.absolutePath +
                    "/${SharedPreferencesUtil.getUserName(this)}" + getString(R.string.avatar_pic_suffix)
        )?.let {
            binding.avatarImage.setImageBitmap(it)
        }
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
                        getBitmapFromUri(uri)?.let {
                            binding.avatarImage.setImageBitmap(it)
                            SDCardHelper.saveBitmapToPrivateCache(
                                it,
                                this,
                                SharedPreferencesUtil.getUserName(this) + getString(R.string.avatar_pic_suffix)
                            )
                            savePicToCloud()
                        }
                    }
                }
            }
        }
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