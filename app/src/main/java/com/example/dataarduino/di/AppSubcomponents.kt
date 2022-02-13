package com.example.dataarduino.di

import com.example.dataarduino.data.DataComponent
import com.example.dataarduino.main.MainComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        MainComponent::class,
        DataComponent::class
    ]
)
class AppSubcomponents
