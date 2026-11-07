# Java Clinic Patient Queue Management System
## Development Checklist and Implementation Roadmap

**Project repository:** https://github.com/Ikie2140/Clinic-Patient-Queue-Management-System-.git

This roadmap is based on the project's described requirements. It is intended to clarify what to implement, which classes to create, which Java concepts to study, and the order in which to develop the system.

---

# 1. Project Overview

Approach this as a small clinic management application with two interfaces sharing one queue management system.

The system has four major responsibilities:

1. **Patient management** – Register patients, retrieve patient records, and manage visits.
2. **Queue management** – Issue queue numbers, organize patients, assign priorities, and call the next patient.
3. **Doctor and room management** – Monitor availability and assign patients to available doctors and rooms.
4. **Data management** – Save, retrieve, update, and delete records through CSV files.

A key design decision is to separate a **patient's permanent record** from an individual **clinic visit**. A patient may visit the clinic multiple times, so the system should not create a duplicate permanent patient record for every visit.

## 1.1 Proposed clinic workflow

1. Staff logs in.
2. The system opens the Clinic Dashboard and Patient Display.
3. Staff registers a new patient or retrieves an existing patient.
4. Staff creates a visit, recording the visit type, reason, and preferred doctor if applicable.
5. The system generates a queue ticket and assigns the visit to the appropriate queue.
6. The patient waits while the Patient Display updates.
7. Staff calls the next eligible patient.
8. The system assigns an available doctor and room.
9. The consultation takes place.
10. Staff completes the visit.
11. The system updates the visit status and releases the doctor and room.

### Important workflow rules

- Doctor availability and room availability are separate properties.
- A returning patient may request a particular doctor.
- If that doctor is unavailable, the patient should remain waiting, choose another available doctor, or arrange a future appointment.
- A patient should not begin consultation if the required doctor or room is unavailable.
- Completing a consultation should update the visit, doctor availability, and room occupancy.
- The Patient Display must read from the same backend state as the Clinic Dashboard. Do not maintain separate queue copies.

---

# 2. Recommended Class Structure

Organize the project into packages according to responsibility. You do not need to create every class immediately; this is the intended structure as the project grows.

```text
src/
├── model/
│   ├── Patient.java
│   ├── Visit.java
│   ├── Doctor.java
│   ├── Room.java
│   ├── Staff.java
│   ├── Priority.java
│   ├── VisitStatus.java
│   └── DoctorStatus.java
│
├── service/
│   ├── ClinicService.java
│   ├── QueueManager.java
│   ├── DoctorManager.java
│   ├── RoomManager.java
│   ├── AppointmentManager.java
│   └── AuthService.java
│
├── repository/
│   ├── PatientRepository.java
│   ├── VisitRepository.java
│   ├── DoctorRepository.java
│   ├── RoomRepository.java
│   └── StaffRepository.java
│
├── gui/
│   ├── LoginFrame.java
│   ├── ClinicFrame.java
│   ├── PatientFrame.java
│   ├── DashboardPanel.java
│   ├── QueuePanel.java
│   ├── DoctorPanel.java
│   ├── PatientRegistrationDialog.java
│   └── AppointmentDialog.java
│
├── util/
│   ├── CSVUtils.java
│   ├── IDGenerator.java
│   ├── InsertionSorter.java
│   └── InputValidator.java
│
└── Main.java
```

## 2.1 Class responsibilities

| Class | Responsibility |
|---|---|
| `Patient` | Stores permanent patient information. |
| `Visit` | Stores an individual clinic visit, including ticket, priority, doctor, room, and status. |
| `Doctor` | Stores doctor details and availability. |
| `Room` | Stores room number and occupancy. |
| `Staff` | Stores staff login information. |
| `QueueManager` | Handles queue insertion, removal, prioritization, and searching. |
| `DoctorManager` | Assigns doctors and updates availability. |
| `RoomManager` | Finds and assigns unoccupied rooms. |
| `AppointmentManager` | Handles scheduled appointments. |
| `ClinicService` | Coordinates managers and clinic operations. |
| `AuthService` | Handles staff authentication. |
| `PatientRepository` | Loads, saves, searches, and updates patient CSV records. |
| `VisitRepository` | Handles visit and queue-history CSV records. |

## 2.2 Patient versus Visit

Keep permanent patient information separate from visit information.

Example:

- Patient P001: John Doe, age 25.
- Visit V001: November 6, 2026, Dr. Santos, Room 1.
- Visit V002: November 15, 2026, Dr. Reyes, Room 2.

Both visits belong to the same patient. This preserves patient history without duplicating permanent records.

A queue entry should reference a `Visit`, not just a `Patient`. The same person may have multiple visits over time.

---

# 3. Backend Design: Required Collections

The required collections should each have a clear responsibility.

## 3.1 LinkedList: Master patient records

```java
LinkedList<Patient> patients = new LinkedList<>();
```

Purpose:

- Store all registered patients.
- Maintain a master collection of patient records.
- Perform linear searches by patient ID or name.
- Support adding, updating, and removing patient records.

Example linear search:

```java
public Patient searchPatient(String id) {
    for (Patient p : patients) {
        if (p.getPatientID().equals(id)) {
            return p;
        }
    }
    return null;
}
```

Do not use this as the active queue. A permanent patient record and a waiting-line entry are different things.

## 3.2 PriorityQueue: Urgent patient management

```java
PriorityQueue<Visit> priorityQueue =
    new PriorityQueue<>(
        Comparator
            .comparingInt((Visit v) -> v.getPriority().getValue())
            .thenComparing(Visit::getArrivalTime)
    );
```

Suggested priority levels:

| Priority | Meaning | Value |
|---|---|---:|
| `EMERGENCY` | Life-threatening | 1 |
| `URGENT` | Requires prompt attention | 2 |
| `REGULAR` | Standard consultation | 3 |

Lower values are served first. Visits with the same priority are served in arrival order.

### Preventing starvation

If emergency patients continuously arrive, regular patients could wait indefinitely. Consider a fairness rule, such as serving a regular patient after a configurable number of higher-priority consultations, provided no emergency case is waiting.

The precise rule should be documented and tested. Medical urgency must not be overridden by a fairness mechanism when an emergency is waiting.

## 3.3 Queue: Regular patient management

```java
Queue<Visit> regularQueue = new ArrayDeque<>();
```

Purpose:

- Store regular walk-in patients.
- Follow First-In, First-Out (FIFO).
- Add patients using `offer()`.
- Retrieve patients using `poll()`.
- View the next patient using `peek()`.

Suggested collection responsibilities:

| Collection | Purpose |
|---|---|
| `LinkedList<Patient>` | Permanent patient registry |
| `PriorityQueue<Visit>` | Urgent and priority-based visits |
| `Queue<Visit>` / `ArrayDeque` | Regular FIFO visits |

Your `QueueManager` should decide which collection to retrieve a patient from when staff clicks **Next Patient**.

## 3.4 Additional collections

```java
ArrayList<Doctor> doctors = new ArrayList<>();
ArrayList<Room> rooms = new ArrayList<>();
LinkedList<Visit> visitHistory = new LinkedList<>();
```

These can be used for iteration, searching, and displaying records.

---

# 4. Algorithms to Implement

Assign each algorithm a specific responsibility.

| Algorithm | Where to implement it | Purpose |
|---|---|---|
| Linear Search | `PatientRepository` | Find a patient by ID or name. |
| Linear Search | `RoomManager` | Find an available room. |
| Linear Search | `DoctorManager` | Find an available doctor. |
| Insertion Sort | `InsertionSorter` | Sort patient records before displaying or exporting. |
| Priority Queue | `QueueManager` | Determine consultation priority. |
| FIFO Queue | `QueueManager` | Maintain regular arrival order. |

## 4.1 Insertion Sort

Sort patient records by patient ID or registration date.

Example:

```text
Before:
P005
P002
P004
P001
P003

After:
P001
P002
P003
P004
P005
```

You can implement a generic or Patient-specific insertion sort.

Do not manually insertion-sort a `PriorityQueue` to determine who gets called next. Its comparator already handles priority ordering. Also, iterating over a `PriorityQueue` does not guarantee sorted output. If you need to display it in order, use a sorted copy.

## 4.2 CSV CRUD

Suggested CSV files:

| File | Stores |
|---|---|
| `patients.csv` | Permanent patient information |
| `visits.csv` | Individual visits and their statuses |
| `doctors.csv` | Doctor information and schedules |
| `rooms.csv` | Room information |
| `staff.csv` | Staff accounts |

Implement:

- **Create:** Add a new record.
- **Read:** Load records when the program starts.
- **Update:** Modify an existing record.
- **Delete:** Remove a record when appropriate.

Example patient registration process:

1. Check whether the patient already exists.
2. If new, create a `Patient` record.
3. Create a `Visit` record.
4. Add the `Visit` to the appropriate queue.
5. Save the changes to CSV.

When the program starts, load CSV files before allowing staff to manage the queue. Save changes as records are modified and ensure the latest data is saved when the application closes.

Do not delete completed visits when removing them from the active queue. Preserve them as historical records.

**CSV parsing warning:** `String.split(",")` is insufficient for general CSV parsing because quoted values can contain commas. Implement proper quoting and escaping or use a CSV library if permitted by the project requirements.

---

# 5. GUI Implementation Checklist

Implement the GUI after the core backend operations work. This avoids spending time connecting buttons to backend methods that are still changing.

## 5.1 Login window — `LoginFrame.java`

- [ ] Create username field.
- [ ] Create password field.
- [ ] Add Login button.
- [ ] Validate credentials using `AuthService`.
- [ ] Display an error for incorrect credentials.
- [ ] On successful login, open the Clinic Dashboard and Patient Display.
- [ ] Close or dispose of the login window.

## 5.2 Patient window — `PatientFrame.java`

This should be a read-only interface.

- [ ] Display current queue number.
- [ ] Display assigned doctor.
- [ ] Display assigned consultation room.
- [ ] Display patient's current queue position, where applicable.
- [ ] Display the currently called patient.
- [ ] Automatically refresh when the queue changes.
- [ ] Display clinic status (`OPEN` / `CLOSED`).
- [ ] Display a message when there are no patients waiting.

**Privacy note:** Decide whether this is a public waiting-room display or an individual patient's queue lookup. For a public display, avoid exposing private information such as full names and medical complaints.

## 5.3 Clinic Dashboard — `ClinicFrame.java`

### Dashboard tab

- [ ] Display total registered patients today.
- [ ] Display waiting patients.
- [ ] Display currently consulting patients.
- [ ] Display completed consultations.
- [ ] Display clinic operating status.
- [ ] Add Open Clinic / Close Clinic button.

### Queue Management tab

- [ ] Display all waiting patients in their actual service order.
- [ ] Display ticket number.
- [ ] Display patient ID.
- [ ] Display priority.
- [ ] Display preferred doctor.
- [ ] Display assigned room, if available.
- [ ] Add Register Patient button.
- [ ] Add Call Next Patient button.
- [ ] Add Skip Patient button.
- [ ] Add Cancel Visit button.
- [ ] Add Search Patient functionality.
- [ ] Add manual priority adjustment with authorization.

### Doctor Availability tab

- [ ] Display all registered doctors.
- [ ] Display doctor specialization.
- [ ] Display availability (`AVAILABLE` / `UNAVAILABLE` / `IN_CONSULTATION`).
- [ ] Display assigned room.
- [ ] Allow staff to change a doctor's availability.
- [ ] Allow staff to view patients assigned to each doctor.
- [ ] Prevent assignment to unavailable doctors.

### Appointment Management tab

- [ ] Register future appointments.
- [ ] Select a patient.
- [ ] Select preferred doctor.
- [ ] Select appointment date and time.
- [ ] View upcoming appointments.
- [ ] Cancel or reschedule appointments.
- [ ] Convert an appointment into an active visit when the patient arrives.

### Account / Session Management

- [ ] Add Logout button.
- [ ] Confirm logout before closing the session.
- [ ] Return to `LoginFrame`.
- [ ] Ensure the Patient Display closes or enters the appropriate waiting state.

---

# 6. Java Classes and Concepts to Learn

You do not need to study all of Java again. Concentrate on the APIs and concepts that your implementation will use.

## 6.1 Priority 1: Collections Framework

**Essential**

| Class / Interface | What to understand |
|---|---|
| `LinkedList` | Adding, removing, searching, iterating. |
| `Queue` | FIFO operations: `offer()`, `poll()`, `peek()`. |
| `PriorityQueue` | Comparators, priority ordering, insertion and removal. |
| `ArrayDeque` | FIFO implementation. |
| `ArrayList` | Managing lists of doctors and rooms. |
| `Comparator` | Custom sorting and priority rules. |
| `Iterator` | Safely traversing and removing elements. |
| `Collections` | Sorting and copying collections. |

## 6.2 Priority 2: Object-Oriented Programming

**Essential**

| Concept | Application |
|---|---|
| Encapsulation | Private fields and getters/setters. |
| Constructors | Initialize Patient, Visit, Doctor, etc. |
| Composition | A Visit references a Patient and potentially a Doctor and Room. |
| Enums | Priority, status, availability. |
| Interfaces | Repository contracts and collection interfaces. |
| Separation of concerns | Keep GUI, business logic, and storage independent. |

## 6.3 Priority 3: Java Swing

**Essential for GUI**

| Class | Application |
|---|---|
| `JFrame` | Main application windows. |
| `JPanel` | Organize GUI components. |
| `JTabbedPane` | Dashboard tabs. |
| `JTable` | Display patient queues and doctor records. |
| `JScrollPane` | Scrollable tables. |
| `JButton` | Trigger operations. |
| `JTextField` | Patient registration and search. |
| `JPasswordField` | Login. |
| `JComboBox` | Doctor, room, and priority selection. |
| `JOptionPane` | Confirmation and error messages. |
| `JDialog` | Registration and appointment forms. |
| `TableModel` / `DefaultTableModel` | Update displayed table data. |
| `SwingUtilities` | Run GUI operations on the Event Dispatch Thread. |
| `javax.swing.Timer` | Periodically refresh the patient display. |

Pay particular attention to `JTable` and its table model. Your dashboard will depend heavily on displaying and refreshing collections.

## 6.4 Priority 4: File Handling

**Essential**

| Class | Application |
|---|---|
| `File` / `Path` | Locate CSV files. |
| `Files` | Read and write files. |
| `BufferedReader` | Read CSV records. |
| `BufferedWriter` | Write CSV records. |
| `String.split()` | Basic CSV parsing. |
| `IOException` | Handle file errors. |
| `LocalDate` | Appointment dates. |
| `LocalTime` | Appointment times. |
| `LocalDateTime` | Arrival timestamps. |
| `DateTimeFormatter` | Format dates and times. |

## 6.5 Priority 5: Additional Java Concepts

- [ ] Lambda expressions.
- [ ] Method references.
- [ ] Exception handling.
- [ ] Generics.
- [ ] Event listeners (`ActionListener`).
- [ ] `equals()` and `hashCode()`.
- [ ] Basic concurrency concepts, particularly Swing's Event Dispatch Thread.

You can build on your existing experience with Java comparators and priority queues instead of relearning them from scratch.

---

# 7. Concrete Development TO-DO List

Follow this order. Each phase has a deliverable so you can tell whether it is complete.

## Phase 1: Backend Foundation

**Goal:** Have a working backend without any GUI.

- [ ] 1. Create the package structure.
- [ ] 2. Implement `Patient.java`.
- [ ] 3. Implement `Visit.java`.
- [ ] 4. Implement `Doctor.java`.
- [ ] 5. Implement `Room.java`.
- [ ] 6. Implement the Priority and Status enums.
- [ ] 7. Create sample patient, doctor, and room records.
- [ ] 8. Implement the master `LinkedList<Patient>`.
- [ ] 9. Implement `Queue<Visit>` for regular patients.
- [ ] 10. Implement `PriorityQueue<Visit>` for urgent patients.
- [ ] 11. Implement the `QueueManager`.
- [ ] 12. Implement patient registration and retrieval.
- [ ] 13. Implement queue insertion, removal, and viewing.
- [ ] 14. Implement `callNextPatient()`.
- [ ] 15. Test FIFO and priority ordering.

**Deliverable:** A console-testable backend that can register patients, create visits, and manage regular and priority queues.

## Phase 2: Doctor and Room Management

**Goal:** Ensure patients can be assigned appropriately.

- [ ] 16. Implement `DoctorManager`.
- [ ] 17. Implement `RoomManager`.
- [ ] 18. Implement linear search for available doctors.
- [ ] 19. Implement linear search for unoccupied rooms.
- [ ] 20. Implement doctor assignment.
- [ ] 21. Implement room assignment.
- [ ] 22. Implement doctor availability updates.
- [ ] 23. Implement room occupancy updates.
- [ ] 24. Prevent duplicate assignments.
- [ ] 25. Handle unavailable preferred doctors.
- [ ] 26. Release doctors and rooms after consultation.

**Deliverable:** The backend can assign and release doctors and rooms while enforcing availability rules.

## Phase 3: CSV Persistence

**Goal:** Preserve data after closing the application.

- [ ] 27. Create the CSV files.
- [ ] 28. Implement `PatientRepository`.
- [ ] 29. Implement `VisitRepository`.
- [ ] 30. Implement doctor and room repositories.
- [ ] 31. Implement CSV loading.
- [ ] 32. Implement CSV saving.
- [ ] 33. Implement patient CRUD.
- [ ] 34. Implement visit CRUD.
- [ ] 35. Implement insertion sort for patient records.
- [ ] 36. Implement linear search for patient records.
- [ ] 37. Test whether data persists after restarting the program.

**Deliverable:** The application can load, create, update, and save records using CSV files.

## Phase 4: Core Clinic Operations

**Goal:** Connect the different backend components.

- [ ] 38. Implement `ClinicService`.
- [ ] 39. Implement clinic OPEN/CLOSED status.
- [ ] 40. Implement patient check-in.
- [ ] 41. Implement queue ticket generation.
- [ ] 42. Implement consultation status changes.
- [ ] 43. Implement patient cancellation.
- [ ] 44. Implement patient skipping and re-queuing rules.
- [ ] 45. Implement consultation completion.
- [ ] 46. Implement queue position calculation.
- [ ] 47. Implement queue statistics.

**Deliverable:** The backend supports the complete basic clinic workflow without requiring GUI interaction.

## Phase 5: GUI Development

**Goal:** Connect Swing components to the working backend.

- [ ] 48. Create `Main.java`.
- [ ] 49. Create `LoginFrame`.
- [ ] 50. Implement staff authentication.
- [ ] 51. Create `ClinicFrame`.
- [ ] 52. Create `DashboardPanel`.
- [ ] 53. Create `QueuePanel`.
- [ ] 54. Display the active queue using `JTable`.
- [ ] 55. Connect Register Patient to the backend.
- [ ] 56. Connect Call Next Patient to the backend.
- [ ] 57. Connect Skip and Cancel operations.
- [ ] 58. Create `DoctorPanel`.
- [ ] 59. Display doctor availability and assigned rooms.
- [ ] 60. Implement doctor availability controls.
- [ ] 61. Create `PatientRegistrationDialog`.
- [ ] 62. Create `PatientFrame`.
- [ ] 63. Implement automatic patient display updates.
- [ ] 64. Connect the Open/Close Clinic controls.
- [ ] 65. Implement Logout.

**Deliverable:** Both interfaces work with the same backend and display consistent information.

## Phase 6: Appointments and Additional Features

**Goal:** Expand the system beyond basic queue management.

- [ ] 66. Implement `AppointmentManager`.
- [ ] 67. Create `AppointmentDialog`.
- [ ] 68. Implement appointment scheduling.
- [ ] 69. Implement appointment cancellation and rescheduling.
- [ ] 70. Implement appointment check-in.
- [ ] 71. Implement appointment and visit history.
- [ ] 72. Implement fairness rules for priority queues.

**Deliverable:** The clinic can schedule future appointments and incorporate arriving appointment patients into the workflow.

## Phase 7: Testing and Finalization

- [ ] 73. Test registration of new patients.
- [ ] 74. Test returning patients.
- [ ] 75. Test multiple doctors.
- [ ] 76. Test unavailable doctors.
- [ ] 77. Test room assignments.
- [ ] 78. Test priority and FIFO behavior.
- [ ] 79. Test queue cancellation and skipping.
- [ ] 80. Test clinic closing and reopening.
- [ ] 81. Test CSV persistence.
- [ ] 82. Test simultaneous GUI updates.
- [ ] 83. Validate input and error handling.
- [ ] 84. Remove debugging code and unused methods.
- [ ] 85. Complete README and documentation.

**Deliverable:** A tested, documented, and presentable application.

---

# 8. Feature Prioritization

Do not attempt every feature simultaneously. Separate essential functionality from extensions.

| Feature | Priority |
|---|---|
| Patient registration | Must have |
| Patient search | Must have |
| Queue management | Must have |
| Priority queue | Must have |
| Regular FIFO queue | Must have |
| Doctor availability | Must have |
| Room assignment | Must have |
| CSV CRUD | Must have |
| Staff login | Must have |
| Patient display | Must have |
| Clinic open/close | Must have |
| Appointment scheduling | Secondary |
| Visit history | Secondary |
| Queue fairness algorithm | Secondary |
| Statistics and reporting | Secondary |

**Recommendation:** Finish Phases 1–5 before working on appointments. A functioning clinic with patient registration, queue management, doctor assignment, room assignment, and persistent records is already a substantial project.

---

# 9. Testing Scenarios

Test the entire clinic workflow, not just individual methods.

| Scenario | Expected result |
|---|---|
| Register new patient | Patient and Visit records created. |
| Register returning patient | Existing Patient reused; new Visit created. |
| Register urgent patient | Correct priority queue placement. |
| Register regular patient | FIFO queue placement. |
| Call next patient | Correct eligible patient is selected. |
| Doctor unavailable | Patient is not assigned to that doctor. |
| No available room | Patient cannot begin consultation. |
| Complete consultation | Visit completed; doctor and room released. |
| Cancel visit | Removed from active queue but history preserved. |
| Close clinic | New check-ins prevented. |
| Restart application | Previously saved records restored. |

### Important edge case

What happens when two patients request the same doctor, but that doctor is already consulting?

The second patient should remain waiting. They should not be silently assigned to another doctor.

---

# 10. Immediate Starting Point

Do not start with the GUI. Begin with the data model and relationships.

## First development session

- [ ] Review the existing Java files and identify what is already implemented.
- [ ] Create the `model` package and implement `Patient.java`.
- [ ] Implement `Visit.java`, separating patient information from visit information.
- [ ] Implement `Doctor.java` and `Room.java`.
- [ ] Create sample objects and test the relationships between them.

## Architectural decisions to settle

1. **Priority rules:** Decide whether priority is based on medical urgency, appointment schedules, or both. Recommended: medical urgency first, with appointment scheduling handled separately.
2. **Queue assignment:** Use a shared clinic queue with optional preferred-doctor assignments rather than entirely independent queues for each doctor.
3. **Patient window:** Use a public waiting-room display that automatically updates, rather than a separate application with its own queue logic.
4. **CSV persistence:** Treat CSV as the storage layer, not as the active queue. Load records into collections when the application starts and synchronize changes with the files.

---

# 11. Suggested Next Step: Audit the Existing Project

Before implementing this roadmap, compare it against the current repository.

An audit should identify:

- Classes and methods already implemented.
- Existing code that can be reused.
- Incomplete or incorrect implementations.
- Missing classes and functionality.
- Dependencies between unfinished components.
- The next concrete task to implement.

This prevents unnecessary rewrites and keeps development focused on what is actually missing.

**Recommended immediate action:** Review the existing Java files, then complete the model classes and test their relationships before proceeding to queue management.
