import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        // veritabanını başlatır
        Database.initialize();

        Scanner scanner = new Scanner(System.in);
        BookRepository bookRepo = new BookRepository();
        StudentRepository studentRepo = new StudentRepository();
        LoanRepository loanRepo = new LoanRepository();

        while (true) {
            System.out.println("\n=== SMART LIBRARY SİSTEMİ ===");
            System.out.println("1. Kitap Ekle");
            System.out.println("2. Kitapları Listele");
            System.out.println("3. Öğrenci Ekle");
            System.out.println("4. Öğrencileri Listele");
            System.out.println("5. Kitap Ödünç Ver");
            System.out.println("6. Ödünç Listesini Görüntüle");
            System.out.println("7. Kitap Geri Teslim Al");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz: ");

            // girdi kontrol
            if (!scanner.hasNextInt()) {
                System.out.println("Lütfen bir sayı giriniz!");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // buffer temizle

            switch (choice) {
                case 1:
                    System.out.print("Kitap Adı: ");
                    String title = scanner.nextLine();
                    System.out.print("Yazar: ");
                    String author = scanner.nextLine();
                    System.out.print("Yıl: ");
                    if (scanner.hasNextInt()) {
                        int year = scanner.nextInt();
                        bookRepo.add(new Book(title, author, year));
                    } else {
                        System.out.println("Hata: Yıl sayı olmalıdır.");
                        scanner.next();
                    }
                    break;

                case 2:
                    System.out.println("\n--- Kitap Listesi ---");
                    List<Book> books = bookRepo.getAll();
                    if(books.isEmpty()) System.out.println("Kayıtlı kitap yok.");
                    for (Book b : books) System.out.println(b);
                    break;

                case 3:
                    System.out.print("Öğrenci Adı: ");
                    String name = scanner.nextLine();
                    System.out.print("Bölüm: ");
                    String dept = scanner.nextLine();
                    studentRepo.add(new Student(name, dept));
                    break;

                case 4:
                    System.out.println("\n--- Öğrenci Listesi ---");
                    List<Student> students = studentRepo.getAll();
                    if(students.isEmpty()) System.out.println("Kayıtlı öğrenci yok.");
                    for (Student s : students) System.out.println(s);
                    break;

                case 5:
                    System.out.println("Önce Öğrenci ve Kitap ID'lerini listelerden kontrol ediniz.");
                    System.out.print("Ödünç verilecek Kitap ID: ");
                    int bId = scanner.nextInt();
                    System.out.print("Alan Öğrenci ID: ");
                    int sId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Tarih (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    loanRepo.add(new Loan(bId, sId, date));
                    break;

                case 6:
                    System.out.println("\n--- Ödünç Geçmişi ---");
                    List<Loan> loans = loanRepo.getAll();
                    if(loans.isEmpty()) System.out.println("Ödünç kaydı yok.");
                    for (Loan l : loans) System.out.println(l);
                    break;

                case 7:
                    System.out.print("İade edilen Kitap ID: ");
                    int returnBookId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("İade Tarihi (YYYY-MM-DD): ");
                    String returnDate = scanner.nextLine();
                    loanRepo.returnBook(returnBookId, returnDate);
                    break;

                case 0:
                    System.out.println("Sistemden çıkılıyor...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }
}

// database
class Database {
    private static final String URL = "jdbc:sqlite:SmartLibrary.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    public static void initialize() {
        String sqlBooks = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "author TEXT, " +
                "year INTEGER)";

        String sqlStudents = "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "department TEXT)";

        String sqlLoans = "CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bookId INTEGER, " +
                "studentId INTEGER, " +
                "dateBorrowed TEXT, " +
                "dateReturned TEXT)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlBooks);
            stmt.execute(sqlStudents);
            stmt.execute(sqlLoans);
        } catch (SQLException e) {
            System.out.println("Tablo hatası: " + e.getMessage());
        }
    }
}

class Book {
    private int id;
    private String title;
    private String author;
    private int year;

    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }
    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    @Override
    public String toString() { return "ID: " + id + " | " + title + " (" + author + ", " + year + ")"; }
}

class Student {
    private int id;
    private String name;
    private String department;

    public Student(int id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
    public Student(String name, String department) {
        this.name = name;
        this.department = department;
    }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    @Override
    public String toString() { return "ID: " + id + " | " + name + " - " + department; }
}

class Loan {
    private int id;
    private int bookId;
    private int studentId;
    private String dateBorrowed;
    private String dateReturned;

    public Loan(int id, int bookId, int studentId, String dateBorrowed, String dateReturned) {
        this.id = id;
        this.bookId = bookId;
        this.studentId = studentId;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
    }
    public Loan(int bookId, int studentId, String dateBorrowed) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.dateBorrowed = dateBorrowed;
    }
    public int getBookId() { return bookId; }
    public int getStudentId() { return studentId; }
    public String getDateBorrowed() { return dateBorrowed; }
    public String getDateReturned() { return dateReturned; }
    @Override
    public String toString() {
        return "LoanID: " + id + " | KitapID: " + bookId + " | ÖğrID: " + studentId +
                " | Alış: " + dateBorrowed + " | Teslim: " + (dateReturned == null ? "Teslim Edilmedi" : dateReturned);
    }
}

class BookRepository {
    public void add(Book book) {
        String sql = "INSERT INTO books(title, author, year) VALUES(?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.executeUpdate();
            System.out.println("Kitap eklendi.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year")));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return books;
    }
}

class StudentRepository {
    public void add(Student student) {
        String sql = "INSERT INTO students(name, department) VALUES(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getDepartment());
            pstmt.executeUpdate();
            System.out.println("Öğrenci eklendi.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("department")));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return students;
    }
}

class LoanRepository {
    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT count(*) FROM loans WHERE bookId = ? AND dateReturned IS NULL";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return false;
    }
    public void add(Loan loan) {
        if(isBookBorrowed(loan.getBookId())) {
            System.out.println("HATA: Bu kitap şu an ödünçte!");
            return;
        }
        String sql = "INSERT INTO loans(bookId, studentId, dateBorrowed, dateReturned) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getStudentId());
            pstmt.setString(3, loan.getDateBorrowed());
            pstmt.setString(4, loan.getDateReturned());
            pstmt.executeUpdate();
            System.out.println("Ödünç verildi.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
    public List<Loan> getAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(new Loan(rs.getInt("id"), rs.getInt("bookId"), rs.getInt("studentId"), rs.getString("dateBorrowed"), rs.getString("dateReturned")));
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return loans;
    }
    public void returnBook(int bookId, String dateReturned) {
        String sql = "UPDATE loans SET dateReturned = ? WHERE bookId = ? AND dateReturned IS NULL";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dateReturned);
            pstmt.setInt(2, bookId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("İade alındı.");
            else System.out.println("Kayıt bulunamadı veya kitap zaten kütüphanede.");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }
}