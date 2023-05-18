package shared;

import benefici.domain.BeneficisDAO;
import benefici.infrastructure.BeneficisDAO_MySQL;
import producte.domain.ProducteDAO;
import producte.infrastructure.ProducteDAO_MySQL;
import slot.domain.SlotDAO;
import slot.infrastructure.SlotDAO_MySQL;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAOFactory {

    public static Connection getConn() {
        return conn;
    }

    private static Connection conn;
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/expenedora";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "1234";
    private static DAOFactory instance;
    private static ProducteDAO producteDAOinstance;
    private static SlotDAO slotDAOinstance;
    private static BeneficisDAO beneficisDAOinstance;
    private DAOFactory() {
        try {
            Class.forName(DB_DRIVER); // Carreguem el driver
            conn = DriverManager.getConnection(DB_ROUTE, DB_USER, DB_PWD); // Arranquem la connexió a la BDD
            System.out.println("Connexió establerta satisfactòriament.");
        } catch (Exception e) {
            System.out.println("S'ha produït un error en intentar connectar amb la base de dades. Revisa els paràmetres.");
            System.out.println(e);
            return;
        }
    }
    public static DAOFactory getInstance() {
        if (instance == null)
            instance = new DAOFactory();
        return instance;
    }
    public ProducteDAO getProducteDAO() {
        if (producteDAOinstance == null)
            producteDAOinstance = new ProducteDAO_MySQL(conn);
        return producteDAOinstance;
    }
    public SlotDAO getSlotDAO() {
        if (slotDAOinstance == null)
            slotDAOinstance = new SlotDAO_MySQL(conn);
        return slotDAOinstance;
    }
    public BeneficisDAO getBeneficisDAO() {
        if (beneficisDAOinstance == null)
            beneficisDAOinstance = new BeneficisDAO_MySQL(conn);
        return beneficisDAOinstance;
    }
}
