package com.salor.ventgo.ui.adapter.stock_opname_assets.scanned_product.detail

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.salor.ventgo.R
import com.salor.ventgo.databinding.ItemDialogStockOpnameDeleteBarangBinding
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.date.DateTimeMasker
import com.salor.ventgo.obj.riwayat_stock_opname_asset.list_scanned.ListScanned
import com.salor.ventgo.ui.activity.stock_opname_assets.list_scanned_product.detail.StockOpnameListDetailScannedProductActivity


class ListDetailScannedProductStockOpnameAdapter(private val context: StockOpnameListDetailScannedProductActivity,
                                                 private val item_homes: List<ListScanned>) : RecyclerView.Adapter<ListDetailScannedProductStockOpnameAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_stock_opname_detail_list_scanned_product, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(v)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // set the data in items
        val item = item_homes[position]


        // TODO: 23/02/18 setDataList

        holder.tvTitle.text = item.code
        holder.tvWaktuScan.text = DateTimeMasker.changeToNameMonthCustom(item.createdAt)

        if (context.status_barang == Cons.PUBLISH_STATUS){

            holder.ivDelete.visibility = View.GONE

        }else if (context.status_barang == Cons.STATUS_PENDING){

            holder.ivDelete.visibility = View.GONE

        }else{

            holder.ivDelete.visibility = View.VISIBLE

        }


        holder.ivDelete.setOnClickListener(View.OnClickListener { dialogDeleteBarang(item.id) })

    }

    override fun getItemCount(): Int {
        return item_homes.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivDelete : ImageView
        internal var cvParent: CardView
        internal var tvTitle: TextView
        internal var tvWaktuScan: TextView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvWaktuScan = itemView.findViewById(R.id.tvWaktuScan)
            cvParent = itemView.findViewById(R.id.cvParent)
            ivDelete = itemView.findViewById(R.id.ivDelete)

        }
    }

    fun dialogDeleteBarang(id : String) {

        try {

            val pDialog = Dialog(context, R.style.DialogLight)
            pDialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding = ItemDialogStockOpnameDeleteBarangBinding.inflate(LayoutInflater.from(context))
            pDialog.setContentView(binding.root)
            pDialog.setCancelable(false)

            val btnTidak = binding.btnTidak
            val btnYa = binding.btnYa


            btnTidak.setOnClickListener(View.OnClickListener { pDialog.dismiss() })

            btnYa.setOnClickListener(View.OnClickListener {
                pDialog.dismiss()
                context.deleteDataScanned(id)
            })

            val size = Point()
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            display.getSize(size)
            val mWidth = size.x

            val window = pDialog!!.window
            val wlp = window!!.attributes as WindowManager.LayoutParams

            wlp.gravity = Gravity.CENTER
            wlp.x = 0
            wlp.y = 0
            wlp.width = mWidth
            window.attributes = wlp
            pDialog!!.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
