package org.processmining.plugins.guete.replayerohnekosten;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.search.NodeExpander;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.plugins.guete.replayer.ReplaySettingsNoCosts;

/**
 * 
 * @author Sebastian Reiners
 * @author hverbeek
 * 
 * @see
 * PetriNetReplayAnalysis: Conformance and Performance measure
 * https://svn.win.tue.nl/repos/prom/Documentation/PetriNetReplayAnalysis.pdf
 *
 */
public class ReplayStateExpander implements NodeExpander<ReplayState>{
	
	@SuppressWarnings("unused")
	private final ReplaySettingsNoCosts Einstellungen;
	
	@SuppressWarnings("unused")
	private final Petrinet Netz;
	
	@SuppressWarnings("unused")
	private final PetrinetSemantics Semantics;
	
	@SuppressWarnings("unused")
	private final Map<Transition, ? extends Object> Map;
	
	public List<ReplayState> Loesungen;
	
	public ReplayStateExpander (ReplaySettingsNoCosts einstellungen, Petrinet netz, PetrinetSemantics semantics, 
			Map<Transition, ? extends Object> map) {
				
	this.Einstellungen = einstellungen;
	this.Netz = netz;
	this.Semantics = semantics;
	this.Map= map;
	}

	/**
	 * Erforderlich durch die Implementierung des Interfaces.
	 */
	public Collection<ReplayState> expandNode(ReplayState toExpand, Progress progress,
			Collection<ReplayState> unmodifiableResultCollection) {
		return null;
	}

	public void processLeaf(ReplayState leaf, Progress progress, Collection<ReplayState> resultCollection) {
	}

}
