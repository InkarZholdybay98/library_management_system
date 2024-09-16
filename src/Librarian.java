import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.mysql.cj.xdevapi.PreparableStatement;
import com.mysql.cj.xdevapi.Result;

public class Librarian extends Library{

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String returnBookDate;

  Connection conn;
  Scanner scanner = new Scanner(System.in);
  // Reader reader = new Reader(conn);
  SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
  SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

  public Librarian(){}

  public Librarian(Connection conn){
    
    super(conn);
    this.conn = conn;
    
  }

  public Librarian(String firstName , String lastName ,String email, String password , Connection conn){
    
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.conn = conn;

    addNewLibrarian();

  }

  public void addReader(){

    String query = "INSERT INTO readers (first_name , last_name ,  birth_date , phone_number , email , address , status , school , university , work_place , registration_date , password) VALUES (?,?,?,?,?,?,?,?,?,?,? , ?)";

    System.out.print("First Name:");
    String firstName = scanner.nextLine();

    System.out.print("Last Name:");
    String lastName = scanner.nextLine();

    System.out.print("Birth Date(DD.MM.YY):");
    String date = scanner.nextLine();
    String sqlDate = formatDate(date);

    System.out.print("Phone Number:");
    String phoneNumber = scanner.nextLine();

    System.out.print("Email:");
    String email = scanner.nextLine();

    System.out.print("Address:");
    String address = scanner.nextLine();

    System.out.print("Status:");
    String status = scanner.nextLine();

    System.out.print("School:");
    String school = scanner.nextLine();

    System.out.print("University:");
    String university = scanner.nextLine();

    System.out.print("Work Place:");
    String work_place = scanner.nextLine();

    System.out.print("Registration Date(DD.MM.YY):");
    String registrationDate = scanner.nextLine();

    String sqlRegDate = formatDate(registrationDate);

    try (PreparedStatement prep = conn.prepareStatement(query)) {

      prep.setString(1, firstName);
      prep.setString(2, lastName);
      prep.setString(3, sqlDate);
      prep.setString(4, phoneNumber);
      prep.setString(5, email);
      prep.setString(6, address);
      prep.setString(7, status);
      prep.setString(8, school);
      prep.setString(9, university);
      prep.setString(10,work_place);
      prep.setString(11,sqlRegDate);

      String passwordOfReader = generatePasswordReader();
      prep.setString(12, passwordOfReader);

      prep.executeUpdate();
      System.out.println("The New Reader was Successfully Added");

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Something Went Wrong");
    }

  };

  public void giveBooks(){

    System.out.println("Who wants to borrow?");

    System.out.print("First Name:");
    String first_name = scanner.nextLine();

    System.out.print("Last Name:");
    String last_name = scanner.nextLine();

    System.out.print("Phone Number:");
    String phoneNumber = scanner.nextLine();

    boolean isReaderExists = checkReader(first_name,last_name,phoneNumber);

   if (isReaderExists) {

    System.out.print("Title of the Book:");
    String title = scanner.nextLine();

    System.out.print("Published year of the Book:");
    String publishedYear = scanner.nextLine();

    System.out.print("Publishing house of the Book:");
    String publishingHouse = scanner.nextLine();

    boolean isBook = checkBook(title , publishedYear , publishingHouse);
    boolean isBookTaken = checkBookTaken(title , publishedYear , publishingHouse);

    if (isBook && !isBookTaken) {
      System.out.print("Author of the Book:");
      String author = scanner.nextLine();
  
      System.out.print("Borrowed Date(DD.MM.YY):");
      String borrowDate = scanner.nextLine();
      String sqlBorrowDate = formatDate(borrowDate);
  
      System.out.print("Return Date(DD.MM.YY):");
      String returnDate = scanner.nextLine();
      String sqlReturnDate = formatDate(returnDate);
  
      String query = "INSERT INTO borrowed_book (title ,author, borrowed_date ,return_date ,who_borrowed_first_name,who_borrowed_last_name,phone_number , published_year , publisher) VALUES (?,?,?,?,?,?,? , ? , ?)";
  
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        
        prep.setString(1, title);
        prep.setString(2, author);
        prep.setString(3, sqlBorrowDate);
        prep.setString(4, sqlReturnDate);
        prep.setString(5, first_name);
        prep.setString(6, last_name);
        prep.setString(7, phoneNumber);
        prep.setString(8, publishedYear);
        prep.setString(9, publishingHouse);
  
        prep.executeUpdate();

        System.out.println("The book was given succesfully");

        Reader reader = new Reader(conn);
        reader.getBook(first_name , last_name , phoneNumber , title , author , sqlBorrowDate);
  
      } catch (Exception e) {
        System.out.println(e);
      }
    } else {

      if (isBookTaken) {
        System.out.println("This book is taken and will be returned on "+returnBookDate);
      } else {
        System.out.println("This book is not in the database");
      }
      
    }

   }else{
    System.out.println("This Person is not in the database");
   }

  };

  public void seeReaders(){
    String query = "select * from readers";

    try (PreparedStatement prep = conn.prepareStatement(query);ResultSet rs = prep.executeQuery()) {

      while (rs.next()) {
        System.out.println(" ");

        System.out.println("ID:" + rs.getInt("id"));
        System.out.println("Full Name:" + rs.getString("first_name")+" "+rs.getString("last_name"));
        
        System.out.println(" ");
      }

    } catch (Exception e) {
      System.out.println(e);
    }
  };

  public void addBook(){

    System.out.print("Title:");
    String title_of_book = scanner.nextLine();

    System.out.print("Author:");
    String author_of_book = scanner.nextLine();

    System.out.print("Year of publishing:");
    String year_of_publishment = scanner.nextLine();

    System.out.print("Publisher:");
    String publisher = scanner.nextLine();

    String query = "INSERT INTO book (title , author , year_of_publishing , publishing_house) VALUES(?,?,? , ?)";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, title_of_book);
      prep.setString(2, author_of_book);
      prep.setString(3, year_of_publishment);
      prep.setString(4, publisher);

      prep.executeUpdate();

      System.out.println("The New Book was successfully added");
    } catch (Exception e) {
      System.out.println(e);
    }

  };

  public void deleteBook(String title , String publishedYear , String publishHouse){

    boolean isBook = checkBook(title, publishedYear, publishHouse);

    if (isBook) {
      
      String query = "delete from book where TRIM(title) = ? AND year_of_publishing = ? AND TRIM(publishing_house) = ?";

      String querySetId = "SET @new_id = 0";
      String queryUpdateId = "UPDATE book SET id = (@new_id := @new_id + 1)";
      String queryAlterAutoincrement = "ALTER TABLE book AUTO_INCREMENT = 1";

      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.setString(1, title);
        prep.setString(2, publishedYear);
        prep.setString(3, publishHouse);

        prep.executeUpdate();

        changeIdMethod(querySetId);
        changeIdMethod(queryUpdateId);
        changeIdMethod(queryAlterAutoincrement);

        System.out.println("The book was succesfully deleted");

      } catch (Exception e) {
        System.out.println(e);
      }

    } else {
      System.out.println("This book is not in the database");
    }

  };

  public void callReadersToReturn(Connection conn){

    System.out.println("List of readers and their phone numbers , who must return books today");
    
    String query = "SELECT * FROM borrowed_book WHERE return_date = CURDATE()";

    try (PreparedStatement prep = conn.prepareStatement(query);ResultSet rs = prep.executeQuery()) {

      boolean isReturnBookList = false;

      while (rs.next()) {

        isReturnBookList = true;

        System.out.println(" ");

        System.out.println("Full Name:"+rs.getString("who_borrowed_first_name") + " "+rs.getString("who_borrowed_last_name"));

        System.out.println("The Book:"+rs.getString("title"));
        System.out.println("Phone Number:"+rs.getString("phone_number"));

        System.out.println(" ");
      }

      if (!isReturnBookList) {
        System.out.println("Nobody has to return books. Empty List");
      }

    } catch (Exception e) {
      System.out.println(e);
    }

  }

  public void addNewLibrarian(){

    String query = "INSERT INTO librarian (first_name ,last_name , email, password) VALUES(? , ? , ? , ?)";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      
      prep.setString(1, firstName);
      prep.setString(2, lastName);
      prep.setString(3, email);
      prep.setString(4, password);

      prep.executeUpdate();

      System.out.println("The new librarian was successfully added!");

    } catch (Exception e) {

      System.out.println("Couldn't add");
      e.printStackTrace();

    }

  }

  public boolean checkReader(String firstName , String lastName ,String phoneNumber){

    boolean isReader;

    String query = "select * from readers where first_name = ? AND last_name = ? AND phone_number = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {

      prep.setString(1, firstName);
      prep.setString(2, lastName);
      prep.setString(3, phoneNumber);

      try (ResultSet rs = prep.executeQuery()) {

        if (rs.next()) {
          isReader = true;
          return isReader;
        }else{
          isReader = false;
          return isReader;
        }

      } catch (Exception e) {
        System.out.println(e);
        return false;
      }
      
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }

  }

  public boolean checkBook(String title , String publishYear , String publishHouse){

    String query = "select * from book where TRIM(title) = ? AND year_of_publishing = ? AND TRIM(publishing_house) = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {

      prep.setString(1, title);
      prep.setString(2, publishYear);
      prep.setString(3, publishHouse);

      try (ResultSet rs = prep.executeQuery()) {

        if (rs.next()) {
          return true;
        }else{
          return false;
        }


      } catch (Exception e) {
        System.out.println(e);
        return false;
      }

    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  public String generatePasswordReader(){

    String password = "";

    for (int i = 0; i < 8; i++) {
      password += (int)(Math.random() * 9);
    }

    return password;

  }

  public String formatDate(String date){
    String sqlDate = "";

    try {

      Date dateObject = inputFormat.parse(date);
      sqlDate = outputFormat.format(dateObject);

    } catch (Exception e) {
      System.out.println(e);
    }

    return sqlDate;

  }

  public boolean checkBookTaken(String title , String publishYear , String publishHouse){
    
    String query = "select * from borrowed_book where TRIM(title) = ? AND published_year = ? AND TRIM(publisher) = ?";

    try (PreparedStatement prep = conn.prepareStatement(query)) {

      prep.setString(1, title);
      prep.setString(2, publishYear);
      prep.setString(3, publishHouse);

      try (ResultSet rs = prep.executeQuery()) {

        if (rs.next()) {
          returnBookDate = rs.getString("return_date");
          return true;
        }else{
          return false;
        }

      } catch (Exception e) {
        System.out.println(e);
        return false;
      }

    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

}