package com.awesomenessstudios.schoolprojects.kiddolingo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentAppSplashBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.mAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class AppSplash : Fragment() {

    private var _binding: FragmentAppSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            splashAppLogo.animate().setDuration(2000).alpha(1f).withEndAction {
                //check if user is already signed in

                if (mAuth.currentUser != null) {
                    val navToHome = AppSplashDirections.actionAppSplashToParentHome()
                    findNavController().navigate(navToHome)
                } else {
                    val navToSignIn = AppSplashDirections.actionAppSplashToParentSignIn()
                    findNavController().navigate(navToSignIn)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}