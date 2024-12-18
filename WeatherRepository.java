package com.example.weather_app.Repository;



import com.example.weather_app.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findByCityNameAndCountryCode(String cityName, String countryCode);
}




