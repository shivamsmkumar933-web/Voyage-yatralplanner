package com.example.data

import com.example.data.api.ApiStatusSummary
import com.example.data.api.TravelApiClient
import com.example.data.api.TravelApiConfig
import com.example.model.Destination
import com.example.model.Flight
import com.example.model.Hotel
import com.example.model.LocalSpot
import com.example.model.TripItem
import com.example.model.TripItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Interface ready for remote API integration (e.g., Amadeus Flight API, Booking.com, Google Places).
 */
interface TravelDataSource {
    fun getDestinations(): List<Destination>
    fun getFlights(): List<Flight>
    fun getHotels(): List<Hotel>
    fun getLocalSpots(): List<LocalSpot>
}

class TravelRepository : TravelDataSource {

    private val proxyApi = if (TravelApiConfig.isBackendProxyActive) {
        TravelApiClient.createProxyApi(TravelApiConfig.backendProxyUrl)
    } else {
        null
    }

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(setOf("dest_1", "hotel_1"))
    val bookmarkedIds: Flow<Set<String>> = _bookmarkedIds.asStateFlow()

    private val _savedTrips = MutableStateFlow<List<TripItem>>(
        listOf(
            TripItem(
                id = "trip_1",
                title = "Autumn in Kyoto",
                subtitle = "Fushimi Inari & Arashiyama Bamboo",
                type = TripItemType.DESTINATION,
                date = "Oct 24 - Oct 31, 2026",
                costUsd = 1250,
                status = "Planned"
            ),
            TripItem(
                id = "trip_2",
                title = "Pacific Skyways #PS402",
                subtitle = "SFO -> HND (Direct)",
                type = TripItemType.FLIGHT,
                date = "Oct 24, 2026 (09:45 AM)",
                costUsd = 680,
                status = "Booked"
            ),
            TripItem(
                id = "trip_3",
                title = "Hoshinoya Ryokan & Onsen",
                subtitle = "Kyoto Old Quarter • 5 Nights",
                type = TripItemType.HOTEL,
                date = "Oct 25 - Oct 30, 2026",
                costUsd = 940,
                status = "Booked"
            )
        )
    )
    val savedTrips: Flow<List<TripItem>> = _savedTrips.asStateFlow()

    fun getApiStatus(): ApiStatusSummary {
        return TravelApiConfig.getApiStatusSummary()
    }

    override fun getDestinations(): List<Destination> = listOf(
        Destination(
            id = "dest_1",
            name = "Kyoto",
            country = "Japan",
            region = "East Asia",
            category = "Culture",
            rating = 4.92f,
            reviewCount = 3840,
            imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 720,
            weatherTemp = "22°C",
            weatherCondition = "Sunny",
            description = "Former imperial capital steeped in thousand-year-old temples, bamboo groves, geisha quarters, and refined tea rituals.",
            highlights = listOf("Fushimi Inari Shrine", "Arashiyama Bamboo Grove", "Gion Historic District", "Kinkaku-ji Golden Pavilion"),
            bestTimeToVisit = "March - May & October - November"
        ),
        Destination(
            id = "dest_2",
            name = "Amalfi Coast",
            country = "Italy",
            region = "Southern Europe",
            category = "Beach",
            rating = 4.88f,
            reviewCount = 2950,
            imageUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 980,
            weatherTemp = "25°C",
            weatherCondition = "Clear Sky",
            description = "Dramatic coastal cliffside villages, turquoise Mediterranean waters, terraced lemon orchards, and exquisite seafood cuisine.",
            highlights = listOf("Positano Cliffside View", "Path of the Gods Hike", "Ravello Gardens", "Capri Island Boat Tour"),
            bestTimeToVisit = "May - September"
        ),
        Destination(
            id = "dest_3",
            name = "Banff & Lake Louise",
            country = "Canada",
            region = "North America",
            category = "Mountain",
            rating = 4.95f,
            reviewCount = 4120,
            imageUrl = "https://images.unsplash.com/photo-1503614472-8c93d56e92ce?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 640,
            weatherTemp = "14°C",
            weatherCondition = "Crisp Air",
            description = "Glacial turquoise alpine lakes surrounded by soaring Canadian Rocky peaks, wildlife sanctuaries, and scenic gondola lookouts.",
            highlights = listOf("Moraine Lake Canoeing", "Plain of Six Glaciers Trail", "Banff Upper Hot Springs", "Icefields Parkway"),
            bestTimeToVisit = "June - September & Dec - Mar"
        ),
        Destination(
            id = "dest_4",
            name = "Santorini",
            country = "Greece",
            region = "Southern Europe",
            category = "Luxury",
            rating = 4.89f,
            reviewCount = 5200,
            imageUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 890,
            weatherTemp = "26°C",
            weatherCondition = "Sunny Breezy",
            description = "Iconic whitewashed cubic houses perched along volcanic calderas with world-famous sunset vistas over the Aegean Sea.",
            highlights = listOf("Oia Castle Sunset", "Caldera Catamaran Cruise", "Akrotiri Ruins", "Red Beach"),
            bestTimeToVisit = "April - October"
        ),
        Destination(
            id = "dest_5",
            name = "Reykjavík & South Coast",
            country = "Iceland",
            region = "Nordic",
            category = "Nature",
            rating = 4.87f,
            reviewCount = 2100,
            imageUrl = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 820,
            weatherTemp = "8°C",
            weatherCondition = "Aurora Forecast",
            description = "Land of fire and ice boasting thunderous waterfalls, black sand volcanic shores, geothermal lagoons, and dancing Northern Lights.",
            highlights = listOf("Blue Lagoon Geothermal Spa", "Skógafoss Waterfall", "Reynisfjara Black Sand Beach", "Golden Circle Route"),
            bestTimeToVisit = "September - March (Aurora) or June - August"
        ),
        Destination(
            id = "dest_6",
            name = "Bali & Ubud",
            country = "Indonesia",
            region = "Southeast Asia",
            category = "Nature",
            rating = 4.85f,
            reviewCount = 6400,
            imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 450,
            weatherTemp = "29°C",
            weatherCondition = "Tropical Warmth",
            description = "Lush emerald rice terraces, serene holistic wellness sanctuaries, sacred monkey forests, and vibrant cultural dance traditions.",
            highlights = listOf("Tegallalang Rice Terraces", "Sacred Monkey Forest Sanctuary", "Uluwatu Cliff Temple", "Mount Batur Sunrise Trek"),
            bestTimeToVisit = "April - October"
        ),
        Destination(
            id = "dest_7",
            name = "Swiss Alps (Zermatt)",
            country = "Switzerland",
            region = "Central Europe",
            category = "Mountain",
            rating = 4.94f,
            reviewCount = 3100,
            imageUrl = "https://images.unsplash.com/photo-1491557345352-5929e343eb89?auto=format&fit=crop&w=800&q=80",
            priceFromUsd = 1150,
            weatherTemp = "12°C",
            weatherCondition = "Alpine Fresh",
            description = "Car-free alpine paradise standing in the shadow of the legendary Matterhorn peak with world-class skiing and scenic cogwheel railways.",
            highlights = listOf("Gornergrat Cogwheel Train", "Matterhorn Glacier Paradise", "Five Lakes Walk", "Swiss Fondue Experience"),
            bestTimeToVisit = "December - April (Ski) & July - September (Hike)"
        )
    )

    override fun getFlights(): List<Flight> = listOf(
        Flight(
            id = "fl_1",
            airline = "Pacific Skyways",
            airlineCode = "PS",
            flightNumber = "PS-402",
            originAirport = "SFO",
            originCity = "San Francisco",
            destinationAirport = "HND",
            destinationCity = "Tokyo",
            departureTime = "09:45 AM",
            arrivalTime = "01:30 PM (+1)",
            duration = "10h 45m",
            stopsInfo = "Nonstop",
            priceUsd = 680,
            cabinClass = "Economy",
            seatsAvailable = 4,
            badge = "Best Value"
        ),
        Flight(
            id = "fl_2",
            airline = "AeroNordic Express",
            airlineCode = "AN",
            flightNumber = "AN-118",
            originAirport = "JFK",
            originCity = "New York",
            destinationAirport = "KEF",
            destinationCity = "Reykjavik",
            departureTime = "07:15 PM",
            arrivalTime = "06:20 AM (+1)",
            duration = "5h 35m",
            stopsInfo = "Nonstop",
            priceUsd = 495,
            cabinClass = "Economy",
            seatsAvailable = 7,
            badge = "Cheapest"
        ),
        Flight(
            id = "fl_3",
            airline = "Emirates & EuroLink",
            airlineCode = "EK",
            flightNumber = "EK-882",
            originAirport = "ORD",
            originCity = "Chicago",
            destinationAirport = "NAP",
            destinationCity = "Naples (Amalfi)",
            departureTime = "04:30 PM",
            arrivalTime = "10:15 AM (+1)",
            duration = "11h 45m",
            stopsInfo = "1 stop (MUC)",
            priceUsd = 760,
            cabinClass = "Premium Economy",
            seatsAvailable = 3,
            badge = "Top Rated"
        ),
        Flight(
            id = "fl_4",
            airline = "Air Canada Rockies",
            airlineCode = "AC",
            flightNumber = "AC-520",
            originAirport = "LAX",
            originCity = "Los Angeles",
            destinationAirport = "YYC",
            destinationCity = "Calgary (Banff)",
            departureTime = "11:00 AM",
            arrivalTime = "03:10 PM",
            duration = "3h 10m",
            stopsInfo = "Nonstop",
            priceUsd = 340,
            cabinClass = "Economy",
            seatsAvailable = 9,
            badge = "Fastest"
        ),
        Flight(
            id = "fl_5",
            airline = "Aegean Star Airways",
            airlineCode = "A3",
            flightNumber = "A3-904",
            originAirport = "LHR",
            originCity = "London Heathrow",
            destinationAirport = "JTR",
            destinationCity = "Santorini",
            departureTime = "08:10 AM",
            arrivalTime = "02:05 PM",
            duration = "3h 55m",
            stopsInfo = "Nonstop",
            priceUsd = 410,
            cabinClass = "Economy",
            seatsAvailable = 5
        ),
        Flight(
            id = "fl_6",
            airline = "Swiss International",
            airlineCode = "LX",
            flightNumber = "LX-038",
            originAirport = "JFK",
            originCity = "New York",
            destinationAirport = "ZRH",
            destinationCity = "Zurich (Zermatt)",
            departureTime = "06:40 PM",
            arrivalTime = "08:20 AM (+1)",
            duration = "7h 40m",
            stopsInfo = "Nonstop",
            priceUsd = 890,
            cabinClass = "Business",
            seatsAvailable = 2,
            badge = "Luxury"
        )
    )

    override fun getHotels(): List<Hotel> = listOf(
        Hotel(
            id = "hotel_1",
            name = "Hoshinoya Ryokan & Spa",
            city = "Kyoto",
            country = "Japan",
            address = "Arashiyama Historic Riverside, Kyoto",
            rating = 4.96f,
            reviewCount = 1240,
            pricePerNightUsd = 380,
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Hot Spring Onsen", "Kaiseki Dining", "Garden View", "Free High-speed Wi-Fi", "Tea Master Experience"),
            badge = "Exceptional",
            freeCancellation = true,
            description = "Traditional cedar pavilion overlooking the Oi River, accessible by private scenic wooden boat with authentic thermal onsen waters."
        ),
        Hotel(
            id = "hotel_2",
            name = "Villa TreVille Cliffside Resort",
            city = "Positano",
            country = "Italy",
            address = "Via Carro 1, 84017 Positano (Amalfi Coast)",
            rating = 4.94f,
            reviewCount = 890,
            pricePerNightUsd = 520,
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Private Beach Club", "Infinity Pool", "Panoramic Terrace", "Michelin Star Chef", "Boat Transfer"),
            badge = "Luxury Pick",
            freeCancellation = true,
            description = "Legendary cliff-perched estate surrounded by fragrant lemon groves, featuring open-air terraces and direct private boat access."
        ),
        Hotel(
            id = "hotel_3",
            name = "Fairmont Chateau Lake Louise",
            city = "Banff",
            country = "Canada",
            address = "111 Lake Louise Dr, Lake Louise, AB",
            rating = 4.88f,
            reviewCount = 3400,
            pricePerNightUsd = 340,
            imageUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Lake & Glacier View", "Heated Indoor Pool", "Alpine Spa", "Canoe Rentals", "Ski Valet"),
            badge = "Iconic Stay",
            freeCancellation = true,
            description = "UNESCO World Heritage site located right on the edge of turquoise Lake Louise and surrounded by towering glacier peaks."
        ),
        Hotel(
            id = "hotel_4",
            name = "Grace Hotel Caldera Suites",
            city = "Santorini",
            country = "Greece",
            address = "Imerovigli, 84700 Santorini",
            rating = 4.97f,
            reviewCount = 1580,
            pricePerNightUsd = 490,
            imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Private Plunge Pool", "Santorinian Sunset Deck", "Champagne Breakfast", "Spa Cabana"),
            badge = "Top Rated",
            freeCancellation = true,
            description = "Carved directly into the clifftop rim of Imerovigli with uninterrupted panoramas of the Aegean volcanic caldera."
        ),
        Hotel(
            id = "hotel_5",
            name = "The Retreat Hotel at Blue Lagoon",
            city = "Grindavík",
            country = "Iceland",
            address = "Nordurljosavegur 9, 240 Grindavík",
            rating = 4.93f,
            reviewCount = 920,
            pricePerNightUsd = 440,
            imageUrl = "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Private Geothermal Lagoon", "Subterranean Spa", "Stargazing Deck", "Gourmet Icelandic Tasting"),
            badge = "Wellness Oasis",
            freeCancellation = true,
            description = "Exclusive 62-suite sanctuary surrounded by moss-covered lava fields and private mineral-rich geothermal turquoise waters."
        ),
        Hotel(
            id = "hotel_6",
            name = "Four Seasons Sayan Ubud",
            city = "Bali",
            country = "Indonesia",
            address = "Jl. Raya Sayan, Ubud, Bali",
            rating = 4.91f,
            reviewCount = 2100,
            pricePerNightUsd = 310,
            imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80",
            amenities = listOf("Ayung River Valley View", "Suspension Bridge Entry", "Holistic Chakra Spa", "Yoga Pavilion"),
            badge = "Best Nature Retreat",
            freeCancellation = true,
            description = "Architectural masterpiece concealed within the lush canopy of the sacred Ayung River valley."
        )
    )

    override fun getLocalSpots(): List<LocalSpot> = listOf(
        LocalSpot(
            id = "spot_1",
            name = "Kamon Coffee & Matcha Lab",
            city = "Kyoto",
            category = "Artisanal Food",
            distance = "0.4 km away",
            rating = 4.95f,
            expertTip = "Ask for the ceremonial Uji single-estate matcha poured over chilled oat milk.",
            description = "Tucked behind a 120-year-old machiya house sliding door, run by a 4th-generation tea artisan.",
            imageUrl = "https://images.unsplash.com/photo-1544787219-7f47ccb76574?auto=format&fit=crop&w=600&q=80",
            isOpenNow = true
        ),
        LocalSpot(
            id = "spot_2",
            name = "Path of the Gods Hidden Lemon Grove",
            city = "Positano",
            category = "Secret Viewpoint",
            distance = "1.2 km away",
            rating = 4.98f,
            expertTip = "Visit at 5:30 PM just as the golden hour illuminates the sea with freshly squeezed granita.",
            description = "A secluded rustic terrace overlooking Amalfi's sheer sea cliffs where Antonio serves fresh chilled sfusato limoncello.",
            imageUrl = "https://images.unsplash.com/photo-1516483638261-f4dbaf036963?auto=format&fit=crop&w=600&q=80",
            isOpenNow = true
        ),
        LocalSpot(
            id = "spot_3",
            name = "Secret Moraine Rockpile Cove",
            city = "Banff",
            category = "Nature Walk",
            distance = "0.8 km away",
            rating = 4.92f,
            expertTip = "Walk past the main observation deck to the quiet cedar shoreline path for crystal-clear reflections.",
            description = "A peaceful sheltered pocket of Lake Moraine with zero crowds and pristine turquoise glacial water.",
            imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=600&q=80",
            isOpenNow = true
        ),
        LocalSpot(
            id = "spot_4",
            name = "Oia Windmill Sunset Secret Steps",
            city = "Santorini",
            category = "Scenic Viewpoint",
            distance = "0.6 km away",
            rating = 4.89f,
            expertTip = "Avoid the crowded castle by taking the stone footpath leading 40 steps down toward Ammoudi Bay.",
            description = "Quiet caldera viewpoint offering an uninterrupted vista of the sun plunging into the Aegean Sea.",
            imageUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=600&q=80",
            isOpenNow = true
        ),
        LocalSpot(
            id = "spot_5",
            name = "Reykjavik Old Harbor Rye Bread Bakery",
            city = "Reykjavík",
            category = "Cultural Gem",
            distance = "0.3 km away",
            rating = 4.87f,
            expertTip = "Try the rugbraud baked with geothermal steam underground, served with smoked Arctic char.",
            description = "Quaint timber harbor bakery using ancient geothermal cooking methods for warm, rich dark rye bread.",
            imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=600&q=80",
            isOpenNow = true
        )
    )

    fun toggleBookmark(id: String) {
        _bookmarkedIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun addTripItem(item: TripItem) {
        _savedTrips.update { listOf(item) + it }
    }

    fun removeTripItem(id: String) {
        _savedTrips.update { it.filter { trip -> trip.id != id } }
    }
}
