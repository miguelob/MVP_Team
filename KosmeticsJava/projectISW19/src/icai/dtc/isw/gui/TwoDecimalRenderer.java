package icai.dtc.isw.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class TwoDecimalRenderer extends DefaultListCellRenderer {

	  /**
	*
	*/
	private static final long serialVersionUID = 1L;
	private ListCellRenderer defaultRenderer;

	  public TwoDecimalRenderer(ListCellRenderer defaultRenderer) {
	    this.defaultRenderer = defaultRenderer;
	  }

	  @Override
	  public Component getListCellRendererComponent(JList list, Object value,
	      int index, boolean isSelected, boolean cellHasFocus) {
	    Component c = defaultRenderer.getListCellRendererComponent(list, value,
	        index, isSelected, cellHasFocus);
	    if (c instanceof JLabel) {
	      if (isSelected) {
	        c.setBackground(Color.PINK);
	      } else {
	        c.setBackground(Color.WHITE);
	      }
	    } else {
	      c.setBackground(Color.WHITE);
	      c = super.getListCellRendererComponent(list, value, index, isSelected,
	          cellHasFocus);
	    }
	    return c;
	  }
	}