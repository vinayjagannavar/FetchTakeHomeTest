package com.example.fetchtakehometest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchItems()
    }

    private fun fetchItems() {

        RetrofitClient.instance.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    val filteredItems = items.filter { !it.name.isNullOrBlank() }
                        .sortedWith(compareBy({ it.listId }, { it.name?.extractNumericPart() }))
                    adapter = ItemAdapter(filteredItems)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("MainActivity", "Failed to fetch items: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching items", t)
            }
        })
    }

    fun String.extractNumericPart(): Int {
        val numericPart = this.replace(Regex("[^0-9]+"), "")
        return if (numericPart.isNotEmpty()) numericPart.toInt() else Int.MAX_VALUE
    }
}
