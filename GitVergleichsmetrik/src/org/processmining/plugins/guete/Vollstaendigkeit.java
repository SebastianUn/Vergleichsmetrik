 package org.processmining.plugins.guete;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.petrinet.replay.Replayer;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessCost;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;




/**
 * Diese Klasse berechnet die Dimension der Vollstaendigkeit. Implementiert das {@link org.processmining.plugins.guete.Guete Guete Interface}.
 * Benutzt state-space-analysis um mit Hilfe des {@link org.processmining.framework.util.search.MultiThreadedSearcher Multi Threaded Searcher} die beste Loesung
 * fuer die jeweilige Sequenz zu finden. Die beste Loesung wird als {@link org.processmining.plugins.guete.replayer.ReplayState Replay State} gespeichert.<p>
 * Wenn die beste Loesung fuer die jeweilige Sequenz gefunden wurde, wird ein Bestrafungsfaktor bestimmt, welcher Fehler im Replay aufdeckt. 
 * Basiert auf dem {@link org.processmining.plugins.petrinet.replay.Replayer Replayer}.
 * 
 * @author Sebastian Reiners
 *
 */
public class Vollstaendigkeit implements Guete {
	
	private PluginContext Kontext;
	private GueteErgebnis Ergebnis;
	
	private double VW = 0; // Default-Wert Vollstaendigkeit
	
	
	private int producedTokens; 	//ni * pi
	private int missingTokens; 		//ni * mi
	private int consumedTokens; 	//ni * ci
	private int remainingTokens; 	//ni * ri
	
	
	
	// Konstruktoren \\ 
	
	/**
	 * Default-Konstruktor. Nicht erlaubt.
	 */
	@SuppressWarnings("unused")
	private Vollstaendigkeit () {}
	
	/**
	 * Erstellt ein Objekt der Klasse <code>Vollstaendigkeit</code>. 
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden.
	 * @param context Plugin Kontext der Klasse zum erstellen von Nachrichten.
	 */
	public Vollstaendigkeit (PluginContext kontext, GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
		Kontext = kontext;
	}

	
	// Methoden \\
	
	/**
	 * Berechnet die Vollstaendigkeit. Speichert das Ergebnis in dem {@link #Ergebnis Ergebnis-Objekt} der Klasse.
	 * Dazu wird der Event Log Sequenz fuer Sequenz durchlaufen und versucht die Sequenz im Petri-Netz zu wiederholen. 
	 * Nach durchlaufender Sequenz wird ueberprueft wie viele Tokens uebrig geblieben sind. So kann bestimmt werden, wie gut 
	 * oder schlecht das Petri-Netz in der Lage war die jeweilige Sequenz wieder zu spiegeln. 
	 * Die Formel ist: <p>
	 * { (1.0 - missingTokens / (2.0 * consumedTokens) - remainingTokens / (2.0 * producedTokens)) }
	 * @param log Event Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 * 
	 */
	public void berechne(XLog log, Petrinet netz, Marking markierung) {

		XEventClasses klassen = getEventKlassen(log);
		Map<Transition, XEventClass> map = getMapping(klassen, netz);
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);
		
		Replayer<ReplayFitnessCost> replayer = new Replayer<ReplayFitnessCost>(Kontext, netz, semantics, map, ReplayFitnessCost.addOperator);
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		
		producedTokens = 0;
		consumedTokens = 0;
		missingTokens = 0;
		remainingTokens = 0;
		double sequenzen = 0;
		
		for (XTrace t : log) {
			List<XEventClass> list = getList(t, klassen);
			try {
				List<Transition> sequence = replayer.replayTrace(markierung, list, setting);
				if (!sequence.isEmpty()) { // Wenn bessere Loesung gefunden
					neueVollstaendigkeit(netz, markierung, sequence, semantics);
				} 
				sequenzen++; // Anzahl der erfolgreich durchlaufenden traces
			} catch (Exception ex) {
				// Das Gesuchte Element ist nicht vorhanden
			}
			
		}
		
		VW = (1.0 - missingTokens / (2.0 * consumedTokens) - remainingTokens / (2.0 * producedTokens)); 
		if (Double.isNaN(VW)) {
			VW = 0;
		}
		
		double vw = VW;
		// Werte in GueteErgebnis einfuegen, VW wird gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.PRODUCEDTOKENS, producedTokens + "");
		Ergebnis.addInfo(Ergebnis.CONSUMEDTOKENS, consumedTokens + "");
		Ergebnis.addInfo(Ergebnis.MISSINGTOKENS, missingTokens + "");
		Ergebnis.addInfo(Ergebnis.REMAININGTOKENS, remainingTokens + "");
		Ergebnis.addInfo(Ergebnis.VOLLSTAENDIGKEIT, nf.format(vw));		// runden
		Ergebnis.addInfo(Ergebnis.ANZAHLSEQUENZEN, sequenzen + "");
		
		Kontext.log("Vollstaendigkeit erfolgreich berechnet. Sie hat den Wert: " + VW + System.lineSeparator(), MessageLevel.NORMAL);
	}
	
	/**
	 * Basiert auf {@link org.processmining.plugins.petrinet.replayfitness.ReplayFitnessPlugin #updateFitness(net, marking, sequence, semantics) 
	 * updateFitness(net, initMarking, sequence, semantics)}. <p>
	 * Erhoeht {@link #consumedTokens}, entsprechend des Gewichts fuer jede Kante im Petri-Netz. Erhoet sich auﬂerdem durch die Anzahl der Markierung aus der Markierung. <p>
	 * Erhoeht {@link #missingTokens}, wenn zu wenig Token in einer Stelle vorhanden sind, um die naechste Transition zu feuern. <p>
	 * Erhoeht {@link #producedTokens}, wenn ein Token in einer Stelle produziert wurde. <p>
	 * Erhoeht {@link #remainingTokens}, wenn im Petri-Netz Token auch nach dem Replay verbleiben. Erhoet enstprechend der Anzahl der verbleibenden Token. 
	 * @param net Petri-Netz
	 * @param initMarking Initiale Markierung des Petri-Netzes
	 * @param sequence Aktuelle Sequenz im Event Log
	 * @param semantics Semantiken des Petri-Netzes
	 * @author T. Yuliani and H.M.W. Verbeek
	 * 
	 * @see
	 * org.processmining.plugins.petrinet.replayfitness.ReplayFitnessPlugin
	 */
	private void neueVollstaendigkeit (Petrinet net, Marking initMarking, List<Transition> sequence, PetrinetSemantics semantics) {
		Marking marking = new Marking(initMarking);
		producedTokens += marking.size();

		for (Transition transition : sequence) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getSource();
					int consumed = arc.getWeight();
					int missing = 0;
					if (arc.getWeight() > marking.occurrences(place)) {
						missing = arc.getWeight() - marking.occurrences(place);
					}
					for (int i = missing; i < consumed; i++) {
						marking.remove(place);
					}
					consumedTokens += consumed;
					missingTokens += missing;
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
					producedTokens += produced;
				}
			}
		}
		consumedTokens += marking.size();
		remainingTokens += marking.isEmpty() ? 0 : marking.size() - 1;
	}
	

	/**
	 * Basiert auf {@link org.processmining.plugins.petrinet.replayfitness.ReplayFitnessPlugin #getList(trace, classes) getList(trace, classes)}. <p> 
	 * Erstellt aus einer Sequenz und XEventClasses eine Liste von einzelnen XEventClass. 
	 * @param trace Sequenz aus dem Event Log
	 * @param classes Klasses aus dem Event Log
	 * @return Liste der einzelnen Event Klassen
	 */
	protected List<XEventClass> getList(XTrace trace, XEventClasses classes) {
		List<XEventClass> list = new ArrayList<XEventClass>();
		for (org.deckfour.xes.model.XEvent event : trace) {
			list.add(classes.getClassOf(event));
		}
		return list;
	}
	
	
	/**
	 * Erhalte alle Event Klassen aus einem Event Log.
	 * Sind keine Classifier vorhanden, werden Standard Classifier benutzt.
	 * @param log Event Log
	 * @return Event Klassen des Event Logs
	 * @see
	 * https://github.com/rupos-it/PetriNetReplayAnalysis/blob/master/src/org/processmining/plugins/petrinet/replayfitness/ReplayFitnessPlugin.java
	 */
	protected XEventClasses getEventKlassen(XLog log) {
		XEventClasses xEventKlasse;
		XEventClassifier classifier;
		
		if (log.getClassifiers().size() > 0) {
			classifier = log.getClassifiers().get(0);
			Ergebnis.addInfo(Ergebnis.VERWENDETECLASSIFIER, "Classifier aus Event Log");
		} else {
			Ergebnis.addInfo(Ergebnis.VERWENDETECLASSIFIER, "Standard Classifier");
			classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
		}
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		xEventKlasse = summary.getEventClasses(classifier);
		
		return xEventKlasse;
	}
	
	/**
	 * Erstellt eine Map<Transition, XEventClass>, welche die Zuweisung von Event Klassen zu Transitionen des Petri-Netzes wieder spiegelt. 
	 * @param classes Event Klassen eines Event Logs
	 * @param net Petri-Netz, welches aus dem Event Log erstellt wurde
	 * @return Zuordnung Transition zu XEventClass
	 */
	protected Map<Transition, XEventClass> getMapping(XEventClasses classes, Petrinet net) {
		Map<Transition, XEventClass> map = new HashMap<Transition, XEventClass>();

		for (Transition transition : net.getTransitions()) {
			for (XEventClass eventClass : classes.getClasses()) {
				if (eventClass.getId().equals(transition.getAttributeMap().get(AttributeMap.LABEL))) {
					map.put(transition, eventClass);
				}
			}
		}
		return map;
	}
	
	// Getter und Setter \\
	
	public double getVW() {
		return VW;
	}
	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}
	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}

}
