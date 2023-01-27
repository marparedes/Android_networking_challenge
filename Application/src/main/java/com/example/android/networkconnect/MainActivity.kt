package com.example.android.networkconnect

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.networkconnect.DownloadCallback.Progress
import com.example.android.networkconnect.adapter.CharactersListAdapter
import com.example.android.networkconnect.databinding.SampleMainBinding
import com.example.android.networkconnect.model.ResponseModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.gson.Gson


/**
 * Sample Activity demonstrating how to connect to the network and fetch raw
 * HTML. It uses a Fragment that encapsulates the network operations on an AsyncTask.
 *
 * This sample uses a TextView to display output.
 */
class MainActivity : FragmentActivity(), DownloadCallback {

    lateinit var binding: SampleMainBinding
    private lateinit var listAdapter: CharactersListAdapter
    // Keep a reference to the NetworkFragment which owns the AsyncTask object
    // that is used to execute network ops.
    private var mNetworkFragment: NetworkFragment? = null

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private var mDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        binding = SampleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mNetworkFragment = NetworkFragment.getInstance(supportFragmentManager, api)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mDownloading = false
        when (item.itemId) {
            R.id.fetch_characters -> {
                if(binding.mainRecyclerView.isVisible) binding.mainRecyclerView.visibility = View.GONE
                startDownload()
                return true
            }
            R.id.clear_action -> {
                finishDownloading()
                return true
            }
        }
        return false
    }

    private fun startDownload() {
        showLoading()
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment!!.startDownload()
            mDownloading = true
        }
    }

    override fun updateFromDownload(result: String?) {
        if (result != null) {
            binding.loading.visibility = View.GONE
            binding.introText.visibility = View.GONE
            setList(result)

        } else {
            hideLoading()
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show()
        }
    }

    override fun getActiveNetworkInfo(): NetworkInfo? {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

    override fun finishDownloading() {
        mDownloading = false
        if (mNetworkFragment != null) {
            mNetworkFragment!!.cancelDownload()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
        when (progressCode) {
            Progress.ERROR -> {}
            Progress.CONNECT_SUCCESS -> {}
            Progress.GET_INPUT_STREAM_SUCCESS -> {}
            Progress.PROCESS_INPUT_STREAM_IN_PROGRESS -> {}
            Progress.PROCESS_INPUT_STREAM_SUCCESS -> {}
        }
    }

    private fun setList(result: String?) {
        val gson = Gson()
        val response: ResponseModel = gson.fromJson(result, ResponseModel::class.java)
        listAdapter = CharactersListAdapter(response.results)

        binding.mainRecyclerView.adapter = listAdapter
        binding.mainRecyclerView.visibility = View.VISIBLE
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.isNestedScrollingEnabled = false
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
        binding.introText.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.loading.visibility = View.GONE
        binding.introText.visibility = View.VISIBLE
    }

    companion object {
        private const val api = "https://rickandmortyapi.com/api/character"
    }
}