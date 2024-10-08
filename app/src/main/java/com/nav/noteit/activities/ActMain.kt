package com.nav.noteit.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nav.noteit.R
import com.nav.noteit.databinding.ActMainBinding
import com.nav.noteit.fragments.FragNotes
import com.nav.noteit.fragments.FragReminders
import com.nav.noteit.helper.Utils
import com.nav.noteit.viewmodel.SearchViewModel
import com.nav.noteit.viewmodel.UserViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class ActMain : ActBase() {


    private lateinit var binding: ActMainBinding
    private lateinit var menu: Menu
    private val searchViewModel: SearchViewModel by viewModels()
    lateinit var snackBarReminder: Snackbar
    private val userViewModel by inject<UserViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        setContentView(initBinding())

        initVariables()
        initClick()
        initFragment()
        lifecycleScope.launch {
            async {
                getUserData()

            }.await()

            setNavigationDrawer()
        }
        searchNote()
        callWorkRequest()


//        selectMenuDrawer()
        if (Utils.isInternetConnectedCheck(this@ActMain)) {
            Toast.makeText(this@ActMain, "Internet is active", Toast.LENGTH_SHORT).show()
        } else {

            Toast.makeText(this@ActMain, "Internet is inactive", Toast.LENGTH_SHORT).show()
        }


    }

    private fun getUserData() {

        userViewModel.getUserDataFromPrefs()
        getTextViewFromDrawer()

    }


    fun selectMenuDrawer() {
        for (frag in supportFragmentManager.fragments) {
            when (frag) {
                is FragNotes -> {
                    binding.mainNavView.setCheckedItem(R.id.actionNotes)
                }

                is FragReminders -> {
                    binding.mainNavView.setCheckedItem(R.id.actionReminder)
                }

                else -> {
                    binding.mainNavView.setCheckedItem(R.id.actionNotes)
                }
            }
        }
    }


    fun changeToSaveIcon(isShow: Boolean, clickListeners: ClickListeners?) {
        if (isShow) {
            binding.imgSideMenu.setImageResource(R.drawable.ic_save)
            binding.imgSideMenu.setOnClickListener {
                clickListeners?.onSaveBtnClick()
            }
        } else {
            binding.imgSideMenu.setImageResource(R.drawable.note_it_logo)
            binding.imgSideMenu.setOnClickListener { }
        }
    }


    private fun searchNote() {
        binding.edtSearchNote.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    searchViewModel.selectItem(newText)
                    searchViewModel.emptySearch(false)
                } else {
                    searchViewModel.emptySearch(true)
                    println("No letter")
                }
                return true
            }

        })
    }


    fun changeIconToBack(isShow: Boolean) {
        if (isShow) {
            binding.mainToolbar.navigationIcon =
                AppCompatResources.getDrawable(baseContext, R.drawable.baseline_arrow_back_24)
            binding.edtSearchNote.visibility = View.INVISIBLE
            binding.mainToolbar.setNavigationOnClickListener {
                if (supportFragmentManager.backStackEntryCount > 0) {

                    supportFragmentManager.popBackStackImmediate()
                }

                changeIconToBack(false)
            }
        } else {
            binding.mainToolbar.navigationIcon =
                AppCompatResources.getDrawable(baseContext, R.drawable.baseline_menu_24)
            binding.edtSearchNote.visibility = View.VISIBLE
            binding.mainToolbar.setNavigationOnClickListener {
                binding.mainDrawerLyt.openDrawer(GravityCompat.START)
            }


        }
    }

    private fun initFragment() {
        val fragMain = FragNotes()
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragTransaction.replace(R.id.mainFragContainer, fragMain)
        fragTransaction.commit()
    }

    private fun initVariables() {

        userViewModel.isRememberMeSet()
        menu = binding.mainNavView.menu
    }


    private fun setNavigationDrawer() {


        binding.mainNavView.setNavigationItemSelectedListener {

            binding.mainDrawerLyt.closeDrawer(GravityCompat.START)


            binding.mainNavView.setCheckedItem(it.itemId)

            when (it.itemId) {
                R.id.actionNotes -> {

                    if (supportFragmentManager.findFragmentById(R.id.mainFragContainer) is FragNotes) {
                        binding.mainDrawerLyt.closeDrawer(GravityCompat.START)
                    } else {

                        if (supportFragmentManager.backStackEntryCount >= 0) {
                            supportFragmentManager.popBackStackImmediate()
                        }
                        Utils.replaceFrag(
                            supportFragmentManager,
                            R.id.mainFragContainer,
                            FragNotes(),
                            R.anim.slide_in_left,
                            R.anim.slide_out_left,
                            0,
                            0
                        )
                    }
                    true
                }

                R.id.actionReminder -> {
                    if (supportFragmentManager.findFragmentById(R.id.mainFragContainer) is FragReminders) {
                        binding.mainDrawerLyt.closeDrawer(GravityCompat.START)
                    } else {

                        Utils.addFrag(
                            supportFragmentManager,
                            R.id.mainFragContainer,
                            FragReminders(),
                            R.anim.slide_in_left,
                            R.anim.slide_out_left,
                            0,
                            0
                        )
                    }
                    true
                }

                R.id.actionSetting -> {
                    true
                }

                R.id.actionLogout -> {


                    showLoader()

                    userViewModel.clearData()

                    startActivity(Intent(this@ActMain, ActLogin::class.java))
                    closeLoader()
                    finish()

                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun getTextViewFromDrawer() {
        val headerView = binding.mainNavView.getHeaderView(0)

        val tv = headerView.findViewById<TextView>(R.id.txtNavUserName)

        tv?.let {

            userViewModel.userData.observe(this@ActMain) { userData ->

                tv.text = userData.fullName

            }
        }


    }

    override fun onRestart() {
        super.onRestart()
        Log.e("ActMain", "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.e("ActMain", "onResume")

    }

    private fun initBinding(): View {
        binding = ActMainBinding.inflate(layoutInflater)

        return binding.root

    }

    private fun initClick() {


        binding.mainToolbar.setNavigationOnClickListener {

            binding.mainDrawerLyt.openDrawer(GravityCompat.START)

        }

    }

    interface ClickListeners {
        fun onSaveBtnClick()
    }


}