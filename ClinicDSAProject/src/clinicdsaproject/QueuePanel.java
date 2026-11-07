package clinicdsaproject;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class QueuePanel extends JPanel {
    private final ClinicInitialDataProcess service;
    private final JTextField patientId = new JTextField();
    private final JTextField reason = new JTextField();
    private final JComboBox<ClinicInitialDataProcess.Priority> priority =
            new JComboBox<>(ClinicInitialDataProcess.Priority.values());
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Ticket", "Patient", "Time", "Priority", "Reason", "Status", "Doctor", "Room"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public QueuePanel(ClinicInitialDataProcess service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.add(new JLabel("Patient ID")); form.add(patientId);
        form.add(new JLabel("Priority")); form.add(priority);
        form.add(new JLabel("Visit Reason")); form.add(reason);
        JTextField time = new JTextField("09:00");
        form.add(new JLabel("Time (HH:mm)")); form.add(time);
        JButton register = new JButton("Add to Queue");
        form.add(register); form.add(new JLabel(""));
        add(form, BorderLayout.NORTH);
        register.addActionListener(e -> safe(() -> {
            service.registerVisit(patientId.getText().trim(),
                    (ClinicInitialDataProcess.Priority) priority.getSelectedItem(), reason.getText().trim(),
                    time.getText().trim());
            refresh(); patientId.setText(""); reason.setText(""); time.setText("09:00");
            info("Patient added to the queue.");
        }));
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton call = new JButton("Call Next");
        JButton consultation = new JButton("Start Consultation");
        JButton complete = new JButton("Complete");
        JButton noShow = new JButton("No-show");
        JButton refresh = new JButton("Refresh");
        call.addActionListener(e -> safe(() -> {
            ClinicInitialDataProcess.Visit visit = service.callNext();
            refresh();
            info("Now calling " + visit.getTicket() + " - " + visit.getPatient().getName()
                    + " (" + visit.getDoctor().getName() + ", " + visit.getRoom().getNumber() + ").");
        }));
        consultation.addActionListener(e -> changeStatus(ClinicInitialDataProcess.VisitStatus.IN_CONSULTATION));
        complete.addActionListener(e -> changeStatus(ClinicInitialDataProcess.VisitStatus.COMPLETED));
        noShow.addActionListener(e -> changeStatus(ClinicInitialDataProcess.VisitStatus.NO_SHOW));
        refresh.addActionListener(e -> refresh());
        actions.add(call); actions.add(consultation); actions.add(complete); actions.add(noShow); actions.add(refresh);
        add(actions, BorderLayout.SOUTH);
        refresh();
    }

    private void changeStatus(ClinicInitialDataProcess.VisitStatus status) {
        int row = table.getSelectedRow();
        if (row < 0) { info("Select a visit first."); return; }
        safe(() -> { service.updateStatus(String.valueOf(model.getValueAt(row, 0)), status); refresh(); });
    }

    private void refresh() {
        model.setRowCount(0);
        for (ClinicInitialDataProcess.Visit visit : service.sortedHistory()) {
            model.addRow(new Object[]{visit.getTicket(), visit.getPatient().getName(),
                visit.getScheduledTime(), visit.getPriority(), visit.getReason(), visit.getStatus(),
                visit.getDoctor() == null ? "-" : visit.getDoctor().getName(),
                visit.getRoom() == null ? "-" : visit.getRoom().getNumber()});
        }
    }

    private void safe(Runnable action) {
        try { action.run(); } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Queue", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void info(String text) { JOptionPane.showMessageDialog(this, text, "Queue", JOptionPane.INFORMATION_MESSAGE); }
}
