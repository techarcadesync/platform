package com.arcadesync.platform.controller;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.arcadesync.platform.cache.ICacheCommonService;
import com.arcadesync.platform.dto.SuccessDTO;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CacheController {

	private ICacheCommonService service;

	@PutMapping("/secure/refresh-cache")
	@Secured("ROLE_REFRESH_CACHE")
	public SuccessDTO refreshCache(@RequestBody List<String> listOfCache) {
		service.refreshCache(listOfCache);
		return SuccessDTO.of();
	}

	@GetMapping("/secure/refresh-cache/keys")
	@Secured("ROLE_REFRESH_CACHE")
	public Set<String> refreshCacheKeys() {
		return service.refreshCacheKeys();
	}
}
