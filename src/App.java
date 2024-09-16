import java.util.*;
import java.sql.*;

public class App {

    static Scanner scanner;
    static Connection conn;
    static Library library;
    static Librarian librarian;
    static Reader reader;
    
    public static void main(String[] args) throws Exception {

        scanner = new Scanner(System.in);

        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "2641");

        librarian = new Librarian(conn);

        System.out.println("Welcome to the Library");

        while (true) {

            System.out.println("State your role:");
            System.out.println("1.Librarian");
            System.out.println("2.Reader");

            int role = scanner.nextInt();
            scanner.nextLine();

            switch (role) {

                case 1:

                    System.out.println("Do you have a personal account?");
                    System.out.println("1.Yes");
                    System.out.println("2.No");

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {

                        case 1:
                            logInLibrarian();
                            break;

                        case 2:
                            registrationLibrarian();
                            break;
                    
                        default:
                            break;
                    }

                    break;

                case 2:

                    System.out.print("Email:");
                    String loginReader = scanner.nextLine();

                    System.out.print("Password:");
                    String passwordReader = scanner.nextLine();

                    System.out.print("Phone Number:");
                    String phoneNumberReader = scanner.nextLine();

                    reader = new Reader(conn);
                    boolean isReader = reader.checkReader(loginReader , passwordReader , conn);

                    if (isReader) {
                        readerMenu(loginReader ,passwordReader , phoneNumberReader);
                    } else {
                        System.out.println("Reader not found");
                    }

                    break;
            
                default:
                    break;
                
            } 
        }

        
    }

    public static void registrationLibrarian(){
       
        System.out.print("Your first name:");
        String firstName = scanner.nextLine();

        System.out.print("Your last name:");
        String lastName = scanner.nextLine();

        System.out.print("Your email:");
        String email = scanner.nextLine();

        System.out.print("Come up with a password:");
        String password = scanner.nextLine();

        while (password.length() != 8) {
            System.out.print("The length of password must be 8.Please, try again:");
            password = scanner.nextLine();
        }

        System.out.print("Repeat your password:");
        String passwordRepeat = scanner.nextLine();

        while (!password.equals(passwordRepeat)) {
            System.out.print("Passwords don't match. Please , try again:");
            passwordRepeat = scanner.nextLine();
        }

        Librarian librarian = new Librarian(firstName ,lastName ,email ,password , conn);

    }

    public static void logInLibrarian(){

        System.out.print("Email:");
        String email = scanner.nextLine();

        System.out.print("Password:");
        String password = scanner.nextLine();

        String query = "SELECT * FROM librarian WHERE email = ? AND password = ?";

        try (PreparedStatement prep = conn.prepareStatement(query)) {
            
            prep.setString(1, email);
            prep.setString(2, password);

            try (ResultSet rs = prep.executeQuery()) {

                if (rs.next()) {
                    System.out.println("Welcome "+rs.getString("first_name"));
                    libraryMenu(); 
                }else{
                    System.out.println("This librarian is not in the database");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Not Found");
            e.printStackTrace();
        }

    }

    public static void libraryMenu(){

        System.out.println("Please , Choose an option");

        while (true) {
            
            System.out.println("1.Add a New Reader");
            System.out.println("2.To give a Book");
            System.out.println("3.View all Readers");

            System.out.println("4.Add a New Book");
            System.out.println("5.Delete a Book");
            System.out.println("6.View all Books");

            System.out.println("7.Call Readers to Return the Books");

            System.out.println("8.Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    librarian.addReader();
                    break;

                case 2:
                    librarian.giveBooks();
                    break;

                case 3:
                    librarian.seeReaders();
                    break;

                case 4:
                    librarian.addBook();
                    break;

                case 5:

                    System.out.print("Title of Book you want to delete:");
                    String title = scanner.nextLine();

                    System.out.print("Published Year:");
                    String yearPublished = scanner.nextLine();

                    System.out.print("Publishing House:");
                    String publishHouse = scanner.nextLine();

                    librarian.deleteBook(title , yearPublished , publishHouse);
                    break;

                case 6:
                    librarian.seeAllBooks();
                    break;

                case 7:
                    librarian.callReadersToReturn(conn);
                    break;

                case 8:
                    System.out.println("You exited the System");
                    System.exit(0);
                    break;
            
                default:
                    break;
            }

        }

    }

    public static void readerMenu(String loginReader ,String passwordReader , String phoneNumberReader){

        System.out.println("Please , Choose an option");

        while (true) {
            System.out.println("1.Return a Book");
            System.out.println("2.View All Books");
            System.out.println("3.View my History of Books");
            System.out.println("4.When should I return book");
            System.out.println("5.Exit");

            int choiceReader = scanner.nextInt();
            scanner.nextLine();

            switch (choiceReader) {

                case 1:
                    reader.returnBook(phoneNumberReader , conn);
                    break;
                
                case 2:
                    reader.seeAllBooks();
                    break;

                case 3:
                    reader.viewHistory(loginReader, passwordReader, conn);
                    break;

                case 4:
                    reader.whenToReturnBook(phoneNumberReader , conn);
                    break;

                case 5:
                    System.out.println("You exited the system");
                    System.exit(0);
                    break;
            
                default:
                    break;
            }
        }
    }

}