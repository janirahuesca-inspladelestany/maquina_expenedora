package slot.infrastructure;

import shared.ApplicationError;
import shared.InfrastructureError;
import slot.domain.Slot;
import slot.domain.SlotDAO;

import java.sql.*;
import java.util.ArrayList;

public class SlotDAO_MySQL implements SlotDAO {
    private Connection conn; // o podríem tenir la referència a la Connection pool
    private static final String STATEMENT_SELECT_ONE_ITEM = "SELECT * FROM slot WHERE posicio=?";
    private static final String STATEMENT_SELECT_ALL = "SELECT * FROM slot";
    private static final String STATEMENT_INSERT = "INSERT INTO slot VALUES (?,?,?)";
    private static final String STATEMENT_DELETE = "DELETE FROM SLOT WHERE posicio=?";
    private static final String STATEMENT_UDPATE = "UPDATE slot SET quantitat=?, codi_producte=?" +
            "WHERE posicio =?";

    public SlotDAO_MySQL(Connection connection) {
        conn = connection;
    }

    @Override
    public void createSlot(Slot s) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_INSERT);

            ps.setInt(1, s.getPosicio());
            ps.setInt(2, s.getQuantitat());
            ps.setString(3, s.getCodiProducte());

            int rowCount = ps.executeUpdate();
        } catch(SQLException exception) {
            throw new InfrastructureError("Error afegint slot a la base de dades.");
        }
    }

    @Override
    public Slot readSlot(int posicio) throws ApplicationError {
        try {
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
        } catch (SQLException exception) {
            throw new InfrastructureError("Error obtenint slot de la base de dades.");
        }
    }

    @Override
    public ArrayList<Slot> readSlots() throws ApplicationError {
        ArrayList<Slot> llistaSlots = new ArrayList<Slot>(); //Llista per guardar els slots que es retornin de la consulta
        try {
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
        } catch (SQLException exception) {
            throw new InfrastructureError("Error obtenint slots de la base de dades");
        }
    }

    @Override
    public void updateSlot(Slot s) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_UDPATE);
            ps.setInt(1, s.getQuantitat());
            ps.setString(2, s.getCodiProducte());
            ps.setInt(3, s.getPosicio());
            int rowCount = ps.executeUpdate();
        } catch (SQLException exception) {
            throw new InfrastructureError("Error actualitzant slot de la base de dades.");
        }
    }

    @Override
    public Slot deleteSlot(Slot s) throws ApplicationError {
        try {
            PreparedStatement ps = conn.prepareStatement(STATEMENT_DELETE);
            ps.setInt(1, s.getPosicio());
            int rowCount = ps.executeUpdate();
            return s;
        } catch (SQLException exception) {
            throw new InfrastructureError("Error eliminant slot de la base de dades.");
        }
    }
}
