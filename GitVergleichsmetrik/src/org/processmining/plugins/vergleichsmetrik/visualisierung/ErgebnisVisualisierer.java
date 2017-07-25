package org.processmining.plugins.vergleichsmetrik.visualisierung;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.vergleichsmetrik.VergleichsmetrikErgebnis;

@Plugin(
		name = "Erstelle Visualisierung des Ergebnisses",
		parameterLabels = {"Vergleichsmetrik Ergebnis"},
		returnLabels = { "Visualisiertes Ergebnis" },
		returnTypes = {JComponent.class},
		userAccessible = true, 
		help = "Dieses Plugin erstellt auf Grundlage eines Ergebnisses eine Visualisierung in ProM."
		)
@Visualizer
public class ErgebnisVisualisierer {


	@UITopiaVariant(
			affiliation = "Westfälische Wilhelms-Universität Münster",
			author = "Sebastian Reiners",
			email = "s_rein39@uni-muenster.de"
			)
	@PluginVariant(variantLabel = "Default", requiredParameterLabels = {0})
	public JComponent visualisiere(PluginContext kontext, VergleichsmetrikErgebnis ergebnis) throws FileNotFoundException {
		System.gc(); // Memory frei machen
		
		// export to file
		PrintWriter pw = new PrintWriter(new File("test.csv"));
		pw.write(ergebnis.getErgebnis().toStringGeordnet());
		pw.close();
		
		ErgebnisVisualisiererPanel panel = new ErgebnisVisualisiererPanel(ergebnis, kontext);
		kontext.getProvidedObjectManager().createProvidedObject("Objekt", panel, kontext);
		return panel;
	}
}
