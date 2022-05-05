package com.e.security.ui.viewmodels.effects

sealed class Effects {
        object StartReportsFragment:Effects()
        object StartFindingsDetailsFragment:Effects()
        object StartCreateFindingFragment:Effects()
}