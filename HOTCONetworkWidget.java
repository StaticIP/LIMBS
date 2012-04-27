package LIMBS;

//necessary Java classes to create component
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.math.*;

public class HOTCONetworkWidget extends JLabel implements MouseListener, MouseMotionListener{
  boolean nodeHovered;  
  private HOTCOAgent model;
  
  private HOTCONode activeNode;
  private HOTCOLink hoverLink;
  
  private LinkedHashSet<HOTCONode> selectedNodes;
  
  public HOTCONetworkWidget(HOTCOAgent agent){
    model = agent;
    activeNode = null;
    nodeHovered = false;
    selectedNodes = new LinkedHashSet<HOTCONode>();
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }
  
  public void mouseExited(MouseEvent e){
    
  }
  
  public void mouseEntered(MouseEvent e){
    
  }
  
  //iterate through the list, finding the first node the click landed on
  private HOTCONode getNode(MouseEvent e) {
    Iterator mainNodeIterator = model.getNodes().iterator();
    while(mainNodeIterator.hasNext()){
      HOTCONode currentNode = (HOTCONode) mainNodeIterator.next();
      double dist = (e.getX() - currentNode.getXPosition())*(e.getX() - currentNode.getXPosition()) +
        (e.getY() - currentNode.getYPosition())*(e.getY() - currentNode.getYPosition());
      if(dist < LIMBSSystem.config().nodeSize * LIMBSSystem.config().nodeSize ){
        return currentNode;
      }
    }
    
    return null;
  }
  
  // iterate through the list, finding the first link the cursor landed on
  private HOTCOLink getLink(MouseEvent e) {
    double smallestDist = Double.MAX_VALUE;
    HOTCOLink closestLink = null;
    
    for (HOTCOLink link : model.getLinks()) {
      double xa = link.getSideOne().getXPosition();
      double ya = link.getSideOne().getYPosition();
      double xb = link.getSideTwo().getXPosition();
      double yb = link.getSideTwo().getYPosition();
      double xc = e.getX();
      double yc = e.getY();

      double dist = Math.sqrt((Math.pow(((yb-ya)*(xc-xa))-((xb-xa)*(yc-ya)),2)) /
        ((Math.pow(xb-xa,2) + Math.pow(yb-ya,2))));
      
      if (dist < 4 && dist < smallestDist) {
        smallestDist = dist;
        closestLink = link;
      }
    }
    
    return closestLink;
  }
 
  public void mouseClicked(MouseEvent e){

  }
  
  public void mousePressed(MouseEvent e){
    activeNode = null;
    activeNode = getNode(e);
    
    model.getSimulation().registerEndChange( new ChangeNotification(){
      public boolean notifyChange(){
        repaint();
        return true;
      }
    });
    
    // clear if not pressing control and not clicking amongst the selected (for dragging)
    if (!e.isControlDown() && !selectedNodes.contains(activeNode)) {
      selectedNodes.clear();
    }
    
    if (activeNode != null) {
      if (e.isControlDown() && selectedNodes.contains(activeNode)) {
        // deselect
        selectedNodes.remove(activeNode);
        activeNode = null;
      }
      else {
        selectedNodes.add(activeNode);
      }
    }
  }
  
  public void mouseReleased(MouseEvent e){
    
    // handle double-click before losing node reference
    if (e.getClickCount() == 2) {
      if (activeNode != null) {
        // modify HOTCO nodes
        HOTCONodeWidget frame = new HOTCONodeWidget(activeNode, model);
      }
      else if (hoverLink != null) {
        // modify HOTCO link
        HOTCOLinkWidget frame = new HOTCOLinkWidget(hoverLink, model);
      }
    }
    
    //release the node as held if one exists
    activeNode = null;
  }
  
  public void mouseMoved(MouseEvent e){
    mouseDragged(e); // calls same function
  }
  
  public void mouseDragged(MouseEvent e){
    
    //set the tooltip
    HOTCONode tooltipfocus = getNode(e);
    if(tooltipfocus == null){
      this.setToolTipText(null);
      nodeHovered = false;
    }else{
      this.setToolTipText(tooltipfocus.getName());
      nodeHovered = true;
    }
    
    if (activeNode == null)
      hoverLink = getLink(e);
    else
      hoverLink = null;
    
    //move the node held if it exists
    if(activeNode != null) {
      double d_x = e.getX() - activeNode.getXPosition();
      double d_y = e.getY() - activeNode.getYPosition();
      
      activeNode.setPosition( e.getX(), e.getY() );
      
      for (HOTCONode node : selectedNodes) {
        if (node != activeNode) {
          node.setPosition( node.getXPosition() + d_x, node.getYPosition() + d_y );
        }
      } // for
    } // if
  }
  
  public void paint(Graphics g){
    super.paint(g);
    Stroke originalStroke = ((Graphics2D)g).getStroke();
   
    //paint the background
    g.setColor(Color.white);
    g.fillRect(0, 0, getWidth(), getHeight());
    
    //paint the links
    for( HOTCOLink currentLink : model.getLinks() ){
      if (currentLink == hoverLink && !nodeHovered) {
        g.setColor(Color.blue);
      } else {
        g.setColor(Color.black);
      }
      
      if (currentLink.getWeight() < 0) {
        float dash = 10.0f;
        float dash1[] = {dash};
        ((Graphics2D)g).setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, 
          BasicStroke.JOIN_MITER, dash, dash1, 0.0f));
      }
      else {
        ((Graphics2D)g).setStroke(new BasicStroke((float) 3.0));
      }
      
      g.drawLine((int)(currentLink.getSideOne().getXPosition()),
                 (int)(currentLink.getSideOne().getYPosition()),
                 (int)(currentLink.getSideTwo().getXPosition()),
                 (int)(currentLink.getSideTwo().getYPosition()));
      
    }
    
    ((Graphics2D)g).setStroke(new BasicStroke((float) 3.0));
    //paint the nodes
    for( HOTCONode currentNode : model.getNodes() ){
      Polygon p = null;
      
      double radius = LIMBSSystem.config().nodeSize / 2d;
      int diameter = LIMBSSystem.config().nodeSize;
      
      //draw background emotion shape and fill with activation amount
      double sx = currentNode.getXPosition() - radius;
      double sy = currentNode.getYPosition() - radius;
      Shape emotionShape = null;
      if( currentNode.getEmotionalValence() > 0.3 ){
        emotionShape = new Ellipse2D.Double(sx-diameter*0.2,sy-diameter*0.2,
                                            diameter*1.4,diameter*1.4);
      }else if(currentNode.getEmotionalValence() > -0.3){
        emotionShape = new Rectangle2D.Double(sx-diameter*0.2,sy-diameter*0.2,
                                            diameter*1.4,diameter*1.4);
      }else{
        Polygon tempEmotionShape = new Polygon();
        tempEmotionShape.addPoint((int)sx,(int)(sy-diameter*0.2));
        tempEmotionShape.addPoint((int)(sx+diameter),(int)(sy-diameter*0.2));
        tempEmotionShape.addPoint((int)(sx+1.2*diameter),(int)(sy+diameter*0.5));
        tempEmotionShape.addPoint((int)(sx+diameter),(int)(sy+diameter*1.2));
        tempEmotionShape.addPoint((int)sx,(int)(sy+diameter*1.2));
        tempEmotionShape.addPoint((int)(sx-0.2*diameter),(int)(sy+diameter*0.5));
        emotionShape = tempEmotionShape;
      }
      
      //fill emotion shape with the activation colour
      g.setColor(Color.GRAY);
      ((Graphics2D)g).fill(emotionShape);
      
      g.setColor(Simulation.getEmotionColor(currentNode.getEmotionalValence(), 
                 currentNode.getEmotionalIntensity(), currentNode.getEmotionalPotency()));
      
      // get shape if node is associated with proposition
      if (currentNode.getBase() != null) {
        int x = (int)((currentNode.getXPosition()-radius));
        int y = (int)((currentNode.getYPosition()-radius));
        p = getPolygon(x, y, (int)currentNode.getBase().getType(), diameter);
      }
      
      // fill colour before drawing outlines
      if (p != null) {
        g.fillPolygon(p);
      } else {
        g.fillOval((int)((currentNode.getXPosition()-radius)),
                   (int)((currentNode.getYPosition()-radius)),
                   diameter, diameter);
      }
      
      // get colour for outlines
      //g.setColor(Color.black);
      if (currentNode == activeNode) {
        g.setColor(Color.red); // active node
      }
      else if (selectedNodes.contains(currentNode)) {
        g.setColor(Color.green); // selected nodes
      }
      else {
        g.setColor(Simulation.getAuraColor(currentNode.getActivation()));
      }
      
      // draw outlines
      if (p != null) {
        g.drawPolygon(p);
      } else {
        g.drawOval((int)((currentNode.getXPosition()-radius)),
                   (int)((currentNode.getYPosition()-radius)),
                   diameter, diameter);
      }
      
    }
    
    // reset stroke
    ((Graphics2D)g).setStroke(originalStroke); 
  }
  
  private Polygon getPolygon(int x, int y, int type, int span) {
    Polygon p = new Polygon();
    switch (type) {
      // evidence = triangle
      case 0: {
        p.addPoint((int)(x+span*0.1), (int)(span*0.9 + y));
        p.addPoint(x + (span/2), y);
        p.addPoint((int)(x + span*0.9), (int)(y + span*0.9));
        break;
      }
      // action = square
      case 1: {
        p.addPoint((int)(x + span*0.1), (int)(y + span*0.1));
        p.addPoint((int)(x + span*0.9), (int)(y + span*0.1));
        p.addPoint((int)(x + span*0.9), (int)(y + span*0.9));
        p.addPoint((int)(x + span*0.1), (int)(y + span*0.9));
        break;
      }
      // goal = diamond
      case 2: {
        p.addPoint(x, y + (span/2));
        p.addPoint(x + (span/2), y);
        p.addPoint(x + span, y + (span/2));
        p.addPoint(x + (span/2), y + span);
        break;
      }
    }
    return p;
  }
  
  public LinkedHashSet<HOTCONode> getSelectedNodes() {
    return selectedNodes;
  }
  
};