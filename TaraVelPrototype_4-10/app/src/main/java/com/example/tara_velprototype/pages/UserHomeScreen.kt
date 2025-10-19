package com.example.tara_velprototype.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.plugin.LocationPuck2D
import com.example.tara_velprototype.R
import androidx.core.graphics.drawable.toBitmap
import android.graphics.Color

@Composable
fun MapScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val mapView = MapView(context)
            val mapboxMap = mapView.getMapboxMap()

            mapView.scalebar.enabled = false
            mapView.attribution.enabled = false
            mapView.compass.enabled = false
            mapView.logo.enabled = false

            mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) {
                val locationPlugin = mapView.location

                locationPlugin.updateSettings {
                    enabled = true


                    val puck = ContextCompat.getDrawable(context, R.drawable.person_pin_circle)!!
                    val puckColor = Color.parseColor("#1D3A70")
                    puck.setTint(puckColor)
                    val bitmap = puck.toBitmap()

                    locationPuck = LocationPuck2D(
                        bearingImage = ImageHolder.from(bitmap)

                    )
                }



                val listener = OnIndicatorPositionChangedListener { point ->
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(15.0)
                            .build()
                    )
                }
                locationPlugin.addOnIndicatorPositionChangedListener(listener)

                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(121.730568, 17.612097))
                        .zoom(12.0)
                        .build()
                )
            }
            mapView
        }
    )
}



@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        tonalElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                MapScreen()
                }
            }
        }
    }
}
