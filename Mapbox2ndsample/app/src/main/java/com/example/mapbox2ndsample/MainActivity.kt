package com.example.mapbox2ndsample

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import kotlin.math.*
import com.mapbox.bindgen.Value
import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.maps.ImageHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.FeatureCollection
import com.mapbox.api.directions.v5.models.DirectionsWaypoint
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var locationPermissionRequested by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val fineGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (!fineGranted || !coarseGranted) {
                (context as? Activity)?.finishAffinity()
            }
        }
    )

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted || !coarseGranted && !locationPermissionRequested) {
            locationPermissionRequested = true
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Sample Code Ni Obordo Part 78",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            MapScreen()
        }
    }
}

@Composable
fun MapScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val mapView = MapView(context)
            val mapboxMap = mapView.getMapboxMap()

            mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                val locationPlugin = mapView.location
                locationPlugin.updateSettings {
                    enabled = true
                    locationPuck = createDefault2DPuck(withBearing = true)
                }
                val client = MapboxDirections.builder()
                    .origin(Point.fromLngLat(121.734472, 17.631444))
                    .destination(Point.fromLngLat(121.738750, 17.640806))
                    .overview(DirectionsCriteria.OVERVIEW_FULL)
                    .profile(DirectionsCriteria.PROFILE_DRIVING)
                    .accessToken(context.getString(R.string.mapbox_access_token))
                    .build()




                client.enqueueCall(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        val route = response.body()?.routes()?.firstOrNull()
                        val geometry = route?.geometry()

                        if (geometry != null) {
                            val lineString = LineString.fromPolyline(geometry, 6)

                            mapView.getMapboxMap().getStyle { style ->


                                style.addSource(
                                    geoJsonSource("road-route") {
                                        feature(Feature.fromGeometry(lineString))
                                    }
                                )

                                style.addLayer(
                                    lineLayer("road-layer", "road-route") {
                                        lineColor("#0096FF")
                                        lineWidth(4.0)
                                    }
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        Log.e("Directions", "Route request failed", t)
                    }
                })



                val path1 = LineString.fromLngLats(
                    listOf(
                        Point.fromLngLat(121.734472, 17.631444),
                        Point.fromLngLat(121.738750, 17.640806)
                    )
                )

                val path2 = LineString.fromLngLats(
                    listOf(
                        Point.fromLngLat(121.737306, 17.628361),
                        Point.fromLngLat(121.747417, 17.634472)
                    )
                )

                style.addSource(geoJsonSource("path1-source") {
                    feature(Feature.fromGeometry(path1))
                })
                style.addSource(geoJsonSource("path2-source") {
                    feature(Feature.fromGeometry(path2))
                })

                style.addLayer(
                    lineLayer("path1-layer", "path1-source") {
                        lineColor("#FF0000")
                        lineWidth(4.0)
                        visibility(com.mapbox.maps.extension.style.layers.properties.generated.Visibility.NONE)
                    }
                )

                style.addLayer(
                    lineLayer("path2-layer", "path2-source") {
                        lineColor("#0000FF")
                        lineWidth(4.0)
                        visibility(com.mapbox.maps.extension.style.layers.properties.generated.Visibility.NONE)
                    }
                )

                var currentLocationPoint: Point? = null

                val listener = OnIndicatorPositionChangedListener { point ->
                    currentLocationPoint = point
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(15.0)
                            .build()
                    )

                    val showThreshold = 300.0
                    val distanceToPath1 = shortestDistanceToPath(point, path1.coordinates())
                    val distanceToPath2 = shortestDistanceToPath(point, path2.coordinates())

                    style.setStyleLayerProperty(
                        "path1-layer",
                        "visibility",
                        Value(if (distanceToPath1 < showThreshold) "visible" else "none")
                    )

                    style.setStyleLayerProperty(
                        "path2-layer",
                        "visibility",
                        Value(if (distanceToPath2 < showThreshold) "visible" else "none")
                    )

                }

                locationPlugin.addOnIndicatorPositionChangedListener(listener)


                val socketUrl = "http://localhost:3000"
                val socket: Socket = try {
                    IO.socket(socketUrl)
                } catch (e: URISyntaxException) {
                    Log.e("Socket", "Invalid socket URL: $socketUrl", e)
                    throw e
                }

                socket.on(Socket.EVENT_CONNECT) {
                    Log.d("Socket", "✅ Connected to WebSocket server")
                }

                socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e("Socket", "❌ Connection error: ${args.joinToString()}")
                }

                socket.on("locationUpdate") { args ->
                    if (args.isNotEmpty()) {
                        val data = args[0] as JSONObject
                        val lat = data.getDouble("lat")
                        val lng = data.getDouble("lng")
                        Log.d("Socket", "📍 Received location: $lat, $lng")
                    }
                }

                socket.connect()


                val handler = Handler(Looper.getMainLooper())
                val updateInterval = 10_000L

                val updateTask = object : Runnable {
                    override fun run() {
                        currentLocationPoint?.let { point ->
                            val locationData = JSONObject().apply {
                                put("lat", point.latitude())
                                put("lng", point.longitude())
                            }
                            socket.emit("sendLocation", locationData)
                            Log.d("Socket", "📤 Sent location: $locationData")
                        }
                        handler.postDelayed(this, updateInterval)
                    }
                }

                handler.post(updateTask)


                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(121.736, 17.635))
                        .zoom(13.0)
                        .build()
                )
            }

            mapView
        }
    )
}

fun shortestDistanceToPath(point: Point, pathCoords: List<Point>): Double {
    if (pathCoords.size < 2) return Double.MAX_VALUE
    var minDist = Double.MAX_VALUE
    for (i in 0 until pathCoords.size - 1) {
        val dist = distancePointToSegment(point, pathCoords[i], pathCoords[i + 1])
        if (dist < minDist) minDist = dist
    }
    return minDist
}


fun distancePointToSegment(p: Point, a: Point, b: Point): Double {
    val R = 6371000.0
    val lat1 = Math.toRadians(a.latitude())
    val lon1 = Math.toRadians(a.longitude())
    val lat2 = Math.toRadians(b.latitude())
    val lon2 = Math.toRadians(b.longitude())
    val latP = Math.toRadians(p.latitude())
    val lonP = Math.toRadians(p.longitude())


    val x1 = R * cos(lat1) * cos(lon1)
    val y1 = R * cos(lat1) * sin(lon1)
    val z1 = R * sin(lat1)

    val x2 = R * cos(lat2) * cos(lon2)
    val y2 = R * cos(lat2) * sin(lon2)
    val z2 = R * sin(lat2)

    val xP = R * cos(latP) * cos(lonP)
    val yP = R * cos(latP) * sin(lonP)
    val zP = R * sin(latP)

    val A = doubleArrayOf(x1, y1, z1)
    val B = doubleArrayOf(x2, y2, z2)
    val P = doubleArrayOf(xP, yP, zP)

    val AB = doubleArrayOf(B[0] - A[0], B[1] - A[1], B[2] - A[2])
    val AP = doubleArrayOf(P[0] - A[0], P[1] - A[1], P[2] - A[2])
    val BP = doubleArrayOf(P[0] - B[0], P[1] - B[1], P[2] - B[2])

    val ab2 = AB[0] * AB[0] + AB[1] * AB[1] + AB[2] * AB[2]
    val ap_ab = AP[0] * AB[0] + AP[1] * AB[1] + AP[2] * AB[2]
    var t = ap_ab / ab2
    t = max(0.0, min(1.0, t))

    val closest = doubleArrayOf(
        A[0] + AB[0] * t,
        A[1] + AB[1] * t,
        A[2] + AB[2] * t
    )

    val dist = sqrt(
        (P[0] - closest[0]).pow(2) +
                (P[1] - closest[1]).pow(2) +
                (P[2] - closest[2]).pow(2)
    )
    return dist
}
