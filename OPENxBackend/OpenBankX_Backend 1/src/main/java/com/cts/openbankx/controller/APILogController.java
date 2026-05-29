package com.cts.openbankx.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.cts.openbankx.model.APILog;
import com.cts.openbankx.service.APILogService;


@RestController
@RequestMapping("/api/v1/logs")
public class APILogController {

    private final APILogService logService;

    public APILogController(APILogService logService) {
        this.logService = logService;
    }


    @GetMapping
    public List<Map<String, Object>> getAllLogs() {
        return toDtoList(logService.findAll());
    }


    @GetMapping("/{id}")
    public Map<String, Object> getLogById(@PathVariable Long id) {
        return toDto(logService.findById(id));
    }


    @GetMapping("/apps/{appId}")
    public List<Map<String, Object>> getLogsByApp(@PathVariable Long appId) {
        return toDtoList(logService.findByTppApp(appId));
    }


    @GetMapping("/errors")
    public List<Map<String, Object>> getErrorLogs() {
        return toDtoList(logService.findErrorLogs());
    }


    @GetMapping("/healthy")
    public List<Map<String, Object>> getHealthyLogs() {
        return toDtoList(logService.findHealthyLogs());
    }


    @DeleteMapping("/{id}")
    public void deleteLog(@PathVariable Long id) {
        logService.delete(id);
    }



    private List<Map<String, Object>> toDtoList(List<APILog> rows) {
        List<Map<String, Object>> out = new ArrayList<>(rows.size());
        for (APILog row : rows) out.add(toDto(row));
        return out;
    }

    private Map<String, Object> toDto(APILog log) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("apiLogId",   log.getApiLogId());
        dto.put("endpoint",   log.getEndpoint());
        dto.put("method",     log.getMethod());
        dto.put("statusCode", log.getStatusCode());
        dto.put("latencyMs",  log.getLatencyMs());
        dto.put("timestamp",  log.getTimestamp());

        if (log.getTppApp() != null) {
            Map<String, Object> tppApp = new LinkedHashMap<>();
            try {
                tppApp.put("tppAppId", log.getTppApp().getTppAppId());
                tppApp.put("appName",  log.getTppApp().getAppName());
                if (log.getTppApp().getTpp() != null) {
                    Map<String, Object> tpp = new LinkedHashMap<>();
                    tpp.put("tppId",     log.getTppApp().getTpp().getTppId());
                    tpp.put("legalName", log.getTppApp().getTpp().getLegalName());
                    tppApp.put("tpp", tpp);
                }
            } catch (Exception ignored) {
            }
            dto.put("tppApp", tppApp);
        } else {
            dto.put("tppApp", null);
        }
        return dto;
    }
}