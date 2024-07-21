package com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Parent
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.kidCollectionRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _parentInfoStateLiveData = MutableLiveData<DataResult<Parent>>()
    val parentInfoStateLiveData: LiveData<DataResult<Parent>>
        get() = _parentInfoStateLiveData

    private val _parentChildrenLiveData = MutableLiveData<DataResult<List<Child>>>()
    val parentChildrenLiveData: LiveData<DataResult<List<Child>>>
        get() = _parentChildrenLiveData

    private val TAG = "HomeViewModel"

    fun getParentInfo(parentID: String)/*: Parent? */ {
        _parentInfoStateLiveData.value = DataResult.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot =
                    Common.parentCollectionRef.document(parentID).get().addOnCompleteListener {
                        if (it.result.exists()) {
                            val parentInfo = it.result.toObject(Parent::class.java)
                            _parentInfoStateLiveData.value = DataResult.Success(parentInfo!!)
                        } else {
                            _parentInfoStateLiveData.value = DataResult.Failure("Parent not found")
                        }
                    }//.await()
                        .addOnFailureListener { exception ->
                            _parentInfoStateLiveData.value = DataResult.Failure(exception.message ?: "Some error occurred")

                        }
            } catch (e: Exception) {
                _parentInfoStateLiveData.value = DataResult.Failure(e.message ?: "Some error occurred")
            }
        }

        //hideProgress()

        //return runBlocking { deferred.await() }
    }

    fun getParentChildren(loggedInParentId: String){
        val childrenQuery = kidCollectionRef.whereEqualTo("parentID", loggedInParentId)

        _parentChildrenLiveData.value = DataResult.Loading

        childrenQuery.get()?.addOnSuccessListener {querySnapshot ->
            val listOfChildren = mutableListOf<Child>()
            for (document in querySnapshot.documents){
                val child = document.toObject(Child::class.java)
                child?.let {
                    listOfChildren.add(it)
                }
            }
            _parentChildrenLiveData.value = DataResult.Success(listOfChildren)
        }?.addOnFailureListener { exception ->
            _parentChildrenLiveData.value = DataResult.Failure(exception.message ?: "Some error occurred")
        }
    }


}