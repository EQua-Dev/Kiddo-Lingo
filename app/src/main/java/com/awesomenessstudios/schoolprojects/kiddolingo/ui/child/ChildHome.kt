package com.awesomenessstudios.schoolprojects.kiddolingo.ui.child

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentChildHomeBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameScoreDetail
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.HelperServices.getCategoryDetailsById
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.ChildViewModel

class ChildHome : Fragment() {

    private var _binding: FragmentChildHomeBinding? = null
    private val binding get() = _binding!!

    val args: ChildHomeArgs by navArgs()

    private lateinit var childViewModel: ChildViewModel

    private lateinit var gameDetails: Map<String, GameScoreDetail>


    private lateinit var child: Child
    val listOfDifficulties = listOf("Easy", "Medium", "Hard")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChildHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.homeCustomToolBar)

        child = args.child
        childViewModel = ViewModelProvider(requireActivity()).get(ChildViewModel::class.java)

        childViewModel.getChildGameInfo(childID = child.id, listOfDifficulties, { gameDetailsMap ->
            Log.d("TAG", "onViewCreated: $gameDetailsMap")
            // Handle the successfully fetched game details map
            gameDetails = gameDetailsMap
            gameDetailsMap.forEach { (difficulty, gameDetail) ->
                // Process each game detail for the corresponding difficulty
                when (difficulty) {
                    listOfDifficulties[0] -> {
                        binding.easyHighScore.text = gameDetail.highScore.ifEmpty { "N/A" }
                        binding.easyLeaderBoard.text = gameDetail.lastScore.ifEmpty { "N/A" }
                    }

                    listOfDifficulties[1] -> {
                        binding.mediumHighScore.text = gameDetail.highScore.ifEmpty { "N/A" }
                        binding.mediumLeaderBoard.text = gameDetail.lastScore.ifEmpty { "N/A" }
                    }

                    listOfDifficulties[2] -> {
                        binding.hardHighScore.text = gameDetail.highScore.ifEmpty { "N/A" }
                        binding.hardLeaderBoard.text = gameDetail.lastScore.ifEmpty { "N/A" }
                    }
                }
            }
        }, { errorMessage ->
            // Handle the error
            Log.e("TAG", errorMessage)
        })

        with(binding) {
            childName.text = child.name

            val categoryOfChild = getCategoryDetailsById(child.ageGroup.toInt())

            categoryOfChild?.let { category ->
                childCategory.text = category.name
                childCategoryDescription.text = category.description
                childCategoryRange.text = "(${category.ageGroup})"
            }

            setCardClicks()
        }

    }

    private fun setCardClicks() {

        with(binding) {
            homeCardEasy.setOnClickListener {
                val navToRules =
                    ChildHomeDirections.actionChildHomeToRules("Easy", child, gameDetails["Easy"]!!)
                findNavController().navigate(navToRules)
            }
            homeCardMedium.setOnClickListener {
                val navToRules = ChildHomeDirections.actionChildHomeToRules(
                    "Medium",
                    child,
                    gameDetails["Medium"]!!
                )
                findNavController().navigate(navToRules)
            }
            homeCardHard.setOnClickListener {
                val navToRules =
                    ChildHomeDirections.actionChildHomeToRules("Hard", child, gameDetails["Hard"]!!)
                findNavController().navigate(navToRules)
            }
            homeRandomDifficultyBtn.setOnClickListener {
                var highestListNumber = listOfDifficulties.size
                //var randomText: String

                if (!gameDetails.containsKey("Easy")) {
                    val randomText = "Easy"
                    val navToRules = ChildHomeDirections.actionChildHomeToRules(
                        randomText,
                        child,
                        gameDetails[randomText]!!
                    )
                    findNavController().navigate(navToRules)
                }
                if (!gameDetails.containsKey("Medium")) {
                    highestListNumber = 1
                    val randomIndex = (0..<highestListNumber).random()
                    val randomText = listOfDifficulties[randomIndex]
                    Log.d("TAG", "setCardClicks: highestListNumber $highestListNumber")
                    val navToRules = ChildHomeDirections.actionChildHomeToRules(
                        randomText,
                        child,
                        gameDetails[randomText]!!
                    )
                    findNavController().navigate(navToRules)
                } else {
                    val randomIndex = (0..<highestListNumber).random()
                    val randomText = listOfDifficulties[randomIndex]
                    Log.d("TAG", "setCardClicks: highestListNumber $highestListNumber")
                    val navToRules = ChildHomeDirections.actionChildHomeToRules(
                        randomText,
                        child,
                        gameDetails[randomText]!!
                    )
                    findNavController().navigate(navToRules)
                }

                // Use the randomText variable as needed

            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                Common.mAuth.signOut()
                val navToStart = ChildHomeDirections.actionChildHomeToParentSignIn()
                findNavController().navigate(navToStart)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}