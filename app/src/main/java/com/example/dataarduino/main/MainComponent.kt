
package com.example.dataarduino.main

import com.example.dataarduino.command.CommandFragment
import com.example.dataarduino.di.ActivityScope
import com.example.dataarduino.livePlot.LiveFragment
import com.example.dataarduino.plot.PlotFragment
import com.example.dataarduino.settings.SettingFragment
import dagger.Subcomponent

// Scope annotation that the RegistrationComponent uses
// Classes annotated with @ActivityScope will have a unique instance in this Component
@ActivityScope
// Definition of a Dagger subcomponent
@Subcomponent
interface MainComponent {

    // Factory to create instances of RegistrationComponent
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    // Classes that can be injected by this Component
    fun inject(activity: MainActivity)
    fun inject(fragment: LiveFragment)
    fun inject(fragment: PlotFragment)
    fun inject(fragment: CommandFragment)
    fun inject(fragment: SettingFragment)

}
