import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A Customer Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */
public class Customer extends User {
    private ArrayList<Integer> blockedPeopleArrayList; // people being blocked by customer
    private ArrayList<Integer> blockedByArrayList; // who blocked the current customer.
    private ArrayList<Integer> seesAsInvisibleArrayList;

    public Customer() {
        // Constructor for if customer profile has just been created and does not have
        // ID
        super("", "", "", 0);
        this.blockedPeopleArrayList = new ArrayList<Integer>();
        this.blockedByArrayList = new ArrayList<Integer>();
        this.seesAsInvisibleArrayList = new ArrayList<Integer>();
    }

    public Customer(String username, String email, String password, ArrayList<Integer> blockedPeopleArrayList,
                    ArrayList<Integer> blockedByArrayList,
                    ArrayList<Integer> seesAsInvisibleArrayList) {
        // Constructor for if customer profile has just been created and does not have
        // ID
        super(username, email, password, 0);
        super.setID(generateNewID());
        this.blockedPeopleArrayList = blockedPeopleArrayList;
        this.blockedByArrayList = blockedByArrayList;
        this.seesAsInvisibleArrayList = seesAsInvisibleArrayList;
    }

    public Customer(String username, String email, String password, int id, ArrayList<Integer> blockedPeopleArrayList,
                    ArrayList<Integer> blockedByArrayList, ArrayList<Integer> seesAsInvisibleArrayList) {
        // Constructor for creating customer objects of pre-existing customer profile
        // (already have IDs)
        super(username, email, password, id);
        this.blockedPeopleArrayList = blockedPeopleArrayList;
        this.blockedByArrayList = blockedByArrayList;
        this.seesAsInvisibleArrayList = seesAsInvisibleArrayList;
    }

    public ArrayList<Integer> getBlockedPeople() {
        return blockedPeopleArrayList;
    }

    public void setBlockedPeople(ArrayList<Integer> blockedPeopleList) {
        this.blockedPeopleArrayList = blockedPeopleList;
    }

    public ArrayList<Integer> getBlockedBy() {
        return blockedByArrayList;
        // may need to be changed
    }

    public void setBlockedBy(ArrayList<Integer> blockedByList) {
        this.blockedByArrayList = blockedByList;
    }

    public void addBlockedSeller(int sellerID) {
        ArrayList<Integer> blockedSellersID = getBlockedPeople();
        blockedSellersID.add(sellerID);
        setBlockedBy(blockedSellersID);
    }

    public ArrayList<Integer> getSeesAsInvisible() {
        return seesAsInvisibleArrayList;
    }

    public void setSeesAsInvisible(ArrayList<Integer> seesAsInvisibleList) {
        this.seesAsInvisibleArrayList = seesAsInvisibleList;
    }

    public Customer verifyCustomerLogin(String email, String password) {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            Customer customer = new Customer();
            // Seller seller = new Seller();
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("Email:" + email + "|" + "Password:" + password)) {
                        customer = new Customer(getCustomerUsernameFromCustomerString(line), email, password,
                                getCustomerID(email, password), getBlockedPeopleArrayListFromCustomerString(line),
                                getBlockedByArrayListFromCustomerString(line),
                                getSeesAsInvisibleArrayListFromCustomerString(line));
                        return customer;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Customer attempts to log in. If email and password match existing account,
            // return
            return customer;
        }
    }

    private int getCustomerID(String email, String password) {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            int customerID = 0;
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("Email:" + email + "|" + "Password:" + password)) {
                        String d = line.split("\\|")[0];
                        d = d.substring(3);
                        customerID = Integer.parseInt(d);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return customerID;
        }
    }

    public String getCustomerUsernameFromCustomerString(String customerString) {
        synchronized (cusObj) {
            //customerString looks like -->
            //ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:0|hasBlocked:0|seesAsInvisible:1,5
            String username = "";
            try {
                File file = new File("customerData.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("|Username:" + username)) {
                        String[] splitCustomerString = customerString.split("\\|");
                        String[] usernameStringList = splitCustomerString[1].split(":");
                        username = usernameStringList[1];
                    }
                }

                return username;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<Integer> getSeesAsInvisibleArrayListFromCustomerString(String customerString) {
        // customerString looks like -->
        //ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:0|hasBlocked:0|seesAsInvisible:1,5
        ArrayList<Integer> seesAsInvisibleList = new ArrayList<>();
        synchronized (cusObj) {
            try {
                File file = new File("customerData.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                // ArrayList<String> numbersAsStringsList = new ArrayList<>();
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("|seesAsInvisible:" + getSeesAsInvisible())) {
                        String[] splitSellerString = customerString.split("\\|");
                        String[] seesAsInvisibleStringList = splitSellerString[6].split(":");
                        String[] numbersAsStringsList = seesAsInvisibleStringList[1].split(",");
                        int currentInt = 0;
                        for (int i = 0; i < numbersAsStringsList.length; i++) {
                            currentInt = Integer.parseInt(numbersAsStringsList[i]);
                            if (currentInt == 0) {
                                return seesAsInvisibleList;
                            }
                            seesAsInvisibleList.add(currentInt);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return seesAsInvisibleList;
        }
    }

    public ArrayList<Integer> getBlockedByArrayListFromCustomerString(String customerString) {
        //customerString looks like -->
        //ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:0|hasBlocked:0|seesAsInvisible:1,5
        ArrayList<Integer> blockedByList = new ArrayList<>();
        synchronized (cusObj) {
            try {
                File file = new File("customerData.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String[] numbersAsStringsList;
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("|wasBlockedBy:" + getBlockedBy())) {
                        String[] splitSellerString = customerString.split("\\|");
                        String[] blockedByeStringList = splitSellerString[4].split(":");
                        numbersAsStringsList = blockedByeStringList[1].split(",");
                        int currentInt = 0;
                        for (int i = 0; i < numbersAsStringsList.length; i++) {
                            currentInt = Integer.parseInt(numbersAsStringsList[i]);
                            if (currentInt == 0) {
                                return blockedByList;
                            }
                            blockedByList.add(currentInt);
                        }
                        break;
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return blockedByList;
        }
    }

    public ArrayList<Integer> getBlockedPeopleArrayListFromCustomerString(String customerString) {
        //customerString looks like -->
        //ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:0|hasBlocked:0|seesAsInvisible:1,5

        ArrayList<Integer> hasBlockedArrayList = new ArrayList<>();
        synchronized (cusObj) {
            try {
                File file = new File("customerData.txt");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("|hasBlocked:" + getBlockedPeople())) {
                        String[] splitSellerString = customerString.split("\\|");
                        String[] blockedByStringList = splitSellerString[5].split(":");
                        String[] numbersAsStringsList = blockedByStringList[1].split(",");
                        System.out.println(numbersAsStringsList);
                        int currentInt = 0;
                        for (int i = 0; i < numbersAsStringsList.length; i++) {
                            currentInt = Integer.parseInt(numbersAsStringsList[i]);
                            if (currentInt == 0) {
                                return hasBlockedArrayList;
                            }
                            hasBlockedArrayList.add(currentInt);
                        }
                        break;
                    }
                }
                return hasBlockedArrayList;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int generateNewID() {
        Seller seller = new Seller();
        ArrayList<String> customerStringArrayList = seller.getFullCustomerList();
        int largestCustomerID = -1;
        int newID = -1;
        for (int i = 0; i < customerStringArrayList.size(); i++) {
            int currentCustomerID = seller.getCustomerIDFromString(customerStringArrayList.get(i));
            if (currentCustomerID > largestCustomerID) {
                largestCustomerID = currentCustomerID;
            }
        }
        newID = largestCustomerID + 2;
        return newID;
    }

    public ArrayList<String> getCustomer() {
        ArrayList<String> customerData = new ArrayList<>();
        return customerData;
    }

    public Customer createCustomerAccount(String username, String email, String password) {
        synchronized (cusObj) {
            Customer customer = new Customer(username, email, password, generateNewID(), getBlockedPeople(),
                    getBlockedBy(), getSeesAsInvisible());
            File file = new File("customerData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));

                String infoCustomer = String.format("ID:%d|Username:%s|Email:%s|Password:%s|wasBlockedBy:" +
                        "|hasBlocked:|seesAsInvisible:", generateNewID(), username, email, password);
                // String line = bfr.readLine();
                bfw.write(infoCustomer);
                bfw.newLine();
                bfr.close();
                bfw.close();
            } catch (FileNotFoundException e) {
                System.out.println("There is no such file, creating a new one...");
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return customer;
        }
    }

    public void editUserName(String username, String newUsername) {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.contains("Username:" + username + "|")) {
                        line = line.replace("Username:" + username + "|", "Username:"
                                + newUsername + "|");
                        lines.set(i, line);
                    }
                }
                Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void editPassword(String password, String newPassword) {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.contains("Password:" + password + "|")) {
                        line = line.replace("Password:" + password + "|", "Password:"
                                + newPassword + "|");
                        lines.set(i, line);
                    }
                }
                Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteAccount(String email) {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            ArrayList<String> allAccounts = new ArrayList<>();
            ArrayList<String> temp = new ArrayList<>(); // create temporary list
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    allAccounts.add(line);
                }
                for (int i = 0; i < allAccounts.size(); i++) {
                    if (!allAccounts.get(i).contains(email)) { // keep lines that don't contain the email
                        temp.add(allAccounts.get(i));
                    }
                }
                bfr.close();

                // write lines from temp back to file
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (String account : temp) {
                    bfw.write(account);
                    bfw.newLine();
                }
                bfw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getMessageHistory() {
        synchronized (messageObj) {
            ArrayList<String> allHistoryCustomer = new ArrayList<>();
            File file = new File("messageData.txt");
            String idCustomer = String.valueOf(this.getID());
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    String sender = line.split("\\|")[1];
                    String receiver = line.split("\\|")[3];
                    if (sender.contains(idCustomer)) {
                        if (line.contains("DeletedBySender:No")) {
                            allHistoryCustomer.add(line);
                        }
                    } else if (receiver.contains(idCustomer)) {
                        if (line.contains("DeletedByReciever:No") || line.contains("DeletedByReceiver:No")) {
                            allHistoryCustomer.add(line);
                        }
                    }
                }

                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return allHistoryCustomer;
        }
    }

    public ArrayList<String> getSpecificMessageHistory(int sellerId) {
        ArrayList<String> allHistoryCustomer = getMessageHistory();
        ArrayList<String> messageHistory = new ArrayList<>();
        for (String s : allHistoryCustomer) {
            if (s.contains("ID:" + sellerId + ".")) {
                messageHistory.add(s);
            }
        }
        return messageHistory;
    }

    public String stringToMessage(String string) {
        return string.substring(string.indexOf("Message:") + 8);
    }

    public String formatMessage(String s, String sellerName) {
        String name = sellerName;
        if (s.contains("SenderID:" + this.getID()))
            name = "You";
        return String.format("%s: %s", name, stringToMessage(s));
    }

    public ArrayList<String> getSellerList() {
        // What about those sellers that have been blocked?
        // UNFINISHED
        // Returns: %s, ID:%d --> e.g. Sniggy, ID:3
        ArrayList<Integer> blockedSellerArrayList = getBlockedPeople();
        ArrayList<Integer> invisiblePeopleArrayList = getSeesAsInvisible();
        ArrayList<String> sellerStringArrayList = new ArrayList<String>(); //
        synchronized (sellerObj) {
            File file = new File("sellerData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                // get list of all seller strings
                while ((line = bfr.readLine()) != null) {
                    if (line.contains("Seller<")) {
                        sellerStringArrayList.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // remove sellers the user sees as invisible
        if (invisiblePeopleArrayList.size() != 0) {
            for (int i = 0; i < invisiblePeopleArrayList.size(); i++) {
                int invisibleSellerID = invisiblePeopleArrayList.get(i); // ID of current invisible seller
                for (int j = 0; j < sellerStringArrayList.size(); j++) {
                    // iterates through whole seller string
                    if (invisibleSellerID == getSellerIDFromSellerString(sellerStringArrayList.get(j))) {
                        // remove the seller string from the list
                        sellerStringArrayList.remove(j);
                    }
                }
            }
        }
        if (blockedSellerArrayList.size() != 0) {
            for (int i = 0; i < blockedSellerArrayList.size(); i++) {
                int blockedSellerID = blockedSellerArrayList.get(i); // ID of current blocked seller
                for (int j = 0; j < sellerStringArrayList.size(); j++) {
                    // iterates through whole seller string
                    if (blockedSellerID == getSellerIDFromSellerString(sellerStringArrayList.get(j))) {
                        // remove the seller string from the list
                        sellerStringArrayList.remove(j);
                    }
                }
            }
        }

        ArrayList<String> sellerArrayList = new ArrayList<>();
        if (sellerStringArrayList.size() > 0) {
            for (int i = 0; i < sellerStringArrayList.size(); i++) {
                // Returns: %s, ID:%d --> e.g. Sniggy, ID:3
                String sellerDisplayString = String.format("%s, %s",
                        getSellerUsernameFromString(sellerStringArrayList.get(i)),
                        getSellerIDFromString(sellerStringArrayList.get(i)));
                sellerArrayList.add(sellerDisplayString);
            }
        }

        return sellerArrayList;
    }

    public String getSellerUsernameFromString(String sellerString) {
        // Input file will look like -->
        // Seller<ID:1|Username:snig|Email:snig@gmail.com|Password:snigIsGreat|wasBlockedBy:2,4
        // |hasBlocked:2,4,6|seesAsInvisible:2|number
        // of stores:3>
        String[] splitSellerString = sellerString.split("\\|");
        // ["Seller<ID:1", "Username:snig", "Email:snig@gmail.com",
        // "Password:snigIsGreat|wasBlockedBy:2,4|hasBlocked:2,4,6|seesAsInvisible:2|number
        // of stores:3>]
        String[] splitUsernameString = splitSellerString[1].split(":");
        String username = splitUsernameString[1];
        return username;
    }

    public String getSellerIDFromString(String sellerString) {
        String[] splitSellerString = sellerString.split("\\|");
        return splitSellerString[0].substring(7);
    }

    public String getUsernamesFromSellerArrayList(ArrayList<String> sellerArrayList) {
        String sellerUsernames = "";
        synchronized (sellerObj) {
            File file = new File("sellerData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                String blockedIDs = String.valueOf(getBlockedPeople());
                blockedIDs = blockedIDs.substring(1, blockedIDs.length() - 1);
                while ((line = bfr.readLine()) != null) {
                    sellerUsernames = line.split("\\|")[1];
                    sellerArrayList.add(sellerUsernames.substring(8));
                }

                for (int i = 0; i < sellerArrayList.size(); i++) {
                    String blockedSeller = blockedIDs.split(",")[i];
                    String banName = "";
                    while ((line = bfr.readLine()) != null) {
                        String firstElement = line.split("\\|")[0];
                        if (firstElement.contains(blockedSeller)) {
                            banName = line.split("\\|")[1];
                            banName = banName.substring(8);
                        }
                    }
                    if (sellerArrayList.get(i).equalsIgnoreCase(banName)) {
                        sellerArrayList.remove(i);
                    }
                }
                for (int i = 0; i < sellerArrayList.size(); i++) {
                    sellerUsernames += (i + 1) + ". " + sellerArrayList.get(i) + "\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sellerUsernames;
        }
    }

    public int getSellerIDFromSellerString(String sellerString) {
        String idString = "";
        int id = 0;
        if (sellerString.contains("Store")) {
            idString = sellerString.substring(sellerString.indexOf(":") + 1, sellerString.indexOf(","));
            id = Integer.parseInt(idString);
        } else if (sellerString.contains("Seller")) {
            idString = sellerString.substring(sellerString.indexOf(":") + 1, sellerString.indexOf("|"));
            id = Integer.parseInt(idString);
        }

        return id;
    }

    public String displayDashboard(int id) {
        // Read messageData so we can figure out the number of messages the seller sent
        // to the customer
        // Read sellerData so we can figure out the number of messages the customer sent
        // to the seller
        synchronized (sellerObj) {
            synchronized (messageObj) {
                String allStringSent = "";
                try {
                    File file = new File("messageData.txt");
                    File file1 = new File("sellerData.txt");
                    BufferedReader bfr = new BufferedReader(new FileReader(file));
                    BufferedReader reader = new BufferedReader(new FileReader(file1));
                    String line;
                    int customerIDFuckingIndex = 0;
                    String findNumMessages = "";
                    String storeName = "";
                    String storeID = "";
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("Store<ID:")) {
                            storeName = line.split(",")[1];
                            storeID = line.split(",")[0];
                            storeID = storeID.substring(9);
                            storeName = storeName.substring(5, storeName.length() - 1);
                            line = reader.readLine();
                            if (line.contains(String.valueOf(id))) {
                                String[] ids = line.split(",");
                                // String storeID = ids[0].substring(8);
                                for (int i = 0; i < ids.length; i++) {
                                    if (Objects.equals(ids[i], String.valueOf(id))) {
                                        customerIDFuckingIndex = i;
                                    }
                                }
                                findNumMessages = reader.readLine();
                                int numbersSent =
                                        Integer.parseInt(findNumMessages.split(",")[customerIDFuckingIndex]);
                                String stringSent = "Store: " + storeName + " Received "
                                        + numbersSent + " messages from you.\n";
                                allStringSent += stringSent;
                            }
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return allStringSent;
            }
        }
    }

    public String sortDashboard() {
        String a = "";
        String dashB = displayDashboard(getID());
        List<String> messagesList;
        messagesList = Arrays.asList(dashB.split("\n"));
        ArrayList<String> messages = new ArrayList<>(messagesList);
        String messagesSorted = "";
        int max;
        int numberSent;
        String temp = "";
        while (!messages.isEmpty()) {
            max = 0;
            for (String m : messages) {
                numberSent = Integer.parseInt(m.substring(m.indexOf("Received") + 9, m.indexOf("message") - 1));
                if (numberSent > max) {
                    max = numberSent;
                    temp = m;
                }
            }
            messages.remove(temp);
            messagesSorted += temp + "\n";
        }
        return messagesSorted;
    }


    public void sendMessageToSeller(int sellerId, String message) {
//        File file = new File("messageData.txt");
//        try {
//            BufferedReader bfr = new BufferedReader(new FileReader(file));
//            BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
//            String line = "";
//            int currentTime = 0;
//            while (true) {
//                line = bfr.readLine();
//                // Time:7|SenderID:2|DeletedBySender:No|ReceiverID:1.2|DeletedByReceiver:No|Message:testing aslkjdfl
//                String[] splitMessageString = line.split("\\|");
//                String[] splitTime = splitMessageString[0].split(":");
//                currentTime = Integer.parseInt(splitTime[1]);
//                if (line == null) {
//                    currentTime++;
//                    break;
//                }
//
//            }
//            bfw.write("Time:" + currentTime + "|SenderID:" + getID() + "|DeletedBySender:No|ReceiverID:"
//                    + id + "|DeletedByReceiver:No|Message:" + message + "\n");
//            bfw.close();
//            bfr.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        synchronized (sellerObj) {
            File file = new File("messageData.txt");
            try (BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true))) {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                int lineNumb = 1;
                String line;
                while ((line = bfr.readLine()) != null) {
                    lineNumb++;
                }
                bfw.write("Time:" + lineNumb + "|SenderID:" + getID() + "|DeletedBySender:No|ReceiverID:"
                        + sellerId + ".0" + "|DeletedByReceiver:No|Message:" + message + "\n");
                bfw.close();
                bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessageToStore(int id, int storeID, String message) {
        synchronized (messageObj) {
            File file = new File("messageData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
                String line;
                int lineNumb = 0;
                while (true) {
                    line = bfr.readLine();
                    lineNumb++;
                    if (line == null) {
                        break;
                    }

                }
                bfw.write("Time:" + lineNumb + "|SenderID:" + getID() + "|DeletedBySender:No|ReceiverID:"
                        + id + "." + storeID + "|DeletedByReceiver:No|Message:" + message + "\n");
                bfw.close();
                bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Seller s = new Seller();
            s.findAndSetSeller(id);
            s.updateDash(storeID, message, this.getID());
        }
    }

    public String messageToString() {
        String messageString = String.format("Customer<ID: %d, Name: %s, Password: %s, Blocked by: %s, Block: %s",
                getID(), getUsername(), getPassword(), this.blockedByArrayList, this.blockedPeopleArrayList);
        return messageString;
    }

    public ArrayList<String> getStoreList() {
        // returns all of the store strings available to message
        // Format of string: Store: %s (ID.%d.%d)
        ArrayList<String> storeArrayList = new ArrayList<String>();
        String allStringSent = "";
        synchronized (sellerObj) {
            try {
                File file = new File("sellerData.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                String storeName = "";
                int sellerID = -1;
                int storeID = -1;
                String singleStoreString = "";
                line = reader.readLine();
                do {
                    if (line.contains("Seller<ID:")) {
                        String sellerIDString = line.substring(line.indexOf(":") + 1, line.indexOf("|"));
                        sellerID = Integer.parseInt(sellerIDString);
                    }

                    if (line.contains("Store<ID:")) {
                        storeID = getStoreIDFromString(line);
                        storeName = getStoreNameFromString(line);
                        singleStoreString = String.format("Store: %s (ID.%d.%d)", storeName, sellerID, storeID);
                        storeArrayList.add(singleStoreString);
                    }
                    if (line != null) {
                        line = reader.readLine();
                    }
                } while (line != null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return storeArrayList;
        }
    }

    private String getStoreNameFromString(String storeString) {
        // Given a seller string, should return the store name in that string
        // Given string looks like: Store<ID:1,Name:Sniggy Store>
        String partOne = storeString.substring(storeString.indexOf(",") + 1);
        String storeName = partOne.substring(partOne.indexOf(":") + 1, partOne.indexOf(">"));
        return storeName;
    }

    private int getStoreIDFromString(String storeString) {
        // Given a seller string, should return the ID in that string
        // Given string looks like: Store<ID:1,Name:Sniggy Store>
        int sellerID = -1;
        String[] storeList1 = storeString.split(":"); // ["Store<ID", "1,Name", "Sniggy Store>"]
        String[] storeList2 = storeList1[1].split(","); // ["1", "Name"]
        sellerID = Integer.parseInt(storeList2[0]);
        return sellerID;
    }

    public void addBlockedUser(int sellerID) {
        synchronized (sellerObj) {
            ArrayList<String> sellerFile = getSellerFile();
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter("sellerData.txt"));
                for (String currentSellerString : sellerFile) {
                    if (currentSellerString.contains("ID:" + sellerID + "|")) {
                        if (currentSellerString.contains("wasBlockedBy:|")) {
                            // if there is no one in the seller's hasBlocked list
                            currentSellerString = currentSellerString.replace("wasBlockedBy:|"
                                    , "wasBlockedBy:" + this.getID() + "|");
                        } else {
                            // Checks to make sure that we are not adding repeats
                            if (blockedPeopleArrayList.contains(sellerID)) {
                                return;
                            }
                            // adds sellerID to file
                            currentSellerString = currentSellerString.replace("|hasBlocked", ","
                                    + this.getID() + "|hasBlocked");
                        }
                    }
                    bfw.write(currentSellerString + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            blockedPeopleArrayList.add(sellerID);
        }
        synchronized (cusObj) {
            try {
                ArrayList<String> customerFile = getFullCustomerFile();
                File file = new File("customerData.txt");
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (String s : customerFile) {
                    if (s.contains("ID:" + this.getID())) {
                        boolean alreadyOnList = false;
                        if (s.contains(":|seesAsInvisible:")) {
                            s = s.replace(":|seesAsInvisible:", ":" + sellerID + "|seesAsInvisible:");
                        } else {
                            // ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:1
                            // |hasBlocked:|seesAsInvisible:
                            // check to see if the ID is already blocked
                            String[] splitCustomerString = s.split("hasBlocked:");
                            String[] splitBlocked = splitCustomerString[1].split("\\|");
                            String[] splitBlockedByList = splitBlocked[0].split(",");
                            for (int j = 0; j < splitBlockedByList.length; j++) {
                                int blockedID = Integer.parseInt(splitBlockedByList[j]);
                                if (blockedID == sellerID) {
                                    alreadyOnList = true;
                                }
                            }
                            if (!alreadyOnList) {
                                s = s.replace("|seesAsInvisible:", ","
                                        + sellerID + "|seesAsInvisible:");
                            }
                        }
                    }
                    bfw.write(s + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeBlockedUser(int sellerID) {
        String sub;
        Boolean user;
        String s1;
        synchronized (sellerObj) {
            ArrayList<String> sellerInfoArrayList = getSellerFile();
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Seller<ID:" + sellerID)) {
                    // Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:4|seesAsInvisible:
                    // |number of stores:3>
                    s1 = sellerInfoArrayList.get(i);
                    sub = s1.substring(s1.indexOf("wasBlockedBy:"), s1.indexOf("|",
                            s1.indexOf("wasBlockedBy:") + 1));
                    String sub1 = s1.substring(s1.indexOf("wasBlockedBy:"));
                    user = s1.contains("ID:" + sellerID + "|");
                    if (user && sub.contains("," + this.getID())) {
                        // case: you want 4, string looks like: wasBlockedBy:2,4,6|
                        sub1 = sub1.replace("," + this.getID(), "");
                        sellerInfoArrayList.set(i, s1.substring(0, s1.indexOf("wasBlockedBy:")) + sub1);
                    } else if (user && sub.contains(":" + this.getID() + ",")) {
                        // case: you want 4, string looks like: wasBlockedBy:4,6|
                        sub1 = sub1.replace(":" + this.getID() + ",", ":");
                        sellerInfoArrayList.set(i, s1.substring(0, s1.indexOf("wasBlockedBy:")) + sub1);
                    } else if (user && sub.contains(":" + this.getID())) {
                        // case: you want 4, string looks like: wasBlockedBy:4|
                        sub1 = sub1.replace(":" + this.getID() + "|", ":|");
                        sellerInfoArrayList.set(i, s1.substring(0, s1.indexOf("wasBlockedBy:")) + sub1);
                    }
                }
            }
            try {
                File file = new File("sellerData.txt");
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (String sellerLine : sellerInfoArrayList) {
                    bfw.write(sellerLine + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int indexOfCustomerID = blockedPeopleArrayList.indexOf(sellerID);
        if (indexOfCustomerID != -1) {
            // find index, plug index in
            blockedPeopleArrayList.remove(indexOfCustomerID);
        }
        synchronized (cusObj) {
            ArrayList<String> customerArrayList = getFullCustomerFile();
            String subCus;
            try {
                String currentCustomerString = "";
                File file = new File("customerData.txt");
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < customerArrayList.size(); i++) {
                    currentCustomerString = customerArrayList.get(i);
                    subCus = currentCustomerString.substring(currentCustomerString.indexOf("hasBlocked:")
                            , currentCustomerString.indexOf("|",
                                    currentCustomerString.indexOf("hasBlocked:") + 1));
                    for (int j = 0; j < customerArrayList.size(); j++) {
                        if (currentCustomerString.contains("ID:" + this.getID())) {
                            // if the line is for the customer we've been looking for
                            if (currentCustomerString.contains(":" + sellerID + "|seesAsInvisible:")) {
                                currentCustomerString = currentCustomerString.replace(":" + sellerID
                                        + "|seesAsInvisible:", ":|seesAsInvisible:");
                            } else if (currentCustomerString.contains("," + sellerID + "|seesAsInvisible:")) {
                                currentCustomerString = currentCustomerString.replace("," + sellerID
                                        + "|hasBlocked:", "|seesAsInvisible:");
                            } else if (subCus.contains("" + sellerID + ",")) {
                                String s = subCus;
                                subCus = subCus.replace(sellerID + ",", "");
                                currentCustomerString = currentCustomerString.replace(s, subCus);
                            }
                        }
                    }
                    bfw.write(currentCustomerString + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<String> getSellerFile() {
        synchronized (sellerObj) {
            ArrayList<String> sellerData = new ArrayList<>();
            try {
                String line;
                BufferedReader bfr = new BufferedReader(new FileReader("sellerData.txt"));
                while (true) {
                    line = bfr.readLine();
                    if (line == null)
                        break;
                    sellerData.add(line);
                }
                bfr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sellerData;
        }
    }

    public ArrayList<String> getFullCustomerFile() {
        synchronized (cusObj) {
            // Returns ArrayList of customerStrings
            ArrayList<String> customerList = new ArrayList<>();
            File file = new File("customerData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    customerList.add(line);
                }
            } catch (IOException e) {
                System.out.println("There are no Customers!");
            }
            return customerList;
        }
    }

    public ArrayList<String> getBlockedSellerNameList() {
        ArrayList<String> sellerList = getSellerFile();
        ArrayList<String> sellerNameList = new ArrayList<>();
        ArrayList<String> customerFile = getFullCustomerFile();
        String cusString = "";
        for (String customer : customerFile) {
            if (customer.contains("ID:" + this.getID()))
                cusString = customer;
        }
        blockedPeopleArrayList = getBlockedPeopleList(cusString);
        String temp;
        for (String s : sellerList) {
            for (int i : blockedPeopleArrayList) {
                if (s.contains("Seller<ID:" + i)) {
                    temp = s.substring(s.indexOf("Username:") + 9, s.indexOf("|",
                            s.indexOf("Username:") + 1));
                    temp += ", ID - " + i;
                    sellerNameList.add(temp);
                }
            }
        }
        return sellerNameList;
    }

    public ArrayList<Integer> getBlockedPeopleList(String line) {
        ArrayList<Integer> blockedList = new ArrayList<>();
        String blocked = line.substring(line.indexOf("hasBlocked:") + 11, line.indexOf("|",
                line.indexOf("hasBlocked:")
                        + 1));
        while (!blocked.isEmpty()) {
            if (blocked.indexOf(',') != -1) {
                blockedList.add(Integer.parseInt(blocked.substring(0, blocked.indexOf(','))));
                blocked = blocked.substring(blocked.indexOf(',') + 1);
            } else {
                blockedList.add(Integer.parseInt(blocked));
                break;
            }
        }
        return blockedList;
    }

}
