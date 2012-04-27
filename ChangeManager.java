package LIMBS;

import java.util.Stack;

public class ChangeManager {
  
 Stack<Change> undoStack = new Stack<Change>();
 Stack<Change> redoStack = new Stack<Change>();
 Stack<ChangeNotification> undoNotifyStack = new Stack<ChangeNotification>();
 Stack<ChangeNotification> redoNotifyStack = new Stack<ChangeNotification>();
 
 int numberOfChangesSinceLastMarker = 0;
 
 public ChangeManager() { }
 
 public void addChange( Change c ) {
   numberOfChangesSinceLastMarker++;
   undoStack.push(c);
   redoStack.clear();
   redoNotifyStack.clear();
 }
 
 public void addChangeNotifier(ChangeNotification c){
   undoNotifyStack.push(c);
 }
 
 public void addEndChangeMarker(){
   
   if(numberOfChangesSinceLastMarker == 0) return;
   numberOfChangesSinceLastMarker = 0;
   
   undoStack.push(endChange);
   undoNotifyStack.push(endChange);
 }
 
 public void undo() {
   
   //add the endChange marker to redo if we can undo
   if(!undoStack.empty()){
     redoStack.push(endChange);
     redoNotifyStack.push(endChange);
   }
   
   //keep undoing until the undo stack is empty, or we hit a stop action
   while(!undoStack.empty()){
     
     //get the current change
     Change currentChange = undoStack.pop();
     
     //if it is a stop action, break the loop
     if(currentChange == endChange) break;
     currentChange.undo();
     
     //add the change to the redo stack
     redoStack.push(currentChange);
     
   }
   
   //find everyone to notify
   while(!undoNotifyStack.empty()){
     ChangeNotification currentNotification = undoNotifyStack.pop();
     if(currentNotification == endChange) break;
     currentNotification.notifyChange();
     redoNotifyStack.push(currentNotification);
   }
   
 }
 
 public void redo() {
   
   //add the endChange marker to redo if we can undo
   if(!redoStack.empty()){
     undoStack.push(endChange);
     undoNotifyStack.push(endChange);
   }
     
   //keep undoing until the undo stack is empty, or we hit a stop action
   while(!redoStack.empty()){
     
     //get the current change
     Change currentChange = redoStack.pop();
     
     //if it is a stop action, break the loop
     if(currentChange == endChange) break;
     currentChange.redo();
     
     //add the change to the redo stack
     undoStack.push(currentChange);
     
   }
   
   //find everyone to notify
   while(!redoNotifyStack.empty()){
     ChangeNotification currentNotification = redoNotifyStack.pop();
     if(currentNotification == endChange) break;
     currentNotification.notifyChange();
     undoNotifyStack.push(currentNotification);
   }
   
 }

 
 public boolean canUndo() {
   return !undoStack.empty();
 }
 
 public boolean canRedo() {
   return !redoStack.empty();
 }
 
 //use a single endchange marker for each undo manager
 private EndChange endChange = new EndChange();
 
 private class EndChange implements Change, ChangeNotification{
   public boolean undo() { return false; };
   public boolean redo() { return false; };
   public boolean notifyChange() { return false; };
 }
}
