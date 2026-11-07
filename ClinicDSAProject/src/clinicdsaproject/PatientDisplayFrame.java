package clinicdsaproject;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PatientDisplayFrame extends JFrame {
    private final ClinicInitialDataProcess service;
    private final JLabel current = new JLabel("No patient is currently being called.", SwingConstants.CENTER);
    private final JLabel waiting = new JLabel("", SwingConstants.CENTER);

    public PatientDisplayFrame(ClinicInitialDataProcess service) {
        this.service = service;
        setTitle("Mercy Clinic - Patient Display");
        setSize(700, 400);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        current.setFont(new Font("Arial", Font.BOLD, 28));
        waiting.setFont(new Font("Arial", Font.PLAIN, 18));
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));
        panel.add(new JLabel("NOW SERVING", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(current, BorderLayout.CENTER);
        panel.add(waiting, BorderLayout.SOUTH);
        add(panel);
        Timer timer = new Timer(1000, e -> refresh());
        timer.start();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { timer.stop(); }
        });
        refresh();
    }

    private void refresh() {
        ClinicInitialDataProcess.Visit active = null;
        for (ClinicInitialDataProcess.Visit visit : service.getVisitHistory()) {
            if (visit.getStatus() == ClinicInitialDataProcess.VisitStatus.CALLED
                    || visit.getStatus() == ClinicInitialDataProcess.VisitStatus.IN_CONSULTATION) active = visit;
        }
        if (active == null) current.setText("No patient is currently being called.");
        else if (active.getDoctor() == null || active.getRoom() == null) {
            current.setText(active.getTicket() + "  -  Assignment pending");
        } else {
            current.setText(active.getTicket() + "  -  " + active.getDoctor().getName()
                    + "  -  " + active.getRoom().getNumber());
        }
        waiting.setText("Patients waiting: " + service.waitingCount());
    }
}
