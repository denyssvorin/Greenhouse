package com.example.recycleview.ui.home

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.FragmentHomeBinding
import com.example.recycleview.ui.home.additional.SharedPhotoEntity
import com.example.recycleview.ui.home.additional.toPlant
import com.example.recycleview.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment(), PlantAdapter.OnPlantClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var plant : Plant

    private val plantAdapter: PlantAdapter by lazy {
        PlantAdapter(
            this,
            this.activity?.applicationContext,
            viewModel
        )
    }

    private lateinit var searchView: SearchView
    private lateinit var searchViewIcon: MenuItem

    private var readPermissionGranted = false
    private var writePermissionGranted = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var contentObserver: ContentObserver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initContentObserver()

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if(readPermissionGranted) {
//                viewModel.loadPhotoByContentUri(Uri.parse(plant.plantImagePath))
//                loadPhotosFromStorageIntoRecyclerView()
            } else {
                Toast.makeText(requireContext(), "Can't read files without permission.", Toast.LENGTH_LONG).show()
            }
        }
        updateOrRequestPermissions()
//        loadPhotosFromStorageIntoRecyclerView()
    }

//    private fun loadPhotosFromStorageIntoRecyclerView() {
//        lifecycleScope.launch {
////            viewModel.plants.observe(viewLifecycleOwner) {
////                plantAdapter.submitList(it)
////            }
//            val photos = loadPhotosFromExternalStorage()
//            val photoUris = photos.map {
//                it.toPlant()
//            }
//            plantAdapter.submitList(photoUris)
//        }
//
//    }

    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if(readPermissionGranted) {
                    binding.rcView.adapter = plantAdapter
                    viewModel.loadPhotoByContentUri(Uri.parse(plant.plantImagePath))
//                    loadPhotosFromStorageIntoRecyclerView()
                }
            }
        }
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    /*private suspend fun loadPhotosFromExternalStorage(): List<SharedPhotoEntity> {
        return withContext(Dispatchers.IO) {

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )

            val photos = mutableListOf<SharedPhotoEntity>()
            requireContext().contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while(cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(SharedPhotoEntity(id, displayName, width, height, contentUri))
                }
                photos.toList()
            } ?: listOf()
        }
    }*/

    private suspend fun loadPhotoByContentUri(contentUri: Uri): SharedPhotoEntity? {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
            )

            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(ContentUris.parseId(contentUri).toString())

            requireContext().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                    val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)

                    val sharedPhoto = SharedPhotoEntity(id, displayName, width, height, contentUri)
                    sharedPhoto
                } else {
                    null
                }
            }
        }
    }


    private fun setupToolbar() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)

                searchViewIcon = menu.findItem(R.id.action_search)
                searchViewIcon.isVisible = true
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)

                searchViewIcon = menu.findItem(R.id.action_search)
                searchView = searchViewIcon.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchViewIcon.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }
                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // TODO: for android 13 use READ_MEDIA_IMAGES permission
    //       for < 13 use permissions above

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun updateOrRequestPermissions() {

        val hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        }

        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
//        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
//        if(!writePermissionGranted) {
//            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
        if(!readPermissionGranted) {
//            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }


    private fun initUI() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.plantEvent.collect() { event ->
                when (event) {
                    is HomeViewModel.HomeEvent.NavigateToDetailsScreen -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                            event.plant, event.plant.plantName
                        )
                        findNavController().navigate(action)
                    }

                    is HomeViewModel.HomeEvent.NavigateToEditScreen -> {
                        val action = HomeFragmentDirections.actionHomeFragmentToEditFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
        binding.apply {
            rcView.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = plantAdapter
            }
            fab.setOnClickListener {
                addNewPlant()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPlantClick(plant: Plant) {
        viewModel.onPlantSelected(plant)
    }

    private fun addNewPlant() {
        viewModel.onAddNewPlant()
    }
}