package shu.cms.applet.measure.tool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import shu.cms.measure.meter.*;
import shu.ui.*;

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
public class InstrumentConfigurationFrame extends JInternalFrame {
//    extends JFrame {
    protected GridBagLayout gridBagLayout1 = new GridBagLayout();
    protected JComboBox jCombo_Instr = new JComboBox();
    protected JCheckBox jCheckBox_Spectral = new JCheckBox();
    protected JLabel jLabel_Instr = new JLabel();
    protected JLabel jLabel_FoundInstr = new JLabel();
    protected JProgressBar jProgressBar1 = new JProgressBar();
//  public Meter meter = new DummyMeter();
    public Meter meter;

    public static void main(String[] args) {
        new InstrumentConfigurationFrame().setVisible(true);
    }

    public InstrumentConfigurationFrame() {
        super("Instrument Configuration", false, true, false, false);
        try {
            setDefaultCloseOperation(this.HIDE_ON_CLOSE);
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected Meter getMeter(String reminder) {
        if (reminder.indexOf(Instrument.EyeOneDisplay.name) != -1) {
            try {
                return new EyeOneDisplay2(Meter.ScreenType.LCD);
            } catch (Exception ex) {
                return null;
            } catch (Error ex) {
                return null;
            }
        } else if (reminder.indexOf(Instrument.EyeOnePro.name) != -1) {
            try {
                return new EyeOnePro(Meter.ScreenType.LCD);
            } catch (Exception ex) {
                return null;
            } catch (Error ex) {
                return null;
            }
        } else if (reminder.indexOf(Instrument.ArgyllDispread.name) != -1) {
            return new ArgyllDispMeter();
        } else if (reminder.indexOf(Instrument.ArgyllSpotread.name) != -1) {
            return new ArgyllSpotMeter();
        } else if (reminder.indexOf(Instrument.CA210.name) != -1) {
            try {
                return new CA210();
            } catch (Exception ex) {
                return null;
            }
        } else if (reminder.indexOf(Instrument.Dummy.name) != -1) {
            return new DummyMeter();
        } else if (reminder.indexOf(Instrument.Remote.name) != -1) {
            try {
                return RemoteMeter.getDefaultInstance();
            } catch (IllegalStateException ex) {
                return null;
            }

        }
        return null;
    }

    public static enum Instrument {
        EyeOneDisplay("Eye-One Display (LCD)"), EyeOnePro("Eye-One Pro (LCD)"),
        ArgyllDispread("Argyll (dispread)"),
        ArgyllSpotread("Argyll (spotread) RECOMMEND!"), CA210("CA-210"),
        Dummy("Dummy"), Remote("Remote Meter");

        Instrument(String name) {
            this.name = name;
        }

        String name;
    }


    private void jbInit() throws Exception {
        getContentPane().setLayout(gridBagLayout1);
        jCheckBox_Spectral.setEnabled(false);
        jCheckBox_Spectral.setToolTipText("");
        jCheckBox_Spectral.setText("Spectral");
        jCheckBox_Spectral.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jCheckBox_Spectral_actionPerformed(e);
            }
        });
        jLabel_Instr.setLabelFor(jCombo_Instr);
        jLabel_Instr.setText("Instrument");
        jLabel_FoundInstr.setLabelFor(jProgressBar1);
        jLabel_FoundInstr.setText("Not found");
        jCombo_Instr.addItem(Instrument.EyeOneDisplay.name);
        jCombo_Instr.addItem(Instrument.EyeOnePro.name);
        jCombo_Instr.addItem(Instrument.ArgyllDispread.name);
        jCombo_Instr.addItem(Instrument.ArgyllSpotread.name);
        jCombo_Instr.addItem(Instrument.CA210.name);
        jCombo_Instr.addItem(Instrument.Remote.name);
        jCombo_Instr.addItem(Instrument.Dummy.name);
        jCombo_Instr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                jComboBox1_actionPerformed(actionEvent);
            }
        });
        jCombo_Instr.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                jCombo_Instr_mouseWheelMoved(e);
            }
        });
        this.getContentPane().add(jLabel_Instr,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        this.getContentPane().add(jCombo_Instr,
                                  new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        this.getContentPane().add(jProgressBar1,
                                  new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 20, 2, 2), 0, 0));
        this.getContentPane().add(jLabel_FoundInstr,
                                  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        this.getContentPane().add(jCheckBox_Spectral,
                                  new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 20));
    }

    protected boolean initInstr(Meter meter) {
        this.jLabel_FoundInstr.setText("Connecting ...");
        if (meter == null) {
            this.jLabel_FoundInstr.setText("Not found");
            return false;
        }
        if (!meter.isConnected()) {
            this.jLabel_FoundInstr.setText("Not found");
            return false;
        } else {
            this.jLabel_FoundInstr.setText("Found");
            return true;
        }
    }

    public boolean instrumentReady = false;
    public void jComboBox1_actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == jCombo_Instr) {
            String reminder = (String) jCombo_Instr.getSelectedItem();
            if (reminder.indexOf("Eye-One Pro") !=
                -1) {
                jCheckBox_Spectral.setEnabled(true);
            } else {
                jCheckBox_Spectral.setEnabled(false);
            }
            meter = getMeter(reminder);
            instrumentReady = initInstr(meter);
        }

    }

    public void jCombo_Instr_mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource() == this.jCombo_Instr) {
            GUIUtils.mouseWheelMoved(e, jCombo_Instr);
        }
    }

    public void jCheckBox_Spectral_actionPerformed(ActionEvent e) {

        boolean selected = jCheckBox_Spectral.isSelected();
        if (meter instanceof EyeOnePro) {
            EyeOnePro i1pro = (EyeOnePro) meter;
            i1pro.setSpectraMode(selected);
        }

    }

}
