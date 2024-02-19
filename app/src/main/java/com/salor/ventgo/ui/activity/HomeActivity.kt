package com.salor.ventgo.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.room.Room
import com.salor.ventgo.R
import com.salor.ventgo.databinding.ActivityHomeBinding
import com.salor.ventgo.databinding.DialogTopHomeBinding
import com.salor.ventgo.db.DBS
import com.salor.ventgo.db.dao_user.Database
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.LoadImage
import com.salor.ventgo.obj.DataHome
import com.salor.ventgo.obj.login.DataUser
import com.salor.ventgo.ui.activity.profile.ProfileActivity
import com.salor.ventgo.ui.adapter.HomeAdapter
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class HomeActivity : BaseActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var dataUser : DataUser
    lateinit var userDatabase: Database
    var isAssets: Boolean = true
    lateinit var loadImage: LoadImage
    var listDataHome: ArrayList<DataHome> = ArrayList()
    lateinit var homeAdapter: HomeAdapter
    lateinit var tvTitleToolbar : TextView

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
           tvTitleToolbar = binding.tvTitleToolbar

        try {
            userDatabase = Room.databaseBuilder(applicationContext, Database::class.java, Cons.DB_NAME_LOGIN_USER).fallbackToDestructiveMigration().build()
        }catch (e : Exception){
            e.printStackTrace()
        }

        setStatusBarGradiantLogin(this)

        loadImage = LoadImage(this)

//        rvList.visibility = View.GONE

        setData()

        binding.tvTitleToolbar.setOnClickListener(View.OnClickListener {

            dialogSelectProduct(applicationContext)

        })

        binding.lProfile.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
            setOveridePendingTransisi(this@HomeActivity)

        })


    }

    override fun onResume() {
        super.onResume()
        try {
            fetchTodoById(Cons.INT_ID)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun setData() {
      //  rvList.visibility = View.GONE
        //setAnimViewGone(lParentContent,rvList)

        listDataHome.clear()

        listDataHome.add(DataHome(1, resources.getString(R.string.label_menu_title_menu_asset_masuk), resources.getString(R.string.label_menu_desc_1_menu_asset_masuk), resources.getString(R.string.label_menu_desc_2_menu_asset_masuk), R.drawable.ic_barang_masuk))
        listDataHome.add(DataHome(2, resources.getString(R.string.label_menu_title_menu_asset_keluar), resources.getString(R.string.label_menu_desc_1_menu_asset_keluar), resources.getString(R.string.label_menu_desc_2_menu_asset_keluar), R.drawable.ic_barang_keluar))
        listDataHome.add(DataHome(3, resources.getString(R.string.label_menu_title_menu_stock_opname), resources.getString(R.string.label_menu_desc_1_menu_stock_opname), resources.getString(R.string.label_menu_desc_2_menu_stock_opname), R.drawable.ic_stock_opname))
        listDataHome.add(DataHome(4, resources.getString(R.string.label_menu_title_menu_barang_assets), resources.getString(R.string.label_menu_desc_1_menu_barang_assets), resources.getString(R.string.label_menu_desc_2_menu_barang_assets), R.drawable.ic_product_assets))

        homeAdapter = HomeAdapter(this, listDataHome, this)
        binding.rvList.adapter = homeAdapter

        binding.rvList.isNestedScrollingEnabled = false

     //   setAnimViewVisible(lParentContent,rvList,0)

    }

    fun setDataStock() {

   //     rvList.visibility = View.GONE
  //      setAnimViewGone(lParentContent,rvList)

        listDataHome.clear()

        listDataHome.add(DataHome(1, resources.getString(R.string.label_menu_title_menu_stock_masuk), resources.getString(R.string.label_menu_desc_1_menu_asset_masuk), resources.getString(R.string.label_menu_desc_2_menu_asset_masuk), R.drawable.ic_barang_masuk))
        listDataHome.add(DataHome(2, resources.getString(R.string.label_menu_title_menu_stock_keluar), resources.getString(R.string.label_menu_desc_1_menu_asset_keluar), resources.getString(R.string.label_menu_desc_2_menu_asset_keluar), R.drawable.ic_barang_keluar))
        listDataHome.add(DataHome(3, resources.getString(R.string.label_menu_title_menu_stock_opname), resources.getString(R.string.label_menu_desc_1_menu_stock_opname), resources.getString(R.string.label_menu_desc_2_menu_stock_opname), R.drawable.ic_stock_opname))
        listDataHome.add(DataHome(4, resources.getString(R.string.label_menu_title_menu_barang_stock), resources.getString(R.string.label_menu_desc_1_menu_barang_assets), resources.getString(R.string.label_menu_desc_2_menu_barang_assets), R.drawable.ic_product_assets))

        homeAdapter = HomeAdapter(this, listDataHome, this)
        binding.rvList.adapter = homeAdapter
        binding.rvList.isNestedScrollingEnabled = false

     //   setAnimViewVisible(lParentContent,rvList,0)


    }


    fun setAnimHeader() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.tvEmail.visibility = View.VISIBLE

            }, 700)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvEmail.visibility = View.VISIBLE
        }


    }


//    fun setAnimArrow() {
//
//        try {
//            Handler().postDelayed({
//
//                TransitionManager.beginDelayedTransition(lParentContent)
//                ivImageArrow.visibility = View.VISIBLE
//
//            }, 700)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            ivImageArrow.visibility = View.VISIBLE
//        }
//
//
//    }

//    fun setAnimList() {
//
//        try {
//            Handler().postDelayed({
//
//                TransitionManager.beginDelayedTransition(lParentContent)
//                rvList.visibility = View.VISIBLE
//
//
//            }, 150)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            rvList.visibility = View.VISIBLE
//        }
//
//
//    }

    fun dialogSelectProduct(context: Context) {
        try {
            val binding = DialogTopHomeBinding.inflate(LayoutInflater.from(context))
            val pDialog = Dialog(context, R.style.DialogLight)
            pDialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            pDialog.setContentView(binding.root)
            pDialog.setCancelable(true)

            // Initialize views
            val lAssets = binding.lAssets
            val lStock = binding.lStock
            val ivCheckAssets = binding.ivCheckAssets
            val ivCheckStock = binding.ivCheckStock
            val tvStock = binding.tvStock
            val tvAssets = binding.tvAssets
            val tvTitleToolbarDialog = binding.tvTitleToolbarDialog

            // Handle UI based on isAssets flag
            if (isAssets) {
                tvAssets?.setTextColor(context.resources.getColor(R.color.color_tv_blue_btn_login))
                tvStock?.setTextColor(context.resources.getColor(R.color.color_gray_tv_name_profil))
                ivCheckAssets?.visibility = View.VISIBLE
                ivCheckStock?.visibility = View.GONE
                tvTitleToolbarDialog?.text = context.resources.getString(R.string.label_assets)
            } else {
                tvAssets?.setTextColor(context.resources.getColor(R.color.color_gray_tv_name_profil))
                tvStock?.setTextColor(context.resources.getColor(R.color.color_tv_blue_btn_login))
                ivCheckAssets?.visibility = View.GONE
                ivCheckStock?.visibility = View.VISIBLE
                tvTitleToolbarDialog?.text = context.resources.getString(R.string.label_stock)
            }

            // Set click listeners
            lStock?.setOnClickListener {
                pDialog?.dismiss()
                isAssets = false
                setDataStock()
                tvTitleToolbar.text = context.resources.getString(R.string.label_stock)
            }

            lAssets?.setOnClickListener {
                pDialog?.dismiss()
                isAssets = true
                setData()
                tvTitleToolbar.text = context.resources.getString(R.string.label_assets)
            }

            // Set dialog width
            val size = Point()
            val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getSize(size)
            val mWidth = size.x
            val window = pDialog?.window
            val wlp = window?.attributes as WindowManager.LayoutParams
            wlp.gravity = Gravity.TOP
            wlp.x = 0
            wlp.y = 0
            wlp.width = mWidth
            window.attributes = wlp

            // Show dialog
            pDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun fetchTodoById(todo_id: Int) {

        try {
            object : AsyncTask<Int, Void, DataUser>() {
                override fun doInBackground(vararg p0: Int?): DataUser? {

                    return userDatabase.daoAccess().fetchUserListById(p0[0]!!)

                }

                override fun onPostExecute(user: DataUser) {
                    super.onPostExecute(user)
                    dataUser = user

                    loadImage.LoadImagePicassoMkLoader(DBS.with(this@HomeActivity).dataImageProfile,
                        binding.ivPhotoProfile,binding.pbLoadingProfile)

                    binding.tvName.text = dataUser.name
                    binding.tvEmail.text = dataUser.email

                    setAnimHeader()
                    //setAnimArrow()

                }
            }.execute(todo_id)
        }catch (e : Exception){
            e.printStackTrace()
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
