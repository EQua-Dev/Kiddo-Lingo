package com.awesomenessstudios.schoolprojects.kiddolingo.ui.game

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentGameBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameDoneDetailsToParse
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Word
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSortedSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.awesomenessstudios.schoolprojects.kiddolingo.DriverDetails
import com.awesomenessstudios.schoolprojects.kiddolingo.StrokeManager
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.CATEGORY1
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.CATEGORY2
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.CATEGORY3
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.CATEGORY4
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.CATEGORY5
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.enable
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/** Main activity which creates a StrokeManager and connects it to the DrawingView.*/

class Game : Fragment(), StrokeManager.DownloadedModelsChangedListener,
    TextToSpeech.OnInitListener {

    private var _binding: FragmentGameBinding? = null

    private val binding get() = _binding!!

    private lateinit var gameViewModel: GameViewModel

    private lateinit var difficulty: String
    private lateinit var child: Child

    private lateinit var category: String

    private var correctMediaPlayer: MediaPlayer? = null
    private var wrongMediaPlayer: MediaPlayer? = null

    private val args: GameArgs by navArgs()

    private var playerScore = 0
    private var wordsIndex = 0
    private var wordsSpoken: Int = 1

    private val correctWords = mutableListOf<Word>()
    private val wrongWords = mutableListOf<Word>()


    @JvmField
    @VisibleForTesting
    val strokeManager = StrokeManager()

    private lateinit var textToSpeech: TextToSpeech

    private var timer: CountDownTimer? = null


    //    val recognitionTask = RecognitionTask()
    private lateinit var languageAdapter: ArrayAdapter<ModelLanguageContainer>
    val fireStore = FirebaseFirestore.getInstance()
    lateinit var driverDetails: DriverDetails

    var plateNumber: String? = ""


    enum class TextState {
        STATE_EMPTY,
        STATE_CHANGED
    }

    private var fetchedWords = listOf<Word>()
    private lateinit var wordToSpeak: Word

    //lateinit var checkBtn: Button
    private var progressDialog: Dialog? = null

    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<CardView>


    private val REQUEST_RECOGNIZE = 3782
    val lang = "en-NG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        difficulty = args.difficulty
        child = args.child
        
        strokeManager.refreshDownloadedModelsStatus()

        textToSpeech = TextToSpeech(requireContext(), this)


        category = when (child.ageGroup) {
            "5" -> {
                CATEGORY1
            }

            "8" -> {
                CATEGORY2
            }

            "11" -> {
                CATEGORY3
            }

            "14" -> {
                CATEGORY4
            }

            "17" -> {
                CATEGORY5
            }

            else -> {
                ""
            }
        }

        correctMediaPlayer = MediaPlayer.create(requireContext(), R.raw.correct_answer)
        wrongMediaPlayer = MediaPlayer.create(requireContext(), R.raw.wrong_answer)

        gameViewModel = ViewModelProvider(requireActivity()).get(GameViewModel::class.java)
        gameViewModel.getWords(category, difficulty)
        gameViewModel.quizWordsLiveData.observe(viewLifecycleOwner, Observer { response ->
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
                    fetchedWords = response.value
                    //pronounceWord()
                    binding.txtWordsCounter.text = resources.getString(
                        R.string.word_counter,
                        wordsSpoken.toString(),
                        fetchedWords.size.toString()
                    )


                    with(binding) {
                        
                        speakWord.setOnClickListener {
                            pronounceWord()
                        }
                        clearButton.setOnClickListener {
                            clearClick()
                        }
                        drawingView.setStrokeManager(strokeManager)
                        statusTextView.setStrokeManager(strokeManager)

                        strokeManager.setStatusChangedListener(statusTextView)
                        plateNumber = strokeManager.textRetrieved
                        strokeManager.setContentChangedListener(drawingView)
                        strokeManager.setDownloadedModelsChangedListener(this@Game)
                        strokeManager.setClearCurrentInkAfterRecognition(true)
                        strokeManager.setTriggerRecognitionAfterInput(false)



                        strokeManager.setActiveModel(lang)
                        strokeManager.reset()
                        // Start countdown timer (replace with your desired logic)

                    }


                }
            }

        })


    }

//  fun downloadClick(v: View?) {
//    strokeManager.download()
//  }

    private fun pronounceWord() {

        //val random = Random()
        //val wordIndex = random.nextInt(fetchedWords.size)
        wordToSpeak = fetchedWords[wordsIndex]
        //binding.txtWordsCounter.text = resources.getString(R.string.word_counter, wordsSpoken.toString(), fetchedWords.size.toString())

        Log.d(TAG, "pronounceWord: ${wordToSpeak.word.lowercase()}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(wordToSpeak.word.lowercase(), TextToSpeech.QUEUE_FLUSH, null, null)
            startCountdown(60000) // Replace 60 with your desired duration in seconds
            with(binding) {
                txtHintDescription.apply {
                    enable(true)
                    setOnClickListener {
                        txtWordDescription.text = wordToSpeak.definition
                    }
                }
                recognizeButton.setOnClickListener {
                    recognizeClick(it)
                }
            }
        }

    }


    private fun startCountdown(seconds: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(seconds, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                binding.txtCountdown.text = String.format("%02d", remainingSeconds)
                val alpha = 1.0f - (remainingSeconds.toFloat() / seconds.toFloat())
                binding.pulsatingCircle.alpha = alpha
                // Update pulsating circle animation (implementation needed)
                // You can use an animation library or custom logic to change the circle's opacity or other properties based on remaining time.
            }

            override fun onFinish() {
                requireContext().toast("Time Up")
                if (wordsSpoken == fetchedWords.size) {

                    //set up the game detail object
                    val gameDetails = GameDoneDetailsToParse(
                        childId = child.id,
                        difficulty = difficulty,
                        totalScore = playerScore.toString(),
                        totalWords = fetchedWords.size.toString(),
                        correctWords = correctWords,
                        wrongWords = wrongWords
                    )
                    val navToFinish =
                        GameDirections.actionDigitalInkMainActivityToQuizDone(gameDetails)
                    findNavController().navigate(navToFinish)

                } else {
                    wordsIndex += 1
                    wordsSpoken += 1
                    binding.txtWordsCounter.text = resources.getString(
                        R.string.word_counter,
                        wordsSpoken.toString(),
                        fetchedWords.size.toString()
                    )
                    clearClick()
                    pronounceWord()
                }
                // Handle timer completion (optional)
            }
        }.start()

    }

    private fun recognizeClick(v: View?) {
        requireContext().showProgress()
        v!!.enable(false)
        strokeManager.recognize().addOnCompleteListener {
            Log.d(TAG, "recognizeClick.CommonRPN: ${Common.recognizedText}")
            v.enable(true)
            //checkBtn.enable(true)
            performSearch(Common.recognizedText.lowercase())
        }
    }

    private fun performSearch(recognizedText: String) = CoroutineScope(Dispatchers.IO).launch {
        Log.d(TAG, "word index: $wordsIndex of ${fetchedWords.size}")
        withContext(Dispatchers.Main) {
            if (recognizedText == wordToSpeak.word.lowercase()) {
                correctMediaPlayer?.start()
                playerScore += wordToSpeak.score.toInt()
                binding.txtScore.text =
                    resources.getString(R.string.player_score, playerScore.toString())
                requireContext().toast("Correct!")
                correctWords.add(wordToSpeak)
                hideProgress()
                binding.recognizeButton.apply {
                    text = if (wordsSpoken == fetchedWords.size) {
                        resources.getString(R.string.finish)
                    } else {
                        resources.getString(R.string.next)
                    }
                    setOnClickListener {
                        if (wordsSpoken == fetchedWords.size) {
                            timer?.cancel()

                            //set up the game detail object
                            val gameDetails = GameDoneDetailsToParse(
                                childId = child.id,
                                difficulty = difficulty,
                                totalScore = playerScore.toString(),
                                totalWords = fetchedWords.size.toString(),
                                correctWords = correctWords,
                                wrongWords = wrongWords
                            )
                            val navToFinish =
                                GameDirections.actionDigitalInkMainActivityToQuizDone(gameDetails)
                            findNavController().navigate(navToFinish)

                        } else {
                            wordsIndex += 1
                            wordsSpoken += 1
                                binding.txtWordsCounter.text = resources.getString(
                                    R.string.word_counter,
                                    wordsSpoken.toString(),
                                    fetchedWords.size.toString()
                                )
                                clearClick()
                                text = resources.getString(R.string.submit)
                                pronounceWord()
                        }
                    }
                }
                //update score here and then set to the new word


            } else {
                wrongMediaPlayer?.start()
                Log.d(TAG, "performSearch: wrong!")
                requireContext().toast("Wrong!")
                wrongWords.add(wordToSpeak)
                hideProgress()
                binding.txtWordDescription.text = resources.getString(R.string.correct_word, wordToSpeak.word.lowercase())
                binding.recognizeButton.apply {
                    text = if (wordsSpoken == fetchedWords.size) {
                        resources.getString(R.string.finish)
                    } else {
                        resources.getString(R.string.next)
                    }
                    setOnClickListener {
                        if (wordsSpoken == fetchedWords.size){
                            timer?.cancel()

                            //set up the game detail object
                            val gameDetails = GameDoneDetailsToParse(
                                childId = child.id,
                                difficulty = difficulty,
                                totalScore = playerScore.toString(),
                                totalWords = fetchedWords.size.toString(),
                                correctWords = correctWords,
                                wrongWords = wrongWords
                            )
                            val navToFinish = GameDirections.actionDigitalInkMainActivityToQuizDone(gameDetails)
                            findNavController().navigate(navToFinish)
                        }else{
                            setOnClickListener {
                                //wrongMediaPlayer?.stop()
                                wordsIndex += 1
                                wordsSpoken += 1
                                binding.txtWordsCounter.text = resources.getString(
                                    R.string.word_counter,
                                    wordsSpoken.toString(),
                                    fetchedWords.size.toString()
                                )
                                clearClick()
                                text = resources.getString(R.string.submit)
                                pronounceWord()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun clearClick() {
        strokeManager.reset()
        binding.drawingView.clear()
    }

    fun deleteClick(v: View?) {
        strokeManager.deleteActiveModel()
    }

    fun checkClick(v: View?) {
        performSearch(Common.recognizedText)
        requireContext().toast(Common.recognizedText)
    }


    private class ModelLanguageContainer
    private constructor(private val label: String, val languageTag: String?) :
        Comparable<ModelLanguageContainer> {

        var downloaded: Boolean = false

        override fun toString(): String {
            return when {
                languageTag == null -> label
                downloaded -> "   [D] $label"
                else -> "   $label"
            }
        }

        override fun compareTo(other: ModelLanguageContainer): Int {
            return label.compareTo(other.label)
        }

        companion object {
            /** Populates and returns a real model identifier, with label and language tag.*/

            fun createModelContainer(label: String, languageTag: String?): ModelLanguageContainer {
                // Offset the actual language labels for better readability
                return ModelLanguageContainer(label, languageTag)
            }

            /** Populates and returns a label only, without a language tag.*/

            fun createLabelOnly(label: String): ModelLanguageContainer {
                return ModelLanguageContainer(label, null)
            }
        }
    }

    private fun populateLanguageAdapter(): ArrayAdapter<ModelLanguageContainer> {
        val languageAdapter =
            ArrayAdapter<ModelLanguageContainer>(
                requireContext(),
                android.R.layout.simple_spinner_item
            )
        languageAdapter.add(ModelLanguageContainer.createLabelOnly("Select language"))
        languageAdapter.add(ModelLanguageContainer.createLabelOnly("Non-text Models"))

        // Manually add non-text models first
        for (languageTag in NON_TEXT_MODELS.keys) {
            languageAdapter.add(
                ModelLanguageContainer.createModelContainer(
                    NON_TEXT_MODELS[languageTag]!!,
                    languageTag
                )
            )
        }
        languageAdapter.add(ModelLanguageContainer.createLabelOnly("Text Models"))
        val textModels = ImmutableSortedSet.naturalOrder<ModelLanguageContainer>()
        for (modelIdentifier in DigitalInkRecognitionModelIdentifier.allModelIdentifiers()) {
            if (NON_TEXT_MODELS.containsKey(modelIdentifier.languageTag)) {
                continue
            }
            val label = StringBuilder()
            label.append(Locale(modelIdentifier.languageSubtag).displayName)
            if (modelIdentifier.regionSubtag != null) {
                label.append(" (").append(modelIdentifier.regionSubtag).append(")")
            }
            if (modelIdentifier.scriptSubtag != null) {
                label.append(", ").append(modelIdentifier.scriptSubtag).append(" Script")
            }
            textModels.add(
                ModelLanguageContainer.createModelContainer(
                    label.toString(),
                    modelIdentifier.languageTag
                )
            )
        }
        languageAdapter.addAll(textModels.build())
        return languageAdapter
    }

    override fun onDownloadedModelsChanged(downloadedLanguageTags: Set<String>) {
//        for (i in 0 until languageAdapter.count) {
//            val container = languageAdapter.getItem(i)!!
//            container.downloaded = downloadedLanguageTags.contains(container.languageTag)
//        }
//        languageAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        strokeManager.download()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
        timer?.cancel()
        correctMediaPlayer?.release()
        correctMediaPlayer = null
        wrongMediaPlayer?.release()
        wrongMediaPlayer = null

    }


    companion object {
        private const val TAG = "MLKDI.Activity"
        private val NON_TEXT_MODELS =
            ImmutableMap.of(
                "zxx-Zsym-x-autodraw",
                "Autodraw",
                "zxx-Zsye-x-emoji",
                "Emoji",
                "zxx-Zsym-x-shapes",
                "Shapes"
            )
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                requireContext().toast("Language data missing or language not supported")
                // Language data is missing or the language is not supported.
                // Handle this scenario as needed.
            }
        } else {
            // Initialization failed. Handle this scenario as needed.
            requireContext().toast("initialisation failed")
        }
    }
}
