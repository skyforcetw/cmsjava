package shu.cms.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.jdesktop.swinghelper.layer.*;
import org.jdesktop.swinghelper.layer.painter.*;
import org.jdesktop.swinghelper.layer.painter.model.*;

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
public class TabbedAnimatingChangeListener
    implements ChangeListener {
  private int index;
  private Timer timer;
  private ComponentPainter<JComponent> painter;
  private float delta;

  public TabbedAnimatingChangeListener() {
    this(100, .1f);
  }

  public TabbedAnimatingChangeListener(int delay, final float delta) {
    this.delta = delta;
    painter = new AnimationPainter();

    timer = new Timer(delay, new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        painter.update();
        PainterModel model = painter.getModel();

        if (model.getAlpha() <= delta) {
          model.setAlpha(0);
          timer.stop();
          return;
        }

        model.setAlpha(model.getAlpha() - delta);
      }
    });
  }

  public float getDelta() {
    return delta;
  }

  public void setDelta(float delta) {
    if (delta <= 0 || delta > 1) {
      throw new IllegalArgumentException();
    }
    this.delta = delta;
  }

  public int getDelay() {
    return timer.getDelay();
  }

  public void setDelay(int delay) {
    timer.setDelay(delay);
  }

  public void stateChanged(ChangeEvent e) {
    JTabbedPane pane = (JTabbedPane) e.getSource();
    JXLayer<JComponent> layer =
        (JXLayer<JComponent>) pane.getSelectedComponent();
    JXLayer<JComponent> oldLayer =
        (JXLayer<JComponent>) pane.getComponentAt(index);

    PainterModel model = painter.getModel();
    model.setAlpha(1 - model.getAlpha());

    // set oldLayer as a foreground
    painter.setComponent(oldLayer);
    // swap painters
//    oldLayer.setPainter(layer.getPainter());
//    layer.setPainter(painter);

    painter.update();
    timer.start();
    index = pane.getSelectedIndex();
  }

  protected static class AnimationPainter
      extends ComponentPainter<JComponent> {
    public AnimationPainter() {
      getModel().setAlpha(0);
    }

    public void paint(Graphics2D g2, JXLayer<JComponent> l) {
      // paint the layer
      l.paint(g2);
      // paint the old layer with diminishing alpha
      super.paint(g2, l);
    }
  }
}
