package com.minjem.dumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.minjem.dumi.fragment.AkunFragment;
import com.minjem.dumi.fragment.BantuanFragment;
import com.minjem.dumi.fragment.BerandaFragment;
import com.minjem.dumi.fragment.InboxFragment;
import com.minjem.dumi.fragment.MitraFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new BerandaFragment());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.bottom_navigation_beranda:
                fragment = new BerandaFragment();
                break;
            case R.id.bottom_navigation_inbox:
                fragment = new InboxFragment();
                break;
            case R.id.bottom_navigation_mitra:
                fragment = new MitraFragment();
                break;
            case R.id.bottom_navigation_bantuan:
                fragment = new BantuanFragment();
                break;
            case R.id.bottom_navigation_akun:
                fragment = new AkunFragment();
                break;
        }
        return loadFragment(fragment);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void exitByBackKey() {
        // do something when the button is clicked
        // do something when the button is clicked
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Apa anda yakin ingin keluar?")
                .setPositiveButton("Ya", (arg0, arg1) -> {
                    finish();
                    startActivity(new Intent(MainActivity.this, HalamanDepanActivity.class));
                    //close();
                })
                .setNegativeButton("Tidak", (arg0, arg1) -> {
                })
                .show();
    }
}
