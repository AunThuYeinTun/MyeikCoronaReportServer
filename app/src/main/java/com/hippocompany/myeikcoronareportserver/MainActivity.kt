package com.hippocompany.myeikcoronareportserver

import DataModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hippocompany.coronareport.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    var listofNews: ArrayList<DataModel> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var Database_Path = "All_News_Uploads_Database/UpdateNews"

    lateinit var databaseReference: DatabaseReference


    val cardCoverImages = arrayOf(R.drawable.download, R.drawable.download, R.drawable.download)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        fab.setOnClickListener { view ->
            startActivity(Intent(this, FabActivity::class.java))
        }
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.recyclerView)
        var layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = layoutManager
        mRecyclerView?.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().reference.child(
            Database_Path
        )
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.v("$databaseError", "Show Error Message")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot!!.exists()) {
                    listofNews.clear()
                    for (h in dataSnapshot.children) {
                        val downloadData = h.getValue(DownloadData::class.java)
                        if (downloadData != null) {
                            listofNews.add(
                                DataModel(
                                    downloadData.caption,
                                    downloadData.date,
                                    downloadData.newsImageUrl
                                )
                            )
                            Log.e("ErrorBody", "$downloadData")

                        }
                    }
                    val adapter = RecyclerViewAdapter(listofNews)
                    mRecyclerView?.adapter = adapter
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

    override fun onStart() {

        super.onStart()
    }


}
