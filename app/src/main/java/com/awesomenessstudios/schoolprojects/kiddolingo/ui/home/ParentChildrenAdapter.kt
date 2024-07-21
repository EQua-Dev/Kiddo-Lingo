package com.awesomenessstudios.schoolprojects.kiddolingo.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.awesomenessstudios.schoolprojects.kiddolingo.R
import com.awesomenessstudios.schoolprojects.kiddolingo.models.Child
import com.awesomenessstudios.schoolprojects.kiddolingo.utils.HelperServices.getCategoryDetailsById

/*
class ParentChildrenAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var childName: TextView
    var childAge: TextView
    var childCategory: TextView
    var childCategoryRange: TextView
    var childDateLastPlayed: TextView

    init {
        childName = itemView.findViewById(R.id.child_card_child_name)
        childAge = itemView.findViewById(R.id.child_card_child_age)
        childCategory = itemView.findViewById(R.id.child_card_child_category)
        childCategoryRange = itemView.findViewById(R.id.child_card_child_category_age_range)
        childDateLastPlayed = itemView.findViewById(R.id.child_card_child_last_played_date)
    }

}*/

/*
class ParentChildrenAdapter : ListAdapter<Child, ParentChildrenAdapter.ChildViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent_home_child_card_layout, parent, false)
        return ChildViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(child: Child) {
            itemView.child_card_child_name.text = child.name
            // Bind other data as needed
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Child>() {
            override fun areItemsTheSame(oldItem: Child, newItem: Child): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Child, newItem: Child): Boolean {
                return oldItem == newItem
            }
        }
    }
}
*/

/*
class ParentChildrenAdapter(private val itemList: List<Child>) :
    RecyclerView.Adapter<ParentChildrenAdapter.YourViewHolder>() {
*/
class ParentChildrenAdapter(private val listener: OnItemClickListener) : ListAdapter<Child, ParentChildrenAdapter.ChildViewHolder>(
    DiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent_home_child_card_layout, parent, false)

        return ChildViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        //val currentItem = itemList[position]
        holder.bind(getItem(position))
    }

    //override fun getItemCount() = itemList.size

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Child) {
            // Bind data to views
            val childName = itemView.findViewById<TextView>(R.id.child_card_child_name)
            val childAge = itemView.findViewById<TextView>(R.id.child_card_child_age)
            val childCategory = itemView.findViewById<TextView>(R.id.child_card_child_category)
            val childCategoryRange = itemView.findViewById<TextView>(R.id.child_card_child_category_age_range)
            val childDateLastPlayed = itemView.findViewById<TextView>(R.id.child_card_child_last_played_date)

            val categoryOfChild = getCategoryDetailsById(item.ageGroup.toInt())

            categoryOfChild?.let { category ->
                childName.text = item.name
                childAge.text = "${item.age} Years"
                childCategory.text = category.name
                childCategoryRange.text = "(${category.ageGroup})"
                childDateLastPlayed.text = item.lastPlayedDate ?: "N/A"

                itemView.setOnClickListener {
                    listener.onItemClick(item)
                }
            }

        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Child>() {
            override fun areItemsTheSame(oldItem: Child, newItem: Child): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Child, newItem: Child): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Child)
    }
}
