package org.processmining.plugins.vergleichsmetrik.visualisierung;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.plugins.guete.GueteErgebnis;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;

/**
 * Erstellt eine Tabelle aller Werte aus {@link GueteErgebnis}.
 * @author Sebastians
 *
 */
public class WertePanel extends JPanel {

	private static final long serialVersionUID = 1808393248846993669L;
	
	/*
	 * GUI
	 */
	private ProMTable tabelle;
	private DefaultTableModel tabellenModell;
	private Object[] zeilenIdentifizierer;
	private Vector<String> zeilenIdentifiziererV4;
	
	/**
	 * 
	 * @param gueteErgebnis
	 */
	public WertePanel (GueteErgebnis gueteErgebnis) {
		
		gueteErgebnis.ordneMap();
		Map<String, Object> infoMap = new HashMap<String, Object>(gueteErgebnis.getInfoGeordnet());
		
		SlickerFactory factory = SlickerFactory.instance();
		
		zeilenIdentifizierer = new Object[] { "Property", "Wert" };
		zeilenIdentifiziererV4 = new Vector<String>(infoMap.size());
		
		for (String key : infoMap.keySet()) {
			zeilenIdentifiziererV4.add(key);
		}
		
		tabellenModell = new DefaultTableModel() {
			private static final long serialVersionUID = -4303950078200984098L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		tabelle = new ProMTable(tabellenModell);
		
		// GUI hinzufuegen
		double[][] size = new double[][] {{200, 600}, {60, 400, 60}};
		setLayout(new TableLayout(size));
		add(factory.createLabel("Ausgewählte Element*"), "0,0,r,c");
		add(tabelle, "0,1,1,1");
		
		erhalteAlleWerte(infoMap);
		
	}
	
	// Methoden \\
	
	private void erhalteAlleWerte(Map<String, Object> infoMap) {
		
		
		Object[][] info = new Object[infoMap.size()][2];
		int counter = 0;
		for (String key : infoMap.keySet()) {
			info[counter] = new Object[] {key, infoMap.get(key)};
			counter++;
		}
		
		tabellenModell.setDataVector(info, zeilenIdentifizierer);
		tabelle.doLayout();
	}
}
