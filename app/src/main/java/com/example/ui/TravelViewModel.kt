package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TravelRepository
import com.example.model.Destination
import com.example.model.Flight
import com.example.model.FlightSearchParams
import com.example.model.Hotel
import com.example.model.HotelSearchParams
import com.example.model.LocalSpot
import com.example.model.TravelTab
import com.example.model.TripItem
import com.example.model.TripItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class TravelUiState(
    val activeTab: TravelTab = TravelTab.EXPLORE,
    val searchQuery: String = "",
    val selectedFilterChip: String = "All",
    val destinations: List<Destination> = emptyList(),
    val flights: List<Flight> = emptyList(),
    val hotels: List<Hotel> = emptyList(),
    val localSpots: List<LocalSpot> = emptyList(),
    val bookmarkedIds: Set<String> = emptySet(),
    val myTrips: List<TripItem> = emptyList(),
    val flightSearchParams: FlightSearchParams = FlightSearchParams(),
    val hotelSearchParams: HotelSearchParams = HotelSearchParams(),
    val selectedDetailItem: Any? = null,
    val bookingSuccessMessage: String? = null,
    val showSecurityDialog: Boolean = false,
    val apiStatus: com.example.data.api.ApiStatusSummary = com.example.data.api.TravelApiConfig.getApiStatusSummary()
)

class TravelViewModel(
    private val repository: TravelRepository = TravelRepository()
) : ViewModel() {

    private val _activeTab = MutableStateFlow(TravelTab.EXPLORE)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilterChip = MutableStateFlow("All")
    private val _flightSearchParams = MutableStateFlow(FlightSearchParams())
    private val _hotelSearchParams = MutableStateFlow(HotelSearchParams())
    private val _selectedDetailItem = MutableStateFlow<Any?>(null)
    private val _bookingSuccessMessage = MutableStateFlow<String?>(null)
    private val _showSecurityDialog = MutableStateFlow(false)

    private val rawDestinations = repository.getDestinations()
    private val rawFlights = repository.getFlights()
    private val rawHotels = repository.getHotels()
    private val rawLocalSpots = repository.getLocalSpots()

    val uiState: StateFlow<TravelUiState> = combine(
        _activeTab,
        _searchQuery,
        _selectedFilterChip,
        repository.bookmarkedIds,
        repository.savedTrips,
        _flightSearchParams,
        _hotelSearchParams,
        _selectedDetailItem,
        _bookingSuccessMessage,
        _showSecurityDialog
    ) { params ->
        val activeTab = params[0] as TravelTab
        val query = params[1] as String
        val chip = params[2] as String
        val bookmarks = params[3] as Set<String>
        val trips = params[4] as List<TripItem>
        val flightParams = params[5] as FlightSearchParams
        val hotelParams = params[6] as HotelSearchParams
        val detailItem = params[7]
        val message = params[8] as String?
        val showSecurity = params[9] as Boolean

        // Filter destinations
        val filteredDestinations = rawDestinations.filter { dest ->
            val matchesQuery = query.isBlank() ||
                dest.name.contains(query, ignoreCase = true) ||
                dest.country.contains(query, ignoreCase = true) ||
                dest.description.contains(query, ignoreCase = true)
            val matchesChip = chip == "All" || dest.category.equals(chip, ignoreCase = true)
            matchesQuery && matchesChip
        }

        // Filter flights
        val filteredFlights = rawFlights.filter { flight ->
            query.isBlank() ||
                flight.airline.contains(query, ignoreCase = true) ||
                flight.originCity.contains(query, ignoreCase = true) ||
                flight.destinationCity.contains(query, ignoreCase = true) ||
                flight.originAirport.contains(query, ignoreCase = true) ||
                flight.destinationAirport.contains(query, ignoreCase = true)
        }

        // Filter hotels
        val filteredHotels = rawHotels.filter { hotel ->
            query.isBlank() ||
                hotel.name.contains(query, ignoreCase = true) ||
                hotel.city.contains(query, ignoreCase = true) ||
                hotel.country.contains(query, ignoreCase = true) ||
                hotel.amenities.any { it.contains(query, ignoreCase = true) }
        }

        // Filter local spots
        val filteredSpots = rawLocalSpots.filter { spot ->
            query.isBlank() ||
                spot.name.contains(query, ignoreCase = true) ||
                spot.city.contains(query, ignoreCase = true) ||
                spot.category.contains(query, ignoreCase = true) ||
                spot.description.contains(query, ignoreCase = true)
        }

        TravelUiState(
            activeTab = activeTab,
            searchQuery = query,
            selectedFilterChip = chip,
            destinations = filteredDestinations,
            flights = filteredFlights,
            hotels = filteredHotels,
            localSpots = filteredSpots,
            bookmarkedIds = bookmarks,
            myTrips = trips,
            flightSearchParams = flightParams,
            hotelSearchParams = hotelParams,
            selectedDetailItem = detailItem,
            bookingSuccessMessage = message,
            showSecurityDialog = showSecurity
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TravelUiState(
            destinations = rawDestinations,
            flights = rawFlights,
            hotels = rawHotels,
            localSpots = rawLocalSpots
        )
    )

    fun openSecurityDialog() {
        _showSecurityDialog.value = true
    }

    fun closeSecurityDialog() {
        _showSecurityDialog.value = false
    }

    fun saveVaultKey(key: String) {
        if (key.isNotBlank()) {
            com.example.data.api.TravelApiConfig.setVaultKey(key)
            _bookingSuccessMessage.value = "API Key locked in private vault & active!"
            _showSecurityDialog.value = false
        }
    }

    fun clearVaultKey() {
        com.example.data.api.TravelApiConfig.clearVaultKey()
        _bookingSuccessMessage.value = "API Key securely wiped from app memory."
    }

    fun setTab(tab: TravelTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterChip(chip: String) {
        _selectedFilterChip.value = chip
    }

    fun toggleBookmark(id: String, item: Any? = null) {
        repository.toggleBookmark(id)
        // If bookmarking a destination, add to planner if not already added
        if (item is Destination && !repository.getDestinations().any { it.id == id }) {
            // bookmark toggled
        }
    }

    fun openDetail(item: Any) {
        _selectedDetailItem.value = item
    }

    fun closeDetail() {
        _selectedDetailItem.value = null
    }

    fun updateFlightOrigin(origin: String) {
        _flightSearchParams.update { it.copy(origin = origin) }
    }

    fun updateFlightDestination(dest: String) {
        _flightSearchParams.update { it.copy(destination = dest) }
    }

    fun updateFlightDate(date: String) {
        _flightSearchParams.update { it.copy(departureDate = date) }
    }

    fun updateHotelCity(city: String) {
        _hotelSearchParams.update { it.copy(destination = city) }
    }

    fun bookFlight(flight: Flight) {
        val trip = TripItem(
            id = "trip_" + UUID.randomUUID().toString().take(6),
            title = "${flight.airline} (${flight.flightNumber})",
            subtitle = "${flight.originAirport} ➔ ${flight.destinationAirport} • ${flight.departureTime}",
            type = TripItemType.FLIGHT,
            date = _flightSearchParams.value.departureDate,
            costUsd = flight.priceUsd,
            status = "Confirmed"
        )
        repository.addTripItem(trip)
        _bookingSuccessMessage.value = "Flight ${flight.flightNumber} to ${flight.destinationCity} reserved successfully!"
        _selectedDetailItem.value = null
    }

    fun bookHotel(hotel: Hotel) {
        val trip = TripItem(
            id = "trip_" + UUID.randomUUID().toString().take(6),
            title = hotel.name,
            subtitle = "${hotel.city}, ${hotel.country} • ${_hotelSearchParams.value.checkInDate}",
            type = TripItemType.HOTEL,
            date = "${_hotelSearchParams.value.checkInDate} - ${_hotelSearchParams.value.checkOutDate}",
            costUsd = hotel.pricePerNightUsd * 3,
            status = "Confirmed"
        )
        repository.addTripItem(trip)
        _bookingSuccessMessage.value = "Stay reserved at ${hotel.name}! Added to your planner."
        _selectedDetailItem.value = null
    }

    fun planDestination(dest: Destination) {
        val trip = TripItem(
            id = "trip_" + UUID.randomUUID().toString().take(6),
            title = "Trip to ${dest.name}",
            subtitle = "${dest.country} • ${dest.category} Escape",
            type = TripItemType.DESTINATION,
            date = "Scheduled for ${dest.bestTimeToVisit}",
            costUsd = dest.priceFromUsd,
            status = "Planned"
        )
        repository.addTripItem(trip)
        _bookingSuccessMessage.value = "${dest.name} added to your Travel Planner!"
        _selectedDetailItem.value = null
    }

    fun removeTrip(tripId: String) {
        repository.removeTripItem(tripId)
    }

    fun dismissSuccessMessage() {
        _bookingSuccessMessage.value = null
    }
}
