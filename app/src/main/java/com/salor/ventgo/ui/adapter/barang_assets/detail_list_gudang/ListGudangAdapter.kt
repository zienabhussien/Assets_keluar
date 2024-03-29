package com.salor.ventgo.ui.adapter.barang_assets.detail_list_gudang

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.salor.ventgo.R
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.obj.assets_barang_list.detail.WarehouseAssets
import com.salor.ventgo.ui.activity.barang_assets.detail_barang.DetailBarangActivity
import com.salor.ventgo.ui.activity.barang_assets.detail_gudang.DetailGudangActivity
import com.google.gson.Gson

class ListGudangAdapter(private val context: Activity, private val item_homes: List<WarehouseAssets>,
                        var detailActivity : DetailBarangActivity) : RecyclerView.Adapter<ListGudangAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_barang_gudang_adapter, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // set the data in items
        val item = item_homes[position]

        // TODO: 23/02/18 setDataList

        holder.tvTitle.text = item.warehouseName
        holder.tvQty.text = item.qty

        holder.cvParent.setOnClickListener {

            val intent = Intent(context, DetailGudangActivity::class.java)
            intent.putExtra(Cons.JSON,Gson().toJson(item))
            intent.putExtra(Cons.JSON_BARANG_ASSETS,detailActivity.str_json)
            context.startActivity(intent)
            detailActivity.setOveridePendingTransisi(detailActivity)

        }

    }

    override fun getItemCount(): Int {
        return item_homes.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var cvParent: CardView
        internal var tvTitle: TextView
        internal var tvQty: TextView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvQty = itemView.findViewById(R.id.tvQty)
            cvParent = itemView.findViewById(R.id.cvParent)

        }
    }

    fun gpsState(): Boolean {
        val isGPSon: Boolean
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val statusgps1 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val statusgps2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!statusgps1 && !statusgps2) {
            isGPSon = false
        } else {
            isGPSon = true
        }
        return isGPSon
    }

    fun showDialogEnableGPS() {
        val builder = AlertDialog.Builder(
                context)
        builder.setTitle("Notification")
        builder.setMessage("Make sure GPS is active")

        builder.setPositiveButton("OK"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
            dialog.dismiss()
        }
        builder.show()
    }


}
