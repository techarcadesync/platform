package com.arcadesync.platform.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class GeoCoordinatesDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6738982539281834144L;
	private Double lat;
	private Double lon;
}
