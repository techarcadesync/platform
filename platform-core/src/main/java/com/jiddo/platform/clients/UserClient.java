package com.jiddo.platform.clients;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiddo.platform.PlatformConstants;
import com.jiddo.platform.dto.UserDetails;
import com.jiddo.platform.exception.ApplicationException;
import com.jiddo.platform.exception.PlatformExceptionCodes;
import com.jiddo.platform.exception.ValidationException;
import com.jiddo.platform.security.SecurityConfigProps;
import com.jiddo.platform.utility.PlatformCommonService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserClient {

	private RestTemplate template;
	private UrlConfig urlConfig;
	private SecurityConfigProps securityProps;
	private PlatformCommonService commonService;
	private ObjectMapper mapper;

	public UserDetails getUserById(String userId) {
		if (ObjectUtils.isEmpty(userId)) {
			return null;
		}
		Set<String> mobileSet = new HashSet<>();
		mobileSet.add(userId);
		Map<String, UserDetails> details = getUserById(mobileSet);
		if (details.containsKey(userId)) {
			return details.get(userId);
		}
		return null;
	}

	public UserDetails getUserByMobileNumber(String mobileNumber) {
		Set<String> mobileNumberSet = new HashSet<>();
		mobileNumberSet.add(mobileNumber);
		Map<String, UserDetails> map = getUserByMobileNumber(mobileNumberSet);
		return map.get(mobileNumber);
	}

	public Map<String, UserDetails> getUserByMobileNumber(Set<String> mobileNumbers) {
		if (ObjectUtils.isEmpty(mobileNumbers)) {
			return Collections.emptyMap();
		}
		log.debug("fetching user with mobileNumbers :{}", mobileNumbers);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(mobileNumbers, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-call/user/mobileNumber",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UserDetails>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return Collections.emptyMap();
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");

		}
	}

	public Map<String, UserDetails> getUserById(Set<String> userIds) {
		if (ObjectUtils.isEmpty(userIds)) {
			return Collections.emptyMap();
		}
		log.debug("fetching user with userIds :{}", userIds);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<Set<String>> entity = new HttpEntity<>(userIds, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/user/id",
					urlConfig.getBaseUrl());
			log.debug("request for fetchig user details : {} body and headers {}", url, entity);
			ResponseEntity<JsonNode> response = template.exchange(url, HttpMethod.GET, entity, JsonNode.class);
			return mapper.convertValue(response.getBody(), new TypeReference<Map<String, UserDetails>>() {
			});
		} catch (HttpStatusCodeException exeption) {
			if (commonService.is404Error(exeption.getResponseBodyAsString())) {
				return Collections.emptyMap();
			}
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}

	}

	public UserDetails[] getAllCZOUser() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/czo-user/all",
					urlConfig.getBaseUrl());
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

			log.debug("request: {} headers {}", url, entity);
			ResponseEntity<UserDetails[]> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity,
					UserDetails[].class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	public UserGetOrCreateResponse getOrCreateUser(String mobileNumber) {
		if (ObjectUtils.isEmpty(mobileNumber)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid userId");
		}
		log.debug("fetching or creating user with mobile :{}", mobileNumber);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, securityProps.getCreds().get("user-service"));
		GetOrCreateUserRequest request = new GetOrCreateUserRequest();
		request.setMobile(mobileNumber);
		HttpEntity<GetOrCreateUserRequest> entity = new HttpEntity<>(request, headers);
		try {
			String url = MessageFormat.format("{0}/user-service/secure/internal-server/user", urlConfig.getBaseUrl());
			log.debug("request for fetchig/creating user details : {} body and headers {}", url, entity);
			ResponseEntity<UserGetOrCreateResponse> response = template.exchange(url, HttpMethod.POST, entity,
					UserGetOrCreateResponse.class);
			return response.getBody();
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from the server :{}", exeption.getResponseBodyAsString());
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
					"User api not working");
		}
	}

	@Data
	public static class UserGetOrCreateResponse {
		private String userId;
		private String mobileNumber;
		private boolean isNewCustomer;
	}

	@Data
	public static class GetOrCreateUserRequest {
		private String mobile;
	}

}
