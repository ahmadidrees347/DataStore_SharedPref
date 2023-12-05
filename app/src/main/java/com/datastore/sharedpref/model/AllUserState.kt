package com.datastore.sharedpref.model

data class AllUserState(
    val isLoading: Boolean = false,
    val userRecord: MutableList<UserEntity>? = null,
    val error: String = ""
)