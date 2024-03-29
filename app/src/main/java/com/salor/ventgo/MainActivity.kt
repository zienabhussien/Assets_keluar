package com.salor.ventgo

//

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.salor.ventgo.ui.activity.BaseActivity
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class MainActivity : BaseActivity() {

    override fun attachBaseContext(newBase: Context) {
        //super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun initToolbar(vTitle: String) {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val viewActionBar = layoutInflater.inflate(R.layout.toolbar_layout, null)
        val params = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER)

        val tvTitle = viewActionBar.findViewById<View>(R.id.actionbar_textview) as TextView
        tvTitle.text = vTitle
        supportActionBar!!.setCustomView(viewActionBar, params)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
