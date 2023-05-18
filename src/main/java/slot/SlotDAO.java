package slot;

import shared.ApplicationError;
import shared.InfrastructureError;

import java.util.ArrayList;

public interface SlotDAO {
    public void createSlot(Slot s) throws ApplicationError;
    public Slot readSlot(int posicio) throws ApplicationError; // Abans no se li passava cap paràmetre
    public ArrayList<Slot> readSlots() throws ApplicationError; // public Map<int, Slot> readSlots();
    public void updateSlot(Slot s) throws ApplicationError; // també li podríem passar dos paràmetres (el slot a actualitzar i les dades a actualitzar)
    public Slot deleteSlot(Slot s) throws ApplicationError; // public void deleteSlot(int posicio) throws SQLException;
}
