package com.cts.openbankx.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.openbankx.model.APILog;
import com.cts.openbankx.model.RateLimitCounter;
import com.cts.openbankx.service.APILogService;
import com.cts.openbankx.service.RateLimitService;

@RestController
@RequestMapping("/api/v1/gateway")
public class MetricsController {
	private final APILogService logService;
	private final RateLimitService rateLimitService;

	public MetricsController(APILogService logService, RateLimitService rateLimitService) {
		this.logService = logService;
		this.rateLimitService = rateLimitService;
	}


	@GetMapping("/logs")
	public List<APILog> getAllLogs() {
		return logService.findAll();
	}

	@GetMapping("/logs/app/{tppAppId}")
	public List<APILog> getLogsByApp(@PathVariable Long tppAppId) {
		return logService.findByTppApp(tppAppId);
	}

	@PostMapping("/logs")
	public APILog createLog(@RequestBody APILog log) {
		return logService.save(log);
	}


	@GetMapping("/rate-limits")
	public List<RateLimitCounter> getAllRateLimits() {
		return rateLimitService.findAll();
	}

	@GetMapping("/rate-limits/app/{tppAppId}")
	public List<RateLimitCounter> getRateLimitsByApp(@PathVariable Long tppAppId) {
		return rateLimitService.findByTppApp(tppAppId);
	}

	@PostMapping("/rate-limits")
	public RateLimitCounter createRateLimit(@RequestBody RateLimitCounter counter) {
		return rateLimitService.save(counter);
	}
}
