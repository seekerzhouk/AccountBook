package com.seekerzhouk.accountbook.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.seekerzhouk.accountbook.R
import java.io.*

object SDCardHelper {
    // 判断SD卡是否被挂载
    private fun isSDCardMounted(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // 保存bitmap图片到SDCard的私有Cache目录
    fun saveBitmapToPrivateCache(bitmap: Bitmap, context: Context): Boolean {
        if (!isSDCardMounted()) {
            return false
        }
        var bufferedOutputStream: BufferedOutputStream? = null
        try {
            bufferedOutputStream = BufferedOutputStream(
                FileOutputStream(
                    File(
                        context.externalCacheDir,
                        SharedPreferencesUtil.getUserName(context) + context.getString(R.string.bg_pic_suffix)
                    )
                )
            ).also {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            bufferedOutputStream?.close()
        }
        return true
    }

    // 从SDCard中寻找指定目录下的文件，返回Bitmap
    fun loadBitmapFromSDCard(filePath: String): Bitmap? {
        return BitmapFactory.decodeFile(filePath)
    }
}