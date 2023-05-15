package slot;

import shared.InfrastructureError;

import java.util.ArrayList;

public interface SlotDAO {
    public void createSlot(Slot s) throws InfrastructureError;
    public Slot readSlot(int posicio) throws InfrastructureError; // Abans no se li passava cap paràmetre
    public ArrayList<Slot> readSlots() throws InfrastructureError; // public Map<int, Slot> readSlots();
    public void updateSlot(Slot s) throws InfrastructureError; // també li podríem passar dos paràmetres (el slot a actualitzar i les dades a actualitzar)
    public Slot deleteSlot(Slot s) throws InfrastructureError; // public void deleteSlot(int posicio) throws SQLException;
}
