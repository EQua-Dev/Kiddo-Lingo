package com.awesomenessstudios.schoolprojects.kiddolingo.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentRulesBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameScoreDetail
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.HelperServices.getDifficultyDetailsByTitle
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.getDate

class Rules : Fragment() {

    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!

    private val args: RulesArgs by navArgs()

    private lateinit var parsedDifficulty: String
    private lateinit var parsedChild: Child
    private lateinit var parsedGameDetails: GameScoreDetail

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parsedDifficulty = args.difficulty
        parsedChild = args.child
        parsedGameDetails = args.gameDetails

        val difficultyDetails = getDifficultyDetailsByTitle(parsedDifficulty)

        difficultyDetails?.let { difficulty ->
            with(binding) {
                txtQuizCategoryTitle.text = difficulty.title
                categoryShortDescription.text = difficulty.shortDescription
                categoryLongDescription.text = difficulty.longDescription
                categoryInstruction.text = difficulty.instruction

                lastPlayedScore.text = if (parsedGameDetails.lastScore.isNotEmpty()) {
                    resources.getString(R.string.game_score, parsedGameDetails.lastScore)
                } else {
                    "N/A"
                }
                highScore.text = if (parsedGameDetails.highScore.isNotEmpty()) {
                    resources.getString(R.string.game_score, parsedGameDetails.highScore)
                } else {
                    "N/A"
                }
                lastPlayedDate.text = if (parsedGameDetails.lastPlayed.isNotEmpty()) {
                    getDate(parsedGameDetails.lastPlayed.toLong(), "EEE, dd MMMM yyy | hh:mm a")
                } else {
                    "N/A"
                }

                when (difficulty.color) {
                    "green" -> {
                        cardRules.setCardBackgroundColor(resources.getColor(R.color.green))
                        startQuizBtn.setBackgroundColor(resources.getColor(R.color.green))
                    }

                    "orange" -> {
                        cardRules.setCardBackgroundColor(resources.getColor(R.color.orange))
                        startQuizBtn.setBackgroundColor(resources.getColor(R.color.orange))
                    }

                    "red" -> {
                        cardRules.setCardBackgroundColor(resources.getColor(R.color.crimson))
                        startQuizBtn.setBackgroundColor(resources.getColor(R.color.crimson))
                    }
                }

                startQuizBtn.setOnClickListener {
                    val navToGame = RulesDirections.actionRulesToDigitalInkMainActivity(
                        parsedDifficulty,
                        parsedChild
                    )
                    findNavController().navigate(navToGame)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}