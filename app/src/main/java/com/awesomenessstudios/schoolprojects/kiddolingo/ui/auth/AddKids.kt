package com.awesomenessstudios.schoolprojects.kiddolingo.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentAddKidsBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.kidCollectionRef
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.mAuth
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.HelperServices.whatAgeCategoryIsThisChild
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class AddKids : Fragment() {

    private var _binding: FragmentAddKidsBinding? = null
    private val binding get() = _binding!!

    val args: AddKidsArgs by navArgs()

    var noOfChildren by Delegates.notNull<Int>()
    lateinit var nameOfChild1: String
    lateinit var ageOfChild1: String
    lateinit var nameOfChild2: String
    lateinit var ageOfChild2: String
    lateinit var nameOfChild3: String
    lateinit var ageOfChild3: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddKidsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noOfChildren = args.noOfChildren
        with(binding) {

            val twoChildren = noOfChildren > 1
            val threeChildren = noOfChildren > 2

            var child1NameOkay = false
            var child1AgeOkay = false

            var child2NameOkay = false
            var child2AgeOkay = false

            var child3NameOkay = false
            var child3AgeOkay = false


            val childAge = resources.getStringArray(R.array.children_age)
            val childAgesAdapter =
                ArrayAdapter(requireContext(), R.layout.drop_down_item, childAge)

            //child 1
            child1Age.setAdapter(childAgesAdapter)
            child1Name.setOnFocusChangeListener { _, hasFocus ->
                nameOfChild1 = child1Name.text.toString().trim()
                if (!hasFocus) {
                    if (nameOfChild1.isEmpty()) {
                        textInputLayoutChild1Name.error =
                            resources.getString(R.string.child_name_error)
                    } else {
                        child1NameOkay = true
                        textInputLayoutChild1Name.error = null
                    }
                }
            }

            //child 2
            if (twoChildren) {
                child2Card.visible(true)
                child2Age.setAdapter(childAgesAdapter)

                child2Name.setOnFocusChangeListener { _, hasFocus ->
                    nameOfChild2 = child2Name.text.toString().trim()
                    if (!hasFocus) {
                        if (nameOfChild2.isEmpty()) {
                            textInputLayoutChild2Name.error =
                                resources.getString(R.string.child_name_error)
                        } else {
                            child2NameOkay = true
                            textInputLayoutChild2Name.error = null
                        }
                    }
                }

            }

            //child 3
            if (threeChildren) {
                child3Card.visible(true)
                child3Age.setAdapter(childAgesAdapter)

                child3Name.setOnFocusChangeListener { _, hasFocus ->
                    nameOfChild3 = child3Name.text.toString().trim()
                    if (!hasFocus) {
                        if (nameOfChild3.isEmpty()) {
                            textInputLayoutChild3Name.error =
                                resources.getString(R.string.child_name_error)
                        } else {
                            child3NameOkay = true
                            textInputLayoutChild3Name.error = null
                        }
                    }
                }
            }

            addKidsBtn.setOnClickListener {
                var child1Data = Child()
                var child2Data = Child()
                var child3Data = Child()

                // validation
                if (twoChildren) {
                    ageOfChild2 = child2Age.text.toString().trim()
                    if (!child2NameOkay) {
                        //toast that it is required and then check if
                        requireContext().toast("Second child name is required")
                    } else {
                        nameOfChild2 = child2Name.text.toString().trim()
                    }

                    val childCategory = whatAgeCategoryIsThisChild(ageOfChild2.toInt())
                    childCategory?.let { category ->

                        child2Data = Child(
                            name = nameOfChild2,
                            age = ageOfChild2,
                            parentID = mAuth.uid!!,
                            dateAdded = System.currentTimeMillis().toString(),
                            ageGroup = category.id.toString(),
                        )
                    }

                }

                if (threeChildren) {
                    ageOfChild3 = child3Age.text.toString().trim()
                    if (!child3NameOkay) {
                        //toast that it is required and then check if
                        requireContext().toast("Third child name is required")
                    } else {
                        nameOfChild3 = child3Name.text.toString().trim()
                    }
                    val childCategory = whatAgeCategoryIsThisChild(ageOfChild3.toInt())
                    childCategory?.let { category ->

                        child3Data = Child(
                            name = nameOfChild3,
                            age = ageOfChild3,
                            parentID = mAuth.uid!!,
                            dateAdded = System.currentTimeMillis().toString(),
                            ageGroup = category.id.toString(),
                        )
                    }
                }

                ageOfChild1 = child1Age.text.toString().trim()
                if (!child1NameOkay) {
                    //toast that it is required and then check if
                    requireContext().toast("First child name is required")
                } else {
                    nameOfChild1 = child1Name.text.toString().trim()
                }
                val childCategory = whatAgeCategoryIsThisChild(ageOfChild1.toInt())
                childCategory?.let { category ->

                    child1Data = Child(
                        name = nameOfChild1,
                        age = ageOfChild1,
                        parentID = mAuth.uid!!,
                        dateAdded = System.currentTimeMillis().toString(),
                        ageGroup = category.id.toString(),
                    )
                }
                requireContext().showProgress()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        kidCollectionRef.document(child1Data.id).set(child1Data).addOnCompleteListener {child1Task ->
                            if (child1Task.isSuccessful){
                                requireContext().toast("$nameOfChild1 added successfully")
                                if (twoChildren){
                                    requireContext().toast("Adding $nameOfChild2...")
                                    kidCollectionRef.document(child2Data.id).set(child2Data).addOnCompleteListener { child2Task ->
                                        if (child2Task.isSuccessful){
                                            requireContext().toast("$nameOfChild2 added successfully")
                                            if (threeChildren){
                                                requireContext().toast("Adding $nameOfChild3...")
                                                kidCollectionRef.document(child3Data.id).set(child3Data).addOnCompleteListener { child3Task ->
                                                    if (child3Task.isSuccessful){
                                                        requireContext().toast("$nameOfChild3 added successfully")
                                                        hideProgress()
                                                        //nav to home
                                                        val navToHome = AddKidsDirections.actionAddKidsToParentHome()
                                                        findNavController().navigate(navToHome)
                                                    }else{
                                                        hideProgress()
                                                        requireContext().toast(child3Task.exception?.localizedMessage!!)
                                                    }
                                                }.addOnFailureListener { e ->
                                                    hideProgress()
                                                    requireContext().toast(e.localizedMessage!!)
                                                }
                                            }else{
                                               hideProgress()
                                               //nav to home
                                                val navToHome = AddKidsDirections.actionAddKidsToParentHome()
                                                findNavController().navigate(navToHome)
                                            }
                                        }else{
                                            hideProgress()
                                            requireContext().toast(child2Task.exception?.localizedMessage!!)
                                        }
                                    }.addOnFailureListener { e ->
                                        hideProgress()
                                        requireContext().toast(e.localizedMessage!!)
                                    }

                                }else{
                                    hideProgress()
                                    //nav to home
                                    val navToHome = AddKidsDirections.actionAddKidsToParentHome()
                                    findNavController().navigate(navToHome)
                                }
                            }else{
                                hideProgress()
                                requireContext().toast(child1Task.exception?.localizedMessage!!)
                            }
                        }.addOnFailureListener { e ->
                            hideProgress()
                            requireContext().toast(e.localizedMessage!!)
                        }

                    }catch (e: Exception){
                        withContext(Dispatchers.Main){
                            requireContext().toast(e.message.toString())
                        }
                    }
                }

            }


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}