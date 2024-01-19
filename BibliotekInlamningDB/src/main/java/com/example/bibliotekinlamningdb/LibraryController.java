package com.example.bibliotekinlamningdb;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryController {

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameF;

    @FXML
    private PasswordField passwordF;

    @FXML
    private Label loginErrorL;
    @FXML
    private TextField searchString;
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> columnId;
    @FXML
    private TableColumn<Book, String> columnTitle;
    @FXML
    private TableColumn<Book, String> columnAuthor;
    @FXML
    private TableColumn<Book, Boolean> columnAvailable;
    @FXML
    private TableView<LoanedBooks> loanedBooksTable;
    @FXML
    private TableColumn<LoanedBooks, Integer> columnBookloaned;
    @FXML
    private TableColumn<LoanedBooks, Date> columnReturnDate;
    @FXML
    private TableColumn<LoanedBooks, Date> columnLoanDate;
    @FXML
    private TextField idText;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField emailText;
    @FXML
    private TextField phoneText;
    @FXML
    private TextField bookIdText;
    @FXML
    private TextField bookIdTextReturn;

    private Stage primaryStage;

    private String currentUserNameLoggedIn;

    private User currentUser;

    @FXML
    public void initialize() {
        primaryStage = new Stage();
    }

    @FXML
    public void loginButtonAction() {
        if (!usernameF.getText().isBlank() && !passwordF.getText().isBlank()) {
            currentUserNameLoggedIn = usernameF.getText();
            validateLogin();
        } else {
            loginErrorL.setText("Incorrect username or password!");
        }
    }

    public ResultSet runDatabaseStatement(String query) {
        Database connectN = new Database();
        Connection connectDB = connectN.getConnection();

        try {
            Statement statement = connectDB.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateLogin() {

        try {
            ResultSet queryResult = runDatabaseStatement("SELECT count(1) FROM user WHERE username = '" + usernameF.getText() + "' AND password = '" + passwordF.getText() + "'");


            while (queryResult.next()) {
                if (queryResult.getInt(1) == 1) {
                    loginErrorL.setText("Welcome!");
                    loadNewScene("MainPage.fxml");
                } else {
                    loginErrorL.setText("Invalid Login.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNewScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            LibraryController controller = loader.getController();
            controller.currentUserNameLoggedIn = usernameF.getText();
            controller.showUserInfo();
            controller.updateLoanedBooksTable();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

            ((Stage) loginButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void searchBooksByTitle() throws SQLException {

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        String searchValue = searchString.getText();
        ArrayList<Book> books = new ArrayList<>();
        int i = 0;
        ResultSet queryResult = runDatabaseStatement("SELECT * FROM books WHERE title LIKE '%" + searchValue + "%'");

        while (queryResult.next()) {
            int id = queryResult.getInt("id");
            String title = queryResult.getString("title");
            String author = queryResult.getString("author");
            boolean available = queryResult.getBoolean("available");

            books.add(new Book(id, title, author, available));
        }
        bookTable.getItems().setAll(books);
    }

    @FXML
    private void searchBooksByAuthor() throws SQLException {

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        String searchValue = searchString.getText();
        ArrayList<Book> books = new ArrayList<>();

        ResultSet queryResult = runDatabaseStatement("SELECT * FROM books WHERE author LIKE '%" + searchValue + "%' ORDER BY title");

        while (queryResult.next()) {
            int id = queryResult.getInt("id");
            String title = queryResult.getString("title");
            String author = queryResult.getString("author");
            boolean available = queryResult.getBoolean("available");

            books.add(new Book(id, title, author, available));
        }
        bookTable.getItems().setAll(books);
    }

    @FXML
    private void showUserInfo() throws SQLException {


        User user = new User();
        ResultSet queryResult = runDatabaseStatement("select * from user where username= '" + currentUserNameLoggedIn + "'");
        while (queryResult.next()) {
            int id = queryResult.getInt("id");
            String userName = queryResult.getString("username");
            String password = queryResult.getString("password");
            String email = queryResult.getString("email");
            String phone = queryResult.getString("phone");

            user.setId(id);
            user.setUsername(userName);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
        }
        idText.setText(String.valueOf(user.getId()));
        usernameText.setText(user.getUsername());
        passwordText.setText(user.getPassword());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhone());
        currentUser = user;

        updateLoanedBooksTable();
    }

    private void updateLoanedBooksTable() throws SQLException {

        columnBookloaned.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        columnLoanDate.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        columnReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        ArrayList<LoanedBooks> books = new ArrayList<>();

        ResultSet queryResult = runDatabaseStatement("SELECT * FROM loans WHERE userId= '" + currentUser.getId() + "'");

        while (queryResult.next()) {
            Integer bookId = queryResult.getInt("bookId");
            Date loanDate = queryResult.getDate("loanDate");
            Date returnDate = queryResult.getDate("returnDate");

            books.add(new LoanedBooks(0, 0, bookId, loanDate, returnDate));
        }
        loanedBooksTable.getItems().setAll(books);

    }


    @FXML
    private void updateUser() throws SQLException {

        Database connectN = new Database();
        Connection connectDB = connectN.getConnection();
        Statement statement = connectDB.createStatement();

        int idValue = Integer.parseInt(idText.getText());
        String usernameValue = usernameText.getText();
        String passwordValue = passwordText.getText();
        String emailValue = emailText.getText();
        String phoneValue = phoneText.getText();
        statement.executeUpdate("UPDATE user SET username= '" + usernameValue + "'"
                + ", password= '" + passwordValue + "'"
                + ", email= '" + emailValue + "'"
                + ", phone= '" + phoneValue + "'"
                + "where id= '" + idValue + "'");

    }

    @FXML
    private void loanBook() throws SQLException {

        String searchValue = bookIdText.getText();
        Book book = new Book();

        ResultSet queryResult = runDatabaseStatement("SELECT * FROM books WHERE id= '" + searchValue + "'");

        while (queryResult.next()) {
            int id = queryResult.getInt("id");
            String title = queryResult.getString("title");
            String author = queryResult.getString("author");
            boolean available = queryResult.getBoolean("available");

            book.setId(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setAvailable(available);
        }

        if (book.getAvailable()) {
            bookIdText.setText("Boken är utlånad!");
        } else {
            int userID = currentUser.getId();
            int bookId = Integer.parseInt(bookIdText.getText());
            Date datetoReturn = Date.valueOf(LocalDate.now().plusDays(30));

            Database connectN = new Database();
            Connection connectDB = connectN.getConnection();
            Statement statement = connectDB.createStatement();
            statement.executeUpdate("INSERT INTO loans (userId, bookId, loanDate, returnDate) values (" + userID + "," + bookId + "," + "now()," + "'" + datetoReturn + "')");
            statement.executeUpdate("UPDATE books SET available= '" + 1 + "'"
                    + "where id= '" + bookId + "'");

        }
        updateLoanedBooksTable();
    }

    @FXML
    private void returnBook() throws SQLException {

        String searchValue = bookIdTextReturn.getText();

        Database connectN = new Database();
        Connection connectDB = connectN.getConnection();
        Statement statement = connectDB.createStatement();
        statement.executeUpdate("DELETE FROM loans where bookId= '" + searchValue + "'");
        statement.executeUpdate("UPDATE books SET available= '" + 0 + "'"
                + "where id= '" + searchValue + "'");
        updateLoanedBooksTable();

    }
}
