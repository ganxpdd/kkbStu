package com.kkb.spring.po;

import lombok.Data;

import java.util.Date;

//@Data
public class User {
	private int id;
	private String username;
	private Date birthday;
	private String sex;
	private String address;


	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", birthday=" + birthday +
				", sex='" + sex + '\'' +
				", address='" + address + '\'' +
				'}';
	}
}
