import java.sql.*;

public class Library {

  private Connection conn;

  public Library(){}

  public Library(Connection conn){
    this.conn = conn;
  }

  public void seeAllBooks(){

    String query = "select * from book";

    try (PreparedStatement prep = conn.prepareStatement(query); ResultSet rs = prep.executeQuery()) {
      while (rs.next()) {
        System.out.println(" ");
        System.out.println("ID:"+rs.getString("id"));
        System.out.println("Title:"+rs.getString("title"));
        System.out.println("Author:"+rs.getString("author"));
        System.out.println("Year of publishing:"+rs.getString("year_of_publishing"));
        System.out.println("Publishing house:"+rs.getString("publishing_house"));
        System.out.println(" ");
      }
    } catch (Exception e) {
      System.out.println(e);
    }

  };

  public void changeIdMethod(String query){

    try(PreparedStatement prep = conn.prepareStatement(query))  {
      prep.executeUpdate();
    } catch (Exception e) {
      System.out.println(e);
    }

  }
}
