package org.math.plot.components;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.math.plot.PlotPanel;
import org.math.plot.canvas.Plot3DCanvas;
import org.math.plot.canvas.PlotCanvas;

/**
 * BSD License
 *
 * @author Yann RICHET
 */

public class PlotToolBar
    extends JToolBar {

  // TODO redesign icons...

  private static final long serialVersionUID = 1L;

  protected ButtonGroup buttonGroup;

  protected JToggleButton buttonCenter;

  protected JToggleButton buttonCenter2;

  //protected JToggleButton buttonEdit;

  protected JToggleButton buttonZoom;

  protected JToggleButton buttonRotate;

  //protected JToggleButton buttonViewCoords;

  protected JButton buttonSetScales;

  protected JButton buttonDatas;

  protected JButton buttonSavePNGFile;

  protected JButton buttonCopyToClipboard;

  protected JToggleButton buttonRescales;

  protected JButton buttonReset;
  protected JButton buttonReset2;

  private boolean denySaveSecurity;

  private JFileChooser pngFileChooser;

  /** the currently selected PlotPanel */
  private PlotCanvas plotCanvas;

  private PlotPanel plotPanel;

  public PlotToolBar(PlotPanel pp) {
    plotPanel = pp;
    plotCanvas = pp.plotCanvas;

    try {
      pngFileChooser = new JFileChooser();
      pngFileChooser.setFileFilter(new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith(".png");
        }

        public String getDescription() {
          return "Portable Network Graphic file";
        }
      });
    }
    catch (AccessControlException ace) {
      denySaveSecurity = true;
    }

    buttonGroup = new ButtonGroup();

    buttonCenter = new JToggleButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/center.png")));
    buttonCenter.setToolTipText("Center axes");
    buttonCenter.setSelected(plotCanvas.ActionMode == PlotCanvas.TRANSLATION);

    buttonCenter2 = new JToggleButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/center.png")));
    buttonCenter2.setToolTipText("Center axes");
    buttonCenter2.setSelected(plotCanvas.ActionMode == PlotCanvas.CENTER);

    buttonZoom = new JToggleButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/zoom.png")));
    buttonZoom.setToolTipText("Zoom");
    buttonZoom.setSelected(plotCanvas.ActionMode == PlotCanvas.ZOOM);

    //buttonEdit = new JToggleButton(new ImageIcon(PlotPanel.class.getResource("icons/edit.png")));
    //buttonEdit.setToolTipText("Edit mode");

    //buttonViewCoords = new JToggleButton(new ImageIcon(PlotPanel.class.getResource("icons/position.png")));
    //buttonViewCoords.setToolTipText("Highlight coordinates / Highlight plot");

    buttonSetScales = new JButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/scale.png")));
    buttonSetScales.setToolTipText("Set scales");

    buttonDatas = new JButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/data.png")));
    buttonDatas.setToolTipText("Get datas");

    buttonCopyToClipboard = new JButton(new ImageIcon(PlotPanel.class.
        getResource(
            "icons/toclipboard.png")));
    buttonCopyToClipboard.setToolTipText("Copy graphics to clipboard");

    buttonRescales = new JToggleButton(new ImageIcon(PlotPanel.class.
        getResource(
            "icons/position.png")));
    buttonRescales.setToolTipText("Rescales");
    buttonRescales.setSelected(plotCanvas.ActionMode == PlotCanvas.RESCALES);

    buttonSavePNGFile = new JButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/topngfile.png")));
    buttonSavePNGFile.setToolTipText("Save graphics in a .PNG File");

    buttonReset = new JButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/back.png")));
    buttonReset.setToolTipText("Reset zoom & axes");
    buttonReset2 = new JButton(new ImageIcon(PlotPanel.class.getResource(
        "icons/back.png")));
    buttonReset2.setToolTipText("Reset zoom & axes");

    /*buttonEdit.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
      plotCanvas.ActionMode = PlotCanvas.EDIT;
     }
       });*/

    buttonZoom.setSelected(true);
    buttonZoom.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.ActionMode = PlotCanvas.ZOOM;
      }
    });

    buttonCenter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.ActionMode = PlotCanvas.TRANSLATION;
      }
    });

    buttonCenter2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.ActionMode = PlotCanvas.CENTER;
      }
    });

    /*buttonViewCoords.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
      plotCanvas.setNoteCoords(buttonViewCoords.isSelected());
     }
       });*/

    buttonSetScales.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.displaySetScalesFrame();
      }
    });

    buttonDatas.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.displayDatasFrame();
      }
    });

    buttonSavePNGFile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        choosePNGFile();
      }
    });

    buttonCopyToClipboard.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copyToClipboard();
      }
    });

    buttonRescales.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.ActionMode = PlotCanvas.RESCALES;
      }
    });

    buttonReset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.resetBase();
      }
    });

    buttonReset2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        plotCanvas.reset();
      }
    });

    buttonGroup.add(buttonCenter);
    buttonGroup.add(buttonCenter2);
    buttonGroup.add(buttonZoom);
    //buttonGroup.add(buttonEdit);
    buttonGroup.add(buttonRescales);

    if (plotCanvas instanceof Plot3DCanvas) {
      add(buttonCenter, null);
    }
    else {
      add(buttonCenter2, null);
    }

    add(buttonZoom, null);
    if (plotCanvas instanceof Plot3DCanvas) {
      add(buttonReset, null);
    }
    else {
      add(buttonReset2, null);
    }

    add(buttonRescales, null);

    //add(buttonViewCoords, null);
    add(buttonSetScales, null);
    //add(buttonEdit, null);
    add(buttonSavePNGFile, null);
    add(buttonCopyToClipboard, null);
    add(buttonDatas, null);

    if (!denySaveSecurity) {
      pngFileChooser.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveGraphicFile();
        }
      });
    }
    else {
      buttonSavePNGFile.setEnabled(false);
    }

    //buttonEdit.setEnabled(plotCanvas.getEditable());

    //buttonViewCoords.setEnabled(plotCanvas.getNotable());

    // allow mixed (2D/3D) plots managed by one toolbar
    if (plotCanvas instanceof Plot3DCanvas) {
      if (buttonRotate == null) {
        buttonRotate = new JToggleButton(new ImageIcon(PlotPanel.class.
            getResource("icons/rotation.png")));
        buttonRotate.setToolTipText("Rotate axes");

        buttonRotate.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            plotCanvas.ActionMode = Plot3DCanvas.ROTATION;
          }
        });
        buttonGroup.add(buttonRotate);
        add(buttonRotate, null, 2);
        buttonRotate.setSelected(plotCanvas.ActionMode == Plot3DCanvas.ROTATION);
      }
      else {
        buttonRotate.setEnabled(true);
      }
    }
    else {
      if (buttonRotate != null) {
        // no removal/disabling just disable
        if (plotCanvas.ActionMode == Plot3DCanvas.ROTATION) {
          plotCanvas.ActionMode = PlotCanvas.ZOOM;
        }
        buttonRotate.setEnabled(false);
      }
    }
  }

  void choosePNGFile() {
    pngFileChooser.showSaveDialog(this);
  }

  void copyToClipboard() {
    plotPanel.toClipboard();
  }

  void saveGraphicFile() {
    java.io.File file = pngFileChooser.getSelectedFile();
    try {
      plotPanel.toGraphicFile(file);
    }
    catch (IOException e) {
      JOptionPane.showConfirmDialog(null, "Save failed : " + e.getMessage(),
                                    "Error", JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.ERROR_MESSAGE);
    }
  }
}
