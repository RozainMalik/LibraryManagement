import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class LibraryManagementSystem extends JFrame implements ActionListener {

    private JTextField bookTitleField, bookAuthorField, bookGenreField, memberNameField;
    private JTextArea displayArea;
    private JComboBox<String> memberComboBox, bookComboBox;
    private JButton addBookButton, registerMemberButton, issueBookButton, returnBookButton, viewAllButton;
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private SimpleDateFormat dateFormat;

    public LibraryManagementSystem() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        setTitle("Library Management System");
        setSize(600, 600);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new JLabel("Book Title:"));
        bookTitleField = new JTextField(20);
        add(bookTitleField);

        add(new JLabel("Author:"));
        bookAuthorField = new JTextField(20);
        add(bookAuthorField);

        add(new JLabel("Genre:"));
        bookGenreField = new JTextField(20);
        add(bookGenreField);

        addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(this);
        add(addBookButton);

        add(new JLabel("Member Name:"));
        memberNameField = new JTextField(20);
        add(memberNameField);

        registerMemberButton = new JButton("Register Member");
        registerMemberButton.addActionListener(this);
        add(registerMemberButton);

        add(new JLabel("Select Member:"));
        memberComboBox = new JComboBox<>();
        add(memberComboBox);

        add(new JLabel("Select Book:"));
        bookComboBox = new JComboBox<>();
        add(bookComboBox);

        issueBookButton = new JButton("Issue Book");
        issueBookButton.addActionListener(this);
        add(issueBookButton);

        returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(this);
        add(returnBookButton);

        viewAllButton = new JButton("View All Borrowed Books");
        viewAllButton.addActionListener(this);
        add(viewAllButton);

        displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea));

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Add Book")) {
            addBook();
        } else if (command.equals("Register Member")) {
            registerMember();
        } else if (command.equals("Issue Book")) {
            issueBook();
        } else if (command.equals("Return Book")) {
            returnBook();
        } else if (command.equals("View All Borrowed Books")) {
            viewAllBorrowedBooks();
        }
    }

    private void addBook() {
        String title = bookTitleField.getText();
        String author = bookAuthorField.getText();
        String genre = bookGenreField.getText();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            displayArea.append("Please fill in all book details.\n");
            return;
        }

        Book book = new Book(title, author, genre);
        books.add(book);
        bookComboBox.addItem(title);

        displayArea.append("Book added: " + title + "\n");

        bookTitleField.setText("");
        bookAuthorField.setText("");
        bookGenreField.setText("");
    }

    private void registerMember() {
        String memberName = memberNameField.getText();

        if (memberName.isEmpty()) {
            displayArea.append("Please enter a member name.\n");
            return;
        }

        Member member = new Member(memberName);
        members.add(member);
        memberComboBox.addItem(memberName);

        displayArea.append("Member registered: " + memberName + "\n");

        memberNameField.setText("");
    }

    private void issueBook() {
        String memberName = (String) memberComboBox.getSelectedItem();
        String bookTitle = (String) bookComboBox.getSelectedItem();

        if (memberName == null || bookTitle == null) {
            displayArea.append("Please select a member and a book.\n");
            return;
        }

        Member member = getMemberByName(memberName);
        Book book = getBookByTitle(bookTitle);

        if (member == null || book == null) {
            displayArea.append("Invalid member or book.\n");
            return;
        }

        if (book.isAvailable()) {
            Date dueDate = new Date();
            dueDate.setTime(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000*30)); //1 month later
            member.borrowBook(book, dueDate);
            displayArea.append(memberName + " issued " + bookTitle + " (Due: " + dateFormat.format(dueDate) + ")\n");
        } else {
            displayArea.append("This book is already borrowed.\n");
        }
    }

    private void returnBook() {
        String memberName = (String) memberComboBox.getSelectedItem();
        String bookTitle = (String) bookComboBox.getSelectedItem();

        if (memberName == null || bookTitle == null) {
            displayArea.append("Please select a member and a book.\n");
            return;
        }

        Member member = getMemberByName(memberName);
        Book book = getBookByTitle(bookTitle);

        if (member == null || book == null) {
            displayArea.append("Invalid member or book.\n");
            return;
        }

        if (member.returnBook(book)) {
            displayArea.append(memberName + " returned " + bookTitle + "\n");
        } else {
            displayArea.append(memberName + " has not borrowed " + bookTitle + "\n");
        }
    }

    private void viewAllBorrowedBooks() {
        displayArea.append("All Borrowed Books:\n");
        for (Member member : members) {
            for (BorrowRecord record : member.getBorrowedBooks()) {
                displayArea.append(record.getMemberName() + " borrowed " + record.getBookTitle() + " (Due: " + dateFormat.format(record.getDueDate()) + ")\n");
            }
        }
    }

    private Member getMemberByName(String name) {
        for (Member member : members) {
            if (member.getName().equals(name)) {
                return member;
            }
        }
        return null;
    }

    private Book getBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return book;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new LibraryManagementSystem();
    }

    class Book {
        private String title;
        private String author;
        private String genre;
        private boolean available;

        public Book(String title, String author, String genre) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.available = true;
        }

        public String getTitle() {
            return title;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    class Member {
        private String name;
        private ArrayList<BorrowRecord> borrowedBooks;

        public Member(String name) {
            this.name = name;
            this.borrowedBooks = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void borrowBook(Book book, Date dueDate) {
            borrowedBooks.add(new BorrowRecord(book.getTitle(), dueDate));
            book.setAvailable(false);
        }

        public boolean returnBook(Book book) {
            for (Iterator<BorrowRecord> iterator = borrowedBooks.iterator(); iterator.hasNext();) {
                BorrowRecord record = iterator.next();
                if (record.getBookTitle().equals(book.getTitle())) {
                    iterator.remove();
                    book.setAvailable(true);
                    return true;
                }
            }
            return false;
        }

        public ArrayList<BorrowRecord> getBorrowedBooks() {
            return borrowedBooks;
        }
    }

    class BorrowRecord {
        private String bookTitle;
        private Date dueDate;

        public BorrowRecord(String bookTitle, Date dueDate) {
            this.bookTitle = bookTitle;
            this.dueDate = dueDate;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public Date getDueDate() {
            return dueDate;
        }

        public String getMemberName() {
            return memberComboBox.getSelectedItem().toString();
        }
    }
}
