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
import com.seekerzhouk.accountbook.databinding.ActivitySetBackgroundBinding
import com.seekerzhouk.accountbook.room.UserAddInfo
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.SDCardHelper
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetBackgroundActivity : OptionActivity() {
    private lateinit var binding: ActivitySetBackgroundBinding
    private val fromSetBg = 2
    private val tag = SetBackgroundActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBackgroundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SDCardHelper.loadBitmapFromSDCard(
            this.externalCacheDir?.absolutePath +
                    "/${SharedPreferencesUtil.getUserName(this)}" + getString(R.string.bg_pic_suffix)
        )
            ?.let {
                binding.imageViewBg.setImageBitmap(it)
            }
        registerForContextMenu(binding.imageViewBg)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.set_background_menu, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.set_background_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_background -> toPickPicture()
        }
        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_background -> toPickPicture()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toPickPicture() {
        Intent().apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }.also {
            startActivityForResult(it, fromSetBg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            fromSetBg -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        getBitmapFromUri(uri)?.let {
                            binding.imageViewBg.setImageBitmap(it)
                            SDCardHelper.saveBitmapToPrivateCache(
                                it,
                                this,
                                SharedPreferencesUtil.getUserName(this) + getString(R.string.bg_pic_suffix)
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
            SharedPreferencesUtil.getUserName(this) + getString(R.string.bg_pic_suffix),
            this.externalCacheDir?.absolutePath +
                    "/${SharedPreferencesUtil.getUserName(this)}" + getString(R.string.bg_pic_suffix)
        )
        file.saveInBackground(true).subscribe(object : Observer<AVFile> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "savePicToCloud onSubscribe()")
            }

            override fun onNext(t: AVFile) {
                MyLog.i(tag, "savePicToCloud onNext()")
                saveBgPicUrl(t.url)
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "savePicToCloud onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "savePicToCloud onComplete()")
            }

        })
    }

    private fun saveBgPicUrl(url: String) {
        // 先查询
        AVQuery<AVObject>(UserAddInfo::class.java.simpleName).apply {
            whereEqualTo("userName", AVUser.getCurrentUser().username)
        }.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "saveBgPicUrl onSubscribe()")
            }

            override fun onNext(t: AVObject) {
                MyLog.i(tag, "saveBgPicUrl onNext()")

                CoroutineScope(Dispatchers.IO).launch {
                    // 再更新
                    AVObject.createWithoutData(UserAddInfo::class.simpleName, t.objectId)
                        .apply {
                            put("bgUrl", url)
                        }.saveInBackground().subscribe(object : Observer<AVObject> {
                            override fun onSubscribe(d: Disposable) {
                                MyLog.i(tag, "saveBgPicUrl ---saveInBackground onSubscribe()")
                            }

                            override fun onNext(t: AVObject) {
                                MyLog.i(tag, "saveBgPicUrl ---saveInBackground onNext()")
                            }

                            override fun onError(e: Throwable) {
                                MyLog.i(tag, "saveBgPicUrl ---saveInBackground onError()", e)
                            }

                            override fun onComplete() {
                                MyLog.i(tag, "saveBgPicUrl ---saveInBackground onComplete()")
                            }

                        })
                }
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "saveBgPicUrl onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "saveBgPicUrl onComplete()")
            }

        })

    }
}