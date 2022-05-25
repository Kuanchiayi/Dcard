package com.example.btnaddtab;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{

    NavigationBarView navigationBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        /*      設定一開始的頁面是Home   */
        setTitle("首頁");
        HomeFragment fragment = new HomeFragment();
        openFragment(fragment);

        /*        ButtonNavigationBar     */
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                HomeFragment homefragment = new HomeFragment();
                FavoriteFragment favoritefragment = new FavoriteFragment();
                UpdateFragment updatefragment = new UpdateFragment();
                switch (item.getItemId()) {
                    case R.id.home:
                        setTitle("首頁");
                        openFragment(homefragment);
                        break;
                    case R.id.favorite:
                        setTitle("收藏");
                        openFragment(favoritefragment);
                        break;
                    case R.id.update:
                        setTitle("更新");
                        openFragment(updatefragment);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void initViews(){
        navigationBarView = findViewById(R.id.navigation);
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_main, fragment);
        fragmentTransaction.commit();
    }
}