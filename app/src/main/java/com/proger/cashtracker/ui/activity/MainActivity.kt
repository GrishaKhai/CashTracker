package com.proger.cashtracker.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.orangegangsters.lollipin.lib.managers.AppLock
import com.github.orangegangsters.lollipin.lib.managers.LockManager
import com.google.android.material.navigation.NavigationView
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.ActivityMainBinding
import com.proger.cashtracker.db.CurrencyRepository
import com.proger.cashtracker.ui.screens.pin.PinCodeActivity
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.LocalData
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var locker = LockManager.getInstance()

    //    lateinit var bottom: NavigationUtils
    @Inject
    lateinit var currency: CurrencyRepository//todo delete here injects
//    @Inject
//    lateinit var db: AppDatabase

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.appBarMain.toolbar)

        //проверка необходимости обновления данных про курс валют и обновление
//todo        val ld = LocalData(baseContext)
//        viewModel.updateCurrency(ld)



        val localDate = LocalData(applicationContext)
        if(localDate.getIsUsingPinCode()){
            val intent = Intent(this@MainActivity, PinCodeActivity::class.java)
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN)
            startActivityForResult(intent, 101)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_accounts, R.id.nav_currencies
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)







        /*navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val a = 32 + 3
                }
                R.id.nav_accounts -> {
                    val a = 32 + 3
                }
            }
            true
        }*/
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            101 -> {
                Toast.makeText(this, "PinCode enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.empty_toolbar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    enum class Option{
        Income, Expense, Debt, Budget
    }
}