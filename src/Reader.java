import java.sql.*;
import java.util.*;

import com.mysql.cj.xdevapi.PreparableStatement;
import com.mysql.cj.xdevapi.Result;

public class Reader extends Library {

  Connection conn;
  Scanner scanner = new Scanner(System.in);

  public Reader(){}

  public Reader(Connection conn){
    super(conn);
    this.conn = conn;
  }
  
  public void getBook(String name , String lastName , String phoneNumber , String bookName , String author , String borrowDate){

    String query = "UPDATE readers SET borrowed_books_history = CONCAT(COALESCE(borrowed_books_history , '') , '\n' , ? ,',',?, ',' ,'Date of borrowment:' ,?) WHERE first_name = ? AND last_name = ? AND phone_number = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      
      prep.setString(1, author);
      prep.setString(2, bookName);
      prep.setString(3, borrowDate);
      prep.setString(4, name);
      prep.setString(5, lastName);
      prep.setString(6, phoneNumber);

      prep.executeUpdate();

    } catch (Exception e) {
      System.out.println("Something went wrong");
      System.out.println(e);
    }

  }

  public boolean checkReader(String loginReader , String passwordReader , Connection conn){

    String query = "select * from readers where email = ? AND password = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, loginReader);
      prep.setString(2, passwordReader);
      
      prep.executeQuery();

      try (ResultSet rs = prep.executeQuery()) {

        if (rs.next()) {
          return true;
        } else {
          return false;
        }

      } catch (Exception e) {
        System.out.println(e);
        return false;
      }


    } catch (Exception e) {
      System.out.println("The reader in not in the database");
      System.out.println(e);
      return false;
    }

  }

  public void returnBook(String phoneNumberReader , Connection conn){

    System.out.print("Title of the Book you want to return:");
    String titleBook = scanner.nextLine();

    String query = "delete from borrowed_book where TRIM(title) =? AND phone_number = ?";

    String querySetId = "SET @new_id = 0";
    String queryUpdateId = "UPDATE borrowed_book SET id = (@new_id := @new_id + 1)";
    String queryAlterAutoincrement = "ALTER TABLE borrowed_book AUTO_INCREMENT = 1";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      
      prep.setString(1, titleBook);
      prep.setString(2, phoneNumberReader);

      prep.executeUpdate();

      changeIdMethod(querySetId);
      changeIdMethod(queryUpdateId);
      changeIdMethod(queryAlterAutoincrement);

      System.out.println("The book " + titleBook + " was returned");

    } catch (Exception e) {
      System.out.println("Could not return a Book");
      System.out.println(e);
    }
  }

  public void viewHistory(String loginReader ,String passwordReader ,Connection conn){

    String query = "select * from readers where email = ? AND password = ? ";

    try  (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, loginReader);
      prep.setString(2, passwordReader);
      
      try (ResultSet rs = prep.executeQuery()) {
        
        while (rs.next()) {
          System.out.println(" ");
          System.out.println(rs.getString("borrowed_books_history"));
          System.out.println(" ");
        }

      } catch (Exception e) {
        System.out.println(e);
      }

    } catch (Exception e) {
      System.out.println("Couldn't view history");
    }

  }

  public void whenToReturnBook(String phoneNumberReader,Connection conn){
    String query = "select * from  borrowed_book where phone_number = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      
      prep.setString(1, phoneNumberReader);

      try (ResultSet rs = prep.executeQuery()) {
        while (rs.next()) {
          
          System.out.println(" ");
          System.out.println("Title of the Book:"+rs.getString("title"));
          System.out.println("Borrowed date:"+rs.getString("borrowed_date"));
          System.out.println("Returen Date:"+rs.getString("return_date"));
          System.out.println(" ");
        }
      } catch (Exception e) {
        System.out.println(e);
      }

    } catch (Exception e) {
      System.out.println(e);
    }
  }
}