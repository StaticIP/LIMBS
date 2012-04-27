package LIMBS;

import java.awt.*;
import java.awt.geom.Rectangle2D;

abstract class ComponentWidget {
  
  abstract public void setCentre(double x, double y);
  abstract public void updateSize();
  abstract public void connect(ComponentWidget w);
  abstract public void disconnect(ComponentWidget w);
  abstract public Component getComponent();
  abstract public Shape getShape();
  abstract protected void setComponent(Component c);
  abstract public void delete();

  
  public final String getName(){
    return this.getComponent().getName();
  }
  
  public final void setName(String newName){
    this.getComponent().setName(newName);
  }
  
  public boolean pointInComponent(float x, float y){
    return this.getShape().contains(x,y);
  }
  
  public double getCentreX(){
    return this.getShape().getBounds2D().getCenterX();
  }
 
  public double getCentreY(){
    return this.getShape().getBounds2D().getCenterY();
  }
  
  public final double getWidth(){
    return this.getShape().getBounds2D().getWidth();
  }
  
  public final double getHeight(){
    return this.getShape().getBounds2D().getHeight();
  }
  
  public final boolean intersects(ComponentWidget widget){
    return this.getShape().intersects( widget.getShape().getBounds2D() )
        && widget.getShape().intersects( this.getShape().getBounds2D() );
  }
  
  abstract public Color getBackgroundColour();
   
  public void drawWidget(Graphics2D g){
   
   //save the original colour
   Color original = g.getColor();
   
   //fill the component with the preferred background colour
   g.setColor(getBackgroundColour());
   g.fill(getShape());
   
   //draw the outline around the component
   g.setColor(Color.black);
   g.draw(getShape());
   
   //restore the original colour
   g.setColor(original);
   
  }
  
  public void drawTooltip(Graphics2D g) {
 //save the original colour
   Color original = g.getColor();
   
   //draw the component's name
   g.setColor(Color.black);
   g.drawString(this.getName(), (int)(this.getCentreX() + this.getWidth()/2 + 1), (int)(this.getCentreY() - this.getHeight()/2));
   
   //restore the original colour
   g.setColor(original);
  }
  
  public void drawSelected(Graphics2D g) {
 //save the original colour
    Color original = g.getColor();
    
    //draw the component's name
    g.setColor(Color.black);
    g.fillOval( (int)(this.getCentreX() - 3), (int)(this.getCentreY() - (this.getHeight()/2) - 7), 7,7);
    //restore the original colour
    g.setColor(original);
  }
  
  public final Rectangle2D getBoundingBox(){
    return this.getShape().getBounds2D();
  }
  
  abstract public ComponentWidget copy();
  
  abstract public void mouseMove(int nx, int ny);
  abstract public void mousePress(int nx, int ny);
  abstract public void mouseDrag(int nx, int ny);
  abstract public void mouseRelease(int nx, int ny);
  
};