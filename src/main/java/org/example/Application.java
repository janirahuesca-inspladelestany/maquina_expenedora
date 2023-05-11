package org.example;

import daos.ProducteDAO;
import daos.ProducteDAO_MySQL;
import model.Producte;

import java.sql.SQLException;
import java.util.ArrayList;

public class Application {
    public static void main(String[] args) {
        ProducteDAO producteDAO = new ProducteDAO_MySQL();
        ArrayList<Producte> llistaProductes = null;

        // Mostrar llista de productes:
        try {
            llistaProductes = producteDAO.readProductes();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        llistaProductes.forEach(f -> System.out.println(f));

        // Crear nou producte:
        Producte p = new Producte("poma1", "Poma", "poma golden", 0.8f, 1.2f);
         try {
            producteDAO.createProducte(p);
         } catch (SQLException e) {
             //TODO gestionar excepció
             e.printStackTrace();
        }
    }
}