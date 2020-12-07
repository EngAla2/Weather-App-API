package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var CITY: String = "Amman,Jo"
    val API: String = "2556a92f449c7c87bef620d62f92223f" // Use API key
    var API_CALL = "https://api.openweathermap.org/data/2.5/onecall?lat=35.95&lon=31.96&units=metric&exclude=hourly,minutely&appid=$API"
    var weather_list : ListView? = null
    var address: Spinner ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weather_list = findViewById(R.id.weather_list)
        address = findViewById(R.id.address)
        weatherTask().execute()
        address?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(
                Adapter: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                CITY = Adapter?.getItemAtPosition(position).toString()
//                val file: File = File(applicationContext.getFilesDir(), "addressList.json")
//                val fileReader = FileReader(file)
//                val bufferedReader = BufferedReader(fileReader)
//                val stringBuilder = StringBuilder()
//                var line: String = bufferedReader.readLine()
//                while (line != null) {
//                    stringBuilder.append(line).append("\n")
//                    line = bufferedReader.readLine()
//                }
//                bufferedReader.close()
//
//                val responce = stringBuilder.toString()
//                API_CALL = "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&units=metric&exclude=hourly,minutely&appid=$API"
            }
        }
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL(API_CALL).readText(
                    Charsets.UTF_8
                )
                println("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                println(API_CALL)
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
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("current")
                val sys = jsonObj.getString("timezone")
                val weather = main.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = main.getLong("dt")

                val sunrise:Long = main.getLong("sunrise")
                val sunset:Long = main.getLong("sunset")

                val address = sys

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.updated_at).text =   "Updated at: "+ SimpleDateFormat(
                    "dd/MM/yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(Date(updatedAt * 1000))
                findViewById<TextView>(R.id.status).text = weather.getString("description").capitalize()
                findViewById<TextView>(R.id.temp).text = main.getString("temp")+"°C"
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat(
                    "hh:mm a",
                    Locale.ENGLISH
                ).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text =  main.getString("wind_speed")
                findViewById<TextView>(R.id.pressure).text = main.getString("pressure")
                findViewById<TextView>(R.id.humidity).text = main.getString("humidity")

                val iconCode =  weather.getString("icon")
                set_icon_to_img(iconCode, findViewById<ImageView>(R.id.icon))


                //  other days weather
                val daily = jsonObj.getJSONArray("daily")

                for (day in 0..7){
                    var temp = daily.getJSONObject(day).getJSONObject("temp")
                    var week_day =  SimpleDateFormat("E,", Locale.ENGLISH).format(
                        Date(
                            daily.getJSONObject(
                                day
                            ).getLong("dt") * 1000
                        )
                    )
                    var day_temp = temp.getString("min") + " - " + temp.getString("max") + "°C"
                    var day_icon =  daily.getJSONObject(day).getJSONArray("weather").getJSONObject(0).getString(
                        "icon"
                    )

                    weatherArrayList.add(ListViewModel(week_day, day_icon, day_temp))
                }
                fillListViwe(weatherArrayList)

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).text = e.toString()
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                print(e.toString())

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

    public fun fillListViwe(weatherArrayList: ArrayList<ListViewModel>) {
        var listViewAdapter = ListViewModelAdapter(this, weatherArrayList as ArrayList)
        weather_list?.adapter = listViewAdapter
        weather_list?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
        }
    }

}
