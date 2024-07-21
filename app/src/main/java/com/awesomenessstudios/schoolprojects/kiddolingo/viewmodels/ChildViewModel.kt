package com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.models.GameScoreDetail
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.kidCollectionRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChildViewModel : ViewModel() {

    private val _childGameInfoStateLiveData = MutableLiveData<DataResult<GameScoreDetail>>()
    val childGameInfoStateLiveData: LiveData<DataResult<GameScoreDetail>>
        get() = _childGameInfoStateLiveData

    private val _parentChildrenLiveData = MutableLiveData<DataResult<List<Child>>>()
    val parentChildrenLiveData: LiveData<DataResult<List<Child>>>
        get() = _parentChildrenLiveData

    private val TAG = "HomeViewModel"

    fun getChildGameInfo(
        childID: String,
        difficulties: List<String>,
        onDetailsFetched: (Map<String, GameScoreDetail>) -> Unit,
        onDetailsNotFetched: (String) -> Unit
    ) {
        _childGameInfoStateLiveData.value = DataResult.Loading

        Log.d(TAG, "getChildGameInfo: called")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gameDetailsMap = mutableMapOf<String, GameScoreDetail>()
                difficulties.forEach { difficulty ->
                    val documentSnapshot = Common.kidCollectionRef.document(childID)
                        .collection("GameDetails").document(difficulty).get().await()

                    if (documentSnapshot.exists()) {
                        val childGameInfo = documentSnapshot.toObject(GameScoreDetail::class.java)
                        if (childGameInfo != null) //{
                            gameDetailsMap[difficulty] = childGameInfo
                        } else {
                            gameDetailsMap[difficulty] = GameScoreDetail()

                    }
                        /*
                        gameDetailsMap[difficulty]
                        onDetailsNotFetched("Game details not found for difficulty: $difficulty")
                        _childGameInfoStateLiveData.postValue(
                            DataResult.Failure("Game details not found for difficulty: $difficulty")
                        )
                        return@launch
                    }*/
                 /*   } else {
                        onDetailsNotFetched("Game details not found for difficulty: $difficulty")
                        _childGameInfoStateLiveData.postValue(
                            DataResult.Failure("Game details not found for difficulty: $difficulty")
                        )
                        return@launch
                    }*/
                }
//                for (difficulty in difficulties) {
//
//                }
                onDetailsFetched(gameDetailsMap)
                Log.d(TAG, "getChildGameInfo: $gameDetailsMap")
                //_childGameInfoStateLiveData.postValue(DataResult.Success(gameDetailsMap))
            } catch (e: Exception) {
                onDetailsNotFetched(e.message ?: "Some error occurred")
                _childGameInfoStateLiveData.postValue(
                    DataResult.Failure(e.message ?: "Some error occurred")
                )
            }
        }
    }

    fun getParentChildren(loggedInParentId: String) {
        val childrenQuery = kidCollectionRef.whereEqualTo("parentID", loggedInParentId)

        _parentChildrenLiveData.value = DataResult.Loading

        childrenQuery.get()?.addOnSuccessListener { querySnapshot ->
            val listOfChildren = mutableListOf<Child>()
            for (document in querySnapshot.documents) {
                val child = document.toObject(Child::class.java)
                child?.let {
                    listOfChildren.add(it)
                }
            }
            _parentChildrenLiveData.value = DataResult.Success(listOfChildren)
        }?.addOnFailureListener { exception ->
            _parentChildrenLiveData.value =
                DataResult.Failure(exception.message ?: "Some error occurred")
        }
    }


}