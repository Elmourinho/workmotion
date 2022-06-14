package com.elmar.workmotion;

import com.elmar.workmotion.model.Events;
import com.elmar.workmotion.model.States;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StateMachineConfigTest {

	@Autowired
	private StateMachineFactory<States, Events> stateMachineFactory;

	@Test
	void testBeginCheckTransition() {
		StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
		bringStateMachineToState(stateMachine, States.ADDED);
		stateMachine.sendEvent(Events.BEGIN_CHECK);

		assertEquals(States.IN_CHECK, stateMachine.getState().getId());
	}

	@Test
	void testFinishSecurityCheckTransition() {
		StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
		bringStateMachineToState(stateMachine, States.IN_CHECK);
		stateMachine.sendEvent(Events.FINISH_SECURITY_CHECK);
		stateMachine.sendEvent(Events.COMPLETE_INITIAL_WORK_PERMIT_CHECK);
		stateMachine.sendEvent(Events.FINISH_WORK_PERMIT_CHECK);
		assertEquals(States.APPROVED, stateMachine.getState().getId());
	}

	@Test
	void testActivateTransition() {
		StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
		bringStateMachineToState(stateMachine, States.APPROVED);
		stateMachine.sendEvent(Events.ACTIVATE);
		assertEquals(States.ACTIVE, stateMachine.getState().getId());
	}

	private void bringStateMachineToState(StateMachine<States, Events> stateMachine, States state) {
		stateMachine.stopReactively().subscribe();
		stateMachine
				.getStateMachineAccessor()
				.doWithAllRegions(sma -> {
					StateMachineContext<States, Events> context =
							new DefaultStateMachineContext<>(state, null, null, null, null);
					sma.resetStateMachine(context);
				});
		stateMachine.startReactively().subscribe();
	}
}
