package com.awesomenessstudios.schoolprojects.kiddolingo

data class DriverDetails(
    var surname: String = "",
    var firstName: String = "",
    var date_of_birth: String = "",
    var gender: String = "",
    var bloodGroup: String = "",
    var state_of_origin: String = "",
    var phoneNumber: String = "",
    var licenseNumber: String = "",
    var licenseClass: String = "",
    var license_date_of_issue: String = "",
    var license_date_of_expiry: String = "",
    var license_state_of_issue: String = "",
    var next_of_kin_name: String = "",
    var next_of_kin_phone_number: String = "",
    val carPlateNumber: String = "",
    val carModel: String = "",
    var carColor: String = ""


)
