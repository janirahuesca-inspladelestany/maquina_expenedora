package daos;

import java.sql.*;

public class BeneficisDAO_MySQL implements BeneficisDAO {

    private Connection conn;

    private static final String STATEMENT_SUM_BENEFICIS = "SELECT SUM(beneficis) AS beneficis FROM benefici";
    private static final String STATEMENT_INSERT = "INSERT INTO benefici VALUES (?)";


    public BeneficisDAO_MySQL(Connection connection) {
        conn = connection;
    }


    @Override
    public void afegirBenefici(float benefici) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_INSERT);

        ps.setFloat(1, benefici);
        int rowCount = ps.executeUpdate();
    }

    @Override
    public float obtenirBeneficis() throws SQLException {
        PreparedStatement ps = conn.prepareStatement(STATEMENT_SUM_BENEFICIS);
        ResultSet rs = ps.executeQuery();  //Serveix per executar la consulta i guardar-la a un ResultSet que guarda un conjunt de dades
        float beneficis = 0;
        while (rs.next()) {
            beneficis = rs.getFloat("beneficis");
        }
        return beneficis;
    }
}
