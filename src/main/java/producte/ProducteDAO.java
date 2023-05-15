package producte;

import shared.InfrastructureError;

import java.util.ArrayList;

public interface ProducteDAO {
    public void createProducte(Producte p) throws InfrastructureError;
    public Producte readProducte(String codiProducte) throws InfrastructureError;
    public ArrayList<Producte> readProductes() throws InfrastructureError; // public Map<String, Producte> readProductes();
    public void updateProducte(Producte p) throws InfrastructureError; // també li podríem passar dos paràmetres (el producte a actualitzar i les dades a actualitzar)
    public Producte deleteProducte(Producte p) throws InfrastructureError; // public void deleteProducte(String codiProducte) throws SQLException;
}
