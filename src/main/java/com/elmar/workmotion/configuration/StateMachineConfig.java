package com.elmar.workmotion.configuration;

import com.elmar.workmotion.model.Events;
import com.elmar.workmotion.model.States;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachineFactory
@Log
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> {

	@Override
	public void configure(StateMachineConfigurationConfigurer<States, Events> config)
			throws Exception {
		config
				.withConfiguration()
				.autoStartup(false)
				.listener(listener());
	}

	@Override
	public void configure(StateMachineStateConfigurer<States, Events> states)
			throws Exception {
		states
				.withStates()
				.initial(States.ADDED)
				.fork(States.IN_CHECK)
				.join(States.ALL_CHECKS_FINISHED)
				.state(States.APPROVED)
				.end(States.ACTIVE)
				.and()
				.withStates()
				.parent(States.IN_CHECK)
				.initial(States.SECURITY_CHECK_STARTED)
				.end(States.SECURITY_CHECK_FINISHED)
				.and()
				.withStates()
				.parent(States.IN_CHECK)
				.initial(States.WORK_PERMIT_CHECK_STARTED)
				.state(States.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
				.end(States.WORK_PERMIT_CHECK_FINISHED);
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
			throws Exception {
		transitions
				.withExternal().source(States.ADDED).target(States.IN_CHECK).event(Events.BEGIN_CHECK)
				.and()
				.withFork().source(States.IN_CHECK).target(States.SECURITY_CHECK_STARTED)
				.target(States.WORK_PERMIT_CHECK_STARTED)
				.and()
				.withExternal().source(States.SECURITY_CHECK_STARTED).target(States.SECURITY_CHECK_FINISHED)
				.event(Events.FINISH_SECURITY_CHECK)
				.and()
				.withExternal().source(States.WORK_PERMIT_CHECK_STARTED)
				.target(States.WORK_PERMIT_CHECK_PENDING_VERIFICATION).event(Events.COMPLETE_INITIAL_WORK_PERMIT_CHECK)
				.and()
				.withExternal().source(States.WORK_PERMIT_CHECK_PENDING_VERIFICATION)
				.target(States.WORK_PERMIT_CHECK_FINISHED).event(Events.FINISH_WORK_PERMIT_CHECK)
				.and()
				.withJoin().source(States.SECURITY_CHECK_FINISHED).source(States.WORK_PERMIT_CHECK_FINISHED)
				.target(States.ALL_CHECKS_FINISHED)
				.and()
				.withExternal().source(States.ALL_CHECKS_FINISHED).target(States.APPROVED)
				.and()
				.withExternal().source(States.APPROVED).target(States.ACTIVE).event(Events.ACTIVATE);
	}

	@Bean
	public StateMachineListener<States, Events> listener() {
		return new StateMachineListenerAdapter<States, Events>() {
			@Override
			public void stateChanged(State<States, Events> from, State<States, Events> to) {
				log.info("State change from " + from + " to: " + to);
			}
		};
	}
}