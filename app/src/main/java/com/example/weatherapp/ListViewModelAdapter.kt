
package com.example.weatherapp
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ListViewModelAdapter(val context: Context, val listModelArrayList: ArrayList<ListViewModel>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val dat_weather: ViewHolder

        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.week_list_weather, null, false)
            dat_weather = ViewHolder(view)
            view.tag = dat_weather
        } else {
            view = convertView
            dat_weather = view.tag as ViewHolder
        }

        dat_weather.day.text = listModelArrayList[position].day
        dat_weather.day_temp.text = listModelArrayList[position].day_temp
        set_icon_to_img(listModelArrayList[position].day_icon.toString(), dat_weather.day_icon)

        return view

    }

    override fun getItem(position: Int): Any {
        return listModelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listModelArrayList.size
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
}

private class ViewHolder(view: View?) {
    val day_icon: ImageView = view?.findViewById<ImageView>(R.id.day_icon) as ImageView
    val day : TextView = view?.findViewById(R.id.day_day) as TextView
    val day_temp : TextView = view?.findViewById<TextView>(R.id.day_temp) as TextView
}