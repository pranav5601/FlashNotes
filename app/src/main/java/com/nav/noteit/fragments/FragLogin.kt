package com.nav.noteit.fragments

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.nav.noteit.R
import com.nav.noteit.activities.ActMain
import com.nav.noteit.databinding.FragLoginBinding
import com.nav.noteit.helper.ApiResultHandler
import com.nav.noteit.helper.OnSpannableClickListener
import com.nav.noteit.helper.Utils
import com.nav.noteit.helper.Utils.isValidEmail
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserSignupRes
import kotlinx.coroutines.runBlocking


class FragLogin : FragBase<FragLoginBinding>() {


//    private val userViewModel: UserViewModel by inject()

    override fun setUpFrag() {

        initClick()
        getUserData()
        initLoginSpannableClick()

    }

    private fun initLoginSpannableClick() {
        val txtSignUpSpan = getString(R.string.don_t_have_an_account_sign_up_here)
        val txtSignUpHere = "Sign up here."
        Utils.makeTextSpannable(
            binding.tvSignUpSpannable,
            txtSignUpSpan,
            txtSignUpSpan.indexOf(txtSignUpHere),
            (txtSignUpSpan.indexOf(txtSignUpHere) + txtSignUpHere.length),
            baseContext,
            object : OnSpannableClickListener {
                override fun onSpannableClick() {
                    Utils.addFrag(
                        baseContext.supportFragmentManager,
                        R.id.loginFragContainer,
                        FragSignUp(),
                        R.anim.slide_in_left,
                        R.anim.slide_out_left,
                        0, 0
                    )
                }

            })
    }

    private fun getUserData() {
        userViewModel.signUpRes.observe(viewLifecycleOwner) { response ->
            val loginApiHandler = ApiResultHandler<UserSignupRes>(baseContext,
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
                    Toast.makeText(baseContext, "${response?.message}", Toast.LENGTH_LONG).show()

                }

            )

            loginApiHandler.handleApiResult(response)

        }
    }

    private fun initClick() {

        binding.lytRememberMeCheckBox.setOnClickListener {
            binding.checkboxRememberMe.isSelected = !binding.checkboxRememberMe.isSelected

            userViewModel.setRememberMe(binding.checkboxRememberMe.isSelected)
        }


        binding.btnLogin.setOnClickListener {

            if (validateFields()) {

                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                userViewModel.loginUser(UserLogin(email, password), baseContext)

            }

        }
    }


    private fun validateFields(): Boolean {
        return if (binding.edtEmail.text == null || binding.edtEmail.text.toString().isEmpty()) {
            binding.edtEmail.isSelected = true
            false
        } else if (!(binding.edtEmail.text.toString()).isValidEmail()) {
            binding.edtEmail.isSelected = true
            binding.edtEmail.error = "Please enter valid email id!"
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
                "Password should contains al least more than 5 letters, a number, an uppercase, and a special character"
            )
            false
        } else {
            true
        }
    }

    override val getBindingLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragLoginBinding
        get() = FragLoginBinding::inflate


}