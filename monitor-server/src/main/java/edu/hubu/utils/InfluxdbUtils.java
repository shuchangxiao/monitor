package edu.hubu.utils;

import com.alibaba.fastjson2.JSONObject;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import edu.hubu.entity.dto.RuntimeData;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.entity.vo.response.RuntimeHistoryVO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class InfluxdbUtils {
    @Value("${spring.influx.url}")
    String url;
    @Value("${spring.influx.user}")
    String user;
    @Value("${spring.influx.password}")
    String password;
    String token = "ONqV6gLEXeZw5vG4ZkPXbnO8H9IsSh6BiGRk4_lOBWeOv8pETSfIv2J-sXuIm325tvoUGfereoJmltCn3SCPbQ==";
    String BUCKET = "monitor";
    String ORG = "test";
    private InfluxDBClient client;
    @PostConstruct
    public void init(){
        client = InfluxDBClientFactory.create(url,token.toCharArray());
    }
    public void writeRuntimeData(int clientId, RuntimeDetailVO vo){
        RuntimeData data = new RuntimeData();
        BeanUtils.copyProperties(vo,data);
        data.setClientId(clientId);
        data.setTimestamp(new Date(vo.getTimestamp()).toInstant());
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, data);
    }
    public RuntimeHistoryVO readRuntimeData(int id){
        String query = """
                from(bucket: "%s")
                |> range(start: %s,stop: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime")
                |> filter(fn: (r) => r["clientId"] == "%s")
                """;
        Instant now = Instant.now();
        Instant beforeOneHours = now.minus(Duration.ofHours(1));
        String format = String.format(query, BUCKET,beforeOneHours,now,id);
        List<FluxTable> tables = client.getQueryApi().query(format, ORG);
        int size = tables.size();
        RuntimeHistoryVO runtimeHistoryVO = new RuntimeHistoryVO();
        if( size == 0) return runtimeHistoryVO;
        List<FluxRecord> records = tables.get(0).getRecords();
        for(int i=0;i<records.size();i++){
            JSONObject object = new JSONObject();
            object.put("timestamp", Objects.requireNonNull(records.get(i).getTime()).plus(Duration.ofHours(8)));
            for(int j=0;j<size;j++){
                FluxRecord record = tables.get(j).getRecords().get(i);
                object.put(record.getField(),record.getValue());
            }
            runtimeHistoryVO.getList().add(object);
        }
        return runtimeHistoryVO;
    }
}
