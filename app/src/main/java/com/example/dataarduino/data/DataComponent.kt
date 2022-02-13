package com.example.dataarduino.data

import com.example.dataarduino.livePlot.LiveFragment
import dagger.Subcomponent

@Subcomponent
interface DataComponent {

    // Factory to create instances of UserComponent
    @Subcomponent.Factory
    interface Factory {
        fun create(): DataComponent
    }

    // Classes that can be injected by this Component
    fun inject(fragment: LiveFragment)
}