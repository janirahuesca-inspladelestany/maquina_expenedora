package daos;
import model.Producte;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ProducteDAO {
    public void createProducte(Producte p) throws SQLException;
    public Producte readProducte() throws SQLException;
    public ArrayList<Producte> readProductes() throws SQLException; // public Map<String, Producte> readProductes();
    public void updateProducte(Producte p) throws SQLException; // també li podríem passar dos paràmetres (el producte a actualitzar i les dades a actualitzar)
    public Producte deleteProducte(Producte p) throws SQLException; // public void deleteProducte(String codiProducte) throws SQLException;
}
