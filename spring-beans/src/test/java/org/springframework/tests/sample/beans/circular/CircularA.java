package org.springframework.tests.sample.beans.circular;

/**
 * 循环依赖测试类A
 */
public class CircularA {

	private CircularB circularB;

	private String name;

	private String age;

	public CircularA() {
	}

	public CircularA(CircularB circularB) {
		this.circularB = circularB;
	}

	public CircularB getCircularB() {
		return circularB;
	}

	public void setCircularB(CircularB circularB) {
		this.circularB = circularB;
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
