package edu.hubu.service.Imp;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import edu.hubu.entity.vo.response.WeatherVO;
import edu.hubu.service.WeatherService;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
@Slf4j
@Service
public class WeatherServiceImp implements WeatherService {
    @Resource
    RestTemplate restTemplate;
    @Value("${spring.weather.key}")
    String key;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude,latitude);
    }
    /**
     * 从缓存中获取天气信息
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return WeatherVO 天气信息对象，若获取失败则返回null
     */
    private WeatherVO fetchFromCache(double longitude, double latitude){
        byte[] data = restTemplate.getForObject(
                "https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key, byte[].class);
        JSONObject jsonObject = decompressStringToJson(data);
        if(jsonObject == null) return null;
        JSONObject location = jsonObject.getJSONArray("location").getJSONObject(0);
        int id = location.getInteger("id");

        String key = Const.FORUM_WEATHER_CACHE +id;
        String cache = stringRedisTemplate.opsForValue().get(key);
        if(cache != null)
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        WeatherVO vo = this.fetchFromAPI(id,location);
        if(vo == null) return null;
        stringRedisTemplate.opsForValue().set(key,JSONObject.from(vo).toString(),1, TimeUnit.HOURS);
        return vo;
    }
    /**
     * 从API获取天气信息
     *
     * @param id 地点ID
     * @param location 地点JSON对象
     * @return WeatherVO 天气信息对象，若获取失败则返回null
     */
    private WeatherVO fetchFromAPI(int id,JSONObject location){
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);
        JSONObject now =decompressStringToJson( restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/now?location="+ id + "&key=" + key, byte[].class));
        if(now == null) return null;
        vo.setNow(now.getJSONObject("now"));
        JSONObject hourly =decompressStringToJson( restTemplate.getForObject(
                "https://devapi.qweather.com/v7/weather/24h?location="+ id + "&key=" + key, byte[].class));
        if(hourly == null) return null;
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }
    /**
     * 将经过gzip压缩的字符串反压缩并解析为JSONObject对象
     *
     * @param data 压缩后的字节数组
     * @return 返回解析后的JSONObject对象，如果解析失败则返回null
     */
    private JSONObject decompressStringToJson(byte[] data){
        ByteArrayOutputStream steam = new ByteArrayOutputStream();
        try {
            GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1)
                steam.write(buffer,0,read);
            inputStream.close();
            steam.close();
            return JSONObject.parseObject(steam.toString());
        } catch (IOException e) {
            log.error("获取位置天气失败！！！");
            return null;
        }
    }
}
