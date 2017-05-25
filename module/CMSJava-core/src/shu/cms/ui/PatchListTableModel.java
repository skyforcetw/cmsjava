package shu.cms.ui;

import java.text.*;
import java.util.List;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import shu.cms.*;
import shu.cms.colorspace.depend.*;
import shu.cms.colorspace.independ.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class PatchListTableModel
    extends AbstractTableModel {
  protected List<Patch> patchList;
  public PatchListTableModel(List<Patch> patchList) {
    setPatchList(patchList);
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Class getColumnClass(int c) {
    Object val = getValueAt(0, c);
    if (val == null) {
      return null;
    }
    else {
      return val.getClass();
    }
  }

  public String[] columnNames = {
//      "Name", "XYZ", "Lab", "RGB", "Spectra", "Reflect Spectra"
      "ID", "Name", "Target", "Measure", "R", "G", "B", "X", "Y", "Z",
      "L*", "a*", "b*"
  };
  /**
   * Returns the number of columns in the model.
   *
   * @return the number of columns in the model
   */
  public int getColumnCount() {
    return 13;
  }

  /**
   * Returns the number of rows in the model.
   *
   * @return the number of rows in the model
   */
  public int getRowCount() {
    return patchList.size();
  }

  /**
   * Returns the value for the cell at <code>columnIndex</code> and
   * <code>rowIndex</code>.
   *
   * @param rowIndex the row whose value is to be queried
   * @param columnIndex the column whose value is to be queried
   * @return the value Object at the specified cell
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    Patch p = patchList.get(rowIndex);
    switch (columnIndex) {
      case 0:
        return rowIndex + 1;
      case 1:
        return p.getName();
      case 2:
        return p.getRGB().getColor();
      case 4:
        return p.getRGB().R;
      case 5:
        return p.getRGB().G;
      case 6:
        return p.getRGB().B;
      case 3:
        CIEXYZ XYZ = (CIEXYZ) p.getXYZ().clone();
        if (whitePatch != null) {
          XYZ.normalize(whitePatch.getXYZ());
        }
        RGB rgb = RGB.fromXYZ(XYZ, RGB.ColorSpace.sRGB);
        return rgb;
      case 7:
        return p.getXYZ().X;
      case 8:
        return p.getXYZ().Y;
      case 9:
        return p.getXYZ().Z;
      case 10:
        if (whitePatch != null) {
          return p.getLab().L;
        }
        else {
          return "?";
        }
      case 11:
        if (whitePatch != null) {
          return p.getLab().a;
        }
        else {
          return "?";
        }
      case 12:
        if (whitePatch != null) {
          return p.getLab().b;
        }
        else {
          return "?";
        }
      default:
        return null;
    }
  }

  protected CIEXYZRenderer XYZRenderer = new CIEXYZRenderer();
  public CIEXYZRenderer getCIEXYZRenderer() {
    return XYZRenderer;
  }

  private final static DecimalFormat df = new DecimalFormat("##.#####");
  private Patch whitePatch;

  protected class CIEXYZRenderer
      extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      JLabel label = (JLabel)
          super.getTableCellRendererComponent(
              table, value, isSelected,
              hasFocus, row, column);
      if (!isSelected) {
        label.setText(df.format(value));
      }
      label.setHorizontalAlignment(JLabel.RIGHT);

      Patch p = patchList.get(row);
      double[] xyValues = p.getXYZ().getxyValues();
      label.setToolTipText("CIExy: " + df.format(xyValues[0]) + ", " +
                           df.format(xyValues[1]));
      return label;
    }

  }

  protected static class ColorRenderer
      extends JLabel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public ColorRenderer(boolean isBordered) {
      this.isBordered = isBordered;
      setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
        JTable table, Object color,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
      Color newColor = (Color) color;
      setBackground(newColor);
      if (isBordered) {
        if (isSelected) {
          if (selectedBorder == null) {
            selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                table.getSelectionBackground());
          }
          setBorder(selectedBorder);
        }
        else {
          if (unselectedBorder == null) {
            unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                table.getBackground());
          }
          setBorder(unselectedBorder);
        }
      }

      setToolTipText("RGB value: " + newColor.getRed() + ", "
                     + newColor.getGreen() + ", "
                     + newColor.getBlue());
      return this;
    }
  }

  protected static class sRGBRenderer
      extends JLabel implements TableCellRenderer {
    Border border = null;
    Border noborder = null;

    public sRGBRenderer() {
      setOpaque(true); //MUST do this for background to show up.
      noborder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                                 Color.white);
      border = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                               Color.black);
    }

    public Component getTableCellRendererComponent(
        JTable table, Object color,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
      RGB rgb = (RGB) color;
      boolean doration = rgb.rationalize();
      Color c = rgb.getColor();

      setBackground(c);
      String tip = null;
      if (doration) {

        setBorder(border);
        tip = "sRGB value: " + c.getRed() + ", " + c.getGreen() + ", " +
            c.getBlue() + " (out of gamut)";

      }
      else {
        setBorder(noborder);
        tip = "sRGB value: " + c.getRed() + ", " + c.getGreen() + ", " +
            c.getBlue();
      }

      setToolTipText(tip);
      return this;
    }
  }

  public void setPatchList(List patchList) {
    this.patchList = patchList;
    this.whitePatch = Patch.Filter.whitePatch(patchList);
    fireTableDataChanged();
  }
}
