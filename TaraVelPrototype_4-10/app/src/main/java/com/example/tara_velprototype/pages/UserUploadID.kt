package com.example.tara_velprototype.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserUploadID(navController: NavController) {
    val context = LocalContext.current

    // State for captured images
    var frontImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var backImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selfieImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Create temporary files for storing images
    val frontImageFile = remember { createImageFile(context) }
    val backImageFile = remember { createImageFile(context) }
    var selfieImageFile = remember { createImageFile(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* handle result if needed */ }
    )

    // Camera launcher for front ID
    val frontCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            frontImageBitmap = loadBitmapFromFile(frontImageFile)
        }
    }

    // Camera launcher for back ID
    val backCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            backImageBitmap = loadBitmapFromFile(backImageFile)
        }
    }

    // Camera launcher for back ID
    val selfieCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selfieImageBitmap = loadBitmapFromFile(selfieImageFile)
        }
    }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val outlineColor = MaterialTheme.colorScheme.onBackground
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IdCaptureSection(
            title = "Front of the ID",
            required = true,
            previewText = "Front ID preview",
            outlineColor = outlineColor,
            imageBitmap = frontImageBitmap,
            onAdd = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", frontImageFile)
                    frontCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            },
            onRetake = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", frontImageFile)
                    frontCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        IdCaptureSection(
            title = "Back of the ID",
            required = true,
            previewText = "Back ID preview",
            outlineColor = outlineColor,
            imageBitmap = backImageBitmap,
            onAdd = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", backImageFile)
                    backCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            },
            onRetake = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", backImageFile)
                    backCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        IdCaptureSection(
            title = "Selfie with the ID",
            required = true,
            previewText = "Selfie with ID preview",
            outlineColor = outlineColor,
            imageBitmap = selfieImageBitmap,
            onAdd = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", selfieImageFile)
                    selfieCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            },
            onRetake = {
                try {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", selfieImageFile)
                    selfieCameraLauncher.launch(uri)
                } catch (e: Exception) {
                    // Handle error - could show a toast or log
                    e.printStackTrace()
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.End)) {
            Text("Back")
        }
    }
}

@Composable
private fun IdCaptureSection(
    title: String,
    required: Boolean,
    previewText: String,
    outlineColor: Color,
    imageBitmap: Bitmap?,
    onAdd: () -> Unit,
    onRetake: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
        if (required) Text("*", style = MaterialTheme.typography.titleMedium)
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onAdd,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(2.dp, outlineColor)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = outlineColor, modifier = Modifier.size(28.dp))
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 8.dp)
        ,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(3.dp, outlineColor)
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Captured ID",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        previewText,
                        style = MaterialTheme.typography.titleLarge,
                        color = outlineColor.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        OutlinedButton(
            onClick = onRetake,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, outlineColor)
        ) {
            Text("retake")
        }
    }
}

private fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

private fun loadBitmapFromFile(file: File): Bitmap? {
    return try {
        BitmapFactory.decodeFile(file.absolutePath)
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserUploadID() {
    val navController = rememberNavController()
    UserUploadID(navController)
}
