package com.mickyli.workflow.po;

import java.io.Serializable;

public class Person implements Serializable{

	private static final long serialVersionUID = 8548953031889391777L;
	private Integer id;//编号  
    private String name;//姓名  
    private String education;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
    
}
