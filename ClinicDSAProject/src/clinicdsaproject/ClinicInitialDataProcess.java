package clinicdsaproject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClinicInitialDataProcess {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public enum Priority {
        EMERGENCY(1), URGENT(2), REGULAR(3);

        private final int value;

        Priority(int value) {
            this.value = value;
        }
    }

    public enum VisitStatus {
        WAITING, CALLED, IN_CONSULTATION, COMPLETED, NO_SHOW, CANCELLED
    }

    public static final class Patient {
        private final String id;
        private String name;
        private String dateOfBirth;
        private String gender;
        private String contact;
        private String address;

        public Patient(String id, String name, String dateOfBirth, String gender, String contact, String address) {
            this.id = id;
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.contact = contact;
            this.address = address;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDateOfBirth() { return dateOfBirth; }
        public String getGender() { return gender; }
        public String getContact() { return contact; }
        public String getAddress() { return address; }
        public void update(String name, String dob, String gender, String contact, String address) {
            this.name = name;
            this.dateOfBirth = dob;
            this.gender = gender;
            this.contact = contact;
            this.address = address;
        }
    }

    public static final class Doctor {
        private final String id;
        private final String name;
        private boolean available = true;

        public Doctor(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public boolean isAvailable() { return available; }
        private void setAvailable(boolean value) { available = value; }
    }

    public static final class Room {
        private final String number;
        private boolean available = true;

        public Room(String number) { this.number = number; }
        public String getNumber() { return number; }
        public boolean isAvailable() { return available; }
        private void setAvailable(boolean value) { available = value; }
    }

    public static final class Visit {
        private final String ticket;
        private final Patient patient;
        private final Priority priority;
        private final String reason;
        private final LocalTime scheduledTime;
        private final LocalDateTime arrival;
        private VisitStatus status = VisitStatus.WAITING;
        private Doctor doctor;
        private Room room;

        private Visit(String ticket, Patient patient, Priority priority, String reason, LocalTime scheduledTime) {
            this.ticket = ticket;
            this.patient = patient;
            this.priority = priority;
            this.reason = reason;
            this.scheduledTime = scheduledTime;
            this.arrival = LocalDateTime.now();
        }
        public String getTicket() { return ticket; }
        public Patient getPatient() { return patient; }
        public Priority getPriority() { return priority; }
        public String getReason() { return reason; }
        public LocalTime getScheduledTime() { return scheduledTime; }
        public VisitStatus getStatus() { return status; }
        public Doctor getDoctor() { return doctor; }
        public Room getRoom() { return room; }
        private void setStatus(VisitStatus value) { status = value; }
        private void assign(Doctor doctor, Room room) {
            this.doctor = doctor;
            this.room = room;
        }
    }

    private static final Comparator<Visit> PRIORITY_ORDER = Comparator
            .comparingInt((Visit visit) -> visit.priority.value)
            .thenComparing(visit -> visit.arrival);

    private final LinkedList<Patient> patients = new LinkedList<>();
    private final PriorityQueue<Visit> priorityQueue = new PriorityQueue<>(PRIORITY_ORDER);
    private final Queue<Visit> regularQueue = new ArrayDeque<>();
    private final LinkedList<Visit> visitHistory = new LinkedList<>();
    private final ArrayList<Doctor> doctors = new ArrayList<>();
    private final ArrayList<Room> rooms = new ArrayList<>();
    private int nextTicket = 1;
    private final Path dataDirectory = resolveDataDirectory();
    private final Path patientsFile = dataDirectory.resolve("patients.csv");
    private final Path visitsFile = dataDirectory.resolve("visits.csv");

    public ClinicInitialDataProcess() {
        doctors.add(new Doctor("DOC-001", "Dr. Santos"));
        doctors.add(new Doctor("DOC-002", "Dr. Reyes"));
        rooms.add(new Room("Room 1"));
        rooms.add(new Room("Room 2"));
        loadData();
    }

    public synchronized List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public synchronized List<Visit> getWaitingVisits() {
        List<Visit> result = new ArrayList<>();
        result.addAll(priorityQueue);
        result.addAll(regularQueue);
        result.sort(PRIORITY_ORDER);
        return result;
    }

    public synchronized List<Visit> getVisitHistory() {
        return new ArrayList<>(visitHistory);
    }

    public synchronized List<Doctor> getDoctors() { return new ArrayList<>(doctors); }
    public synchronized List<Room> getRooms() { return new ArrayList<>(rooms); }

    private static Path resolveDataDirectory() {
        String configuredDirectory = System.getProperty("clinic.data.dir");
        if (configuredDirectory != null && !configuredDirectory.isBlank()) {
            return Path.of(configuredDirectory).toAbsolutePath().normalize();
        }

        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        Path localDataDirectory = workingDirectory.resolve("data");
        if (Files.isDirectory(localDataDirectory)) return localDataDirectory;

        Path parentDataDirectory = workingDirectory.resolve("..").resolve("data").normalize();
        if (Files.isDirectory(parentDataDirectory)) return parentDataDirectory;

        return localDataDirectory;
    }

    public synchronized Patient findPatient(String query) {
        for (Patient patient : patients) {
            if (patient.id.equalsIgnoreCase(query) || patient.name.equalsIgnoreCase(query)) {
                return patient;
            }
        }
        return null;
    }

    public synchronized Patient findPatientBinary(String id) {
        List<Patient> sorted = new ArrayList<>(patients);
        iterativeQuickSort(sorted, Comparator.comparing(Patient::getId));
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            int middle = (low + high) >>> 1;
            int comparison = sorted.get(middle).id.compareToIgnoreCase(id);
            if (comparison == 0) return sorted.get(middle);
            if (comparison < 0) low = middle + 1;
            else high = middle - 1;
        }
        return null;
    }

    public synchronized void addPatient(Patient patient) {
        if (patient.id.isBlank() || patient.name.isBlank()) throw new IllegalArgumentException("Patient ID and name are required.");
        if (findPatient(patient.id) != null) throw new IllegalArgumentException("Patient ID already exists.");
        patients.add(patient);
        saveData();
    }

    public synchronized void updatePatient(Patient patient) {
        Patient existing = findPatient(patient.id);
        if (existing == null) throw new IllegalArgumentException("Patient was not found.");
        existing.update(patient.name, patient.dateOfBirth, patient.gender, patient.contact, patient.address);
        saveData();
    }

    public synchronized void deletePatient(String id) {
        Patient patient = findPatient(id);
        if (patient == null) throw new IllegalArgumentException("Patient was not found.");
        patients.remove(patient);
        saveData();
    }

    public synchronized Visit registerVisit(String patientId, Priority priority, String reason, String time) {
        Patient patient = findPatient(patientId);
        if (patient == null) throw new IllegalArgumentException("Register the patient before creating a visit.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("Visit reason is required.");
        LocalTime scheduledTime;
        try {
            scheduledTime = LocalTime.parse(time.trim(), TIME_FORMAT);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Time is required and must use HH:mm format, for example 09:30.");
        }
        for (Visit visit : getWaitingVisits()) {
            if (visit.patient == patient) throw new IllegalArgumentException("This patient already has an active queue visit.");
        }
        String ticket = String.format("Q-%03d", nextTicket++);
        Visit visit = new Visit(ticket, patient, priority, reason, scheduledTime);
        if (priority == Priority.REGULAR) regularQueue.offer(visit);
        else priorityQueue.offer(visit);
        visitHistory.add(visit);
        saveData();
        return visit;
    }

    public synchronized Visit callNext() {
        Visit next = pollNextWaiting();
        if (next == null) throw new IllegalStateException("There are no patients waiting.");
        Doctor doctor = findAvailableDoctor();
        Room room = findAvailableRoom();
        if (doctor == null || room == null) {
            requeue(next);
            throw new IllegalStateException("No doctor and room are available for the next patient.");
        }
        doctor.setAvailable(false);
        room.setAvailable(false);
        next.assign(doctor, room);
        next.setStatus(VisitStatus.CALLED);
        saveData();
        return next;
    }

    public synchronized void updateStatus(String ticket, VisitStatus status) {
        Visit visit = findVisit(ticket);
        if (visit == null) throw new IllegalArgumentException("Visit was not found.");
        if (status == VisitStatus.IN_CONSULTATION && visit.getStatus() != VisitStatus.CALLED) {
            throw new IllegalArgumentException("Only a called patient can start consultation.");
        }
        visit.setStatus(status);
        if (status == VisitStatus.COMPLETED || status == VisitStatus.CANCELLED || status == VisitStatus.NO_SHOW) {
            priorityQueue.remove(visit);
            regularQueue.remove(visit);
            releaseResources(visit);
        }
        saveData();
    }

    public synchronized int waitingCount() { return priorityQueue.size() + regularQueue.size(); }
    public synchronized int activeCount() {
        int count = 0;
        for (Visit visit : visitHistory) {
            if (visit.status == VisitStatus.CALLED || visit.status == VisitStatus.IN_CONSULTATION) count++;
        }
        return count;
    }

    public synchronized List<Visit> sortedHistory() {
        List<Visit> result = new ArrayList<>(visitHistory);
        iterativeQuickSort(result, Comparator.comparing(Visit::getTicket));
        return result;
    }

    public static <T> void iterativeQuickSort(List<T> values, Comparator<T> comparator) {
        if (values.size() < 2) return;
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, values.size() - 1});
        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int left = range[0];
            int right = range[1];
            if (left >= right) continue;
            int i = left;
            int j = right;
            T pivot = values.get(left + (right - left) / 2);
            while (i <= j) {
                while (comparator.compare(values.get(i), pivot) < 0) i++;
                while (comparator.compare(values.get(j), pivot) > 0) j--;
                if (i <= j) {
                    T temp = values.get(i);
                    values.set(i, values.get(j));
                    values.set(j, temp);
                    i++;
                    j--;
                }
            }
            if (left < j) stack.push(new int[]{left, j});
            if (i < right) stack.push(new int[]{i, right});
        }
    }

    private Doctor findAvailableDoctor() {
        for (Doctor doctor : doctors) if (doctor.available) return doctor;
        return null;
    }

    private Room findAvailableRoom() {
        for (Room room : rooms) if (room.available) return room;
        return null;
    }

    private Visit findVisit(String ticket) {
        for (Visit visit : visitHistory) if (visit.ticket.equalsIgnoreCase(ticket)) return visit;
        return null;
    }

    private void requeue(Visit visit) {
        if (visit.priority == Priority.REGULAR) regularQueue.offer(visit);
        else priorityQueue.offer(visit);
    }

    private Visit pollNextWaiting() {
        Visit next;
        while ((next = priorityQueue.poll()) != null) {
            if (next.status == VisitStatus.WAITING) return next;
        }
        while ((next = regularQueue.poll()) != null) {
            if (next.status == VisitStatus.WAITING) return next;
        }
        return null;
    }

    private void releaseResources(Visit visit) {
        if (visit.doctor != null) visit.doctor.setAvailable(true);
        if (visit.room != null) visit.room.setAvailable(true);
    }

    private void loadData() {
        try {
            if (Files.exists(patientsFile)) {
                for (String line : Files.readAllLines(patientsFile, StandardCharsets.UTF_8)) {
                    String[] fields = line.split(",", -1);
                    if (fields.length >= 6 && !fields[0].equals("id")) {
                        patients.add(new Patient(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]));
                    }
                }
            }
            if (Files.exists(visitsFile)) {
                for (String line : Files.readAllLines(visitsFile, StandardCharsets.UTF_8)) {
                    String[] fields = line.split(",", -1);
                    if (fields.length >= 4 && !fields[0].equals("ticket")) {
                        Patient patient = findPatient(fields[1]);
                        if (patient != null) {
                            Priority priority = Priority.valueOf(fields[2]);
                            boolean hasTimeColumn = fields.length > 5;
                            String time = hasTimeColumn && !fields[4].isBlank()
                                    ? fields[4] : LocalTime.now().format(TIME_FORMAT);
                            Visit visit = new Visit(fields[0], patient, priority, fields[3],
                                    LocalTime.parse(time, TIME_FORMAT));
                            visit.setStatus(VisitStatus.valueOf(hasTimeColumn ? fields[5]
                                    : fields.length > 4 ? fields[4] : "WAITING"));
                            visitHistory.add(visit);
                            nextTicket = Math.max(nextTicket, Integer.parseInt(fields[0].replace("Q-", "")) + 1);
                            if (visit.status == VisitStatus.WAITING) requeue(visit);
                        }
                    }
                }
            }
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println("Clinic data could not be loaded: " + ex.getMessage());
        }
    }

    private void saveData() {
        try {
            Files.createDirectories(dataDirectory);
            List<String> patientLines = new ArrayList<>();
            patientLines.add("id,name,dateOfBirth,gender,contact,address");
            for (Patient patient : patients) {
                patientLines.add(csv(patient.id, patient.name, patient.dateOfBirth, patient.gender, patient.contact, patient.address));
            }
            Files.write(patientsFile, patientLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            List<String> visitLines = new ArrayList<>();
            visitLines.add("ticket,patientId,priority,reason,time,status");
            for (Visit visit : visitHistory) {
                visitLines.add(csv(visit.ticket, visit.patient.id, visit.priority.name(), visit.reason,
                        visit.scheduledTime.format(TIME_FORMAT), visit.status.name()));
            }
            Files.write(visitsFile, visitLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Clinic data could not be saved: " + ex.getMessage(), ex);
        }
    }

    private static String csv(String... fields) {
        return String.join(",", fields).replace("\n", " ");
    }
}
