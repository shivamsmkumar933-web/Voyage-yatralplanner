package com.example.data.api

import com.example.model.Destination
import com.example.model.Flight
import com.example.model.Hotel
import com.example.model.LocalSpot
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Clean Retrofit endpoints corresponding to the Backend Proxy / Edge functions.
 * When a proxy URL is supplied, queries route here securely.
 */
interface TravelBackendProxyApi {

    @GET("destinations")
    suspend fun getDestinations(
        @Query("query") query: String? = null,
        @Query("category") category: String? = null
    ): List<Destination>

    @GET("flights/search")
    suspend fun searchFlights(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("departureDate") departureDate: String,
        @Query("cabinClass") cabinClass: String? = null
    ): List<Flight>

    @GET("hotels/search")
    suspend fun searchHotels(
        @Query("city") city: String,
        @Query("checkIn") checkIn: String,
        @Query("checkOut") checkOut: String,
        @Query("guests") guests: Int = 2
    ): List<Hotel>

    @GET("places/nearby")
    suspend fun getLocalSpots(
        @Query("city") city: String? = null,
        @Query("radiusKm") radiusKm: Int = 10
    ): List<LocalSpot>
}
