package com.salor.ventgo.ui.activity.barang_stock

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.transition.TransitionManager
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.See
import com.salor.ventgo.obj.stock_barang_list.BarangStock
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.adapter.barang_stock.ListBarangAdapter
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityListBarangStockBinding
import com.salor.ventgo.databinding.DialogFailureCustomBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.IOException
import java.util.*

class ListBarangActivity : BaseActivity() {

    lateinit var timer : Timer
    var str_keyword_search : String = ""
    var offset : Int = 0
    var limit : Int = 20
    var isAnim : Boolean = false
    var isNotLoad : Boolean = false
    var listData : ArrayList<BarangStock> = ArrayList()
    lateinit var listBarangAdapter : ListBarangAdapter
    lateinit var binding: ActivityListBarangStockBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBarangStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarGradiantListSearch(this)

        timer = Timer()

        setData()

        binding.rBack.setOnClickListener(View.OnClickListener { onBackPressed() })

        // TODO: 25/07/18 detect nested scroll to bottom
        binding.nestedscrollview.getViewTreeObserver().addOnScrollChangedListener(ViewTreeObserver
            .OnScrollChangedListener {
            val totalHeight = binding.nestedscrollview.getChildAt(0).getHeight()
            val scrollY = binding.nestedscrollview.getScrollY()
            val isBottomReached = binding.nestedscrollview.canScrollVertically(1)

            if (!isBottomReached) {
                if (!isNotLoad) {
                   binding.pbLoadingBottom.visibility = View.VISIBLE

                    offset += 20

                    getDataListBarang(binding.lBottomLoading,false)

                }
            }

        })

        // TODO search barang asset
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

//                rvListBarang.visibility = View.GONE
//                lLoading.visibility = View.VISIBLE
//
//                str_keyword_search = etSearch.getText().toString()
//
//                timer.cancel()
//                timer = Timer()
//                timer.schedule(
//                        object : TimerTask() {
//                            override fun run() {
//
//                                // TODO: do what you need here (refresh list)
//                                try {
//                                    this@ListBarangActivity.runOnUiThread(Runnable {
//                                        try {
//
//                                            listData.clear()
//                                            offset = 0
//
//                                            getDataListBarang(lLoading,true)
//
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                        }
//                                    })
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                                // you will probably need to use runOnUiThread(Runnable action) for some specific actions
//                            }
//                        },
//                        5000
//                )
            }
        })

        // TODO set ime click Search
        binding.etSearch.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                str_keyword_search = binding.etSearch.getText().toString()

                isNotLoad = false

                hideKeyboardwithoutPopulate(this@ListBarangActivity)

                binding.rvListBarang.visibility = View.GONE
                listData.clear()
                offset = 0

                getDataListBarang(binding.lLoading,true)
            }
            false
        }


        getDataListBarang(binding.lLoading,false)
    }

    fun setData(){

        listBarangAdapter = ListBarangAdapter(this, listData,this)
        binding.rvListBarang.setAdapter(listBarangAdapter)
        binding.rvListBarang.isNestedScrollingEnabled = false

    }

    private fun getDataListBarang(pbLoading : LinearLayout, isSearch : Boolean) {
        pbLoading.visibility = View.VISIBLE
        val service = ApiClient.getClient()

        val call = service.stockOpnameStock(limit,offset,str_keyword_search)

        See.logE(Cons.CALLRESPONSE, "" + call.request())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pbLoading.visibility = View.GONE
                binding.rvListBarang.visibility = View.VISIBLE
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        See.logE("respon_barang_stock", respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            val jsonDataArray = json.getJSONArray(Cons.ITEMS_DATA)

                            val listBarang = Gson().fromJson(jsonDataArray.toString(), Array<BarangStock>::class.java).toList()

                            listData.addAll(listBarang)

                            listBarangAdapter.notifyDataSetChanged()

                            if (!isSearch){
                                if (listData.isEmpty()){
                                    isNotLoad = true
                                }
                            }

                            if(!isAnim){
                                setAnimHeader()
                            }

                            if(offset == 0){

                                if (listData.isEmpty()){
                                    setVisibleEmptyData()

                                    return
                                }else{
                                    setVisibleParent()
                                    return
                                }

                            }

                        } else {
                            pbLoading.visibility = View.GONE
                            See.toast(this@ListBarangActivity, api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {

                    pbLoading.visibility = View.GONE
                    dialogFailure("list",resources.getString(R.string.label_failure_content_server_title),resources.getString(R.string.label_failure_content_server_content))

                    //   See.toast(this@ListBarangActivity, resources.getString(R.string.label_something_wrong))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                pbLoading.visibility = View.GONE
                dialogFailure("list",resources.getString(R.string.label_failure_content1),resources.getString(R.string.label_failure_content2))
            }
        })

    }

    fun setAnimHeader() {

        isAnim = true
        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.ivSearch.visibility = View.VISIBLE


            }, 400)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.ivSearch.visibility = View.VISIBLE
        }


    }


    fun setVisibleParent(){
        setAnimViewVisible(binding.lParentContent,binding.rvListBarang,0)
        setAnimViewGone(binding.lParentContent,binding.layoutEmptyData.lParentEmptyData,0)
    }

    fun setVisibleEmptyData(){
        binding.rvListBarang.visibility = View.GONE
        setAnimViewVisible(binding.lParentContent,binding.layoutEmptyData.lParentEmptyData,0)
    }

    fun dialogFailure(type : String,title : String,subTitle : String) {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialgBinding = DialogFailureCustomBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(dialgBinding.root)
            dialog.setCancelable(false)


            dialgBinding.tvContent.text = title
            dialgBinding.tvContent2.text = subTitle

            dialgBinding.btnBack.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                onBackPressed()
            })

            dialgBinding.btnRefresh.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                if (type == "list"){

                    getDataListBarang(binding.lLoading,false)
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
        setOveridePendingTransisi(this@ListBarangActivity)
    }
}
