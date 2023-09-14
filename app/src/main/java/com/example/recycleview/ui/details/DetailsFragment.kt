package com.example.recycleview.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.FragmentDetailsBinding
import com.example.recycleview.ui.SharedTitleViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.img_plant_details

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailsViewModel>()

    private val args by navArgs<DetailsFragmentArgs>()

    private val plantId by lazy { args.plantId }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlant(plantId)

        initUI()
    }

    private fun initUI() {

        setupToolbarTitle()

        viewModel.plantData.observe(viewLifecycleOwner) { plant ->

            if (plant != null) {
                Picasso.with(requireContext())
                    .load(plant.plantImagePath)
                    .error(R.drawable.ic_error_24)
                    .into(img_plant_details)

                binding.apply {
                    tvTitle.text = plant.plantName
                    tvContent.text = plant.plantDescription
                    fabEdit.setOnClickListener {
                        val passedPlant = Plant(
                            plantImagePath = plant.plantImagePath,
                            plantName = plant.plantName,
                            plantDescription = plant.plantDescription
                        )
                        viewModel.editPlant(passedPlant)
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.detailsEvent.collect() { event ->
                    when (event) {
                        is DetailsViewModel.DetailsEvent.NavigateToEditScreen -> {
                            val action =
                                DetailsFragmentDirections.actionDetailsFragmentToEditFragment(
                                    plant = Plant(
                                        plantId = args.plantId,
                                        plantName = plant.plantName,
                                        plantDescription = plant.plantDescription,
                                        plantImagePath = plant.plantImagePath
                                    )
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbarTitle() {
        val title = getTitleFromSharedViewModel()

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (title.isNotEmpty())
            actionBar?.title = title
    }

    private fun getTitleFromSharedViewModel(): String {
        val sharedTitleViewModel = ViewModelProvider(requireActivity()).get(
            SharedTitleViewModel::class.java
        )
        return sharedTitleViewModel.title.value ?: getString(R.string.details)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}