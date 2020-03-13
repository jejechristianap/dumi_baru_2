package com.fidac.dumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.fidac.dumi.fragment.AkunFragment;
import com.fidac.dumi.fragment.BantuanFragment;
import com.fidac.dumi.fragment.BerandaFragment;
import com.fidac.dumi.fragment.InboxFragment;
import com.fidac.dumi.fragment.MitraFragment;
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
}
