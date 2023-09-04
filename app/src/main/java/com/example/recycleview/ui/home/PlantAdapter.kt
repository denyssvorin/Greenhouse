package com.example.recycleview.ui.home

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.PlantItemBinding
import com.squareup.picasso.Picasso
import java.io.File

class PlantAdapter(
    private val listener: OnPlantClickListener,
    private val applicationContext: Context?,
    private val viewModel: HomeViewModel
) :
    ListAdapter<Plant, PlantAdapter.PlantHolder>(DiffPlantCallback()) {

    inner class PlantHolder(private val binding: PlantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val plant = getItem(position)
                        listener.onPlantClick(plant)
                    }
                }
            }
        }

        fun bind(plant: Plant) {
            binding.apply {

                // Завантажуємо фотографію за contentUri
                val contentUri = Uri.parse(plant.plantImagePath)
                viewModel.loadPhotoByContentUri(contentUri)
                val loadedPhoto = viewModel.loadedPhoto.value

                binding.apply {
                    Picasso.with(applicationContext)
                        .load(loadedPhoto)
                        .error(R.drawable.ic_error_24)
                        .into(img)
                    tvTitle.text = plant.plantName
                    itemView.setOnClickListener {
                        listener.onPlantClick(plant)
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantHolder {
        val binding =
            PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    interface OnPlantClickListener {
        fun onPlantClick(plant: Plant)
    }

    class DiffPlantCallback() : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem.plantId == newItem.plantId

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem == newItem

    }
}
