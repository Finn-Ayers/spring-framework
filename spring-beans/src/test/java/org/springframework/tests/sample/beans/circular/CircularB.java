package org.springframework.tests.sample.beans.circular;

/**
 * 循环依赖测试B
 */
public class CircularB {

	private CircularA circularA;

	private String name;

	private String age;

	public CircularB() {
	}

	public CircularB(CircularA circularA) {
		this.circularA = circularA;
	}

	public CircularA getCircularA() {
		return circularA;
	}

	public void setCircularA(CircularA circularA) {
		this.circularA = circularA;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
}
