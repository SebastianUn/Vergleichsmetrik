package org.processmining.plugins.guete;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * Interface fuer alle Klassen, welche die <code>Guete</code> oder Teile der <code>Guete</code> berechnen. 
 * 
 * @author Sebastian Reiners
 *
 */
public interface Guete {

	/**
	 * Wert zwischen [<code>0</code>,<code>1</code>].
	 * @param log Event-Log
	 * @param netz Petri-Netz, welches aus dem Event-Log erzeugt wurde
	 * @param markierung Initiale Markierung des Petri-Netzes
	 */
	public void berechne (XLog log, Petrinet netz, Marking markierung);
	
	/**
	 * Erhalte das aktuelle Ergebnis-Objekt
	 * @return Aktuelle Ergebnis-Objekt
	 */
	public GueteErgebnis getErgebnis();
	
	/**
	 * Setze ein neues Ergebnis-Objekt.
	 * @param ergebnis Neues Ergebnis-Objekt
	 */
	public void setErgebnis (GueteErgebnis ergebnis);
	
}
