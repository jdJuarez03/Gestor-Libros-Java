package biblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Biblioteca {
    // Atributos de la clase
    private ArrayList<Libro> libros;
    private JFrame ventanaPrincipal;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private JTextField txtTitulo, txtAutor, txtEditorial;
    private JButton btnAgregar, btnEliminar, btnBuscar, btnInstrucciones, btnExportar, btnEstado;
    private int filaEnEdicion = -1;
    private JButton btnEditar;

    // Clase interna para representar Libro
    private class Libro {
        String titulo;
        String autor;
        String editorial;
        boolean prestado;

        Libro(String titulo, String autor, String editorial) {
            this.titulo = titulo;
            this.autor = autor;
            this.editorial = editorial;
            this.prestado = false;
        }
    }

    // Constructor
    public Biblioteca() {
        libros = new ArrayList<>();
        crearInterfaz();
        cargarDatosGuardados();
    }

    // Método para crear la interfaz
    private void crearInterfaz() {
    ventanaPrincipal = new JFrame("📚 GESTOR DE LIBROS");
    ventanaPrincipal.setSize(900, 500);
    ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventanaPrincipal.setLayout(new BorderLayout());

    //PANEL LATERAL (IZQUIERDA)
    JPanel panelLateral = new JPanel();
    panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
    panelLateral.setPreferredSize(new Dimension(250, 0));
    panelLateral.setBackground(new Color(245, 245, 245));
    panelLateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Título del Panel Lateral
    JLabel lblHeader = new JLabel("NUEVO REGISTRO");
    lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblHeader.setForeground(new Color(100, 100, 100));
    lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

    txtTitulo = new JTextField();
    txtAutor = new JTextField();
    txtEditorial = new JTextField();
    
    Dimension dimCaja = new Dimension(Integer.MAX_VALUE, 30); 
    Dimension dimBoton = new Dimension(Integer.MAX_VALUE, 40);
    Font fuenteInputs = new Font("Segoe UI", Font.PLAIN, 13);
    
        txtTitulo = new JTextField();
        txtTitulo.setMaximumSize(dimCaja);
        txtTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTitulo.setFont(fuenteInputs);
        
        txtAutor = new JTextField();
        txtAutor.setMaximumSize(dimCaja);
        txtAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtAutor.setFont(fuenteInputs);
        
        txtEditorial = new JTextField();
        txtEditorial.setMaximumSize(dimCaja);
        txtEditorial.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEditorial.setFont(fuenteInputs);

    // Agregamos elementos al panel
    panelLateral.add(lblHeader);
    panelLateral.add(Box.createVerticalStrut(20));

    panelLateral.add(crearLabel("Título del Libro:"));
    panelLateral.add(txtTitulo);
    panelLateral.add(Box.createVerticalStrut(10));

    panelLateral.add(crearLabel("Autor:"));
    panelLateral.add(txtAutor);
    panelLateral.add(Box.createVerticalStrut(10));

    panelLateral.add(crearLabel("Editorial:"));
    panelLateral.add(txtEditorial);
    panelLateral.add(Box.createVerticalStrut(30));

    // --- BOTONES EN EL LATERAL ---
    // Botón principal (Agregar)
    btnAgregar = new JButton("AGREGAR LIBRO");
    btnAgregar.setBackground(new Color(33, 150, 243));
    btnAgregar.setForeground(Color.GRAY); 
    btnAgregar.setFocusPainted(false);
    btnAgregar.setMaximumSize(new Dimension(250, 40));
    btnAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
    
    // Botones secundarios
    btnEditar = new JButton("✏️ Editar");
        styleButton(btnEditar);
    btnEstado = new JButton("🔄 Prestar/Devolver");
        styleButton(btnEstado);
    btnEliminar = new JButton("🗑 Eliminar");
        styleButton(btnEliminar);
    btnBuscar = new JButton("🔍 Buscar");
        styleButton(btnBuscar);
    btnExportar = new JButton("📄 Exportar CSV");
        styleButton(btnExportar);
    btnInstrucciones = new JButton("❓ Ayuda");
        styleButton(btnInstrucciones);

    // Botones laterales
    panelLateral.add(btnAgregar);
    panelLateral.add(Box.createVerticalStrut(10));
    panelLateral.add(btnEditar);
    panelLateral.add(Box.createVerticalStrut(10));
    panelLateral.add(btnEstado); 
    panelLateral.add(Box.createVerticalStrut(10));
    panelLateral.add(btnEliminar);
    panelLateral.add(Box.createVerticalStrut(10));
    panelLateral.add(btnBuscar);
    panelLateral.add(Box.createVerticalStrut(10));
    panelLateral.add(btnExportar);
    panelLateral.add(Box.createVerticalGlue()); 
    panelLateral.add(btnInstrucciones);

    // Listeners
    btnAgregar.addActionListener(e -> agregarLibro());
    btnEditar.addActionListener(e -> cargarDatosParaEditar());
    btnEstado.addActionListener(e -> cambiarEstado());
    btnEliminar.addActionListener(e -> eliminarLibro());
    btnBuscar.addActionListener(e -> buscarLibro());
    btnExportar.addActionListener(e -> exportarLibrosACSV());
    btnInstrucciones.addActionListener(e -> mostrarInstrucciones());


    // TABLA (Panel Central)
    String[] columnas = {"Título", "Autor", "Editorial", "Estado"};
    modeloTabla = new DefaultTableModel(columnas, 0);
    
    tablaLibros = new JTable(modeloTabla) {
        public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (!isRowSelected(row)) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
            }
            return c;
        }
    };
    tablaLibros.setRowHeight(25);
    tablaLibros.setShowVerticalLines(false); 
    tablaLibros.getTableHeader().setBackground(Color.WHITE);
    tablaLibros.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    
    JScrollPane scrollTabla = new JScrollPane(tablaLibros);
    scrollTabla.setBorder(BorderFactory.createEmptyBorder()); 
    scrollTabla.getViewport().setBackground(Color.WHITE); 

    ventanaPrincipal.add(panelLateral, BorderLayout.WEST);
    ventanaPrincipal.add(scrollTabla, BorderLayout.CENTER);

    ventanaPrincipal.setLocationRelativeTo(null);
}
    // Método para mostrar la ventana
    public void mostrar() {
        SwingUtilities.invokeLater(() -> {
            ventanaPrincipal.setVisible(true);
        });
    }
    
    // Método para agregar o editar libro
    private void agregarLibro() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String textoEditorial = txtEditorial.getText().trim();

        if (!titulo.isEmpty() && !autor.isEmpty() && !textoEditorial.isEmpty()) {
            
            if (filaEnEdicion == -1) {
                //MODO AGREGAR NUEVO
                Libro nuevoLibro = new Libro(titulo, autor, textoEditorial);
                libros.add(nuevoLibro);
                
                modeloTabla.addRow(new Object[]{
                    nuevoLibro.titulo, 
                    nuevoLibro.autor, 
                    nuevoLibro.editorial, 
                    nuevoLibro.prestado ? "Prestado" : "Disponible"
                });
            } else {
                //MODO EDITAR
                Libro libroEditado = libros.get(filaEnEdicion);
                libroEditado.titulo = titulo;
                libroEditado.autor = autor;
                libroEditado.editorial = textoEditorial; 
                
                // Actualizamos la TABLA
                modeloTabla.setValueAt(titulo, filaEnEdicion, 0);
                modeloTabla.setValueAt(autor, filaEnEdicion, 1);
                modeloTabla.setValueAt(textoEditorial, filaEnEdicion, 2);
                
                // Restaurar interfaz
                filaEnEdicion = -1;
                btnAgregar.setText("AGREGAR LIBRO");
                btnAgregar.setBackground(new Color(231, 76, 60));
            }

            guardarCambios(); 
            
            // Limpiar campos
            txtTitulo.setText("");
            txtAutor.setText("");
            txtEditorial.setText("");
            txtTitulo.requestFocus();
            
        } else {
            JOptionPane.showMessageDialog(ventanaPrincipal, 
                "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosParaEditar() {
        int fila = tablaLibros.getSelectedRow();
        
        if (fila != -1) {
            //Rellenar las cajas de texto con los datos de la fila
            txtTitulo.setText((String) modeloTabla.getValueAt(fila, 0));
            txtAutor.setText((String) modeloTabla.getValueAt(fila, 1));
            txtEditorial.setText((String) modeloTabla.getValueAt(fila, 2));
            
            //Cambiar el estado a "Editando"
            filaEnEdicion = fila;
            
            //Cambiar visualmente el botón principal para avisar al usuario
            btnAgregar.setText("GUARDAR CAMBIOS");
            btnAgregar.setBackground(new Color(39, 174, 96));
            
        } else {
            JOptionPane.showMessageDialog(ventanaPrincipal, 
                "Selecciona un libro de la tabla para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para eliminar libro
    private void eliminarLibro() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada != -1) {
            String editorial = (String) modeloTabla.getValueAt(filaSeleccionada, 2);
            
            // Confirmación antes de borrar
            int confirm = JOptionPane.showConfirmDialog(ventanaPrincipal, 
                    "¿Estás seguro de eliminar el libro con Editorial: " + editorial + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            
            if(confirm == JOptionPane.YES_OPTION) {
                libros.removeIf(libro -> libro.editorial.equals(editorial));
                modeloTabla.removeRow(filaSeleccionada);
                guardarCambios();
            }
        } else {
            JOptionPane.showMessageDialog(ventanaPrincipal, 
                "Selecciona un libro de la tabla para eliminar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para buscar libro
    private void buscarLibro() {
        String textoBusqueda = JOptionPane.showInputDialog(ventanaPrincipal, "Ingrese título o Editorial:");
        
        if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
            List<Libro> resultados = libros.stream()
                .filter(libro -> 
                    libro.titulo.toLowerCase().contains(textoBusqueda.toLowerCase()) || 
                    libro.editorial.contains(textoBusqueda))
                .toList();

            if (!resultados.isEmpty()) {
                JPanel panelResultados = new JPanel();
                panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
                
                for (Libro libro : resultados) {
                    JPanel panelLibro = new JPanel(new GridLayout(4,1));
                    panelLibro.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createEtchedBorder(),
                            BorderFactory.createEmptyBorder(5,5,5,5)
                    ));
                    
                    panelLibro.add(new JLabel("Título: " + libro.titulo));
                    panelLibro.add(new JLabel("Autor: " + libro.autor));
                    panelLibro.add(new JLabel("Editorial: " + libro.editorial));
                    panelLibro.add(new JLabel("Estado: " + (libro.prestado ? "Prestado" : "Disponible")));
                    
                    panelResultados.add(panelLibro);
                    panelResultados.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                JScrollPane scrollResultados = new JScrollPane(panelResultados);
                scrollResultados.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(
                    ventanaPrincipal, 
                    scrollResultados, 
                    "Resultados de la Búsqueda", 
                    JOptionPane.PLAIN_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(ventanaPrincipal, "No se encontraron libros con ese criterio.");
            }
        }
    }
    
    // Método para cambiar el estado (Prestar/Devolver)
    private void cambiarEstado() {
        int filaSeleccionada = tablaLibros.getSelectedRow();
        
        if (filaSeleccionada != -1) {
            // Obtener el libro de la lista
            Libro libro = libros.get(filaSeleccionada);
            
            // Invertir el estado
            libro.prestado = !libro.prestado;
            
            // Actualizar la Tabla Visualmente
            modeloTabla.setValueAt(libro.prestado ? "Prestado" : "Disponible", filaSeleccionada, 3);
            
            // Guardar el cambio en el archivo
            guardarCambios();
            
        } else {
            JOptionPane.showMessageDialog(ventanaPrincipal, 
                "Selecciona un libro para cambiar su estado", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para exportar libros a CSV
    private void exportarLibrosACSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Libros a CSV");
            fileChooser.setSelectedFile(new File("libros_biblioteca.csv"));
            
            int userSelection = fileChooser.showSaveDialog(ventanaPrincipal);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                
                // Asegurar extensión .csv
                if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
                }

                try (PrintWriter writer = new PrintWriter(fileToSave)) {
                    writer.println("Título,Autor,Editorial,Estado");
                    
                    for (Libro libro : libros) {
                        writer.println(String.format("%s,%s,%s,%s", 
                            libro.titulo.replace(",", ";"), 
                            libro.autor.replace(",", ";"), 
                            libro.editorial, 
                            (libro.prestado ? "Prestado" : "Disponible")
                        ));
                    }
                }
                
                JOptionPane.showMessageDialog(ventanaPrincipal, 
                    "Libros exportados exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaPrincipal, 
                "Error al exportar: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para mostrar instrucciones
    private void mostrarInstrucciones() {
        String instrucciones = """
        GESTOR DE LIBROS
        ================

        1. AGREGAR: 
           Llene los campos y presione "Agregar".
           
        2. ELIMINAR: 
           Seleccione una fila en la tabla y presione "Eliminar".

        3. BUSCAR: 
           Busque por coincidencia de nombre Editorial.

        4. EXPORTAR: 
           Guarde su inventario actual en un archivo Excel/CSV.
        """;

        JTextArea areaInstrucciones = new JTextArea(instrucciones);
        areaInstrucciones.setEditable(false);
        areaInstrucciones.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaInstrucciones.setMargin(new Insets(10,10,10,10));

        JOptionPane.showMessageDialog(
            ventanaPrincipal, 
            new JScrollPane(areaInstrucciones), 
            "Ayuda", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Método para crear etiquetas alineadas
private JLabel crearLabel(String texto) {
    JLabel lbl = new JLabel(texto);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lbl.setForeground(Color.GRAY);
    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
    return lbl;
}

// Método para botones secundarios
private void styleButton(JButton btn) {
    btn.setMaximumSize(new Dimension(250, 30));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);
    btn.setBackground(Color.WHITE);
    btn.setFocusPainted(false);
}

//MÉTODOS DE GUARDAR Y CARGAR
    private void guardarCambios() {
        try (PrintWriter writer = new PrintWriter(new File("datos_biblioteca.txt"))) {
            for (Libro libro : libros) {
                writer.println(libro.titulo + ";" + libro.autor + ";" + libro.editorial + ";" + libro.prestado);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    private void cargarDatosGuardados() {
        File archivo = new File("datos_biblioteca.txt");
        if (!archivo.exists()) return;

        try (Scanner scanner = new Scanner(archivo)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(";");
                if (partes.length >= 4) { 
                    Libro libro = new Libro(partes[0], partes[1], partes[2]);
                    libro.prestado = Boolean.parseBoolean(partes[3]);
                    
                    libros.add(libro);
                    
                    modeloTabla.addRow(new Object[]{
                        libro.titulo, 
                        libro.autor, 
                        libro.editorial, 
                        libro.prestado ? "Prestado" : "Disponible"
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }
   
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Biblioteca gestor = new Biblioteca();
            gestor.mostrar();
        });
    }
}