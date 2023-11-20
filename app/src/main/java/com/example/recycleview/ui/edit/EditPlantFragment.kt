package com.example.recycleview.ui.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.FragmentEditPlantBinding
import com.example.recycleview.ui.SharedTitleViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPlantFragment : Fragment() {

    private var _binding: FragmentEditPlantBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditPlantViewModel>()
    private val args by navArgs<EditPlantFragmentArgs>()

    private lateinit var imageUri: String

    private val pickImageRequest = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri ->

            val newUri = imageUri.lastPathSegment

//            lifecycleScope.launch {
//                viewModel.mapPhotos(newUri.toString())
//            }

            Picasso.with(requireContext())
                .load(imageUri)
                .error(R.drawable.ic_error_24)
                .into(binding.imgEditPlant)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    private fun initUI() {
        binding.apply {
            imageUri = R.drawable.plant_placeholder_coloured.toString()
            // check for args from HomeFragment or DetailsFragment
            // "empty" - from HomeFragment
            if (args.plant.plantImagePath != "empty") {
                imgEditPlant.setImageURI(Uri.parse(args.plant.plantImagePath))
                editTextPlantTitle.setText(args.plant.plantName)
                editTextPlantDescription.setText(args.plant.plantDescription)
            }
            imageButtonPickFromGallery.setOnClickListener {
                onFileChoose()
            }
            fab.setOnClickListener {
                val editTextTitle = editTextPlantTitle.text.toString()
                if (editTextTitle.isNotBlank()) {
                    val editTextDescription = editTextPlantDescription.text.toString()

//                    if (args.plant.plantImagePath != "empty" && viewModel.mappedPhotos.value == null) {
//                        imageUri = args.plant.plantImagePath
//                    }

                    val plant = Plant(
                        plantId = args.plant.plantId,
                        plantImagePath = imageUri,
                        plantName = editTextTitle,
                        plantDescription = editTextDescription
                    )

                    if (args.plant.plantImagePath == "empty") {
                        viewModel.savePlant(plant)
                    } else {
//                        viewModel.updatePlant(plant)

                        val sharedTitleViewModel = ViewModelProvider(requireActivity()).get(
                            SharedTitleViewModel::class.java
                        )
                        sharedTitleViewModel.title.value = editTextPlantTitle.text.toString()
                        findNavController().navigateUp()

                    }
                } else {
                    Snackbar.make(it, "Plant name must be filled", Snackbar.LENGTH_LONG).show()
                }
            }

//            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//                viewModel.editPlantEvent.collect { event ->
//                    when (event) {
//                        EditPlantViewModel.EditPlantEvent.NavigateToHomeScreen -> {
//                            val action =
//                                EditPlantFragmentDirections.actionEditFragmentToHomeFragment()
//                            findNavController().navigate(action)
//                        }
//                    }
//                }
//            }

//            viewModel.mappedPhotos.observe(viewLifecycleOwner) { finalPhotoUri ->
//                this@EditPlantFragment.imageUri = finalPhotoUri
//            }
        }
    }

    private fun onFileChoose() {
        pickImageRequest.launch("image/*")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}