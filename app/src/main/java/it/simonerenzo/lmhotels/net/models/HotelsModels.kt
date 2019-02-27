package it.simonerenzo.lmhotels.net.models

import java.io.Serializable

data class HotelsResponse(val hotels: List<Hotel>)

data class Hotel(val id: Int,
                 val name: String,
                 val location: Location,
                 val stars: Int,
                 val checkIn: CheckHours,
                 val checkOut: CheckHours,
                 val contact: Contact,
                 val images: List<String>?,
                 val userRating: Float) : Serializable

data class Location(val address: String,
                    val city: String,
                    val latitude: Double,
                    val longitude: Double) : Serializable

data class CheckHours(val from: String,
                      val to: String) : Serializable

data class Contact(val phoneNumber: String,
                   val email: String) : Serializable