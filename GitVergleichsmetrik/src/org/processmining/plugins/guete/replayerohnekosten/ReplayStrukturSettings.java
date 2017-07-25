package org.processmining.plugins.guete.replayerohnekosten;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.guete.replayer.ReplayAction;
import org.processmining.plugins.guete.replayer.ReplaySettingsNoCosts;

/**
* @author Sebastian Reiners
* @author hverbeek
* 
* @see
* PetriNetReplayAnalysis: Conformance and Performance measure
* https://svn.win.tue.nl/repos/prom/Documentation/PetriNetReplayAnalysis.pdf
*/
public class ReplayStrukturSettings implements ReplaySettingsNoCosts {

	
	private final Map<ReplayAction, Integer> weights;
	private final Map<ReplayAction, Boolean> actions;

	public ReplayStrukturSettings() {
		actions = new HashMap<ReplayAction, Boolean>();
		actions.put(ReplayAction.INSERT_ENABLED_MATCH, true);
		actions.put(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
		actions.put(ReplayAction.REMOVE_HEAD, true);
		actions.put(ReplayAction.INSERT_ENABLED_MISMATCH, true);
		actions.put(ReplayAction.INSERT_DISABLED_MATCH, true);
		actions.put(ReplayAction.INSERT_DISABLED_MISMATCH, false);

		weights = new HashMap<ReplayAction, Integer>();
		weights.put(ReplayAction.INSERT_ENABLED_MATCH, 1);
		weights.put(ReplayAction.INSERT_ENABLED_INVISIBLE, 10);
		weights.put(ReplayAction.REMOVE_HEAD, 100);
		weights.put(ReplayAction.INSERT_ENABLED_MISMATCH, 100);
		weights.put(ReplayAction.INSERT_DISABLED_MATCH, 100);
		weights.put(ReplayAction.INSERT_DISABLED_MISMATCH, 1000);
	}

	public Integer getWeight(ReplayAction action) {
		return weights.get(action);
	}

	public void setWeight(ReplayAction action, int weight) {
		weights.put(action, weight);
	}

	public void setAction(ReplayAction action, boolean isEnabled) {
		actions.put(action, isEnabled);
	}

	public boolean isAllowed(ReplayAction action) {
		return actions.get(action);
	}

	public boolean isFinal(Marking marking, List<? extends Object> trace) {
		return trace.isEmpty();
	}
}