package com.arcadesync.platform.filters;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InMemoryFilterService {

	private GenericDTOFilterServiceFactory factory;

	public <T> List<T> filter(List<AbstractFilter> filter, List<T> data, Class clazzz) {
		DTOFilterService<T> service = factory.getService(clazzz);
		if (ObjectUtils.isEmpty(service)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"no service found for : " + clazzz.getSimpleName());
		}
		return service.filter(filter, data);
	}

}
