package com.salor.ventgo.ui.activity.login

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.transition.TransitionManager
import com.salor.ventgo.R
import com.salor.ventgo.db.DBS
import com.salor.ventgo.db.dao_user.Database
import com.salor.ventgo.helper.Cons
import com.salor.ventgo.helper.Loading
import com.salor.ventgo.helper.See
import com.salor.ventgo.obj.login.DataUser
import com.salor.ventgo.service.ApiClient
import com.salor.ventgo.ui.activity.BaseActivity
import com.salor.ventgo.ui.activity.HomeActivity
import com.google.gson.Gson
import com.salor.ventgo.databinding.ActivityLoginBinding
import com.salor.ventgo.databinding.ActivityLoginLibBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.io.IOException

class LoginActivity : BaseActivity() {


    lateinit var userDatabase: Database
    lateinit var pLoading : Loading
    var str_email : String = ""
    var str_password : String = ""
    lateinit var binding: ActivityLoginLibBinding


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginLibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            userDatabase = Room.databaseBuilder(applicationContext, Database::class.java, Cons.DB_NAME_LOGIN_USER).fallbackToDestructiveMigration().build()

        }catch (e : Exception){
            e.printStackTrace()
        }


        pLoading = Loading(this)
        setStatusBarGradiantLogin(this)

        setAnimHeader()

        binding.btnLogin.setOnClickListener {

            str_email = binding.etEmail.text.toString()
            str_password = binding.etPassword.text.toString()

            if (str_email == ""){

                binding.etEmail.requestFocus()
                binding.etEmail.error = resources.getString(R.string.label_form_wajib_diisi)

            }else if (str_password == ""){

               binding.etPassword.requestFocus()
               binding.etPassword.error = resources.getString(R.string.label_form_wajib_diisi)

            }else{
                deleteAllUser()
            }

        }

        binding.tvLupaSandi.setOnClickListener {
            val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
            setOveridePendingTransisi(this)
        }

        binding.tfbEmail.setOnClickListener {
            binding.etEmail.requestFocus()
            showKeyboard(binding.etEmail)
        }

        binding.tfbPassword.setOnClickListener {
            binding.etPassword.requestFocus()
            showKeyboard(binding.etPassword)
        }

    }

    private fun loginServer() {
        pLoading.showLoading(resources.getString(R.string.label_loading_title_dialog), false)
        val service = ApiClient.getClient()
        val gson = Gson()

        val call = service.userLogin(str_email, str_password)
        See.logE(Cons.CALLRESPONSE,""+call.request())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                pLoading.dismissDialog()
                if (response.isSuccessful) {
                    try {
                        val respon = response.body()!!.string()
                        val json = JSONObject(respon)

                        val api_status = json.getInt(Cons.API_STATUS)
                        val api_message = json.getString(Cons.API_MESSAGE)

                        if (api_status == Cons.INT_STATUS) {

                            val datum: DataUser = gson.fromJson(json.toString(), DataUser::class.java)

                            val data = DataUser(Cons.INT_ID,
                                    datum.id,
                                    datum.name,
                                    datum.photo,
                                    datum.email,
                                    datum.idCmsPrivileges,
                                    datum.status,
                                    datum.password,
                                    datum.phoneNumber,
                                    datum.address

                            )


                            DBS.with(this@LoginActivity).saveIdUser(datum.id)
                            DBS.with(this@LoginActivity).saveDataImageProfile(datum.photo)
                            DBS.with(this@LoginActivity).savePasswordUser(str_password)

                            // todo save data
                            insertRow(data)


                        } else {
                            Toast.makeText(this@LoginActivity, api_message, Toast.LENGTH_SHORT).show()
                            Log.e("toastlogin", api_message)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    pLoading.dismissDialog()
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.label_something_wrong), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                pLoading.dismissDialog()
                Toast.makeText(this@LoginActivity, resources.getString(R.string.label_koneksi_error), Toast.LENGTH_SHORT).show()
            }
        })

    }

    @SuppressLint("StaticFieldLeak")
    private fun deleteAllUser() {
        try {
            object : AsyncTask<String, Void, String>() {
                override fun doInBackground(vararg params: String?): String? {

                    userDatabase.daoAccess().deleteAll()

                    return ""
                }

                override fun onPostExecute(result: String?) {

                    loginServer()
                }
            }.execute()
        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    @SuppressLint("StaticFieldLeak")
    private fun insertRow(user: DataUser) {

        try {
            object : AsyncTask<DataUser, Void, Long>() {
                override fun doInBackground(vararg params: DataUser): Long? {
                    return userDatabase.daoAccess().insertUser(params[0])
                }

                override fun onPostExecute(id: Long?) {
                    super.onPostExecute(id)

//                    loadAllAuth()

                    DBS.with(this@LoginActivity).isLogin = true

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    setOveridePendingTransisi(this@LoginActivity)


                }
            }.execute(user)
        }catch (e : Exception){
            e.printStackTrace()
        }


    }

    fun setAnimHeader() {
        try {
            Handler().postDelayed({

                try {
                    TransitionManager.beginDelayedTransition(binding.lParentContent)
                    binding.tvSubHeader.visibility = View.VISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }, 750)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvSubHeader.visibility = View.VISIBLE
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setOveridePendingTransisi(this@LoginActivity)
    }
}
