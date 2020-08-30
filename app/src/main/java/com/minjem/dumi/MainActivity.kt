package com.minjem.dumi

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.minjem.dumi.fragment.*
import com.minjem.dumi.model.SharedPrefManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*


open class MainActivity : AppCompatActivity()/*, Toolbar.OnMenuItemClickListener*/{
    private var pinjaman = false
    private lateinit var toolbarMenu: Menu
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var position: String
    lateinit var fm: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbarOnclick()
        position = "Beranda"
//        fm = supportFragmentManager
//        toolbarClick = this as ToolbarOnClick
        /*val toolbar = findViewById<View>(R.id.toolbarMain) as Toolbar
        toolbar.inflateMenu(R.menu.menu_toolbar_main)
        toolbar.setOnMenuItemClickListener(this)*/

        when(intent.getStringExtra("navigation")){
            "PinjamanFragment" -> {
                loadFragment(PinjamanFragment())
            }
            else -> {
                loadFragment(BerandaFragment())
                supportActionBar?.title = "Beranda"
            }
        }
        // untuk toggle open dan close navigation
        mToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        // tambahkan mToggle ke drawer_layout sebagai pengendali open dan close drawer
        drawer_layout.addDrawerListener(mToggle)
        mToggle.syncState()
        Glide.with(this)
                .load(SharedPrefManager.getInstance(this).user.imageProfile)
                .transform(CircleCrop(), RoundedCorners(16))
                .placeholder(R.drawable.circle_white)
                .into(nav_view.getHeaderView(0).ivPhotoProfile)
        nav_view.getHeaderView(0).tvNamaPns.text = SharedPrefManager.getInstance(this).user.namaLengkap
        nav_view.getHeaderView(0).tvNip.text = SharedPrefManager.getInstance(this).user.nip

        nav_view.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.bottom_navigation_beranda ->{
                    loadFragment(BerandaFragment())
                    position = "Beranda"
//                    toolbarMenu.findItem(R.id.actionRiwayat).isVisible = false
                    supportActionBar?.title = position
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.bottom_navigation_inbox ->{
                    loadFragment(InboxFragment())
                    position = "Inbox"
                    supportActionBar?.title = position
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.bottom_navigation_mitra -> {
                    loadFragment(PinjamanFragment())
                    position = "Pinjaman"
//                    toolbarMenu.findItem(R.id.actionRiwayat).isVisible = true
                    supportActionBar?.title = position
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.bottom_navigation_bantuan -> {
                    loadFragment(BantuanFragment())
                    position = "Bantuan"
                    supportActionBar?.title = position
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.bottom_navigation_akun -> {
                    loadFragment(AkunFragment())
                    position = "Akun"
                    supportActionBar?.title = position
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> true
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_toolbar_main, menu)
        toolbarMenu = menu!!

        return true
    }

    private fun toolbarOnclick(){
        toolbarMain.setOnMenuItemClickListener {
            when(it.itemId){
                /*R.id.actionRiwayat -> {
                    Toast.makeText(this, "Riwayat", Toast.LENGTH_SHORT).show()
                    val myFragment: PinjamanFragment = PinjamanFragment.getHistory()
                    val fragment = supportFragmentManager.findFragmentById(R.id.fPinjaman)
                    toolbarClick.getHistoryPinjaman("wulugh")


                    true
                }*/
                R.id.actionLogout -> {
                    SharedPrefManager.getInstance(applicationContext).logout()
                    finish()
                    startActivity(Intent(this@MainActivity, HalamanDepanActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
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



    /*override fun getHistoryPinjaman(test: String) {
        Log.d("MainActivity", "getHistoryPinjaman: $test")
    }*/

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
/*override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.actionLogout -> {
            SharedPrefManager.getInstance(applicationContext).logout()
            finish()
            startActivity(Intent(this@MainActivity, HalamanDepanActivity::class.java))
            return true
        }

        }
        return false
    }*/
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