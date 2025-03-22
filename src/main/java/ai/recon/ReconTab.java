
package ai.recon;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ReconTab {
    private final JPanel mainPanel = new JPanel(new BorderLayout());

    public ReconTab(MontoyaApi api, ReconPromptHandler promptHandler, ExecutorService executor) {
        /* JLabel label = new JLabel("Select a host in scope:");
        JButton refreshButton = new JButton("🔁 Refresh");
        JComboBox<String> hostSelector = new JComboBox<>();
        JTextArea questionArea = new JTextArea("What kind of application is this?");
        JButton askButton = new JButton("Ask AI");
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        for (String host : RequestUtils.getScopedHosts(api)) {
            hostSelector.addItem(host);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(label, BorderLayout.WEST);
        topPanel.add(hostSelector, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        mainPanel.add(askButton, BorderLayout.EAST);
        mainPanel.add(new JScrollPane(outputArea), BorderLayout.SOUTH); */
         JLabel label = new JLabel("Select a host in scope:");
        JButton refreshButton = new JButton("🔁 Refresh");
        JComboBox<String> hostSelector = new JComboBox<>();
        JButton askButton = new JButton("Ask AI");

        // Soru alanı (küçük)
        JTextArea questionArea = new JTextArea(4, 40);
        JScrollPane questionScroll = new JScrollPane(questionArea);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionScroll.setBorder(BorderFactory.createTitledBorder("Question"));

        // Yanıt alanı (büyük)
        JTextArea outputArea = new JTextArea(20, 60);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("AI Response"));

        // Hostları yükle
        refreshHostList(hostSelector, api);

        // Üst panel: host seçimi + refresh
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JPanel hostPanel = new JPanel(new BorderLayout(5, 5));
        hostPanel.add(label, BorderLayout.WEST);
        hostPanel.add(hostSelector, BorderLayout.CENTER);
        topPanel.add(hostPanel, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Alt panel: buton
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(askButton);

        // Ana panel: her şey burada birleşiyor
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(questionScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(outputScroll, BorderLayout.SOUTH);
        


        askButton.addActionListener(e -> executor.execute(() -> {
            String host = (String) hostSelector.getSelectedItem();
            List<ProxyHttpRequestResponse> requests = RequestUtils.getRequestsForHost(api, host);
            String fullPrompt = RequestUtils.formatRequests(requests) + "\n\nQUESTION:\n" + questionArea.getText();

            var response = promptHandler.ask(fullPrompt);
            SwingUtilities.invokeLater(() -> outputArea.setText(response.content()));
        }));
        refreshButton.addActionListener(e -> refreshHostList(hostSelector, api));

        // Hazır sorular
        JComboBox<String> questionTemplates = new JComboBox<>(new String[]{
            "What kind of application is this?",
            "List all authentication-related endpoints.",
            "Summarize the API structure and key methods.",
            "Are there any potentially sensitive data transfers?",
            "Does the application appear to use session management?"
        });
        

        // Kullanıcı seçim yaptığında input alanına otomatik yaz
        questionTemplates.addActionListener(e -> {
            String selected = (String) questionTemplates.getSelectedItem();
            if (selected != null) {
                questionArea.setText(selected);
            }
        });

        // Soru paneli (template + custom input)
        /* JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
        questionPanel.add(new JLabel("Select a question or write your own:"), BorderLayout.NORTH);
        questionPanel.add(questionTemplates, BorderLayout.CENTER);
        questionPanel.add(questionScroll, BorderLayout.SOUTH);

        mainPanel.add(questionPanel, BorderLayout.CENTER); */

        // Soru paneli (template + custom input)
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel qLabel = new JLabel("Select a question or write your own:");
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionTemplates.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionTemplates.setMaximumSize(new Dimension(300, 25)); // Sabit genişlik burada işe yarar

        questionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 100)); // Yükseklik sınırlı

        questionPanel.add(qLabel);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(questionTemplates);
        questionPanel.add(Box.createVerticalStrut(5));
        questionPanel.add(questionScroll);

        // Ekleme
        mainPanel.add(questionPanel, BorderLayout.CENTER);

    }
private void refreshHostList(JComboBox<String> hostSelector, MontoyaApi api) {
    hostSelector.removeAllItems();
    for (String host : RequestUtils.getScopedHosts(api)) {
        hostSelector.addItem(host);
    }
}

    public Component getUiComponent() {
        return mainPanel;
    }
}
