package LIMBS;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

public class Interface implements WindowListener {

    private JFrame mainFrame;
    private Timer interfaceUpdateTimer;

    private ClosableTabbedPane tabbedPane;
    private JPanel simPane;

    private LIMBSMenu menu;
    private ControlPanel top;
    private DetailsPane details;
    private DrawingCanvasWidget canvas;

    private HelpContents helpContents = null;
    private Preferences preferences = null;

    private final Simulation model;

    public Interface(Simulation m) {

        model = m;
        model.setInterface( this );

        tabbedPane = new ClosableTabbedPane();
        simPane = new JPanel(new BorderLayout(20, 20));

        // Create and set up the window.
        mainFrame = new JFrame("LIMBS");
        interfaceUpdateTimer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintInterface();
            }
        });
        interfaceUpdateTimer.stop();
        interfaceUpdateTimer.setRepeats(true);

        mainFrame.addWindowListener(this);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(900, 700));
        if( LIMBSSystem.config().screenSize == 2 ) {
            mainFrame.setExtendedState(mainFrame.getExtendedState() | mainFrame.MAXIMIZED_BOTH);
        }

        // setup the different components of the interface and add them
        menu = new LIMBSMenu(this, model);
        menu.setVisible(true);
        mainFrame.setJMenuBar(menu);
        top = new ControlPanel(this, model);
        top.setPreferredSize(new Dimension(1200, 100));
        simPane.add(top, BorderLayout.NORTH);

        canvas = new DrawingCanvasWidget(this, model);
        canvas.setBackground(Color.white);
        simPane.add(canvas, BorderLayout.CENTER);

        details = new DetailsPane(this, model);
        details.setPreferredSize(new Dimension(350, 300));
        simPane.add(details, BorderLayout.EAST);

        tabbedPane.add("Main", simPane);

        tabbedPane.setPreferredSize(new Dimension(800, 600));
        mainFrame.getContentPane().add(tabbedPane);

        // Display the window.
        mainFrame.pack();
        mainFrame.setVisible(true);

    }
    
    public void dispose() {
        mainFrame.dispose();
    }

    public void startSimulation() {
        interfaceUpdateTimer.start();
        top.disableControls();
        canvas.disableMouseEvents();
        menu.disableControls();
    }

    public void stopSimulation() {
        interfaceUpdateTimer.stop();
        canvas.enableMouseEvents();
        top.enableControls();
        menu.enableControls();
    }

    public void paintInterface() {
        this.mainFrame.repaint();
    }

    public void selectComponent(Component c) {
        this.details.setComponent(c);
    }

    public void selectComponentWidget(ComponentWidget cw) {
        this.menu.setComponentWidget(cw);
    }

    public void addAgentInterfaceTab(final Agent a) {
        getTabPane().addTab(a.getName(), a.getAgentLevelInterface());
    }

    public void changeAgentInterfaceTabName(final String oldName, final String newName) {

        boolean result = getTabPane().renameTab(oldName, newName);
        if ( result ) {
            // register an undo action
            model.registerChange(new Change() {
                public boolean undo() {
                    getTabPane().renameTab(newName, oldName);
                    return true;
                }

                public boolean redo() {
                    getTabPane().renameTab(oldName, newName);
                    return true;
                }
            }, null);
        }
    }

    public DetailsPane getDetails() {
        return this.details;
    }

    public ClosableTabbedPane getTabPane() {
        return this.tabbedPane;
    }

    public void openHelpContents() {
        if (helpContents == null) {
            helpContents = new HelpContents();
        } else {
            helpContents.setVisible(true);
        }
    }

    public void openPreferences() {
        if (preferences == null) {
            preferences = new Preferences(this);
        } else {
            preferences.setVisible(true);
        }
    }

    public DrawingCanvasWidget getCanvas() {
        return this.canvas;
    }

    public LIMBSMenu getMenu() {
        return this.menu;
    }

    public ControlPanel getControlPanel() {
        return this.top;
    }

    public Simulation getModel() {
        return this.model;
    }

    public void windowActivated(WindowEvent e) {
        // Invoked when the Window is set to be the active Window.
    }

    public void windowClosed(WindowEvent e) {
        // Invoked when a window has been closed as the result of calling
        // dispose on the window.
    }

    public void windowClosing(WindowEvent e) {
        // Invoked when the user attempts to close the window from the window's
        // system menu.
        // System.out.println("closing cose - windows");
        interfaceUpdateTimer.stop();
        model.pauseSimulation();
        LIMBSSystem.SystemCall().shutdown(this, this.model);
    }

    public void windowDeactivated(WindowEvent e) {
        // Invoked when a Window is no longer the active Window.
    }

    public void windowDeiconified(WindowEvent e) {
        // Invoked when a window is changed from a minimized to a normal state.
    }

    public void windowIconified(WindowEvent e) {
        // Invoked when a window is changed from a normal to a minimized state.
    }

    public void windowOpened(WindowEvent e) {
    }

    public java.awt.Component getFrame() {
        return this.mainFrame;
    }
    
    //various action listeners for certain buttons
    private class NewComponentActionListener implements ActionListener {
      private Interface i = null;
      public NewComponentActionListener( Interface d ){
        this.i = d;
      }
      public void actionPerformed(ActionEvent e) {
        i.canvas.newComponent( e.getActionCommand() );
      }
    }
    
    public ActionListener getComponentCreationActionListener(){
      return new NewComponentActionListener( this );
    }

};
