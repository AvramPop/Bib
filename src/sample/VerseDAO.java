package sample;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class VerseDAO {

    public void add(VersePOJO versePOJO) {
        try (
                Connection conn = newConnection("postgresql", "localhost", "5432", "BibleCompi", "dani", "dani");
                PreparedStatement stm =
                        conn.prepareStatement("INSERT INTO bible(id, Book, chapter, verse, text) values(?,?,?,?,?)");
        ){
            stm.setInt(1, versePOJO.getId());
            stm.setString(2, versePOJO.getBook());
            stm.setInt(3, versePOJO.getChapter());
            stm.setInt(4, versePOJO.getVerse());
            stm.setString(5, versePOJO.getText());


            stm.executeUpdate();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public List<VersePOJO> getAll() {
        List<VersePOJO> result = new LinkedList<>();

        try (
                Connection conn = newConnection("postgresql", "localhost", "5432", "BibleCompi", "dani", "dani");
                Statement stm = conn.createStatement();
                ResultSet rs = stm.executeQuery("select id, Book, chapter, verse, text from bible");
        ){
            while (rs.next()) {
                VersePOJO st = new VersePOJO();

                st.setId(rs.getInt("id"));
                st.setBook(rs.getString("Book"));
                st.setChapter(rs.getInt("chapter"));
                st.setVerse(rs.getInt("verse"));
                st.setText(rs.getString("text"));

                result.add(st);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.err.println("Can’t load driver. Verify CLASSPATH");
            System.err.println(e.getMessage());
        }

    }

    private static Connection newConnection(String type, String host, String port, String dbName, String user,
                                            String pw) {

        loadDriver();
        DriverManager.setLoginTimeout(60);
        try {
            String url = new StringBuilder().append("jdbc:").append(type) // “mysql”
                    .append("://").append(host).append(":").append(port).append("/").append(dbName).append("?user=")
                    .append(user).append("&password=").append(pw).toString();
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Cannot connect to the database: " + e.getMessage());
        }

        return null;
    }
}
