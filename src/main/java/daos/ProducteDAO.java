package daos;
import model.Producte;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ProducteDAO {
    public void createProducte(Producte p) throws SQLException;
    public Producte readProducte(String codiProducte) throws SQLException; // Abans no se li passava cap paràmetre
    public ArrayList<Producte> readProductes() throws SQLException; // public Map<String, Producte> readProductes();
    public void updateProducte(Producte p) throws SQLException; // també li podríem passar dos paràmetres (el producte a actualitzar i les dades a actualitzar)
    public Producte deleteProducte(Producte p) throws SQLException; // public void deleteProducte(String codiProducte) throws SQLException;
}
