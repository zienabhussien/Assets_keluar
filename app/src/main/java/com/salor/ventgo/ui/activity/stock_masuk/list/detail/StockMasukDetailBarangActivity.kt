package com.salor.ventgo.ui.activity.stock_masuk.list.detail

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.transition.TransitionManager
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.stock_list_barang_masuk.StockListBarangMasuk
import com.salor.ventgo.ui.activity.BaseActivity
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityStockMasukDetailBarangBinding
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class StockMasukDetailBarangActivity : BaseActivity() {

    lateinit var stockBarangMasuk : StockListBarangMasuk
    lateinit var binding: ActivityStockMasukDetailBarangBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockMasukDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantLogin(this)

        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        val str_json = intent.getStringExtra(Cons.JSON)
        stockBarangMasuk = Gson().fromJson(str_json,StockListBarangMasuk::class.java)

        binding.tvTitle.text = stockBarangMasuk.itemName
        binding.tvSku.text = stockBarangMasuk.sku
        binding.tvNameWarehouse.text = stockBarangMasuk.warehouseName
        binding.tvTypeQuantity.text = stockBarangMasuk.qtyType
        binding.tvQuantity.text = stockBarangMasuk.qty
        binding.tvDescription.text = stockBarangMasuk.description
        binding.tvTanggal.text = DateTimeMasker.changeToDate(stockBarangMasuk.createdAt)
       binding.tvJam.text = DateTimeMasker.changeToHour(stockBarangMasuk.createdAt)

        try {
           binding.tvPno.text = stockBarangMasuk.noPo
        }catch (e : Exception){
            e.printStackTrace()
            binding.tvPno.text = " - "
        }

        binding.ivBarcode.setBarcodeText(stockBarangMasuk.sku)
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
        setOveridePendingTransisi(this@StockMasukDetailBarangActivity)
    }
}
