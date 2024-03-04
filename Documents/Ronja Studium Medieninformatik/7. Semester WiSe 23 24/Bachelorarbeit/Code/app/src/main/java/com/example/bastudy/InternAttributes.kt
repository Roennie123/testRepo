package com.example.bastudy

data class InternAttributes(
    // last 4 digits are missing
    val tempUUID: String = "18ea2510-879d-45b0-834c-2bbef42c",

    /**
     * questionaireLink_StudyModeA := Link to questionaire on limesurvey for asking about week without interventions
     * questionaireLink_StudyModeB := Link to questionaire on limesurvey for asking about week with interventions
     */
    val questionaireLink_StudyModeA: String = "https://surveys.informatik.uni-ulm.de/index.php/114715?",
    val questionaireLink_StudyModeB: String = "https://surveys.informatik.uni-ulm.de/index.php/911257?"
)
