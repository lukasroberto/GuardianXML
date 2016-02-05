package com.winksys.fortress.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.winksys.fortress.IServerContext;
import com.winksys.fortress.ui.vo.MensagemVO;

public class MainUI extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8301524497209907040L;
	private ArrayList<MensagemVO> mensagens = new ArrayList<MensagemVO>();
	private IServerContext ctx;
	
	public MainUI(final IServerContext ctx) {
		this.ctx = ctx;
		setTitle("Gateway Guardiancar");
		setSize(800, 600);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
				
		final JTable table = new JTable();
		table.setModel(new TableModel() {
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				
			}
			
			@Override
			public void removeTableModelListener(TableModelListener l) {
				
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
			
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				MensagemVO m = mensagens.get(rowIndex);
				switch (columnIndex) {
				case 0:
					return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(m.getData());
				case 1:
					return m.getTerminal();
				case 2:
					return m.getMensagem();
							
				}
				return null;
			}
			
			@Override
			public int getRowCount() {
				return mensagens.size();
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				switch (columnIndex) {
				case 0:
					return "Data";
				case 1: 
					return "Terminal";
				case 2:
					return "Mensagem";
				}
				return null;
			}
			
			@Override
			public int getColumnCount() {
				return 3;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}
			
			@Override
			public void addTableModelListener(TableModelListener l) {
				
			}
		});
		
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(new JScrollPane(table), BorderLayout.CENTER);
		
		final JLabel fid = new JLabel();
		final JLabel rid = new JLabel();
		final JLabel lrc = new JLabel();
		final JLabel lfc = new JLabel();
		
		JPanel ids = new JPanel();
		ids.setLayout(new FlowLayout());
		ids.add(fid);
		ids.add(rid);
		ids.add(lrc);
		ids.add(lfc);
		
		main.add(ids, BorderLayout.NORTH);
		
		setContentPane(main);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						List<MensagemVO> mensagens = ctx.getMensagens();
						MainUI.this.mensagens.addAll(mensagens);
											
						while (MainUI.this.mensagens.size() > 100) {
							MainUI.this.mensagens.remove(0);
						}
						
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								fid.setText("NID: " + MainUI.this.ctx.getFid());
								rid.setText("RID: " + MainUI.this.ctx.getRid());
								
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								if (MainUI.this.ctx.getLastFwdCheck() != null) {
									lfc.setText("Ultimo envio: " + sdf.format(MainUI.this.ctx.getLastFwdCheck()));
								}
								if (MainUI.this.ctx.getLastRetCheck() != null) {
									lrc.setText("Ultima mensagem recebida: " + sdf.format(MainUI.this.ctx.getLastRetCheck()));
								}
								SwingUtilities.updateComponentTreeUI(MainUI.this);
							}
							
						});
						
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {}
			}
			
		}).start();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
	
}
