package com.sohu.mp.sharingplan.enums;

public enum DataSourceTypeEnum {
	WRITE("write"),
	READ("read");

	private String name;

	DataSourceTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
