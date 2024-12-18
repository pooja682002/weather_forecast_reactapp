package com.example.weather_app.service;

import com.example.weather_app.model.WeatherData;
import com.example.weather_app.Repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private static final String API_KEY = "21ee27d194a5f798825036e6a0adab0a";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Autowired
    private WeatherRepository weatherRepository;

    public WeatherData getWeatherData(String cityName, String countryCode) {
        Optional<WeatherData> optionalWeatherData = weatherRepository.findByCityNameAndCountryCode(cityName, countryCode);

        if (optionalWeatherData.isPresent() && isCacheValid(optionalWeatherData.get())) {
            return optionalWeatherData.get();
        } else {
            WeatherData newWeatherData = fetchWeatherDataFromAPI(cityName, countryCode);
            return weatherRepository.save(newWeatherData);
        }
    }

    private boolean isCacheValid(WeatherData weatherData) {
        LocalDateTime now = LocalDateTime.now();
        return weatherData.getRetrievedAt() != null && weatherData.getRetrievedAt().isAfter(now.minusHours(1));
    }

    private WeatherData fetchWeatherDataFromAPI(String cityName, String countryCode) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("q", cityName + "," + countryCode)
                    .queryParam("appid", API_KEY)
                    .queryParam("units", "metric")
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            WeatherData weatherData = new WeatherData();
            weatherData.setCityName(cityName);
            weatherData.setCountryCode(countryCode);
            weatherData.setTemperature(((Number) ((Map) response.get("main")).get("temp")).doubleValue());
            weatherData.setHumidity(((Number) ((Map) response.get("main")).get("humidity")).doubleValue());
            weatherData.setWindSpeed(((Number) ((Map) response.get("wind")).get("speed")).doubleValue());

            if (response.containsKey("rain")) {
                weatherData.setRainVolume(((Number) ((Map) response.get("rain")).getOrDefault("1h", 0.0)).doubleValue());
            }

            if (response.containsKey("snow")) {
                weatherData.setSnowVolume(((Number) ((Map) response.get("snow")).getOrDefault("1h", 0.0)).doubleValue());
            }

            weatherData.setRetrievedAt(LocalDateTime.now());
            return weatherData;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data from API: " + e.getMessage());
        }
    }
}


