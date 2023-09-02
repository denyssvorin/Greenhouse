package com.example.recycleview.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recycleview.R
import com.example.recycleview.databinding.FragmentDetailsBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.img_empty
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    // This property below is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<DetailsViewModel>()

    private val args = navArgs<DetailsFragmentArgs>()

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

        initUI()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.detailsEvent.collect() { event ->
                when (event) {
                    is DetailsViewModel.DetailsEvent.NavigateToEditScreen -> {
                        val action = DetailsFragmentDirections.actionDetailsFragmentToEditFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun initUI() {
        Picasso.with(requireContext())
            .load(args.value.plant.plantImagePath)
            .error(R.drawable.ic_error_24)
            .into(img_empty)

        binding.tvTitle.text = args.value.plant.plantName
        binding.tvContent.text = args.value.plant.plantDescription
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}