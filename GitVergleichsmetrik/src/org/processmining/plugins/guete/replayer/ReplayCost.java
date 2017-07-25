package org.processmining.plugins.guete.replayer;

/**
 * Cost structure interface for the cost-based Petri net trace replayer.
 * 
 * @author hverbeek
 * 
 * @see
 * PetriNetReplayAnalysis: Conformance and Performance measure
 * https://svn.win.tue.nl/repos/prom/Documentation/PetriNetReplayAnalysis.pdf
 */
public interface ReplayCost {

	/**
	 * Returns whether this cost is acceptable.
	 * 
	 * @return Whether this cost is acceptable.
	 */
	public boolean isAcceptable();

	/**
	 * Returns whether this cost equals the given object.
	 * 
	 * @param o
	 *            The given object.
	 * @return Whether this cost equals the given object.
	 */
	public boolean equals(Object o);

	/**
	 * Returns the hash code for this cost.
	 * 
	 * @return The hash code for this cost.
	 */
	public int hashCode();
}
