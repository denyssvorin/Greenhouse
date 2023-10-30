package com.example.recycleview.ui.edit

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.*
import com.example.recycleview.R
import com.example.recycleview.data.Plant


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddNewPlantScreen(
    navController: NavHostController,
    viewModel: EditPlantViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val imageUri by viewModel.mappedPhotos1.collectAsState()

    val getContent = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let { imageUri ->
            val newImage = imageUri.lastPathSegment.toString()

            // map element to required format
            viewModel.mapPhotos(newImage)
        }
    }


    var newPlantName by rememberSaveable { mutableStateOf("") }
    var newPlantDescription by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.create))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.search)
                            )
                        })
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.savePlant(
                        plant = Plant(
                            plantImagePath = imageUri ?: R.drawable.plant_placeholder_coloured.toString(),
                            plantName = newPlantName,
                            plantDescription = newPlantDescription
                        )
                    )
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.save),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                        {
                            GlideImage(
                                model = imageUri ?: R.drawable.plant_placeholder_coloured,
                                contentDescription = stringResource(R.string.plant),
                                modifier = Modifier
                                    .size(250.dp)
                                    .align(Alignment.Center)

                            )
                            Button(
                                onClick = {
                                    getContent.launch("image/*")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier
                                    .size(64.dp)
                                    .align(Alignment.BottomStart),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_photo_alternate),
                                    contentDescription = stringResource(R.string.add_from_gallery),
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 10.dp
                            )
                            .align(Alignment.CenterHorizontally),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp
                                ),
                            value = newPlantName,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            onValueChange = { newValue ->
                                newPlantName = newValue
                            },
                            label = { Text(stringResource(R.string.name)) },
                            shape = MaterialTheme.shapes.small
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    bottom = 8.dp
                                ),
                            value = newPlantDescription,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            onValueChange = { newValue ->
                                newPlantDescription = newValue
                            },
                            label = { Text(stringResource(R.string.description)) }
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImagePickerDemo(viewModel: EditPlantViewModel = hiltViewModel()) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val getContent = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri.value = it }
        // Оновлення зображення у ViewModel, якщо потрібно
        uri?.toString()?.let { viewModel.mapPhotos(it) }
    }

    Column {
        // Button для виклику Activity Result
        Button(onClick = { getContent.launch("image/*") }) {
            Text("Вибрати зображення")
        }

        // Зображення, яке відображається, коли вибрано
        imageUri.value?.let { uri ->
            GlideImage(
                model = uri,
                contentDescription = null, // Встановіть опис, якщо потрібно
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}