package com.example.medicare.Doctor.utils

import com.example.medicare.Doctor.model.DoctorModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDoctorHelper {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Doctors")

    fun saveDoctorProfile(doctor: DoctorModel, onComplete: (Boolean, String?) -> Unit) {
        val doctorId = doctor.doctorUid
        if (doctorId.isNotEmpty()) {
            databaseReference.child(doctorId).setValue(doctor)
                .addOnSuccessListener {
                    onComplete(true, null)
                }
                .addOnFailureListener { e ->
                    onComplete(false, e.message)
                }
        } else {
            onComplete(false, "Doctor UID is empty")
        }
    }

    fun getDoctorProfile(doctorUid: String, onResult: (DoctorModel?) -> Unit) {
        databaseReference.child(doctorUid).get().addOnSuccessListener {
            val doctor = it.getValue(DoctorModel::class.java)
            onResult(doctor)
        }.addOnFailureListener {
            onResult(null)
        }
    }
}
