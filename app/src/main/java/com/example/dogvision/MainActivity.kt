package com.example.dogvision

import ViewPagerAdapter
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.dogvision.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView






class MainActivity : AppCompatActivity() {

  //  FirstFragment firstFragment = new FirstFragment();
  //  SecondFragment secondFragment = new SecondFragment();
//   ThirdFragment thirdFragment = new ThirdFragment();


    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //replaceFragment(Home())

        viewPager = binding.viewPager
        bottomNavigation = binding.bottomNavigation

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigation.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {

               // R.id.Home -> replaceFragment(Home())
                //R.id.List -> replaceFragment(List())
                R.id.Home -> viewPager.currentItem = 0
                R.id.List -> viewPager.currentItem = 1


                else -> {

                }
            }

                true

            }
        }



    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, fragment)
        fragmentTransaction.commit()
    }



}