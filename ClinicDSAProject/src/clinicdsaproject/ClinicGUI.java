package clinicdsaproject;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClinicGUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ClinicGUI.class.getName());
    private static final Color GREEN = new Color(0, 130, 65);
    private static final Color LIGHT_GREEN = new Color(232, 247, 237);
    private final ClinicInitialDataProcess service = new ClinicInitialDataProcess();
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private JLabel waitingValue;
    private JLabel activeValue;
    private JLabel availableDoctorsValue;
    private JLabel availableRoomsValue;
    private PatientDisplayFrame patientDisplayFrame;

    public ClinicGUI() {
        initComponents();
        buildClinicLayout();
    }

    private void buildClinicLayout() {
        getContentPane().removeAll();
        setTitle("Mercy Clinic - Patient Queue Management System");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_GREEN);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("MERCY CLINIC  |  PATIENT QUEUE MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);
        JButton logout = navButton("Logout");
        logout.addActionListener(e -> {
            new LogInUIiii().setVisible(true);
            dispose();
        });
        header.add(logout, BorderLayout.EAST);

        JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 8));
        navigation.setBackground(new Color(210, 235, 218));
        navigation.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        JButton dashboard = navButton("Dashboard");
        JButton patients = navButton("Patient Registration");
        JButton queue = navButton("Queue Handling");
        JButton display = navButton("Public Display");
        navigation.add(dashboard); navigation.add(patients); navigation.add(queue); navigation.add(display);

        content.add(createDashboard(), "dashboard");
        content.add(new PatientRegistrationPanel(service), "patients");
        content.add(new QueuePanel(service), "queue");
        dashboard.addActionListener(e -> { refreshDashboard(); cards.show(content, "dashboard"); });
        patients.addActionListener(e -> cards.show(content, "patients"));
        queue.addActionListener(e -> cards.show(content, "queue"));
        display.addActionListener(e -> openPatientDisplay());
        root.add(header, BorderLayout.NORTH);
        root.add(navigation, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
        setMinimumSize(new java.awt.Dimension(1050, 650));
        pack();
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    private void openPatientDisplay() {
        try {
            if (patientDisplayFrame == null || !patientDisplayFrame.isDisplayable()) {
                patientDisplayFrame = new PatientDisplayFrame(service);
                patientDisplayFrame.setLocationRelativeTo(this);
            }
            patientDisplayFrame.setVisible(true);
            patientDisplayFrame.toFront();
            patientDisplayFrame.requestFocus();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "The patient display could not be opened: " + ex.getMessage(),
                    "Public Display", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        JLabel heading = new JLabel("Clinic Overview");
        heading.setFont(new Font("Times New Roman", Font.BOLD, 28));
        heading.setForeground(GREEN);
        panel.add(heading, BorderLayout.NORTH);
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        waitingValue = valueLabel();
        activeValue = valueLabel();
        availableDoctorsValue = valueLabel();
        availableRoomsValue = valueLabel();
        cardsPanel.add(statCard("Patients Waiting", waitingValue));
        cardsPanel.add(statCard("Being Served", activeValue));
        cardsPanel.add(statCard("Available Doctors", availableDoctorsValue));
        cardsPanel.add(statCard("Available Rooms", availableRoomsValue));
        panel.add(cardsPanel, BorderLayout.CENTER);
        panel.add(createResourcePanel(), BorderLayout.SOUTH);
        refreshDashboard();
        return panel;
    }

    private JPanel createResourcePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 30));
        StringBuilder doctorText = new StringBuilder("<html><h2>Doctors</h2>");
        for (ClinicInitialDataProcess.Doctor doctor : service.getDoctors()) {
            doctorText.append(doctor.getName()).append(" - ").append(doctor.isAvailable() ? "Available" : "Occupied").append("<br>");
        }
        doctorText.append("</html>");
        StringBuilder roomText = new StringBuilder("<html><h2>Rooms</h2>");
        for (ClinicInitialDataProcess.Room room : service.getRooms()) {
            roomText.append(room.getNumber()).append(" - ").append(room.isAvailable() ? "Available" : "Occupied").append("<br>");
        }
        roomText.append("</html>");
        panel.add(resourceCard(doctorText.toString()));
        panel.add(resourceCard(roomText.toString()));
        return panel;
    }

    private JPanel resourceCard(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(GREEN, 2));
        panel.add(new JLabel(text), BorderLayout.NORTH);
        return panel;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(GREEN, 2), title));
        card.add(value);
        return card;
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("0", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 32));
        label.setForeground(GREEN);
        return label;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(204, 204, 204));
        button.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        button.setPreferredSize(new java.awt.Dimension(180, 42));
        return button;
    }

    private void refreshDashboard() {
        if (waitingValue == null) return;
        waitingValue.setText(String.valueOf(service.waitingCount()));
        activeValue.setText(String.valueOf(service.activeCount()));
        availableDoctorsValue.setText(String.valueOf(service.getDoctors().stream().filter(ClinicInitialDataProcess.Doctor::isAvailable).count()));
        availableRoomsValue.setText(String.valueOf(service.getRooms().stream().filter(ClinicInitialDataProcess.Room::isAvailable).count()));
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 449, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
