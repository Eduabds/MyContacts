package br.mycontacts.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexao {

    private static final String URL = "jdbc:sqlite:mycontacts.db";

    private Conexao() {
    }

    public static Connection conectar() throws SQLException {
        return conectar(URL);
    }

    public static Connection conectar(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }
}
