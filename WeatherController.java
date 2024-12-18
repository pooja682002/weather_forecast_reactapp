package com.example.weather_app.controller;

import com.example.weather_app.model.WeatherData;
import com.example.weather_app.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{cityName}/{countryCode}")
    public WeatherData getWeather(@PathVariable String cityName, @PathVariable String countryCode) {
        return weatherService.getWeatherData(cityName, countryCode);
    }
}

