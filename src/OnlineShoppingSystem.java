import java.util.*;

sealed interface Entity permits Product, Customer, Order {
}

record Product(String name, double price, int quantity) implements Entity {
    public Product {
        if (!name.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Product name must be alphanumeric.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity must be non-negative.");
        }
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

}

record Customer(String name, String email) implements Entity {
    public Customer {
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}

record Order(Customer customer, Product product, int quantity) implements Entity {
    public double calculateTotalPrice() {
        return product.getPrice() * quantity;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}

public class OnlineShoppingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Product> inventory = new HashMap<>();
    private static final Map<String, Customer> customers = new HashMap<>();
    private static final List<Order> orders = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("""
                                   Options:
                                   1. Add product to inventory
                                   2. Display available products
                                   3. Add customer
                                   4. Place an order
                                   5. Calculate total price of an order
                                   6. Display customer information and orders
                                   7. Exit
                                   """);
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1 -> addProductToInventory();
                    case 2 -> displayAvailableProducts();
                    case 3 -> addCustomer();
                    case 4 -> placeOrder();
                    case 5 -> calculateTotalPrice();
                    case 6 -> displayCustomerInfoAndOrders();
                    case 7 -> {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please choose a number between 1 and 7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void addProductToInventory() {
        try {
            System.out.println("Enter product name:");
            String name = scanner.nextLine();
            System.out.println("Enter product price:");
            String priceInput = scanner.nextLine();
            double price = parsePriceInput(priceInput);
            System.out.println("Enter product quantity:");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            if (inventory.containsKey(name)) {
                System.out.println("Product with the same name already exists.");
                return;
            }

            Product product = new Product(name, price, quantity);
            inventory.put(name, product);
            System.out.println("Product added to inventory successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear the invalid input
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static double parsePriceInput(String priceInput) {
        if (priceInput.startsWith("$")) {
            // Remove '$' symbol and parse the remaining string as double
            return Double.parseDouble(priceInput.substring(1));
        } else {
            return Double.parseDouble(priceInput);
        }
    }

    private static void displayAvailableProducts() {
        System.out.println("Available Products:");
        inventory.values().forEach(product -> System.out.println(product.getName() + " - $" + product.getPrice()));
    }

    private static void addCustomer() {
        try {
            System.out.println("Enter customer name:");
            String name = scanner.nextLine();
            System.out.println("Enter customer email:");
            String email = scanner.nextLine();

            if (customers.containsKey(email)) {
                System.out.println("Customer with the same email already exists.");
                return;
            }

            Customer customer = new Customer(name, email);
            customers.put(email, customer);
            System.out.println("Customer added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void placeOrder() {
        try {
            System.out.println("Enter customer email:");
            String email = scanner.nextLine();
            System.out.println("Available Products:");
            displayAvailableProducts();
            System.out.println("Enter product name:");
            String productName = scanner.nextLine();
            System.out.println("Enter quantity:");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            Customer customer = customers.get(email);
            if (customer == null) {
                System.out.println("Customer with the provided email does not exist.");
                return;
            }

            Product product = inventory.get(productName);
            if (product == null) {
                System.out.println("Product with the provided name does not exist.");
                return;
            }

            Order order = new Order(customer, product, quantity);
            orders.add(order);
            System.out.println("Order placed successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear the invalid input
        }
    }

    private static void calculateTotalPrice() {
        try {
            System.out.println("Enter customer email:");
            String email = scanner.nextLine();
            System.out.println("Enter product name:");
            String productName = scanner.nextLine();

            Customer customer = customers.get(email);
            if (customer == null) {
                System.out.println("Customer with the provided email does not exist.");
                return;
            }

            Product product = inventory.get(productName);
            if (product == null) {
                System.out.println("Product with the provided name does not exist.");
                return;
            }

            double totalPrice = orders.stream()
                    .filter(order -> order.getCustomer().getEmail().equals(email)
                            && order.getProduct().getName().equals(productName))
                    .mapToDouble(Order::calculateTotalPrice)
                    .sum();

            System.out.println("Total price of the orders for customer " + email + " and product " + productName + ": $" + totalPrice);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear the invalid input
        }
    }

    private static void displayCustomerInfoAndOrders() {
        try {
            System.out.println("Enter customer email:");
            String email = scanner.nextLine();

            Customer customer = customers.get(email);
            if (customer == null) {
                System.out.println("Customer with the provided email does not exist.");
                return;
            }

            System.out.println("Orders placed by " + customer.getName() + ":");
            orders.stream()
                    .filter(order -> order.getCustomer().getEmail().equals(email))
                    .forEach(order -> System.out.println("Product: " + order.getProduct().getName() + ", Quantity: " + order.getQuantity()));
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear the invalid input
        }
    }
}
