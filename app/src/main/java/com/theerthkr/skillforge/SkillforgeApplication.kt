package com.theerthkr.skillforge

import android.app.Application
import android.content.Context
import com.theerthkr.skillforge.data.repository.SkillforgeRepository

class SkillforgeApplication : Application() {
    
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        SkillforgeRepository.instance.init(appContext)
    }
}
