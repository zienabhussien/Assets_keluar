package com.salor.ventgo.ui.activity.assets_masuk.list

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.salor.ventgo.databinding.ActivityAssetsKeluarDetailAssetsBinding
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.assets_masuk.detail_assets.AssetsMasukListScanAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class DetailAssetsActivity : BaseActivity() {

    lateinit var assetsKeluarListScanAdapter: AssetsMasukListScanAdapter
    lateinit var binding: ActivityAssetsKeluarDetailAssetsBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetsKeluarDetailAssetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantListSearch(this)

        setData()

        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        setAnimHeader()
    }

    fun setData(){

        val listData : ArrayList<String> = ArrayList()

        listData.add("TY507041645R2696B")
        listData.add("TY507041645R2692C")
        listData.add("TY507041645R2666B")
        listData.add("TY507041645R2666B")


        assetsKeluarListScanAdapter = AssetsMasukListScanAdapter(this, listData)
        binding.rvListGudang.setAdapter(assetsKeluarListScanAdapter)
        binding.rvListGudang.isNestedScrollingEnabled = false

    }


    fun setAnimHeader() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.tvDetailScan.visibility = View.VISIBLE

            }, 400)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvDetailScan.visibility = View.VISIBLE
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
