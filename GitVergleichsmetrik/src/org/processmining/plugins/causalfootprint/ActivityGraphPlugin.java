package org.processmining.plugins.causalfootprint;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.plugins.ConvertPetriNetToAcceptingPetriNetPlugin;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.causalactivitygraphcreator.parameters.DiscoverCausalActivityGraphParameters;
import org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin;
import org.processmining.causalactivitygraphcreator.plugins.DiscoverCausalActivityGraphPlugin;
import org.processmining.causalactivitymatrix.models.CausalActivityMatrix;
import org.processmining.causalactivitymatrixcreator.plugins.CreateFromAcceptingPetriNetPlugin;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@Plugin(
		name = "ActivityGraphPlugin",
		parameterLabels = {"Petri-Netz", "Event Log"},
		returnLabels = { "ActivityGraphPlugin" },
		returnTypes = {CausalActivityGraph.class},
		userAccessible = false, 
		help = "Dieses Plugin erstellt aus einem Petri-Netz oder Event Log einen Causal Activity Graph."
		)
public class ActivityGraphPlugin {

	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "Petri Netz", requiredParameterLabels = {0})
	public CausalActivityGraph Erstelle (PluginContext context, Petrinet netz) {
		
		CausalActivityGraph CauActGraphPetri = erstelleActivityGraph(context, netz);
		
		if (CauActGraphPetri != null) context.log("Causal Activity Graph aus Petri-Netz wurde erfolgreich erstellt.");
		else context.log("Causal Activity Graph aus Petri-Netz konnte nicht erstellt werden.");
		
		return CauActGraphPetri;
		
	}
	
	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "Event Log", requiredParameterLabels = {0})
	public CausalActivityGraph Erstelle (PluginContext context, XLog log) {
		
		CausalActivityGraph CauActGraphLog = erstelleActivityGraph(context, log);
		
		if (CauActGraphLog != null) context.log("Causal Activity Graph aus Event Log wurde erfolgreich erstellt.");
		else context.log("Causal Activity Graph aus Event Log konnte nicht erstellt werden.");
		
		return CauActGraphLog;
		
	}
	
	// Methoden \\ 
	
	/**
	 * Ueberprueft, ob eingebenes Objekt ein {@link org.processmining.models.graphbased.directed.petrinet.Petrinet Petri-Netz}
	 * oder ein {@link org.deckfour.xes.model.XLog Event Log} ist. <p>
	 * @param o Eingegebenes Objekt
	 * @return <code>true</code>, wenn die Eingabe ein Petri-Netz oder Event Log ist. Ansonsten <code>false</code>
	 */
	public static boolean konvertierbar (Object o) {
		return (o instanceof Petrinet || o instanceof XLog);
	}
	
	/**
	 * Erstellt aus dem eingegebenen Objekt einen {@link org.processmining.causalactivitygraph.models.CausalActivityGraph Causal Activity Graph},
	 * welcher dann zur weiteren Analyse benutzt werden kann. 
	 * Ueberprueft vorher mit {@link #konvertierbar(Object) konvertierbar(Object)} ob das eingegebene Objekt umgewandelt werden kann.
	 * 
	 * @param o Eingegebenes Objekt
	 * @return In ein Causal Activity Graph umgewandeltes <code>Object</code>
	 * @throws Exception
	 */
	public static CausalActivityGraph erstelleActivityGraph (PluginContext context, Object o) {
		
		if (konvertierbar(o) == false) context.log("Eingabe ist weder Petri-Netz noch Event Log.", MessageLevel.DEBUG);
		
		if (o instanceof Petrinet) {
			CausalActivityGraph CauActGraph = AusPetriNetz(context, (Petrinet) o);
			return CauActGraph;
		} else {
			CausalActivityGraph CauActGraph = AusEventLog(context, (XLog) o);
			return CauActGraph;
		}
	}
	
	/**
	 * Erstellt aus einem {@link org.processmining.models.graphbased.directed.petrinet.Petrinet Petri-Netz} einen 
	 * {@link org.processmining.causalactivitygraph.models.CausalActivityGraph Causal Activity Graph}. <p>
	 * Dabei wird das Petri-Netz zunaechst in ein {@link org.processmining.acceptingpetrinet.models.AcceptingPetriNet Accepting Petri Net}
	 * umgewandelt. Dies erfolgt mit Hilfe des {@link org.processmining.acceptingpetrinet.plugins.ConvertPetriNetToAcceptingPetriNetPlugin 
	 * Convert Petri Net to Accepting Petri Net} Plugins. <p>
	 * Dann wird eine {@link org.processmining.causalactivitymatrix.models.CausalActivityMatrix Causal Activity Matrix} aus dem Accepting Petri Net
	 * erstellt. Dies geschiet mit Hilfe des {@link org.processmining.causalactivitymatrixcreator.plugins.CreateFromAcceptingPetriNetPlugin Create
	 * from Accepting Petri Net} Plugins. <p>
	 * Zuletzt wird die <code>Causal Activity Matrix</code> in einen <code>Causal Activity Graph</code> umgewandelt. Dies geschiet mit Hilfe des 
	 * {@link org.processmining.causalactivitygraphcreator.plugins.ConvertCausalActivityMatrixToCausalActivityGraphPlugin Convert Causal Activity
	 * Matrix to Causal Activity Graph} Plugins.
	 * 
	 * @param netz Petri-Netz
	 * @return <code>Causal Activity Graph</code> aus Petri-Netz
	 */
	public static CausalActivityGraph AusPetriNetz (PluginContext context, Petrinet netz) {
		
		// Erstelle benoetigte Plugins 
		ConvertPetriNetToAcceptingPetriNetPlugin AccConverter = new ConvertPetriNetToAcceptingPetriNetPlugin();
		CreateFromAcceptingPetriNetPlugin matrixConverter = new CreateFromAcceptingPetriNetPlugin();
		ConvertCausalActivityMatrixToCausalActivityGraphPlugin CausalConverter = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
		
		// Wandle Petri-Netz in ein Causal Activity Graph um
		AcceptingPetriNet AccNetz = AccConverter.runDefault(context, netz); 				// Erstelle Accepting Petri-Netz aus bekanntem Petri Netz
		CausalActivityMatrix CauActMatrix = matrixConverter.runDefault(context, AccNetz); 	// Erstelle Causal Activity Matrix aus Accepting Petri-Netz
		CausalActivityGraph CauActGraph = CausalConverter.runDefault(context, CauActMatrix);// Erstelle Causal Activity Graph aus Causal Activity Matrix
		
		return CauActGraph;
	}
	
	/**
	 * Erstellt aus einem {@link org.deckfour.xes.model.XLog Event Log} einen 
	 * {@link org.processmining.causalactivitygraph.models.CausalActivityGraph Causal Activity Graph}. <p>
	 * Dazu wird das {@link org.processmining.causalactivitygraphcreator.plugins.DiscoverCausalActivityGraphPlugin Discover Causal Activity Graph} Plugin benoetigt. 
	 * @param log Event-Log
	 * @return <code>Causal Activity Graph</code> aus Event-Log
	 */
	public static CausalActivityGraph AusEventLog (PluginContext context, XLog log) {
		
		// Erstelle benoetigte Plugins und Werte
		DiscoverCausalActivityGraphPlugin CausalConverter = new DiscoverCausalActivityGraphPlugin();
		DiscoverCausalActivityGraphParameters parameters = new DiscoverCausalActivityGraphParameters(log);
		
		// Wandle Event Log in Causal Activity Graph um 
		CausalActivityGraph CauActGraph = CausalConverter.run(context, log, parameters);

		return CauActGraph;
	}
	
}
