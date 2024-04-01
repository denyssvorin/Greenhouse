package com.example.recycleview.ui.add

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R
import com.example.recycleview.data.Plant


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddNewPlantScreen(
    navController: NavHostController,
    viewModel: AddNewPlantViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val imageUri by viewModel.mappedPhoto.collectAsState()

    val getContent = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let { imageUri ->
            val newImage = imageUri.lastPathSegment.toString()

            // map element to required format
            viewModel.mapPhotos(newImage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        text = stringResource(R.string.create),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.search),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            plantImagePath = imageUri,
                            plantName = viewModel.plantName,
                            plantDescription = viewModel.plantDescription
                        )
                    )
                    Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT)
                        .show()
                    navController.popBackStack()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.save),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .size(height = 250.dp, width = Dp.Unspecified)
                                    .align(Alignment.Center)
                                    .padding(8.dp)
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
                                    .padding(8.dp)
                                    .size(64.dp)
                                    .align(Alignment.BottomStart),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_photo_alternate),
                                    contentDescription = stringResource(R.string.add_from_gallery),
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
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
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 8.dp
                                ),
                            value = viewModel.plantName,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                ),
                            onValueChange = { newValue ->
                                viewModel.updatePlantNameTextField(newValue)
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
                                    bottom = 16.dp
                                ),
                            value = viewModel.plantDescription,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                cursorColor = MaterialTheme.colorScheme.primary,
                            ),
                            onValueChange = { newValue ->
                                viewModel.updatePlantDescriptionTextField(newValue)
                            },
                            label = { Text(stringResource(R.string.description)) }
                        )
                    }
                }
            }
        }
    )
}