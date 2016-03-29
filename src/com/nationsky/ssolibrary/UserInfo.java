package com.nationsky.ssolibrary;

public class UserInfo {

	private String name;

	private String idCode;

	public String getName() {
		return name;
	}

	public String getIdCode() {
		return idCode;
	}

	public UserInfo(String _name, String _idcode) {
		this.name = _name;
		this.idCode = _idcode;
	}
}
