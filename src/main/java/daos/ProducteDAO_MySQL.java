package daos;

import model.Producte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProducteDAO_MySQL implements ProducteDAO {

    private Connection conn = null; // o podríem tenir la referència a la Connection pool
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/expenedora";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "1234";
    private static final String STATEMENT_SELECT_ALL = "SELECT * FROM producte";
    private static final String STATEMENT_INSERT = "INSERT INTO producte VALUES (?,?,?,?,?)";


    public ProducteDAO_MySQL() {
        try {
            Class.forName(DB_DRIVER); // Carreguem el driver
            conn = DriverManager.getConnection(DB_ROUTE, DB_USER, DB_PWD); // Arranquem la connexió a la BDD
            System.out.println("Connexió establerta satisfactòriament.");
        } catch (Exception e) {
            System.out.println("S'ha produït un error en intentar connectar amb la base de dades. Revisa els paràmetres.");
            System.out.println(e);
        }
    }

    @Override
    public void createProducte(Producte p) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_INSERT);

        ps.setString(1, p.getCodiProducte());
        ps.setString(2, p.getNom());
        ps.setString(3,p.getDescripcio());
        ps.setFloat(4, p.getPreuCompra());
        ps.setFloat(5, p.getPreuVenta());

        int rowCount = ps.executeUpdate();
    }

    @Override
    public Producte readProducte() throws SQLException {
        return null;
    }

    @Override
    public ArrayList<Producte> readProductes() throws SQLException {
        ArrayList<Producte> llistaProductes = new ArrayList<Producte>(); //Llista per guardar els productes que es retornin de la consulta
        PreparedStatement ps = conn.prepareStatement(STATEMENT_SELECT_ALL); //Serveix per fer una consulta
        ResultSet rs = ps.executeQuery();  //Serveix per executar la consulta i guardar-la a un ResultSet que guarda un conjunt de dades
        while (rs.next()) { //S'executara el while m'entre hi hagi contingut al ResultSet
            Producte p = new Producte(); // Creo producte
            p.setCodiProducte(rs.getString("codi_producte")); // Carrego les dades al ResultSet
            p.setNom(rs.getString("nom"));
            p.setDescripcio(rs.getString("descripcio"));
            p.setPreuCompra(rs.getFloat("preu_copmra"));
            p.setPreuVenta(rs.getFloat("preu_venta"));
            llistaProductes.add(p); // Afegeixo el producte a la llista
        }
        return llistaProductes;
    }

    @Override
    public void updateProducte(Producte p) throws SQLException {

    }

    @Override
    public Producte deleteProducte(Producte p) throws SQLException {
        return null;
    }
}
