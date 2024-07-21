package com.awesomenessstudios.schoolprojects.kiddolingo.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentQuizDoneBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameDoneDetailsToParse
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameScoreDetail
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.GameViewModel

class QuizDone : Fragment() {

    private var _binding: FragmentQuizDoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var gameViewModel: GameViewModel

    private lateinit var childGameDetails: GameScoreDetail

    private val args: QuizDoneArgs by navArgs()
    private lateinit var gameDetails: GameDoneDetailsToParse

    private val TAG = "QuizDone"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameDetails = args.gameDetails
        with(binding) {


            gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
            gameViewModel.getChildGameDetailsInfo(gameDetails.childId, gameDetails.difficulty)
            gameViewModel.childGameDetailLiveData.observe(viewLifecycleOwner, Observer { response ->
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
                        childGameDetails = response.value

                        val correctWords = mutableListOf<String>()
                        val wrongWords = mutableListOf<String>()
                        gameDetails.correctWords.forEach { word ->
                            correctWords.add(word.word)
                        }
                        gameDetails.wrongWords.forEach { word ->
                            wrongWords.add(word.word)
                        }


                        childCorrectWords.text = correctWords.joinToString(",")
                        childWrongWords.text = wrongWords.joinToString(",")
                        childTotalNoWords.text =
                            resources.getString(R.string.total_words_text, gameDetails.totalWords)
                        childTotalScore.text =
                            resources.getString(R.string.points_text, gameDetails.totalScore)
                        childDifficulty.text = gameDetails.difficulty
                        childHighScore.text =
                            if (gameDetails.totalScore.toInt() > childGameDetails.highScore.toInt()) {
                                resources.getString(R.string.new_high_score, gameDetails.totalScore)
                            } else {
                                childGameDetails.highScore
                            }
                        gameOverOkayButton.setOnClickListener {
                            //write new score to cloud
                            val gameScoreDetail = GameScoreDetail(
                                lastPlayed = System.currentTimeMillis().toString(),
                                highScore = if (gameDetails.totalScore.toInt() > childGameDetails.highScore.toInt()) {
                                    gameDetails.totalScore
                                } else {
                                    childGameDetails.highScore
                                },
                                lastScore = gameDetails.totalScore
                            )

                            saveGameDetails(gameScoreDetail)


                        }

                    }
                }
            })

        }
    }

    private fun saveGameDetails(gameScoreDetail: GameScoreDetail) {

        gameViewModel.saveGameDetail(gameScoreDetail, gameDetails.difficulty, gameDetails.childId)

        gameViewModel.gameDetailsUploadStateLiveData.observe(
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
                        val navBackToHome = QuizDoneDirections.actionQuizDoneToParentHome()
                        findNavController().navigate(navBackToHome)
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}