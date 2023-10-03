package com.rbs.studio.trackless.vpn.view.activites

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.AdRequest
import com.rbs.studio.trackless.vpn.AppSettings
import com.rbs.studio.trackless.vpn.R
import com.rbs.studio.trackless.vpn.databinding.ActivityControllerBinding

class ControllerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControllerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControllerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        //Adddddddd final
        loadBannerAd()
        //Adddddddd
    }

    /*
    * Controller activity control the Navigation behaviour of the Fragments
    * */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.main_nav_menu)
        val menu = popupMenu.menu
        binding.mainBottomNav.setupWithNavController(menu, navHostFragment.navController)
    }
    private fun loadBannerAd() {
        if (!AppSettings.isUserPaid) {
            binding!!.adBlock.visibility = View.VISIBLE
            binding!!.bannerContainerAdmob.visibility = View.GONE


            if (AppSettings.enableAdmobAds) {
                binding!!.bannerContainerAdmob.visibility = View.VISIBLE
                loadAdmobBannerAd()

            }
        } else {
            binding!!.adBlock.visibility = View.GONE
        }
    }

    private fun loadAdmobBannerAd() {
        val adview = binding!!.bannerContainerAdmob
        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)
    }
}