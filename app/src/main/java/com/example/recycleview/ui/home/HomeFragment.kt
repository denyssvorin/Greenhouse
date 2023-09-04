package com.example.recycleview.ui.home

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
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
import com.example.recycleview.utils.onQueryTextChanged
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.rcView
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(), PlantAdapter.OnPlantClickListener {

    private var _binding: FragmentHomeBinding? = null
    private var _bindingMainActivity: ActivityMainBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    private val plantAdapter: PlantAdapter by lazy {
        PlantAdapter(
            this,
            this.activity?.applicationContext,
            viewModel
        )
    }

    private lateinit var searchView: SearchView
    private lateinit var searchViewIcon: MenuItem


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

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

    private fun initUI() {
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