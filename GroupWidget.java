package LIMBS;

import java.util.LinkedList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

public class GroupWidget extends ComponentWidget {
  
  private class Point {
    public int x;
    public int y;
    
    public double distance(int px, int py){
      return Math.sqrt( (double) ((px-x)*(px-x)) + (double) ((py-y)*(py-y)) );
    }
    
    public double distance(Point other, int px, int py){
      double to = (double) ( (x-other.x) * (px-other.x) + (y-other.y) * (py-other.y) )
        / (double) ( (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y) );
      if( to < 0.0 ){
        return other.distance(px,py);
      }else if( to < 1.0 ){
        double xDist = (double) px - ((double) other.x + to * (double) (x-other.x));
        double yDist = (double) py - ((double) other.y + to * (double) (y-other.y));
        return Math.sqrt( xDist*xDist + yDist*yDist );
      }else{
        return distance(px,py);
      }
    }
  };
  
  private Group group = null;
  private LinkedList<Point> vertices = null;
  private Polygon shape = null;
  
  public GroupWidget(Group group) {
    this.group = group;
    
    //create a default shape
    vertices = new LinkedList<Point>();
    Point temp = new Point();
    temp.x = 10;
    temp.y = 10;
    vertices.add(temp);
    temp = new Point();
    temp.x = 10;
    temp.y = 260;
    vertices.add(temp);
    temp = new Point();
    temp.x = 260;
    temp.y = 260;
    vertices.add(temp);
    temp = new Point();
    temp.x = 260;
    temp.y = 10;
    vertices.add(temp);
    updateShape();
    
    this.group.getSimulation().addGroupWidget(this);
  }
  
  public void delete(){
    this.group.delete();
    this.group.getSimulation().removeGroupWidget(this);
  }
  
  public void updateSize() {} //not a configurable property
  
  public Shape getShape() {
    return this.shape;
  }
  
  public Group getGroup() {
    return this.group;
  }
  
  public void addAgent(Agent a) {
    this.group.addAgent(a);
  }
  
  public Component getComponent() {
    return this.group;
  }
  
  public void removeAgent(Agent a) {
    this.group.removeAgent(a);
  }
  
  public boolean pointInComponent(int x, int y){
    boolean result =  this.getShape().contains(x,y);
    
    for( int index = 0; index < vertices.size(); index++){
      Point vertex1 = vertices.get(index);
      Point vertex2 = vertices.get((index+1)%vertices.size());
      result = result || (vertex1.distance(vertex2,x,y) <= 10.0);
    }
    
    return result;
  }
  
  public void connect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.startsWith("LIMBS.GroupWidget")) {
      return;
    } else if (className.equals("LIMBS.AgentWidget") || className.equals("LIMBS.UtilityWidget") ) {
      this.group.addAgent((Agent) w.getComponent());
    } else {
      this.group.addProposition((Proposition) w.getComponent());
    }
  }
  
  public void disconnect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.startsWith("LIMBS.GroupWidget")) {
      return;
    } else if (className.equals("LIMBS.AgentWidget") || className.equals("LIMBS.UtilityWidget") ) {
      this.group.removeAgent((Agent) w.getComponent());
    } else {
      this.group.removeProposition((Proposition) w.getComponent());
    }
  }
  
  protected void setComponent(Component comp) {
    this.group = (Group) comp;
  }
  

  
  
  public void drawTooltip(Graphics2D g) {
    //save the original colour
    Color original = g.getColor();
    
    //find the top-right-most vertex
    Point topmost = (Point) vertices.getFirst();
    for( Point p : vertices ){
      if(p.y < topmost.y ){
        topmost = p;
      }
    }
    
    //draw the component's name
    g.setColor(Color.black);
    g.drawString(this.getName(), topmost.x + 5, topmost.y - 5);
    
    //restore the original colour
    g.setColor(original);
  }
  
  //variables used for determining movement/stretching
  int movementType = -1;
  int originalClickX = -1;
  int originalClickY = -1;
  Point draggedVertex = null;
  
  //doesn't have a particular functionality
  public void mouseMove(final int nx, final int ny){}
  
  //figure out the type of movement being executed
  public void mousePress(final int nx, final int ny){
    
    //if we are near a vertex (within say 10 pixels) choose that vertex
    for( Point p : vertices ){
      if( p.distance(nx, ny) <= 10.0 ) {
        movementType = 1;
        draggedVertex = p;
        group.getSimulation().registerChange( new Change(){
          private final Point up = draggedVertex;
          private final int ux = draggedVertex.x;
          private final int uy = draggedVertex.y;
          public boolean undo(){
            up.x = ux;
            up.y = uy;
            updateShape();
            return true; 
          }
          public boolean redo(){
            return true;
          }
        }, null);
        return;
      }
    }
    
    //if we are near an edge (within say 10 pixels) create a new pixel and use that
    for( int index = 0; index < vertices.size(); index++){
      Point vertex1 = vertices.get(index);
      Point vertex2 = vertices.get((index+1)%vertices.size());
      if( vertex1.distance(vertex2, nx, ny) > 10.0 ) continue;
      Point newVertex = new Point();
      newVertex.x = nx;
      newVertex.y = ny;
      vertices.add((index+1)%vertices.size(), newVertex);
      draggedVertex = newVertex;
      movementType = 2;
      updateShape();
      group.getSimulation().registerChange( new Change(){
        private final Point up = draggedVertex;
        public boolean undo(){
          vertices.remove(up);
          updateShape();
          return true; 
        }
        public boolean redo(){
          return true;
        }
      }, null);
      return;
    }
    
    //if we are in the middle, drag the object
    movementType = 0;
    originalClickX = nx;
    originalClickY = ny;
    group.getSimulation().registerChange( new Change(){
      private final double ux = getCentreX();
      private final double uy = getCentreY();
      public boolean undo(){
        setCentre( this.ux, this.uy );
        return true; 
      }
      public boolean redo(){
        return true;
      }
    }, null);
    
  }
  
  //carry out the movement
  public void mouseDrag(final int nx, final int ny){
    switch(movementType){
      
      //we are dragging the entire object
      case 0:
        setCentre( getCentreX() + nx - originalClickX, getCentreY() + ny - originalClickY );
        originalClickX = nx;
        originalClickY = ny;
        break;
        
        //we are dragging a single vertex
      case 1:
      case 2:
        if(draggedVertex == null) return;
        draggedVertex.x = nx;
        draggedVertex.y = ny;
        updateShape();
        break;
        
        //this is really for error handling
      default:
        break;
    }
  }
  
  public void setCentre(final double x, final double y) {
    for(Point p : vertices){
      p.x += (x - getCentreX());
      p.y += (y - getCentreY());
    }
    updateShape();
  }
  
  //send the second half of the movement event
  public void mouseRelease(final int nx, final int ny){
    switch(movementType){
      
      //we are dragging the entire object
      case 0:
        setCentre( getCentreX() + nx - originalClickX, getCentreY() + ny - originalClickY );
        originalClickX = -1;
        originalClickY = -1;
        group.getSimulation().registerChange( new Change(){
          private final double ux = getCentreX();
          private final double uy = getCentreY();
          public boolean undo(){
            return true; 
          }
          public boolean redo(){
            setCentre( this.ux, this.uy );
            return true;
          }
        }, null);
        break;
        
        //we are dragging a single vertex
      case 1:
        if(draggedVertex == null) return;
        draggedVertex.x = nx;
        draggedVertex.y = ny;
        updateShape();
        group.getSimulation().registerChange( new Change(){
          private final Point up = draggedVertex;
          private final int ux = nx;
          private final int uy = ny;
          public boolean undo(){
            return true; 
          }
          public boolean redo(){
            up.x = ux;
            up.y = uy;
            updateShape();
            return true;
          }
        }, null);
        break;
        
        //we have created a new vertex
      case 2:
        if(draggedVertex == null) return;
        draggedVertex.x = nx;
        draggedVertex.y = ny;
        updateShape();
        group.getSimulation().registerChange( new Change(){
          private final Point up = draggedVertex;
          private final int index = vertices.indexOf(up);
          public boolean undo(){
            return true; 
          }
          public boolean redo(){
            vertices.add(index, up);
            updateShape();
            return true;
          }
        }, null);
        break;
        
        //this is really for error handling
      default:
        break;
    }
    movementType = -1;
    draggedVertex = null;
  }
  
  private void updateShape(){
    shape = new Polygon();
    for(Point p : vertices)
      shape.addPoint( p.x, p.y );
    
  }
  
  public ComponentWidget copy(){
    Group copiedGroup = (Group) this.group.copy();
    GroupWidget clone = new GroupWidget(copiedGroup);
    clone.vertices.clear();
    clone.vertices.addAll(vertices);
    clone.updateShape();
    return (ComponentWidget) clone;
  }
  
  private static Color backgroundColour = new Color(0.1f,0.1f,0.1f,0.1f);
  public Color getBackgroundColour(){
    return backgroundColour;
  }
  
};
