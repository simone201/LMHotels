package it.simonerenzo.lmhotels.net.models

data class Hotel(val id: Int,
                 val name: String,
                 val location: Location,
                 val stars: Int,
                 val checkIn: CheckHours,
                 val checkOut: CheckHours,
                 val contact: Contact,
                 val images: List<String>,
                 val userRating: Float)

data class Location(val address: String,
                    val city: String,
                    val latitude: Double,
                    val longitude: Double)

data class CheckHours(val from: String,
                      val to: String)

data class Contact(val phoneNumber: String,
                   val email: String)