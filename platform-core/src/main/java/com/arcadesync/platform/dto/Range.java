package com.arcadesync.platform.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Range<T extends Comparable<T>> implements Serializable {

	private static final long serialVersionUID = 198744388888453L;

	private T from;
	private T to;

	public boolean inBetween(T val) {
		return from.compareTo(val) <= 0 && to.compareTo(val) >= 0;
	}
}
