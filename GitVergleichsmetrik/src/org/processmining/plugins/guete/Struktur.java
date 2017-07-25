package org.processmining.plugins.guete;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.search.ExpandCollection;
import org.processmining.framework.util.search.MultiThreadedSearcher;
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
import org.processmining.plugins.guete.replayerohnekosten.ReplayState;
import org.processmining.plugins.guete.replayerohnekosten.ReplayStateExpander;
import org.processmining.plugins.guete.replayerohnekosten.ReplayStrukturSettings;
import org.processmining.plugins.petrinet.reduction.Murata;
import org.processmining.plugins.petrinet.reduction.MurataInput;
import org.processmining.plugins.petrinet.reduction.MurataParameters;

/**
 * Berechnung die Dimension der Struktur. Implementiert das {@link org.processmining.plugins.guete.Guete Guete Interface}.
 * @author Sebastian Reiners
 *
 */
public class Struktur implements Guete {

	private PluginContext Kontext;
	private GueteErgebnis Ergebnis;
	private double Gewichtung;
	
	private double SW = 0; // Default-Wert
	
	// Konstruktoren \\ 
	
	/**
	 * Erstellt ein Objekt der Klasse <code>Struktur</code>. 
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden.
	 * @param context Plugin Kontext der Klasse zum erstellen von Nachrichten
	 * @param gewichtung Gewichtung der Simple zur Advanced Strucutral Appropriatness
	 */
	public Struktur (PluginContext context, GueteErgebnis ergebnis, double gewichtung) {
		Ergebnis = ergebnis;
		Kontext = context;
		Gewichtung = gewichtung;
	}

	// Methoden \\
	
	/**
	 * Berechnet die Struktur. Speichert das Ergebnis in dem {@link #Ergebnis Ergebnis-Objekt} der Klasse. 
	 * Berechnet zunaechst die simple strucutural appropriatness in {@link #BerechneSimple(Petrinet)}. 
	 * Berechnet dann die advanced strucutural appropriatness in {@link #BerechneAdvanced(XLog, Petrinet, Marking)}. 
	 * Zuletzt werden die beiden Werte kombiniert. 
	 * @param log Event Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 */
	public void berechne(XLog log, Petrinet netz, Marking markierung) {
		Kontext.log("Die 'simple strucutural appropriatness' wird berechnet." , MessageLevel.NORMAL);
		double simple = BerechneSimple(netz);
		
		Kontext.log("Die 'simple strucutural appropriatness'hat den Wert: " + simple , MessageLevel.NORMAL);
		
		Kontext.log("Die 'advanced strucutural appropriatness' wird berechnet." , MessageLevel.NORMAL);
		double advanced = BerechneAdvanced(log, netz, markierung);
		
		Kontext.log("Die 'advanced strucutural appropriatness' hat den Wert: " + advanced, MessageLevel.NORMAL);
		
		SW = advanced - Gewichtung * simple;
		
		if (SW < 0) {
			SW = 0;
		}
		if (Double.isNaN(SW)) {
			SW = 0;
		}
		
		double sw = SW;
		// Werte in GueteErgebnis einfuegen, Werte werden gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.STRUKTUR, nf.format(sw));
		Ergebnis.addInfo(Ergebnis.GEWICHTUNGSIMPLEADVANCED, Gewichtung + "");
		Ergebnis.addInfo(Ergebnis.SIMPLESTRUCUTURALAPPROPRIATNESS, nf.format(simple));
		Ergebnis.addInfo(Ergebnis.ADVANCEDSTRUCUTURALAPPROPRIATNESS, nf.format(advanced));
		
		Kontext.log("Aus der 'advanced strucutural appropriatness' und der 'simple strucutural appropriatness'"
		+ " ergibt sich ein Wert von " + SW + " fuer die Struktur." + System.lineSeparator(), MessageLevel.NORMAL);
	}
	
	/**
	 * Berechnet die simple strucutural appropriatness. Dazu werden die Transitionen und Stellen des Petri-Netzes gezaehlt.
	 * Zwischenergebnisse werden als Statistik im Ergebnis-Objekt gespeichert.  
	 * @param netz Petri-Netz
	 * @return
	 */
	private double BerechneSimple (Petrinet netz) {
		double anzTransitionen = 0; // Zunaechst existieren keine Transitionen oder Verbindungen
		double anzObjekte = 0;
		
		
		Collection<Transition> transitionen = netz.getTransitions(); // Erhalte alle Transitionen in dem Petri-Netz
		ArrayList<Transition> listTrans = new ArrayList<Transition>();
		Iterator<Transition> it = transitionen.iterator();
		while (it.hasNext()) { 				// Erstellt eine Liste, welche alle Transitionen enthaelt (einzigartig)
			Transition trans = it.next();	// Erhalte das naechste Element aus der Collection
			if (listTrans.contains(trans)) {
				continue; 					// Element ist bereits vorhanden, naechste Iteration
			} else {
				listTrans.add(trans);		// Element ist nicht vorhanden, fuege hinzu
			}
		}
		
		anzTransitionen = listTrans.size();
		anzObjekte = transitionen.size() + netz.getPlaces().size();
		
		Ergebnis.addInfo(Ergebnis.ANZAHLEINZIGARTIGETRANSITIONEN, anzTransitionen + "");
		Ergebnis.addInfo(Ergebnis.ANZAHLSTELLEN, netz.getPlaces().size() + "");
		Ergebnis.addInfo(Ergebnis.ANZAHLTRANSITIONEN, netz.getTransitions().size() + "");
		
		Kontext.log("Es gibt " + anzTransitionen +" einzigartige Transitionen und " +  anzObjekte + 
				" Stellen und Transitionen.", MessageLevel.TEST);
		double ergebnis =  0;
		ergebnis = ((anzTransitionen + 2) / (anzObjekte)); // Berechne simple strucutural appropriatness
		return ergebnis;
	}
	
	/**
	 * Berechnet die advanced strucutural appropriatness. Es werden alternative doppelte Transitionen sowie ueberfluessige
	 * unsichtbare Transitionen gezaehlt. Je mehr dieser Transitionen im Vergleich zur gesamten Anzahl von Transitionen vorhanden sind, desto 
	 * kleiner wird der Wert der advanced structural appropriateness. Zwischenergebnisse werden im Ergebnis-Objekt der Klasse gespeichert. 
	 * @param log Event Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 * @return
	 */
	private double BerechneAdvanced (XLog log, Petrinet netz, Marking markierung) {
		int anzTransitionen = 0;  	// Anzahl Transitionen
		int anzDoppTransitionen = 0;// Anzahl Doppelter Transitionen
		int anzUnsTransitionen = 0;	// Anzhal Ueberfluessiger unsichterbarer Transitionen
		
		Collection<Transition> transitionen = netz.getTransitions(); // Erhalte alle Transitionen in dem Petri-Netz 
		anzTransitionen = transitionen.size();
		
		if (anzTransitionen == 0) return 0; // Wenn keine Transitionen vorhanden sind, gebe den Wert 0 zurueck
		
		/*
		 * Doppelte Transitionen
		 * 
		 * Erstellt zwei Listen. Die erste Liste enhaelt alle Transitionen genau ein mal. 
		 * Die zweite Liste enhaelt alle Transitionen, welche mehr als einmal vorkommen.
		 */
		ArrayList<Transition> listTrans = new ArrayList<Transition>();
		ArrayList<Transition> listDopp = new ArrayList<Transition>();
		Iterator<Transition> TransIt = transitionen.iterator();
		while (TransIt.hasNext()) { 				// Erstellt eine Liste, welche alle einzigartigen Transitionen enthaelt 
			Transition trans = TransIt.next();		// Erhalte das naechste Element aus der Collection
			if (listTrans.contains(trans)) {
				listDopp.add(trans); 		// Element ist bereits vorhanden
			} else {
				listTrans.add(trans);		// Element ist nicht vorhanden, 
			}
		}
		
		
		Iterator<XTrace> TraceIt = log.iterator();
		TransIt = listDopp.iterator(); // Reset Iterator
		
		while (TransIt.hasNext()) {
			Transition trans = TransIt.next();
			List<XTrace> list = new ArrayList<XTrace>(); // Liste wird jeden durchlauf neu erstellt
			while (TraceIt.hasNext()) {
				XTrace trace = TraceIt.next();
				XEvent event = (XEvent) trans;
				if (trace.contains(event)) {
					list.add(trace); 			// Fuege alle Sequenzen hinzu, die Element mit doppelten Transitionen enthalten
				}
			}
			// Durchlaufe Sequenzen auf der Suche nach alternativen doppelten Transitionen
			anzDoppTransitionen = sucheDoppelteTransitionen(log, trans, netz, list, markierung);
		}
		
		/*
		 * Unsichtbare Transitionen
		 */
		// Wenn keine unsichtbaren Transitionen vorhanden sind, muss auch nicht nach ihnen gesucht werden
		TransIt = transitionen.iterator();
		int AnzUnsTransGesamt = 0; // Zaehlt die gesamte Anzahl an unsichtbaren Transitionen im Petri-Netz
		boolean murataDurchgefuehrt = false;
		while (TransIt.hasNext()) {
			Transition trans = TransIt.next();
			if (trans.isInvisible()) {
				AnzUnsTransGesamt++;
				if (murataDurchgefuehrt == false) {
					murataDurchgefuehrt = true;
					int anzTransRedNetz = 0;
					// Es gibt mindestens eine unsichtbare Transition, starte suche nach Ueberfluessigen mit Murata-Regeln 
					Murata reduzierer = new Murata();
					try {
						MurataInput input = new MurataInput(netz, markierung);
						input.setVisibleSacred(netz);
						MurataParameters parameter = new MurataParameters(); // Default
						Petrinet reduziertesNetz = reduzierer.run(Kontext, input, parameter).getNet();
						anzTransRedNetz = reduziertesNetz.getTransitions().size();
						
						anzUnsTransitionen = transitionen.size() - anzTransRedNetz;
					} catch (ConnectionCannotBeObtained e) {
						e.printStackTrace();
					} 
				}
				continue; // Suche nach restlichen aber fuehre Murata nicht nochmal durch
			} else {
				// Suche weiter
			}
			
		}
		
		Ergebnis.addInfo(Ergebnis.ANZAHLUNSICHTBARERTRANSITIONEN, AnzUnsTransGesamt + "");
		Ergebnis.addInfo(Ergebnis.ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN, anzUnsTransitionen + "");
		Ergebnis.addInfo(Ergebnis.ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN, anzDoppTransitionen + "");
		Kontext.log("Es wurden " + anzDoppTransitionen + " doppelte und "+ anzUnsTransitionen + " ueberfluessige Transitionen gefunden.");
		
		// Berechne advanced strucutural appropriatness
		double ergebnis = (double) (anzTransitionen - (anzDoppTransitionen + anzUnsTransitionen)) / (anzTransitionen);  
																										   
		return ergebnis;
	}
	
	
	
	/**
	 * Durchsucht eine Liste mit Sequenzen in einem Petri-Netz. Gibt die Anzahl der Sequenzen zurueck, 
	 * welche nicht eindeutige Pfade enthalten. Nicht eindeutige Pfade sind solche, in denen alternative
	 * doppelte Aktivitaeten vorkommen. <p>
	 * Benutzt den <code>MultiThreadedSearcher</code>. 
	 * @see org.processmining.framework.util.search.MultiThreadedSearcher
	 * 
	 * @param netz
	 * @param list
	 * @param markierung
	 * @return Anzahl alternativer Doppelter Akivitaeten
	 */
	private int sucheDoppelteTransitionen (XLog log, Transition trans, Petrinet netz, List<XTrace> list, Marking markierung) {
		final ReplayStrukturSettings einstellungen = new ReplayStrukturSettings();
		
		// Erstelle Petri-Netz Semantiken
		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class); // Default
		semantics.initialize(netz.getTransitions(), markierung);
		
		// Erstelle Collection(s)
		ReplayState initState = new ReplayState(null, markierung, null, list);
		Collection<ReplayState> initStates = new TreeSet<ReplayState>();
		Collection<ReplayState> finalStates = new TreeSet<ReplayState>();
		initStates.add(initState);
		
		// Erstelle Map
		XEventClasses klassen = XLogInfoFactory.createLogInfo(log).getEventClasses();
		Map<Transition, XEventClass> map = new HashMap<Transition, XEventClass>();
		for (Transition transition : netz.getTransitions()) {
			for (XEventClass eventClass : klassen.getClasses()) {
				if (eventClass.getId().equals(transition.getAttributeMap().get(AttributeMap.LABEL))) {
					map.put(transition, eventClass);
				}
			}
		}
		
		// MultiThreadedSearcher initialisieren 
		ReplayStateExpander expander = new ReplayStateExpander(einstellungen, netz, semantics, map);
		MultiThreadedSearcher<ReplayState> searcher = new MultiThreadedSearcher<ReplayState>(expander, new ExpandCollection<ReplayState>() {
			private final TreeSet<ReplayState> states = new TreeSet<ReplayState>();
			
			public void add(Collection<? extends ReplayState> newElements) {
				
				states.addAll(newElements);
			}

			public boolean isEmpty() {
				return states.isEmpty();
			}

			public ReplayState pop() {
				ReplayState head = states.first();
				states.remove(head);
				return head;
			}
		});
		
		searcher.addInitialNodes(initStates);
		
		try {
			searcher.startSearch(Kontext.getExecutor(), Kontext.getProgress(), finalStates);
		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		}
		
		int ergebnis = 0;
		
		if(expander.Loesungen.isEmpty()) {
			// Keine Loesung, gebe 0 zurueck
		} else {
			ergebnis = sucheAlternative(netz, list, markierung, initState, trans, expander, trans, finalStates, finalStates);
		}
		
		return ergebnis;
	}
	
	/**
	 * Sucht eine alternative Loesung in dem gegebenen Petri-Netz. Dazu wird die Transition aus dem Petri-Netz entfernt, 
	 * welche ein Teil der aktuellen Loesung ist. Um die Integritaet des Petri-Netzes zu wahren, wird eine Dummy Transition erstellt, welche 
	 * die gleichen eingehenden und ausgehenden Kanten besitzt. Wenn die Dummy Transition aufgerufen wird bedeutet dies, dass es keine
	 * Alternative gibt. 
	 * 
	 * @param netz
	 * @param list
	 * @param markierung
	 * @param searcher
	 * @param expander
	 * @return
	 */
	private int sucheAlternative (Petrinet netz, List<XTrace> list, Marking markierung, ReplayState loesung,
			Transition zuEntfernen, ReplayStateExpander expander, Transition trans,
			Collection<ReplayState> initStates, Collection<ReplayState> finalStates) {
		
		int alternativen = 0;
		
		// Bereite Petri-Netz vor 
		Transition t = netz.addTransition("DieserNameSollteNiemalsExisitieren" + Math.random()); // Erstelle eine neue Transition, noch ohne Verbindungen
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = netz.getInEdges(zuEntfernen); 
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = netz.getOutEdges(zuEntfernen);
		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preSetIter = preset.iterator();
		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postSetIter = postset.iterator();
		
		/*
		 *  Entfernen aller ankommenden Verbindungen der Transition.
		 *  Zusaetzlich: Erstelle  neue Verbindungen 
		 */
		while (preSetIter.hasNext()) { 
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = preSetIter.next();
			
			Arc arc = (Arc) edge;
			Place place = (Place) arc.getSource(); 
			netz.addArc(place, t); 			// Fuege eingehende Verbindung hinzu
			
			
			netz.removeEdge(edge); // Entfernen
		}
		
		/*
		 * Entferne alle ausgehenden Verbindungen der Transition
		 * Zusaetzlich: Erstelle  neue Verbindungen 
		 */ 
		while (postSetIter.hasNext()) {
			PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = postSetIter.next();
			
			Arc arc = (Arc) edge;
			Place place = (Place) arc.getTarget(); 
			netz.addArc(t, place); 			// Fuege ausgehende Verbindung hinzu
			
			netz.removeEdge(edge);
		}
		
		// Entfernen der Transition
		netz.removeTransition(zuEntfernen);
		
		
		/*
		 * Initalisiere Multi Threaded Searcher
		 */
		MultiThreadedSearcher<ReplayState> searcher = new MultiThreadedSearcher<ReplayState>(expander, new ExpandCollection<ReplayState>() {
			private final TreeSet<ReplayState> states = new TreeSet<ReplayState>();
			
			public void add(Collection<? extends ReplayState> newElements) {
				
				states.addAll(newElements);
			}

			public boolean isEmpty() {
				return states.isEmpty();
			}

			public ReplayState pop() {
				ReplayState head = states.first();
				states.remove(head);
				return head;
			}
		});
		
		searcher.addInitialNodes(initStates);
		
		try {
			searcher.startSearch(Kontext.getExecutor(), Kontext.getProgress(), finalStates);
		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		}
		
		List <ReplayState> loesungen = expander.Loesungen;
		ArrayList<Transition> sequence = new ArrayList<Transition>();
		
		for (int i = 0; i < loesungen.size(); i++) {
			ReplayState state = loesungen.get(i);
			while (state != null) {
				if (state.Transition != null) {
					sequence.add(0, state.Transition);
				} 
				if (sequence.contains(t)) {
					break; // Dummy-Element vorhanden, keine alternative Loesung 
				}
				for (int y = 0; y < sequence.size(); y++) {
					if (trans.getLabel().equals( sequence.get(y).getLabel())) {
						if (testeAlternative(state, t, netz) == false) {
							// Sequenz wurde getestet, das Ergebnis was, dass keine doppelte Transition vorliegt
							break; 
						} else {
							alternativen++;
						}
					}
				}
				state = state.ElternState;
			}
		}
		return alternativen;
	}
	
	/**
	 * Es wird getestet ob, ausgehend vom Replay-State, die Transition erreicht werden kann - also eine Alternative vorliegt.
	 * Bei vorliegender Schleife der Laenge 1 oder 2 liegt keine Alternative vor.
	 * Wenn die gefundene Transition von der dummy-Transition errreichbar ist oder die gefundene Transition die dummy-
	 * Transition erreichen kann, liegt ebenfalls keine Alternative vor.  
	 * @param state Der gefundene Replay-State
	 * @param t	Die zu testende Transition
	 * @param netz Petri-Netz mit Dummy Transition
	 * @return True, wenn eine Alternative besteht, ansonsten false
	 */
	private boolean testeAlternative (ReplayState state, Transition t, Petrinet netz) {
		
		// Teste auf Schleife
		if (state.equals(state.ElternState)) {
			// Schleife der Laenge 1
			return false;
		}
		if (state.equals(state.ElternState.ElternState)) {
			// Schleife der Laenge 2
			return false;
		}
		
		// Teste auf Erreichbarkeit
		if (istErreichbar(state.Transition, t, netz)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Ist die Transition t2 von t1 oder t1 von t2 erreichbar. <code> true </code> wenn ja, <code> false </code> wenn nein.
	 * @param t1 Transition 1
	 * @param t2 Transition 2
	 * @param netz Petri-Netz mit Dummy Transition
	 * @return
	 */
	private boolean istErreichbar (Transition t1, Transition t2, Petrinet netz) {

		Collection<Transition> t1zut2 = FolgendeTransitionen(t1, netz);
		Collection<Transition> t2zut1 = FolgendeTransitionen(t2, netz);
		
		if (t1zut2.contains(t2) || t2zut1.contains(t1)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Erstellt eine Collection aller folgender (erreichbarer) Transitionen.
	 * Wird rekursiv aufgerufen.
	 * @param t1 Erste Transition
	 * @param netz Petri-Netz
	 * @return
	 */
	private Collection<Transition> FolgendeTransitionen (Transition t1, Petrinet netz) {
		
		Collection<Transition> transitionen = null;
		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = netz.getOutEdges(t1);
		Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> Iter = postset.iterator();
		
		while (Iter.hasNext()) {
				PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge = Iter.next();
	
				Arc arc = (Arc) edge;
				Place place = (Place) arc.getTarget(); // Finde die naechste Stelle
				
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postsetPlace = netz.getOutEdges(place);
				Iterator<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postSetPlaceIter = postsetPlace.iterator();
			
				while (postSetPlaceIter.hasNext()) {
					PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge2 = postSetPlaceIter.next();
					Arc arc2 = (Arc) edge2;
					
					// Rufe methode rekursiv auf, bis alle Transitionen gefunden wurden
					transitionen = FolgendeTransitionen((Transition) arc2.getTarget(), netz); 
				}
				
		}
		transitionen.add(t1);
		return transitionen;
	}
	
	// Getter und Setter \\
	
	public double getSW() {
		return SW;
	}

	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}

	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}

	public double getGewichtung() {
		return Gewichtung;
	}

	public void setGewichtung(double gewichtung) {
		Gewichtung = gewichtung;
	}


}
