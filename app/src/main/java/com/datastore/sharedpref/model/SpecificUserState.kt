package com.datastore.sharedpref.model

data class SpecificUserState(
    val isLoading: Boolean = false,
    val userRecord: UserEntity? = null,
    val error: String = ""
)