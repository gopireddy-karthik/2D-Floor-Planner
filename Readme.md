# 2D Floor Planner

A desktop-based interactive **2D Floor Planner** application built using Java and Swing. This tool allows users to design architectural layouts by creating customizable rooms, placing doors and windows, adding furniture, and managing spatial constraints seamlessly.

## ✨ Features

- **Dynamic Room Creation:** Add rooms (Bedroom, Bathroom, Kitchen, Living Room) with custom dimensions.
- **Overlap Detection:** Built-in collision logic to prevent rooms from overlapping during placement or movement.
- **Interactive Manipulation:** Drag and move rooms or furniture items anywhere on the canvas.
- **Component Rotation:** Rotate rooms and furniture by 90-degree increments to fit your custom layout.
- **Architectural Elements:** Add doors and windows dynamically to walls with automated boundary checking.
- **Save & Load Progress:** Uses Java Binary Serialization (`.ser`) to save your current layout configurations locally and reload them later.

## 🛠️ Tech Stack

- **Language:** Java 8+
- **GUI Framework:** Java Swing & AWT (Abstract Window Toolkit)
- **Architecture:** Object-Oriented Design (OOD) / MVC Pattern

## 🚀 Getting Started

### Prerequisites
Make sure you have the Java Development Kit (JDK) installed on your system.

🎮 How to Use
1. Add Room: Select a room type from the dropdown, click "Add Room", type the dimensions (e.g., 300,200), and click anywhere on the white canvas.
2. Move Elements: Click "Move Room" or "Move Furniture" and drag the respective items across the workspace.
3. Add Infrastructure: Select "Add Door" or "Add Window" and click on a room edge to place them.
4. Manage Layout: Use the "Rotate Selected" or "Remove Selected" buttons to modify existing elements.
5. Persistence: Use "Save Floor Plan" to cache your work locally and "Load Floor Plan" to resume your session.
