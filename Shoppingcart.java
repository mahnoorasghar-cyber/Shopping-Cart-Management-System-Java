package com.mycompany.shoppingcart;
import java.util.ArrayList;
import java.util.Scanner;

class InvalidQuantityException extends Exception {
    public InvalidQuantityException(String message) {
        super(message);
    }
}

class EmptyCartException extends Exception {
    public EmptyCartException(String message) {
        super(message);
    }
}



class Product {

    private static int totalProductsCreated = 0;

    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        totalProductsCreated++;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setPrice(double price) {
        if (price >= 0) this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) this.quantity = quantity;
    }

    public static int getTotalProductsCreated() {
        return totalProductsCreated;
    }

    public double getFinalPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%-20s Rs. %7.2f  x%d", name, getFinalPrice(), quantity);
    }
}



class DiscountedProduct extends Product {

    private double discountPercent;

    public DiscountedProduct(String name, double price, int quantity, double discountPercent) {
        super(name, price, quantity);
        this.discountPercent = discountPercent;
    }

    @Override
    public double getFinalPrice() {
        return getPrice() * (1 - discountPercent / 100.0);
    }

    @Override
    public String toString() {
        return String.format("%-20s Rs. %7.2f  x%d  [%.0f%% OFF -> Rs. %.2f]",
                getName(), getPrice(), getQuantity(),
                discountPercent, getFinalPrice());
    }
}


class Cart {

    private static Cart instance = null;

    private ArrayList<Product> items;
    private static int totalOrdersPlaced = 0;

    private Cart() {
        items = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addProduct(Product p) throws InvalidQuantityException {
        if (p.getQuantity() <= 0) {
            throw new InvalidQuantityException(
                    "Cannot add '" + p.getName() + "' — quantity must be > 0!");
        }
        items.add(p);
        System.out.println("  ✔ Added: " + p.getName());
    }

    public boolean removeProduct(String name) {
        return items.removeIf(p -> p.getName().equalsIgnoreCase(name));
    }

    public double getTotal() {
        double total = 0;
        for (Product p : items) {
            total += p.getFinalPrice() * p.getQuantity();
        }
        return total;
    }

    public void checkout() throws EmptyCartException {
        if (items.isEmpty()) {
            throw new EmptyCartException("Cart is empty! Add items before checkout.");
        }
        totalOrdersPlaced++;
        System.out.println("\n Order #" + totalOrdersPlaced + " placed successfully!");
        printReceipt();
        items.clear();
    }

    public void printReceipt() {
        System.out.println("\n ______________________________________________");
        System.out.println("              SHOPPING RECEIPT                     ");
        System.out.println(" ______________________________________________");

        for (Product p : items) {
            System.out.println(" ║  " + p);
        }

        System.out.println(" ______________________________________________");
        System.out.printf("  %-30s Rs. %10.2f  %n", "TOTAL:", getTotal());
        System.out.println(" ______________________________________________");
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public int size() { return items.size(); }

    public static int getTotalOrdersPlaced() {
        return totalOrdersPlaced;
    }
}



public class Shoppingcart {

   public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Cart cart = Cart.getInstance();

    while (true) {
        System.out.println("\n\t\t SHOPPING CART MENU \t\t");
        System.out.println("1. Add Product");
        System.out.println("2. Remove Product");
        System.out.println("3. View Receipt");
        System.out.println("4. Checkout");
        System.out.println("5. Exit");
        System.out.print("Choose option: ");

        int choice = sc.nextInt();
        sc.nextLine(); 

        try {
            if (choice == 1) {
                System.out.print("Enter product name: ");
                String name = sc.nextLine();

                System.out.print("Enter price: ");
                double price = sc.nextDouble();

                System.out.print("Enter quantity: ");
                int quantity = sc.nextInt();

                System.out.print("Discounted product? (y/n): ");
                sc.nextLine();
                String ans = sc.nextLine();

                Product p;
                if (ans.equalsIgnoreCase("y")) {
                    System.out.print("Enter discount percent: ");
                    double discount = sc.nextDouble();
                    sc.nextLine();
                    p = new DiscountedProduct(name, price, quantity, discount);
                } else {
                    p = new Product(name, price, quantity);
                }

                cart.addProduct(p);
            }
            else if (choice == 2) {
                System.out.print("Enter product name to remove: ");
                String name = sc.nextLine();
                if (cart.removeProduct(name))
                    System.out.println("Product removed.");
                else
                    System.out.println("Product not found.");
            }
            else if (choice == 3) {
                if (cart.isEmpty()) System.out.println("Cart is empty.");
                else cart.printReceipt();
            }
            else if (choice == 4) {
                cart.checkout();
            }
            else if (choice == 5) {
                System.out.println("Exiting...");
                break;
            }
            else {
                System.out.println("Invalid option.");
            }
        } catch (InvalidQuantityException | EmptyCartException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            sc.nextLine();
        }
    }

}
}