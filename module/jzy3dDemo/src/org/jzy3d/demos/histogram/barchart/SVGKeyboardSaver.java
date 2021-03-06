/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jzy3d.demos.histogram.barchart;

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import org.jzy3d.chart.Chart;

/**
 *
 * @author ao
 */
public class SVGKeyboardSaver extends KeyAdapter {

    private final Chart chart;

    public SVGKeyboardSaver(Chart chart) {
        this.chart = chart;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
            JFileChooser jfc = new JFileChooser(".");
            jfc.setFileFilter(new FileNameExtensionFilter("PNG file", new String[]{".png"}));
            jfc.showSaveDialog(e.getComponent());
            
            if(jfc.getSelectedFile()!=null){
		if(!jfc.getSelectedFile().getParentFile().exists())
			jfc.getSelectedFile().mkdirs();
                try {
                    ImageIO.write(chart.screenshot(), "png", new File(jfc.getSelectedFile().toString()+".png"));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(e.getComponent(), "Error saving file.");
                    Logger.getLogger(SVGKeyboardSaver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
