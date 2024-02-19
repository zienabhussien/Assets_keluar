package com.salor.ventgo.ui

import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.salor.ventgo.R
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump


class MyApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        // TODO: 09/03/18 set CalligraphyConfig Text Font
//        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/open_sans_regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        )

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(resources.getString(R.string.splash_text))
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )


        Stetho.initializeWithDefaults(this)


    }

}
