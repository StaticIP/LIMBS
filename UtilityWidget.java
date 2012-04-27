package LIMBS;

import java.awt.Shape;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class UtilityWidget extends ComponentWidget {

    private Utility utility = null;
    private Polygon shape = null;
    private int x = (LIMBSSystem.config().agentSize / 2) + 10;
    private int y = (LIMBSSystem.config().agentSize / 2) + 10;
    private int width = LIMBSSystem.config().agentSize;

    public UtilityWidget(Utility utility) {
        this.utility = utility;
        this.setCentre(x, y);
        this.utility.getSimulation().addUtilityWidget(this);
    }

    public void delete(){
        this.utility.delete();
        this.utility.getSimulation().removeUtilityWidget(this);
    }

    public void setCentre(final double nx, final double ny) {
        this.x = (int)nx; this.y = (int)ny;
        this.shape = new Polygon();
        this.shape.addPoint(x-width/2, (int) ( (float) y - 0.207106781f * (float) (width) ) );
        this.shape.addPoint((int) ( (float) x - 0.207106781f * (float) (width) ), y-width/2 );
        this.shape.addPoint((int) ( (float) x + 0.207106781f * (float) (width) ), y-width/2 );
        this.shape.addPoint(x+width/2, (int) ( (float) y - 0.207106781f * (float) (width) ) );
        this.shape.addPoint(x+width/2, (int) ( (float) y + 0.207106781f * (float) (width) ) );
        this.shape.addPoint((int) ( (float) x + 0.207106781f * (float) (width) ), y+width/2 );
        this.shape.addPoint((int) ( (float) x - 0.207106781f * (float) (width) ), y+width/2 );
        this.shape.addPoint(x-width/2, (int) ( (float) y + 0.207106781f * (float) (width) ) );
    }

    public void updateSize() {
        this.width = LIMBSSystem.config().agentSize;
        this.setCentre(this.x, this.y);
    }

    public Shape getShape() {
        return shape;
    }

    public Component getComponent() {
        return this.utility;
    }

    public void connect(ComponentWidget w) {
        String className = w.getClass().getName();
        if (className.equals("LIMBS.GroupWidget")) {
            this.utility.addGroup((Group) w.getComponent());
        }
    }

    public void disconnect(ComponentWidget w) {
        String className = w.getClass().getName();
        if (className.equals("LIMBS.GroupWidget")) {
            this.utility.removeGroup((Group) w.getComponent());
        }
    }

    protected void setComponent(Component comp ) {
        this.utility = (Utility) comp;
    }

    public ComponentWidget copy() {
        Utility a = (Utility) this.utility.copy();
        ComponentWidget copy = new UtilityWidget(a);
        copy.setCentre(this.getCentreX()+5, this.getCentreY()+5);
        return copy;
    }

    //doesn't have a particular functionality
    public void mouseMove(int nx, int ny){}

    //sends the first half of the movement event
    public void mousePress(int nx, int ny){
        setCentre( nx, ny );
        utility.getSimulation().registerChange( new Change(){
            private final int ux = x;
            private final int uy = y;
            public boolean undo(){
                setCentre( this.ux, this.uy );
                return true; 
            }
            public boolean redo(){
                return true;
            }
        }, null);
    }

    //drags the widget
    public void mouseDrag(int nx, int ny){
        setCentre( nx, ny );
    }

    //send the second half of the movement event
    public void mouseRelease(int nx, int ny){
        setCentre( nx, ny );
        utility.getSimulation().registerChange( new Change(){
            private final int ux = x;
            private final int uy = y;
            public boolean undo(){
                return true; 
            }
            public boolean redo(){
                setCentre( this.ux, this.uy );
                return true;
            }
        }, null);
    }

    public void scale(double factor) {
        width = (int) (width * factor);
        this.setCentre(this.x, this.y);
    }
    
    public Color getBackgroundColour(){
      return Color.magenta;
    }

};
