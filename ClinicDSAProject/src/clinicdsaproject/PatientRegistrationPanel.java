package clinicdsaproject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PatientRegistrationPanel extends JPanel {
    private final ClinicInitialDataProcess service;
    private final JTextField id = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField dob = new JTextField();
    private final JTextField gender = new JTextField();
    private final JTextField contact = new JTextField();
    private final JTextField address = new JTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Patient ID", "Name", "Date of Birth", "Gender", "Contact", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public PatientRegistrationPanel(ClinicInitialDataProcess service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        addField(form, "Patient ID", id);
        addField(form, "Full Name", name);
        addField(form, "Date of Birth", dob);
        addField(form, "Gender", gender);
        addField(form, "Contact", contact);
        addField(form, "Address", address);
        add(form, BorderLayout.NORTH);
        JPanel buttons = new JPanel();
        JButton add = button("Register Patient");
        JButton update = button("Update");
        JButton delete = button("Delete");
        JButton clear = button("Clear");
        add.addActionListener(e -> run(() -> {
            service.addPatient(readPatient());
            refresh();
            clearFields();
            message("Patient registered.");
        }));
        update.addActionListener(e -> run(() -> {
            service.updatePatient(readPatient());
            refresh();
            message("Patient updated.");
        }));
        delete.addActionListener(e -> run(() -> {
            service.deletePatient(id.getText().trim());
            refresh();
            clearFields();
            message("Patient deleted.");
        }));
        clear.addActionListener(e -> clearFields());
        buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(clear);
        add(buttons, BorderLayout.SOUTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                id.setText(String.valueOf(tableModel.getValueAt(row, 0)));
                name.setText(String.valueOf(tableModel.getValueAt(row, 1)));
                dob.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                gender.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                contact.setText(String.valueOf(tableModel.getValueAt(row, 4)));
                address.setText(String.valueOf(tableModel.getValueAt(row, 5)));
            }
        });
        refresh();
    }

    private ClinicInitialDataProcess.Patient readPatient() {
        return new ClinicInitialDataProcess.Patient(id.getText().trim(), name.getText().trim(),
                dob.getText().trim(), gender.getText().trim(), contact.getText().trim(), address.getText().trim());
    }

    private void refresh() {
        tableModel.setRowCount(0);
        for (ClinicInitialDataProcess.Patient patient : service.getPatients()) {
            tableModel.addRow(new Object[]{patient.getId(), patient.getName(), patient.getDateOfBirth(),
                patient.getGender(), patient.getContact(), patient.getAddress()});
        }
    }

    private void clearFields() {
        id.setText(""); name.setText(""); dob.setText(""); gender.setText(""); contact.setText(""); address.setText("");
        id.requestFocusInWindow();
    }

    private static void addField(JPanel panel, String label, JTextField field) {
        panel.add(new JLabel(label)); panel.add(field);
    }

    private static JButton button(String text) {
        JButton button = new JButton(text);
        button.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        return button;
    }

    private void run(Runnable action) {
        try { action.run(); } catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Clinic", JOptionPane.WARNING_MESSAGE); }
    }

    private void message(String text) { JOptionPane.showMessageDialog(this, text, "Clinic", JOptionPane.INFORMATION_MESSAGE); }
}
