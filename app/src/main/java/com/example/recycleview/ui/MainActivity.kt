package com.example.recycleview.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.recycleview.R
import com.example.recycleview.data.Plant
import com.example.recycleview.databinding.ActivityMainBinding
import com.example.recycleview.ui.home.PlantAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PlantAdapter.OnPlantClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() = with(binding) {
        toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navDrawerView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item1 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
                R.id.item2 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
                R.id.item3 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
                R.id.item11 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
                R.id.item21 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
                R.id.item31 -> Toast.makeText(this@MainActivity, "Item pressed: ${item.itemId}", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        fab.setOnClickListener {
            // TODO: navigate to editPlant screen
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, drawerLayout)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onPlantClick(plant: Plant) {}

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()
}