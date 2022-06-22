package com.example.recycleview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recycleview.databinding.ActivityEditPlantBinding

class EditPlant : AppCompatActivity() {
    lateinit var binding: ActivityEditPlantBinding
    var imageIndex = 0
    var imageId =  R.drawable.plant1
    private val imageIdList = listOf(
        R.drawable.plant1,
        R.drawable.plant2,
        R.drawable.plant3,
        R.drawable.plant4,
        R.drawable.plant5
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        choosingImage()
    }
    private fun choosingImage() = with(binding){
        buttonNextImage.setOnClickListener {
            imageIndex++
            if (imageIndex > imageIdList.size - 1) imageIndex = 0
            imageId = imageIdList[imageIndex]
            imageView.setImageResource(imageId)
        }
        buttonDone.setOnClickListener {
            val plant = Plant(imageId, editTextTitle.text.toString(), editTextDescription.text.toString())
            val editIntent = Intent().apply {
                putExtra("key",plant)
            }
            setResult(RESULT_OK, editIntent)
            finish()
        }

    }
}