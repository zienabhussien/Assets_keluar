package com.salor.ventgo.ui.adapter.assets_keluar.detail_assets

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.salor.ventgo.R
import com.salor.ventgo.ui.activity.assets_keluar.list.detail_scan.AssetsKeluarDetailScanActivity

class AssetsKeluarListScanAdapter(private val context: Activity, private val item_homes: List<String>) :
    RecyclerView.Adapter<AssetsKeluarListScanAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_assets_keluar_details_scan_adapter, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // set the data in items
        val item = item_homes[position]

        // TODO: 23/02/18 setDataList

        holder.tvTitle.text = item

        holder.cvParent.setOnClickListener {

            val intent = Intent(context,AssetsKeluarDetailScanActivity::class.java)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return item_homes.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var cvParent: CardView
        internal var tvTitle: TextView
        internal var tvWaktuScan: TextView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvWaktuScan = itemView.findViewById(R.id.tvWaktuScan)
            cvParent = itemView.findViewById(R.id.cvParent)

        }
    }



}
