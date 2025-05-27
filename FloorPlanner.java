import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class FloorPlanner extends JFrame {
    private JPanel canvas;
    private JPanel controlPanel;
    private ArrayList<Room> rooms;
    private ArrayList<Furniture> furniture;
    private Color currentColor;
    private String currentRoomType;
    private String currentAction;
    private Room selectedRoom;
    private Furniture selectedFurniture;
    private Point dragStart;

    public FloorPlanner() {
        setTitle("2D Floor Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        rooms = new ArrayList<>();
        furniture = new ArrayList<>();
        currentColor = Color.GREEN;
        currentRoomType = "Bedroom";
        currentAction = "ADD_ROOM";

        initComponents();
    }
    private void removeSelected() {
        if (selectedRoom != null) {
            rooms.remove(selectedRoom);
            selectedRoom = null;
        } else if (selectedFurniture != null) {
            furniture.remove(selectedFurniture);
            selectedFurniture = null;
        }
        canvas.repaint();
    }

    private void initComponents() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                for (Room room : rooms) {
                    room.draw(g2d);
                }
                for (Furniture item : furniture) {
                    item.draw(g2d);
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    
        // Place this method outside the mouse listener
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelected());
        controlPanel.add(removeButton);
    
        String[] roomTypes = {"Bedroom", "Bathroom", "Kitchen", "Living Room"};
        JComboBox<String> roomTypeComboBox = new JComboBox<>(roomTypes);
        roomTypeComboBox.addActionListener(e -> {
            currentRoomType = (String) roomTypeComboBox.getSelectedItem();
            updateCurrentColor();
        });

   
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> currentAction = "ADD_ROOM");

        JButton addDoorButton = new JButton("Add Door");
        addDoorButton.addActionListener(e -> currentAction = "ADD_DOOR");

        JButton addWindowButton = new JButton("Add Window");
        addWindowButton.addActionListener(e -> currentAction = "ADD_WINDOW");

        String[] furnitureTypes = {"Bed", "Table", "Chair", "Sofa", "Commode", "Sink", "Shower", "Stove", "Refrigerator", "Dining Set"};
        JComboBox<String> furnitureComboBox = new JComboBox<>(furnitureTypes);
        JButton addFurnitureButton = new JButton("Add Furniture");
        addFurnitureButton.addActionListener(e -> {
            currentAction = "ADD_FURNITURE";
            String selectedFurniture = (String) furnitureComboBox.getSelectedItem();
            addFurniture(selectedFurniture);
        });

        JButton rotateButton = new JButton("Rotate Selected");
        rotateButton.addActionListener(e -> rotateSelected());

        JButton saveButton = new JButton("Save Floor Plan");
        saveButton.addActionListener(e -> saveFloorPlan());

        JButton loadButton = new JButton("Load Floor Plan");
        loadButton.addActionListener(e -> loadFloorPlan());

        JButton moveRoomButton = new JButton("Move Room");
        moveRoomButton.addActionListener(e -> currentAction = "MOVE_ROOM");

        JButton moveFurnitureButton = new JButton("Move Furniture");
        moveFurnitureButton.addActionListener(e -> currentAction = "MOVE_FURNITURE");


        controlPanel.add(roomTypeComboBox);
        controlPanel.add(addRoomButton);
        controlPanel.add(addDoorButton);
        controlPanel.add(addWindowButton);
        controlPanel.add(furnitureComboBox);
        controlPanel.add(addFurnitureButton);
        controlPanel.add(rotateButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(moveRoomButton);
        controlPanel.add(moveFurnitureButton);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }

    private void updateCurrentColor() {
        switch (currentRoomType) {
            case "Bedroom": currentColor = Color.GREEN; break;
            case "Bathroom": currentColor = Color.CYAN; break;
            case "Kitchen": currentColor = Color.PINK; break;
            case "Living Room": currentColor = Color.YELLOW; break;
        }
    }

    private void handleMousePressed(MouseEvent e) {
        if (currentAction.equals("ADD_ROOM")) {
            addRoom(e.getX(), e.getY());
        } else if (currentAction.equals("MOVE_FURNITURE")) {
            selectedRoom = null; // Deselect any room
            selectedFurniture = null; // Deselect any furniture
            for (Furniture item : furniture) {
                if (item.contains(e.getPoint())) {
                    selectedFurniture = item; // Select the furniture for moving
                    dragStart = e.getPoint(); // Store the drag start point
                    break;
                }
            }}
            else {
            selectedRoom = null;
            selectedFurniture = null;
            for (Room room : rooms) {
                if (room.contains(e.getPoint())) {
                    selectedRoom = room;
                    // Store original position for the selected room
                    selectedRoom.originalPosition = new Point(selectedRoom.x, selectedRoom.y); // Store original position
                                        break;
                                    }
                                }
                                for (Furniture item : furniture) {
                                    if (item.contains(e.getPoint())) {
                                        selectedFurniture = item;
                                        break;
                                    }
                                }
                                dragStart = e.getPoint();
                            }
        }
                        private void handleMouseDragged(MouseEvent e) {
                            if (selectedRoom != null) {
                                int dx = e.getX() - dragStart.x;
                                int dy = e.getY() - dragStart.y;
                        
                                // Move the room
                                selectedRoom.move(dx, dy);
                        
                                // Check for overlap with other rooms
                                if (!isValidPlacement(selectedRoom.x, selectedRoom.y, selectedRoom.width, selectedRoom.height, selectedRoom)) {
                                    // Show error message
                                    JOptionPane.showMessageDialog(this, "Cannot move room. Overlap detected with another room.");
                        
                                    // Revert to original position
                                    selectedRoom.x = selectedRoom.originalPosition.x;
                                    selectedRoom.y = selectedRoom.originalPosition.y;
                                }
                        
                                // Update dragStart to the current mouse position
                                dragStart = e.getPoint(); // Update dragStart to the new position
                            } else if (selectedFurniture != null) {
                                int dx = e.getX() - dragStart.x;
                                int dy = e.getY() - dragStart.y;
                        
                                // Move the furniture
                                selectedFurniture.move(dx, dy);
                        
                                // Check boundaries to prevent moving out of canvas
                                if (selectedFurniture.x < 0) {
                                    selectedFurniture.x = 0;
                                } else if (selectedFurniture.x + selectedFurniture.width > canvas.getWidth()) {
                                    selectedFurniture.x = canvas.getWidth() - selectedFurniture.width;
                                }
                                if (selectedFurniture.y < 0) {
                                    selectedFurniture.y = 0;
                                } else if (selectedFurniture.y + selectedFurniture.height > canvas.getHeight()) {
                                    selectedFurniture.y = canvas.getHeight() - selectedFurniture.height;
                                }
                        
                                // Update dragStart to the current mouse position
                                dragStart = e.getPoint(); // Update dragStart to the new position
                            }
                            canvas.repaint();
                        }
                    
                        
                    
                        private void handleMouseReleased(MouseEvent e) {
                            if (selectedRoom != null) {
                                if (currentAction.equals("ADD_DOOR")) {
                                    selectedRoom.addDoor(e.getPoint());
                                } else if (currentAction.equals("ADD_WINDOW")) {
                                    selectedRoom.addWindow(e.getPoint());
                                } else if (currentAction.equals("MOVE_ROOM")) {
                                    // Check for overlap when moving the room
                                    if (!isValidPlacement(selectedRoom.x, selectedRoom.y, selectedRoom.width, selectedRoom.height, selectedRoom)) {
                                        // Show error message
                                        JOptionPane.showMessageDialog(this, "Cannot move room. Overlap detected with another room.");
                                        // Revert to original position
                                        selectedRoom.x = selectedRoom.originalPosition.x;
                                        selectedRoom.y = selectedRoom.originalPosition.y;
                                    }
                                }
                            } else if (selectedFurniture != null) {
                                // You can add any action for furniture if needed, or just leave it out
                            } else {
                                // Optionally handle the case where nothing is selected
                                JOptionPane.showMessageDialog(this, "No room or furniture selected.");
                            }
                            canvas.repaint();
                        }
                    
                    private void addRoom(int x, int y) {
                        String input = JOptionPane.showInputDialog("Enter room dimensions (width,height):");
                        if (input != null && !input.isEmpty()) {
                            String[] dimensions = input.split(",");
                            if (dimensions.length == 2) {
                                try {
                                    int width = Integer.parseInt(dimensions[0].trim());
                                    int height = Integer.parseInt(dimensions[1].trim());
                                    
                                    // Check if the new room can be placed without overlapping
                                    if (isValidPlacement(x, y, width, height, null)) {
                                        rooms.add(new Room(x, y, width, height, currentColor, currentRoomType));
                                        canvas.repaint();
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Invalid placement. Room overlaps with existing rooms.");
                                    }
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers for width and height.");
                                }
                            }
                        }
                    }
                    
                        private void addFurniture(String type) {
                            int x = canvas.getWidth() / 2;
                            int y = canvas.getHeight() / 2;
                            furniture.add(new Furniture(x, y, 50, 50, type));
                            canvas.repaint();
                        }
                    
                        private boolean isValidPlacement(int x, int y, int width, int height, Room movingRoom) {
                            Rectangle newRoom = new Rectangle(x, y, width, height);
                            for (Room room : rooms) {
                                // Check for overlap only with other rooms, not the moving room itself
                                if (room != movingRoom && newRoom.intersects(room.getBounds())) {
                                    return false; // Overlap detected with another room
                                }
                            }
                            return true; // No overlap
                        }
                        private void rotateSelected() {
                            if (selectedRoom != null) {
                                selectedRoom.rotate();
                            } else if (selectedFurniture != null) {
                                selectedFurniture.rotate();
                            }
                            canvas.repaint();
                        }
                    
                        private void saveFloorPlan() {
                            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("floorplan.ser"))) {
                                oos.writeObject(rooms);
                                oos.writeObject(furniture);
                                JOptionPane.showMessageDialog(this, "Floor plan saved successfully.");
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(this, "Error saving floor plan: " + e.getMessage());
                            }
                        }
                    
                        private void loadFloorPlan() {
                            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("floorplan.ser"))) {
                                rooms = (ArrayList<Room>) ois.readObject();
                                furniture = (ArrayList<Furniture>) ois.readObject();
                                canvas.repaint();
                                JOptionPane.showMessageDialog(this, "Floor plan loaded successfully.");
                            } catch (IOException | ClassNotFoundException e) {
                                JOptionPane.showMessageDialog(this, "Error loading floor plan: " + e.getMessage());
                            }
                        }
                    
                        private static class Room implements Serializable {
                            public Point originalPosition;
                            int x, y, width, height;
        Color color;
        String type;
        ArrayList<Point> doors;
        ArrayList<Point> windows;
        double rotation;

        private boolean doesOverlap(Point p, int width, int height) {
            Rectangle newElement = new Rectangle(p.x, p.y - height, width, height);
            for (Point door : doors) {
                Rectangle doorRect = new Rectangle(door.x, door.y - 20, 5, 40); // Assuming door dimensions
                if (newElement.intersects(doorRect)) {
                    return true; // Overlap detected with an existing door
                }
            }
            for (Point window : windows) {
                Rectangle windowRect = new Rectangle(window.x, window.y - 25, 10, 25); // Assuming window dimensions
                if (newElement.intersects(windowRect)) {
                    return true; // Overlap detected with an existing window
                }
            }
            return false; // No overlap
        }

        Room(int x, int y, int width, int height, Color color, String type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.type = type;
            this.doors = new ArrayList<>();
            this.windows = new ArrayList<>();
            this.rotation = 0;
        }

        void draw(Graphics2D g2d) {
            AffineTransform oldTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(rotation), x + width / 2, y + height / 2);

            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
            g2d.drawString(type, x + 5, y + 20);

            g2d.setColor(Color.WHITE);
            for (Point door : doors) {
                g2d.drawRect(door.x, door.y -20, 5,40 );
                g2d.setColor(Color.WHITE);            
                g2d.fillRect(door.x, door.y -20, 5, 40); // Fill the rectangle with the current color
            }
                
            

            g2d.setColor(Color.BLUE);
            for (Point window : windows) {
                g2d.drawRect(window.x , window.y - 25, 10, 25);
                
                g2d.drawRect(window.x , window.y , 10, 25);
                
            }

            g2d.setTransform(oldTransform);
        }

        void move(int dx, int dy) {
            x += dx;
            y += dy;
            for (Point door : doors) {
                door.translate(dx, dy);
            }
            for (Point window : windows) {
                window.translate(dx, dy);
            }
        }

        void rotate() {
            rotation += 90;
            if (rotation >= 360) {
                rotation = 0;
            }
            int temp = width;
            width = height;
            height = temp;
        }

        void addDoor(Point p) {
    if (!doesOverlap(p, 5, 40)) { // Check for overlap before adding the door
        doors.add(p);
    } else {
        JOptionPane.showMessageDialog(null, "Cannot add door. Overlap detected with existing doors or windows.");
    }
}

void addWindow(Point p) {
    if (!doesOverlap(p, 10, 25)) { // Check for overlap before adding the window
        windows.add(p);
    } else {
        JOptionPane.showMessageDialog(null, "Cannot add window. Overlap detected with existing doors or windows.");
    }
}

        boolean contains(Point p) {
            return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    private static class Furniture implements Serializable {
        int x, y, width, height;
        String type;
        double rotation;

        Furniture(int x, int y, int width, int height, String type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = type;
            this.rotation = 0;
        }

        void draw(Graphics2D g2d) {
            AffineTransform oldTransform = g2d.getTransform();
            g2d.rotate(Math.toRadians(rotation), x + width / 2, y + height / 2);

            g2d.setColor(Color.GRAY);
            g2d.fillRect(x, y, width, height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);
            g2d.drawString(type, x + 5, y + 20);

            g2d.setTransform(oldTransform);
        }

        void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        void rotate() {
            rotation += 90;
            if (rotation >= 360) {
                rotation = 0;
            }
            int temp = width;
            width = height;
            height = temp;
        }

        boolean contains(Point p) {
            return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FloorPlanner().setVisible(true);
        });
    }
}