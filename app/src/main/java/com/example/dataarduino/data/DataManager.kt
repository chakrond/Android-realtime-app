package com.example.dataarduino.data

import com.example.android.dagger.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

private const val HOST_SERVER_URL = "host_server_url"


/**
 * Handles User lifecycle. Manages registrations, logs in and logs out.
 * Knows when the user is logged in.
 *
 * Marked with @Singleton since we only one an instance of UserManager in the application graph.
 */
@Singleton
class DataManager @Inject constructor(
    private val storage: Storage,
    // Since UserManager will be in charge of managing the UserComponent lifecycle,
    // it needs to know how to create instances of it
    private val dataComponentFactory: DataComponent.Factory
) {

    /**
     *  UserComponent is specific to a logged in user. Holds an instance of UserComponent.
     *  This determines if the user is logged in or not, when the user logs in,
     *  a new Component will be created. When the user logs out, this will be null.
     */

    var dataComponent: DataComponent? = null
        private set

    val hostServerUrl: String
        get() = storage.getString(HOST_SERVER_URL)

    fun setHostServerUrl(value: String) {

        storage.setString(HOST_SERVER_URL, value)
        instanceDataCreated()
    }

    fun setStorageData(key: String, value: String) {

        storage.setString(key, value)
        instanceDataCreated()
    }

    fun getStorageData(key: String): String {

        return storage.getString(key)
    }

    fun clearData() {
        // When the user logs out, we remove the instance of UserComponent from memory
        dataComponent = null
    }

    fun clearStorageData(key: String) {

        storage.setString(key, "")
        clearData()
    }

    private fun instanceDataCreated() {
        // When the user logs in, we create a new instance of UserComponent
        dataComponent = dataComponentFactory.create()
    }
}