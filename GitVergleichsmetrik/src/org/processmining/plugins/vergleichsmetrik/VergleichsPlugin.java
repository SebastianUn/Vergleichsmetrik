package org.processmining.plugins.vergleichsmetrik;

import java.io.FileNotFoundException;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.guete.GueteErgebnis;
import org.processmining.plugins.guete.gui.VergleichsmetrikUIInputParameter;
import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikEinstellungen;
import org.processmining.plugins.vergleichsmetrik.visualisierung.ErgebnisVisualisierer;
import org.processmining.plugins.vergleichsmetrik.visualisierung.ErgebnisVisualisiererPanel;


@Plugin(
		name = "Vergleichsmetrik",
		parameterLabels = {"Event-Log", "Petri-Netz", "Markierung", "Einstellungen"},
		returnLabels = { "Güte des Petri-Netzes" },
		returnTypes = {VergleichsmetrikErgebnis.class},
		userAccessible = true, 
		help = "Dieses Plugin bewertet ein Petri-Netz und einen zugehörigen Event-Log anhand einer Gütefunktion."
		)
public class VergleichsPlugin {
	
	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "Ohne UI", requiredParameterLabels = {0, 1, 2, 3})
	public static VergleichsmetrikErgebnis Vergleich (PluginContext kontext, XLog log, Petrinet netz, Marking markierung, VergleichsmetrikEinstellungen einstellungen) {
		kontext.log("Vergleichsmetrik wird gestartet.", MessageLevel.NORMAL);
		// Zeit Start
		long startTime = System.nanoTime();
		
		// Erstelle Ergebnis-Objekte
		GueteErgebnis gueteErgebnis = new GueteErgebnis();
		VergleichsmetrikErgebnis ergebnis = new VergleichsmetrikErgebnis(kontext, gueteErgebnis); 
		if (einstellungen == null) {
			einstellungen = new VergleichsmetrikEinstellungen();
		}
		ergebnis.setEinstellungen(einstellungen); // Uebernehme Einstellungen
		
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

		// Visualisiere Ergebnis
		ErgebnisVisualisierer visualisiererPlugin = new ErgebnisVisualisierer();
		try {
			visualisiererPlugin.visualisiere(kontext, ergebnis);
			kontext.getProvidedObjectManager().createProvidedObject("Ergebnis", new ErgebnisVisualisiererPanel(ergebnis, kontext), kontext);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		// Gebe das Ergebnis zurueck
		kontext.log("Das Petri-Netz hat eine Guete von: " + ergebnis.getGuete() + ". Die Berechnung dauerte " + zeit + " Millisekunden. Dies entspricht " +
					zeitInMin2 + " Minuten");
		return ergebnis;
	}
	
	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "UI", requiredParameterLabels = {0, 1, 2})
	public static VergleichsmetrikErgebnis Vergleich (UIPluginContext kontext, XLog log, Petrinet netz, Marking markierung) {
		kontext.log("Vergleichsmetrik wird gestartet.", MessageLevel.NORMAL);
		
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
		
		VergleichsmetrikErgebnis ergebnis = Vergleich(kontext, log, netz, markierung, einstellungen);
		// Visualisiere Ergebnis
			ErgebnisVisualisierer visualisiererPlugin = new ErgebnisVisualisierer();
			try {
				visualisiererPlugin.visualisiere(kontext, ergebnis);
				kontext.getProvidedObjectManager().createProvidedObject("Ergebnis", new ErgebnisVisualisiererPanel(ergebnis, kontext), kontext);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
		return ergebnis;
	}
	
}
