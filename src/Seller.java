import java.io.*;
import java.util.ArrayList;

/**
 * A Seller Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */

public class Seller extends User {
    private ArrayList<Integer> blockedPeopleArrayList;
    private ArrayList<Integer> blockedByArrayList;
    private ArrayList<Integer> seesAsInvisibleArrayList;
    private ArrayList<Store> storesArrayList;
    private ArrayList<String> sellerInfoArrayList; // might not need, but will help for updating seller file

    public Seller() {
        // Constructor for new seller account without any data, useful for creating and getting sellerData array list
        super("", "", "", 0);
        this.blockedPeopleArrayList = new ArrayList<Integer>();
        this.blockedByArrayList = new ArrayList<Integer>();
        this.seesAsInvisibleArrayList = new ArrayList<Integer>();
        this.sellerInfoArrayList = getFullSellerFile();

    }

    public Seller(String username, String email, String password) {
        super(username, email, password, 0);
        synchronized (sellerObj) {
            // Constructor for new seller account
            super.setID(generateNewID());
            this.blockedPeopleArrayList = new ArrayList<>();
            this.blockedByArrayList = new ArrayList<>();
            this.seesAsInvisibleArrayList = new ArrayList<>();
            this.storesArrayList = new ArrayList<>();
            this.sellerInfoArrayList = getFullSellerFile();
            addNewToFile();
        }
    }

    public Seller(String userName, String email, String password, int id,
                  ArrayList<Store> storesArrayList,
                  ArrayList<Integer> blockedPeopleArrayList,
                  ArrayList<Integer> blockedByArrayList,
                  ArrayList<Integer> seesAsInvisibleArrayList) {
        // Constructor for existing seller account (has id)
        super(userName, email, password, id);
        this.blockedPeopleArrayList = blockedPeopleArrayList;
        this.blockedByArrayList = blockedByArrayList;
        this.seesAsInvisibleArrayList = seesAsInvisibleArrayList;
        this.storesArrayList = storesArrayList;
        this.sellerInfoArrayList = getFullSellerFile();
    }

    public void addNewToFile() {
        synchronized (sellerObj) {
            File file = new File("sellerData.txt");
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
                bfw.write(formatSeller());
                bfw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Integer> getBlockedPeople() {
        return blockedPeopleArrayList;
    }

    public void setBlockedPeople(ArrayList<Integer> blockedPeopleList) {
        this.blockedPeopleArrayList = blockedPeopleList;
        // may need to be changed
    }

    public void setBlockedBy(ArrayList<Integer> blockedByList) {
        this.blockedByArrayList = blockedByList;
    }


    public ArrayList<Integer> getBlockedBy() {
        return blockedByArrayList;
    }

    public ArrayList<String> getFullSellerFile() {
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

    public int generateNewID() {
        synchronized (sellerObj) {
            ArrayList<String> sellerStringArrayList = getFullSellerFile();
            int largestSellerID = -1;
            int newID = -1;
            for (int i = 0; i < sellerStringArrayList.size(); i++) {
                String currentSellerString = sellerStringArrayList.get(i);
                if (currentSellerString.contains("Seller<")) {
                    int currentCustomerID = getSellerIDFromSellerString(currentSellerString);
                    if (currentCustomerID > largestSellerID) {
                        largestSellerID = currentCustomerID;
                    }
                }
            }
            newID = largestSellerID + 2;
            return newID;
        }
    }

    public ArrayList<String> getSellers() {
        ArrayList<String> sellerData = new ArrayList<String>();
        for (String line : sellerInfoArrayList) {
            if (line.contains("Seller<"))
                sellerData.add(line);
        }
        return sellerData;
    }

    public boolean findAndSetSeller(int id) {
        // can be used in main to display dashboards with just id for customers, create new seller object,
        // set values using id, and call the dashboard methods
        String blocked;
        ArrayList<Integer> blockedList = new ArrayList<Integer>();
        String blockedBy;
        String invis;
        ArrayList<Integer> whoBlockedBy = new ArrayList<Integer>();
        ArrayList<Integer> invisible = new ArrayList<>();
        if (id % 2 != 1) {
            return false;
        } //if id isnt seller id return false
        try {
            String line;
            synchronized (sellerObj) {
                BufferedReader bfr = new BufferedReader(new FileReader("sellerData.txt"));
                while (true) {
                    line = bfr.readLine();
                    if (line == null)
                        return false; // if seller with that id isnt found, it will end
                    else if (line.indexOf("Seller<ID:" + id) != -1)
                        break;
                }
                bfr.close();
            }
            String username = line.substring(line.indexOf("Username:") + 9, line.indexOf('|', line.indexOf('|') + 1));
            String email = line.substring(line.indexOf("Email:") + 6, line.indexOf('|', line.indexOf("Email:") + 1));
            String password = line.substring(line.indexOf("Password:") + 9, line.indexOf('|'
                    , line.indexOf("Password:") + 1));
            blockedBy = line.substring(line.indexOf("wasBlockedBy:") + 13, line.indexOf("|"
                    , line.indexOf("wasBlockedBy:")));
            blocked = line.substring(line.indexOf("hasBlocked:") + 11, line.indexOf("|"
                    , line.indexOf("hasBlocked:") + 1));
            invis = line.substring(line.indexOf("seesAsInvisible:") + 16, line.indexOf("|"
                    , line.indexOf("seesAsInvisible:") + 1));
            while (!blocked.isEmpty()) {
                if (blocked.indexOf(',') != -1) {
                    blockedList.add(Integer.parseInt(blocked.substring(0, blocked.indexOf(','))));
                    blocked = blocked.substring(blocked.indexOf(',') + 1);
                } else {
                    blockedList.add(Integer.parseInt(blocked));
                    break;
                }
            }
            while (!blockedBy.isEmpty()) {
                if (blockedBy.indexOf(',') != -1) {
                    whoBlockedBy.add(Integer.parseInt(blockedBy.substring(0, blockedBy.indexOf(','))));
                    blockedBy = blockedBy.substring(blockedBy.indexOf(',') + 1);
                } else {
                    whoBlockedBy.add(Integer.parseInt(blockedBy));
                    break;
                }
            }
            while (!invis.isEmpty()) {
                if (invis.contains(",")) {
                    invisible.add(Integer.parseInt(invis.substring(0, invis.indexOf(','))));
                    invis = invis.substring(invis.indexOf(',') + 1);
                } else {
                    invisible.add(Integer.parseInt(invis));
                    break;
                }
            }
            this.setUsername(username);
            this.setEmail(email);
            this.setPassword(password);
            this.setID(id);
            this.blockedByArrayList = whoBlockedBy;
            this.blockedPeopleArrayList = blockedList;
            this.seesAsInvisibleArrayList = invisible;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // error, didnt set variables
        }
        return true; // seller successfully set to this seller with id
    }


    public Seller verifySellerLogin(String email, String password) {
        this.sellerInfoArrayList = getFullSellerFile();
        Seller seller = new Seller();
        int id = 0;
        String emailLine;
        String passwordLine;
        for (String string : sellerInfoArrayList) {
            if (string.contains("Seller<")) {
                emailLine = string.substring(string.indexOf("Email:"), string.indexOf("|Password:"));
                passwordLine = string.substring(string.indexOf("Password:"), string.indexOf("|was"));
                if (emailLine.equals("Email:" + email) && passwordLine.equals("Password:" + password)) {
                    id = Integer.parseInt(string.substring(string.indexOf("ID:") + 3, string.indexOf("|")));
                    break;
                }
            }
        }
        if (id != 0) {
            seller.findAndSetSeller(id);
        }
        return seller;
    }

    public ArrayList<Integer> getSeesAsInvisibleArrayListFromSellerString(String sellerString) {
        // sellerString looks like --> Seller<ID:1|Username:snig|Email:snig@gmail.com|Password:snigIsGreat
        // |wasBlockedBy:2,4|hasBlocked:2,4,6|seesAsInvisible:2|number of stores:3>
        ArrayList<Integer> seesAsInvisibleList = new ArrayList<>();
        /*String[] splitSellerString = sellerString.split("\\|");
        String[] seesAsInvisibleStringList = splitSellerString[6].split(":");
        String[] numbersAsStringsList = seesAsInvisibleStringList[1].split(",");
        int currentInt = 0;
        for (int i = 0; i < numbersAsStringsList.length; i++) {
            currentInt = Integer.parseInt(numbersAsStringsList[i]);
            if (currentInt == 0) {
                return seesAsInvisibleArrayList;
            }
            seesAsInvisibleArrayList.add(currentInt);
        }
        return seesAsInvisibleArrayList;*/
        String invis = sellerString.substring(sellerString.indexOf("seesAsInvisible:") + 16, sellerString.indexOf("|"
                , sellerString.indexOf("seesAsInvisible:") + 1));
        while (!invis.isEmpty()) {
            if (invis.contains(",")) {
                seesAsInvisibleList.add(Integer.parseInt(invis.substring(0, invis.indexOf(','))));
                invis = invis.substring(invis.indexOf(',') + 1);
            } else {
                seesAsInvisibleList.add(Integer.parseInt(invis));
                break;
            }
        }
        return seesAsInvisibleList;
    }

    public ArrayList<Integer> getBlockedByArrayListFromSellerString(String sellerString) {
        // sellerString looks like --> Seller<ID:1|Username:snig|Email:snig@gmail.com|Password:snigIsGreat
        // |wasBlockedBy:2,4|hasBlocked:2,4,6|seesAsInvisible:2|number of stores:3>
        ArrayList<Integer> blockedByList = new ArrayList<>();
        String[] splitSellerString = sellerString.split("\\|");
        String[] blockedByeStringList = splitSellerString[4].split(":");
        String[] numbersAsStringsList = blockedByeStringList[1].split(",");
        int currentInt = 0;
        for (int i = 0; i < numbersAsStringsList.length; i++) {
            currentInt = Integer.parseInt(numbersAsStringsList[i]);
            if (currentInt == 0) {
                return blockedByList;
            }
            blockedByList.add(currentInt);
        }
        return blockedByList;
    }

    public ArrayList<Integer> getBlockedPeopleArrayListFromSellerString(String sellerString) {
        // sellerString looks like --> Seller<ID:1|Username:snig|Email:snig@gmail.com|Password:snigIsGreat
        // |wasBlockedBy:2,4|hasBlocked:2,4,6|seesAsInvisible:2|number of stores:3>
        ArrayList<Integer> hasBlockedArrayList = new ArrayList<>();
        String[] splitSellerString = sellerString.split("\\|");
        String[] blockedByStringList = splitSellerString[5].split(":");
        String[] numbersAsStringsList = blockedByStringList[1].split(",");
        int currentInt = 0;
        for (int i = 0; i < numbersAsStringsList.length; i++) {
            currentInt = Integer.parseInt(numbersAsStringsList[i]);
            if (currentInt == 0) {
                return hasBlockedArrayList;
            }
            hasBlockedArrayList.add(currentInt);
        }
        return hasBlockedArrayList;
    }


    //findCustomer() // search customer file/arraylist? for that username
    public String messageToString(String message, int senderID, int recipientID) {
        return String.format("Message<SenderID:&d.0|DeletedBy1:No|ReceiverID:%d|DeletedBy2:No|Message:\"%s\">"
                , senderID, recipientID, message);
    }

    public String stringToMessage(String string) {
        return string.substring(string.indexOf("Message:") + 8);
    }

    public String formatSeller() {
        String blockedBy = "";
        String hasBlocked = "";
        String seesAsInvisible = "";
        getStoresList();
        for (int i = 0; i < blockedByArrayList.size(); i++) {
            blockedBy += "" + blockedByArrayList.get(i);
            if (i != blockedByArrayList.size() - 1)
                blockedBy += ",";
        }
        for (int i = 0; i < blockedPeopleArrayList.size(); i++) {
            hasBlocked += "" + blockedPeopleArrayList.get(i);
            if (i != blockedPeopleArrayList.size() - 1)
                hasBlocked += ",";
        }
        for (int i = 0; i < seesAsInvisibleArrayList.size(); i++) {
            hasBlocked += "" + seesAsInvisibleArrayList.get(i);
            if (i != seesAsInvisibleArrayList.size() - 1)
                seesAsInvisible += ",";
        }
        return String.format("Seller<ID:%d|Username:%s|Email:%s|Password:%s|wasBlockedBy:%s|hasBlocked:%s" +
                        "|seesAsInvisible:%s|number of stores:%d>",
                getID(), getUsername(), getEmail(), getPassword(), blockedBy, hasBlocked, seesAsInvisible,
                storesArrayList.size());
    }


    public void rewriteSellerDataFile() {
        synchronized (sellerObj) {
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream("sellerData.txt"));
                for (String currentSellerString : sellerInfoArrayList) {
                    pw.write(currentSellerString + "\n");
                }
                pw.close();
            } catch (Exception e) {
                System.out.println("Error Updating File!");
                e.printStackTrace();
            }
        }
    }

    /*public ArrayList<String> updatedStores() {
        ArrayList<String> stores = new ArrayList<>();
        for (Store s : storesArrayList)
            stores.addAll(s.formatStore());
        return stores;
    }*/

    public void createNewStore(String name) {
        synchronized (sellerObj) {
            this.sellerInfoArrayList = getFullSellerFile();
            getStoresList();
            int max = 0;
            for (Store s : storesArrayList) {
                if (s.getId() > max)
                    max = s.getId();
            }
            storesArrayList.add(new Store(max + 1, name));
            int index = 0;
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Seller<ID:" + this.getID())) {
                    sellerInfoArrayList.set(i, sellerInfoArrayList.get(i).replace("number of stores:"
                            + (storesArrayList.size() - 1), "number of stores:" + storesArrayList.size()));
                    index = i;
                    break;
                }
            }
            index += 1;
            ArrayList<String> formatted = storesArrayList.get(storesArrayList.size() - 1).formatStore();
            for (String s : formatted) {
                sellerInfoArrayList.add(index + (5 * (storesArrayList.size() - 1)), s);
                index++;
            }
            rewriteSellerDataFile();
        }
    }

    public ArrayList<String> getStores() {
        sellerInfoArrayList = getFullSellerFile();
        ArrayList<String> storeData = new ArrayList<String>();
        for (String line : sellerInfoArrayList) {
            if (line.contains("Store<"))
                storeData.add(line);
        }
        return storeData;
    }

    public boolean editSellerUsername(String newUsername) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            boolean isUniqueUsername = verifyUniqueUsername(newUsername);
            if (!isUniqueUsername) {
                return false;
            }
            String s = "";
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                s = sellerInfoArrayList.get(i);
                if (s.contains("Username:" + this.getUsername() + "|")) {
                    sellerInfoArrayList.set(i, s.replace("Username:" + this.getUsername()
                            + "|", "Username:" + newUsername + "|"));
                }
            }
            this.setUsername(newUsername);
            rewriteSellerDataFile();
            return true;
        }
    }

    public boolean editEmail(String newEmail) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            boolean isUniqueEmail = verifyUniqueEmail(newEmail);
            if (!isUniqueEmail) {
                return false;
            }
            String s = "";
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                s = sellerInfoArrayList.get(i);
                if (s.contains("Email:" + getEmail() + "|")) {
                    sellerInfoArrayList.set(i, s.replace("Email:" + getEmail()
                            + "|", "Email:" + newEmail + "|"));
                }
            }
            this.setEmail(newEmail);
            rewriteSellerDataFile();
            return true;
        }
    }

    public void editPassword(String newPass) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            String s = "";
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                s = sellerInfoArrayList.get(i);
                if (s.contains("Username:" + this.getUsername() + "|") && s.contains("Password:" + getPassword() + "|")) {
                    sellerInfoArrayList.set(i, s.replace("Password:" + getPassword()
                            + "|", "Password:" + newPass + "|"));
                }
            }
            this.setPassword(newPass);
            rewriteSellerDataFile();
        }
    }

    public void deleteAccount(String email) {
        synchronized (sellerObj) {
            this.sellerInfoArrayList = getFullSellerFile();
            getStoresList();
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Email:" + email + "|")) {
                    sellerInfoArrayList.remove(i);
                    for (int j = 0; j < (5 * storesArrayList.size()); j++)
                        sellerInfoArrayList.remove(i);
                }
            }
            rewriteSellerDataFile();
        }
        synchronized (messageObj) {
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
                    if (s.contains("ID:" + this.getID())) {
                        s = s.replace("DeletedBySender:No", "DeletedBySender:Yes");
                        s = s.replace("DeletedByReceiver:No", "DeletedByReceiver:Yes");
                    }
                    bfw.write(s + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ArrayList<Integer> blockedP = blockedPeopleArrayList;
        for (int i : blockedP) {
            removeBlockedUser(i);
        }
        removeIfBlocked();
    }

    public void removeIfBlocked() {
        synchronized (cusObj) {
            File file = new File("customerData.txt");
            ArrayList<String> fullCustomer = getFullCustomerList();
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (String customer : fullCustomer) {
                    if (customer.contains("hasBlocked:" + this.getID() + "|")) {
                        customer = customer.replace("hasBlocked:" + this.getID() + "|", "hasBlocked:|");
                    } else if (customer.contains("," + this.getID())) {
                        customer = customer.replace("," + this.getID(), "");
                    } else if (customer.contains("hasBlocked:" + this.getID() + ",")) {
                        customer = customer.replace("hasBlocked:" + this.getID() + ",", "hasBlocked:");
                    }
                    if (customer.contains("," + this.getID())) {
                        customer = customer.replace("," + this.getID(), "");
                    } else if (customer.contains("seesAsInvisible:" + this.getID() + ",")) {
                        customer = customer.replace("seesAsInvisible:" + this.getID() + ",", "seesAsInvisible:");
                    } else if (customer.contains("seesAsInvisible:" + this.getID())) {
                        customer = customer.replace("seesAsInvisible:" + this.getID(), "seesAsInvisible:");
                    }
                    bfw.write(customer + "\n");
                }
                bfw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<String> getMessageHistory() {
        synchronized (messageObj) {
            ArrayList<String> allHistorySeller = new ArrayList<>();
            File file = new File("messageData.txt");
            String idSeller = String.valueOf(this.getID());
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    String sender = line.substring(line.indexOf("Time:") + 7, line.indexOf("|Deleted"));
                    String receiver = line.substring(line.indexOf("|ReceiverID:"), line.indexOf("|DeletedByReceiver"));
                    if (sender.contains("ID:" + idSeller) && line.contains("DeletedBySender:No")) {
                        allHistorySeller.add(line);
                    } else if (receiver.contains("ID:" + idSeller) && line.contains("DeletedByReceiver:No")) {
                        allHistorySeller.add(line);
                    }
                }
                bfr.close();
            } catch (IOException e) {
                System.out.println("There is no message before!");
            }
            return allHistorySeller;
        }
    }

    public ArrayList<String> getSpecificMessageHistory(int id) {
        ArrayList<String> allHistorySeller = getMessageHistory();
        ArrayList<String> messageHistory = new ArrayList<>();
        for (String s : allHistorySeller) {
            if ((s.contains("ID:" + id))) {
                messageHistory.add(s);
            }
        }
        return messageHistory;
    }

    public ArrayList<String> getFullCustomerList() {
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

    public ArrayList<Integer> getSeesAsInvisible() {
        return seesAsInvisibleArrayList;
    }

    public ArrayList<String> getCustomerList() {
        synchronized (cusObj) {
            ArrayList<Integer> blocked;
            blocked = this.blockedPeopleArrayList;
            ArrayList<Integer> invisiblePeople;
            invisiblePeople = this.seesAsInvisibleArrayList;
            ArrayList<String> customerList = new ArrayList<>();
            File file = new File("customerData.txt");
            try {
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    customerList.add(line);
                }
                for (Integer blockedID : invisiblePeople) {
                    for (int i = 0; i < customerList.size(); i++) {
                        if (customerList.get(i).contains("ID:" + blockedID)) {
                            customerList.remove(i);
                            break;
                        }
                    }
                }
                for (Integer blockedID : blocked) {
                    for (int i = 0; i < customerList.size(); i++) {
                        if (customerList.get(i).contains("ID:" + blockedID)) {
                            customerList.remove(i);
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("There are no Customers!");
            }
            return customerList;
        }
    }

    public ArrayList<String> getUsernamesFromCustomerArrayList(ArrayList<String> customerList) {
        ArrayList<String> customerNameList = new ArrayList<>();
        for (String s : customerList) {
            customerNameList.add(s.substring(0, s.indexOf(",")));
        }
        return customerNameList;
    }

    public ArrayList<String> getCustomerNameList() {
        ArrayList<String> customerList = getCustomerList();
        ArrayList<String> customerNameList = new ArrayList<>();
        String temp;
        for (String s : customerList) {
            temp = s.substring(s.indexOf("Username:") + 9, s.indexOf("|", s.indexOf("Username:") + 1));
            temp += ", ID - " + getCustomerIDFromString(s);
            customerNameList.add(temp);
        }
        return customerNameList;
    }

    public ArrayList<String> getFullCustomerNameList() {
        // Returns string elements in form --> Mary, ID - 2
        ArrayList<String> customerList = getFullCustomerList();
        ArrayList<String> customerNameList = new ArrayList<>();
        String temp;
        for (String s : customerList) {
            temp = s.substring(s.indexOf("Username:") + 9, s.indexOf("|", s.indexOf("Username:") + 1));
            temp += ", ID - " + getCustomerIDFromString(s);
            customerNameList.add(temp);
        }
        return customerNameList;
    }

    public ArrayList<String> getBlockedCustomerNameList() {
        ArrayList<String> customerList = getFullCustomerList();
        ArrayList<String> customerNameList = new ArrayList<>();
        String temp;
        for (String s : customerList) {
            for (int i : blockedPeopleArrayList) {
                if (s.contains("ID:" + i)) {
                    temp = s.substring(s.indexOf("Username:") + 9, s.indexOf("|", s.indexOf("Username:") + 1));
                    temp += ", ID - " + getCustomerIDFromString(s);
                    customerNameList.add(temp);
                }
            }
        }
        return customerNameList;
    }

    public String getCustomerNameFromID(int id) {
        ArrayList<String> customerList = getCustomerList();
        String customerUsername = "";
        for (String s : customerList) {
            if (s.contains("ID:" + id)) {
                customerUsername = s.substring(s.indexOf("Username:") + 9, s.indexOf("|Email:"));
                return customerUsername;
            }
        }
        return customerUsername;
    }

    public void sendMessageToCustomer(int id, String message) {
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
                //String lineNumb = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                bfw.write("Time:" + lineNumb + "|SenderID:" + getID() + ".0|DeletedBySender:No|ReceiverID:" + id
                        + "|DeletedByReceiver:No|Message:" + message + "\n");
                bfw.close();
                bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addBlockedUser(int customerID) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            String currentSellerString = "";
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                currentSellerString = sellerInfoArrayList.get(i);
                if (currentSellerString.contains("Username:" + this.getUsername() + "|")) {
                    if (currentSellerString.contains("hasBlocked:|")) {
                        // if there is no one in the seller's hasBlocked list
                        sellerInfoArrayList.set(i, currentSellerString.replace("hasBlocked:|", "hasBlocked:"
                                + customerID + "|"));
                    } else {
                        // Checks to make sure that we are not adding repeats
                        if (blockedPeopleArrayList.contains(customerID)) {
                            return;
                        }
                        // adds customerID to file
                        sellerInfoArrayList.set(i, currentSellerString.replace("|seesAsInvisible", ","
                                + customerID + "|seesAsInvisible"));
                    }
                }
            }
            blockedPeopleArrayList.add(customerID);
            rewriteSellerDataFile();
        }

        String s = "";
        synchronized (cusObj) {
            ArrayList<String> customers = getFullCustomerList();
            try {
                File file = new File("customerData.txt");
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < customers.size(); i++) {
                    s = customers.get(i);
                    if (s.contains("ID:" + customerID)) {
                        boolean alreadyOnList = false;
                        if (s.contains(":|hasBlocked:")) {
                            s = s.replace(":|hasBlocked:", ":" + this.getID() + "|hasBlocked:");
                        } else {
                            // ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:1
                            // |hasBlocked:|seesAsInvisible:
                            // check to see if the ID is already blocked
                            String[] splitCustomerString = s.split("wasBlockedBy:");
                            String[] splitBlocked = splitCustomerString[1].split("\\|");
                            String[] splitBlockedByList = splitBlocked[0].split(",");
                            for (int j = 0; j < splitBlockedByList.length; j++) {
                                int blockedID = Integer.parseInt(splitBlockedByList[j]);
                                if (blockedID == customerID) {
                                    alreadyOnList = true;
                                }
                            }
                            if (!alreadyOnList) {
                                s = s.replace("|hasBlocked:", "," + this.getID() + "|hasBlocked:");
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

    public ArrayList<String> getNotBlockedByArrayList() {
        ArrayList<String> notBlockedPeopleArrayList = getFullCustomerNameList(); // From form --> Mary, ID - 2
        // From form --> Mary, ID - 2 in notBlockedPeopleArrayList
        // Check to see if ID in that form matches ID from blockedPeopleArrayList
        // If matches, remove from notBlockedPeopleArrayList
        for (int i = 0; i < blockedPeopleArrayList.size(); i++) {
            for (int j = 0; j < notBlockedPeopleArrayList.size(); j++) {
                int currentNotBlockedID = getCustomerIDFromPrintString(notBlockedPeopleArrayList.get(j));
                if (currentNotBlockedID == (blockedPeopleArrayList.get(i))) {
                    // if the customer ID in the current string matches the blockedPeople ID
                    int indexOfBlockedPerson = j;
                    notBlockedPeopleArrayList.remove(indexOfBlockedPerson);
                }
            }
        }

        return notBlockedPeopleArrayList;
    }

    public void removeBlockedUser(int customerID) {
        String sub;
        Boolean user;
        String s1;
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Seller<")) {
                    // Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:4|seesAsInvisible:
                    // |number of stores:3>
                    s1 = sellerInfoArrayList.get(i);
                    sub = s1.substring(s1.indexOf("hasBlocked:"), s1.indexOf("|", s1.indexOf("hasBlocked:") + 1));
                    user = s1.contains("Username:" + this.getUsername() + "|");
                    if (user && sub.contains("," + customerID)) {
                        // case: you want 4, string looks like: hasBlocked:2,4,6|
                        sellerInfoArrayList.set(i, s1.replace("," + customerID, ""));
                    } else if (user && sub.contains(":" + customerID + ",")) {
                        // case: you want 4, string looks like: hasBlocked:4,6|
                        sellerInfoArrayList.set(i, s1.replace(":" + customerID + ",", ":"));
                    } else if (user && sub.contains(":" + customerID)) {
                        // case: you want 4, string looks like: hasBlocked:4|
                        sellerInfoArrayList.set(i, s1.replace(":" + customerID + "|", ":|"));
                    }
                }
            }
            int indexOfCustomerID = blockedPeopleArrayList.indexOf(customerID);
            if (indexOfCustomerID != -1) {
                // find index, plug index in
                blockedPeopleArrayList.remove(indexOfCustomerID);
            }
            rewriteSellerDataFile();
        }
        synchronized (cusObj) {
            ArrayList<String> customerArrayList = getFullCustomerList();
            try {
                String currentCustomerString = "";
                File file = new File("customerData.txt");
                BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < customerArrayList.size(); i++) {
                    Customer customer = new Customer();
                    currentCustomerString = customerArrayList.get(i); // looks like: ID:2|Username:Mary
                    // |Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:1,3|hasBlocked:|seesAsInvisible:
                    ArrayList<Integer> wasBlockedByArrayList
                            = customer.getBlockedByArrayListFromCustomerString(currentCustomerString);
                    for (int j = 0; j < customerArrayList.size(); j++) {
                        if (currentCustomerString.contains("ID:" + customerID)) {
                            // if the line is for the customer we've been looking for
                            if (currentCustomerString.contains(":" + this.getID() + "|hasBlocked:")) {
                                currentCustomerString = currentCustomerString.replace(":" + this.getID() + "|hasBlocked:"
                                        , ":|hasBlocked:");
                            } else if (currentCustomerString.contains("," + this.getID() + "|hasBlocked:")) {
                                currentCustomerString = currentCustomerString.replace("," + this.getID() + "|hasBlocked:"
                                        , "|hasBlocked:");
                            } else {
                                currentCustomerString = currentCustomerString.replace(this.getID() + ",", "");
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

    public void addSeesAsInvisibleToSeller(int customerID) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            String currentSellerString = "";
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                currentSellerString = sellerInfoArrayList.get(i);
                if (currentSellerString.contains("Username:" + this.getUsername() + "|")) {
                    if (currentSellerString.contains("seesAsInvisible:|")) {
                        // if there is no one in the customer's seeAsInvisible list
                        sellerInfoArrayList.set(i, currentSellerString.replace("|number of stores", customerID
                                + "|number of stores"));
                    } else {
                        // Checks to make sure that we are not adding repeats
                        // System.out.println("current seller string: " + currentSellerString);
                        ArrayList<Integer> seesAsInvisibleList
                                = getSeesAsInvisibleArrayListFromSellerString(currentSellerString);
                        if (seesAsInvisibleList.contains(customerID)) {
                            return;
                        }
                        // adds customerID to file
                        sellerInfoArrayList.set(i, currentSellerString.replace("|number of stores", "," + customerID
                                + "|number of stores"));
                    }
                }
            }
            seesAsInvisibleArrayList.add(customerID);
            rewriteSellerDataFile();
        }
    }


    public void removeInvisibleUser(int customerID) {
        synchronized (sellerObj) {
            // Given string
            String sub = "";
            Boolean containsUsername;
            String customerString = "";
            sellerInfoArrayList = getFullSellerFile();
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Seller<")) {
                    customerString = sellerInfoArrayList.get(i);
                    // Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:
                    // |seesAsInvisible:|number of stores:3>
                    String[] splitSellerString = customerString.split("\\|");
                    String seesAsInvisibleString = splitSellerString[6]; // looks like "seesAsInvisible:"
                    // or "seesAsInvisible:2" or "seesAsInvisible:2,4"
                    // sub = customerString.substring(customerString.indexOf("seesAsInvisible:")
                    // , customerString.indexOf("|", customerString.indexOf("seesAsInvisible:") + 1));

                    containsUsername = customerString.contains("Username:" + this.getUsername() + "|");
                    if (containsUsername && seesAsInvisibleString.contains("," + customerID)) {
                        // case: looking for 4 in "seesAsInvisible:2,4|" or "seesAsInvisible:2,4,6|"
                        seesAsInvisibleString = seesAsInvisibleString.replace("," + customerID, "");
                        splitSellerString[6] = seesAsInvisibleString;
                        customerString = String.join("|", splitSellerString);
                        sellerInfoArrayList.set(i, customerString);
                    } else if (containsUsername && customerString.contains("seesAsInvisible:" + customerID + "|")) {
                        // case: looking for 2 in "seesAsInvisible:2|"
                        sellerInfoArrayList.set(i, customerString.replace("seesAsInvisible:" + customerID
                                , "seesAsInvisible:"));
                    } else if (containsUsername && customerString.contains("seesAsInvisible:" + customerID + ",")) {
                        // case: looking for 2 in "seesAsInvisible:2,4|"
                        sellerInfoArrayList.set(i, customerString.replace("seesAsInvisible:" + customerID + ","
                                , "seesAsInvisible:"));
                    } else if (containsUsername) {
                        return;
                    }
                }
            }
            int indexOfCustomerID = seesAsInvisibleArrayList.indexOf(customerID);
            if (indexOfCustomerID != -1) {
                // find index, plug index in
                seesAsInvisibleArrayList.remove(indexOfCustomerID);
            }
            rewriteSellerDataFile();
        }
    }

    public ArrayList<String> getInvisibleToArrayListFromSellerString() {
        ArrayList<String> sellerArrayList = getFullCustomerList();
        ArrayList<String> seesAsInvisArrayList = new ArrayList<>();
        String currentInvisString;
        Customer customer = new Customer();
        for (String s : sellerArrayList) {
            for (int invisID : seesAsInvisibleArrayList) {
                if (s.contains("ID:" + invisID)) {
                    currentInvisString = s.substring(s.indexOf("Username:") + 9, s.indexOf("|"
                            , s.indexOf("Username:") + 1));
                    currentInvisString += ", ID - " + String.valueOf(getCustomerIDFromString(s));
                    seesAsInvisArrayList.add(currentInvisString);
                }
            }
        }
        return seesAsInvisArrayList;
    }

    public ArrayList<String> getInvisibleList() {
        ArrayList<String> list = getInvisibleToArrayListFromSellerString();
        ArrayList<String> finalList = new ArrayList<>();
        String invisibleCustomerInfo = "";
        String currentCustomerString = "";
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                currentCustomerString = list.get(i);
                if (!currentCustomerString.contains("ID - ")) {
                    // if NOT in form -> Mary, ID - 2
                    invisibleCustomerInfo = getCustomerNameFromID(getCustomerIDFromString(currentCustomerString));
                    invisibleCustomerInfo += ", ID - " + String.valueOf(getCustomerIDFromString(currentCustomerString));
                } else {
                    invisibleCustomerInfo = currentCustomerString;
                }
                finalList.add(invisibleCustomerInfo);
            }
        }
        return finalList;
    }

    public ArrayList<String> getDashboardList(int id) {
        getStoresList();
        for (Store storeString : storesArrayList)
            if (storeString.getId() == id)
                return storeString.sortList();
        ArrayList<String> dashboardArrayList = new ArrayList<String>();
        return dashboardArrayList;
    }

    public ArrayList<String> getDashboardUserList(int id) {
        getStoresList();
        for (Store storeString : storesArrayList)
            if (storeString.getId() == id) {
                return storeString.sortUserList();
            }
        ArrayList<String> dashboardUserArrayList = new ArrayList<String>();
        return dashboardUserArrayList;
    }

    public void getStoresList() {
        storesArrayList = new ArrayList<>();
        int numStores = 0;
        String s;
        int index = 0;
        sellerInfoArrayList = getFullSellerFile();
        for (int i = 0; i < sellerInfoArrayList.size(); i++) {
            s = sellerInfoArrayList.get(i);
            if (s.contains("Seller<") && s.contains("ID:" + this.getID())) {
                numStores = Integer.parseInt(s.substring(s.indexOf("number of stores:") + 17, s.indexOf('>')));
                index = i;
                break;
            }
        }

        String line;
        String name;
        int id;
        String[] usersString;
        String[] usersFreqString;
        ArrayList<Integer> users = new ArrayList<Integer>();
        ArrayList<Integer> usersFreq = new ArrayList<>();
        String[] words;
        ArrayList<String> wordsFin = new ArrayList<String>();
        String[] wordsFreqString;
        ArrayList<Integer> wordsFreq;
        Customer customer = new Customer();

        for (int i = index + 1; i < index + 1 + (5 * numStores); i += 5) {
            line = sellerInfoArrayList.get(i);
            name = line.substring(line.indexOf("Name:") + 5, line.indexOf(">"));
            id = customer.getSellerIDFromSellerString(line);
            //Integer.parseInt(line.substring(line.indexOf("ID:") + 3, line.indexOf("\\|")));
            wordsFin = new ArrayList<>();
            words = sellerInfoArrayList.get(i + 3).split(",");
            wordsFreqString = sellerInfoArrayList.get(i + 4).split(",");
            wordsFreq = new ArrayList<>();
            usersFreq = new ArrayList<>();
            users = new ArrayList<>();
            for (String wordFreq : wordsFreqString) {
                wordsFreq.add(Integer.parseInt(wordFreq));
            }
            usersString = sellerInfoArrayList.get(i + 1).split(",");
            usersFreqString = sellerInfoArrayList.get(i + 2).split(",");
            for (String user : usersString) {
                users.add(Integer.parseInt(user));
            }
            for (String userF : usersFreqString) {
                usersFreq.add(Integer.parseInt(userF));
            }
            for (String word : words) {
                wordsFin.add(word);
            }
            storesArrayList.add(new Store(id, name, wordsFin, wordsFreq, users, usersFreq));
        }
    }

    public int getCustomerIDFromString(String customerString) {
        // ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:|hasBlocked:|seesAsInvisible:
        ArrayList<Integer> hasBlockedArrayList = new ArrayList<>();
        String[] splitCustomerString = customerString.split("\\|");
        String[] blockedByStringList = splitCustomerString[0].split(":");
        String idAsString = blockedByStringList[1];
        int id = Integer.parseInt(idAsString);
        return id;
    }

    public int getCustomerIDFromPrintString(String s) {
        // Returns customerID from one of these things --> Mary, ID - 2
        int id = Integer.parseInt(s.substring(s.indexOf("ID - ") + 5));
        return id;
    }


    public String formatStores() {
        getStoresList();
        String formatted = "";
        for (Store s : storesArrayList) {
            formatted += s.formatStoreForDash() + "\n";
        }
        return formatted;
    }

    public String formatMessage(String s, String customerName) {
        String name = customerName;
        if (s.contains("SenderID:" + this.getID()))
            name = "You";
        return String.format("%s: %s", name, stringToMessage(s));
    }

    public String formatMessageForEdit(String s) {
        if (s.contains("SenderID:" + this.getID()))
            return String.format("%s: %s", "You", stringToMessage(s));
        else
            return "";
    }

    public void updateDash(int storeID, String message, int cusID) {
        getStoresList();
        ArrayList<String> storeListThing = new ArrayList<>();
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            for (Store sto : storesArrayList) {
                if (sto.getId() == storeID) {
                    sto.updateDash(message, cusID);
                    storeListThing = sto.formatStore();
                    break;
                }
            }
            int j = 0;
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Seller<ID:" + this.getID())) {
                    j = i;
                    break;
                }
            }
            for (int i = j; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).contains("Store<ID:" + storeID)) {
                    j = i;
                    break;
                }
            }
            for (int i = 0; i < 5; i++) {
                sellerInfoArrayList.set(j + i, storeListThing.get(i));
            }
            rewriteSellerDataFile();
        }
    }

    public boolean getStoreValid(int id) {
        getStoresList();
        for (Store s : storesArrayList)
            if (s.getId() == id)
                return (s.getId() == id);
        return false;
    }

    public boolean changeStoreName(String newName, int id) {
        synchronized (sellerObj) {
            sellerInfoArrayList = getFullSellerFile();
            String oldname;
            ArrayList<String> stores = getStores();
            String s3;
            getStoresList();
            for (Store s : storesArrayList) {
                if (s.getId() == id) {
                    for (String s1 : stores) {
                        s3 = s1.substring(s1.indexOf("Name:"), s1.indexOf(">"));
                        if (s3.equals("Name:" + newName)) {
                            return false;
                        }
                    }
                    oldname = s.getName();
                    s.setName(newName);
                    String s2;
                    for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                        s2 = sellerInfoArrayList.get(i);
                        if (s2.contains("Store<ID:" + id) && s2.contains("Name:" + oldname)) {
                            sellerInfoArrayList.set(i, s2.replace(oldname, newName));
                            break;
                        }
                    }
                    rewriteSellerDataFile();
                }
            }
            return true;
        }
    }

    public void deleteStore(int id) {
        synchronized (sellerObj) {
            getStoresList();
            String name = "";
            ArrayList<String> formatted = new ArrayList<>();
            for (int i = 0; i < storesArrayList.size(); i++) {
                if (storesArrayList.get(i).getId() == id) {
                    formatted = storesArrayList.get(i).formatStore();
                    storesArrayList.remove(i);
                    break;
                }
            }
            sellerInfoArrayList = getFullSellerFile();
            String s1;
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                s1 = sellerInfoArrayList.get(i);
                if (s1.contains("Seller<ID:" + this.getID())) {
                    sellerInfoArrayList.set(i, s1.replace("number of stores:"
                            + (storesArrayList.size() + 1), "number of stores:" + storesArrayList.size()));
                }
            }
            int deleteIndex;
            for (int i = 0; i < sellerInfoArrayList.size(); i++) {
                if (sellerInfoArrayList.get(i).equals(formatted.get(0))) {
                    deleteIndex = i;
                    for (int j = 0; j < 5; j++) {
                        sellerInfoArrayList.remove(deleteIndex);
                    }
                    break;
                }
            }

            rewriteSellerDataFile();
        }
    }

    public String getCustomerEmailFromCustomerString(String customerString) {
        // Start with: ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:
        // |hasBlocked:|seesAsInvisible:
        String[] splitCustomerString = customerString.split("\\|");
        String[] splitEmailString = splitCustomerString[2].split(":");
        String customerEmail = splitEmailString[1];
        return customerEmail;
    }

    public String getSellerEmailFromSellerString(String sellerString) {
        // Starting with:Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:4
        // |seesAsInvisible:|number of stores:3>
        String[] splitSellerString = sellerString.split("\\|");
        String[] splitEmailString = splitSellerString[2].split(":");
        String sellerEmail = splitEmailString[1];
        return sellerEmail;
    }

    public String getCustomerUsernameFromCustomerString(String customerString) {
        // Start with: ID:2|Username:Mary|Email:mary@yahoo.com|Password:maryIsAwesome|wasBlockedBy:
        // |hasBlocked:|seesAsInvisible:
        String[] splitCustomerString = customerString.split("\\|");
        String[] splitUsernameString = splitCustomerString[1].split(":");
        String customerUsername = splitUsernameString[1];
        return customerUsername;
    }

    public String getSellerUsernameFromSellerString(String sellerString) {
        // Starting with:Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:4
        // |seesAsInvisible:|number of stores:3>
        String[] splitSellerString = sellerString.split("\\|");
        String[] splitUsernameString = splitSellerString[1].split(":");
        String sellerUsername = splitUsernameString[1];
        return sellerUsername;
    }

    public int getSellerIDFromSellerString(String sellerString) {
        // Starting with:Seller<ID:1|Username:snig|Email:s@g|Password:sniggy|wasBlockedBy:|hasBlocked:4
        // |seesAsInvisible:|number of stores:3>
        String[] splitSellerString = sellerString.split("\\|");
        String[] splitIDString = splitSellerString[0].split(":");
        int sellerID = Integer.parseInt(splitIDString[1]);
        return sellerID;
    }

}
