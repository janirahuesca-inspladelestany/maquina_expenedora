package daos;

import model.Producte;
import model.Slot;

import java.sql.SQLException;
import java.util.ArrayList;

public interface SlotDAO {
    public void createSlot(Slot s) throws SQLException;
    public Slot readSlot(int posicio) throws SQLException; // Abans no se li passava cap paràmetre
    public ArrayList<Slot> readSlots() throws SQLException; // public Map<int, Slot> readSlots();
    public void updateSlot(Slot s) throws SQLException; // també li podríem passar dos paràmetres (el slot a actualitzar i les dades a actualitzar)
    public Slot deleteSlot(Slot s) throws SQLException; // public void deleteSlot(int posicio) throws SQLException;
}
