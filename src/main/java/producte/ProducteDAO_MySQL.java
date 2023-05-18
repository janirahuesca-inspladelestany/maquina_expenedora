package producte;

import shared.ApplicationError;
import shared.InfrastructureError;

import java.sql.*;
import java.util.ArrayList;

public class ProducteDAO_MySQL implements ProducteDAO {

    private Connection conn; // o podríem tenir la referència a la Connection pool
    private static final String STATEMENT_SELECT_ONE_ITEM = "SELECT * FROM producte WHERE codi_producte=?";
    private static final String STATEMENT_SELECT_ALL = "SELECT * FROM producte";
    private static final String STATEMENT_INSERT = "INSERT INTO producte VALUES (?,?,?,?,?)";
    private static final String STATEMENT_DELETE = "DELETE FROM producte WHERE codi_producte=?";
    private static final String STATEMENT_UDPATE = "UPDATE producte SET codi_producte=?, nom=?, descripcio=?, " +
            "preu_compra=?, preu_venta=? WHERE codi_producte=?";


    public ProducteDAO_MySQL(Connection connection) {
        conn = connection;
    }

    @Override
    public void createProducte(Producte p) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_INSERT);

            ps.setString(1, p.getCodiProducte());
            ps.setString(2, p.getNom());
            ps.setString(3, p.getDescripcio());
            ps.setFloat(4, p.getPreuCompra());
            ps.setFloat(5, p.getPreuVenta());

            int rowCount = ps.executeUpdate();
        } catch (SQLException exception) {
            throw new InfrastructureError("Error afegint producte a la base de dades.");
        }
    }

    @Override
    public Producte readProducte(String codiProducte) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_SELECT_ONE_ITEM);
            ps.setString(1, codiProducte);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Producte p = new Producte();
                p.setCodiProducte(rs.getString("codi_producte"));
                p.setNom(rs.getString("nom"));
                p.setDescripcio(rs.getString("descripcio"));
                p.setPreuCompra(rs.getFloat("preu_compra"));
                p.setPreuVenta(rs.getFloat("preu_venta"));
                return p;
            }
            return null;
        } catch (SQLException exception) {
            throw new InfrastructureError("Error obtenint producte de la base de dades.");
        }
    }

    @Override
    public ArrayList<Producte> readProductes() throws ApplicationError {
        ArrayList<Producte> llistaProductes = new ArrayList<Producte>(); //Llista per guardar els productes que es retornin de la consulta
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_SELECT_ALL); //Serveix per fer una consulta
            ResultSet rs = ps.executeQuery();  //Serveix per executar la consulta i guardar-la a un ResultSet que guarda un conjunt de dades
            while (rs.next()) { //S'executara el while m'entre hi hagi contingut al ResultSet
                Producte p = new Producte(); // Creo producte
                p.setCodiProducte(rs.getString("codi_producte")); // Carrego les dades al ResultSet
                p.setNom(rs.getString("nom"));
                p.setDescripcio(rs.getString("descripcio"));
                p.setPreuCompra(rs.getFloat("preu_compra"));
                p.setPreuVenta(rs.getFloat("preu_venta"));
                llistaProductes.add(p); // Afegeixo el producte a la llista
            }
            return llistaProductes;
        } catch (SQLException exception) {
            throw new InfrastructureError("Error obtenint productes de la base de dades.");
        }
    }

    @Override
    public void updateProducte(Producte p) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_UDPATE);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescripcio());
            ps.setFloat(3, p.getPreuCompra());
            ps.setFloat(4, p.getPreuVenta());
            ps.setString(5, p.getCodiProducte());
            int rowCount = ps.executeUpdate();
        } catch (SQLException exception) {
            throw new InfrastructureError("Error actualitzant producte de la base de dades.");
        }
    }

    @Override
    public Producte deleteProducte(Producte p) throws InfrastructureError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_DELETE);
            ps.setString(1, p.getCodiProducte());
            int rowCount = ps.executeUpdate();
            return p;
        } catch (SQLException exception) {
            throw new InfrastructureError("Error eliminant producte de la base de dades.");
        }
    }
}
