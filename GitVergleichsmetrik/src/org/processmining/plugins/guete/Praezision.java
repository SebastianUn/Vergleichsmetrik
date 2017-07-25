package org.processmining.plugins.guete;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.search.ExpandCollection;
import org.processmining.framework.util.search.MultiThreadedSearcher;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.guete.praezision.ReplayPraezisionCost;
import org.processmining.plugins.guete.praezision.ReplayPraezisionSettings;
import org.processmining.plugins.guete.replayer.ReplayCostAddOperator;
import org.processmining.plugins.guete.replayer.ReplayState;
import org.processmining.plugins.guete.replayer.ReplayStateExpander;

/**
 * Berechnet die Dimension der Praezision. Implementiert das {@link org.processmining.plugins.guete.Guete Guete Interface}.
 * @author Sebastian Reiners
 *
 */
public class Praezision implements Guete {


	private PluginContext Kontext;
	private GueteErgebnis Ergebnis;
	private List<Double> AbweichungList = new ArrayList<Double>();
	private List<Double> BestrafungList = new ArrayList<Double>();
	
	
	private double PW = 0; // Default-Wert der Praezision
	
	// Konstruktoren \\ 
	
	/**
	 * Default-Konstruktor. Nicht erlaubt.
	 */
	@SuppressWarnings("unused")
	private Praezision () {}
	
	/**
	 * Erstellt ein Objekt der Klasse <code>Praezision</code>. 
	 * @param kontext Plugin Kontext des Plugins
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden
	 */
	public Praezision (PluginContext kontext, GueteErgebnis ergebnis) {; 
		Kontext = kontext;
		Ergebnis = ergebnis;
	}

	// Methoden \\ 
	
	/**
	 * Berechnet die Praezision auf Basis standardisierter Einstellungen. Speichert das Ergebnis in dem {@link #Ergebnis Ergebnis-Objekt} der Klasse.
	 * Einstellungen aus {@link org.processmining.plugins.guete.praezision.ReplayPraezisionSettings Praezision 
	 * Settings} werden verwendet. 
	 * @param log Event Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 */
	public void berechne(XLog log, Petrinet netz, Marking markierung) {
		
		ReplayPraezisionSettings einstellungen = new ReplayPraezisionSettings();
		PW = berechneMitKosten(log, netz, markierung, einstellungen);
		if (Double.isNaN(PW)) {
			PW = 0;
		}
		double pw = PW;
		// Werte in GueteErgebnis einfuegen, Werte werden gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.PRAEZISION, nf.format(pw));
		Ergebnis.addInfo(Ergebnis.DURCHSCHNITTLICHEABWEICHUNG, nf.format(berechneDurchschnitt(AbweichungList)));
		Ergebnis.addInfo(Ergebnis.DURCHSCHNITTLICHEBESTRAFUNG, nf.format(berechneDurchschnitt(BestrafungList)));
		
		Kontext.log("Praezision erfolgreich berechnet. Sie hat den Wert: " + PW + System.lineSeparator(), MessageLevel.NORMAL);
	}
	
	/**
	 * Berechnet die <code>behavioral appropriateness</code> fuer einen Event Log und ein dazugehoeriges Petri-Netz mit Markierung. Benutzt 
	 * standardisierte Einstellung aus {@link org.processmining.plugins.guete.praezision.ReplayPraezisionSettings Praezision 
	 * Settings} fuer die Gewichtung von Fehlern und Abweichungen. <p>
	 * Durchlaeuft den gesammten Event Log, Sequenz (= trace) fuer Sequenz, und initalisiert fuer jede Sequenz eine eigenen Suche fuer die beste Loesung. 
	 * Benutzt state-space-analysis um mit Hilfe des {@link org.processmining.framework.util.search.MultiThreadedSearcher Multi Threaded Searcher} die beste Loesung
	 * fuer die jeweilige Sequenz zu finden. Die beste Loesung wird als {@link org.processmining.plugins.guete.replayer.ReplayState Replay State} gespeichert.<p>
	 * Wenn die beste Loesung fuer die jeweilige Sequenz gefunden wurde, wird ein Bestrafungsfaktor bestimmt, welcher Fehler im Replay aufdeckt. Dazu werden 
	 * zunaechst unsichtbare Transitionen erfasst und dann ein Abweichungsfaktor mit {@link #berechneAbweichung(Collection, XLog, ReplayState, Map, ReplayPraezisionSettings)} berechnet. <p>
	 * Die <code>behavioral appropriateness</code> wird fuer jede Sequenz einzeln bestimmt. Zuletzt werden die einzelnen Werte addiert und der Durschnitt berechnet.
	 * @param log Event Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 * @param einstellungen Einstellungen mit denen Abweichung gewichtet werden
	 * @return Der Durschnitt aller Werte der <code>behavioral appropriateness</code> jeder Sequenz des Event Logs
	 * 
	 * @see
	 * http://www.processmining.org/online/conformance_checker?s[]=replay - fuer die Idee hinter der behavioral appropriateness
	 */
	public double berechneMitKosten (XLog log, Petrinet netz, Marking markierung, 
			final ReplayPraezisionSettings einstellungen) {
		
		double behavAppr = 0;
		List<Double> ergebnis = new ArrayList<Double>();
		final ReplayCostAddOperator<ReplayPraezisionCost> addOperator = ReplayPraezisionCost.addOperator; 
		
		// Erstelle XEventClasses
		XEventClassifier classifier;
		if (log.getClassifiers().size() > 0) {
			classifier = log.getClassifiers().get(0); 		// Wenn Classifier vorhanden, benutze diese
		} else {
			classifier = XLogInfoImpl.STANDARD_CLASSIFIER;	// Andernfalls benutze standard classifier
		}
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses classes = summary.getEventClasses(classifier);
		
		// Erstelle Petri-Netz Semantiken
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class); // Default
		semantics.initialize(netz.getTransitions(), markierung);
		
		// Erstelle Map
		Map<Transition, XEventClass> map = new HashMap<Transition, XEventClass>();
		for (Transition transition : netz.getTransitions()) {
			for (XEventClass eventClass : classes.getClasses()) {
				if (eventClass.getId().equals(transition.getAttributeMap().get(AttributeMap.LABEL))) {
					map.put(transition, eventClass);
				}
			}
		}
			
		for (XTrace trace : log) { // for start
			// Erstelle List mit Events
			List<XEventClass> list = new ArrayList<XEventClass>();
			for (XEvent event : trace) {
				list.add(classes.getClassOf(event));
			}
			
			// Erstelle Collection(s)
			ReplayState<ReplayPraezisionCost> initState = new ReplayState<ReplayPraezisionCost>(null, markierung, null, einstellungen.getInitialCost(), list);
			Collection<ReplayState<ReplayPraezisionCost>> initStates = new TreeSet<ReplayState<ReplayPraezisionCost>>();
			Collection<ReplayState<ReplayPraezisionCost>> finalStates = new TreeSet<ReplayState<ReplayPraezisionCost>>();
			initStates.add(initState);
	
			
			
			// Erstelle Expander und initalisiere den Multi Threaded Searcher
			ReplayStateExpander<ReplayPraezisionCost> expander = new ReplayStateExpander<ReplayPraezisionCost>(einstellungen, netz, semantics, map, addOperator);
			MultiThreadedSearcher<ReplayState<ReplayPraezisionCost>> searcher = new MultiThreadedSearcher<ReplayState<ReplayPraezisionCost>>(expander,
					new ExpandCollection<ReplayState<ReplayPraezisionCost>>() {
						private final TreeSet<ReplayState<ReplayPraezisionCost>> states = new TreeSet<ReplayState<ReplayPraezisionCost>>();
	
						public void add(Collection<? extends ReplayState<ReplayPraezisionCost>> newElements) {
							
							states.addAll(newElements);
						}
	
						public boolean isEmpty() {
							return states.isEmpty();
						}
	
						public ReplayState<ReplayPraezisionCost> pop() {
							ReplayState<ReplayPraezisionCost> head = states.first();
							states.remove(head);
							
							return head;
						}
					});
			searcher.addInitialNodes(initStates);
	
			
			/*
			 * Beginne mit der Suche
			 */
			try {
				searcher.startSearch(Kontext.getExecutor(), Kontext.getProgress(), finalStates);
			} catch (InterruptedException e) {  
				Kontext.log(e);
			} catch (ExecutionException e) {
				Kontext.log(e);
			} 
			
			if (expander.bestState == null) {
				return 0.0; // Keine Loesung gefunden
			}
	
			// Fuehre solange aus, bis erster State erreicht wurde
			int anzahl = 0;
			ReplayState<ReplayPraezisionCost> state = expander.bestState;
	
			while (state.parentState != null) {
				anzahl++;
				try {
					if (state.transition.isInvisible() == true) { // state.transition kann 'null' sein
						anzahl--;
					} 
				} catch (Exception e) {
					// Tue nichts
				}
				state = state.parentState;
			}
			
			// Erstelle Map mit feuerbaren transitionen auf der untersten Ebene
			Collection<Transition> enabledTransitions;
			synchronized (semantics) {
				semantics.setCurrentState(state.marking); // 'state' ist die unterteste Ebene 
				enabledTransitions = semantics.getExecutableTransitions();
			}
			
			/*
			 * Berechne Abweichung
			 */
			double abweichung = berechneAbweichung(enabledTransitions, log, expander.bestState, map, einstellungen);  
			
			// Berechne behavioral appropriateness
			if (anzahl > 0) {
				double bestrafung = abweichung / anzahl;
				AbweichungList.add(abweichung);
				BestrafungList.add(bestrafung);
				behavAppr = 1.0 - bestrafung;
			}
			ergebnis.add(behavAppr);
		} // for ende

		return berechneDurchschnitt(ergebnis);
		
	}

	/**
	 * Berechnet die Abweichung des Petri-Netzes vom Event Log. Berechnet hierbei die Abweichung fuer einen 
	 * {@link org.processmining.plugins.guete.replayer.ReplayState Replay State} welcher als Input Parameter gegeben sein muss. 
	 * @param enabledTransitions
	 * @param log
	 * @param state
	 * @param map
	 * @return
	 * @see
	 * org.processmining.plugins.petrinet.replayer.algorithms.behavapp.BehavAppNaiveAlg#replayLog(
	 * PluginContext, org.processmining.models.graphbased.directed.petrinet.PetrinetGraph, XLog, org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping,
	 * org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter)
	 *  */
	private double berechneAbweichung (Collection<Transition> enabledTransitions, XLog log, 
			ReplayState<ReplayPraezisionCost> state, Map<Transition, XEventClass> map, ReplayPraezisionSettings einstellungen) {
		
		int nominator = 0; // Erhoet sich, wenn Abweichung auftritt
		int denominator = 0;
		double ergebnis = 0;
		
		
		/*
		 * Erstelle eine Map von States, welche die Kinder und nicht die Eltern abspeichert
		 */
		List<ReplayState<ReplayPraezisionCost>> list = new ArrayList<ReplayState<ReplayPraezisionCost>>();
		Map<ReplayState<ReplayPraezisionCost>, List<ReplayState<ReplayPraezisionCost>>> stateToChildren = 
				new HashMap<ReplayState<ReplayPraezisionCost>, List<ReplayState<ReplayPraezisionCost>>>();
		
		// Erstelle Liste mit allen ReplayStates
		while (state.parentState != null) {
			list.add(state);
			state = state.parentState;
		} 

		// Liste umdrehen
		List<ReplayState<ReplayPraezisionCost>> kinderList = new ArrayList<ReplayState<ReplayPraezisionCost>>(list);
		Collections.reverse(kinderList);
		List<ReplayState<ReplayPraezisionCost>> kinderList2 = new ArrayList<ReplayState<ReplayPraezisionCost>>(kinderList); // Klon der kinderList
		
		// Erstelle Map
		Iterator <ReplayState<ReplayPraezisionCost>> stateIter = list.iterator();
		while (stateIter.hasNext()) {
			ReplayState<ReplayPraezisionCost> currState = stateIter.next();
			kinderList.remove(currState);
			if (kinderList.isEmpty()) {
				stateToChildren.put(currState, null);
				break;
			}
			stateToChildren.put(currState, kinderList);
		}
		
		
		/*
		 * Konstruiert eine Liste von Events aus dem Event Log, welche den Transitionen entsprechen
		 */
		Iterator<ReplayState<ReplayPraezisionCost>> replayIterator = kinderList2.iterator();
		List<XEventClass> logContinuation = new ArrayList<XEventClass>();
		XEventClass xClass;
		

		if (replayIterator.hasNext()) { 
			while (replayIterator.hasNext()) { 
				ReplayState<ReplayPraezisionCost> current = replayIterator.next(); 
				xClass = map.get(current.transition); 			// Erhalte die XEventClass ueber den Transitionen-key des Replay States
				logContinuation.add(xClass);
			}
		}

		// Pruefe ob gewichtung vorliegt 
		if (state.cost != null) {
			for (XEventClass eventClass : logContinuation) {
				if(logContinuation.contains(eventClass)) {
					// Es wird mehr Verhalten im Petri-Netz erlaubt als im Log
					nominator += state.cost.cost; // Kosten des Fehlverhaltens
				}
				denominator += state.cost.cost; 
			}

		} else { // Keine Gewichtung
			for (XEventClass eventClass : logContinuation) {
				if(logContinuation.contains(eventClass)) {
					// Es wird mehr Verhalten im Petri-Netz erlaubt als im Log
					nominator++;  
				}
				denominator++;
			}
	
		}
	
		// Erfasst Events, welche nicht vom Modell erlaubt sind
		if (kinderList2 != null) {
			for (ReplayState<ReplayPraezisionCost> repState : kinderList2) {
				if (logContinuation.contains(map.get(repState.transition))) { // Map <Transition, XEventClass>
					nominator++;
				}
			}
			denominator += kinderList2.size();
		}

		
		if (denominator > 0) {
			ergebnis += ((double) nominator / (double) denominator);
		}
		return ergebnis;
	}
	
	/**
	 * Berechnet den Durchschnitt einer gegeben List von Doubles. Gibt 0 zurueck, wenn die Liste keine Elemente besitzt.  
	 * @param list Liste mit Doubles
	 * @return Durchschnitt der Liste
	 */
	private double berechneDurchschnitt (List<Double> list) {
		double sum = 0;
		Iterator<Double> ergebnisIter = list.iterator(); 
		while (ergebnisIter.hasNext()) { 
			double next = ergebnisIter.next();
			sum += next;
		}
		if (list.size() == 0) {
			return 0;
		} else {
			return sum/list.size();
		}
	}
	
	// Getter und Setter \\ 
	
	/**
	 * Erhalte den berechneten Wert der Dimension. Ist 0, wenn keine Berechnung durchgefuehrt wurde. 
	 * @return
	 */
	public double getPW() {
			return PW;
		}

	/**
	 * Erhalte das Ergebnis-Objekt der Klasse.
	 * @return
	 */
	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}

	/**
	 * Setze ein neues Ergebnis-Objekt.
	 * @param ergebnis
	 */
	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}

}
