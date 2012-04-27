package LIMBS;

import javax.swing.JPanel;
import java.awt.event.*;

@SuppressWarnings("serial")
abstract class AgentInterface extends JPanel {
 
  private Agent a = null;
  
  private ActionListener refresher =  new ActionListener() {
      public void actionPerformed( ActionEvent e ){
        refresh();
      }
    };
  
  public AgentInterface( Agent a ){
    super();
    this.setName(a.getName());
    this.a = a;
    a.addListener( refresher );
    
  }
  
  final public void noLongerVisible(){
    a.removeListener( refresher );
  }
  
  public abstract void refresh();
  
};
