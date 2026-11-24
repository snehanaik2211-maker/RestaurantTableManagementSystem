
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// Interface
interface ReservationOperations {
    void makeReservation();
    void cancelReservation();
    void modifyReservation();
    void searchReservation();
}

// Abstract class
abstract class Person {
    protected String name;
    protected String phone;

    Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    abstract void displayDetails();
}

// Customer class
class Customer extends Person {

    Customer(String name, String phone) {
        super(name, phone);
    }

    @Override
    void displayDetails() {
        System.out.println("Customer Name: " + name);
        System.out.println("Phone Number: " + phone);
    }
}

// Table class
class Table {
    private int tableNumber;
    private int capacity;
    private String category;

    Table(int tableNumber, int capacity, String category) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.category = category;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getCategory() {
        return category;
    }

    public String getTableType() {
        return category;
    }

    public void displayTable() {
        System.out.println(
            "Table: " + tableNumber +
            " | Capacity: " + capacity +
            " | Category: " + getTableType()
        );
    }
}

// Inheritance and method overriding
class SpecialTable extends Table {

    SpecialTable(int number, int capacity, String category) {
        super(number, capacity, category);
    }

    @Override
    public String getTableType() {
        return "Special " + super.getCategory();
    }
}

// Reservation class
class Reservation {
    private static int nextId = 1001;

    private int reservationId;
    private Customer customer;
    private Table table;
    private LocalDate date;
    private LocalTime time;
    private int guests;

    static final long DURATION_HOURS = 2;

    Reservation(Customer customer, Table table,
                LocalDate date, LocalTime time, int guests) {

        this.reservationId = nextId++;
        this.customer = customer;
        this.table = table;
        this.date = date;
        this.time = time;
        this.guests = guests;
    }

    public int getReservationId() {
        return reservationId;
    }

    public Table getTable() {
        return table;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getGuests() {
        return guests;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void update(Table table, LocalDate date,
                       LocalTime time, int guests) {
        this.table = table;
        this.date = date;
        this.time = time;
        this.guests = guests;
    }

    public void displayReservation() {
        System.out.println("--------------------------------");
        System.out.println("Reservation ID: " + reservationId);
        customer.displayDetails();
        System.out.println("Table Number: " + table.getTableNumber());
        System.out.println("Table Category: " + table.getTableType());
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Number of Guests: " + guests);
        System.out.println("--------------------------------");
    }
}

// Custom exception
class ReservationException extends Exception {
    ReservationException(String message) {
        super(message);
    }
}

// Main Restaurant class
public class RestaurantReservationSystem
        implements ReservationOperations {

    private ArrayList<Table> tables = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();

    private Scanner sc = new Scanner(System.in);

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    // Constructor
    RestaurantReservationSystem() {
        // Default restaurant tables
        tables.add(new Table(1, 2, "Normal"));
        tables.add(new Table(2, 2, "Normal"));
        tables.add(new Table(3, 4, "Family"));
        tables.add(new Table(4, 4, "Family"));
        tables.add(new SpecialTable(5, 6, "VIP"));
        tables.add(new Table(6, 8, "Party"));
    }

    // Display all tables
    void displayTables() {
        System.out.println("\n----- RESTAURANT TABLES -----");

        for (Table t : tables) {
            t.displayTable();
        }
    }

    // Check overlapping reservations
    private boolean isTableAvailable(Table table,
                                     LocalDate date,
                                     LocalTime time,
                                     int ignoredId) {

        LocalTime newEnd = time.plusHours(
                Reservation.DURATION_HOURS);

        for (Reservation r : reservations) {

            if (r.getTable().getTableNumber()
                    == table.getTableNumber()
                    && r.getDate().equals(date)
                    && r.getReservationId() != ignoredId) {

                LocalTime oldEnd = r.getTime().plusHours(
                        Reservation.DURATION_HOURS);

                // Check time overlap
                if (time.isBefore(oldEnd)
                        && r.getTime().isBefore(newEnd)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Assign suitable table
    private Table findSuitableTable(int guests,
                                    LocalDate date,
                                    LocalTime time,
                                    int ignoredId) {

        Table bestTable = null;

        for (Table t : tables) {

            if (t.getCapacity() >= guests
                    && isTableAvailable(t, date, time, ignoredId)) {

                if (bestTable == null
                        || t.getCapacity() < bestTable.getCapacity()) {
                    bestTable = t;
                }
            }
        }

        return bestTable;
    }

    // Display available tables
    void displayAvailableTables() {

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        System.out.print("Enter time (HH:mm): ");
        LocalTime time = LocalTime.parse(
                sc.nextLine(), TIME_FORMAT);

        System.out.print("Enter number of guests: ");
        int guests = Integer.parseInt(sc.nextLine());

        System.out.println("\nAvailable Tables:");

        boolean found = false;

        for (Table t : tables) {

            if (t.getCapacity() >= guests
                    && isTableAvailable(t, date, time, -1)) {

                t.displayTable();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No suitable tables available.");
        }
    }

    // Method overloading
    private Reservation createReservation(
            Customer customer, int guests,
            LocalDate date, LocalTime time)
            throws ReservationException {

        return createReservation(customer, guests,
                date, time, null);
    }

    private Reservation createReservation(
            Customer customer, int guests,
            LocalDate date, LocalTime time,
            String category)
            throws ReservationException {

        if (guests <= 0) {
            throw new ReservationException(
                    "Number of guests must be positive.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new ReservationException(
                    "Past dates cannot be reserved.");
        }

        if (time.isBefore(LocalTime.of(0, 0))
                || time.plusHours(2).isAfter(LocalTime.MAX)) {
            throw new ReservationException(
                    "Invalid reservation time.");
        }

        Table selected = null;

        // Assign smallest suitable available table
        for (Table t : tables) {

            boolean categoryMatch =
                    category == null
                    || t.getCategory().equalsIgnoreCase(category);

            if (categoryMatch
                    && t.getCapacity() >= guests
                    && isTableAvailable(t, date, time, -1)) {

                if (selected == null
                        || t.getCapacity() < selected.getCapacity()) {
                    selected = t;
                }
            }
        }

        if (selected == null) {
            throw new ReservationException(
                    "No suitable table available.");
        }

        return new Reservation(customer, selected,
                date, time, guests);
    }

    // 1. Make reservation
    @Override
    public void makeReservation() {

        try {
            System.out.print("Enter customer name: ");
            String name = sc.nextLine();

            System.out.print("Enter phone number: ");
            String phone = sc.nextLine();

            System.out.print("Enter number of guests: ");
            int guests = Integer.parseInt(sc.nextLine());

            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(sc.nextLine());

            System.out.print("Enter time (HH:mm): ");
            LocalTime time = LocalTime.parse(
                    sc.nextLine(), TIME_FORMAT);

            Customer customer = new Customer(name, phone);

            Reservation r = createReservation(
                    customer, guests, date, time);

            reservations.add(r);

            System.out.println("\nReservation Successful!");
            r.displayReservation();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 2. Cancel reservation
    @Override
    public void cancelReservation() {

        System.out.print("Enter Reservation ID: ");
        int id = Integer.parseInt(sc.nextLine());

        Reservation found = null;

        for (Reservation r : reservations) {
            if (r.getReservationId() == id) {
                found = r;
                break;
            }
        }

        if (found != null) {
            reservations.remove(found);
            System.out.println("Reservation cancelled successfully.");
        } else {
            System.out.println("Reservation not found.");
        }
    }

    // 3. Modify reservation
    @Override
    public void modifyReservation() {

        System.out.print("Enter Reservation ID: ");
        int id = Integer.parseInt(sc.nextLine());

        Reservation found = null;

        for (Reservation r : reservations) {
            if (r.getReservationId() == id) {
                found = r;
                break;
            }
        }

        if (found == null) {
            System.out.println("Reservation not found.");
            return;
        }

        try {
            System.out.print("Enter new number of guests: ");
            int guests = Integer.parseInt(sc.nextLine());

            System.out.print("Enter new date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(sc.nextLine());

            System.out.print("Enter new time (HH:mm): ");
            LocalTime time = LocalTime.parse(
                    sc.nextLine(), TIME_FORMAT);

            Table newTable = findSuitableTable(
                    guests, date, time, id);

            if (newTable == null) {
                throw new ReservationException(
                        "No suitable table available.");
            }

            found.update(newTable, date, time, guests);

            System.out.println("Reservation modified successfully.");
            found.displayReservation();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 4. Search reservation
    @Override
    public void searchReservation() {

        System.out.print("Enter Reservation ID: ");
        int id = Integer.parseInt(sc.nextLine());

        for (Reservation r : reservations) {

            if (r.getReservationId() == id) {
                r.displayReservation();
                return;
            }
        }

        System.out.println("Reservation not found.");
    }

    // 5. Display daily reservations
    void displayDailyReservations() {

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        boolean found = false;

        System.out.println("\nDaily Reservations:");

        for (Reservation r : reservations) {

            if (r.getDate().equals(date)) {
                r.displayReservation();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No reservations for this date.");
        }
    }

    // 6. Table utilization
    void displayTableUtilization() {

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine());

        int totalTables = tables.size();
        int occupiedTables = 0;

        for (Table t : tables) {

            for (Reservation r : reservations) {

                if (r.getDate().equals(date)
                        && r.getTable().getTableNumber()
                        == t.getTableNumber()) {

                    occupiedTables++;
                    break;
                }
            }
        }

        double utilization =
                totalTables == 0 ? 0 :
                (occupiedTables * 100.0) / totalTables;

        System.out.println("\nTotal Tables: " + totalTables);
        System.out.println("Occupied Tables: " + occupiedTables);
        System.out.println("Available Tables: "
                + (totalTables - occupiedTables));

        System.out.printf("Table Utilization: %.2f%%\n",
                utilization);
    }

    // 7. Add new table
    void addTable() {

        try {
            System.out.print("Enter table number: ");
            int number = Integer.parseInt(sc.nextLine());

            for (Table t : tables) {
                if (t.getTableNumber() == number) {
                    System.out.println("Table number already exists.");
                    return;
                }
            }

            System.out.print("Enter table capacity: ");
            int capacity = Integer.parseInt(sc.nextLine());

            if (capacity <= 0) {
                System.out.println("Capacity must be positive.");
                return;
            }

            System.out.print("Enter category: ");
            String category = sc.nextLine();

            tables.add(new Table(number, capacity, category));

            System.out.println("Table added successfully!");

        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {

        RestaurantReservationSystem restaurant =
                new RestaurantReservationSystem();

        Scanner input = restaurant.sc;

        int choice;

        do {
            System.out.println("\n================================");
            System.out.println(" RESTAURANT TABLE RESERVATION");
            System.out.println("================================");
            System.out.println("1. Display All Tables");
            System.out.println("2. Display Available Tables");
            System.out.println("3. Reserve a Table");
            System.out.println("4. Modify Reservation");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Search Reservation");
            System.out.println("7. Display Daily Reservations");
            System.out.println("8. Display Table Utilization");
            System.out.println("9. Add New Table");
            System.out.println("0. Exit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(input.nextLine());

                switch (choice) {

                    case 1:
                        restaurant.displayTables();
                        break;

                    case 2:
                        restaurant.displayAvailableTables();
                        break;

                    case 3:
                        restaurant.makeReservation();
                        break;

                    case 4:
                        restaurant.modifyReservation();
                        break;

                    case 5:
                        restaurant.cancelReservation();
                        break;

                    case 6:
                        restaurant.searchReservation();
                        break;

                    case 7:
                        restaurant.displayDailyReservations();
                        break;

                    case 8:
                        restaurant.displayTableUtilization();
                        break;

                    case 9:
                        restaurant.addTable();
                        break;

                    case 0:
                        System.out.println("Thank you!");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {
                choice = -1;
                System.out.println("Please enter a valid number.");
            }

        } while (choice != 0);
    }
}

