package Practica_6;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


public class VentanaDatos extends JFrame{
	
	private DataSetMunicipios datosMunis;
	private JLabel lblSuperior;
	private MiJTree tree;
	private DefaultTreeModel modeloTree;
	private DefaultMutableTreeNode raiz;
	private JButton btnInsercion;
	private JButton btnBorrado;
	private JButton btnOrden;
	private JTable tablaDatos;
	private MiTableModel modeloDatos;
	private JPanel pnlDerecha;
	private int criterioOrden;
	
	public VentanaDatos(JFrame ventOrigen) {
		setSize(1000, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		lblSuperior = new JLabel("");
		this.add(lblSuperior, BorderLayout.NORTH);
		
		tree = new MiJTree();
		this.add(new JScrollPane(tree), BorderLayout.WEST);
		
		tablaDatos = new JTable();
		this.add(new JScrollPane(tablaDatos),BorderLayout.CENTER);
		
		pnlDerecha = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				dibujarGrafico((Graphics2D) g);
				{
					setOpaque(true);
					setPreferredSize(new Dimension(300, 600));
				}
			}
		};
		this.add(pnlDerecha, BorderLayout.EAST);
		
		JPanel pnlInferior = new JPanel();
		this.add(pnlInferior, BorderLayout.SOUTH);
		
		btnInsercion = new JButton("Insertar");
		pnlInferior.add(btnInsercion);
		btnBorrado = new JButton("Borrar");
		pnlInferior.add(btnBorrado);
		btnOrden = new JButton("Ordenar");
		pnlInferior.add(btnOrden);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				ventOrigen.setVisible(false);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				ventOrigen.setVisible(true);
			}
			
		});;
		
		
		btnInsercion.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int filaSel = tablaDatos.getSelectedRow();
				if(filaSel >= 0) {
					String provinciaSel = (String) tablaDatos.getValueAt(filaSel, 4);
			        String autonomiaSel = (String) tablaDatos.getValueAt(filaSel, 5);
			        Municipio nuevo = new Municipio(datosMunis.getListaMunicipios().size()+1, " ", 50000, provinciaSel, autonomiaSel);
			        datosMunis.anyadir(nuevo);
			        int filaNueva = modeloDatos.getRowCount()+1;
			        ((MiTableModel) modeloDatos).anyadeFila(filaNueva);
			        ((MiTableModel) modeloDatos).setListaMunicipios(datosMunis.getMunicipiosEnProvincia(provinciaSel));
			        tablaDatos.repaint();
				}
				
			}
			
		});
		
		btnBorrado.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int filaSel = tablaDatos.getSelectedRow();
				if(filaSel >= 0) {
					String provinciaSel = (String) tablaDatos.getValueAt(filaSel, 4);
					int option = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres borrar este municipio?", "Confirmación de Borrado", JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION) {
						((MiTableModel) modeloDatos).borrarFila(filaSel);
			            ((MiTableModel) modeloDatos).setListaMunicipios(datosMunis.getMunicipiosEnProvincia(provinciaSel));
			            tablaDatos.setModel(modeloDatos);
			            tablaDatos.repaint();
					}
				}else {
					JOptionPane.showMessageDialog(null, "Selecciona un municipio para borrar.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		btnOrden.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String provincia = tree.getSelectionPath().getLastPathComponent().toString();
				List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provincia);
				
				if(criterioOrden == 1) {
					municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
					criterioOrden = 0;
				}else {
					municipiosEnProvincia.sort(Comparator.comparingInt(Municipio::getHabitantes).reversed());
					criterioOrden = 1;
				}
				((MiTableModel) tablaDatos.getModel()).setListaMunicipios(municipiosEnProvincia);
				tablaDatos.repaint();
			}
		});
		
	}
	
	private String obtenerProvinciaSeleccionada() {
		TreePath path = tree.getSelectionPath();
		if(path != null) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object userObject = selectedNode.getUserObject();
			if(userObject instanceof String) {
				return (String) userObject;
			}
		}
		return null;
	}
	
	
	//PANEL
	private void dibujarGrafico(Graphics2D grafico) {
		int anchoPnlDerecha = pnlDerecha.getWidth();
        int altoPnlDerecha = pnlDerecha.getHeight();
        
        if(tree.getSelectionPath() != null) {
        	DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            String provinciaSel = "";
            
            if(nodoSel != null && nodoSel.isLeaf()) {
            	provinciaSel = (String) nodoSel.getUserObject();
            }
            
            List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provinciaSel);
            int poblacionProvincia = 0;
            int poblacionTotal = 0;
            
            for(Municipio m: municipiosEnProvincia) {
            	poblacionProvincia += m.getHabitantes();
            }
            
            for(Municipio m: datosMunis.getListaMunicipios()) {
            	poblacionTotal += m.getHabitantes();
            }
            
            int anchoBarraProvincia = (anchoPnlDerecha / 2) - 20;
            int alturaMax = altoPnlDerecha - 40;
            double porcentajeProvincia = (double) poblacionProvincia / poblacionTotal;
            int alturaBarraProvincia = (int) (porcentajeProvincia * alturaMax);
            int xBarraProvincia = 10;
            int yBarraProvincia = altoPnlDerecha - alturaBarraProvincia;

            grafico.setColor(Color.GREEN);
            grafico.fillRect(xBarraProvincia, yBarraProvincia, anchoBarraProvincia, alturaBarraProvincia);
            grafico.setColor(Color.BLACK);
            int ySeparador = yBarraProvincia;
            
            for(Municipio m: municipiosEnProvincia) {
            	int alturaSeparador = (int) ((double) m.getHabitantes() / poblacionProvincia * alturaBarraProvincia);
                grafico.drawLine(xBarraProvincia, ySeparador, xBarraProvincia + anchoBarraProvincia, ySeparador);
                grafico.drawLine(xBarraProvincia, ySeparador + alturaSeparador, xBarraProvincia + anchoBarraProvincia, ySeparador + alturaSeparador);
                ySeparador += alturaSeparador;
            }
            
            int anchoBarraEstado = (anchoPnlDerecha / 2) - 20;
            int xBarraEstado = xBarraProvincia + anchoBarraProvincia + 10;
            int alturaBarraEstado = alturaMax;
            int yBarraEstado = altoPnlDerecha - alturaBarraEstado;

            grafico.setColor(Color.BLUE);
            grafico.fillRect(xBarraEstado, yBarraEstado, anchoBarraEstado, alturaBarraEstado);

            grafico.setColor(Color.BLACK);    
        }

	}
	
	
	//TREE
	public void setTree(DataSetMunicipios datosMunis) {
		this.datosMunis = datosMunis;
		
		raiz = new DefaultMutableTreeNode("Municipios");
		modeloTree = new DefaultTreeModel(raiz);
		tree.setModel(modeloTree);
		tree.setEditable(false);
		crearNodos(datosMunis.getListaMunicipios(), raiz);
		rendererProvincia(tree, datosMunis);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				if(tp != null) {
					DefaultMutableTreeNode nodoSel = (DefaultMutableTreeNode) tp.getLastPathComponent();
					if(nodoSel != null && nodoSel.isLeaf()) {
						String provinciaSel = (String) nodoSel.getUserObject();
						CargaDatosTabla(provinciaSel);
						rendererProvincia(tree, datosMunis);
						pnlDerecha.repaint();
					}
				}
			}
			
		});
	}
	private void rendererProvincia(JTree treeDatos, DataSetMunicipios datosMunis) {
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				// TODO Auto-generated method stub
				Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if(value instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					if(datosMunis.getListaProvincias().contains(node.getUserObject())) {
						String provincia = (String) node.getUserObject();
						int habitantes = 0;
						for(int i=0; i<datosMunis.getMunicipiosEnProvincia(provincia).size(); i++) {
							habitantes += datosMunis.getMunicipiosEnProvincia(provincia).get(i).getHabitantes();
						}
						JProgressBar progressBar = new JProgressBar();
		                progressBar.setMaximum(5000000);
		                progressBar.setValue(habitantes);
		                
		                JPanel pnl = new JPanel(new BorderLayout());
		                pnl.add(new JLabel(provincia), BorderLayout.WEST);
		                pnl.add(progressBar, BorderLayout.EAST);
		                return pnl;	
		                
					}
				}
				return c;
			}
			
		});
	}
	
	public static class MiJTree extends JTree{
		public void expandir(TreePath path, boolean estado) {
			setExpandedState(path, estado);
		}
	}
	
	private DefaultMutableTreeNode crearNodo(Object dato, DefaultMutableTreeNode nodoPadre, int posi) {
		DefaultMutableTreeNode nodo1 = new DefaultMutableTreeNode(dato);
		modeloTree.insertNodeInto(nodo1, nodoPadre, posi);
		tree.expandir(new TreePath(nodo1.getPath()), true);
		return nodo1;
	}
	
	private void crearNodos(List<Municipio> municipios, DefaultMutableTreeNode nodoPadre) {
		ArrayList<String> autonomias = new ArrayList<String>();
		for(Municipio m: municipios) {
			String autonomia = m.getAutonomia();
			if(!autonomias.contains(autonomia)) {
				DefaultMutableTreeNode nodoAutonomia = crearNodo(autonomia, nodoPadre, autonomias.size());
				autonomias.add(autonomia);
				
				ArrayList<String> provincias = new ArrayList<String>();
				for(Municipio mu: municipios) {
					if(mu.getAutonomia().equals(autonomia)) {
						String provincia = mu.getProvincia();
						if(!provincias.contains(provincia)) {
							crearNodo(provincia, nodoAutonomia, provincias.size());
							provincias.add(provincia);
						}
					}
				}
			}
		}
	}
	

	
	//TABLA DATOS
	private void CargaDatosTabla(String provinciaSel) {
		List<Municipio> municipiosEnProvincia = datosMunis.getMunicipiosEnProvincia(provinciaSel);
		municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
		modeloDatos = new MiTableModel(municipiosEnProvincia);
		tablaDatos.setModel(modeloDatos);
		
		tablaDatos.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			private JProgressBar pbPoblacion = new JProgressBar(50000, 5000000);
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
				if(column == 3) {
					int poblacion = (Integer) value;
					double porcentaje = (double) (poblacion - 50000) / (5000000 - 50000);
			
					int red = (int) (255 * porcentaje);
					int green = (int) (255 * (1 - porcentaje));

					pbPoblacion.setValue(poblacion);
					pbPoblacion.setForeground(new Color(red, green, 0));
			
					return pbPoblacion;
				}else {
					return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				}
			}

		});
		
		tablaDatos.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				boolean municipioEstaSel;
				int filaEnTabla = tablaDatos.rowAtPoint(e.getPoint());
				int colEnTabla = tablaDatos.columnAtPoint(e.getPoint());
				if(colEnTabla == 1 && filaEnTabla >= 0) {
					Municipio municipioSel = municipiosEnProvincia.get(filaEnTabla);
					municipioEstaSel = true;
					colorearCelda(municipioSel, municipioEstaSel, municipiosEnProvincia);
				}else {
					municipioEstaSel = false;
					colorearCelda(null, municipioEstaSel, null);
				}
			}
			
		});
		
	}
	
	private void colorearCelda(Municipio municipioSel, boolean municipioEstaSel, List<Municipio> municipiosEnProvincia ) {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if(municipioEstaSel && column == 1) {
					Municipio municipioActual = municipiosEnProvincia.get(row);
					
					if(municipioActual.equals(municipioSel)) {
						c.setBackground(Color.WHITE);
					}else if(municipioActual.getHabitantes() > municipioSel.getHabitantes()){
						c.setBackground(Color.RED);
					}else if(municipioActual.getHabitantes() < municipioSel.getHabitantes()) {
						c.setBackground(Color.GREEN);
					}else {
						c.setBackground(Color.WHITE);
					}
				}else {
					c.setBackground(table.getBackground());
				}
				return c;
			
			}
		};
		
		tablaDatos.getColumnModel().getColumn(1).setCellRenderer(renderer);
		tablaDatos.repaint();
	}
	
	private class MiTableModel implements TableModel{
		
		private final Class<?>[] CLASES_COLS = {Integer.class, String.class,  Integer.class, String.class, String.class};
		private List<Municipio> listaMunicipios;
		
		private MiTableModel(List<Municipio> municipios) {
			this.listaMunicipios = municipios;
		}
		
		public void setListaMunicipios(List<Municipio> municipiosEnProvincia) {
			this.listaMunicipios = municipiosEnProvincia;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			// TODO Auto-generated method stub
			return CLASES_COLS[columnIndex];
		}
		
		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 6;
		}
		
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return listaMunicipios.size();
		}
		
		private static final String[] cabeceras = {"Código", "Nombre", "Habitantes", "Provincia", "Autonomia"};

		@Override
		public String getColumnName(int columnIndex) {
			// TODO Auto-generated method stub
			return cabeceras[columnIndex];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			Municipio m = listaMunicipios.get(rowIndex);
			switch(columnIndex) {
			case 0:
				return m.getCodigo();
			case 1:
				return m.getNombre();
			case 2:
				return m.getHabitantes();
			case 3:
				return m.getProvincia();
			case 4:
				return m.getAutonomia();
			default:
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			if(columnIndex == 1 || columnIndex == 2) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			Municipio m = listaMunicipios.get(rowIndex);
			switch(columnIndex) {
			case 0:
				m.setCodigo((Integer) aValue);
				break;
			case 1:
				m.setNombre((String) aValue);
				break;
			case 2:
				try {
					m.setHabitantes((Integer) aValue);
					tablaDatos.repaint();
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Nº de habitantes erróneo");
				}
			
				break;
			case 3:
				m.setProvincia((String) aValue);
				break;
			case 4:
				m.setAutonomia((String) aValue);
				break;
		
			}
			
		}
		
		ArrayList<TableModelListener> listaEsc = new ArrayList<>();

		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			listaEsc.add(l);
			
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			listaEsc.remove(l);
		}
		
		public void fireTableChanged(TableModelEvent e) {
			for(TableModelListener l: listaEsc) {
				l.tableChanged(e);
			}
		}
		
		public void borrarFila(int fila) {
			if(fila >= 0 && fila < listaMunicipios.size()) {
				Municipio municipioBorrado = listaMunicipios.remove(fila);
				datosMunis.quitar(municipioBorrado.getCodigo());
				 fireTableChanged(new TableModelEvent(modeloDatos, fila, datosMunis.getListaMunicipios().size()));
			}
		}
		
		public void anyadeFila(int fila) {
			fireTableChanged(new TableModelEvent(modeloDatos, fila, datosMunis.getListaMunicipios().size()));
			tablaDatos.repaint();
		}
		
	}

	
}
