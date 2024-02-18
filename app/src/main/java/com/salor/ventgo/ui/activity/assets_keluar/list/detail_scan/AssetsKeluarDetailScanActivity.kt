package com.salor.ventgo.ui.activity.assets_keluar.list.detail_scan

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.asset_list_barang_keluar.AssetListBarangKeluar
import com.salor.ventgo.ui.activity.BaseActivity
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityAssetsKeluarDetailAssetsBinding
import com.salor.ventgo.databinding.ActivityAssetsKeluarDetailScanBinding
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class AssetsKeluarDetailScanActivity : BaseActivity() {

    lateinit var dataBarang : AssetListBarangKeluar
    lateinit var binding: ActivityAssetsKeluarDetailScanBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetsKeluarDetailScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantLogin(this)

        val str_json = intent.getStringExtra(Cons.JSON)

        dataBarang = Gson().fromJson(str_json,AssetListBarangKeluar::class.java)

        binding.tvTitle.text = dataBarang.itemName
        binding.tvSku.text = dataBarang.itemSku
        binding.tvGudang.text = dataBarang.warehouseName
        binding.tvCode.text = dataBarang.code

        binding.ivBarcode.setBarcodeText(dataBarang.code)

        try {

            binding.tvDescription.text = dataBarang.description
        }catch (e : Exception){
            e.printStackTrace()
            binding.tvDescription.text = " - "
        }

        binding.tvTanggal.text = DateTimeMasker.changeToDate(dataBarang.createdAt)
        binding.tvHour.text = DateTimeMasker.changeToHour(dataBarang.createdAt)


        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

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


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@AssetsKeluarDetailScanActivity)
    }
}
