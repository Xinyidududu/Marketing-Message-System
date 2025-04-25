import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

/**
 * A User Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */

public class User {
    public static final Object sellerObj = new Object();
    public static final Object messageObj = new Object();
    public static final Object cusObj = new Object();
    private String username;
    private String email;
    private String password;
    private int id;

    public User(String username, String email, String password, int id) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setID(int idSet) {
        this.id = idSet;
    }

    public int getID() {
        return id;
    }

    public int getAndVerifyValidNumber(int minNum, int maxNum) {
        // This method takes in input and evaluates it. If valid, it returns the given int.
        // If invalid, it prints and error message and returns -1
        Scanner scan = new Scanner(System.in);
        int validNumber = -1;
        try {
            validNumber = Integer.parseInt(scan.nextLine());
            if (validNumber > maxNum || validNumber < minNum) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
        return validNumber;
    }

    public boolean verifyUniqueEmail(String checkEmail) {
        Seller seller = new Seller();
        Customer customer = new Customer();
        ArrayList<String> customerStringArrayList = seller.getFullCustomerList();
        for (int i = 0; i < customerStringArrayList.size(); i++) {
            String currentCustomerEmail = seller.getCustomerEmailFromCustomerString(customerStringArrayList.get(i));
            if (checkEmail.equals(currentCustomerEmail)) {
                return false;
            }
        }
        ArrayList<String> sellerStringArrayList = seller.getFullSellerFile();
        for (int i = 0; i < sellerStringArrayList.size(); i++) {
            if (sellerStringArrayList.get(i).contains("Seller<")) {
                String currentSellerEmail = seller.getSellerEmailFromSellerString(sellerStringArrayList.get(i));
                if (checkEmail.equals(currentSellerEmail)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean verifyUniqueUsername(String usedName) {
        Seller seller = new Seller();
        Customer customer = new Customer();
        ArrayList<String> customerStringArrayList = seller.getFullCustomerList();
        for (int i = 0; i < customerStringArrayList.size(); i++) {
            String s = customerStringArrayList.get(i);
            String currentCustomerUsername = seller.getCustomerUsernameFromCustomerString(s);
            if (usedName.equals(currentCustomerUsername)) {
                return false;
            }
        }
        ArrayList<String> sellerStringArrayList = seller.getFullSellerFile();
        for (int i = 0; i < sellerStringArrayList.size(); i++) {
            if (sellerStringArrayList.get(i).contains("Seller<")) {
                String currentSellerUsername = seller.getSellerUsernameFromSellerString(sellerStringArrayList.get(i));
                if (usedName.equals(currentSellerUsername)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void editMessage(String message, String newMessage) {
        File file = new File("messageData.txt");
        ArrayList<String> messages = new ArrayList<>();
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line;
            while (true) {
                line = bfr.readLine();
                if (line == null) {
                    break;
                }
                messages.add(line);
            }
            bfr.close();
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
            for (String s : messages) {
                if (s.contains("ID:" + this.getID()) && s.contains(message))
                    s = s.replace(message, newMessage);
                bfw.write(s + "\n");
            }
            bfw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(String message) {
        File file = new File("messageData.txt");
        ArrayList<String> messages = new ArrayList<>();
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line;
            while (true) {
                line = bfr.readLine();
                if (line == null) {
                    break;
                }
                messages.add(line);
            }
            bfr.close();
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
            for (String s : messages) {
                if (s.contains("SenderID:" + this.getID()) && s.contains(message))
                    s = s.replace("DeletedBySender:No", "DeletedBySender:Yes");
                else if (s.contains("RecieverID:" + this.getID()) && s.contains(message))
                    s = s.replace("DeletedByReciever:No", "DeletedByReciever:Yes");
                bfw.write(s + "\n");
            }
            bfw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
