package ru.vtb24.mobilebanking.android.locator.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.vtb24.mobilebanking.android.locator.R

@Composable
fun YandexMapView(
    latitude: String,
    longitude: String,
    mapRoutes: List<DrivingRoute>,
    isPreview: Boolean = false,
    followMeIsActive: Boolean,
    position: Point,
    startRouteIcon: Bitmap,
    endRouteIcon: Bitmap,
) {
    if (isPreview) return
    // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
    // with input from Compose UI, those updates are encapsulated into the MapViewContainer
    // composable. In this way, when an update to the MapView happens, this composable won't
    // recompose and the MapView won't need to be recreated.
    val mapView = rememberMapViewWithLifecycle()
    val mapObjects = remember { mapView.map.mapObjects.addCollection().apply { } }
    val context = LocalContext.current
    if (followMeIsActive)
        mapView.map.move(
            CameraPosition(position, mapView.map.cameraPosition.zoom.takeIf { it > 14.5f } ?: 14.5f, 0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    LaunchedEffect(key1 = null, block = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val locationLayer =
                MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
            locationLayer.setObjectListener(object : UserLocationObjectListener {
                override fun onObjectUpdated(view: UserLocationView, p1: ObjectEvent) {}
                override fun onObjectRemoved(view: UserLocationView) {}
                override fun onObjectAdded(view: UserLocationView) {
                    view.accuracyCircle.fillColor =
                        ColorUtils.setAlphaComponent(Color.parseColor("#80C8D1"), 70)
                }
            })
            locationLayer.isVisible = true

        }
    })

    LaunchedEffect(key1 = mapRoutes, block = {
        Log.d("!!!1", "Drawing lines: ${mapRoutes.firstOrNull()?.geometry?.points?.firstOrNull()}")
        mapObjects.clear()
        if (mapRoutes.isNotEmpty()) {

            val route: PolylineMapObject = mapRoutes.first().let {
                Log.d(
                    "!!!1",
                    "addPolyline first point: ${it.geometry.points[0].latitude} ${it.geometry.points[0].longitude}"
                )
                mapObjects.addPolyline(it.geometry)
            }
            route.setStrokeColor(0xFFE31836.toInt())
            route.dashLength = 6f
            route.gapLength = 6f
            route.strokeWidth = 2f

            val firstRoutPoints = mapRoutes[0].geometry.points
            val first = firstRoutPoints.firstOrNull()
            val last = firstRoutPoints.lastOrNull()
            if (first != null && last != null) {
                val pmStart: PlacemarkMapObject = mapObjects.addPlacemark(first)
                pmStart.setIcon(ImageProvider.fromBitmap(startRouteIcon))
                val pmEnd: PlacemarkMapObject = mapObjects.addPlacemark(last)
                pmEnd.setIcon(ImageProvider.fromBitmap(endRouteIcon))
            }
        }
    })
    Box(modifier = Modifier.fillMaxSize()) {
        MapViewContainer(
            mapView, mapRoutes.firstOrNull()?.geometry?.points?.firstOrNull()
                ?: Point(55.807079, 37.583998)
        )
    }
}


private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        Log.d("!!!", "!!! $event")
        when (event) {
            Lifecycle.Event.ON_START -> {
                Log.d("!!!", "!!!1")
                MapKitFactory.getInstance().onStart()
                mapView.onStart()
            }
            Lifecycle.Event.ON_STOP -> {
                Log.d("!!!", "!!!2")
                MapKitFactory.getInstance().onStop()
                mapView.onStop()
            }
//            else -> throw IllegalStateException()
            else -> {
                Log.d("!!!", "$event")
            }
        }
    }

/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context).apply { id = R.id.map } }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        // Make MapView follow the current lifecycle
        val lifecycleObserver = getMapLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

    return mapView
}


@Composable
private fun MapViewContainer(
    map: MapView,
    currentPosition: Point,
) {

    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        factory = { map },
        modifier = Modifier,
        update = { mapView ->
            // Reading zoom so that AndroidView recomposes when it changes. The getMapAsync lambda
            // is stored for later, Compose doesn't recognize state reads

        }
    )

}

private val CAMERA_TARGET = Point(59.951029, 30.317181)
fun drawStaff(mapView: MapView) {
    mapView.map.move(
        CameraPosition(CAMERA_TARGET, 16.0f, 0.0f, 45.0f)
    )

    var sublayerManager = mapView.map.sublayerManager
    var mapObjects = mapView.map.mapObjects

    val circle = Circle(CAMERA_TARGET, 100f)
    mapObjects.addCircle(circle, Color.RED, 2f, Color.WHITE)

    val points = ArrayList<Point>()
    points.add(Point(59.949911, 30.316560))
    points.add(Point(59.949121, 30.316008))
    points.add(Point(59.949441, 30.318132))
    points.add(Point(59.950075, 30.316915))
    points.add(Point(59.949911, 30.316560))
    val polygon = Polygon(LinearRing(points), ArrayList())
    val polygonMapObject: PolygonMapObject = mapObjects.addPolygon(polygon)
    polygonMapObject.fillColor = 0x3300FF00
    polygonMapObject.strokeWidth = 3.0f
    polygonMapObject.strokeColor = Color.GREEN
}
