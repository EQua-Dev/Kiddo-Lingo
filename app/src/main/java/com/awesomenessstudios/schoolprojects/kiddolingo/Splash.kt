package com.awesomenessstudios.schoolprojects.kiddolingo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentStudioSplashBinding

class Splash : Fragment() {

    private var _binding: FragmentStudioSplashBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStudioSplashBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            splashLogo.animate().setDuration(2000).alpha(1f).withEndAction{
                val navToSecondSplash = SplashDirections.actionSplashToAppSplash()
                findNavController().navigate(navToSecondSplash)
               /* val intentToMain = Intent(this, DigitalInkMainActivity::class.java)
                startActivity(intentToMain)*/
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}