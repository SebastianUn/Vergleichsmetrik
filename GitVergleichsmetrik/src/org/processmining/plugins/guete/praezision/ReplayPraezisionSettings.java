package org.processmining.plugins.guete.praezision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.guete.Praezision;
import org.processmining.plugins.guete.replayer.ReplayAction;
import org.processmining.plugins.guete.replayer.ReplaySettings;

/**
 * Beinhaltet die Einstellungen fuer die Kostenstruktur der Klasse {@link Praezision}. Implementiert das {@link ReplaySetting} Interface.
 * Basiert auf {@link org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting ReplayFitnessSettings} und wurde so veraendert, dass 
 * die Klasse die Einstellungen fuer die Kostenstruktur der Praezision implementieren kann. 
 * @author Sebastian Reiners
 *
 */
public class ReplayPraezisionSettings implements ReplaySettings<ReplayPraezisionCost> {

	private final Map<ReplayAction, Integer> weights;
	private final Map<ReplayAction, Boolean> actions;
	
	/**
	 * Erstellt ein Objekt der Klasse. 
	 * Speichert die Gewichtungen fuer bestimmte Aktionen, welche in einem Replay eines Event Logs auftreten koennen.  
	 */
	public ReplayPraezisionSettings() {
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

	/**
	 * Erhalte die Gewichtung fuer eine bestimmte {@link ReplayAction}. List den Wert aus {@link #weights}.
	 * @param action Replay Action fuer die die Gewichtung bestimmt werden soll
	 * @return Gewichtung wenn vorhanden, ansonsten <code>null</code>
	 */
	public Integer getWeight(ReplayAction action) {
		return weights.get(action);
	}

	/**
	 * Setze eine neue Gewichtung fuer eine ReplayAction. Ueberschreibt den Wert, wenn ReplayAction bereits in {@link #weights} vorhanden.
	 * @param action Replay Action
	 * @param weight
	 */
	public void setWeight(ReplayAction action, int weight) {
		weights.put(action, weight);
	}

	public ReplayPraezisionCost getInitialCost() {
		return new ReplayPraezisionCost(0, this);
	}

	public boolean isAllowed(ReplayAction action) {
		return actions.get(action);
	}

	public boolean isFinal(Marking marking, List<? extends Object> trace) {
		return trace.isEmpty();
	}

	public ReplayPraezisionCost getMaximalCost() {
		return new ReplayPraezisionCost(Integer.MAX_VALUE, this);
	}
}