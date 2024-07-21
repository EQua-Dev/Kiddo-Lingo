package com.awesomenessstudios.schoolprojects.kiddolingo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.databinding.FragmentParentHomeBinding
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.network.DataResult
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.Common.mAuth
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.HelperServices.getGreeting
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.hideProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.showProgress
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.toast
import com.awesomenessstudios.schoolprojects.kiddolingo.viewmodels.HomeViewModel

class ParentHome : Fragment(), ParentChildrenAdapter.OnItemClickListener {

    private var _binding: FragmentParentHomeBinding? = null
    private val binding get () = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var childrenAdapter: ParentChildrenAdapter

  /*  private var childrenAdapter: FirestoreRecyclerAdapter<Child, ParentChildrenAdapter>? =
        null
*/
    val TAG = "ParentHome"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentParentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            val layoutManager = LinearLayoutManager(requireContext())
            rvChildrenList.layoutManager = layoutManager
            rvChildrenList.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    layoutManager.orientation
                )
            )

            childrenAdapter = ParentChildrenAdapter(this@ParentHome)

            rvChildrenList.adapter = childrenAdapter
        }

        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        homeViewModel.getParentInfo(mAuth.uid!!)
        homeViewModel.parentInfoStateLiveData.observe(
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
                        Log.d(TAG, "onViewCreated: ${response.value}")
                        val parent = response.value
                        with(binding){
                            parentGreeting.text = getGreeting(parent.name)
                            parentHomeChildrenTitle.text = resources.getString(R.string.parent_children_title, parent.noOfChildren.toString())
                            homeViewModel.getParentChildren(mAuth.uid!!)

                            homeViewModel.parentChildrenLiveData.observe(viewLifecycleOwner, Observer{ children ->
                                Log.d(TAG, "onViewCreated: $children")
                                updateUiWithChildren(children)
                            })
                        }
                    }
                }

            })

    }

    private fun updateUiWithChildren(children: DataResult<List<Child>>) {
        when (children) {
            is DataResult.Loading -> {
                // Show loading state
                requireContext().showProgress()
            }
            is DataResult.Success -> {
                hideProgress()
                // Update UI with the list of children
                val listOfChildren = children.value
                childrenAdapter.submitList(listOfChildren)

                // Do something with the list of children
            }
            is DataResult.Failure -> {
                hideProgress()
                // Show failure state and display error message
                val errorMessage = children.error
                requireContext().toast(errorMessage)
            }
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(item: Child) {
        requireContext().toast(item.name)
        val navToChildHome = ParentHomeDirections.actionParentHomeToChildHome(item)
        findNavController().navigate(navToChildHome)
    }

}