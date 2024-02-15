package com.salor.ventgo.ui.activity.stock_opname_assets

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.transition.TransitionManager
import com.salor.ventgo.R
import com.salor.ventgo.db.DBS
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.See
import com.salor.ventgo.obj.warehouse_list.WarehouseList
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.stock_opname_assets.ListGudangAdapter
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityListGudangBinding
import com.salor.ventgo.databinding.DialogFailureCustomBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.IOException

class ListGudangActivity : BaseActivity() {

    var listWarehouse: ArrayList<WarehouseList> = ArrayList()
    lateinit var listGudangAdapter: ListGudangAdapter
    lateinit var binding: ActivityListGudangBinding
    lateinit var lLoading: LinearLayout

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListGudangBinding.inflate(layoutInflater)
        setContentView(binding.root)
         lLoading = binding.lLoading

        setStatusBarGradiantLogin(this)

        setData()

        binding.rBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        getDataList(binding.lLoading)

    }


    private fun getDataList(pbLoading: LinearLayout) {
        pbLoading.visibility = View.VISIBLE
        val service = ApiClient.getClient()

        val idUser = DBS.with(this).idUser.toInt()

        val call = service.warehouseList(idUser)

        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pbLoading.visibility = View.GONE
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_warehouse", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            val jsonDataArray = json.getJSONArray(Cons.ITEMS_DATA)

                            val listData = Gson().fromJson(jsonDataArray.toString(), Array<WarehouseList>::class.java).toList()

                            if(listData.isEmpty()){

                                setVisibleEmptyData()
                                pbLoading.visibility = View.GONE

                            }else{

                                setVisibleParent()

                                listWarehouse.addAll(listData)

                                listGudangAdapter.notifyDataSetChanged()

                                setAnimHeader()
                            }



                        } else {
                            pbLoading.visibility = View.GONE
                            See.toast(this@ListGudangActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.visibility = View.GONE
                    dialogFailure(resources.getString(R.string.label_failure_content_server_title),resources.getString(R.string.label_failure_content_server_content))

                    //       See.toast(this@ListGudangActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                pbLoading.visibility = View.GONE
                dialogFailure(resources.getString(R.string.label_failure_content1),resources.getString(R.string.label_failure_content2))
            }
        })

    }

    fun setData() {
        listGudangAdapter = ListGudangAdapter(this, listWarehouse,this)
        binding.rvList.setAdapter(listGudangAdapter)
        binding.rvList.isNestedScrollingEnabled = false

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setVisibleParent() {
        setAnimViewVisible(binding.lParentContent, binding.rvList, 0)
        setAnimViewGone(binding.lParentContent, binding.itemEmptyData.lParentEmptyData, 0)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setVisibleEmptyData() {
        binding.rvList.visibility = View.GONE
        setAnimViewVisible(binding.lParentContent, binding.itemEmptyData.lParentEmptyData, 0)

    }


    fun dialogFailure(title : String,subTitle : String) {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding = DialogFailureCustomBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)

            val btnBack: Button = binding.btnBack
            val btnRefresh: Button = binding.btnRefresh

            val tvContent: TextView = binding.tvContent
            val tvContent2: TextView = binding.tvContent2

            tvContent.text = title
            tvContent2.text = subTitle

            btnBack.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                onBackPressed()
            })

            btnRefresh.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                getDataList(lLoading)
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
                binding.tvListHeader.visibility = View.VISIBLE
//                rvList.visibility = View.VISIBLE

            }, 400)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvListHeader.visibility = View.VISIBLE
//            rvList.visibility = View.VISIBLE
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@ListGudangActivity)
    }
}
