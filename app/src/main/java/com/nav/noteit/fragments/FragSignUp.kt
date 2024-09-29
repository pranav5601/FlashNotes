package com.nav.noteit.fragments

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.nav.noteit.R
import com.nav.noteit.activities.ActMain
import com.nav.noteit.databinding.FragSignUpBinding
import com.nav.noteit.helper.ApiResultHandler
import com.nav.noteit.helper.OnSpannableClickListener
import com.nav.noteit.helper.Utils
import com.nav.noteit.models.UserReq
import com.nav.noteit.models.UserSignupRes
import com.nav.noteit.remote.local_data.UserPreferences
import com.nav.noteit.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


class FragSignUp : FragBase<FragSignUpBinding>() {


//    private val userViewModel by inject<UserViewModel>()
    private val userDataPrefs by inject<UserPreferences>()


    override fun setUpFrag() {


        initClick()
        observeUserData()
        initLoginSpannable()


    }

    private fun initLoginSpannable() {
        val txtLogin = getString(R.string.already_have_an_account_login_here)
        val loginHere = "Login here."
        Utils.makeTextSpannable(
            binding.tvLoginSpannable,
            txtLogin,
            txtLogin.indexOf(loginHere),
            (txtLogin.indexOf(loginHere) + loginHere.length),
            baseContext,
            object : OnSpannableClickListener {
                override fun onSpannableClick() {
                    parentFragmentManager.popBackStack()
                }

            })
    }

    private fun observeUserData() {
        userViewModel.signUpRes.observe(viewLifecycleOwner) { res ->


            Log.e("res", Gson().toJson(res))

            val apiHandler = ApiResultHandler<UserSignupRes>(baseContext,

                onLoading = {
                    showLoader()
                },
                onSuccess = { data ->

                    if (data?.status == "success") {
                        runBlocking {

                            //store data locally
                            data.userData?.let {
                                userViewModel.writeUserDataToPrefs(data.userData)
                                val mainIntent = Intent(baseContext, ActMain::class.java)
                                startActivity(mainIntent)
                                activity?.finish()
                            } ?: {
                                Utils.showAlertDialog(baseContext, "Something went wrong!!")
                            }
                        }

                    } else {
                        Utils.showAlertDialog(baseContext, data?.message!!)
                    }
                    closeLoader()

                },
                onFailure = {
                    closeLoader()
                    Toast.makeText(baseContext, "${res?.data?.code}", Toast.LENGTH_LONG).show()

                }
            )
            apiHandler.handleApiResult(res)


        }
    }

    private fun initClick() {
        binding.lytRememberMeCheckBox.setOnClickListener {
            binding.checkboxRememberMe.isSelected = !binding.checkboxRememberMe.isSelected
        }

        binding.btnSignUp.setOnClickListener {


            if (validateFields()) {

                val txtEmail = binding.edtEmail.text.toString()
                val txtFullName = binding.edtFullName.text.toString()
                val txtPassword = binding.edtPassword.text.toString()
                val txtCountry = binding.edtCountry.text.toString()

                val userData = UserReq(txtFullName, txtEmail, txtPassword, txtCountry)

                Log.e("userRes", Gson().toJson(userData))


                signUpUser(userData)


            }


        }
    }

    private fun signUpUser(userData: UserReq) {

        userViewModel.signUpUser(userData, baseContext)


    }


    private fun validateFields(): Boolean {
        return if (binding.edtEmail.text == null || binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.isSelected = true
            false
        } else if (!Utils.isValidEmailAddress(binding.edtEmail.text.toString())) {
            binding.edtEmail.isSelected = true
            binding.edtEmail.error = "Please enter valid email id!"
            false
        } else if (binding.edtFullName.text == null || binding.edtFullName.text.toString()
                .isEmpty()
        ) {
            binding.edtFullName.isSelected = true
            binding.edtFullName.error = "Can not keep this empty!"
            false
        } else if (binding.edtPassword.text == null || binding.edtPassword.text.toString()
                .isEmpty()
        ) {
            binding.edtPassword.isSelected = true
            false
        } else if (Utils.isValidPassword(binding.edtPassword.text.toString())) {
            binding.edtPassword.isSelected = true
            Utils.showAlertDialog(
                baseContext,
                "Password should contains al least more than 5 letters, a number, an uppercase, and a special char"
            )
            false
        } else if (binding.edtConfirmPassword.text == null || binding.edtConfirmPassword.text.toString()
                .isEmpty()
        ) {
            binding.edtLytConfirmPassword.editText?.isSelected = true
            binding.edtConfirmPassword.error = "Please rewrite your password!"
            false
        } else if (binding.edtConfirmPassword.text.toString() != binding.edtPassword.text.toString()) {

            Utils.showAlertDialog(baseContext, "Password doesn't match!")
            false
        } else !(binding.edtCountry.text == null || binding.edtCountry.text.toString().isEmpty())

    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragSignUpBinding
        get() = FragSignUpBinding::inflate

}