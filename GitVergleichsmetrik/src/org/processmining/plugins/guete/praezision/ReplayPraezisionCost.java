package org.processmining.plugins.guete.praezision;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.guete.Praezision;
import org.processmining.plugins.guete.replayer.ReplayAction;
import org.processmining.plugins.guete.replayer.ReplayCost;
import org.processmining.plugins.guete.replayer.ReplayCostAddOperator;

/**
 * Beschreibt die Kostenstruktur fuer die Klasse {@link Praezision}. Implementiert das {@link ReplayCost} und {@link Comparable} Interface.
 * Basiert auf {@link org.processmining.plugins.petrinet.replayfitness.ReplayFitnessCost ReplayFitnessCost} und wurde so veraendert, dass 
 * die Klasse die Kostenstruktur der Praezision implementieren kann. 
 * @author Sebastian Reiners
 * 
 *
 */
public class ReplayPraezisionCost implements ReplayCost, Comparable<ReplayPraezisionCost> {

	public Integer cost;
	public final ReplayPraezisionSettings setting;

	/**
	 * Erstellt ein Objekt der Klasse.
	 * @param cost Kosten einer bestimmten Aktion
	 * @param setting Einstellungen der Dimension
	 */
	public ReplayPraezisionCost(Integer cost, ReplayPraezisionSettings setting) {
		this.cost = cost;
		this.setting = setting;
	}

	/**
	 * 
	 */
	public static ReplayCostAddOperator<ReplayPraezisionCost> addOperator = new ReplayCostAddOperator<ReplayPraezisionCost>() {
		public ReplayPraezisionCost add(ReplayPraezisionCost cost, ReplayAction action, Transition transition, Object object) {
			ReplayPraezisionCost newCost = new ReplayPraezisionCost(cost.cost, cost.setting);
			newCost.cost += cost.setting.getWeight(action);
			return newCost;
		}
	};

	public int compareTo(ReplayPraezisionCost cost) {
		return this.cost.compareTo(cost.cost);
	}

	public boolean isAcceptable() {
		return true;
	}

	public int hashCode() {
		return cost.hashCode();
	}

	/**
	 * Gibt die {@link #cost Kosten} als String wieder.
	 * @return Kosten als String
	 */
	public String toString() {
		return cost.toString();
	}

	/**
	 * Ob die Kosten des eingebenen Objekts kleiner, gleich oder groeﬂer den Kosten dieses Objektes sind. Vergleich {@link #cost} mit den Kosten der Eingabe.
	 * Gibt false zurueck, wenn o keine Kostenstruktur implementiert. 
	 * @param o Objekt mit Kosten
	 * @return True, wenn die Kosten gleich sind, ansonsten false
	 */
	public boolean equals(Object o) {
		if (o instanceof ReplayPraezisionCost) {
			return cost.equals(((ReplayPraezisionCost) o).cost);
		}
		return false;
	}
}

