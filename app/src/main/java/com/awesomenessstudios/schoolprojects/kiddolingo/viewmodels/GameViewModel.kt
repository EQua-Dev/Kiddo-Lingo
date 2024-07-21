package com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameScoreDetail
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Word
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GameViewModel : ViewModel() {

    private val _quizWordsLiveData = MutableLiveData<DataResult<List<Word>>>()
    val quizWordsLiveData: LiveData<DataResult<List<Word>>>
        get() = _quizWordsLiveData

    private val _childGameDetailLiveData = MutableLiveData<DataResult<GameScoreDetail>>()
    val childGameDetailLiveData: LiveData<DataResult<GameScoreDetail>>
        get() = _childGameDetailLiveData

    private val _gameDetailsUploadStateLiveData = MutableLiveData<DataResult<Boolean>>()
    val gameDetailsUploadStateLiveData: LiveData<DataResult<Boolean>>
        get() = _gameDetailsUploadStateLiveData


    private val TAG = "GameViewModels"
    fun getWords(category: String, difficulty: String) {
        _quizWordsLiveData.value = DataResult.Loading
        Common.quizCollectionRef.document(category).collection(difficulty).get()
            .addOnCompleteListener {
                val words = mutableListOf<Word>()
                val quizResult = it.result.documents
                for (quiz in quizResult) {
                    val quizWord = quiz.toObject(Word::class.java)
                    words.add(quizWord!!)
                    Log.d(TAG, "getWords: $words")
                }
                _quizWordsLiveData.value = DataResult.Success(words.shuffled())

            }
    }

    fun getChildGameDetailsInfo(childID: String, difficulty: String) {
        _childGameDetailLiveData.value = DataResult.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot =
                    Common.kidCollectionRef.document(childID).collection("GameDetails")
                        .document(difficulty).get().addOnCompleteListener {
                            if (it.result.exists()) {
                                val gameDetail = it.result.toObject(GameScoreDetail::class.java)
                                _childGameDetailLiveData.value = DataResult.Success(gameDetail!!)
                            } else {
                                val newGameDetail = GameScoreDetail()
                                _childGameDetailLiveData.value =
                                    DataResult.Success(newGameDetail)
                            }
                        }//.await()
                        .addOnFailureListener { exception ->
                            _childGameDetailLiveData.postValue(
                                DataResult.Failure(exception.message ?: "Some error occurred")
                            )

                        }
            } catch (e: Exception) {
                _childGameDetailLiveData.postValue(
                    DataResult.Failure(
                        e.message ?: "Some error occurred"
                    )
                )

            }
        }
    }

    fun saveGameDetail(gameDetail: GameScoreDetail, difficulty: String, childID: String) {
        _gameDetailsUploadStateLiveData.value = DataResult.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Common.kidCollectionRef.document(childID).collection("GameDetails")
                    .document(difficulty).set(gameDetail).await()
                _gameDetailsUploadStateLiveData.postValue(DataResult.Success(true))

            } catch (e: Exception) {
                _gameDetailsUploadStateLiveData.postValue(
                    DataResult.Failure(
                        e.message ?: "Some error occurred"
                    )
                )
            }
        }

    }
}