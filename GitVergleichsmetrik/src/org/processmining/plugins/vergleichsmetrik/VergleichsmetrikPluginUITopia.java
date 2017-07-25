package org.processmining.plugins.vergleichsmetrik;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.guete.GueteErgebnis;
import org.processmining.plugins.guete.gui.VergleichsmetrikUIInputParameter;
import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikEinstellungen;

/**
 * Diese Klasse wird in {@link VergleichsPlugin} als Methode aufgerufen.
 * @author Sebastians
 * @deprecated
 */

@Deprecated
@Plugin(
		name = "Vergleichsmetrik UI",
		parameterLabels = {"Event-Log", "Petri-Netz", "Markierung"},
		returnLabels = { "Güte des Petri-Netzes" },
		returnTypes = {VergleichsmetrikErgebnis.class},
		userAccessible = false, 
		help = "Dieses Plugin bewertet ein Petri-Netz und einen zugehörigen Event-Log anhand einer Gütefunktion mit UI Erweiterungen."
		)
public class VergleichsmetrikPluginUITopia {
	
	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "UI", requiredParameterLabels = {0, 1, 2})
	public static VergleichsmetrikErgebnis Vergleich (UIPluginContext kontext, XLog log, Petrinet netz, Marking markierung) {
		kontext.log("Vergleichsmetrik wird gestartet.", MessageLevel.NORMAL);
		// Zeit Start
		long startTime = System.nanoTime();
		
		// Panel initialisierung
		VergleichsmetrikUIInputParameter panel = new VergleichsmetrikUIInputParameter();
		
		// Erstelle Dialog mit panel
		VergleichsmetrikEinstellungen einstellungen = new VergleichsmetrikEinstellungen();
		InteractionResult result = kontext.showConfiguration("Vergleichsmetrik Parameter", panel);
		
		if (result == InteractionResult.CANCEL) {
			return null;
		}
		einstellungen = panel.getEinstellungen();
		kontext.getProvidedObjectManager().createProvidedObject("Vergleichsmetrik Einstellungen", einstellungen, kontext);
		
		// Erstelle Ergebnis-Objekte
		GueteErgebnis gueteErgebnis = new GueteErgebnis();
		VergleichsmetrikErgebnis ergebnis = new VergleichsmetrikErgebnis(kontext, gueteErgebnis); 
		ergebnis.setEinstellungen(einstellungen); // Uebernehme Einstellungen aus der GUI
		
		// Berechne die Guete
		ergebnis.BerechneGuete(log, netz, markierung);
		gueteErgebnis = ergebnis.getErgebnis();
			
		// Zeit Ende
		long endTime = System.nanoTime();
		long zeit = (endTime - startTime) / 1000000; // Division durch 1000000 -> Millisekunden
		double zeitInMin = (double) zeit / 1000 / 60;
		String zeitInMin2 = zeitInMin + "";
		try {
			zeitInMin2 = zeitInMin2.substring(0, 4); // 2 Stellen nach dem Komma
		} catch (Exception e) { };
		gueteErgebnis.addInfo(gueteErgebnis.ZEIT, zeit + "");

				
		// Gebe das Ergebnis zurueck
		kontext.log("Das Petri-Netz hat eine Guete von: " + ergebnis.getGuete() + ". Die Berechnung dauerte " + zeit + " Millisekunden. Dies entspricht " +
					zeitInMin2 + " Minuten");
		return ergebnis;
	}
}
