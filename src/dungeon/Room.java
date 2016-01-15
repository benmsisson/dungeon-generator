package dungeon;

import java.awt.Rectangle;
import java.awt.Point;
//import java.awt.line;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

/**
 *
 * @author Ben
 */
public class Room {

    private int x, y, length, height; //x and y are top left corner, length is distance to the right, height is distance down
    private Rectangle dim;
    private Rectangle dimWide; //Length+1
    private Rectangle dimLong; //Height+1

    private ArrayList<Room> rooms;
    private ArrayList<Point> doors;
    private ArrayList<Door> newDoors;
    public ArrayList<Rectangle> rects;
    private int id = -1;
    private Random rand;
    public Color col;

    public static enum Position {

        CENTER {
                    public String toString() {
                        return " ";
                    }
                },
        LEFT {
                    public String toString() {
                        return "[";
                    }
                },
        RIGHT {
                    public String toString() {
                        return "]";
                    }
                },
        TOP {
                    public String toString() {
                        return "¯";
                    }
                },
        BOTTOM {
                    public String toString() {
                        return "_";
                    }
                },
        TOPLEFT {
                    public String toString() {
                        return "⎡";
                    }
                },
        TOPRIGHT {
                    public String toString() {
                        return "⎤";
                    }
                },
        BOTTOMLEFT {
                    public String toString() {
                        return "⎣";
                    }
                },
        BOTTOMRIGHT {
                    public String toString() {
                        return "⎦";
                    }
                },
        NIL {
                    public String toString() {
                        return "?";
                    }
                }
    };

    public Room(int x, int y, int length, int height, Random rand) {
        rooms = new ArrayList<>();
        doors = new ArrayList<>();
        rects = new ArrayList<>();
        newDoors = new ArrayList<>();
        ///doors.add(new Point(x,y));
        this.x = x;
        this.y = y;
        this.length = length;
        this.height = height;
        this.rand = rand;
        dim = new Rectangle(x, y, length, height);
        //dimPlus = new Rectangle(x-1,y-1,length+2,height+2);
        dimWide = new Rectangle(x - 1, y, length + 2, height);
        dimLong = new Rectangle(x, y - 1, length, height + 2);
        col = Color.GRAY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public boolean contains(int posX, int posY) {
        return dim.contains(new Point(posX, posY));
    }

    public boolean contains(Point p) {
        return dim.contains(p);
    }

    //public boolean containsExt()
    public boolean intersects(Room r) {
        return dim.intersects(r.dim);
    }

    public boolean tangent(Room r) {
        return dimLong.intersects(r.dim) || dimWide.intersects(r.dim);
        //return new Rectangle(x-1,y-1,length+2,height+2).intersects(r.dim);

    }

    public void addNeighbor(Room r) {
        if (!rooms.contains(r)) {
            rooms.add(r);
            r.rooms.add(this);
        }
    }

    public int getId() {
        return id;
    }

    public void kill(ArrayList<Room> toKill) {
        toKill.remove(this);
        for (Room r : rooms) {
            if (toKill.contains(r)) {
                r.kill(toKill);
            }
        }
        rooms.clear();
    }

    public void setId(int id) {
        this.id = id;
        for (Room r : rooms) {
            if (r.getId() == -1) {
                r.setId(id);
            }
        }
    }

    public void setupDoors() {
        for (Room r : rooms) {
            doorPoint(r);
        }
    }

    private void doorPoint(Room r) {
        //System.out.println("RIGHT NOW THE VISUALS ARE INCORRECT BECAUSE IT IS POSSIBLE FOR A ROOM TO HAVE MORE THAN TWO COLLISIONS. GO AHEAD AND MAKE AN ARRAYLIST OF RECTANGLES AND FILL IT UP WITH ALL THE NONEMPTY COLWIDES AND COLLONGS THIS SHOULD NOT FIX ANYTHING BECAUSE PRESUMAVLY THE DOORS SHOULD HAVE WORKED");
        //System.out.println("NOTE SOME ARE BEING OVERLAPPED BY THE EMTPY RECTANGLES AND THUS MISS A SPOT");
//Gets a point within this room that touches the other room
        //First, get the rectangles that result from the other's dimWide and dimLong onto us
        //Choose a random point within either of these rectangles.
        boolean found = false;
        Door toCheck = null;
        for (Door d : r.newDoors) {
            if (d.contains(this)) {
                found = true;
                toCheck = d;
            }
        }
        if (found) {
            //System.out.println("A");
            Point p = toCheck.getPoint1();
            Point north = new Point(p.x - 1, p.y);
            Point south = new Point(p.x + 1, p.y);
            Point east = new Point(p.x, p.y - 1);
            Point west = new Point(p.x, p.y + 1);
            if (dim.contains(north)) {
                newDoors.add(toCheck);
                toCheck.setPos(north);
            } else if (dim.contains(south)) {
                newDoors.add(toCheck);
                toCheck.setPos(south);
            } else if (dim.contains(east)) {
                newDoors.add(toCheck);
                toCheck.setPos(east);
            } else if (dim.contains(west)) {
                newDoors.add(toCheck);
                toCheck.setPos(west);
            } else {
                System.out.println("Was this misplaced?");
            }
        } else {
            Rectangle rWide = r.dimWide.intersection(dim);
            Rectangle rLong = r.dimLong.intersection(dim);
            int dX = 0, dY = 0;
            Point toAdd = new Point(-1, -1);
            if (!rWide.isEmpty()) {
                rects.add(rWide);
                dX = rand.nextInt(rWide.width) + rWide.x;
                dY = rand.nextInt(rWide.height) + rWide.y;
                toAdd = new Point(dX, dY);

            }
            if (!rLong.isEmpty()) {
                rects.add(rLong);
                dX = rand.nextInt(rLong.width) + rLong.x;
                dY = rand.nextInt(rLong.height) + rLong.y;
                toAdd = new Point(dX, dY);
            }
            doors.add(toAdd);
            newDoors.add(new Door(this, toAdd, r));
        }
        //System.out.println(rooms.contains(this));
    }

    public ArrayList<Door> doors() {
        return newDoors;
    }
    /*public int[][] toArray(){
     int[][] arr = new int[length][height];
     for (int fX = 0; fX < length; fX++) {
     for (int fY = 0; fY < height; fY++) {
                
     }
     }
     return arr;
     }*/

    public Position getPos(int posX, int posY) { //Returns from position enum
        /*if (contains(posX-1,posY)){ //Check if to the left is inside
         if (contains(posX,posY-1)){ //Check if up is inside
         if (contains(posX+1,posY)){
         if (contains(posX,posY+1)){
         return Position.CENTER;
         }
         } else {
                    
         }
         }
         } else {
            
         }
         */
        /*Point p = new Point(posX,posY); //CORRECT FOR NOW
         if (contains(posX,posY)){
         //if (contains(posX+1,posY+1)){
         return true;
         //}
         }
         for (Door d: newDoors){
         if (d.getPoint1().equals(p)){
         //    return true;
         }
         if (d.getPoint2().equals(p)){
         //    return true;
         }
         }*/
        Point p = new Point(posX, posY);
        Point north = new Point(p.x - 1, p.y);
        Point south = new Point(p.x + 1, p.y);
        Point east = new Point(p.x, p.y - 1);
        Point west = new Point(p.x, p.y + 1);
        if (contains(north)) {
            if (contains(south)) {
                if (contains(west)) {
                    if (contains(east)) {
                        return Position.CENTER;
                    }
                    else {
                        return Position.LEFT;
                    }
                } else {
                    if (contains(east)){
                        return Position.RIGHT;
                    }
                }
            }
        }
        for (Door d : newDoors) {
            if (d.getPoint1().equals(p)) {
                return Position.CENTER;
            }
            if (d.getPoint2().equals(p)) {
                return Position.CENTER;
            }
        }
        if (contains(north)) {
            if (contains(west)) {
                if (contains (east)){
                    return Position.BOTTOM;
                }
                return Position.TOPLEFT;
            } else if (contains(east)) {
                return Position.TOPRIGHT;
            } else {
                return Position.TOP;
            }
        }
        if (contains(south)) {
            if (contains(west)) {
                if (contains(east)){
                    return Position.TOP;
                }
                return Position.BOTTOMLEFT;
            } else if (contains(east)) {
                return Position.BOTTOMRIGHT;
            } else {
                return Position.TOP;
            }
        }
        return Position.NIL;
    }
}
