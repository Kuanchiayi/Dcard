package com.example.btnaddtab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements TabLayoutMediator.TabConfigurationStrategy {

    View view;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ImageView filter;
    String url_new = " https://www.dcard.tw/service/api/v2/posts";
    String url_hit = "https://www.dcard.tw/service/api/v2/posts?popular=true";
    NewestFragment newestFragment = new NewestFragment(url_new);
    HitFragment hitFragment = new HitFragment(url_hit);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titles.add("最新");
        titles.add("熱門");

        addToViewPager2Adapter(url_new, url_hit);
         /*           連結tabLayout和viewPager2          */
        new TabLayoutMediator(tabLayout, viewPager2, this).attach();

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDialog();
            }
        });
    }

    public void initViews(){
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager2 = view.findViewById(R.id.viewPager2);
        filter = view.findViewById(R.id.iv_filter);
    }

    private void addToViewPager2Adapter(String url_new, String url_hit) {
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);

        fragmentArrayList.clear();
        fragmentArrayList.add(newestFragment);
        fragmentArrayList.add(hitFragment);

        viewPager2Adapter.setData(fragmentArrayList);
        viewPager2.setAdapter(viewPager2Adapter);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
        tab.setText(titles.get(position));
    }

    private void onCreateDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.editcount, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // 使用setView()方法將佈局顯示到dialog
        alertDialog.setView(view);
        EditText userInput = (EditText) view.findViewById(R.id.et_input);

        alertDialog
                .setTitle("輸入貼文數量：")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (tabLayout.getSelectedTabPosition() == 0) {
                                    newestFragment.sendGet("https://www.dcard.tw/service/api/v2/posts?limit=" + userInput.getText());
                                    tabLayout.selectTab(tabLayout.getTabAt(0));
                                } else {
                                    hitFragment.sendGet("https://www.dcard.tw/service/api/v2/posts?popular=true&limit=" + userInput.getText());
                                    tabLayout.selectTab(tabLayout.getTabAt(1));
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }
}