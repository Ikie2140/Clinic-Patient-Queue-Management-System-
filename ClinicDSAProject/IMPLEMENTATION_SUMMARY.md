# Clinic Patient Queue Management System

## Overview

The existing NetBeans Swing project was extended into a clinic queue application without replacing the login flow or project configuration.

## Implemented files

### Added

- `src/clinicdsaproject/PatientRegistrationPanel.java` - patient registration, update, delete, clear, and table selection.
- `src/clinicdsaproject/QueuePanel.java` - visit registration, queue display, call-next, consultation, completion, no-show, and refresh actions.
- `src/clinicdsaproject/PatientDisplayFrame.java` - read-only public display for a second monitor.

### Modified

- `src/clinicdsaproject/ClinicInitialDataProcess.java` - shared clinic service, model classes, collections, queue rules, search, sorting, and CSV persistence.
- `src/clinicdsaproject/ClinicGUI.java` - green clinic dashboard, side navigation, resource overview, logout, and panel integration.

### Preserved

- `src/clinicdsaproject/LogInUIiii.java` remains the login entry point.
- Test credentials remain `STAFF-001` / `admin123`.
- Existing NetBeans project files and package name remain unchanged.

## Data structures and algorithms

- `LinkedList<Patient>` stores permanent patient records.
- `PriorityQueue<Visit>` stores emergency and urgent visits.
- `ArrayDeque<Visit>` stores regular visits using FIFO behavior.
- `ArrayList<Doctor>` and `ArrayList<Room>` store clinic resources.
- Linear search finds patients, doctors, and rooms.
- Binary search finds a patient by ID after a sorted copy is produced.
- Iterative Quick Sort sorts visit history for the queue table.

## Queue behavior

1. Emergency visits are served before urgent visits.
2. Urgent visits are served before regular visits.
3. Visits with equal priority use arrival order.
4. Calling a patient assigns an available doctor and room.
5. Consultation can only start after a visit is called.
6. Completing, cancelling, or marking a visit as no-show releases assigned resources.
7. An active waiting visit cannot be duplicated for the same patient.

## Persistence

The service creates a `data` directory relative to the application working directory and stores:

- `data/patients.csv`
- `data/visits.csv`

Records are loaded on startup and saved after successful patient and queue operations. Existing data is not overwritten with mock records. Two doctors and two rooms are initialized as in-memory clinic resources for the school-project demonstration.

## Validation

The project sources were compiled successfully with:

```text
javac -d build\classes src\clinicdsaproject\*.java
```

The graphical interface was not launched in this environment, so final visual verification should be performed in NetBeans. The implementation uses Swing event handlers and remains compatible with the existing Ant project.

## Known limitations

- Appointment scheduling has not been added as a separate workflow yet; the current visit form records queue visits and priorities.
- CSV fields are intended for simple school-project values and do not implement quoted comma escaping.
- Doctor and room assignments are restored as available when the application restarts; active assignments are not persisted across sessions.
