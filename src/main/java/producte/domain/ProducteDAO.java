package producte.domain;

import shared.ApplicationError;

import java.util.ArrayList;

public interface ProducteDAO {
    public void createProducte(Producte p) throws ApplicationError;
    public Producte readProducte(String codiProducte) throws ApplicationError;
    public ArrayList<Producte> readProductes() throws ApplicationError; // public Map<String, Producte> readProductes();
    public void updateProducte(Producte p) throws ApplicationError; // també li podríem passar dos paràmetres (el producte a actualitzar i les dades a actualitzar)
    public Producte deleteProducte(Producte p) throws ApplicationError; // public void deleteProducte(String codiProducte) throws SQLException;
}
