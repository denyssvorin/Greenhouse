package com.example.recycleview.ui.home

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.ActivityMainBinding
import com.example.recycleview.databinding.FragmentHomeBinding
import com.example.recycleview.ui.home.additional.SharedPhotoEntity
import com.example.recycleview.utils.onQueryTextChanged
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.rcView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment(), PlantAdapter.OnPlantClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private val plantAdapter: PlantAdapter by lazy {
        PlantAdapter(
            this,
            this.activity,
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

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermissionGranted =
                    permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: readPermissionGranted
                writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermissionGranted

                if (readPermissionGranted) {
                    initRecView()


                } else {
                    Toast.makeText(
                        requireContext(),
                        "Can't read files without permission.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        updateOrRequestPermissions()
        initRecView()
    }

    private fun initContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (readPermissionGranted) {
                    initRecView()
                }
            }
        }
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun updateOrRequestPermissions() {

        val hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
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
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun initUI() {
        binding.apply {
            initRecView()

            viewModel.plants.observe(viewLifecycleOwner) {
                plantAdapter.submitList(it)
            }
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
            fab.setOnClickListener {
                addNewPlant()
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

    private fun initRecView() {
        binding.rcView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = plantAdapter
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