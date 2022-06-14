package com.elmar.workmotion.model.input;

import com.elmar.workmotion.model.States;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeInput {
	private String name;
	private Short age;
	private States state;
}
