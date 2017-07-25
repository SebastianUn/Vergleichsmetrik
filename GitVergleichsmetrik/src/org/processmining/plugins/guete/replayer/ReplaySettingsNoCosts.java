package org.processmining.plugins.guete.replayer;

import java.util.List;

import org.processmining.models.semantics.petrinet.Marking;

/**
 * Diese Klasse beschreibt Einstellungen fuer den Replay eines Event Logs, ohne, dass auf Kosten geachtet wird.
 * 
 * @author Sebastian Reiners
 * @author hverbeek
 * 
 * @see
 * PetriNetReplayAnalysis: Conformance and Performance measure
 * https://svn.win.tue.nl/repos/prom/Documentation/PetriNetReplayAnalysis.pdf
 */
public interface ReplaySettingsNoCosts {

	public boolean isFinal (Marking markierung, List<? extends Object> trace);
	
	public boolean isAllowed(ReplayAction action);
}
