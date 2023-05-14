package daos;

import model.Slot;

import java.sql.*;
import java.util.ArrayList;

public class SlotDAO_MySQL implements SlotDAO {
    private Connection conn = null; // o podríem tenir la referència a la Connection pool
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_ROUTE = "jdbc:mysql://localhost:3306/expenedora";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "1234";
    private static final String STATEMENT_SELECT_ONE_ITEM = "SELECT * FROM slot WHERE posicio=?";
    private static final String STATEMENT_SELECT_ALL = "SELECT * FROM slot";
    private static final String STATEMENT_INSERT = "INSERT INTO slot VALUES (?,?,?)";
    private static final String STATEMENT_DELETE = "DELETE FROM SLOT WHERE posicio=?";
    private static final String STATEMENT_UDPATE = "UPDATE slot SET quantitat=?, codi_producte=?" +
            "WHERE posicio =?";

    public SlotDAO_MySQL() {
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
    public void createSlot(Slot s) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_INSERT);

        ps.setInt(1, s.getPosicio());
        ps.setInt(2, s.getQuantitat());
        ps.setString(3,s.getCodiProducte());

        int rowCount = ps.executeUpdate();
    }

    @Override
    public Slot readSlot(int posicio) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_SELECT_ONE_ITEM);
        ps.setInt(1, posicio);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Slot s = new Slot();
            s.setPosicio(rs.getInt("posicio"));
            s.setQuantitat(rs.getInt("quantitat"));
            s.setCodiProducte(rs.getString("codi_producte"));
            return s;
        }
        return null;
    }

    @Override
    public ArrayList<Slot> readSlots() throws SQLException {
        ArrayList<Slot> llistaSlots = new ArrayList<Slot>(); //Llista per guardar els slots que es retornin de la consulta
        PreparedStatement ps = conn.prepareStatement(STATEMENT_SELECT_ALL); //Serveix per fer una consulta
        ResultSet rs = ps.executeQuery();  //Serveix per executar la consulta i guardar-la a un ResultSet que guarda un conjunt de dades
        while (rs.next()) { //S'executara el while m'entre hi hagi contingut al ResultSet
            Slot s = new Slot(); // Creo slot
            s.setPosicio(rs.getInt("posicio")); // Carrego les dades al ResultSet
            s.setQuantitat(rs.getInt("quantitat"));
            s.setCodiProducte(rs.getString("codi_producte"));
            llistaSlots.add(s); // Afegeixo el slot a la llista
        }
        return llistaSlots;
    }

    @Override
    public void updateSlot(Slot s) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_UDPATE);
        ps.setInt(1, s.getQuantitat());
        ps.setString(2, s.getCodiProducte());
        ps.setInt(3, s.getPosicio());
        int rowCount = ps.executeUpdate();
    }

    @Override
    public Slot deleteSlot(Slot s) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_DELETE);
        ps.setInt(1, s.getPosicio());
        int rowCount = ps.executeUpdate();
        return s;
    }
}
