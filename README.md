# Clinic-Patient-Queue-Management-System-
For a school project. A program that handles patient queueing in a clinic

- **Programming Language**: Java Development
- **Environment**: NetBeans IDE 
- **User Interface:** Java Swing (GUI) 
- **Project Type**: Fully Functional Desktop Application

-----
**Clinic Patient Queue Management System**
- Registers patients, manage waiting lines, and prioritize urgent cases
- Queue, Priority Queue, Linked List

#### Requirements
Implement at least two appropriate algorithms, including one from each category: 
**Searching Algorithms**: 
- Linear Search 
- Binary Search 
- Interpolation Search 
**Sorting Algorithms**: 
- Bubble Sort 
- Insertion Sort
- Selection Sort 
- Quick Sort

> Notes:
> - "not just basic database operations." will need database implementations

## How to test the application

### 1. Open and run

1. Open the `ClinicDSAProject` folder in NetBeans.
2. Confirm the project Java platform is JDK 26.
3. Clean and build the project.
4. Run the project. The login window should open.

Use the existing test credentials:

```text
Staff ID: STAFF-001
Password: admin123
```

The application reads and writes CSV files from a `data` folder. It first checks
the application working directory, then its parent directory (so the project
can be run from either the repository root or `ClinicDSAProject`). Before
running, make sure these files are available in the selected folder:

- `data/patients.csv`
- `data/visits.csv`

For a different location, set the `clinic.data.dir` Java system property to
that folder before launching the application.

### 2. Test patient registration

1. Select **Patient Registration** from the side navigation.
2. Enter a new patient ID and the required patient information.
3. Click **Register Patient**.
4. Select the patient row to load it into the form.
5. Test **Update**, **Delete**, and **Clear**.
6. Try registering the same patient ID twice; the application should reject the duplicate.

### 3. Test queue registration and time

1. Select **Queue Handling**.
2. Enter an existing patient ID.
3. Select a priority.
4. Enter a visit reason.
5. Enter a time using `HH:mm`, such as `09:30`.
6. Click **Add to Queue**.
7. Confirm that the queue table shows the ticket, patient, time, priority, reason, and status.
8. Enter an invalid time such as `9:30 PM`; the application should reject it and request `HH:mm`.

### 4. Test queue order

Add several waiting visits and click **Call Next**:

1. Emergency visits should be called first.
2. Urgent visits should be called next.
3. Regular visits should follow FIFO order.
4. The called visit should show its assigned doctor and room.
5. Click **Start Consultation**, then **Complete**.
6. Confirm that completing a visit releases the doctor and room.
7. Use **No-show** on a waiting visit and confirm it is not called later.

### 5. Test public display

1. Click **Public Display**.
2. Confirm that a separate read-only window opens.
3. Call a patient from **Queue Handling**.
4. Confirm that the public display updates with the queue number, doctor, room, and waiting count.
5. Close the public display and open it again to verify it can be reopened.

### 6. Test persistence

1. Register a patient and a timed visit.
2. Close and run the application again.
3. Confirm that the patient, visit time, priority, reason, and status remain in the queue.
4. Inspect the CSV files only after closing the application or completing the operation so the latest save is visible.

The current sample data includes completed, cancelled, and waiting visits. Waiting
sample visits are intentionally used to test the priority queue and regular FIFO
behavior.
