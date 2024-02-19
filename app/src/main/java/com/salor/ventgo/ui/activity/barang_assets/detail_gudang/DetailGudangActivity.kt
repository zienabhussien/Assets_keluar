package com.salor.ventgo.ui.activity.barang_assets.detail_gudang

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.salor.ventgo.R
import com.salor.ventgo.databinding.ActivityListBarangDetailGudangBinding
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.assets_barang_list.BarangAssets
import com.salor.ventgo.obj.assets_barang_list.detail.DetailItem
import com.salor.ventgo.obj.assets_barang_list.detail.WarehouseAssets
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.barang_assets.detail_list_barang_scan.ListBarangScanAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class DetailGudangActivity : BaseActivity() {

    lateinit var barangAssets : BarangAssets
    var listDetailItem : ArrayList<DetailItem> = ArrayList()
    lateinit var warehouseAssets : WarehouseAssets
    lateinit var listBarangScan : ListBarangScanAdapter
    lateinit var binding: ActivityListBarangDetailGudangBinding


    override fun attachBaseContext(newBase: Context) {
        ;
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBarangDetailGudangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantListSearch(this)

       binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        val str_json = intent.getStringExtra(Cons.JSON)
        val str_json_barang_assets = intent.getStringExtra(Cons.JSON_BARANG_ASSETS)
        warehouseAssets = Gson().fromJson(str_json,WarehouseAssets::class.java)

        barangAssets = Gson().fromJson(str_json_barang_assets, BarangAssets::class.java)

        binding.tvTitleToolbar.text = warehouseAssets.warehouseName

        binding.tvTitle.text = barangAssets.name
        binding.tvQty.text = barangAssets.stock + " " + resources.getString(R.string.label_pcs)
        binding.tvDate.text = DateTimeMasker.changeToNameMonthCustom(barangAssets.createdAt)


        listDetailItem = warehouseAssets.detailItem as ArrayList<DetailItem>

        setData()

        setAnimViewVisible(binding.lParentContent,binding.tvBarangAssetsDummy,700)

    }

    fun setData(){

        listBarangScan = ListBarangScanAdapter(this, listDetailItem)
        binding.rvListGudang.setAdapter(listBarangScan)
        binding.rvListGudang.isNestedScrollingEnabled = false

        setAnimHeader()
    }


    fun setAnimHeader() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.tvDetailScan.visibility = View.VISIBLE

            }, 700)
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
        setOveridePendingTransisi(this@DetailGudangActivity)

    }
}
