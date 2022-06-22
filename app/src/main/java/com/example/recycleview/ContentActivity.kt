package com.example.recycleview

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.recycleview.databinding.ActivityContentBinding

class ContentActivity : AppCompatActivity() {
    lateinit var binding : ActivityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val item = intent.getSerializableExtra("item") as Plant

        binding.apply {
            imgEmpty.setImageResource(item.imageId)
            tvTitleEmpty.text = item.title
            tvContentEmpty.text = item.description
            editButton.setOnClickListener { onClickEdit(it) }
        }
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun onClickEdit(view: View) {
        val i = Intent(this,EditPlant::class.java)
        startActivity(i)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}