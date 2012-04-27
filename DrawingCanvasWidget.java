package LIMBS;

import java.util.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;

public class DrawingCanvasWidget extends JComponent implements
  MouseInputListener, MouseWheelListener {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Interface interf;
  private Simulation simulation = null;
  private boolean enableMouseEvents = true;
  
  // variables needed for interaction
  private ComponentWidget currentlySelected = null;
  private Point dragStart = null, dragRelease = null;
  private ZoomAndPanManager canvasViewManager;
  
  public DrawingCanvasWidget(Interface i, Simulation s) {
    this.interf = i;
    this.simulation = s;
    this.setBorder(BorderFactory.createLineBorder(Color.black));
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.addMouseWheelListener(this);   
    
    ToolTipManager.sharedInstance().setInitialDelay(100);
    
    this.canvasViewManager = new ZoomAndPanManager(this, -20, 10, 1.2);
    zoom(1); zoom(-1);
  }
  
  public void zoom(int z) {
    Rectangle bounds = null;
    bounds = this.getBounds(bounds);
    Point middle = new Point(bounds.width / 2, bounds.height / 2 );
    canvasViewManager.zoom( z, middle);
    this.repaint();   
  }
  
  public void pan(int xDiff, int yDiff) {
    Point p1 = new Point(0,0);
    Point p2 = new Point(xDiff, yDiff);
    canvasViewManager.pan(p1, p2);
    this.repaint();
  }
  
  public void pan() {
    canvasViewManager.pan(dragStart, dragRelease);
    this.repaint();
    dragStart = dragRelease;
  }
  
  public void enableMouseEvents() {
    this.enableMouseEvents = true;
  }
  
  public void disableMouseEvents() {
    this.enableMouseEvents = false;
  }
  
  public void paint(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;
    
    // draw the background / clear old drawing
    g2D.setColor(Color.white);
    Rectangle bounds = this.getBounds();
    bounds.x = 0;
    bounds.y = 0;
    bounds.width--;
    bounds.height--;
    g2D.fill(bounds);
    
    //get the adjusted co-ordinate system
    AffineTransform adjustment = g2D.getTransform();
    adjustment.concatenate(canvasViewManager.getCoordTransform());
    AffineTransform inverseAdjustment = g2D.getTransform();
    g2D.setTransform( adjustment );
    
    // redraw everyone!        
    for (GroupWidget gr : this.simulation.getGroupWidgets()) 
      gr.drawWidget(g2D);
    for (EvidenceWidget ev : this.simulation.getEvidenceWidgets())
      ev.drawWidget(g2D);
    for (ActionWidget a : this.simulation.getActionWidgets())
      a.drawWidget(g2D);
    for (GoalWidget go : this.simulation.getGoalWidgets())
      go.drawWidget(g2D);
    for (UtilityWidget u : this.simulation.getUtilityWidgets())
      u.drawWidget(g2D);
    for (AgentWidget a : this.simulation.getAgentWidgets())
      a.drawWidget(g2D);
    
    //draw the tooltips if required
    if( LIMBSSystem.config().showNames) {
      for( ComponentWidget w : this.getComponentSet() )
        w.drawTooltip(g2D);
    }
    
    if (this.currentlySelected != null)
      this.currentlySelected.drawSelected(g2D);
    
    //un-adjust the co-ordinate system
    g2D.setTransform( inverseAdjustment );
    
    // add the outline
    g2D.setColor(Color.black);
    g2D.draw(bounds);
    
  }
  
  public void updateWidgetSizes() {
    for (EvidenceWidget ev : this.simulation.getEvidenceWidgets())
      ev.updateSize();
    for (ActionWidget a : this.simulation.getActionWidgets())
      a.updateSize();
    for (GoalWidget go : this.simulation.getGoalWidgets())
      go.updateSize();
    for (AgentWidget a : this.simulation.getAgentWidgets())
      a.updateSize();
    for (UtilityWidget u : this.simulation.getUtilityWidgets())
      u.updateSize();
    
    repaint();
  }
  
  public void newComponent( String type ){
    if( !ComponentWidgetFactory.isValidType( type ) ) return;
    
    simulation.registerEndChange(new ChangeNotification() {
      public boolean notifyChange() {
        repaint();
        return true;
      }
    });
    
    ComponentWidget newCW = ComponentWidgetFactory.getNewWidget( simulation, type );
    AffineTransform panner = canvasViewManager.getCoordTransform();
    Point2D startPoint = new Point2D.Double(panner.getScaleX() * newCW.getWidth() / 2.0,
                                            panner.getScaleY() * newCW.getWidth() / 2.0 );
    try{ panner.inverseTransform(startPoint, startPoint); } catch( Exception e ){};
    newCW.setCentre( startPoint.getX(), startPoint.getY() );
    this.currentlySelected = newCW;
    interf.selectComponent(newCW.getComponent());
    interf.selectComponentWidget(newCW);
    updateComponentConnections();
    
    repaint();
  }
  
  public void copyComponent(ComponentWidget cw) {
    this.currentlySelected = cw.copy();
    this.interf.selectComponent(this.currentlySelected.getComponent());
    this.interf.selectComponentWidget(this.currentlySelected);
    repaint();
  }
  
  public void removeComponent(ComponentWidget cw) {
    
    cw.delete();
    this.currentlySelected = null;
    repaint();
    
  }
  
  public void mouseClicked(MouseEvent e) {
    
    Point2D.Float p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    try{
      p2 = canvasViewManager.transformPoint( e.getPoint() );
    }catch( NoninvertibleTransformException exception ){
      p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    }
    
    // if we are within bounds, find the clicked component
    this.currentlySelected = checkAgents( (int) p2.getX(), (int) p2.getY() );
    if (this.currentlySelected != null) {
      if (e.getClickCount() == 2)
        this.interf
        .addAgentInterfaceTab( (Agent) this.currentlySelected.getComponent());
    }
    
    this.currentlySelected = checkUtilities( (int) p2.getX(), (int) p2.getY() );
    if (this.currentlySelected != null) {
      if (e.getClickCount() == 2)
        this.interf
        .addAgentInterfaceTab( (Agent) this.currentlySelected.getComponent());
    }
    
    if (this.currentlySelected == null)
      this.currentlySelected = checkActions( (int) p2.getX(), (int) p2.getY() );
    if (this.currentlySelected == null)
      this.currentlySelected = checkEvidence( (int) p2.getX(), (int) p2.getY() );
    if (this.currentlySelected == null)
      this.currentlySelected = checkGoals( (int) p2.getX(), (int) p2.getY() );
    if (this.currentlySelected == null)
      this.currentlySelected = checkGroups( (int) p2.getX(), (int) p2.getY() );
    
    // if we've selected a component, propagate this information
    if (this.currentlySelected != null) {
      interf.selectComponent(this.currentlySelected.getComponent());
      interf.selectComponentWidget(this.currentlySelected);
    }
    this.repaint();
  }
  
  public void mouseEntered(MouseEvent e) {
  }
  
  public void mouseExited(MouseEvent e) {
  }
  
  public void mouseMoved(MouseEvent e) {
    
    Point2D.Float p1 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    Point2D.Float p2 = null;
    try{
      p2 = canvasViewManager.transformPoint( e.getPoint() );
    }catch( NoninvertibleTransformException exception ){
      p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    }
    
    if (LIMBSSystem.config().showNames == false) {
      // if we are within bounds, find the component corresponding to
      // the current mouse position
      ComponentWidget hover = null;
      hover = checkAgents( (int) p2.getX(), (int) p2.getY() );
      if (hover == null)
        hover = checkUtilities( (int) p2.getX(), (int) p2.getY() );
      if (hover == null)
        hover = checkActions( (int) p2.getX(), (int) p2.getY() );
      if (hover == null)
        hover = checkEvidence( (int) p2.getX(), (int) p2.getY() );
      if (hover == null)
        hover = checkGoals( (int) p2.getX(), (int) p2.getY() );
      if (hover == null)
        hover = checkGroups( (int) p2.getX(), (int) p2.getY() );
      
      // set the tool tip correctly
      if (hover == null) {
        super.setToolTipText(null);
      } else {
        super.setToolTipText(hover.getComponent().getName());
      }
    }
    this.repaint();
  }
  
  public void mousePressed(MouseEvent e) {
    
    Point2D.Float p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    try{
      p2 = canvasViewManager.transformPoint( e.getPoint() );
    }catch( NoninvertibleTransformException exception ){
      p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    }
    
    if (this.enableMouseEvents) {
      // if we are within bounds, find the clicked component
      this.currentlySelected = checkAgents( (int) p2.getX(), (int) p2.getY() );
      if (this.currentlySelected == null)
        this.currentlySelected = checkUtilities( (int) p2.getX(), (int) p2.getY() );
      if (this.currentlySelected == null)
        this.currentlySelected = checkActions( (int) p2.getX(), (int) p2.getY() );
      if (this.currentlySelected == null)
        this.currentlySelected = checkEvidence( (int) p2.getX(), (int) p2.getY() );
      if (this.currentlySelected == null)
        this.currentlySelected = checkGoals( (int) p2.getX(), (int) p2.getY() );
      if (this.currentlySelected == null)
        this.currentlySelected = checkGroups( (int) p2.getX(), (int) p2.getY() );
      
      // if we've selected a component, propagate this information
      if (this.currentlySelected != null) {
        
        simulation.registerEndChange(new ChangeNotification() {
          public boolean notifyChange() {
            repaint();
            return true;
          }
        });
        
        interf.selectComponent(this.currentlySelected.getComponent());
        interf.selectComponentWidget(this.currentlySelected);
        this.currentlySelected.mousePress( (int) p2.getX(), (int) p2.getY() );
      } else {
        
        interf.selectComponent(null);
        interf.selectComponentWidget(null);
        dragStart = e.getPoint();
      }
    }
    this.repaint();
  }
  
  public void mouseReleased(MouseEvent e) {
    
    Point2D.Float p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    try{
      p2 = canvasViewManager.transformPoint( e.getPoint() );
    }catch( NoninvertibleTransformException exception ){
      p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    }
    
    if (this.enableMouseEvents) {
      // if we never had selected a component, just return
      if (this.currentlySelected == null) {
        dragStart = null;
        return;
      }
      this.currentlySelected.mouseRelease( (int) p2.getX(), (int) p2.getY() );
      updateComponentConnections();
      interf.selectComponent(this.currentlySelected.getComponent());
      interf.selectComponentWidget(this.currentlySelected);
      
    }
  }
  
  public void mouseDragged(MouseEvent e) {
    
    Point2D.Float p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    try{
      p2 = canvasViewManager.transformPoint( e.getPoint() );
    }catch( NoninvertibleTransformException exception ){
      p2 = new Point2D.Float( (float) e.getX(), (float) e.getY() );
    }
    
    if (this.enableMouseEvents) {
      if (LIMBSSystem.config().showNames == false) {
        // if we are within bounds, find the component corresponding to
        // the
        // current mouse position
        ComponentWidget hover = null;
        hover = checkAgents( (int) p2.getX(), (int) p2.getY() );
        if (hover == null)
          hover = checkUtilities( (int) p2.getX(), (int) p2.getY() );
        if (hover == null)
          hover = checkActions( (int) p2.getX(), (int) p2.getY() );
        if (hover == null)
          hover = checkEvidence( (int) p2.getX(), (int) p2.getY() );
        if (hover == null)
          hover = checkGoals( (int) p2.getX(), (int) p2.getY() );
        if (hover == null)
          hover = checkGroups( (int) p2.getX(), (int) p2.getY() );
        
        // set the tool tip correctly
        if (hover == null) {
          super.setToolTipText(null);
        } else {
          super.setToolTipText(hover.getComponent().getName());
        }
      }
      if (this.currentlySelected != null) {
        this.currentlySelected.mouseDrag( (int) p2.getX(), (int) p2.getY() );
        interf.selectComponent(this.currentlySelected.getComponent());
        interf.selectComponentWidget(this.currentlySelected);
      } else {
        if( dragStart != null ) {
          dragRelease = e.getPoint();
          this.pan();
        }
      }
      
      this.repaint();
    }
  }
  
  public void mouseWheelMoved(MouseWheelEvent e) {
    zoom(e.getWheelRotation());
  }
  
  private ComponentWidget checkAgents(int x, int y) {
    for (AgentWidget a : this.simulation.getAgentWidgets())
      if (a.pointInComponent(x, y))
      return (ComponentWidget) a;
    return null;
  }
  
  private ComponentWidget checkEvidence(int x, int y) {
    for (EvidenceWidget e : this.simulation.getEvidenceWidgets())
      if (e.pointInComponent(x, y))
      return (ComponentWidget) e;
    return null;
  }
  
  private ComponentWidget checkActions(int x, int y) {
    for (ActionWidget a : this.simulation.getActionWidgets())
      if (a.pointInComponent(x, y))
      return (ComponentWidget) a;
    return null;
  }
  
  private ComponentWidget checkGoals(int x, int y) {
    for (GoalWidget g : this.simulation.getGoalWidgets())
      if (g.pointInComponent(x, y))
      return (ComponentWidget) g;
    return null;
  }
  
  private ComponentWidget checkGroups(int x, int y) {
    for (GroupWidget g : this.simulation.getGroupWidgets())
      if (g.pointInComponent(x, y))
      return (ComponentWidget) g;
    return null;
  }
  
  private ComponentWidget checkUtilities(int x, int y) {
    for (UtilityWidget u : this.simulation.getUtilityWidgets())
      if (u.pointInComponent(x, y))
      return (ComponentWidget) u;
    return null;
  }
  
  private Set<ComponentWidget> getComponentSet() {
    Set<ComponentWidget> components = new LinkedHashSet<ComponentWidget>();
    components.addAll(this.simulation.getAgentWidgets());
    components.addAll(this.simulation.getActionWidgets());
    components.addAll(this.simulation.getUtilityWidgets());
    components.addAll(this.simulation.getEvidenceWidgets());
    components.addAll(this.simulation.getGoalWidgets());
    components.addAll(this.simulation.getGroupWidgets());
    return components;
  }
  
  public void updateComponentConnections() {
    Set<ComponentWidget> components = getComponentSet();
    for (ComponentWidget c1 : components) {
      for (ComponentWidget c2 : components) {
        if (c1.intersects(c2))
          c1.connect(c2);
      }
    }
    for (ComponentWidget c1 : components) {
      for (ComponentWidget c2 : components) {
        if (!c1.intersects(c2))
          c1.disconnect(c2);
      }
    }
  }
  
  public int nameExists(String newName) {
    int count = 0;
    Set<ComponentWidget> components = getComponentSet();
    for (ComponentWidget c: components) {
      if( (c.getName()).equals(newName) ) {
        count++;
      }
    }
    return count;
  }
  
};
