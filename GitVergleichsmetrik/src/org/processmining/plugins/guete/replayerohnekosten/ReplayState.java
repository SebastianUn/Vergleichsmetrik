package org.processmining.plugins.guete.replayerohnekosten;

import java.util.List;
import java.util.UUID;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;


/**
 * Diese Klasse ist die Grundklasse des Replayers. Da die Kosten nicht berechnet werden muessen wird <code>Compareable</code> nicht 
 * implementiert. 
 * @author Sebastian Reiners
 * @see org.processmining.plugins.guete.replayer.ReplayState ReplayState
 */
public class ReplayState {

	/**
	 * Uebergelagerte Replay-State. <code>null</code>, wenn dieser State der erste State ist
	 */
	public ReplayState ElternState;
	
	public Marking Markierung;
	
	public Transition Transition;
	
	public List <? extends Object> trace;
	
	private final UUID id = UUID.randomUUID();
	
	/**
	 * 
	 * @param state Uebergelagerter State, <code>null</code>, wenn dieser State der erste State im Replay ist
	 * @param markierung Markierung im Petri-Netz
	 * @param transition Transition des Replay State
	 * @param trace Aktueller Trace
	 */
	public ReplayState(ReplayState state, Marking markierung, Transition transition, List<? extends Object> trace) {
		this.ElternState = state;
		this.Markierung = markierung;
		this.Transition = transition;
		this.trace = trace;
	}
	
	/**
	 * Ob die beiden Objekte gleich sind. Vergleicht die ID.
	 * @param o Objekt mit ID
	 * @return True, wenn die Objekte die gleiche ID haben
	 */
	public boolean equals(Object o) {
		if (o instanceof ReplayState) {
			return id.equals(((ReplayState) o).id);
		}
		return false;
	}
	
	/**
	 * Returns the hash code of this state.
	 */
	public int hashCode() {
		return id.hashCode();
	}

	// Cost-Methoden muessen nicht implementiert werden
}
