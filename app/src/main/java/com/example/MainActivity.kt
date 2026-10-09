package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Destination
import com.example.model.TravelTab
import com.example.ui.TravelUiState
import com.example.ui.TravelViewModel
import com.example.ui.components.ApiSecurityDialog
import com.example.ui.components.AppNavigationRail
import com.example.ui.components.BookingDetailDialog
import com.example.ui.components.CategoryTabs
import com.example.ui.components.DestinationCard
import com.example.ui.components.EmptyTripsView
import com.example.ui.components.FlightCard
import com.example.ui.components.FlightSearchHeader
import com.example.ui.components.HeroSection
import com.example.ui.components.HotelCard
import com.example.ui.components.HotelSearchHeader
import com.example.ui.components.LocalSpotCard
import com.example.ui.components.TripBudgetSummaryCard
import com.example.ui.components.TripItemCard
import com.example.ui.theme.PrimaryOcean
import com.example.ui.theme.SecondaryCoral
import com.example.ui.theme.VoyageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoyageTheme {
                val viewModel: TravelViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.bookingSuccessMessage) {
                    uiState.bookingSuccessMessage?.let { message ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.dismissSuccessMessage()
                    }
                }

                VoyageApp(
                    uiState = uiState,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun VoyageApp(
    uiState: TravelUiState,
    viewModel: TravelViewModel,
    snackbarHostState: SnackbarHostState
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 768.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("app_snackbar_host")
                )
            },
            floatingActionButton = {
                // Show FAB on compact mobile screens when not in planner tab
                if (!isWideScreen && uiState.activeTab != TravelTab.MY_TRIPS && uiState.myTrips.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { viewModel.setTab(TravelTab.MY_TRIPS) },
                        containerColor = SecondaryCoral,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("fab_my_planner")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = PrimaryOcean,
                                    contentColor = Color.White
                                ) {
                                    Text("${uiState.myTrips.size}", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Luggage,
                                contentDescription = "My Planner",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = navBarBottom)
            ) {
                // 1. Navigation Rail for Wide Screens (Tablets, Laptops, Desktops, Chromebooks)
                if (isWideScreen) {
                    AppNavigationRail(
                        activeTab = uiState.activeTab,
                        onTabSelected = { viewModel.setTab(it) },
                        savedTripsCount = uiState.myTrips.size,
                        onSecurityClick = { viewModel.openSecurityDialog() }
                    )
                }

                // 2. Main Responsive Content Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 340.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 1280.dp)
                            .testTag("main_responsive_grid"),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Top status bar spacing
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(statusBarTop))
                        }

                        // Hero Section spans across all columns
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HeroSection(
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                selectedFilterChip = uiState.selectedFilterChip,
                                onFilterChipSelect = { viewModel.setFilterChip(it) },
                                destinationsCount = uiState.destinations.size,
                                flightsCount = uiState.flights.size,
                                hotelsCount = uiState.hotels.size,
                                onSecurityClick = { viewModel.openSecurityDialog() }
                            )
                        }

                        // Category Tabs (shown on compact phone screens only; wide screens use side NavRail)
                        if (!isWideScreen) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                CategoryTabs(
                                    activeTab = uiState.activeTab,
                                    onTabSelected = { viewModel.setTab(it) },
                                    savedTripsCount = uiState.myTrips.size
                                )
                            }
                        }

                        // Active Module content with Adaptive Responsive Layout
                        when (uiState.activeTab) {
                            TravelTab.EXPLORE -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SectionHeader(
                                        title = "Popular Destinations & Wonders",
                                        subtitle = "Hand-picked places curated by local explorers",
                                        count = uiState.destinations.size
                                    )
                                }

                                if (uiState.destinations.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyResultsView(
                                            query = uiState.searchQuery,
                                            onReset = {
                                                viewModel.setSearchQuery("")
                                                viewModel.setFilterChip("All")
                                            }
                                        )
                                    }
                                } else {
                                    items(uiState.destinations, key = { it.id }) { dest ->
                                        Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                            DestinationCard(
                                                destination = dest,
                                                isBookmarked = uiState.bookmarkedIds.contains(dest.id),
                                                onBookmarkToggle = { viewModel.toggleBookmark(dest.id, dest) },
                                                onCardClick = { viewModel.openDetail(dest) },
                                                onPlanClick = { viewModel.planDestination(dest) }
                                            )
                                        }
                                    }
                                }
                            }

                            TravelTab.FLIGHTS -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                        FlightSearchHeader(
                                            searchParams = uiState.flightSearchParams,
                                            onOriginChange = { viewModel.updateFlightOrigin(it) },
                                            onDestinationChange = { viewModel.updateFlightDestination(it) },
                                            onDateChange = { viewModel.updateFlightDate(it) }
                                        )
                                    }
                                }

                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SectionHeader(
                                        title = "Available Flight Deals",
                                        subtitle = "Live scheduled routes with verified best prices",
                                        count = uiState.flights.size
                                    )
                                }

                                if (uiState.flights.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyResultsView(
                                            query = uiState.searchQuery,
                                            onReset = { viewModel.setSearchQuery("") }
                                        )
                                    }
                                } else {
                                    items(uiState.flights, key = { it.id }) { flight ->
                                        Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                            FlightCard(
                                                flight = flight,
                                                onSelectFlight = { viewModel.openDetail(flight) }
                                            )
                                        }
                                    }
                                }
                            }

                            TravelTab.HOTELS -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                        HotelSearchHeader(
                                            searchParams = uiState.hotelSearchParams,
                                            onDestinationChange = { viewModel.updateHotelCity(it) }
                                        )
                                    }
                                }

                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SectionHeader(
                                        title = "Curated Hotels, Stays & Villas",
                                        subtitle = "Top-rated accommodations with free cancellation",
                                        count = uiState.hotels.size
                                    )
                                }

                                if (uiState.hotels.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyResultsView(
                                            query = uiState.searchQuery,
                                            onReset = { viewModel.setSearchQuery("") }
                                        )
                                    }
                                } else {
                                    items(uiState.hotels, key = { it.id }) { hotel ->
                                        Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                            HotelCard(
                                                hotel = hotel,
                                                isBookmarked = uiState.bookmarkedIds.contains(hotel.id),
                                                onBookmarkToggle = { viewModel.toggleBookmark(hotel.id, hotel) },
                                                onReserveClick = { viewModel.openDetail(hotel) },
                                                onCardClick = { viewModel.openDetail(hotel) }
                                            )
                                        }
                                    }
                                }
                            }

                            TravelTab.LOCAL_DISCOVERY -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SectionHeader(
                                        title = "Hidden Local Gems & Secrets",
                                        subtitle = "Off-the-beaten-path cafes, lookouts & trails",
                                        count = uiState.localSpots.size
                                    )
                                }

                                if (uiState.localSpots.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyResultsView(
                                            query = uiState.searchQuery,
                                            onReset = { viewModel.setSearchQuery("") }
                                        )
                                    }
                                } else {
                                    items(uiState.localSpots, key = { it.id }) { spot ->
                                        Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                            LocalSpotCard(
                                                spot = spot,
                                                onPlanSpot = {
                                                    viewModel.planDestination(
                                                        Destination(
                                                            id = spot.id,
                                                            name = spot.name,
                                                            country = spot.city,
                                                            region = spot.category,
                                                            category = spot.category,
                                                            rating = spot.rating,
                                                            reviewCount = 120,
                                                            imageUrl = spot.imageUrl,
                                                            priceFromUsd = 25,
                                                            weatherTemp = "20°C",
                                                            weatherCondition = "Clear",
                                                            description = spot.description,
                                                            highlights = listOf(spot.expertTip),
                                                            bestTimeToVisit = "All Year"
                                                        )
                                                    )
                                                },
                                                onCardClick = { viewModel.openDetail(spot) }
                                            )
                                        }
                                    }
                                }
                            }

                            TravelTab.MY_TRIPS -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                        TripBudgetSummaryCard(trips = uiState.myTrips)
                                    }
                                }

                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    SectionHeader(
                                        title = "My Travel Itinerary",
                                        subtitle = "Active reservations and saved travel plans",
                                        count = uiState.myTrips.size
                                    )
                                }

                                if (uiState.myTrips.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyTripsView(onExploreClick = { viewModel.setTab(TravelTab.EXPLORE) })
                                    }
                                } else {
                                    items(uiState.myTrips, key = { it.id }) { trip ->
                                        Box(modifier = Modifier.padding(horizontal = if (isWideScreen) 8.dp else 16.dp)) {
                                            TripItemCard(
                                                trip = trip,
                                                onDeleteClick = { viewModel.removeTrip(trip.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive details & booking dialog
            uiState.selectedDetailItem?.let { item ->
                BookingDetailDialog(
                    item = item,
                    onDismiss = { viewModel.closeDetail() },
                    onBookFlight = { viewModel.bookFlight(it) },
                    onBookHotel = { viewModel.bookHotel(it) },
                    onPlanDestination = { viewModel.planDestination(it) }
                )
            }

            // Security Architecture & API Integration Dialog
            if (uiState.showSecurityDialog) {
                ApiSecurityDialog(
                    status = uiState.apiStatus,
                    onDismiss = { viewModel.closeSecurityDialog() },
                    onSaveVaultKey = { viewModel.saveVaultKey(it) },
                    onClearVaultKey = { viewModel.clearVaultKey() }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PrimaryOcean.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "$count available",
                    color = PrimaryOcean,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyResultsView(
    query: String,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results found for \"$query\"",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Try searching for a different city, airline, hotel, or reset your filters.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = PrimaryOcean,
            onClick = onReset
        ) {
            Text(
                text = "Clear Filters",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
