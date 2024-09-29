package com.nav.noteit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nav.noteit.activities.ActBase
import com.nav.noteit.activities.ActLogin
import com.nav.noteit.activities.ActMain
import com.nav.noteit.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject


abstract class FragBase<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    val binding: T get() = _binding ?: error("error")

    val userId: String = ""
    val userViewModel: UserViewModel by inject()

    val noteReminderCoroutineScope = Dispatchers.IO + SupervisorJob()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = getBindingLayout(inflater, container, false)

        return binding.root
    }

    fun changeIconToBack(isShow: Boolean) {

        if (baseContext is ActMain) {
            (baseContext as? ActMain)?.changeIconToBack(isShow)
        }

    }



    fun selectMenuDrawer() {

        if (baseContext is ActMain) {
            (baseContext as? ActMain)?.selectMenuDrawer()
        }

    }
    fun changeToSaveIcon(isShow: Boolean, clickListeners: ActMain.ClickListeners) {

        if (baseContext is ActMain) {
            (baseContext as? ActMain)?.changeToSaveIcon(isShow,clickListeners)
        }

    }

    fun getUserIdFromPrefs():String? = userViewModel.userId.value





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getUserId()

        setUpFrag()


    }

    fun showLoader(){
        if(baseContext is ActMain){
            (baseContext as? ActMain)?.showLoader()
        }
        if(baseContext is ActLogin){
            (baseContext as? ActLogin)?.showLoader()
        }
    }
    fun closeLoader(){
        if(baseContext is ActMain){
            (baseContext as? ActMain)?.closeLoader()
        }
        if(baseContext is ActLogin){
            (baseContext as? ActLogin)?.closeLoader()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    lateinit var baseContext: ActBase
    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = ((context as? ActBase)!!)
    }

    abstract fun setUpFrag()
    abstract val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> T
}