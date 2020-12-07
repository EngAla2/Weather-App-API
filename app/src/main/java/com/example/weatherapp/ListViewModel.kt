package com.example.weatherapp

class  ListViewModel{
    var day: String? = null
    var day_icon: String? = null
    var day_temp: String? = null

    constructor(_day: String, _day_icon: String, _day_temp: String) {
        this.day = _day
        this.day_icon = _day_icon
        this.day_temp = _day_temp
    }
}