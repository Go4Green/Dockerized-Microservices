package com.thecrafter.ecosnap.ui.activity

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.thecrafter.ecosnap.Adapter
import com.thecrafter.ecosnap.ApiService
import com.thecrafter.ecosnap.R
import com.thecrafter.ecosnap.data.Incident
import com.thecrafter.ecosnap.data.mockIncidents
import com.thecrafter.ecosnap.inflate
import com.thecrafter.ecosnap.ui.holders.IncidentVH
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Context.DashboardActivityIntent(): Intent {
    return Intent(this, MainActivity::class.java)
}

class MainActivity : AppCompatActivity() {

    lateinit var mAdapter: Adapter<Incident, IncidentVH>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.fab_camera).setOnClickListener {
            startActivity(CameraPreviewIntent(), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        mAdapter = Adapter { parent ->
            val view = inflate(
                parent,
                R.layout.list_item_incident
            )
            val vh = IncidentVH(view, this)
            vh.date = view.findViewById(R.id.txt_date)
            vh.time = view.findViewById(R.id.txt_time)
            vh.address = view.findViewById(R.id.txt_address)
            vh.ecopoints = view.findViewById(R.id.txt_eco_points)
            vh.status = view.findViewById(R.id.img_status)
            vh
        }
        incident_list.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        incident_list.layoutManager = layoutManager
        incident_list.addItemDecoration(DividerItemDecoration(incident_list.context, layoutManager.orientation))
    }

    override fun onStart() {
        super.onStart()
        val uid = "34710ea4-1f24-11ea-9712-0242ac110002"
        ApiService.service.listIncidents(uid)
            .enqueue(object: Callback<List<Incident>> {
                override fun onFailure(call: Call<List<Incident>>, t: Throwable) {
                    Log.e("ApiService", t.toString())
                }

                override fun onResponse(
                    call: Call<List<Incident>>,
                    response: Response<List<Incident>>
                ) {
                    if (response.body() != null) {
                        mAdapter.addItems(*response.body()!!.toTypedArray())
                    }
                }
            })

        ApiService.service.ecoPoints(uid)
            .enqueue(object: Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.e("ApiService", t.toString())
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<Int>,
                    response: Response<Int>
                ) {
                    if (response.body() != null) {
                        txt_eco_points.text = response.body()!!.toString()
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
