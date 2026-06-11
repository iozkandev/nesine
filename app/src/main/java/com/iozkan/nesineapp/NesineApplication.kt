package com.iozkan.nesineapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom [Application] annotated with [HiltAndroidApp] so Hilt can generate the
 * application-level dependency container and act as the root of the DI graph.
 */
@HiltAndroidApp
class NesineApplication : Application()