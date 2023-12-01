package com.datastore.sharedpref

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.datastore.sharedpref.databinding.ActivityMainBinding
import com.datastore.sharedpref.local.DataStoreUtils
import com.datastore.sharedpref.local.PrefUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val prefs by lazy { PrefUtils(this) }
    private val dataStore by lazy { DataStoreUtils(this) }

    private var counter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            if (dataStore.stringSetExample.first().isNotEmpty()) {
                counter = dataStore.stringSetExample.first().last().toInt()
                Log.e("hashSet*", counter.toString())
            }
        }
        with(binding) {

            showResult()
            btnClearAll.setOnClickListener {
                lifecycleScope.launch {
                    dataStore.clearDataStore()
                    showResult()
                }
            }
            btnSave1.setOnClickListener {
                lifecycleScope.launch {
                    dataStore.isFirstTime = flowOf(!dataStore.isFirstTime.first())
                }

                lifecycleScope.launch {
                    val hashSet = dataStore.stringSetExample.first().toHashSet()
                    Log.e("hashSet*", hashSet.toString())
                    counter++
                    hashSet.add(counter.toString())
                    Log.e("hashSet*", hashSet.toString())

                    dataStore.stringSetExample = flowOf(hashSet)
                }
                if (etPinCode.text.toString().isNotBlank())
                    dataStore.pinCode = flowOf(etPinCode.text.toString())
                showResult()

            }
            btnSave.setOnClickListener {
                lifecycleScope.launch {
                    dataStore.setValue(
                        binding.etSaveKey.text.toString(),
                        binding.etSaveValue.text.toString()
                    )
                    readValue(binding.etSaveKey.text.toString())
                }
            }
            btnRead.setOnClickListener {
                readValue(binding.etReadKey.text.toString())
            }
        }
    }

    private fun showResult() {
        lifecycleScope.launch {
            binding.txtResult.text = getDataStoreResult()
        }
    }

    private fun readValue(key: String) {
        lifecycleScope.launch {
            val value = dataStore.getValue(key, "").first()
            binding.tvReadValue.text = value
        }
    }

    private fun getValuesFromPref() {
        with(binding) {
            prefs.isFirstTime = !prefs.isFirstTime
            if (etPinCode.text.toString().isNotBlank())
                prefs.pinCode = etPinCode.text.toString()

            txtResult.text = getResult()
        }
    }

    private fun getResult() =
        "${prefs.isFirstTime} + ${prefs.pinCode}"

    private suspend fun getDataStoreResult(): String {
        return "" + dataStore.isFirstTime.first() + " + " + dataStore.pinCode.first() + " + " + dataStore.stringSetExample.first()
    }
}