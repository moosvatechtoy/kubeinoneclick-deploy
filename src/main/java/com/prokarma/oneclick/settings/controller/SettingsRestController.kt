package com.prokarma.oneclick.settings.controller

import com.prokarma.oneclick.settings.bo.Settings
import com.prokarma.oneclick.settings.repository.SettingsRepository
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

/**
 * The controller for the settings management
 */
@RestController
@RequestMapping("/api/settings")
@Secured("ROLE_ADMIN")
class SettingsRestController(
        private val settings: com.prokarma.oneclick.settings.bo.Settings,
        private val settingsRepository: com.prokarma.oneclick.settings.repository.SettingsRepository) {

    @GetMapping
    fun getSettings() = settings

    @PutMapping
    fun saveSettings(@RequestBody settings: com.prokarma.oneclick.settings.bo.Settings) {
        // update global settings bean
        this.settings.externalUrl = settings.externalUrl
        this.settings.envVars = settings.envVars
        // saving the data
        settingsRepository.save()
    }

}
