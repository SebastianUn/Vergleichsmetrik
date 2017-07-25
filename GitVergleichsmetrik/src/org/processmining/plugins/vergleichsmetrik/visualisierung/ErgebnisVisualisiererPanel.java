package org.processmining.plugins.vergleichsmetrik.visualisierung;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.plugins.guete.GueteErgebnis;
import org.processmining.plugins.petrinet.replayresult.visualization.ProMTableWithoutHeader;
import org.processmining.plugins.vergleichsmetrik.VergleichsmetrikErgebnis;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
public class ErgebnisVisualisiererPanel extends JPanel {

	// GUI
	private final WertePanel wertePanel;
	
	// Slicker
	@SuppressWarnings("unused")
	private SlickerFactory factory;
	@SuppressWarnings("unused")
	private SlickerDecorator decorator;
	
	// Info
	@SuppressWarnings("unused")
	private final VergleichsmetrikErgebnis vergleichsmetrikErgebnis;
	private final GueteErgebnis gueteErgebnis;

	// Konstruktoren \\
	
	public ErgebnisVisualisiererPanel(VergleichsmetrikErgebnis ergebnis, PluginContext kontext) {
		
		
		// Slickerbox
		factory = SlickerFactory.instance();
		decorator = SlickerDecorator.instance();
		
		// Ergebnisse
		vergleichsmetrikErgebnis = ergebnis;
		gueteErgebnis = ergebnis.getErgebnis();
		
		// Tabellen-Info
		wertePanel = new WertePanel(gueteErgebnis);
		add("Statistiken", wertePanel);
		
		validate();
		repaint();
		
	}
	
	@SuppressWarnings("unused")
	private ProMTableWithoutHeader erstelleTabelle (Map<String, Object> infoMap, PluginContext kontext) throws Exception {
		
		int index = 0;
		Object[][] infoTable;
		
		// Ueberpruefe die Map
		if (infoMap != null) {
			infoTable = new Object[infoMap.size()][2]; // Erstelle Tabelle so groﬂ wie die Ergebnis-Map
		} else {
			kontext.log("Ergebnis-Map nicht vorhanden. Ergebnis kann nicht erstellt werden.", MessageLevel.DEBUG);
			throw new Exception("Ergebnis Map ist null");
		}
		
		for (String key : infoMap.keySet()) {
			infoTable[index++] = new Object[] {key, infoMap.get(key)}; 
		}
		
		// Layout der Tabelle
		DefaultTableModel tableModel = new DefaultTableModel(infoTable, new Object[] { "Property", "Value" }) {
			private static final long serialVersionUID = -4303950078200984098L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		// Erstelle Tabelle
		ProMTableWithoutHeader promTable = new ProMTableWithoutHeader(tableModel);
		promTable.setPreferredSize(new Dimension(300, 150));
		promTable.setMinimumSize(new Dimension(300, 150));
		promTable.setPreferredWidth(0, 180);
		promTable.setPreferredWidth(1, 20);
		return promTable;

	}
	
	
}
