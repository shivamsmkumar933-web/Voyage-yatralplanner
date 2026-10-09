package com.example.model

enum class TravelTab(val label: String) {
    EXPLORE("Explore"),
    FLIGHTS("Flights"),
    HOTELS("Hotels"),
    LOCAL_DISCOVERY("Local Gems"),
    MY_TRIPS("My Planner")
}

enum class TripItemType {
    DESTINATION,
    FLIGHT,
    HOTEL
}

data class Destination(
    val id: String,
    val name: String,
    val country: String,
    val region: String,
    val category: String, // "Culture", "Beach", "Mountain", "Nature", "Luxury"
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val priceFromUsd: Int,
    val weatherTemp: String,
    val weatherCondition: String,
    val description: String,
    val highlights: List<String>,
    val bestTimeToVisit: String,
    val isBookmarked: Boolean = false
)

data class Flight(
    val id: String,
    val airline: String,
    val airlineCode: String,
    val flightNumber: String,
    val originAirport: String,
    val originCity: String,
    val destinationAirport: String,
    val destinationCity: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val stopsInfo: String,
    val priceUsd: Int,
    val cabinClass: String,
    val seatsAvailable: Int,
    val badge: String? = null,
    val isBookmarked: Boolean = false
)

data class Hotel(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val address: String,
    val rating: Float,
    val reviewCount: Int,
    val pricePerNightUsd: Int,
    val imageUrl: String,
    val amenities: List<String>,
    val badge: String? = null,
    val freeCancellation: Boolean = true,
    val description: String,
    val isBookmarked: Boolean = false
)

data class LocalSpot(
    val id: String,
    val name: String,
    val city: String,
    val category: String,
    val distance: String,
    val rating: Float,
    val expertTip: String,
    val description: String,
    val imageUrl: String,
    val isOpenNow: Boolean = true
)

data class TripItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: TripItemType,
    val date: String,
    val costUsd: Int,
    val status: String
)

data class FlightSearchParams(
    val origin: String = "San Francisco (SFO)",
    val destination: String = "Tokyo (NRT)",
    val departureDate: String = "Oct 24, 2026",
    val cabinClass: String = "Economy",
    val passengers: Int = 1
)

data class HotelSearchParams(
    val destination: String = "Kyoto, Japan",
    val checkInDate: String = "Oct 25, 2026",
    val checkOutDate: String = "Oct 30, 2026",
    val guests: Int = 2,
    val rooms: Int = 1
)
