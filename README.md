# PinBerry – Final Project for Object-Oriented Programming

This repository contains the complete development of **PinBerry**, a multimedia gallery application built in Java using Swing for the graphical user interface. Throughout the project, we produced and refined several deliverables that document both the system design and its implementation, following core principles of **Object-Oriented Programming (OOP)** and the **SOLID** design principles.

---

## 📚 Deliverables

### 1. 📝 Academic Paper (IEEE Format)
We wrote a paper in IEEE format that documents:
- The problem context and design motivation
- Design decisions and justification
- Application of SOLID principles
- Results obtained during implementation
- Conclusions and future work

The paper reflects the theoretical framework behind our system architecture and shows how our decisions evolved as the project progressed.

---

### 2. 📄 Technical Report
The technical report includes:
- Detailed documentation of classes, methods, and relationships
- UML class and sequence diagrams
- Tables of user stories
- Application of object-oriented design patterns (inheritance, polymorphism, aggregation, composition)
- Clear connection between system behavior and the OOP/SOLID principles

---

### 3. 🖼️ Academic Poster
We designed a one-page visual poster summarizing the project, featuring:
- The application’s objective and motivation
- System architecture overview
- Key screenshots of the interface
- Tools and technologies used
- Highlights of the OOP and SOLID principles applied

---

### 4. 📊 Presentation Slides
Our presentation slides guided the oral defense and included:
- Motivation and problem statement
- Functional and technical design
- Architectural diagram and class overview
- Screenshots of the working application
- Key conclusions and future improvements

---

### 5. 🎞️ Demo Video
A short video demo was recorded to show the application in action.  
The demo includes:
- Launching the app and logging in
- Uploading and viewing images
- Creating and deleting folders
- Visual interaction with the GUI

The application already contains uploaded images for demonstration purposes.

**Test users:**
- Username: `1`, Password: `1`
- Username: `2`, Password: `2`

---

### 6. 💻 Graphical User Interface (Java Swing)
The entire GUI was implemented in the `App.java` class using **Java Swing**.  
We structured the code to maintain separation of concerns as much as possible:

- `App` coordinates GUI actions and controls application flow
- `User`, `Folder`, and `Multimedia` (interface) manage core logic
- Object creation and storage actions are delegated to the appropriate classes

---

## 🧠 Application of SOLID Principles

- **SRP (Single Responsibility):** Each class handles one purpose. For example, `Folder` stores and retrieves media, while `User` handles user-specific actions.
- **OCP (Open–Closed):** Adding new media types (e.g., `Gif`, `Video`) did not require changes to existing classes due to the use of interfaces.
- **LSP (Liskov Substitution):** All media types can be used interchangeably through the `Multimedia` interface.
- **ISP (Interface Segregation):** The `Multimedia` interface contains only the methods required for all media types.
- **DIP (Dependency Inversion):** The system avoids strong coupling through clean abstraction and well-defined responsibilities.

---

## 🚀 How to Run

1. Make sure you have Java installed.
2. Open the project in your preferred IDE (e.g., IntelliJ or NetBeans).
3. Run the `App.java` class.
4. Use the following test users to log in and explore:
   - `Username: 1` | `Password: 1`
   - `Username: 2` | `Password: 2`

---

## 🙏 Acknowledgments

We would like to express our sincere gratitude to the people (and beings) who supported us throughout this project:

- **Cristian**, for being the constant support and encouragement behind Ian's focus.
- **Mia**, Herrera’s cat, for her comforting presence and company during late coding nights.
- **Mallerly**, Herrera’s mother, for her love and endless motivation.
- **Twice**, whose music served as the project's official soundtrack and whose images were respectfully used as sample content for the app interface.

This project was not just a software exercise—it was built with dedication, support, and heart.

---

## 👨‍💻 Developers

- Juan Sebastian Herrera Rodriguez - 20242020032
- Isabela Chica Becerra - 20242020035

---

## 📌 Final Note

PinBerry is a full-stack educational project that demonstrates how to apply Object-Oriented Programming principles in practice. It blends software design, user experience, and teamwork into a working application that serves as a foundation for future extensions and improvements.
