package com.datastore.sharedpref.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.datastore.sharedpref.adapter.UserAdapter
import com.datastore.sharedpref.databinding.ActivityAddUserBinding
import com.datastore.sharedpref.local.Constant
import com.datastore.sharedpref.model.UserAddress
import com.datastore.sharedpref.model.UserEntity
import com.datastore.sharedpref.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddUserActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddUserBinding.inflate(layoutInflater) }
    private val myViewModel by viewModels<HomeViewModel>()
    private val adapter by lazy { UserAdapter(arrayListOf()) }
    private var updatedObjectId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.getStringExtra(Constant.DATA_OBJ)?.let { value ->
            myViewModel.getUserById(value)
        }

        initAdapter()
        initViews()
        getAllUsers()
    }

    private fun initAdapter() {
        adapter.editListener = {
            setDataToEdit(it)
        }
        adapter.delListener = {
            myViewModel.deleteUser(it.id.toHexString())
        }
        binding.recyclerView.adapter = adapter
    }

    private fun setDataToEdit(user: UserEntity) {
        updatedObjectId = user.id.toHexString()
        with(binding) {
            edtUserName.setText(user.name)
            edtUserAge.setText(user.age.toString())
            if (user.address.isNotEmpty()) {
                edtPermanentStreetName.setText(user.address[0].houseAddress)
                edtPermanentRegionName.setText(user.address[0].regionName)
                if (user.address.size > 1) {
                    edtTemporaryStreetName.setText(user.address[1].houseAddress)
                    edtTemporaryRegionName.setText(user.address[1].regionName)
                }
            }
        }
    }

    private fun getAllUsers() {
        myViewModel.getAllUsers()
        lifecycleScope.launch {
            myViewModel.specificUserData.collectLatest { state ->
                if (!state.isLoading) {
                    Log.e("data*", "No Loading")
                    if (state.error.isNotEmpty()) {
                        Log.e("data*", "Error: ${state.error}")
                    } else {
                        if (state.userRecord != null) {
                            Log.e("data*", state.userRecord.toString())
                            setDataToEdit(state.userRecord)

                        } else {
                            Log.e("data*", "userEntity is NullOrEmpty")
                        }
                    }
                } else {
                    Log.e("data*", "Loading")
                }
            }
        }
        lifecycleScope.launch {
            myViewModel.userData.collectLatest { state ->
                if (!state.isLoading) {
                    Log.e("data*", "No Loading")
                    if (state.error.isNotEmpty()) {
                        Log.e("data*", "Error: ${state.error}")
                    } else {
                        if (state.userRecord != null) {
                            Log.e("data*", state.userRecord.size.toString())
                            adapter.userList = state.userRecord
                            adapter.filteredUserList = state.userRecord
                            adapter.notifyDataSetChanged()
                        } else {
                            Log.e("data*", "userEntity is NullOrEmpty")
                        }
                    }
                } else {
                    Log.e("data*", "Loading")
                }
            }
        }
    }

    private fun initViews() {
        with(binding) {
            btnSave.setOnClickListener {
                val userName = edtUserName.text.toString().trim()
                val userAge = edtUserAge.text.toString().trim()
                val permanentStName = edtPermanentStreetName.text.toString().trim()
                val permanentRegionName = edtPermanentRegionName.text.toString().trim()
                val tempStName = edtTemporaryStreetName.text.toString().trim()
                val tempRegionName = edtTemporaryRegionName.text.toString().trim()

                if (validateInputData()) {
                    val permanentAddress = UserAddress().apply {
                        houseAddress = permanentStName
                        regionName = permanentRegionName
                    }
                    val tempAddress = UserAddress().apply {
                        houseAddress = tempStName
                        regionName = tempRegionName
                    }
                    val user = UserEntity().apply {
                        name = userName
                        age = userAge.toInt()
                        address = arrayListOf(tempAddress, permanentAddress).toRealmList()

                    }
                    if (updatedObjectId.isNotEmpty()) {
                        user.id = org.mongodb.kbson.ObjectId(hexString = updatedObjectId)
                        myViewModel.updateUser(updatedObjectId, user)
                    } else {
                        myViewModel.insertUser(user)
                    }
                    resetInputData()
                } else {
                    Toast.makeText(
                        this@AddUserActivity,
                        "Enter in all data fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateInputData(): Boolean {
        with(binding) {
            val userName = edtUserName.text.toString().trim()
            val userAge = edtUserAge.text.toString().trim()
            val permanentStName = edtPermanentStreetName.text.toString().trim()
            val permanentRegionName = edtPermanentRegionName.text.toString().trim()
            val tempStName = edtTemporaryStreetName.text.toString().trim()
            val tempRegionName = edtTemporaryRegionName.text.toString().trim()
            if (userName.isBlank() || userAge.isBlank() || permanentStName.isBlank() ||
                permanentRegionName.isBlank() || tempStName.isBlank() || tempRegionName.isBlank()
            ) return false
            return true
        }
    }

    private fun resetInputData() {
        updatedObjectId = ""
        with(binding) {
            edtUserName.setText("")
            edtUserAge.setText("")
            edtPermanentStreetName.setText("")
            edtPermanentRegionName.setText("")
            edtTemporaryStreetName.setText("")
            edtTemporaryRegionName.setText("")

        }
    }

}