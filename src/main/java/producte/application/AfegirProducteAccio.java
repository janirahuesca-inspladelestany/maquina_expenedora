package producte.application;

import producte.domain.Producte;
import producte.domain.ProducteDAO;
import producte.domain.ProducteExistentError;
import shared.ApplicationError;

public class AfegirProducteAccio {
    public static void run(ProducteDAO producteDAO, Producte producte) throws ApplicationError {
        // Creem un producte auxiliar i li assignem el valor del nou producte que hem creat
        Producte producteLlegit = producteDAO.readProducte(producte.getCodiProducte());

        // Comprovem si el producte que es vol crear ja existeix.
        // Si ja existeix, informem a l'usuari i donem a triar dues opcions (actualitzar el producte o descartar els canvis):
        if (producteLlegit != null) {
            throw new ProducteExistentError(String.format("El producte amb el codi %s ja existeix", producte.getCodiProducte()));
        }

        // Guardem el producte p a la BD:
        producteDAO.createProducte(producte);
    }
}
