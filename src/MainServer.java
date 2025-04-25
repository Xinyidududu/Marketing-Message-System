import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A MainServer Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */
public class MainServer implements Runnable {
    private Socket socket;

    public void run() {
        try {
            String username = "";
            String email = "";
            String password = "";
            boolean continueProgram = true;
            Customer customer = new Customer();
            Seller seller = new Seller();
            Seller tempSeller = new Seller();
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            boolean loggedIn = false;
            // PHASE 1: LOGIN/SIGN UP
            while (continueProgram) {
                // CONTAINS ENTIRE PROGRAM: LOOPS UNTIL EXIT
                while (true) {
                    while (!loggedIn) {
                        int welcomeChoiceInt = ois.readInt();
                        if (welcomeChoiceInt == -1)
                            return;
                        // LOGIN: requires an email and password (each email is unique)
                        if (welcomeChoiceInt == 0 || welcomeChoiceInt == 1) {
                            boolean validLogin = false;
                            int tryAgain = -1;
                            while (!validLogin && (tryAgain != 2)) {
                                // Loops while the user has neither given a valid login nor indicated a desire
                                // to go back
                                email = (String) ois.readObject();
                                if (email == null)
                                    return;
                                password = (String) ois.readObject();
                                if (password == null)
                                    return;
                                if (welcomeChoiceInt == 0) {
                                    // returns a seller object or null customer object
                                    seller = tempSeller.verifySellerLogin(email, password);
                                    if (seller != null && seller.getEmail() != "") {
                                        // the login is valid if the seller object was assigned a valid email
                                        validLogin = true;
                                    }
                                    oos.writeBoolean(validLogin);
                                    oos.flush();
                                } else if (welcomeChoiceInt == 1) {
                                    // should return a customer object or null customer object
                                    customer = customer.verifyCustomerLogin(email, password);
                                    //System.out.println(customer);
                                    if (customer != null && customer.getEmail() != "") {
                                        // the login is valid if the customer object was assigned a valid ID
                                        validLogin = true;
                                    }
                                    oos.writeBoolean(validLogin);
                                    oos.flush();
                                }
                                if (!validLogin) {
                                    continue;
                                }
                            }

                            // CREATE NEW ACCOUNT
                        } else if (welcomeChoiceInt == 2) {
                            int newAccountTypeInt = ois.readInt();
                            if (newAccountTypeInt == -1) {
                                return;
                            }
                            emailLoop:
                            while (true) {
                                email = (String) ois.readObject();
                                if (email == null)
                                    return;
                                if (email.isEmpty())
                                    continue;
                                if (!seller.verifyUniqueEmail(email)) {
                                    oos.writeBoolean(false);
                                    oos.flush();
                                    continue emailLoop;
                                }
                                oos.writeBoolean(true);
                                oos.flush();
                                break;
                            }
                            usernameLoop:
                            while (true) {
                                username = (String) ois.readObject();
                                if (username == null)
                                    return;
                                if (username.isEmpty())
                                    continue;
                                if (!seller.verifyUniqueUsername(username)) {
                                    oos.writeBoolean(false);
                                    oos.flush();
                                    continue usernameLoop;
                                }
                                oos.writeBoolean(true);
                                oos.flush();
                                break;
                            }

                            while (true) {
                                password = (String) ois.readObject();
                                if (password == null)
                                    return;
                                if (!password.isEmpty())
                                    break;
                            }
                            if (newAccountTypeInt == 0) {
                                // Create new customer with information gathered
                                customer.setEmail(email);
                                customer.setUsername(username);
                                customer.setPassword(password);
                                customer.setID(customer.generateNewID());
                                customer.createCustomerAccount(username, email, password);
                                oos.writeBoolean(true);
                                oos.flush();

                            } else if (newAccountTypeInt == 1) {
                                // Create new seller with information gathered
                                seller = new Seller(username, email, password);
                                oos.writeBoolean(true);
                                oos.flush();
                            }
                        } else if (welcomeChoiceInt == 3) {
                            return;
                        }

                        // PHASE 2: USING ACCOUNT
                        loggedIn = true;
                    }

                    // PHASE 2C: CUSTOMER EXPERIENCE

                    if (customer.getID() > 0) {
                        oos.writeObject(customer.getUsername());
                        oos.flush();
                        while (loggedIn) {
                            int customerMenuChoiceInt = ois.readInt();
                            if (customerMenuChoiceInt == 0) {
                                // 0. Send message to a seller
                                ArrayList<String> sellerArrayList = customer.getSellerList();
                                oos.writeObject(sellerArrayList);
                                oos.flush();
                                int choiceOfSellerID = ois.readInt();
                                if (choiceOfSellerID != -1) {
                                    String messageSendToSeller = (String) ois.readObject();
                                    if (messageSendToSeller == null) {
                                        break;
                                    }
                                    customer.sendMessageToSeller(choiceOfSellerID, messageSendToSeller);
                                    oos.writeBoolean(true);
                                    oos.flush();
                                } else {
                                    continue;
                                }

                            } else if (customerMenuChoiceInt == 1) {
                                // 1. Send message to a store
                                ArrayList<String> storeArrayList = customer.getStoreList();
                                oos.writeObject(storeArrayList);
                                oos.flush();
                                if (storeArrayList.size() != 0) {
                                    int choiceOfStoreIndex = ois.readInt();
                                    if (choiceOfStoreIndex == -1)
                                        break;
                                    int sellerID = -1;
                                    int storeID = -1;
                                    String selectedStore = "";
                                    selectedStore = storeArrayList.get(choiceOfStoreIndex);
                                    String selectedStoreName = selectedStore.substring(selectedStore.indexOf(":") + 2,
                                            selectedStore.indexOf("(") - 1);
                                    String sellerIDString = selectedStore.substring(selectedStore.indexOf(".") + 1,
                                            selectedStore.indexOf(".", selectedStore.indexOf(".") + 1));
                                    sellerID = Integer.parseInt(sellerIDString);
                                    storeID = Integer.parseInt(selectedStore.substring(selectedStore.indexOf(".", selectedStore.indexOf(".") + 1)
                                            + 1, selectedStore.indexOf(")")));
                                    String message = (String) ois.readObject();
                                    if (message == null) {
                                        break;
                                    }
                                    customer.sendMessageToStore(sellerID, storeID, message);
                                    oos.writeBoolean(true);
                                    oos.flush();
                                }

                            } else if (customerMenuChoiceInt == 2) {
                                // 2. View message history
                                try {
                                    int sellerID = -1;
                                    String userOptionChoice = "";
                                    String sellerName = "";
                                    String[] messageOptionsList = {"Edit Message", "Delete Message", "Return to menu"};
                                    ArrayList<String> sellerLineArrayList = customer.getSellerList(); // Returns list of --> Sniggy, ID:3
                                    oos.writeObject(sellerLineArrayList);
                                    oos.flush();
                                    if (sellerLineArrayList.size() == 0) {
                                        break;
                                    } else {
                                        // If there are unblocked/visible sellers
                                        String sellerChosenPrintString = (String) ois.readObject();
                                        if (sellerChosenPrintString == null) {
                                            break;
                                        }
                                        String[] splitChosenSellerPrintString = sellerChosenPrintString.split(":");
                                        sellerID = Integer.parseInt(splitChosenSellerPrintString[1]);
                                        ArrayList<String> messagesHistory = customer.getSpecificMessageHistory(sellerID);
                                        if (messagesHistory.isEmpty()) {
                                            oos.writeObject(messagesHistory);
                                            oos.flush();
                                            break;
                                        }
                                        ArrayList<String> messageArrayListToDisplay = new ArrayList<String>();
                                        for (String s : sellerLineArrayList)
                                            if (s.contains("ID:" + sellerID))
                                                sellerName = s.substring(0, s.indexOf(","));
                                        for (String s : messagesHistory) {
                                            messageArrayListToDisplay.add(customer.formatMessage(s, sellerName));
                                        }
                                        oos.writeObject(messageArrayListToDisplay);
                                        oos.flush();
                                        userOptionChoice = (String) ois.readObject();
                                        if (userOptionChoice == null)
                                            break;
                                        if (userOptionChoice.equals(messageOptionsList[0])) {
                                            // Option 0. Edit Message
                                            String messageToBeEdited = (String) ois.readObject();
                                            String editedMessageInput = (String) ois.readObject();
                                            if (messageToBeEdited.isEmpty() || editedMessageInput == null)
                                                break;
                                            customer.editMessage(messageToBeEdited, editedMessageInput);
                                            oos.writeBoolean(true);
                                            oos.flush();

                                        } else if (userOptionChoice.equals(messageOptionsList[1])) {
                                            // Option 1. Delete Message
                                            String messageToBeDeleted = (String) ois.readObject();
                                            int confirmDelete = ois.readInt();
                                            if (confirmDelete == 0) {
                                                customer.deleteMessage(messageToBeDeleted);
                                                oos.writeBoolean(true);
                                                oos.flush();
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            } else if (customerMenuChoiceInt == 3) {
                                // 3. View all store dashboards
                                String dashB = customer.displayDashboard(customer.getID());
                                oos.writeObject(dashB);
                                oos.flush();
                                Boolean sortOrNot = ois.readBoolean();
                                if (sortOrNot == true) {
                                    String sortedB = customer.sortDashboard();
                                    oos.writeObject(sortedB);
                                    oos.flush();
                                }

                            } else if (customerMenuChoiceInt == 4) {
                                // 4. Choose users to block
                                Boolean blockAgain;
                                do {
                                    String beforeBlockSellerList = String.valueOf(customer.getSellerList());
                                    beforeBlockSellerList = beforeBlockSellerList.substring(1,
                                            beforeBlockSellerList.length() - 1);
                                    oos.writeObject(beforeBlockSellerList);
                                    oos.flush();
                                    if (beforeBlockSellerList.equals("ul") || beforeBlockSellerList.isEmpty()) {
                                        break;
                                    }
                                    String blockInputID = (String) ois.readObject();
                                    if (blockInputID == null || !beforeBlockSellerList.contains(blockInputID)) {
                                        break;
                                    }
                                    int blockSellerIDs;
                                    try {
                                        blockSellerIDs = Integer.parseInt(blockInputID);
                                    } catch (NumberFormatException e) {
                                        break;
                                    }
                                    customer.addBlockedUser(blockSellerIDs);
                                    oos.writeBoolean(true);
                                    oos.flush();
                                    blockAgain = ois.readBoolean();
                                } while (blockAgain);
                            } else if (customerMenuChoiceInt == 5) {
                                // 5. Choose users see as invisible
                                boolean changeAgain;
                                Boolean invisibleMore;
                                do {
                                    String beforeInvisibleSellerList = String.valueOf(customer.getSellerList());
                                    beforeInvisibleSellerList = beforeInvisibleSellerList.substring(1,
                                            beforeInvisibleSellerList.length() - 1);
                                    oos.writeObject(beforeInvisibleSellerList);
                                    oos.flush();
                                    if (beforeInvisibleSellerList.equals("ul") || beforeInvisibleSellerList.isEmpty()) {
                                        break;
                                    }
                                    String invisibleInputID = (String) ois.readObject();
                                    if (invisibleInputID == null || !beforeInvisibleSellerList.contains(invisibleInputID)) {
                                        break;
                                    }
                                    int invisibleToIDs;
                                    try {
                                        invisibleToIDs = Integer.parseInt(invisibleInputID);
                                    } catch (NumberFormatException e) {
                                        break;
                                    }
                                    customer.addBlockedUser(invisibleToIDs);
                                    invisibleMore = ois.readBoolean();
                                } while (invisibleMore);
                            } else if (customerMenuChoiceInt == 6) {
                                // 6. Edit account
                                int response = -1;
                                do {
                                    int userOrPass = -1;
                                    userOrPass = ois.readInt();
                                    if (userOrPass == -1) {
                                        break;
                                    }
                                    if (userOrPass == 0) {
                                        String newUsername = (String) ois.readObject();
                                        if (newUsername == null) {
                                            break;
                                        }
                                        customer.editUserName(customer.getUsername(), newUsername);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                    } else if (userOrPass == 1) {
                                        String newPassword = (String) ois.readObject();
                                        if (newPassword == null) {
                                            break;
                                        }
                                        customer.editPassword(password, newPassword);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                    }
                                    response = ois.readInt();
                                    if (response == -1 || response == 1) {
                                        break;
                                    }
                                } while (true);

                            } else if (customerMenuChoiceInt == 7) {
                                // 7. Delete account
                                int response2 = 0;
                                do {
                                    response2 = ois.readInt();
                                    if (response2 == -1 || response2 == 1) {
                                        break;
                                    }
                                    // response2 = scan.nextLine();
                                    if (response2 == 0) {
                                        String responseTwo = (String) ois.readObject();
                                        if (responseTwo == null)
                                            break;
                                        // String responseTwo = scan.nextLine();
                                        customer.deleteAccount(responseTwo);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                        // pop up tellig user account deleted
                                        loggedIn = false;
                                        // change loggedIn boolean to false
                                        break;
                                    }
                                } while (true);
                            } else if (customerMenuChoiceInt == 8) {
                                // 8. Log out
                                loggedIn = false;
                            }
                        }
                    }

                    // PHASE 2S: SELLER EXPERIENCE

                    if (seller.getID() > 0) {
                        oos.writeObject(seller.getUsername());
                        oos.flush();
                        while (loggedIn) {
                            int sellerMenuChoiceInt = 0;
                            try {
                                sellerMenuChoiceInt = ois.readInt();
                            } catch (NumberFormatException e) {
                                return;
                            }
                            if (sellerMenuChoiceInt == 0) {
                                // 0. Send message to a customer
                                ArrayList<String> customerArrayList = seller.getCustomerNameList();
                                oos.writeObject(customerArrayList);
                                oos.flush();
                                ArrayList<String> customerNamesList = seller.getUsernamesFromCustomerArrayList(customerArrayList);
                                oos.writeObject(customerNamesList);
                                oos.flush();

                                if (customerNamesList.size() != 0) {
                                    // Selecting a user to message
                                    int customerToMessagePosition = ois.readInt();
                                    if (customerToMessagePosition == -1) {
                                        break;
                                    }
                                    int customerToMessageID = seller.getCustomerIDFromPrintString(customerArrayList.get(customerToMessagePosition));
                                    String messageToCustomer = (String) ois.readObject();
                                    if (messageToCustomer != null) {
                                        seller.sendMessageToCustomer(customerToMessageID, messageToCustomer);
                                    }
                                }

                            } else if (sellerMenuChoiceInt == 1) {
                                // 1. View message history
                                String repeatString = "";
                                int cusID = -1;
                                String cusName = "";
                                ArrayList<String> cusList = seller.getCustomerNameList();
                                oos.writeObject(cusList);
                                oos.flush();
                                ArrayList<String> cusNameList = seller.getUsernamesFromCustomerArrayList(cusList);
                                oos.writeObject(cusNameList);
                                oos.flush();
                                if (cusNameList.size() != 0) {
                                    // If there is a message history
                                    int customerToViewIndex = ois.readInt();
                                    if (customerToViewIndex == -1) {
                                        break;
                                    }
                                    cusID = seller.getCustomerIDFromPrintString(cusList.get(customerToViewIndex));
                                    ArrayList<String> messageHistoryArrayList = seller.getSpecificMessageHistory(cusID);
                                    oos.writeObject(messageHistoryArrayList);
                                    oos.flush();
                                    if (!messageHistoryArrayList.isEmpty()) {
                                        // If message history with a specific user already exists
                                        ArrayList<String> messageArrayListToDisplay = new ArrayList<String>();
                                        for (String s : cusList)
                                            if (s.contains("ID - " + cusID))
                                                cusName = s.substring(0, s.indexOf(","));
                                        for (String s : messageHistoryArrayList) {
                                            messageArrayListToDisplay.add(seller.formatMessage(s, cusName));
                                        }
                                        oos.writeObject(messageArrayListToDisplay);
                                        oos.flush();
                                        int optionChoicePosition = ois.readInt();
                                        if (optionChoicePosition == -1) {
                                            break;
                                        }
                                        String[] formattedHistoryList = new String[messageArrayListToDisplay.size()];
                                        // elements in format -> You: message (or) Sender: message
                                        for (int i = 0; i < messageArrayListToDisplay.size(); i++) {
                                            formattedHistoryList[i] = messageArrayListToDisplay.get(i);
                                        }
                                        if (optionChoicePosition == 0) {
                                            // Option 0. Edit Message
                                            int messageToEditIndex = ois.readInt();
                                            if (messageToEditIndex == -1)
                                                break;
                                            String[] messageToBeEditedList = formattedHistoryList[messageToEditIndex].split(": ");
                                            String messageToBeEdited = messageToBeEditedList[1];
                                            String editedMessageInput = (String) ois.readObject();
                                            if (editedMessageInput == null)
                                                break;
                                            seller.editMessage(messageToBeEdited, editedMessageInput);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        } else if (optionChoicePosition == 1) {
                                            // Option 1. Delete Message
                                            int messageToDeleteIndex = ois.readInt();
                                            if (messageToDeleteIndex == -1)
                                                break;
                                            String[] messageToBeDeletedList = formattedHistoryList[messageToDeleteIndex].split(": ");
                                            String messageToBeDeleted = messageToBeDeletedList[1];
                                            seller.deleteMessage(messageToBeDeleted);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        }
                                    }
                                }


                            } else if (sellerMenuChoiceInt == 2) {
                                // 2. View my stores dashboards
                                int storeID = -1;
                                try {
                                    String formattedStoreString = seller.formatStores();
                                    oos.writeObject(formattedStoreString);
                                    oos.flush();
                                    if (formattedStoreString != null || !formattedStoreString.isEmpty() || !formattedStoreString.equals(" ")) {
                                        // If the seller DOES have stores
                                        String[] formattedStoreList = formattedStoreString.split("\n");
                                        int storeToViewIndex = ois.readInt();
                                        if (storeToViewIndex != -1) {
                                            String[] storeIDList = formattedStoreList[storeToViewIndex].split(" - ");
                                            storeID = Integer.parseInt(storeIDList[1]);
                                            ArrayList<String> dashboardWordsArrayList = new ArrayList<String>();
                                            ArrayList<String> dashboardUsersArrayList = new ArrayList<String>();

                                            int dashboardChoiceIndex = ois.readInt();
                                            if (dashboardChoiceIndex == 0) {
                                                // 0. User
                                                dashboardUsersArrayList = seller.getDashboardUserList(storeID);
                                                oos.writeObject(dashboardUsersArrayList);
                                                oos.flush();
                                            } else if (dashboardChoiceIndex == 1) {
                                                // 1. Word
                                                dashboardWordsArrayList = seller.getDashboardList(storeID);
                                                oos.writeObject(dashboardWordsArrayList);
                                                oos.flush();
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                            } else if (sellerMenuChoiceInt == 3) {
                                // 3. Manage stores
                                boolean reply;
                                String response;
                                int storeID;
                                String resp;
                                boolean bool;
                                try {
                                    storeEdit:
                                    do {
                                        String[] storeManageList = {"Edit Store", "Create New Store"};
                                        String storeResp = (String) ois.readObject();
                                        if (storeResp == null)
                                            break;
                                        if (storeResp.equals(storeManageList[0])) {
                                            // "Edit Store"
                                            String formattedStoreString = seller.formatStores();
                                            oos.writeObject(formattedStoreString);
                                            oos.flush();
                                            if (formattedStoreString != null || !formattedStoreString.isEmpty() || !formattedStoreString.equals(" ")) {
                                                // If the seller DOES have stores
                                                storeID = ois.readInt();
                                                if (storeID == -1) {
                                                    break;
                                                }
                                                String[] editStoreList = {"Edit Name", "Delete Store"};
                                                resp = (String) ois.readObject();
                                                if (resp == null)
                                                    break;
                                                if (resp.equals(editStoreList[0])) {
                                                    String newName = (String) ois.readObject();
                                                    bool = seller.changeStoreName(newName, storeID);
                                                    oos.writeBoolean(bool);
                                                    oos.flush();
                                                } else if (resp.equals(editStoreList[1])) {
                                                    Boolean confirmation = ois.readBoolean();
                                                    if (confirmation) {
                                                        seller.deleteStore(storeID);
                                                    }
                                                } else {
                                                    break;
                                                }
                                            }
                                        } else if (storeResp.equals(storeManageList[1])) {
                                            // "Create New Store"
                                            ArrayList<String> stores = seller.getStores();
                                            oos.writeObject(stores);
                                            oos.flush();
                                            String newStoreName = "";
                                            String s1;
                                            loop:
                                            while (true) {
                                                newStoreName = (String) ois.readObject();
                                                for (String s : stores) {
                                                    s1 = s.substring(s.indexOf("Name:"), s.indexOf(">"));
                                                    if (s1.equals("Name:" + newStoreName)) {
                                                        oos.writeBoolean(false);
                                                        oos.flush();
                                                        boolean cont = ois.readBoolean();
                                                        if (cont)
                                                            continue loop;
                                                        else
                                                            break storeEdit;
                                                    }
                                                }
                                                break;
                                            }
                                            seller.createNewStore(newStoreName);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        } else {
                                            break;
                                        }
                                        reply = ois.readBoolean();
                                    } while (reply);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            } else if (sellerMenuChoiceInt == 4) {
                                // 4. Choose users to block
                                int blocker;
                                do {
                                    blocker = ois.readInt();
                                    if (blocker == -1)
                                        break;
                                    if (blocker == 0) {
                                        // Adding customers to wasBlockedBy in customerData and hasBlocked in sellerData
                                        ArrayList<String> notBlockedCustomerNameArrayList = seller.getNotBlockedByArrayList();
                                        oos.writeObject(notBlockedCustomerNameArrayList);
                                        oos.flush();
                                        if (!notBlockedCustomerNameArrayList.isEmpty()) {
                                            int idToBlock = ois.readInt();
                                            if (idToBlock == -1)
                                                break;
                                            seller.addBlockedUser(idToBlock);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        } else {
                                            break;
                                        }
                                    } else if (blocker == 1) {
                                        // Removing customers from wasBlockedBy in customerData and hasBlocked in sellerData
                                        ArrayList<String> blockedCustomerNameArrayList = seller.getBlockedCustomerNameList();
                                        oos.writeObject(blockedCustomerNameArrayList);
                                        oos.flush();
                                        if (!blockedCustomerNameArrayList.isEmpty()) {
                                            int idToUnblock = ois.readInt();
                                            if (idToUnblock == -1)
                                                break;
                                            seller.removeBlockedUser(idToUnblock);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                    int blockMore;
                                    blockMore = ois.readInt();
                                    if (blockMore == -1 || blockMore == 1)
                                        break;
                                } while (true);
                            } else if (sellerMenuChoiceInt == 5) {
                                // 5. Choose users to become invisible to
                                int addOrRemove;
                                do {
                                    addOrRemove = ois.readInt();
                                    if (addOrRemove == -1)
                                        break;
                                    if (addOrRemove == 0) {
                                        // Set customer as invisible
                                        ArrayList<String> seeAsInvisibleArrayList = seller.getInvisibleList();
                                        ArrayList<String> seeAsVisibleArrayList = seller.getFullCustomerNameList();
                                        for (int i = 0; i < seeAsInvisibleArrayList.size(); i++) {
                                            if (seeAsVisibleArrayList.contains(seeAsInvisibleArrayList.get(i))) {
                                                seeAsVisibleArrayList.remove(seeAsInvisibleArrayList.get(i));
                                            }
                                        }
                                        oos.writeObject(seeAsVisibleArrayList);
                                        oos.flush();
                                        String[] seeAsVisibleList = new String[seeAsVisibleArrayList.size()];
                                        for (int i = 0; i < seeAsVisibleArrayList.size(); i++) {
                                            seeAsVisibleList[i] = seeAsVisibleArrayList.get(i);
                                        }
                                        if (!seeAsVisibleArrayList.isEmpty()) {
                                            String userToInvisible = (String) ois.readObject();
                                            if (userToInvisible == null)
                                                break;
                                            String[] idToInvisibleList = userToInvisible.split(" - ");
                                            int idToInvisible = Integer.parseInt(idToInvisibleList[1]);
                                            seller.addSeesAsInvisibleToSeller(idToInvisible);
                                            oos.writeBoolean(true);
                                            oos.flush();
                                        } else {
                                            break;
                                        }
                                    } else if (addOrRemove == 1) {
                                        // Reverse setting customer as invisible
                                        ArrayList<String> seeAsInvisibleArrayList = seller.getInvisibleList();
                                        oos.writeObject(seeAsInvisibleArrayList);
                                        oos.flush();
                                        if (seeAsInvisibleArrayList.isEmpty()) {
                                            break;
                                        }
                                        String userToUninvisible = (String) ois.readObject();
                                        if (userToUninvisible == null)
                                            break;
                                        String[] idToUninvisibleList = userToUninvisible.split(" - ");
                                        int idToUninvisible = Integer.parseInt(idToUninvisibleList[1]);
                                        seller.removeInvisibleUser(idToUninvisible);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                    } else {
                                        break;
                                    }
                                    int invisMore;
                                    invisMore = ois.readInt();
                                    if (invisMore == -1 || invisMore == 1)
                                        break;
                                } while (true);

                            } else if (sellerMenuChoiceInt == 6) {
                                // 6. Edit account
                                String response = "";
                                do {
                                    String[] sellerEditList = {"Username", "Password", "Email"};
                                    String editTypeChoice = (String) ois.readObject();
                                    Boolean success;
                                    if (editTypeChoice.equals(sellerEditList[0])) {
                                        // Username
                                        String newUsername = (String) ois.readObject();
                                        if (newUsername == null || newUsername.isEmpty())
                                            break;
                                        success = seller.editSellerUsername(newUsername);
                                        oos.writeBoolean(success);
                                        oos.flush();
                                    } else if (editTypeChoice.equals(sellerEditList[1])) {
                                        // Password
                                        String newPassword = (String) ois.readObject();
                                        if (newPassword == null || newPassword.isEmpty())
                                            break;
                                        seller.editPassword(newPassword);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                    } else if (editTypeChoice.equals(sellerEditList[2])) {
                                        // Email
                                        String newEmail = (String) ois.readObject();
                                        if (newEmail == null || newEmail.isEmpty())
                                            break;
                                        success = seller.editEmail(newEmail);
                                        oos.writeBoolean(success);
                                        oos.flush();
                                    } else {
                                        break;
                                    }
                                    int editMore = -1;
                                    editMore = ois.readInt();
                                    if (editMore == -1 || editMore == 1)
                                        break;
                                } while (true);

                            } else if (sellerMenuChoiceInt == 7) {
                                // 7. Delete Account
                                int response = -1;
                                response = ois.readInt();
                                if (response == 0) {
                                    String emailConfirm = (String) ois.readObject();
                                    if (emailConfirm == null)
                                        break;
                                    if (emailConfirm.equals(seller.getEmail())) {
                                        seller.deleteAccount(emailConfirm);
                                        oos.writeBoolean(true);
                                        oos.flush();
                                        loggedIn = false;
                                    } else {
                                        oos.writeBoolean(false);
                                        oos.flush();
                                    }
                                }

                            } else if (sellerMenuChoiceInt == 8) {
                                // 9. Log out
                                loggedIn = false;
                            }
                        }
                    }
                }
            }
            oos.close();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MainServer(Socket socket) {
        this.socket = socket;
    }
}
