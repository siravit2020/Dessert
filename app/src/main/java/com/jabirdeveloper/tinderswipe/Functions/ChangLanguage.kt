package com.jabirdeveloper.tinderswipe.Functions

import com.akexorcist.localizationactivity.ui.LocalizationActivity

class ChangLanguage : LocalizationActivity() {
    fun changeEnglishLanguage() {
        setLanguage("en")
    }

    fun changeThaiLanguage() {
        setLanguage("th")
    }

}