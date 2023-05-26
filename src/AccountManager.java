import org.dreambot.dbui.components.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
public class AccountManager extends DreamFrame {
	public AccountManager() throws IOException {
		super("@ Bot Manager", ImageIO.read(new URL("https://i.imgur.com/6wL0R36.png")));
		initComponents();
		try {
			loadAccounts(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setIfExists(DreamTextField field, String input) {
		if(!input.equals("null")) {
			field.setText(input);
		} else {
			field.setText("");
		}
	}

	private void loadAccounts(int index) throws IOException {
		File f = new File(System.getProperty("user.home") + File.separator + "accountManager.txt");
		if(!f.exists()){
			f.createNewFile();
		} else{
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			ArrayList<String> lines = new ArrayList<>();
			while( line != null) {
				lines.add(line);
				line = br.readLine();
			}
			if(lines.size() == 0 || index > lines.size()-1) return;

			for(String l : lines) {
				String[] details = l.split(seperator);
				if (details != null && details.length == 11) {
					Account a = new Account(details);
					accounts.put(a.nickname, a);
					labels.put(a.nickname, new DreamLabel(a.nickname));
					listModel.addElement(labels.get(a.nickname));
				}
			}


			accountsList.setSelectedIndex(0);
		}
	}

	private void setAccount(Account a) {
		nicknameTextField.setText(a.nickname);
		scriptTextField.setText(a.script);
		emailTextField.setText(a.email);
		passwordTextField.setText(a.password);
		setIfExists(pinTextField, a.pin);
		setIfExists(proxyIpTextField, a.proxyIp);
		setIfExists(proxyPortTextField, a.proxyPort);
		setIfExists(proxyUsernameTextField, a.proxyUsername);
		setIfExists(proxyPasswordTextField, a.proxyPassword);
		setIfExists(launchParamsTextField, a.launchParams);
		setIfExists(javaParamsTextField, a.javaParams);
		if(procs.containsKey(a.nickname)) {
			if(procs.get(a.nickname).isAlive()) {
				launchButton.setText("Re-Launch");
				pack();
			} else {
				procs.remove(emailTextField.getText());
				launchButton.setText("Launch");
				pack();
			}
		}
	}

	private void saveAccount(ActionEvent e) {
		try {
			File f = new File(System.getProperty("user.home") + File.separator + "accountManager.txt");
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			ArrayList<String> lines = new ArrayList<>();
			Account selected = accounts.get(((DreamLabel)listModel.get(accountsList.getSelectedIndex())).getText());

			String originalNickname = selected.nickname;

			selected.nickname = nicknameTextField.getText();
			selected.script = scriptTextField.getText();
			selected.email = emailTextField.getText();
			selected.password = passwordTextField.getText();
			selected.pin = pinTextField.getText();
			selected.proxyIp = proxyIpTextField.getText();
			selected.proxyPort = proxyPortTextField.getText();
			selected.proxyUsername = proxyUsernameTextField.getText();
			selected.proxyPassword = proxyPasswordTextField.getText();
			selected.launchParams = launchParamsTextField.getText();
			selected.javaParams = javaParamsTextField.getText();

			accounts.put(selected.nickname, selected);
			if(labels.containsKey(selected.nickname)) {
				labels.get(selected.nickname).setText(selected.nickname);
			} else {
				labels.put(selected.nickname, new DreamLabel(selected.nickname));
			}
			listModel.set(listModel.indexOf(labels.get(originalNickname)), labels.get(selected.nickname));



			boolean found = false;
			while (line != null) {
				if(line.contains(originalNickname)) {
					line = selected.toString();
					found = true;

				}
				lines.add(line);
				line = br.readLine();
			}
			br.close();
			if(!found) lines.add(selected.toString());
			BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));
			for(String l : lines) {
				writer.append(l);
				writer.newLine();
			}
			writer.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	private void launchAccount(ActionEvent e) {
		try {
			if(procs.containsKey(emailTextField.getText())) {
				if(procs.get(emailTextField.getText()).isAlive()) {
					launchButton.setText("Re-Launch");
					pack();
				} else {
					procs.remove(emailTextField.getText());
					launchButton.setText("Launch");
					pack();
				}
			}
			ProcessBuilder builder = new ProcessBuilder(createCmdList());
			builder.inheritIO();
			Process p = builder.start();
			Thread.sleep(500);
			if(p.isAlive()) procs.put(emailTextField.getText(), p);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	private void exportBat(ActionEvent e) {
		StringSelection stringSelection = new StringSelection(createCmd());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	private String[] createCmdList() {
		ArrayList<String> b = new ArrayList<>();
		b.add("java");
		if(javaParamsTextField.getText() != null && javaParamsTextField.getText().length() > 0) {
			b.add(javaParamsTextField.getText());
		}
		b.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
		b.add("-jar");
		b.add(System.getProperties().get("user.home")+File.separator+"DreamBot"+File.separator+"BotData"+File.separator+"client.jar");
		b.add("-script");
		b.add(scriptTextField.getText());
		b.add("-accountUsername");
		b.add(emailTextField.getText());
		b.add("-accountPassword");
		b.add(passwordTextField.getText());
		if(pinTextField.getText() != null && pinTextField.getText().length() == 4) {
			try {
				int port = Integer.parseInt(pinTextField.getText());
				b.add("-accountPin");
				b.add(""+port);
			} catch (Exception ee) {
			}
		}
		if(proxyIpTextField.getText() != null && proxyIpTextField.getText().length() > 0) {
			b.add("-proxyHost");
			b.add(proxyIpTextField.getText());
		}
		if(proxyPortTextField.getText() != null && proxyPortTextField.getText().length() > 0) {
			b.add("-proxyPort");
			b.add(proxyPortTextField.getText());
		}
		if(proxyUsernameTextField.getText() != null && proxyUsernameTextField.getText().length() > 0) {
			b.add("-proxyUser");
			b.add(proxyUsernameTextField.getText());
		}
		if(proxyPasswordTextField.getText() != null && proxyPasswordTextField.getText().length() > 0) {
			b.add("-proxyPass");
			b.add(proxyPasswordTextField.getText());
		}
		if(launchParamsTextField.getText() != null && launchParamsTextField.getText().length() > 0) {
			b.add("-params");
			b.add(launchParamsTextField.getText());
		}
		b.add("-world");
		if(checkBox1.isSelected()) b.add("members");
		else b.add("f2p");
		return b.toArray(new String[0]);
	}

	private String createCmd() {
		StringBuilder b = new StringBuilder("java ");
		if(javaParamsTextField.getText() != null && javaParamsTextField.getText().length() > 0) {
			b.append(javaParamsTextField.getText());
			b.append(" -jar ");
		} else {
			b.append("-jar ");
		}

		b.append(System.getProperties().get("user.home")+File.separator+"DreamBot"+File.separator+"BotData"+File.separator+"client.jar");
		b.append(" -script \"" + scriptTextField.getText()+"\"");
		b.append(" -accountUsername " + emailTextField.getText());
		b.append(" -accountPassword " + passwordTextField.getText());
		if(pinTextField.getText() != null && pinTextField.getText().length() == 4) {
			try {
				int port = Integer.parseInt(pinTextField.getText());
				b.append(" -accountPin " + port);
			} catch (Exception ee) {
			}
		}
		if(proxyIpTextField.getText() != null && proxyIpTextField.getText().length() > 0) {
			b.append(" -proxyHost " + proxyIpTextField.getText());
		}
		if(proxyPortTextField.getText() != null && proxyPortTextField.getText().length() > 0) {
			b.append(" -proxyPort " + proxyPortTextField.getText());
		}
		if(proxyUsernameTextField.getText() != null && proxyUsernameTextField.getText().length() > 0) {
			b.append(" -proxyUser " + proxyUsernameTextField.getText());
		}
		if(proxyPasswordTextField.getText() != null && proxyPasswordTextField.getText().length() > 0) {
			b.append(" -proxyPass " + proxyPasswordTextField.getText());
		}
		if(launchParamsTextField.getText() != null && launchParamsTextField.getText().length() > 0) {
			b.append(" -params " + launchParamsTextField.getText());
		}
		if(checkBox1.isSelected()) b.append(" -world members");
		else b.append(" -world f2p");
		return b.toString();
	}

	private void accountSelected(ListSelectionEvent e) {
		if(accountsList.getSelectedIndex() >= 0)
			setAccount(accounts.get(((DreamLabel)listModel.get(accountsList.getSelectedIndex())).getText()));
	}

	private void deleteButtonClicked(MouseEvent e) {
		DreamLabel ll = ((DreamLabel)listModel.get(accountsList.getSelectedIndex()));
		Account a = accounts.getOrDefault(ll.getText(), null);
		if(a != null) {
			accounts.remove(a.nickname);
			listModel.removeElementAt(accountsList.getSelectedIndex());
			try {
				File f = new File(System.getProperty("user.home") + File.separator + "accountManager.txt");
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				ArrayList<String> lines = new ArrayList<>();
				boolean found = false;
				while (line != null) {
					if (!line.contains(a.nickname)) {
						lines.add(line);
					}
					line = br.readLine();
				}
				br.close();
				BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));
				for (String l : lines) {
					writer.append(l);
					writer.newLine();
				}
				writer.close();
			} catch (Exception ee) {}
		}
	}

	private void newAccountClicked(MouseEvent e) {
		Account a = new Account();
		a.nickname = "New Account";
		int i = 0;
		while(accounts.containsKey(a.nickname)) {
			i += 1;
			a.nickname = "New Account " + i;
		}
		accounts.put(a.nickname, a);
		labels.put(a.nickname, new DreamLabel(a.nickname));
		listModel.addElement(labels.get(a.nickname));
		accountsList.setSelectedIndex(listModel.size()-1);
	}

	private void initComponents() {
		accountsList = new DreamList();
		accountsList.setModel(listModel);
		scrollPane1 = new DreamScrollPane(accountsList);
		saveButton = new DreamButton("Save");
		launchButton = new DreamButton("Launch");
		exportButton = new DreamButton("Copy");
		deleteButton = new DreamButton("Delete");
		label1 = new DreamLabel();
		label2 = new DreamLabel();
		label3 = new DreamLabel();
		label4 = new DreamLabel();
		label5 = new DreamLabel();
		label6 = new DreamLabel();
		label7 = new DreamLabel();
		label8 = new DreamLabel();
		label9 = new DreamLabel();
		label10 = new DreamLabel();
		label11 = new DreamLabel();
		nicknameTextField = new DreamTextField();
		scriptTextField = new DreamTextField();
		emailTextField = new DreamTextField();
		passwordTextField = new DreamTextField();
		pinTextField = new DreamTextField();
		proxyIpTextField = new DreamTextField();
		proxyPortTextField = new DreamTextField();
		proxyUsernameTextField = new DreamTextField();
		proxyPasswordTextField = new DreamTextField();
		launchParamsTextField = new DreamTextField();
		javaParamsTextField = new DreamTextField();
		newAccountButton = new DreamButton("New Account");
		label12 = new DreamLabel();
		checkBox1 = new DreamCheckBox();

		//======== this ========
		setTitle("@ Bot Manager");
//		Container contentPane = getContentPane();
		setLayout(new BorderLayout());
		DreamHeader header = new DreamHeader(this,"@ Bot Manager");
		add(header, BorderLayout.PAGE_START);
//		setContentPane(new DreamPanel());
		DreamPanel contentPane = new DreamPanel();
		add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);


		//======== scrollPane1 ========
		{

			//---- accountsList ----
			accountsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			accountsList.addListSelectionListener(e -> accountSelected(e));
			scrollPane1.setViewportView(accountsList);
		}
		contentPane.add(scrollPane1);
		scrollPane1.setBounds(10, 10, 195, 390);

		//---- saveButton ----
		saveButton.setText("Save");
		saveButton.addActionListener(e -> saveAccount(e));
		contentPane.add(saveButton);
		saveButton.setBounds(new Rectangle(new Point(380, 420), saveButton.getPreferredSize()));

		//---- launchButton ----
		launchButton.setText("Launch");
		launchButton.addActionListener(e -> launchAccount(e));
		contentPane.add(launchButton);
		launchButton.setBounds(new Rectangle(new Point(555, 420), launchButton.getPreferredSize()));

		//---- exportButton ----
		exportButton.setText("Copy CMD");
		exportButton.addActionListener(e -> exportBat(e));
		contentPane.add(exportButton);
		exportButton.setBounds(new Rectangle(new Point(455, 420), exportButton.getPreferredSize()));

		//---- label1 ----
		label1.setText("Nickname");
		contentPane.add(label1);
		label1.setBounds(new Rectangle(new Point(230, 35), label1.getPreferredSize()));

		//---- label2 ----
		label2.setText("Script");
		contentPane.add(label2);
		label2.setBounds(new Rectangle(new Point(230, 65), label2.getPreferredSize()));

		//---- label3 ----
		label3.setText("Email");
		contentPane.add(label3);
		label3.setBounds(new Rectangle(new Point(230, 95), label3.getPreferredSize()));

		//---- label4 ----
		label4.setText("Password");
		contentPane.add(label4);
		label4.setBounds(new Rectangle(new Point(230, 125), label4.getPreferredSize()));

		//---- label5 ----
		label5.setText("Pin");
		contentPane.add(label5);
		label5.setBounds(new Rectangle(new Point(230, 155), label5.getPreferredSize()));

		//---- label6 ----
		label6.setText("Proxy IP");
		contentPane.add(label6);
		label6.setBounds(new Rectangle(new Point(230, 185), label6.getPreferredSize()));

		//---- label7 ----
		label7.setText("Proxy Port");
		contentPane.add(label7);
		label7.setBounds(new Rectangle(new Point(230, 215), label7.getPreferredSize()));

		//---- label8 ----
		label8.setText("Proxy Username");
		contentPane.add(label8);
		label8.setBounds(new Rectangle(new Point(230, 245), label8.getPreferredSize()));

		//---- label9 ----
		label9.setText("Proxy Password");
		contentPane.add(label9);
		label9.setBounds(new Rectangle(new Point(230, 275), label9.getPreferredSize()));

		//---- label10 ----
		label10.setText("Launch Parameters");
		contentPane.add(label10);
		label10.setBounds(new Rectangle(new Point(230, 305), label10.getPreferredSize()));

		//---- label11 ----
		label11.setText("Java Parameters");
		contentPane.add(label11);
		label11.setBounds(new Rectangle(new Point(230, 335), label11.getPreferredSize()));
		contentPane.add(nicknameTextField);
		nicknameTextField.setBounds(350, 35, 140, nicknameTextField.getPreferredSize().height);
		contentPane.add(scriptTextField);
		scriptTextField.setBounds(350, 65, 140, scriptTextField.getPreferredSize().height);
		contentPane.add(emailTextField);
		emailTextField.setBounds(350, 95, 140, 22);
		contentPane.add(passwordTextField);
		passwordTextField.setBounds(350, 125, 140, 22);
		contentPane.add(pinTextField);
		pinTextField.setBounds(350, 155, 140, 22);
		contentPane.add(proxyIpTextField);
		proxyIpTextField.setBounds(350, 185, 140, 22);
		contentPane.add(proxyPortTextField);
		proxyPortTextField.setBounds(350, 215, 140, 22);
		contentPane.add(proxyUsernameTextField);
		proxyUsernameTextField.setBounds(350, 245, 140, 22);
		contentPane.add(proxyPasswordTextField);
		proxyPasswordTextField.setBounds(350, 275, 140, 22);
		contentPane.add(launchParamsTextField);
		launchParamsTextField.setBounds(350, 305, 275, 22);
		contentPane.add(javaParamsTextField);
		javaParamsTextField.setBounds(350, 335, 275, 22);

		//---- newAccountButton ----
		newAccountButton.setText("New Account");
		newAccountButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				newAccountClicked(e);
			}
		});
		contentPane.add(newAccountButton);
		newAccountButton.setBounds(new Rectangle(new Point(10, 420), newAccountButton.getPreferredSize()));

		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				deleteButtonClicked(e);
			}
		});
		contentPane.add(deleteButton);
		deleteButton.setBounds(new Rectangle(new Point(newAccountButton.getX()+newAccountButton.getWidth()+10, 420), deleteButton.getPreferredSize()));

		//---- label12 ----
		label12.setText("Members?");
		contentPane.add(label12);
		label12.setBounds(230, 365, 83, 16);
		contentPane.add(checkBox1);
		checkBox1.setBounds(new Rectangle(new Point(347, 365), checkBox1.getPreferredSize()));

		{
			// compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < contentPane.getComponentCount(); i++) {
				Rectangle bounds = contentPane.getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = contentPane.getInsets();
			preferredSize.width += insets.right+5;
			preferredSize.height += insets.bottom+5;
			contentPane.setMinimumSize(preferredSize);
			contentPane.setPreferredSize(preferredSize);
		}
		pack();
		setLocationRelativeTo(getOwner());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}



	private HashMap<String, Account> accounts = new HashMap<>();
	private HashMap<String, DreamLabel> labels = new HashMap<>();
	private HashMap<String, Process> procs = new HashMap<>();
	private String seperator = "\\|@\\|";
	private DefaultListModel listModel = new DefaultListModel();
	private JScrollPane scrollPane1;
	private DreamList<String> accountsList;
	private DreamButton saveButton;
	private DreamButton launchButton;
	private DreamButton exportButton;
	private DreamButton newAccountButton;
	private DreamButton deleteButton;
	private DreamLabel label1;
	private DreamLabel label2;
	private DreamLabel label3;
	private DreamLabel label4;
	private DreamLabel label5;
	private DreamLabel label6;
	private DreamLabel label7;
	private DreamLabel label8;
	private DreamLabel label9;
	private DreamLabel label10;
	private DreamLabel label11;
	private DreamLabel label12;
	private DreamTextField nicknameTextField;
	private DreamTextField scriptTextField;
	private DreamTextField emailTextField;
	private DreamTextField passwordTextField;
	private DreamTextField pinTextField;
	private DreamTextField proxyIpTextField;
	private DreamTextField proxyPortTextField;
	private DreamTextField proxyUsernameTextField;
	private DreamTextField proxyPasswordTextField;
	private DreamTextField launchParamsTextField;
	private DreamTextField javaParamsTextField;
	private JCheckBox checkBox1;

	public static void main(String[] args) {
		try {
			new AccountManager().setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
