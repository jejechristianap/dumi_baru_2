package com.minjem.dumi

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.system.Os.close
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.minjem.dumi.fragment.*
import com.minjem.dumi.model.SharedPrefManager
import com.minjem.dumi.model.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity(){
    private var pinjaman = false
    private lateinit var mToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // memunculkan tombol burger menu
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        loadFragment(BerandaFragment())
        // untuk toggle open dan close navigation
        mToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        // tambahkan mToggle ke drawer_layout sebagai pengendali open dan close drawer
        drawer_layout.addDrawerListener(mToggle)
        mToggle.syncState()

        nav_view.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.bottom_navigation_beranda ->{
                    println("Beranda Clicked")
                    loadFragment(BerandaFragment())
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.bottom_navigation_inbox ->{
                    println("Inbox Clicked")
                    loadFragment(InboxFragment())

                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> true
            }
        }

        /*loadFragment(BerandaFragment())
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        Log.d("INTENT PINJAMAN", "onCreate: ${intent.getStringExtra("fragment")}")
        if (intent.getStringExtra("fragment") == "pinjaman"){
            loadFragment(PinjamanFragment())
            pinjaman = true
        }*/

        /* For swipe layout
        vpMain.offscreenPageLimit = 5
        vpMain.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottom_navigation.menu.findItem(R.id.bottom_navigation_beranda).isChecked = true
                    1 -> bottom_navigation.menu.findItem(R.id.bottom_navigation_inbox).isChecked = true
                    2 -> bottom_navigation.menu.findItem(R.id.bottom_navigation_mitra).isChecked = true
                    3 -> bottom_navigation.menu.findItem(R.id.bottom_navigation_bantuan).isChecked = true
                    4 -> bottom_navigation.menu.findItem(R.id.bottom_navigation_akun).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        setupViewPager(vpMain)*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return mToggle.onOptionsItemSelected(item)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit()
            return true
        }
        return false
    }

    /*private fun setupViewPager(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val fragmentHome = BerandaFragment()
        val fragmentInbox = InboxFragment()
        val fragmentPinjaman = PinjamanFragment()
        val fragmentBantuan = BantuanFragment()
        val fragmentAkun = AkunFragment()
        viewPagerAdapter.addFragment(fragmentHome)
        viewPagerAdapter.addFragment(fragmentInbox)
        viewPagerAdapter.addFragment(fragmentPinjaman)
        viewPagerAdapter.addFragment(fragmentBantuan)
        viewPagerAdapter.addFragment(fragmentAkun)

        viewPager.adapter = viewPagerAdapter
    }



    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: Fragment? = null
        var index = 0

        Log.d("MAIN", "onNavigationItemSelected: $menuItem")

        when (menuItem.itemId) {
            R.id.bottom_navigation_beranda -> fragment = BerandaFragment()
            R.id.bottom_navigation_inbox -> fragment = InboxFragment()
            R.id.bottom_navigation_mitra -> fragment = PinjamanFragment()
            R.id.bottom_navigation_bantuan -> fragment = BantuanFragment()
            R.id.bottom_navigation_akun -> fragment = AkunFragment()
        }
//        vpMain.currentItem = index

        *//* Swipe layout
               when (menuItem.itemId) {
                   R.id.bottom_navigation_beranda -> {
                       index = 0
                       vpMain.currentItem = index
                   }
                   R.id.bottom_navigation_inbox -> {
                       index = 1
                       vpMain.currentItem = index
                   }
                   R.id.bottom_navigation_mitra -> {
                       index = 2
                       vpMain.currentItem = index
                   }
                   R.id.bottom_navigation_bantuan -> {
                       index = 3
                       vpMain.currentItem = index
                   }
                   R.id.bottom_navigation_akun -> {
                       index = 4
                       vpMain.currentItem = index
                   }
               }
       *//*
        return loadFragment(fragment)
    }*/

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey()
            //moveTaskToBack(false);
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exitByBackKey() {
        AlertDialog.Builder(this)
                .setMessage("Apa anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _: DialogInterface?, _: Int ->
                    SharedPrefManager.getInstance(applicationContext).logout()
                    finish()
                    startActivity(Intent(this@MainActivity, HalamanDepanActivity::class.java))
                }
                .setNegativeButton("Tidak") { _: DialogInterface?, _: Int -> }
                .show()
    }
}