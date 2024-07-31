package com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Parent
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.mAuth
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _authStateLiveData = MutableLiveData<DataResult<Boolean>>()
    val authStateLiveData: LiveData<DataResult<Boolean>>
        get() = _authStateLiveData


    //fun registerParent(fullName: String, email: String, password: String, noOfChildren: String, confirmPassword: String) = viewModelSc

    fun createParent(
        email: String,
        password: String,
        fullName: String,
        noOfChildren: Int,
        onLoading: (Boolean) -> Unit,
        onAccountCreated: () -> Unit,
        onAccountNotCreated: (String) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) {
            onLoading(true)
        }

        Common.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val newUserId = Common.mAuth.uid
                    //val user = mAuth.currentUser
                    val parentData = Parent(
                        userId = newUserId!!,
                        name = fullName,
                        email = email,
                        noOfChildren = noOfChildren,
                        dateJoined = System.currentTimeMillis().toString()
                    )


                    saveUser(parentData, onLoading, onAccountCreated, onAccountNotCreated)


                } else {
                    it.exception?.message?.let { message ->
                        //hideProgress()
                        onLoading(false)
                        onAccountNotCreated(message)
                        _errorLiveData.value = message
                    }
                }
            }.addOnFailureListener {
                it.message?.let { message ->
                    //hideProgress()
                    onLoading(false)
                    onAccountNotCreated(message)
                    _errorLiveData.value = message
                }
            }
    }

    private fun saveUser(
        parentData: Parent,
        onLoading: (Boolean) -> Unit,
        onParentSaved: () -> Unit,
        onParentNotSaved: (String) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.Main) {
            onLoading(true)
        }

        try {
            Common.parentCollectionRef.document(parentData.userId).set(parentData).await()
            Log.d("TAG", "saveUser: Saved")
            withContext(Dispatchers.Main) {
                onLoading(false)
                onParentSaved()
            }

            //_authStateLiveData.value = DataResult.Success(true)

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onLoading(false)
                onParentNotSaved(e.message ?: "Some error occurred")
            }

            //_authStateLiveData.postValue(DataResult.Failure(e.message ?: "Some error occurred"))
        }
    }

    fun loginUser(email: String, password: String) {

        _authStateLiveData.value = DataResult.Loading
        email.let { mAuth.signInWithEmailAndPassword(it, password) }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _authStateLiveData.value = DataResult.Success(true)

                } else {
                    //pbLoading.visible(false)
                    hideProgress()
                    _authStateLiveData.value =
                        DataResult.Failure(it.exception?.message ?: "Some error occurred")
                    //activity?.toast(it.exception?.message.toString())
                }
            }
    }


}