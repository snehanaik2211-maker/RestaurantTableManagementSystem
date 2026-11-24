import java.util.*;
import java.time.*;

// Class 1: Table
class Table2 {
    int tableNumber;
    int capacity;
    String category;

    Table2(int tableNumber, int capacity, String category) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.category = category;
    }
}

// Class 2: Reservation
class Reservation2 {
    int reservationId;
    String customerName;
    LocalDate date;
    LocalTime time;
    int guests;
    Table2 table;

    Reservation2(int id, String name, LocalDate date,
                LocalTime time, int guests, Table2 table) {

        this.reservationId = id;
        this.customerName = name;
        this.date = date;
        this.time = time;
        this.guests = guests;
        this.table = table;
    }
}

// Class 3: Restaurant
class Restaurant {

    ArrayList<Table2> tables = new ArrayList<>();
    ArrayList<Reservation2> reservations = new ArrayList<>();

    int nextId = 101;

    Restaurant() {
        tables.add(new Table2(1, 2, "Indoor"));
        tables.add(new Table2(2, 4, "Indoor"));
        tables.add(new Table2(3, 4, "Outdoor"));
        tables.add(new Table2(4, 6, "Indoor"));
        tables.add(new Table2(5, 8, "Outdoor"));
    }

    // Check overlapping reservations
    boolean isAvailable(Table2 table, LocalDate date,
                        LocalTime time, int excludeId) {

        LocalTime endTime = time.plusHours(2);

        for (Reservation2 r : reservations) {

            if (r.reservationId == excludeId)
                continue;

            if (r.table.tableNumber == table.tableNumber
                    && r.date.equals(date)) {

                LocalTime oldEnd = r.time.plusHours(2);

                if (time.isBefore(oldEnd)
                        && r.time.isBefore(endTime)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Display available tables
    void displayAvailableTables(LocalDate date,
                               LocalTime time) {

        System.out.println("\nAvailable Tables:");

        for (Table2 t : tables) {
            if (isAvailable(t, date, time, -1)) {
                System.out.println(
                    "Table: " + t.tableNumber +
                    " | Capacity: " + t.capacity +
                    " | Category: " + t.category
                );
            }
        }
    }

    // Reserve a table
    void reserveTable(String name, LocalDate date,
                      LocalTime time, int guests) {

        Table2 selected = null;

        for (Table2 t : tables) {

            if (t.capacity >= guests
                    && (selected == null
                    || t.capacity < selected.capacity)
                    && isAvailable(t, date, time, -1)) {

                selected = t;
            }
        }

        if (selected == null) {
            System.out.println("Sorry! No suitable table available.");
            return;
        }

        Reservation2 r = new Reservation2(
            nextId++, name, date, time, guests, selected
        );

        reservations.add(r);

        System.out.println("\nReservation Successful!");
        System.out.println("Reservation ID: " + r.reservationId);
        System.out.println("Customer Name: " + name);
        System.out.println("Table Number: " + selected.tableNumber);
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
    }

    // Search reservation
    void searchReservation(int id) {

        for (Reservation2 r : reservations) {

            if (r.reservationId == id) {
                displayReservation(r);
                return;
            }
        }

        System.out.println("Reservation not found!");
    }

    // Modify reservation
    void modifyReservation(int id, LocalDate date,
                           LocalTime time, int guests) {

        for (Reservation2 r : reservations) {

            if (r.reservationId == id) {

                Table2 selected = null;

                for (Table2 t : tables) {

                    if (t.capacity >= guests
                            && (selected == null
                            || t.capacity < selected.capacity)
                            && isAvailable(t, date, time, id)) {

                        selected = t;
                    }
                }

                if (selected == null) {
                    System.out.println("No suitable table available!");
                    return;
                }

                r.date = date;
                r.time = time;
                r.guests = guests;
                r.table = selected;

                System.out.println("Reservation Modified Successfully!");
                return;
            }
        }

        System.out.println("Reservation not found!");
    }

    // Cancel reservation
    void cancelReservation(int id) {

        Iterator<Reservation2> iterator = reservations.iterator();

        while (iterator.hasNext()) {

            Reservation2 r = iterator.next();

            if (r.reservationId == id) {
                iterator.remove();
                System.out.println("Reservation Cancelled Successfully!");
                return;
            }
        }

        System.out.println("Reservation not found!");
    }

    // Display reservation details
    void displayReservation(Reservation2 r) {

        System.out.println("\nReservation Details");
        System.out.println("ID: " + r.reservationId);
        System.out.println("Name: " + r.customerName);
        System.out.println("Date: " + r.date);
        System.out.println("Time: " + r.time);
        System.out.println("Guests: " + r.guests);
        System.out.println("Table Number: " + r.table.tableNumber);
        System.out.println("Category: " + r.table.category);
    }

    // Display daily reservations and utilization
    void dailyReservations(LocalDate date) {

        int count = 0;
        Set<Integer> usedTables = new HashSet<>();

        System.out.println("\nDaily Reservations for " + date);

        for (Reservation2 r : reservations) {

            if (r.date.equals(date)) {
                displayReservation(r);
                usedTables.add(r.table.tableNumber);
                count++;
            }
        }

        System.out.println("\nTotal Reservations: " + count);

        double utilization =
            (usedTables.size() * 100.0) / tables.size();

        System.out.println("Table Utilization: "
                           + utilization + "%");
    }
}

// Class 4: Main
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Restaurant restaurant = new Restaurant();

        int choice;

        do {

            System.out.println("\n===== RESTAURANT TABLE RESERVATION SYSTEM =====");
            System.out.println("1. Display Available Tables");
            System.out.println("2. Reserve a Table");
            System.out.println("3. Search Reservation");
            System.out.println("4. Modify Reservation");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Display Daily Reservations");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {

                case 1:
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    LocalDate d1 = LocalDate.parse(sc.nextLine());

                    System.out.print("Enter Time (HH:MM): ");
                    LocalTime t1 = LocalTime.parse(sc.nextLine());

                    restaurant.displayAvailableTables(d1, t1);
                    break;

                case 2:
                    System.out.print("Enter Customer Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    LocalDate date = LocalDate.parse(sc.nextLine());

                    System.out.print("Enter Time (HH:MM): ");
                    LocalTime time = LocalTime.parse(sc.nextLine());

                    System.out.print("Enter Number of Guests: ");
                    int guests = Integer.parseInt(sc.nextLine());

                    restaurant.reserveTable(name, date, time, guests);
                    break;

                case 3:
                    System.out.print("Enter Reservation ID: ");
                    int id = Integer.parseInt(sc.nextLine());

                    restaurant.searchReservation(id);
                    break;

                case 4:
                    System.out.print("Enter Reservation ID: ");
                    int mid = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter New Date (YYYY-MM-DD): ");
                    LocalDate newDate = LocalDate.parse(sc.nextLine());

                    System.out.print("Enter New Time (HH:MM): ");
                    LocalTime newTime = LocalTime.parse(sc.nextLine());

                    System.out.print("Enter New Number of Guests: ");
                    int newGuests = Integer.parseInt(sc.nextLine());

                    restaurant.modifyReservation(
                        mid, newDate, newTime, newGuests
                    );
                    break;

                case 5:
                    System.out.print("Enter Reservation ID: ");
                    int cid = Integer.parseInt(sc.nextLine());

                    restaurant.cancelReservation(cid);
                    break;

                case 6:
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    LocalDate dailyDate = LocalDate.parse(sc.nextLine());

                    restaurant.dailyReservations(dailyDate);
                    break;

                case 7:
                    System.out.println("Thank you for using our system!");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

        } while (choice != 7);

        sc.close();
    }
}