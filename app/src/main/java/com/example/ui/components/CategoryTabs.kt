package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TravelTab
import com.example.ui.theme.PrimaryOcean
import com.example.ui.theme.SecondaryCoral

@Composable
fun CategoryTabs(
    activeTab: TravelTab,
    onTabSelected: (TravelTab) -> Unit,
    savedTripsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("category_tabs"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TravelTab.values().forEach { tab ->
            val isSelected = activeTab == tab
            val icon = getTabIcon(tab)

            Surface(
                onClick = { onTabSelected(tab) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) PrimaryOcean else MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = if (isSelected) 3.dp else 0.dp,
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tab == TravelTab.MY_TRIPS && savedTripsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = if (isSelected) SecondaryCoral else PrimaryOcean,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$savedTripsCount", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = tab.label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Modern Navigation Rail for wider screens (Tablets, Laptops, Desktops, Foldables).
 */
@Composable
fun AppNavigationRail(
    activeTab: TravelTab,
    onTabSelected: (TravelTab) -> Unit,
    savedTripsCount: Int,
    onSecurityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight().testTag("app_navigation_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryOcean,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Voyage",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Voyage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TravelTab.values().forEach { tab ->
            val isSelected = activeTab == tab
            NavigationRailItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == TravelTab.MY_TRIPS && savedTripsCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = SecondaryCoral,
                                    contentColor = Color.White
                                ) {
                                    Text("$savedTripsCount", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(imageVector = getTabIcon(tab), contentDescription = tab.label)
                        }
                    } else {
                        Icon(imageVector = getTabIcon(tab), contentDescription = tab.label)
                    }
                },
                label = { Text(tab.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = PrimaryOcean,
                    selectedTextColor = PrimaryOcean,
                    indicatorColor = PrimaryOcean.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}")
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        NavigationRailItem(
            selected = false,
            onClick = onSecurityClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "API & Plan",
                    tint = PrimaryOcean
                )
            },
            label = { Text("API & Plan", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_rail_security")
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun getTabIcon(tab: TravelTab): ImageVector {
    return when (tab) {
        TravelTab.EXPLORE -> Icons.Default.Explore
        TravelTab.FLIGHTS -> Icons.Default.Flight
        TravelTab.HOTELS -> Icons.Default.Hotel
        TravelTab.LOCAL_DISCOVERY -> Icons.Default.Place
        TravelTab.MY_TRIPS -> Icons.Default.CardTravel
    }
}
