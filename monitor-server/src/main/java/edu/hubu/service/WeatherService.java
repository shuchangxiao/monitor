package edu.hubu.service;

import edu.hubu.entity.vo.response.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeather(double longitude,double latitude);
}
