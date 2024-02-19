package com.salor.ventgo.ui.activity.barang_stock.detail_stock

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.salor.ventgo.R
import com.salor.ventgo.databinding.ActivityListBarangStockDetailBinding
import com.salor.ventgo.databinding.DialogFailureCustomBinding
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.See
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.stock_barang_list.BarangStock
import com.salor.ventgo.obj.stock_barang_list.detail.StockBarangDetail
import com.salor.ventgo.obj.stock_barang_list.detail.WarehouseStock
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.barang_stock.detail_list_gudang.ListGudangStockAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DetailBarangStockActivity : BaseActivity() {

    var warehouseStockList : ArrayList<WarehouseStock> = ArrayList()
    lateinit var barangStock : BarangStock
    lateinit var listGudangAdapter : ListGudangStockAdapter
    lateinit var binding: ActivityListBarangStockDetailBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBarangStockDetailBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_list_barang_stock_detail)

        setStatusBarGradiantListSearch(this)

        setData()

        val str_json = intent.getStringExtra(Cons.JSON)
        barangStock = Gson().fromJson(str_json,BarangStock::class.java)

        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })


        getDataListWarehouse(binding.lLoading)
    }

    fun setData(){

        listGudangAdapter = ListGudangStockAdapter(this, warehouseStockList)
        binding.rvListGudang.setAdapter(listGudangAdapter)
       binding.rvListGudang.isNestedScrollingEnabled = false

    }

    private fun getDataListWarehouse(pbLoading: LinearLayout) {
        pbLoading.visibility = View.VISIBLE
        val service = ApiClient.getClient()

        val call = service.stockBarangDetail(barangStock.id)

        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pbLoading.visibility = View.GONE
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_barang_asset", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            binding.cvParent.visibility = View.VISIBLE

                            val listProduk = Gson().fromJson(json.toString(), StockBarangDetail::class.java)

                            binding.tvTitle.text = listProduk.name
                            binding.tvSku.text = listProduk.sku
                            binding.tvQty.text = listProduk.stock + " " + resources.getString(R.string.label_pcs)
                            binding.tvDate.text = DateTimeMasker.changeToNameMonthCustom(listProduk.createdAt)

                            binding.ivBarcode.setBarcodeText(listProduk.sku)

                            val jsonDataArray = json.getJSONArray(Cons.ITEMS_WAREHOUSE)

                            val listData = Gson().fromJson(jsonDataArray.toString(), Array<WarehouseStock>::class.java).toList()

                            warehouseStockList.addAll(listData)

                            listGudangAdapter.notifyDataSetChanged()

                            setAnimHeader()

                        } else {
                            pbLoading.visibility = View.GONE
                            See.toast(this@DetailBarangStockActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.visibility = View.GONE
                    dialogFailure("list",resources.getString(R.string.label_failure_content_server_title),resources.getString(R.string.label_failure_content_server_content))

                    //        See.toast(this@DetailBarangStockActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                pbLoading.visibility = View.GONE
                dialogFailure("list",resources.getString(R.string.label_failure_content1),resources.getString(R.string.label_failure_content2))
            }
        })

    }



    fun setAnimHeader() {

        try {
            Handler().postDelayed({
                TransitionManager.beginDelayedTransition(binding.lParentContent)
                 binding.tvDetailGudang.visibility = View.VISIBLE

            }, 700)
        } catch (e: Exception) {
            e.printStackTrace()
           binding.tvDetailGudang.visibility = View.VISIBLE
        }

    }

    fun dialogFailure(type: String,title : String,subTitle : String) {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DialogFailureCustomBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(false)

            dialogBinding.tvContent.text = title
            dialogBinding.tvContent2.text = subTitle

            dialogBinding.btnBack.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                onBackPressed()
            })

            dialogBinding.btnRefresh.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                if (type == "list") {
                    getDataListWarehouse(binding.lLoading)
                }

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


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@DetailBarangStockActivity)
    }
}
