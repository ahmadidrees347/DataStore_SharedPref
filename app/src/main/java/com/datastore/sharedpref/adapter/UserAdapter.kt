package com.datastore.sharedpref.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.datastore.sharedpref.databinding.ItemUserBinding
import com.datastore.sharedpref.model.UserEntity
import java.util.Locale


class UserAdapter(
    var userList: MutableList<UserEntity> = arrayListOf()
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    var editListener: ((UserEntity) -> Unit)? = null
    var delListener: ((UserEntity) -> Unit)? = null
    var filteredUserList: List<UserEntity> = userList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currencyModel = filteredUserList[position]
        holder.bindData(currencyModel)
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(currencyModel: UserEntity) {
            with(binding) {
                txtTitle.text = currencyModel.name
                txtAddress.text =
                    currencyModel.address.joinToString(separator = "\n") { (it.houseAddress + "-" + it.regionName) }
                imgDel.setOnClickListener { delListener?.invoke(currencyModel) }
                imgEdit.setOnClickListener { editListener?.invoke(currencyModel) }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val searchString = constraint.toString().lowercase(Locale.getDefault())

                val filteredList = if (searchString.isEmpty()) {
                    userList
                } else {
                    userList.filter { user ->
                        user.name.lowercase(Locale.getDefault()).contains(searchString)
                    }
                }

                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredUserList = (results?.values as List<UserEntity>).toMutableList()
                notifyDataSetChanged()
            }
        }
    }
}