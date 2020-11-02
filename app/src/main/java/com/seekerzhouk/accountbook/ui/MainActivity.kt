package com.seekerzhouk.accountbook.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.ActivityMainBinding
import com.seekerzhouk.accountbook.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var lastPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navController = findNavController(R.id.nav_host_fragment_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_details, R.id.navigation_me
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.isNeedSync().observe(this, {
            if (it) {
                mainViewModel.loadCloudPic()
                mainViewModel.syncData()
                binding.myProgressBar.show(getString(R.string.data_syncing))
                mainViewModel.saveHasSyncFinished(false)
                mainViewModel.saveIsNeedSync(false)
            }
        })

        mainViewModel.hasSyncFinished().observe(this, {
            if (it) {
                binding.myProgressBar.onJobFinished(getString(R.string.sync_finished)).laterDismiss(1500) {}
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.saveIsNeedSync(false)
    }

    override fun onBackPressed() {
//        // 如果是bottomNavigationView的三个fragment，单独处理返回键
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.primaryNavigationFragment
//        if (currentFragment is HomeFragment || currentFragment is DetailsFragment || currentFragment is MeFragment) {
//            val curTime = SystemClock.uptimeMillis()
//            if (curTime - lastPressedTime < 3_000) {
//                finish()
//            } else {
//                Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).also {
//                    it.setGravity(Gravity.CENTER, 0, 0)
//                }.show()
//            }
//            lastPressedTime = SystemClock.uptimeMillis()
//        } else {
//            super.onBackPressed()
//        }

        val curTime = SystemClock.uptimeMillis()
        if (curTime - lastPressedTime < 3_000) {
            finish()
        } else {
            Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).also {
                it.setGravity(Gravity.CENTER, 0, 0)
            }.show()
        }
        lastPressedTime = SystemClock.uptimeMillis()
    }
}
