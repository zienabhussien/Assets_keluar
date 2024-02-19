package com.salor.ventgo.ui.activity.stock_opname_assets.list_scanned_product.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.salor.ventgo.R
import com.salor.ventgo.databinding.ActivityStockOpnameDetailScannedProductBinding
import com.salor.ventgo.databinding.DialogFailureCustomBinding
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.Loading
import com.salor.ventgo.helper.See
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.riwayat_stock_opname_asset.list_asset.StockOpnameListAsset
import com.salor.ventgo.obj.riwayat_stock_opname_asset.list_asset.detail_asset_scanned.DetailItemScanned
import com.salor.ventgo.obj.riwayat_stock_opname_asset.list_scanned.ListScanned
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.stock_opname_assets.scanned_product.detail.ListDetailScannedProductStockOpnameAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StockOpnameListDetailScannedProductActivity : BaseActivity() {

    var isDelete : Boolean = false
    var status_barang : String = ""
    var id_stock_opname : String = ""
    lateinit var pbLoading : Loading
    lateinit var dataAsset : StockOpnameListAsset
    var listData : ArrayList<ListScanned> = ArrayList()
    lateinit var binding: ActivityStockOpnameDetailScannedProductBinding
    lateinit var listScannedProductStockOpnameAdapter: ListDetailScannedProductStockOpnameAdapter

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockOpnameDetailScannedProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantLogin(this)

        pbLoading = Loading(this)

        status_barang = intent.getStringExtra(Cons.STATUS_STOCK).toString()
        id_stock_opname = intent.getStringExtra(Cons.ID_STOCK_OPNAME).toString()
        val str_json = intent.getStringExtra(Cons.JSON)
        dataAsset = Gson().fromJson(str_json,StockOpnameListAsset::class.java)



        // TODO set data detail list assset
       binding.tvTitle.text = dataAsset.itemName
        binding.tvGudang.text = dataAsset.warehouse
        binding.tvQty.text = dataAsset.qty.toString()
        binding.tvDate.text = DateTimeMasker.changeToNameMonthCustom(dataAsset.lastInsert)

        // TODO set adapter
        setData()

        binding.rBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        getDataListScanned(binding.lLoading)

    }

    private fun getDataListScanned(pbLoading: LinearLayout) {
        pbLoading.visibility = View.VISIBLE
        binding.rvListGudang.visibility = View.GONE
        val service = ApiClient.getClient()

        val call = service.stockOpnameAssetItemScannedList(id_stock_opname.toInt(),dataAsset.idItem.toInt())

        See.logE("id_item", "" + dataAsset.idItem)
        See.logE("id_stock_opname_asset", "" + id_stock_opname)

        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: retrofit2.Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                pbLoading.visibility = View.GONE
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_asset_list", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            listData.clear()

                            binding.rvListGudang.visibility = View.VISIBLE
                            val jsonDataArray = json.getJSONArray(Cons.ITEMS_DATA)

                            val listAsset = Gson().fromJson(jsonDataArray.toString(), Array<ListScanned>::class.java).toList()

                            listData.addAll(listAsset)

                            listScannedProductStockOpnameAdapter.notifyDataSetChanged()

                            setAnimHeader()

                            val jsonDetail = json.getJSONObject("detail_item")

                            val detailItem = Gson().fromJson(jsonDetail.toString(),DetailItemScanned::class.java)

                            // TODO set data detail list assset
//                            tvTitle.text = detailItem.itemName
//                            tvGudang.text = detailItem.warehouse

                            try{
                                binding.tvQty.text = detailItem.qty.toString()
                            }catch (e : Exception){
                                e.printStackTrace()
                            }


                            try{
                                binding.tvDate.text = DateTimeMasker.changeDetailMonthCustom(detailItem.lastInsert)
                            }catch (e : Exception){
                                e.printStackTrace()
                                binding.tvDate.text = DateTimeMasker.changeToNameMonthCustom(dataAsset.lastInsert)

                            }

                            //  lAddStock.visibility = View.VISIBLE

                        } else {
                            pbLoading.visibility = View.GONE
                            See.toast(this@StockOpnameListDetailScannedProductActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.visibility = View.GONE
                    dialogFailure(resources.getString(R.string.label_failure_content_server_title),resources.getString(R.string.label_failure_content_server_content))

                    //            See.toast(this@StockOpnameListDetailScannedProductActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {

                pbLoading.visibility = View.GONE
                dialogFailure(resources.getString(R.string.label_failure_content1),resources.getString(R.string.label_failure_content2))

            }
        })

    }


    fun setData(){
        listScannedProductStockOpnameAdapter = ListDetailScannedProductStockOpnameAdapter(this, listData)
        binding.rvListGudang.adapter = listScannedProductStockOpnameAdapter
        binding.rvListGudang.isNestedScrollingEnabled = false
    }

    fun dialogFailure(title : String,subTitle : String) {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window!!.attributes.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DialogFailureCustomBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            val btnBack: Button = dialogBinding.btnBack
            val btnRefresh: Button = dialogBinding.btnRefresh

            val tvContent: TextView = dialogBinding.tvContent
            val tvContent2: TextView = dialogBinding.tvContent2

            tvContent.text = title
            tvContent2.text = subTitle


            btnBack.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                onBackPressed()
            })

            btnRefresh.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                getDataListScanned(binding.lLoading)
            })

            val size = Point()
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            display.getSize(size)
            val mWidth = size.x

            val window = dialog.window
            val wlp = window!!.attributes

            wlp.gravity = Gravity.CENTER
            wlp.x = 0
            wlp.y = 0
            wlp.width = mWidth
            window.attributes = wlp
            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAnimHeader() {
        try {
            Handler().postDelayed({
                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.tvDetailScan.visibility = View.VISIBLE
                binding.tvGudang.visibility = View.VISIBLE
                binding.tvQty.visibility = View.VISIBLE
                binding.tvDate.visibility = View.VISIBLE

            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvDetailScan.visibility = View.VISIBLE
            binding.tvGudang.visibility = View.VISIBLE
            binding.tvQty.visibility = View.VISIBLE
            binding.tvDate.visibility = View.VISIBLE
        }
    }

    fun deleteDataScanned(id : String) {
        pbLoading.showLoading(resources.getString(R.string.label_loading_title_dialog),false)
        val service = ApiClient.getClient()

        val call = service.stockOpnameAssetItemScannedDelete(id_stock_opname,id)
        See.logE("id_stock_opname_asset", "" + dataAsset.idItem)
        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: retrofit2.Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                pbLoading.dismissDialog()
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_asset_list", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            See.toast(this@StockOpnameListDetailScannedProductActivity,api_message)

                            isDelete = true

                            getDataListScanned(binding.lLoading)
                            //  lAddStock.visibility = View.VISIBLE

                        } else {
                            pbLoading.dismissDialog()
                            See.toast(this@StockOpnameListDetailScannedProductActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.dismissDialog()
                    See.toast(this@StockOpnameListDetailScannedProductActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {

                pbLoading.dismissDialog()
                See.toast(this@StockOpnameListDetailScannedProductActivity,resources.getString(R.string.label_koneksi_error))

            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        if(isDelete){

            val intent = Intent()
            setResult(Cons.RES_DETAIL_STOCK,intent)
            finish()
            setOveridePendingTransisi(this@StockOpnameListDetailScannedProductActivity)

        }else{
            super.onBackPressed()
            setOveridePendingTransisi(this@StockOpnameListDetailScannedProductActivity)
        }
    }
}
