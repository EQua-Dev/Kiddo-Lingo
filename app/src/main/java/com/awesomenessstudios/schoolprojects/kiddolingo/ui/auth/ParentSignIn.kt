package com.awesomenessstudios.schoolprojects.kiddolingo.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentParentSignInBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.enable
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.AuthViewModel

class ParentSignIn : Fragment() {

    var _binding: FragmentParentSignInBinding? = null

    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    private lateinit var email: String
    private lateinit var password: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentParentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        with(binding) {
            accountLogInCreateAccount.setOnClickListener {
                val navToSignUp = ParentSignInDirections.actionParentSignInToParentSignUp()
                findNavController().navigate(navToSignUp)
            }

            accountLogInBtnLogin.enable(false)

            signInPassword.addTextChangedListener {
                email = signInEmail.text.toString().trim()
                password = it.toString().trim()
                accountLogInBtnLogin.apply {
                    enable(email.isNotEmpty() && password.isNotEmpty())
                    setOnClickListener {
                        authViewModel.loginUser(email, password)
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
                                        val navToHome =
                                            ParentSignInDirections.actionParentSignInToParentHome()
                                        findNavController().navigate(navToHome)
                                    }
                                }

                            })
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}