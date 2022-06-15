package com.elmar.workmotion.controller;

import com.elmar.workmotion.model.Employee;
import com.elmar.workmotion.model.input.EmployeeInput;
import com.elmar.workmotion.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@PostMapping("/employee")
	@ApiOperation(value = "Add new employee", response = Employee.class)
	public ResponseEntity<Employee> addEmployee(@RequestBody EmployeeInput employeeInput) {
		Employee employee = new Employee(employeeInput.getName(), employeeInput.getAge());
		Employee savedEmployee = employeeService.save(employee);
		return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
	}

	@GetMapping("/employees/{id}")
	@ApiOperation(value = "Get employee details", response = Employee.class)
	public ResponseEntity<Employee> getEmployee(@PathVariable long id) {
		return employeeService.findById(id).map(employee -> new ResponseEntity<>(employee, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
	}

	@PutMapping("/employees/{id}/events/{event}")
	@ApiOperation(value = "Update employee state", response = Employee.class)
	public ResponseEntity<Employee> update(@PathVariable long id, @PathVariable String event) {
		try{
			Employee employee = employeeService.updateState(id, event);
			return new ResponseEntity<>(employee, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
