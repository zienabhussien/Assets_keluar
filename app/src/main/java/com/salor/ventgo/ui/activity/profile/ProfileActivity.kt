package com.salor.ventgo.ui.activity.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.room.Room
import androidx.transition.TransitionManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.salor.ventgo.R
import com.salor.ventgo.databinding.ActivityProfileBinding
import com.salor.ventgo.databinding.DialogCameraBinding
import com.salor.ventgo.databinding.ItemDialogLogoutBinding
import com.salor.ventgo.db.DBS
import com.salor.ventgo.db.dao_user.Database
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.LoadImage
import com.salor.ventgo.helper.See
import com.salor.ventgo.obj.login.DataUser
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.activity.login.LoginActivity
import com.salor.ventgo.ui.activity.profile.camera.UpdateProfileActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

class ProfileActivity : BaseActivity() {

    lateinit var dataUser : DataUser
    lateinit var userDatabase: Database
    lateinit var loadImage: LoadImage
    lateinit var binding: ActivityProfileBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        EasyImage.configuration(this)

        try {
            userDatabase = Room.databaseBuilder(applicationContext, Database::class.java,
                Cons.DB_NAME_LOGIN_USER).fallbackToDestructiveMigration().build()
        }catch (e : Exception){
            e.printStackTrace()
        }

        setStatusBarGradiantLogin(this)

        loadImage = LoadImage(this)


        binding.ivBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

        setAnim()

       binding.btnLogout.setOnClickListener(View.OnClickListener {
            dialogLogout()

        })

        try {
            fetchTodoById(Cons.INT_ID)
        }catch (e : Exception){
            e.printStackTrace()
        }


        binding.lChangePassword.setOnClickListener(View.OnClickListener {

            val intent = Intent(this,ChangePasswordActivity::class.java)
            startActivity(intent)
            setOveridePendingTransisi(this@ProfileActivity)

        })

       binding.ivProfilePhoto.setOnClickListener(View.OnClickListener {

            if (Build.VERSION.SDK_INT >= 23) {
                val permissionlistener = object : PermissionListener {
                    override fun onPermissionGranted() {

                        try {
                            dialog_camera()
                        }catch (e : Exception){

                            e.printStackTrace()
                        }
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                        Toast.makeText(this@ProfileActivity, resources.getString(R.string.label_permission_diperlukan), Toast.LENGTH_SHORT).show()
                    }
                }

                TedPermission(this@ProfileActivity)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage(resources.getString(R.string.label_rational_message_camera_photo))
                        .setDeniedMessage(resources.getString(R.string.label_denied_message))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

                        .check()


            } else {

                try {

                    dialog_camera()
                }catch (e : Exception){

                    e.printStackTrace()
                }
            }
        })

    }

    fun dialog_camera() {
        try {

            var dialog = Dialog(this, R.style.DialogLight)
            dialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding = DialogCameraBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(R.layout.dialog_camera)
            dialog.setCancelable(true)

           binding.btnGallery.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                EasyImage.openGallery(this@ProfileActivity, Cons.REQUEST_UPDATE_PHOTO)
            })

           binding.btnCamera.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
                EasyImage.openCamera(this@ProfileActivity, Cons.REQUEST_UPDATE_PHOTO)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        See.logE("Request Code ", "$requestCode Result Code : $resultCode")

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                //Some error handling
                e!!.printStackTrace()
            }

            override fun onImagePicked(imageFile: File, source: EasyImage.ImageSource, type: Int) {

                See.logE("type", "" + type)

                //                fileUtils.cropImageActivity(imageFile, type);
                See.logE("file_photo", "" + imageFile)

                if (type == Cons.REQUEST_UPDATE_PHOTO) {


                    val i = Intent(this@ProfileActivity, UpdateProfileActivity::class.java)
                    i.putExtra("str_photo", Uri.fromFile(imageFile).path)

                    startActivityForResult(i, Cons.REQ_CAMERA)

                    // dari gallery convers 2
                }

            }


            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(this@ProfileActivity)
                    photoFile?.delete()
                }

            }


        })

        if (requestCode == Cons.REQ_CAMERA && resultCode == Cons.RES_Camera) {

           binding.pbLoadingProfile.visibility = View.VISIBLE
            val photo = data!!.getStringExtra(Cons.FilePhoto)
            loadImage.LoadImagePicassoMkLoader(DBS.with(this@ProfileActivity).dataImageProfile,
                binding.ivProfilePhoto,binding.pbLoadingProfile)

        }


    }


    fun setAnim() {

        try {
            Handler().postDelayed({

                TransitionManager.beginDelayedTransition(binding.lParentContent)
                binding.lChangePassword.visibility = View.VISIBLE
//                rvList.visibility = View.VISIBLE


            }, 700)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.lChangePassword.visibility = View.VISIBLE
//            rvList.visibility = View.VISIBLE
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

                    loadImage.LoadImagePicassoMkLoader(DBS.with(this@ProfileActivity).dataImageProfile,
                        binding.ivProfilePhoto,binding.pbLoadingProfile)

                   binding.tvName.text = dataUser.name
                   binding.tvEmail.text = dataUser.email
                   binding.tvPhoneNumber.text = dataUser.phoneNumber
                   binding.tvAlamat.text = dataUser.address


                }
            }.execute(todo_id)
        }catch (e : Exception){
            e.printStackTrace()
        }


    }


    fun dialogLogout() {
        try {
            val dialog = Dialog(this, R.style.DialogLight)
            dialog.window?.attributes?.windowAnimations = R.style.PauseDialogAnimation
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding = ItemDialogLogoutBinding.inflate(LayoutInflater.from(this))
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)


            binding.btnYa.setOnClickListener(View.OnClickListener {
                dialog.dismiss()

                DBS.with(this@ProfileActivity).isLogin = false

                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                setOveridePendingTransisi(this@ProfileActivity)

            })

           binding.btnTidak.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })

            val size = Point()
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            display.getSize(size)
            val mWidth = size.x

            val window = dialog.window
            val wlp = window!!.attributes as WindowManager.LayoutParams

            wlp.gravity = Gravity.CENTER
            wlp.x = 0
            wlp.y = 0
            wlp.width = mWidth
            window.attributes = wlp
            dialog.show()
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
        setOveridePendingTransisi(this@ProfileActivity)
    }
}
