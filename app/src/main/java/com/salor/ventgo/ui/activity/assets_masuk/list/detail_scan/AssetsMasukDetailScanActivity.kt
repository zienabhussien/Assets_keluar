package com.salor.ventgo.ui.activity.assets_masuk.list.detail_scan

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.bottlerocketstudios.barcode.generation.ui.BarcodeView
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.asset_list_barang_masuk.AssetListBarangMasuk
import com.salor.ventgo.ui.activity.BaseActivity
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityAssetsMasukDetailScanBinding
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class AssetsMasukDetailScanActivity : BaseActivity() {

    lateinit var ivBarcode : BarcodeView
    var mHandler = Handler()
    lateinit var dataBarang : AssetListBarangMasuk
    lateinit var binding: ActivityAssetsMasukDetailScanBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetsMasukDetailScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantLogin(this)

        val str_json = intent.getStringExtra(Cons.JSON)
        dataBarang = Gson().fromJson(str_json,AssetListBarangMasuk::class.java)

        ivBarcode = findViewById(R.id.ivBarcode)
        binding.tvTitle.text = dataBarang.itemName
        binding.tvSku.text = dataBarang.itemSku
        binding.tvGudang.text = dataBarang.warehouseName
        binding.tvCode.text = dataBarang.code

        try {
            binding.tvPno.text = dataBarang.noPo
        }catch (e : Exception){
            e.printStackTrace()
            binding.tvPno.text = " - "
        }

        try {
            binding.tvDescription.text = dataBarang.description
        }catch (e : Exception){
            e.printStackTrace()
            binding.tvDescription.text = " - "
        }

        binding.tvTanggal.text = DateTimeMasker.changeToDate(dataBarang.createdAt)
        binding.tvWaktu.text = DateTimeMasker.changeToHour(dataBarang.createdAt)

        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        mHandler.removeCallbacks(mUpdateBarcodeRunnable)
        mHandler.postDelayed(mUpdateBarcodeRunnable, 500)

        setAnimHeader()

    }

    fun setAnimHeader() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.tvInfoDetail.visibility = View.VISIBLE


            }, 700)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvInfoDetail.visibility = View.VISIBLE
        }


    }

    private val mUpdateBarcodeRunnable = Runnable {
        ivBarcode.setBarcodeText(dataBarang.code)
        binding.pbLoadingBarcode.visibility = View.GONE
    }

//    private val mUpdateBarcodeRunnable = Runnable {
//        pbLoadingBarcode.visibility = View.GONE
//        ivBarcode.setBarcodeText(dataBarang.code)
//    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@AssetsMasukDetailScanActivity)
    }
}
