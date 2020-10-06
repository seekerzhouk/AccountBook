package com.seekerzhouk.accountbook

import android.app.Application
import cn.leancloud.AVLogger
import cn.leancloud.AVOSCloud

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 开启 初始化LeanCloud SDK 的调试日志。在 AVOSCloud.initialize() 之前调用
        AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);

        // 初始化LeanCloud 提供 this、App ID、绑定的自定义 API 域名作为参数
        AVOSCloud.initializeSecurely(this, "FIu0pU0zFmwNkbH0lcNJjK8K-gzGzoHsz", "https://fiu0pu0z.lc-cn-n1-shared.com");
    }
}