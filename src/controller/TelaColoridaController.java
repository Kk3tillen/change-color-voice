package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.ControladaPorVoz;
import model.ThreadVoz;
import view.TelaColorida;

public class TelaColoridaController implements ActionListener, ControladaPorVoz{
	public TelaColorida tela;
	public JButton btnLigaVoz;
	public JPanel panel;
	
	public TelaColoridaController(TelaColorida tela){
		this.tela = tela;
		this.btnLigaVoz = tela.getBtnLigaVoz();
		this.panel = tela.getPanel();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		mudarCor();
	}
	
	public void mudarCor() {
		btnLigaVoz.setText("Carregando...");
		ThreadVoz threadVoz = new ThreadVoz(this, btnLigaVoz);
		threadVoz.execute();
	}
	
	@Override
	public void executaComandoPorVoz(String oQueFoiFalado) {
		if(oQueFoiFalado.equals("change color to blue"))
			panel.setBackground(new Color(0, 0, 255));
		else
		if(oQueFoiFalado.equals("change color to red"))
			panel.setBackground(new Color(255, 0, 0));
		else
		if(oQueFoiFalado.equals("change color to green"))
			panel.setBackground(new Color(0, 255, 0));
	}

}
