package LIMBS;

import java.awt.datatransfer.*;
import java.io.IOException;

// Transferable in a separate file because static fields must be publicly accessible
public class AgentTransferable implements Transferable {
  public static DataFlavor agentFlavor = new DataFlavor(Agent.class, "LIMBS Agent");
  private DataFlavor[] flavors = {agentFlavor};
  private Agent data;
  
  public AgentTransferable(Agent a) {
    data = a;
  }
  
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(flavor)) {
      throw new UnsupportedFlavorException(flavor);
    }
    
    return data;
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }
  
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(agentFlavor);
  }
}