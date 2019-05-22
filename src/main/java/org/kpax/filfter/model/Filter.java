package org.kpax.filfter.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Filter {
	String path;
	FilterType type;
	Object value;

	public Filter() {
	}

	public Filter(String path, FilterType type, Object value) {
		this.path = path;
		this.type = type;
		this.value = value;
	}
}
