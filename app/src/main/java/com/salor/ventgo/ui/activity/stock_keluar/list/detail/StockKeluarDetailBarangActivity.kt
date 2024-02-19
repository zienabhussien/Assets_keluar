package com.salor.ventgo.ui.activity.stock_keluar.list.detail

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityStockKeluarDetailBarangBinding
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.stock_list_barang_keluar.StockListBarangKeluar
import com.salor.ventgo.ui.activity.BaseActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class StockKeluarDetailBarangActivity : BaseActivity() {

    lateinit var stockBarangKeluar : StockListBarangKeluar
    lateinit var binding: ActivityStockKeluarDetailBarangBinding


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockKeluarDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantLogin(this)

       binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        val str_json = intent.getStringExtra(Cons.JSON)
        stockBarangKeluar = Gson().fromJson(str_json,StockListBarangKeluar::class.java)

        binding.tvTitle.text = stockBarangKeluar.itemName
        binding.tvSku.text = stockBarangKeluar.sku
        binding.tvNameWarehouse.text = stockBarangKeluar.warehouseName
        binding.typeQuantity.text = stockBarangKeluar.qtyType
        binding.tvQuantity.text = stockBarangKeluar.qty
        binding.tvDescription.text = stockBarangKeluar.description
        binding.tvTanggal.text = DateTimeMasker.changeToDate(stockBarangKeluar.createdAt)
        binding.tvJam.text = DateTimeMasker.changeToHour(stockBarangKeluar.createdAt)

       binding.ivBarcode.setBarcodeText(stockBarangKeluar.sku)

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
        setOveridePendingTransisi(this@StockKeluarDetailBarangActivity)
    }
}
