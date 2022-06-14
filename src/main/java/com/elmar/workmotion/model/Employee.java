package com.elmar.workmotion.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")

@AllArgsConstructor
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private long id;

	@Column(name = "name")
	@Getter
	@Setter
	private String name;

	@Column(name = "age")
	@Getter
	@Setter
	private Short age;

	@Column(name = "states")
	@Enumerated(EnumType.STRING)
	@ElementCollection(targetClass = States.class)
	@Getter
	@Setter
	private List<States> states;

	public Employee(String name, Short age) {
		this.name = name;
		this.age = age;
	}
}
