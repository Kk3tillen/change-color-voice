package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.TelaColoridaController;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;

public class TelaColorida extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JButton btnLigaVoz;	

	public static void main(String[] args) {
		TelaColorida frame = new TelaColorida();
		frame.setVisible(true);
	}

	public TelaColorida() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		panel = new JPanel();
		panel.setBackground(new Color(177, 177, 177));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(null);
		setContentPane(panel);		
		
		btnLigaVoz = new JButton("START");
		btnLigaVoz.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnLigaVoz.setBounds(160, 120, 115, 30);
		TelaColoridaController telaCtrl = new TelaColoridaController(this);
		btnLigaVoz.addActionListener(telaCtrl);
		
		panel.add(btnLigaVoz);
	}

	public JButton getBtnLigaVoz() {
		return btnLigaVoz;
	}

	public void setBtnLigaVoz(JButton btnLigaVoz) {
		this.btnLigaVoz = btnLigaVoz;
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
}
