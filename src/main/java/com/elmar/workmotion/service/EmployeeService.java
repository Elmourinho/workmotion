package com.elmar.workmotion.service;

import com.elmar.workmotion.model.Employee;
import com.elmar.workmotion.model.Events;
import com.elmar.workmotion.model.States;
import com.elmar.workmotion.repository.EmployeeRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final StateMachineFactory<States, Events> stateMachineFactory;

	public EmployeeService(EmployeeRepository employeeRepository,
			StateMachineFactory<States, Events> stateMachineFactory) {
		this.employeeRepository = employeeRepository;
		this.stateMachineFactory = stateMachineFactory;
	}

	public Employee save(Employee employee) {
		employee.setStates(Collections.singletonList(States.ADDED));
		return employeeRepository.save(employee);
	}

	public Optional<Employee> findById(long id) {
		return employeeRepository.findById(id);
	}

	public Employee updateState(long id, String event) {
		Employee employee =
				employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
		StateMachine<States, Events> stateMachine = buildStateMachine(employee);
		boolean isRelevant = stateMachine.sendEvent(Events.valueOf(event));
		if (isRelevant) {
			employee.setStates(new ArrayList<>(stateMachine.getState().getIds()));
			return employeeRepository.save(employee);
		}
		throw new IllegalArgumentException("Provided event is not relevant");
	}

	private StateMachine<States, Events> buildStateMachine(Employee employee) {
		String employeeIdKey = String.valueOf(employee.getId());
		StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine(employeeIdKey);
		stateMachine.stopReactively().subscribe();
		stateMachine
				.getStateMachineAccessor()
				.doWithAllRegions(sma -> {
					DefaultStateMachineContext<States, Events> context;
					List<States> states = employee.getStates();
					if (states.size() <= 1) {
						context = new DefaultStateMachineContext<>(states.get(0), null, null, null,
								null);
					} else {
						List<StateMachineContext<States, Events>> contextList = new ArrayList<>();
						states.subList(1, employee.getStates().size()).forEach(s -> contextList.add(
								new DefaultStateMachineContext<>(s, null, null, null, null)));
						context = new DefaultStateMachineContext<States, Events>(contextList, states.get(0), null, null,
								null, null);
					}
					sma.resetStateMachine(context);
				});
		stateMachine.startReactively().subscribe();
		return stateMachine;
	}
}
