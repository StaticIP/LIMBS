package LIMBS;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

//An internal class which handles the proposition list items (which have a checkbox, and
//valence field)
@SuppressWarnings("serial")
public class PropositionListItem extends JPanel implements ActionListener {
    
    JCheckBox name;
    JTextField valence;
    Double value;
    JButton plus, minus;    
    DecimalFormat newFormat = new DecimalFormat("#.#");
    
    public PropositionListItem(String n) {
        super();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        //Initialize and add all of the required components
        name = new JCheckBox(n);
        valence = new JTextField(4);
        value = 0.0;
        value =  Double.valueOf(newFormat.format(value));
        valence.setText(String.valueOf(value));
        
        plus = new JButton("+");
        plus.setMargin(new Insets(0,0,0,0));
        plus.setActionCommand("plus");
        plus.addActionListener(this);
        
        minus = new JButton("-");
        minus.setMargin(new Insets(0,0,0,0));
        minus.setActionCommand("minus");
        minus.addActionListener(this);
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        this.add(name, c);
        
        JPanel valencePanel = new JPanel(new GridLayout(1,3));
        valencePanel.setPreferredSize(new Dimension(110,25));
        valencePanel.add(minus);
        valencePanel.add(valence);
        valencePanel.add(plus);
        c.gridx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        this.add(valencePanel, c);
        
       
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if( command.equals("plus")) {
            if( value < 1.0 ) {
                value += 0.1;
                value =  Double.valueOf(newFormat.format(value));
                valence.setText(String.valueOf(value));
            }
        } else if ( command.equals("minus")) {
            if( value > -1.0 ) {
                value -= 0.1;
                value =  Double.valueOf(newFormat.format(value));
                valence.setText(String.valueOf(value));
            }
        }
    }
    
}
