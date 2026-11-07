# Claude Prompt: Clinic Patient Queue Management System

You are working on a Java NetBeans project for a school project. I will provide the existing project files and a separate student-grade-record project as a UI reference.

Your task is to **build upon the existing clinic project**, not replace it with a new application.

## Project and reference

### Main project to modify

`Clinic-Patient-Queue-Management-System-/ClinicDSAProject/`

Inspect the existing source files, `.form` files, `nbproject` configuration, README, and development checklist before changing anything.

### UI reference project

`Student-Grade-Record-Sample-Java-OOP-Project/StudentGradeRecordOOPFinalProject/`

Use the reference project's existing Swing layout and implementation style as guidance. The reference is for visual structure and project organization, not for copying student-management behavior.

The clinic application should have the same general polished desktop-app feel, but use a **green clinic theme** and **side navigation** instead of the student application's red theme and top navigation.

## Non-negotiable implementation rules

1. Use Java Swing and keep the project compatible with NetBeans.
2. Extend the existing project. Do not start over or generate an unrelated Maven/Gradle project.
3. Preserve existing behavior unless a change is required to connect the clinic functionality.
4. Make small, surgical changes to existing files.
5. Do not delete or rewrite working code just to use a different architecture.
6. Preserve NetBeans-generated coding patterns where they already exist:
   - `initComponents()`
   - `//GEN-BEGIN` / `//GEN-END` sections
   - NetBeans-style event-handler methods
   - existing package names and project configuration
7. If a generated `.java` file must change, keep its corresponding `.form` file synchronized.
8. Do not manually put large tab implementations inside a generated main frame if a separate `JPanel` class is safer.
9. You may create one Java class per tab or feature when useful. Prefer small, focused classes such as:
   - `QueuePanel`
   - `PatientRegistrationPanel`
   - `DoctorRoomPanel`
   - `AppointmentPanel`
   - `PatientDisplayPanel`
   - `LoginUI`
10. Reuse existing helpers and classes before adding duplicates.
11. Do not add external dependencies unless absolutely necessary. The project should run with the JDK and NetBeans.
12. Do not use database frameworks. Persist records with CSV files as required by the project.
13. Do not leave fake buttons, empty handlers, unexplained TODOs, or success messages for operations that were not completed.
14. If a requirement cannot safely be implemented because the existing project is missing necessary context, inspect more files first. If it is still impossible, document the limitation clearly in the summary.

## Application goal

Implement a fully functional **Clinic Patient Queue Management System** for a school project.

The application must:

- Register patients.
- Maintain patient records.
- Create clinic visits and queue tickets.
- Manage regular and urgent patients.
- Prioritize emergency and urgent cases.
- Call the next eligible patient.
- Skip a no-show.
- Update visit status.
- Assign a doctor and an available room.
- Track doctor and room availability.
- Support appointments or scheduled visits.
- Save and reload records through CSV files.
- Provide searching and sorting functionality.
- Provide a read-only patient/public display window for a second monitor.

## Existing UI direction

Keep the main visual structure similar to the student-grade reference:

- Login screen before the main application.
- Main desktop frame with clearly separated sections.
- Reusable Swing panels for major features.
- Form fields grouped inside titled panels.
- Tables for records and queue data.
- Buttons with consistent styling.
- Refresh, clear, search, update, and print actions where appropriate.

For this clinic project:

- Use a professional green-based color palette.
- Put navigation on the **left side**.
- Use a main content area on the right that changes according to the selected navigation item.
- Keep the UI readable and appropriate for a clinic.
- Avoid unnecessary redesign of the existing frame.
- Keep the optional public display in a separate window so it can be placed on a second monitor.

## Required login behavior

Keep or improve the existing login flow without breaking it.

The login screen must:

- Accept the existing project’s intended staff credentials or mock credentials.
- Reject invalid credentials with a clear dialog.
- Open the staff dashboard after successful login.
- Provide logout that returns to the login screen and disposes the staff window.

If the existing project already defines credentials, preserve them and document the test credentials in the summary. Do not silently change them.

## Required staff features

### 1. Dashboard / overview

Show useful current information such as:

- Number of waiting patients.
- Number of emergency or urgent patients.
- Number of patients currently being served.
- Available doctors.
- Available rooms.
- Recently called queue number.

The dashboard must reflect the shared backend state, not hard-coded values.

### 2. Patient registration

Provide a form to:

- Add a new patient.
- Search for an existing patient.
- Update patient information.
- Clear the form.
- Prevent duplicate patient IDs.
- Validate required fields.

At minimum, support:

- Patient ID
- Full name
- Date of birth or age
- Gender
- Contact number
- Address

Keep permanent patient information separate from a clinic visit. A returning patient must not become a duplicate permanent patient record.

### 3. Queue handling

Provide a staff panel to:

- Register a patient for a visit.
- Generate a queue number.
- Select regular, urgent, or emergency priority.
- Record the reason for the visit.
- Mark whether the visit is an appointment.
- Choose a preferred doctor when applicable.
- Display waiting patients.
- Call the next eligible patient.
- Skip a no-show.
- Update a visit as waiting, called, in consultation, completed, cancelled, or no-show.
- Reassign a patient to a doctor or room when needed.
- Refresh the queue table.

When staff clicks **Call Next**, the queue must apply the actual priority and FIFO rules described below. Do not use table row order alone to decide who is next.

### 4. Doctor and room management

Display doctors and rooms with their current status.

The system must:

- Show available and occupied doctors.
- Show available and occupied rooms.
- Assign only available doctors and rooms.
- Release the doctor and room after a visit is completed or cancelled.
- Prevent a consultation from starting when the required doctor or room is unavailable.
- Allow reassignment when appropriate.

Doctor availability and room availability are separate states.

### 5. Appointments

Support basic appointment data:

- Patient
- Date
- Time
- Preferred doctor
- Status

Appointments may be placed into the queue when their scheduled visit is due. If full appointment scheduling is not already present, implement a practical basic version rather than an unfinished placeholder.

### 6. Public/patient display

Create or preserve a separate read-only window for a second monitor.

It should show only appropriate public information, such as:

- Currently called queue number.
- Doctor.
- Room.
- Current queue or recently called numbers.
- Basic status text.

Do not expose private patient details such as full name, address, phone number, or medical reason.

The public display must read from the same queue/service state as the staff interface. Do not create a second independent copy of the queue.

## Required data structures

Use the data structures meaningfully in the implementation:

### LinkedList

Use a `LinkedList<Patient>` or equivalent patient registry for permanent patient records.

Use it to support:

- Adding patient records.
- Updating and removing records.
- Linear search by patient ID or name.

### PriorityQueue

Use a `PriorityQueue<Visit>` for urgent and emergency visits.

Suggested priority:

1. `EMERGENCY`
2. `URGENT`
3. `REGULAR`

Lower priority values should be served first. Within the same priority, preserve arrival order.

### Queue / ArrayDeque

Use a FIFO queue such as `Queue<Visit>` with `ArrayDeque<Visit>` for regular walk-in visits.

Do not use a sorted table as a substitute for the queue algorithm.

### Supporting collections

Use suitable collections for:

- Doctors
- Rooms
- Visit history
- Appointment records

Keep a visit distinct from a permanent patient record. Queue entries should represent visits, not just patients.

## Required algorithms

The project requires searching and sorting algorithms that are more than simple CRUD.

Implement and visibly use:

1. **Linear search**
   - Find a patient by ID or name.
   - Find an available room or doctor when appropriate.

2. **Binary search**
   - Use it on a clearly documented sorted collection, such as patient records sorted by patient ID.
   - Ensure the collection is actually sorted before binary search.
   - Do not claim to use binary search if the code only performs a linear scan.

3. **Iterative Quick Sort**
   - Use it to sort a displayed or stored list, such as patient records, visit history, or queue history.
   - Implement it iteratively rather than recursively if the project requirement specifically expects iterative Quick Sort.
   - Add a clear comparator or sorting criterion.

Document where each algorithm is used and why.

## Queue rules

Implement these rules consistently in the service/backend and UI:

- Emergency visits are served before urgent visits.
- Urgent visits are served before regular visits.
- Visits with equal priority are served in arrival order.
- A skipped no-show is removed from the waiting queue and marked `NO_SHOW`.
- Calling a patient changes the visit status and records the called visit.
- A patient cannot be called twice while already in consultation.
- Completing a visit releases its assigned doctor and room.
- A visit should not be lost when a doctor or room is unavailable; keep it waiting or show a clear actionable message.
- Prevent duplicate active queue entries for the same visit.

If a fairness rule is added to prevent regular-patient starvation, document it and never allow it to override an emergency case.

## CSV persistence

Use CSV files for persistent storage, following the existing project’s conventions.

At minimum, persist enough data to restore:

- Patients
- Visits and queue history
- Doctors
- Rooms
- Appointments, if implemented

Requirements:

- Create missing CSV files or seed them with mock data when appropriate.
- Load data at startup.
- Save changes after successful create, update, delete, queue, assignment, and status operations.
- Handle empty files and missing files safely.
- Validate CSV data before creating objects.
- Do not silently discard malformed records; show or log a clear error according to the project’s existing style.
- Avoid corrupting existing records when updating.

Seed mock data only when it does not overwrite user data. Document the seed data in the summary.

## Concurrency and timers

If the existing project or UI needs automatic refreshing:

- Use Swing-safe mechanisms such as `javax.swing.Timer` for UI refreshes.
- Do not update Swing components from a background thread.
- Do not add a 20-second consultation timer unless it is implemented safely and fits the existing design.
- If a consultation timer is implemented, make it configurable, clearly visible, and safe to stop when the window closes.

## Validation and error handling

Validate:

- Required fields.
- Duplicate patient IDs.
- Invalid queue priority.
- Invalid date/time values.
- Invalid numeric values.
- Missing doctor or room assignment when required.
- Duplicate active queue entries.
- Invalid CSV records.

Use clear user-facing dialogs for recoverable input errors. Do not catch broad exceptions and ignore them. Do not return fake success.

## NetBeans and source organization

Before editing:

1. Inspect all existing `.java` and `.form` files.
2. Identify which files are NetBeans-generated.
3. Identify existing model, service, repository, GUI, and utility code.
4. Reuse the current package structure unless there is a strong reason to add a small package.
5. Check the existing build configuration and compile target.

When creating panels:

- Follow the style already used by the reference project.
- Use a separate Java file for each substantial panel if that prevents the main frame from becoming fragile.
- Keep event handlers near the components they control.
- Use descriptive component names.
- Preserve `.form` files for form-editor-managed classes.
- Hand-written panels may omit `.form` files only when they are intentionally not managed by the NetBeans GUI builder.

## Testing and verification

After implementation:

1. Compile the project using the existing NetBeans/Ant build configuration.
2. Fix all compile errors.
3. Run any existing tests.
4. Add focused tests only where the project already has a suitable test structure or where they are necessary to verify queue behavior.
5. Manually verify, as far as the environment allows:
   - Login success and failure.
   - Patient registration, search, update, and duplicate rejection.
   - Regular FIFO behavior.
   - Emergency and urgent priority behavior.
   - No-show handling.
   - Doctor and room assignment/release.
   - CSV load and save.
   - Public display updates from shared state.
   - Logout.
6. If a graphical environment is unavailable, still compile and test backend logic, then state the limitation precisely.

Do not claim that a feature works unless it was verified or clearly explain why it could not be verified.

## Final deliverable

After coding, create or update a Markdown file in the main clinic project named:

`IMPLEMENTATION_SUMMARY.md`

The summary must be based on the actual final code and include:

1. Project overview.
2. Files created, modified, and intentionally left unchanged.
3. Existing behavior preserved.
4. Implemented staff features.
5. Implemented patient/public display features.
6. Data structures and exactly where each is used.
7. Searching and sorting algorithms and exactly where each is used.
8. Queue priority and FIFO rules.
9. Patient-versus-visit data model.
10. CSV files, columns, seed data, and persistence behavior.
11. Login credentials for testing, if applicable.
12. Build and test commands executed.
13. Test results.
14. Known limitations or environment restrictions.
15. Any NetBeans `.form` synchronization notes.

Do not write a generic planned-feature summary. Describe only what is actually implemented.

## Final response format

When finished, report:

- A concise implementation summary.
- The path to `IMPLEMENTATION_SUMMARY.md`.
- The files changed.
- The validation commands and results.
- Any limitations that still require manual testing in NetBeans.

Begin by inspecting the supplied projects and existing code. Then implement the clinic system incrementally while preserving the existing NetBeans project structure.
