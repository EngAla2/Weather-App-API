package com.example.weatherapp.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.RemoteViews
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ListViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
var icon :String? = null
var main :String? = null
class WeatherAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.weather_app_widget)
    println("--------------------------------------------------------------------------------")
    println("--------------------------------------------------------------------------------")

    try{
        weatherTask().execute()
        val calendar = Calendar.getInstance()
        val date = calendar.time

        // full name form of the day
        while (main==null){weatherTask().execute()}
        var day =SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)
        views.setTextViewText(R.id.appwidget_temp_day, day + ", "+  main.toString() + "°C")


    }catch (e: Exception){
        println("---------------toStringtoString-----------------------------------------------------------------")
        print(e.toString())
        println("--------------------------------------------------------------------------------")

    }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

class weatherTask() : AsyncTask<String, Void, String>() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): String? {
        var response:String?
        try{
            response = URL("https://api.openweathermap.org/data/2.5/weather?q=Zarqa&units=metric&exclude=hourly,daily&appid=06c921750b9a82d8f5d1294e1586276f").readText(
                Charsets.UTF_8
            )
        }catch (e: Exception){
            response = null
            print(e.toString())

        }
        return response
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        try {
            val weatherArrayList = ArrayList<ListViewModel>()

            /* Extracting JSON returns from the API */

            var data = JSONObject(result)
            println(data)
            icon = data.getJSONArray("weather").getJSONObject(0).getString("icon")
            main = data.getJSONObject("main").getDouble("temp").toString()
            println(data.getJSONObject("main"))



    }catch (e: java.lang.Exception){
            println("--------------------------------------------------------------------------------")
            println(e.toString())
            println("--------------------------------------------------------------------------------")
        }
    }
}
private fun set_icon_to_img(iconCode: String, img: ImageView) {
    val imageUrl = "http://openweathermap.org/img/w/$iconCode.png"
    Picasso.get()
        .load(imageUrl)
        .into(img, object : Callback {
            override fun onSuccess() {
                Log.d("icon", "success")
            }

            override fun onError(e: Exception?) {
                Log.d("icon", "error")
            }
        })
}