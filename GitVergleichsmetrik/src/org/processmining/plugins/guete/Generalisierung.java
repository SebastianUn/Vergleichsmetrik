package org.processmining.plugins.guete;

import java.text.NumberFormat;

import org.deckfour.xes.model.XLog;
import org.processmining.causalactivitygraph.models.CausalActivityGraph;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.causalfootprint.ActivityGraphPlugin;

/**
 * Berechnet die Dimension der Generalisierung. Implementiert das {@link org.processmining.plugins.guete.Guete Guete Interface}.
 * @author Sebastian Reiners
 *
 */
public class Generalisierung implements Guete {

	private PluginContext Kontext;
	private GueteErgebnis Ergebnis;
	private double GW = 0.0; // Default-Wert
	
	// Konstruktoren \\ 
	
	/**
	 * Default-Konstruktor. Nicht erlaubt.
	 */
	@SuppressWarnings("unused")
	private Generalisierung () {}
	
	/**
	 * Erstellt ein Objekt der Klasse <code>Generalisierung</code>. 
	 * @param kontext Plugin Kontext des Plugins
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden
	 */
	public Generalisierung (PluginContext kontext, GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
		Kontext = kontext;
	}

	// Methoden \\ 
	
	/**
	 * Berechnete Generalisierung. Es werden der Event Log und das Petri-Netz verglichen. Speichert das Ergebnis in dem {@link #Ergebnis Ergebnis-Objekt} der Klasse. <p>
	 * '1' Perfekte übereinstimmung. <p>
	 * '0' Keine Eindeutige Aussage möglich. <p>
	 * '-1' Unterscheidung  in allen Punkten. <p>
	 * Zu diesem Zwecke werden sowohl der Event-Log als auch das Petri-Netz in einen {@link org.processmining.causalactivitygraph.models.CausalActivityGraph Causal Activity Graph}
	 * umgewandelt. Die beiden Graphen werden dann verglichen und ihre Uebereinstimmung geprueft.
	 * @param log Event-Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Markierung des Petri-Netzes, wird hier nicht benoetigt wird aber durch das Interface eine Notwendigkeit ist
	 * 
	 */
	public void berechne(XLog log, Petrinet netz, Marking markierung) {

		// Erstelle einen Causal Activity Graph aus dem Event Log und dem Petri-Netz
		ActivityGraphPlugin actGraphPlugin = new ActivityGraphPlugin();
		CausalActivityGraph CauActGraphPetri = actGraphPlugin.Erstelle(Kontext, netz);	// Aus Petri-Netz
		CausalActivityGraph CauActGraphLog = actGraphPlugin.Erstelle(Kontext, log); 	// Aus Event-Log 	
		
		// Vergleiche die beiden Graphen
		
		GW = CauActGraphPetri.getMatch(CauActGraphLog);
		
		if (Double.isNaN(GW)) {
			GW = 0;
		}
		
		double gw = GW;
		// Werte in GueteErgebnis einfuegen, Wert wird gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.GENERALISIERUNG, nf.format(gw));
		
		Kontext.log("Die Generalisierung wurde erfolgreich berechnet. Sie hat den Wert: " + GW + System.lineSeparator()
		, MessageLevel.NORMAL);
		
	}

	// Getter und Setter \\

	/**
	 * Gibt den Wert der Generalisierung zurueck.
	 * @return Wert der Generalisierung
	 */
	public double getGW() {
		return GW;
	}

	/**
	 * Gibt das Ergebnis-Objekt zurueck.
	 */
	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}

	/**
	 * Weißt der Klasse ein neues Ergebnis-Objekt zu.
	 * @param ergebnis
	 */
	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}

}
