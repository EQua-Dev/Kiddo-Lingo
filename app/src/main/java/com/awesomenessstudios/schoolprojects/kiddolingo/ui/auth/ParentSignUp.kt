package com.awesomenessstudios.schoolprojects.kiddolingo.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentParentSignUpBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.google.android.material.textfield.TextInputEditText
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.isPasswordValid
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.AuthViewModel

class ParentSignUp : Fragment() {

    private var _binding: FragmentParentSignUpBinding? = null

    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel


    private lateinit var fullName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String
    private var noOfChildren: Int = 1

    private var fullNameOkay = false
    private var emailOkay = false
    private var passwordOkay = false
    private var confirmPasswordOkay = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentParentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        with(binding){
            txtNoOfChildren.text = noOfChildren.toString()
            createAccountLogIn.setOnClickListener {
                val navToSignIn = ParentSignUpDirections.actionParentSignUpToParentSignIn()
                findNavController().navigate(navToSignIn)
            }

            signUpUsername.setOnFocusChangeListener { _, hasFocus ->
                fullName = signUpUsername.text.toString().trim()
                if (!hasFocus){
                    if (fullName.isEmpty()){
                        textInputLayoutSignUpUsername.error = resources.getString(R.string.fullname_error)
                    }else{
                        fullNameOkay = true
                        textInputLayoutSignUpUsername.error = null
                    }
                }
            }

            signUpEmail.setOnFocusChangeListener { v, hasFocus ->
                val emailLayout = v as TextInputEditText
                email = emailLayout.text.toString().trim()
                if (!hasFocus) {
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        textInputLayoutSignUpEmail.error =
                            resources.getString(R.string.email_invalid)
                    } else {
                        emailOkay = true
                        binding.textInputLayoutSignUpEmail.error = null
                    }
                }
            }

            btnMinusNoOfChildren.setOnClickListener {
                if (noOfChildren >= 1 && noOfChildren != 1){
                    noOfChildren -= 1
                    txtNoOfChildren.text = noOfChildren.toString()
                }
            }

            btnPlusNoOfChildren.setOnClickListener {
                if (noOfChildren == 3){
                    requireContext().toast("Max of 3 children allowed")
                }else{
                    noOfChildren += 1
                    txtNoOfChildren.text = noOfChildren.toString()
                }
            }

            signUpPassword.setOnFocusChangeListener { v, hasFocus ->
                val passwordLayout = v as TextInputEditText
                password = passwordLayout.text.toString().trim()
                if (!hasFocus) {
                    if (isPasswordValid(password)) {
                        textInputLayoutSignUpPassword.error =
                            resources.getString(R.string.invalid_password)
                    } else {
                        passwordOkay = true
                        textInputLayoutSignUpPassword.error = null
                    }
                }
            }
            signUpConfirmPassword.setOnFocusChangeListener { v, hasFocus ->
                val confirmPasswordLayout = v as TextInputEditText
                confirmPassword = confirmPasswordLayout.text.toString().trim()
                if (!hasFocus) {
                    if (confirmPassword != password) {
                        textInputLayoutSignUpConfirmPassword.error =
                            resources.getString(R.string.invalid_confirm_password)
                    } else {
                        confirmPasswordOkay = true
                        textInputLayoutSignUpConfirmPassword.error = null
                    }
                }
            }

            accountSignUpButton.setOnClickListener {
                if (fullNameOkay &&
                    emailOkay &&
                    passwordOkay &&
                    confirmPasswordOkay){
                    fullName = signUpUsername.text.toString().trim()
                    email = signUpEmail.text.toString().trim()

                    authViewModel.createParent(email, password, fullName, noOfChildren)
                    authViewModel.authStateLiveData.observe(
                        viewLifecycleOwner,
                        Observer { response ->
                            when (response) {
                                is DataResult.Loading -> {
                                    requireContext().showProgress()
                                }

                                is DataResult.Failure -> {
                                    hideProgress()
                                    requireContext().toast(response.error)
                                }

                                is DataResult.Success -> {
                                    hideProgress()
                                    val navToAddKids = ParentSignUpDirections.actionParentSignUpToAddKids(noOfChildren)
                                    findNavController().navigate(navToAddKids)
                                }
                            }

                        })

                    //createUser(email, password)
                }
            }

        }
    }

    /*private fun createUser(email: String, password: String) {

        requireContext().showProgress()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val newUserId = mAuth.uid
                    //val user = mAuth.currentUser
                    val parentData = Parent(
                        userId = newUserId!!,
                        name = fullName,
                        email = email,
                        noOfChildren = noOfChildren,
                        dateJoined = System.currentTimeMillis().toString()
                    )
                    saveUser(parentData)

                } else {
                    it.exception?.message?.let { message ->
                        hideProgress()
                        requireActivity().toast(message)
                    }
                }
            }.addOnFailureListener {
                it.message?.let { message ->
                    hideProgress()
                    requireActivity().toast(message)
                }
            }
    }*/

    /*private fun saveUser(parentData: Parent) = CoroutineScope(Dispatchers.IO).launch {
        try {
            parentCollectionRef.document(parentData.userId).set(parentData).await()
            hideProgress()
            val navToAddKids = ParentSignUpDirections.actionParentSignUpToAddKids(noOfChildren)
            findNavController().navigate(navToAddKids)
        }catch (e: Exception){
            withContext(Dispatchers.Main){
                requireContext().toast(e.message.toString())
            }
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}