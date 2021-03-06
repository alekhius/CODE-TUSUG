package CONTROLLERS;

import GUI.AutobusGUI;
import GUI.RootGUI;
import Servicios.Fachada;
import static Validacion.Validador.validaIngreso;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Alekhius
 */
public class Autobus {

    public AutobusGUI ui;
    private SQLAutobus bd;
    private String matricula, id, marca, num_eco, url_img;
    private int km, asientos;

    public Autobus(AutobusGUI ui) {
        this.url_img = "src/imagenes/autobu.png";
        this.ui = ui;
        this.bd = new SQLAutobus();
    }

    public void ingresarAutobus() {
        cargarvariables();
        try {
            bd.ingresarAutobus(matricula, id, marca, num_eco, km, asientos, url_img);
        } catch (SQLException ex) {
            System.err.println(
                    ex.getErrorCode());
            Logger.getLogger(Autobus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void modificarAutobus() {
        if (validaIngreso(ui.txt_marca, ui.txt_No_Eco, ui.txt_Km, ui.txt_matricula, ui.txt_asientos)) {

            cargarvariables();
            try {
                bd.update(matricula, marca, num_eco, km, asientos);
            } catch (SQLException ex) {
                System.err.println(
                        ex.getErrorCode());
                Logger.getLogger(Autobus.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Error: Debe llenar todos los campos");
        }
    }

    public void cargarvariables() {
        matricula = ui.txt_matricula.getText().toLowerCase();
       id = "";//ui.txt_id.getText().toLowerCase();
        marca = ui.txt_marca.getText().toLowerCase();
        num_eco = ui.txt_No_Eco.getText().toLowerCase();
        km = Integer.valueOf(ui.txt_Km.getText());
        asientos = Integer.valueOf(ui.txt_asientos.getText());
        //Logger.getLogger("Hi").log(Level.SEVERE, " - Linea 55 - ");
        url_img = "src/imagenes/autobu.png";//seleccionarImg();
    }

    public String seleccionarImg() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF", "jpg", "gif");
        ui.url_img.setFileFilter(filter);
        ui.url_img.showOpenDialog(ui);
        String f = ui.url_img.getSelectedFile().getAbsolutePath();
        return f;
    }

    public String[] obtenerAutobus(String m) {
        String registro[] = bd.obtenerRegistro(m);
        return registro;
    }

    public ArrayList<String> obtenerlista() {

        ArrayList lista = bd.listaAutobuses();

        return lista;
    }

    public void actualizarLista() {
        cargarLista(ui.list);
        ui.txt_marca.setText(null);
        ui.txt_No_Eco.setText(null);
        ui.txt_Km.setText(null);
        ui.txt_matricula.setText(null);
        ui.txt_asientos.setText(null);
    }

    public void borrarAutobus() {
        SQLAutobus sql = new SQLAutobus();
        try {
            sql.borrarAutobusBy(ui.txt_matricula.getText());
        } catch (SQLException ex) {
            Logger.getLogger(Autobus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void putImageProfile(String path) {
        // Replace los simbolos        '\'       por '/'
        try{
        String Path = path.replace('\u005C\u005C' ,  '\u002F');
        ImageIcon fot = new ImageIcon(Path);
        Icon icon = new ImageIcon(fot.getImage().getScaledInstance(ui.lb_imagen_autobus.getWidth(), ui.lb_imagen_autobus.getHeight(), Image.SCALE_DEFAULT));
        //Icon icon = new ImageIcon(Path);
        
        ui.lb_imagen_autobus.setIcon(icon);
        }catch(NullPointerException ex){
            //Logger.getLogger(Autobus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void evtCargarImagen(){        
        try{
            // Obtener la ruta de la imagen
            String absPathImg = Fachada.getSelectedFileImage();
            // Cargar las variables
            matricula = ui.txt_matricula.getText().toLowerCase();
            //System.err.println(matricula);
            // Actualizar dato en la Base de datos
            // Preparar Consulta
            Connection conn = Conexion.getConexion();
            PreparedStatement pstm = conn.prepareStatement("UPDATE sistemaTusug.autobus SET url_img = ? WHERE lower(matricula) = ? ");
            pstm.setString(1, absPathImg);
            pstm.setString(2, matricula);
            pstm.execute();
            pstm.close();
            //conn.close();
            // Mostrar la img en el Label
            putImageProfile(absPathImg);
        } catch (SQLException e) {
            System.out.println("Error 404: Conexion refusada o algun pedo así");
        } catch (NullPointerException ex) {
            Logger.getLogger(Autobus.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void cargarLista(JList l) {
        DefaultListModel modelo = new DefaultListModel();
        ArrayList<String> lista = obtenerlista();
        for (int i = 0; i < lista.size(); i++) {
            modelo.addElement(lista.get(i).toUpperCase());
        }
        l.setModel(modelo);
    }

    public void evtAgregarAutobus() {
        if (validaIngreso(ui.txt_marca, ui.txt_No_Eco, ui.txt_Km, ui.txt_matricula, ui.txt_asientos)) {
            ingresarAutobus();
            ui.txt_marca.setText(null);
            ui.txt_No_Eco.setText(null);
            ui.txt_Km.setText(null);
            ui.txt_matricula.setText(null);
            ui.txt_asientos.setText(null);
        } else {
            System.err.println("Error: Debe llenar todos los campos");
        }
    }
}
