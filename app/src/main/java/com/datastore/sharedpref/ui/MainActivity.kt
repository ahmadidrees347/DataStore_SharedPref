package com.datastore.sharedpref.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.datastore.sharedpref.adapter.UserAdapter
import com.datastore.sharedpref.databinding.ActivityMainBinding
import com.datastore.sharedpref.local.Constant
import com.datastore.sharedpref.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val myViewModel by viewModels<HomeViewModel>()
    private val adapter by lazy { UserAdapter(arrayListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initAdapter()
        getAllUsers()
    }

    private fun initViews() {
        with(binding) {
            addUser.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddUserActivity::class.java))
            }
            edtSearchName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    myViewModel.filterData(p0.toString())
//                adapter.filter.filter(p0.toString())
                }
            })
        }
    }

    private fun initAdapter() {

        adapter.editListener = { user ->
            val intent = Intent(this, AddUserActivity::class.java)
            intent.putExtra(Constant.DATA_OBJ, user.id.toHexString())
            startActivity(intent)
        }
        adapter.delListener = {
            myViewModel.deleteUser(it.id.toHexString())
        }
        binding.recyclerView.adapter = adapter
    }

    private fun getAllUsers() {
        myViewModel.getAllUsers()
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

}