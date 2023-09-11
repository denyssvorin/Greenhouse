package com.example.recycleview.ui.home

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.PlantItemBinding
import com.squareup.picasso.Picasso

class PlantAdapter(
    private val listener: OnPlantClickListener,
    private val applicationContext: Application
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
                Picasso.with(applicationContext)
                    .load(plant.plantImagePath)
                    .centerCrop()
                    .resize(1000, 1000)
                    .placeholder(R.drawable.plant_1573_4)
                    .error(R.drawable.ic_error_24)
                    .into(img)
                tvTitle.text = plant.plantName
                itemView.setOnClickListener {
                    listener.onPlantClick(plant)
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
