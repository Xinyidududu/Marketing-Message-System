import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A MainClient Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */
public class MainClient {
    public static void main(String[] args) {
        try {
            String username = "";
            String email = "";
            String password = "";
            boolean continueProgram = true;
            Socket socket;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                socket = new Socket("localhost", 4242);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "ERROR: Server Offline, Please Try Again Later!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                continueProgram = false;
            }
            int sellOrCus = 0;
            boolean loggedIn = false;
            // PHASE 1: LOGIN/SIGN UP
            while (continueProgram) {
                // CONTAINS ENTIRE PROGRAM: LOOPS UNTIL EXIT
                while (true) {
                    while (!loggedIn) {
                        sellOrCus = 0;
                        username = "";
                        email = "";
                        password = "";
                        // Main menu for sign up/login
                        String[] welcomeOptions = {"0. Returning seller? Log in", "1. Returning customer? Log in",
                                "2. New user? Sign up", "3. Quit"};
                        String welcomeChoice = (String) JOptionPane.showInputDialog(null,
                                "Welcome to Marketplace Messenger!", "Sign Up/Login Menu",
                                JOptionPane.PLAIN_MESSAGE, null, welcomeOptions, null);
                        if (welcomeChoice == null)
                            return;
                        // TODO: Server: Send user choice to server
                        int welcomeChoiceInt = -1;
                        for (int i = 0; i < welcomeOptions.length; i++) {
                            if (welcomeChoice.equals(welcomeOptions[i]))
                                welcomeChoiceInt = i;
                        }
                        oos.writeInt(welcomeChoiceInt);
                        oos.flush();
                        if (welcomeChoiceInt == -1) {
                            JOptionPane.showMessageDialog(null,
                                    "Please select an option", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        // LOGIN: requires an email and password (each email is unique)
                        if (welcomeChoiceInt == 0 || welcomeChoiceInt == 1) {
                            boolean validLogin = false;
                            int tryAgain = -1;
                            while (!validLogin && (tryAgain != 2)) {
                                // Loops while the user has neither given a valid login nor indicated a desire
                                // to go back
                                email = JOptionPane.showInputDialog(null,
                                        "Enter your Email", "Login",
                                        JOptionPane.QUESTION_MESSAGE);
                                oos.writeObject(email);
                                oos.flush();
                                if (email == null)
                                    return;
                                password = JOptionPane.showInputDialog(null,
                                        "Enter your Password", "Login",
                                        JOptionPane.QUESTION_MESSAGE);
                                oos.writeObject(password);
                                oos.flush();
                                if (password == null)
                                    return;
                                if (welcomeChoiceInt == 0) {
                                    // returns a seller object or null customer object
                                    validLogin = ois.readBoolean();
                                    if (validLogin)
                                        sellOrCus = 2;
                                } else if (welcomeChoiceInt == 1) {
                                    validLogin = ois.readBoolean();
                                    if (validLogin)
                                        sellOrCus = 1;
                                }
                                if (!validLogin) {
                                    JOptionPane.showMessageDialog(null,
                                            "Your login information is invalid!", "Login Error",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    continue;
                                }
                            }

                            // CREATE NEW ACCOUNT
                        } else if (welcomeChoiceInt == 2) {
                            int newAccountType = -1;
                            String[] newAccountTypeList = {"0. Create Customer Account", "1. Create Seller Account"};
                            String newAccountTypeString = (String) JOptionPane.showInputDialog(null,
                                    "Would you like to make a customer or seller account?",
                                    "New Account Menu", JOptionPane.PLAIN_MESSAGE, null, newAccountTypeList,
                                    null);
                            if (newAccountTypeString == null)
                                return;
                            int newAccountTypeInt = -1;
                            for (int i = 0; i < newAccountTypeList.length; i++) {
                                if (newAccountTypeString.equals(newAccountTypeList[i]))
                                    newAccountTypeInt = i;
                            }
                            oos.writeInt(newAccountTypeInt);
                            oos.flush();
                            if (newAccountTypeInt == -1) {
                                JOptionPane.showMessageDialog(null, "Please select an option",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            emailLoop:
                            while (true) {
                                email = JOptionPane.showInputDialog(null,
                                        "What email should be associated with your account?",
                                        "Create New Account",
                                        JOptionPane.QUESTION_MESSAGE);
                                oos.writeObject(email);
                                oos.flush();
                                if (email == null)
                                    return;
                                if (email.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "ERROR: No email entered!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    continue;
                                }
                                Boolean emailLooper = ois.readBoolean();
                                if (!emailLooper) {
                                    JOptionPane.showMessageDialog(null,
                                            "ERROR: An account already exists with that email!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    continue;
                                }
                                break;
                            }
                            usernameLoop:
                            while (true) {
                                username = JOptionPane.showInputDialog(null,
                                        "Please choose your username.",
                                        "Create New Account", JOptionPane.QUESTION_MESSAGE);
                                oos.writeObject(username);
                                oos.flush();
                                if (username == null)
                                    return;
                                if (username.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "ERROR: No username entered!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    continue;
                                }
                                Boolean usernameLooper = ois.readBoolean();
                                if (!usernameLooper) {
                                    JOptionPane.showMessageDialog(null,
                                            "ERROR: An account already exists with that username!",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    continue usernameLoop;
                                }
                                break;
                            }

                            while (true) {
                                password = JOptionPane.showInputDialog(null,
                                        "Please choose your password.",
                                        "Create New Account", JOptionPane.QUESTION_MESSAGE);
                                oos.writeObject(password);
                                oos.flush();
                                if (password == null)
                                    return;
                                if (!password.isEmpty())
                                    break;
                                JOptionPane.showMessageDialog(null,
                                        "ERROR: No password entered!",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            if (newAccountTypeInt == 0) {
                                // Create new customer with information gathered
                                Boolean cusNewAccConfirmation = ois.readBoolean();
                                if (cusNewAccConfirmation) {
                                    JOptionPane.showMessageDialog(null,
                                            "Customer account created!",
                                            "Create New Account", JOptionPane.PLAIN_MESSAGE);
                                    sellOrCus = 1;
                                }

                            } else if (newAccountTypeInt == 1) {
                                // Create new seller with information gathered
                                //seller = new Seller(username, email, password);
                                Boolean sellNewAccConfirmation = ois.readBoolean();
                                if (sellNewAccConfirmation) {
                                    JOptionPane.showMessageDialog(null,
                                            "Seller account created!",
                                            "Create New Account", JOptionPane.PLAIN_MESSAGE);
                                    sellOrCus = 2;
                                }
                            }
                        } else if (welcomeChoiceInt == 3) {
                            JOptionPane.showMessageDialog(null, "See you next time!",
                                    "Marketplace Messenger", JOptionPane.PLAIN_MESSAGE);
                            return;

                        }

                        // PHASE 2: USING ACCOUNT
                        loggedIn = true;
                    }

                    // PHASE 2C: CUSTOMER EXPERIENCE
                    if (sellOrCus == 1) {
                        String cusUserName = (String) ois.readObject();
                        String welcomeString = String.format("Welcome, %s!\n", cusUserName);
                        while (loggedIn) {
                            int customerMenuChoiceInt = -1;
                            String[] customerMenuList = {"0. Send message to a seller",
                                    "1. Send message to a store", "2. View message history",
                                    "3. View all store dashboards ",
                                    "4. Choose users to block",
                                    "5. Choose users to become invisible to", "6. Edit account ",
                                    "7. Delete account", "8. Log out"};
                            String customerMenuChoiceString = (String) JOptionPane.showInputDialog(null,
                                    welcomeString + "\nCustomer Marketplace Messenger Menu",
                                    "Marketplace Messenger for Customers",
                                    JOptionPane.PLAIN_MESSAGE, null, customerMenuList, null);
                            if (customerMenuChoiceString == null)
                                return;
                            for (int i = 0; i < customerMenuList.length; i++) {
                                if (customerMenuChoiceString.equals(customerMenuList[i]))
                                    customerMenuChoiceInt = i;
                            }
                            oos.writeInt(customerMenuChoiceInt);
                            oos.flush();
                            if (customerMenuChoiceInt == -1) {
                                JOptionPane.showMessageDialog(null,
                                        "Please select an option", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            if (customerMenuChoiceInt == 0) {
                                // 0. Send message to a seller
                                ArrayList<String> sellerArrayList = (ArrayList<String>) ois.readObject();
                                ArrayList<Integer> sellerIDArrayList = new ArrayList<Integer>();
                                for (int i = 0; i < sellerArrayList.size(); i++) {
                                    String[] splitString = sellerArrayList.get(i).split(":");
                                    sellerIDArrayList.add(Integer.parseInt(splitString[1]));
                                }
                                String[] sellerNamesToDisplay = new String[sellerArrayList.size()];
                                for (int i = 0; i < sellerIDArrayList.size(); i++) {
                                    sellerNamesToDisplay[i] = sellerArrayList.get(i);
                                }
                                int choiceOfSellerID = -1;
                                String sellerDropdown = (String) JOptionPane.showInputDialog(null
                                        , "Select a seller to message!", "0. Send message to a seller"
                                        , JOptionPane.PLAIN_MESSAGE, null,
                                        sellerNamesToDisplay, null);
                                if (sellerDropdown != null) {
                                    for (int i = 0; i < sellerNamesToDisplay.length; i++) {
                                        if (sellerDropdown.equals(sellerNamesToDisplay[i])) {
                                            choiceOfSellerID = sellerIDArrayList.get(i);
                                            break;
                                        }
                                    }
                                    oos.writeInt(choiceOfSellerID);
                                    oos.flush();
                                    if (choiceOfSellerID == -1) {
                                        JOptionPane.showMessageDialog(null,
                                                "Error! No Seller Chosen!",
                                                "0. Send message to a seller", JOptionPane.ERROR_MESSAGE);
                                        continue;
                                    }
                                    String messageSendToSeller = JOptionPane.showInputDialog(null
                                            , "Enter the message here: ",
                                            "0. Send message to a seller"
                                            , JOptionPane.INFORMATION_MESSAGE);
                                    oos.writeObject(messageSendToSeller);
                                    oos.flush();
                                    if (messageSendToSeller == null) {
                                        break;
                                    }
                                    Boolean sentMessageSuccess = ois.readBoolean();
                                    if (sentMessageSuccess)
                                        JOptionPane.showMessageDialog(null, "Message sent!",
                                                "0. Send message to a seller", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    oos.writeInt(-1);
                                    oos.flush();
                                    JOptionPane.showMessageDialog(null, "There is no seller to send!",
                                            "0. Send message to a seller", JOptionPane.ERROR_MESSAGE);
                                }

                            } else if (customerMenuChoiceInt == 1) {
                                // 1. Send message to a store
                                int choiceOfStoreIndex = -1;
                                ArrayList<String> storeArrayList = (ArrayList<String>) ois.readObject();
                                String[] storeDropdown = new String[storeArrayList.size()];
                                if (storeArrayList.size() != 0) {
                                    for (int i = 0; i < storeArrayList.size(); i++) {
                                        storeDropdown[i] = (i + 1) + ". " + storeArrayList.get(i);
                                    }
                                    String chooseStoreDropdown = (String) JOptionPane.showInputDialog(
                                            null
                                            , "Select a store to message!", "1. Send message to a store"
                                            , JOptionPane.PLAIN_MESSAGE, null, storeDropdown,
                                            null);
                                    if (chooseStoreDropdown == null) {
                                        oos.writeInt(-1);
                                        oos.flush();
                                        break;
                                    }
                                    for (int i = 0; i < storeDropdown.length; i++) {
                                        if (chooseStoreDropdown.contains(storeDropdown[i])) {
                                            choiceOfStoreIndex = i;
                                            break;
                                        }
                                    }
                                    oos.writeInt(choiceOfStoreIndex);
                                    oos.flush();
                                    String message = JOptionPane.showInputDialog(null,
                                            "What is your message?"
                                            , "1. Send message to a store", JOptionPane.INFORMATION_MESSAGE);
                                    oos.writeObject(message);
                                    oos.flush();
                                    if (message == null) {
                                        break;
                                    }
                                    Boolean storeSentConfirm = ois.readBoolean();
                                    if (storeSentConfirm)
                                        JOptionPane.showMessageDialog(null, "Message sent!",
                                                "1. Send message to a store", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "There is no store to send!",
                                            "1. Send message to a store", JOptionPane.ERROR_MESSAGE);
                                }

                            } else if (customerMenuChoiceInt == 2) {
                                // 2. View message history
                                try {
                                    int sellerID = -1;
                                    String sellerName = "";
                                    String userOptionChoice = "";
                                    String[] messageOptionsList = {"Edit Message", "Delete Message", "Return to menu"};
                                    ArrayList<String> sellerLineArrayList = (ArrayList<String>) ois.readObject(); // Returns list of --> Sniggy, ID:3
                                    String[] sellerLineStringList = new String[sellerLineArrayList.size()];
                                    if (sellerLineArrayList.size() == 0) {
                                        JOptionPane.showMessageDialog(null,
                                                "There are no messages for you to view!",
                                                "2. View message history",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    } else {
                                        // If there are unblocked/visible sellers
                                        for (int i = 0; i < sellerLineArrayList.size(); i++) {
                                            sellerLineStringList[i] = sellerLineArrayList.get(i);
                                        }
                                        String sellerChosenPrintString = (String) JOptionPane.showInputDialog(
                                                null
                                                , "Choose a seller to view history",
                                                "2. View history messages"
                                                , JOptionPane.PLAIN_MESSAGE, null, sellerLineStringList,
                                                null);
                                        oos.writeObject(sellerChosenPrintString);
                                        oos.flush();
                                        if (sellerChosenPrintString == null) {
                                            break;
                                        }
                                        ArrayList<String> messageArrayListToDisplay =
                                                (ArrayList<String>) ois.readObject();
                                        if (messageArrayListToDisplay.isEmpty()) {
                                            JOptionPane.showMessageDialog(null,
                                                    "No message history! Returning to main manu...",
                                                    "2. View message history", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        String[] messageListToDisplay = new String[messageArrayListToDisplay.size()];
                                        String messageHistoryString = "";
                                        for (int i = 0; i < messageArrayListToDisplay.size(); i++) {
                                            messageListToDisplay[i] = messageArrayListToDisplay.get(i);
                                            messageHistoryString += "\n" + messageListToDisplay[i];
                                        }
                                        // String[] messageOptionsList = {"Edit Message", "Delete Message", "Return to menu"};
                                        userOptionChoice = (String) JOptionPane.showInputDialog(null,
                                                "Message History\n" + messageHistoryString + "\n\nOptions:",
                                                "2. View message history", JOptionPane.PLAIN_MESSAGE,
                                                null, messageOptionsList, null);
                                        oos.writeObject(userOptionChoice);
                                        oos.flush();
                                        if (userOptionChoice == null)
                                            break;
                                        ArrayList<String> formattedHistoryArrayList = new ArrayList<>();
                                        for (String message : messageArrayListToDisplay) {
                                            if (message.substring(0, 4).contains("You:"))
                                                formattedHistoryArrayList.add(message);
                                        }
                                        String[] formattedHistoryList = new String[formattedHistoryArrayList.size()];
                                        // elements in format -> You: message (or) Sender: message
                                        for (int i = 0; i < formattedHistoryArrayList.size(); i++) {
                                            formattedHistoryList[i] = formattedHistoryArrayList.get(i);
                                        }
                                        if (userOptionChoice.equals(messageOptionsList[0])) {
                                            // Option 0. Edit Message
                                            int messageIndex = -1;
                                            String messageToEditDisplay = (String) JOptionPane.showInputDialog(
                                                    null
                                                    , "Select a message to edit:", "2. View message history"
                                                    , JOptionPane.PLAIN_MESSAGE, null, formattedHistoryList,
                                                    null);

                                            String messageToBeEdited = "";
                                            for (int i = 0; i < formattedHistoryList.length; i++) {
                                                if (messageToEditDisplay.equals(formattedHistoryList[i])) {
                                                    int indexOfColon = messageToEditDisplay.indexOf(": ") + 2;
                                                    messageToBeEdited = messageToEditDisplay.substring(indexOfColon);
                                                    break;
                                                }
                                            }
                                            oos.writeObject(messageToBeEdited);
                                            oos.flush();
                                            if (messageToBeEdited == null) {
                                                oos.writeObject(null);
                                                oos.flush();
                                                break;
                                            }
                                            String editedMessageInput = JOptionPane.showInputDialog(null,
                                                    "What would you like the edited message to be?",
                                                    "2. View message history"
                                                    , JOptionPane.PLAIN_MESSAGE);
                                            oos.writeObject(editedMessageInput);
                                            oos.flush();
                                            if (messageToBeEdited.isEmpty() || editedMessageInput == null)
                                                break;
                                            Boolean editSuccess = ois.readBoolean();
                                            if (editSuccess)
                                                JOptionPane.showMessageDialog(null,
                                                        "Message edited!",
                                                        "2. View message history", JOptionPane.INFORMATION_MESSAGE);

                                        } else if (userOptionChoice.equals(messageOptionsList[1])) {
                                            // Option 1. Delete Message
                                            int messageIndex = -1;
                                            String messageToEditDisplay = (String) JOptionPane.showInputDialog(
                                                    null
                                                    , "Select a message to delete:",
                                                    "2. View message history"
                                                    , JOptionPane.PLAIN_MESSAGE, null,
                                                    messageListToDisplay, null);
                                            String messageToBeDeleted = "";
                                            for (int i = 0; i < messageListToDisplay.length; i++) {
                                                if (messageToEditDisplay.equals(messageListToDisplay[i])) {
                                                    int indexOfColon = messageToEditDisplay.indexOf(": ") + 2;
                                                    messageToBeDeleted = messageToEditDisplay.substring(indexOfColon);
                                                    break;
                                                }
                                            }
                                            oos.writeObject(messageToBeDeleted);
                                            oos.flush();
                                            int confirmDelete = -1;
                                            confirmDelete = JOptionPane.showConfirmDialog(null,
                                                    "Deleting message [" + messageToBeDeleted + "]\n" +
                                                            "Note: This only deletes a message for you," +
                                                            " the receiver may still be able to view it\n" +
                                                            "Are you sure you want to delete this message?",
                                                    "2. View message history", JOptionPane.YES_NO_OPTION);
                                            oos.writeInt(confirmDelete);
                                            oos.flush();
                                            if (confirmDelete == 0) {
                                                Boolean deletionDone = ois.readBoolean();
                                                if (deletionDone)
                                                    JOptionPane.showMessageDialog(null,
                                                            "Message deleted!",
                                                            "2. View message history", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                JOptionPane.showMessageDialog(null,
                                                        "Message Deletion canceled!",
                                                        "2. View message history", JOptionPane.INFORMATION_MESSAGE);
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null,
                                            "Not a valid number! Returning to main manu...",
                                            "2. View message history", JOptionPane.INFORMATION_MESSAGE);
                                }

                            } else if (customerMenuChoiceInt == 3) {
                                // 3. View all store dashboards
                                String dashB = (String) ois.readObject();
                                int sortOrNot = -1;
                                sortOrNot = JOptionPane.showConfirmDialog(null,
                                        "Store dashboard:\n"
                                                + dashB + "\n Do you want to sort the dashboard?"
                                        , "3. View all store dashboards", JOptionPane.YES_NO_OPTION);
                                if (sortOrNot == JOptionPane.YES_OPTION) {
                                    oos.writeBoolean(true);
                                    oos.flush();
                                    String sortedB = (String) ois.readObject();
                                    JOptionPane.showMessageDialog(null,
                                            "Sorted store dashboard: \n" + sortedB,
                                            "3. View all store dashboards", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    oos.writeBoolean(false);
                                    oos.flush();
                                }

                            } else if (customerMenuChoiceInt == 4) {
                                // 4. Choose users to block
                                boolean blockAgain;
                                ArrayList<Integer> blockSellerID = new ArrayList<>();
                                do {
                                    String beforeBlockSellerList = (String) ois.readObject();
                                    if (beforeBlockSellerList.equals("ul") || beforeBlockSellerList.isEmpty()) {
                                        JOptionPane.showMessageDialog(null,
                                                "There are no sellers available."
                                                , "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    String blockInputID = JOptionPane.showInputDialog(null,
                                            "Please enter the ID of the seller that you want to block: \n"
                                                    + beforeBlockSellerList + "\n",
                                            "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                    oos.writeObject(blockInputID);
                                    oos.flush();
                                    if (blockInputID == null) {
                                        break;
                                    }
                                    if (!beforeBlockSellerList.contains(blockInputID)) { //what if i entered 100000, and there is no such id for seller
                                        JOptionPane.showMessageDialog(null
                                                , "There is no such ID! Back to the main manu!"
                                                , "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    try {
                                        Integer.parseInt(blockInputID);
                                    } catch (NumberFormatException e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid input! Back to the main manu!"
                                                , "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    //blockSellerID.add(blockSellerIDs);
                                    //customer.setBlockedPeople(blockSellerID);
                                    Boolean blockConfirm = ois.readBoolean();
                                    if (blockConfirm) {
                                        int blockMore = -1;
                                        blockMore = JOptionPane.showConfirmDialog(null
                                                , "Finished blocking!\nDo you want to block more people?"
                                                , "4. Choose users to block", JOptionPane.YES_NO_OPTION);
                                        if (blockMore == -1 || blockMore == JOptionPane.NO_OPTION) {
                                            blockAgain = false;
                                        } else if (blockMore == JOptionPane.YES_OPTION) {
                                            blockAgain = true;
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "Invalid input! Back to the main manu!"
                                                    , "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                            blockAgain = false;
                                        }
                                        oos.writeBoolean(blockAgain);
                                        oos.flush();
                                    } else {
                                        blockAgain = false;
                                        oos.writeBoolean(blockAgain);
                                        oos.flush();
                                    }
                                } while (blockAgain);
                            } else if (customerMenuChoiceInt == 5) {
                                // 5. Choose users see as invisible
                                ArrayList<Integer> invisibleToID = new ArrayList<>();
                                boolean changeAgain;
                                do {
                                    String beforeInvisibleSellerList = (String) ois.readObject();
                                    if (beforeInvisibleSellerList.equals("ul") || beforeInvisibleSellerList.isEmpty()) {
                                        JOptionPane.showMessageDialog(null,
                                                "There are no sellers available."
                                                , "5. Choose users see as invisible",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    String invisibleInputID = JOptionPane.showInputDialog(null,
                                            "Please enter the ID of the seller that " +
                                                    "you want to become invisible to: \n"
                                                    + beforeInvisibleSellerList + "\n",
                                            "5. Choose users see as invisible"
                                            , JOptionPane.INFORMATION_MESSAGE);
                                    oos.writeObject(invisibleInputID);
                                    oos.flush();
                                    if (invisibleInputID == null) {
                                        break;
                                    }
                                    if (!beforeInvisibleSellerList.contains(invisibleInputID)) {
                                        JOptionPane.showMessageDialog(null
                                                , "There is no such ID! Back to the main manu!"
                                                , "5. Choose users see as invisible",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        break;
                                    }
                                    try {
                                        Integer.parseInt(invisibleInputID);
                                    } catch (NumberFormatException e) {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid input! Back to the main manu!"
                                                , "5. Choose users see as invisible", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                    //invisibleToID.add(invisibleToIDs);
                                    //customer.setSeesAsInvisible(invisibleToID);
                                    int invisibleMore = -1;
                                    invisibleMore = JOptionPane.showConfirmDialog(null
                                            , "Finished changing!\nDo you want to become invisible to more people?"
                                            , "5. Choose users see as invisible", JOptionPane.YES_NO_OPTION);
                                    if (invisibleMore == -1) {
                                        break;
                                    }
                                    if (invisibleMore == JOptionPane.NO_OPTION) {
                                        changeAgain = false;
                                    } else if (invisibleMore == JOptionPane.YES_OPTION) {
                                        changeAgain = true;
                                    } else {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid input! Back to the main manu!"
                                                , "5. Choose users see as invisible", JOptionPane.INFORMATION_MESSAGE);
                                        changeAgain = false;
                                    }
                                    oos.writeBoolean(changeAgain);
                                    oos.flush();
                                } while (changeAgain);
                            } else if (customerMenuChoiceInt == 6) {
                                // 6. Edit account
                                int response = -1;
                                do {
                                    int userOrPass = -1;
                                    String[] userOrPassList = {"Username", "Password"};
                                    userOrPass = JOptionPane.showOptionDialog(null,
                                            "Would you like to edit your username or password?",
                                            "6. Edit account ",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                            null, userOrPassList, userOrPassList[0]);
                                    oos.writeInt(userOrPass);
                                    oos.flush();
                                    if (userOrPass == -1) {
                                        break;
                                    }
                                    if (userOrPass == 0) {
                                        String newUsername = (String) JOptionPane.showInputDialog(null,
                                                "What would you like your new username to be?",
                                                "6. Edit account ",
                                                JOptionPane.QUESTION_MESSAGE);
                                        oos.writeObject(newUsername);
                                        oos.flush();
                                        if (newUsername == null) {
                                            break;
                                        }
                                        Boolean userConfirm = ois.readBoolean();
                                        if (userConfirm)
                                            JOptionPane.showMessageDialog(null,
                                                    "Successful username change made.", "6. Edit account",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        else
                                            JOptionPane.showMessageDialog(null,
                                                    "An Account with that Username already exists!"
                                                    , "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                    } else if (userOrPass == 1) {
                                        String newPassword = (String) JOptionPane.showInputDialog(null,
                                                "What would you like your new password to be?",
                                                "6. Edit account ",
                                                JOptionPane.QUESTION_MESSAGE);
                                        oos.writeObject(newPassword);
                                        oos.flush();
                                        if (newPassword == null) {
                                            break;
                                        }
                                        Boolean passConfirm = ois.readBoolean();
                                        if (passConfirm)
                                            JOptionPane.showMessageDialog(null,
                                                    "Successful password change made.", "6. Edit account",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    }
                                    response = JOptionPane.showConfirmDialog(null,
                                            "Would you like to edit the account again?", "6. Edit account",
                                            JOptionPane.YES_NO_OPTION);
                                    oos.writeInt(response);
                                    oos.flush();
                                    if (response == -1 || response == 1) {
                                        break;
                                    }
                                } while (true);

                            } else if (customerMenuChoiceInt == 7) {
                                // 7. Delete account
                                int response2 = 0;
                                do {
                                    response2 = JOptionPane.showConfirmDialog(null,
                                            "Are you going to delete the account?", "7. Delete account",
                                            JOptionPane.YES_NO_OPTION);
                                    oos.writeInt(response2);
                                    oos.flush();
                                    if (response2 == -1 || response2 == 1) {
                                        break;
                                    }

                                    if (response2 == 0) {
                                        String responseTwo = (String) JOptionPane.showInputDialog(null,
                                                "Enter your email to confirm delete", "7. Delete account",
                                                JOptionPane.QUESTION_MESSAGE);
                                        oos.writeObject(responseTwo);
                                        oos.flush();
                                        if (responseTwo == null)
                                            break;
                                        //  System.out.println("What is your email?");
                                        // String responseTwo = scan.nextLine();
                                        Boolean deleteConfirm = ois.readBoolean();
                                        if (deleteConfirm) {
                                            JOptionPane.showMessageDialog(null,
                                                    "Account successfully deleted.", "7. Delete account",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            loggedIn = false;
                                            break;
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "Incorrect Email!\n\nReturning to main menu!",
                                                    "7. Delete account",
                                                    JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    } //else if (response2.equalsIgnoreCase("No")) {
                                    //  break;
                                    // }
                                } while (true);
                            } else if (customerMenuChoiceInt == 8) {
                                // 8. Log out
                                loggedIn = false;
                            }
                        }
                    }

                    // PHASE 2S: SELLER EXPERIENCE

                    if (sellOrCus == 2) {
                        String sellerName = (String) ois.readObject();
                        String welcomeString = String.format("Welcome, %s!\n", sellerName);
                        while (loggedIn) {
                            int sellerMenuChoiceInt = -1;
                            String[] sellerMenuList = {"0. Send message to a customer",
                                    "1. View message history", "2. View my stores dashboards", "3. Manage stores",
                                    "4. Choose users to block", "5. Choose users to become invisible to", "6. Edit account",
                                    "7. Delete Account", "8. Log out"};
                            String sellerMenuChoiceString = (String) JOptionPane.showInputDialog(null,
                                    welcomeString + "\nSeller Marketplace Messenger Menu", "Marketplace Messenger for Sellers",
                                    JOptionPane.PLAIN_MESSAGE, null, sellerMenuList, null);
                            if (sellerMenuChoiceString == null)
                                return;
                            // TODO: Server: Send user choice to server
                            for (int i = 0; i < sellerMenuList.length; i++) {
                                if (sellerMenuChoiceString.equals(sellerMenuList[i]))
                                    sellerMenuChoiceInt = i;
                            }
                            if (sellerMenuChoiceInt == -1) {
                                JOptionPane.showMessageDialog(null, "Please select an option", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            oos.writeInt(sellerMenuChoiceInt);
                            oos.flush();

                            // TODO: Fill out each case
                            if (sellerMenuChoiceInt == 0) {
                                // 0. Send message to a customer
                                // TODO: Server: get customerArrayList and customerNamesList from server
                                ArrayList<String> customerArrayList = (ArrayList<String>) ois.readObject();
                                ArrayList<String> customerNamesList = (ArrayList<String>) ois.readObject();
                                ArrayList<String> customerNamesArrayListToDisplay = new ArrayList<String>();

                                if (customerNamesList.size() == 0) {
                                    JOptionPane.showMessageDialog(null, "You have no unblocked/visible customers",
                                            "0. Send message to a customer", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // Selecting a user to message
                                    for (int i = 0; i < customerNamesList.size(); i++) {
                                        customerNamesArrayListToDisplay.add("" + (i + 1) + ". " + customerNamesList.get(i));
                                    }
                                    String[] customerNamesListToDisplay = new String[customerNamesArrayListToDisplay.size()];
                                    for (int i = 0; i < customerNamesArrayListToDisplay.size(); i++) {
                                        customerNamesListToDisplay[i] = customerNamesArrayListToDisplay.get(i);
                                    }

                                    int customerToMessagePostion = -1;
                                    String customerToMessageString = (String) JOptionPane.showInputDialog(null,
                                            "Select a user to message!", "0. Send message to a customer",
                                            JOptionPane.PLAIN_MESSAGE, null, customerNamesListToDisplay, null);
                                    if (customerToMessageString != null) {
                                        for (int i = 0; i < customerNamesListToDisplay.length; i++) {
                                            if (customerToMessageString.equals(customerNamesListToDisplay[i])) {
                                                customerToMessagePostion = i;
                                                break;
                                            }
                                        }
                                        oos.writeInt(customerToMessagePostion);
                                        oos.flush();
                                        if (customerToMessagePostion == -1) {
                                            // if customer's message not appearing
                                            JOptionPane.showMessageDialog(null,
                                                    "Cannot find this customer.", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        // TODO: client-server communication needed for next line
                                        String messageToCustomer = JOptionPane.showInputDialog(null, "What is your message?",
                                                "0. Send message to a customer", JOptionPane.PLAIN_MESSAGE);
                                        oos.writeObject(messageToCustomer);
                                        oos.flush();
                                        // TODO: Server: Send user message and recipient to server OR if they chose not to send a message
                                        if (messageToCustomer == null) {
                                            JOptionPane.showMessageDialog(null, "No message sent!",
                                                    "0. Send message to a customer", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Message Sent!",
                                                    "0. Send message to a customer", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    } else {
                                        oos.writeInt(-1);
                                        oos.flush();
                                        JOptionPane.showMessageDialog(null, "There are no users for you to message!",
                                                "0. Send message to a customer", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                }

                            } else if (sellerMenuChoiceInt == 1) {
                                // 1. View message history
                                String repeatString = "";
                                int cusID = -1;
                                String cusName = "";
                                // TODO: Server: Client needs to get cusList and cusNameList from server (next 2 lines)
                                ArrayList<String> cusList = (ArrayList<String>) ois.readObject();
                                ArrayList<String> cusNameList = (ArrayList<String>) ois.readObject();
                                ArrayList<String> cusNameArrayListToDisplay = new ArrayList<String>();
                                if (cusNameList.size() == 0) {
                                    JOptionPane.showMessageDialog(null, "You have no unblocked/visible customers",
                                            "1. View message history", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // If there is a message history
                                    for (int i = 0; i < cusNameList.size(); i++) {
                                        cusNameArrayListToDisplay.add("" + (i + 1) + ". " + cusNameList.get(i));
                                    }
                                    String[] cusNameListToDisplay = new String[cusNameArrayListToDisplay.size()];
                                    for (int i = 0; i < cusNameArrayListToDisplay.size(); i++) {
                                        cusNameListToDisplay[i] = cusNameArrayListToDisplay.get(i);
                                    }
                                    int customerToViewIndex = -1;
                                    String customerToViewString = (String) JOptionPane.showInputDialog(null,
                                            "Choose a Customer to view history", "1. View message history",
                                            JOptionPane.PLAIN_MESSAGE, null, cusNameListToDisplay, null);
                                    if (customerToViewString == null) {
                                        oos.writeInt(-1);
                                        oos.flush();
                                        break;
                                    }
                                    for (int i = 0; i < cusNameListToDisplay.length; i++) {
                                        if (customerToViewString.equals(cusNameListToDisplay[i])) {
                                            customerToViewIndex = i;
                                            break;
                                        }
                                    }
                                    oos.writeInt(customerToViewIndex);
                                    oos.flush();
                                    // TODO: client-server communication needed for next 2 lines
                                    ArrayList<String> messageHistoryArrayList = (ArrayList<String>) ois.readObject();
                                    if (messageHistoryArrayList.isEmpty()) {
                                        // If message history with a specific user does not exist
                                        JOptionPane.showMessageDialog(null,
                                                "You have no message history with this user!\nReturning to main menu",
                                                "1. View message history", JOptionPane.PLAIN_MESSAGE);
                                    } else {
                                        // If message history with a specific user already exists
                                        ArrayList<String> messageArrayListToDisplay = (ArrayList<String>) ois.readObject();
                                        String[] messageListToDisplay = new String[messageArrayListToDisplay.size()];
                                        String messageHistoryString = "";
                                        String[] messageOptionsList = {"0. Edit Message", "1. Delete Message", "2. Return to menu"};
                                        for (int i = 0; i < messageArrayListToDisplay.size(); i++) {
                                            messageListToDisplay[i] = messageArrayListToDisplay.get(i);
                                            messageHistoryString += "\n" + messageListToDisplay[i];
                                        }
                                        int optionChoicePosition = -1;
                                        String optionChoiceString = (String) JOptionPane.showInputDialog(null,
                                                "Message History\n" + messageHistoryString + "\n\nOptions:",
                                                "1. View message history", JOptionPane.PLAIN_MESSAGE,
                                                null, messageOptionsList, null);
                                        if (optionChoiceString == null) {
                                            oos.writeInt(-1);
                                            oos.flush();
                                            break;
                                        }
                                        // TODO: Server: Send user choice to server
                                        for (int i = 0; i < messageOptionsList.length; i++) {
                                            if (optionChoiceString.equals(messageOptionsList[i])) {
                                                optionChoicePosition = i;
                                                break;
                                            }
                                        }
                                        oos.writeInt(optionChoicePosition);
                                        oos.flush();
                                        String[] formattedHistoryList = new String[messageArrayListToDisplay.size()];
                                        // elements in format -> You: message (or) Sender: message
                                        for (int i = 0; i < messageArrayListToDisplay.size(); i++) {
                                            formattedHistoryList[i] = messageArrayListToDisplay.get(i);
                                        }
                                        if (optionChoicePosition == 0) {
                                            // Option 0. Edit Message
                                            int messageToEditIndex = -1;
                                            String messageToEditString = (String) JOptionPane.showInputDialog(null,
                                                    "Select a message to edit:", "1. View message history/Option 0. Edit Message",
                                                    JOptionPane.PLAIN_MESSAGE, null, formattedHistoryList, null);
                                            if (messageToEditString == null) {
                                                oos.writeInt(-1);
                                                oos.flush();
                                                break;
                                            }
                                            // TODO: Server: Send user choice to server
                                            for (int i = 0; i < formattedHistoryList.length; i++) {
                                                if (messageToEditString.equals(formattedHistoryList[i])) {
                                                    messageToEditIndex = i;
                                                    break;
                                                }
                                            }
                                            oos.writeInt(messageToEditIndex);
                                            oos.flush();
                                            String[] messageToBeEditedList = formattedHistoryList[messageToEditIndex].split(": ");
                                            String messageToBeEdited = messageToBeEditedList[1];
                                            String editedMessageInput = JOptionPane.showInputDialog(null,
                                                    "What would you like to change the message to?",
                                                    "1. View message history/Option 0. Edit Message",
                                                    JOptionPane.QUESTION_MESSAGE);
                                            oos.writeObject(editedMessageInput);
                                            oos.flush();
                                            if (editedMessageInput == null)
                                                break;
                                            // TODO: client-server communication needed for next line
                                            Boolean sellerEditConfirmation = ois.readBoolean();
                                            if (sellerEditConfirmation)
                                                JOptionPane.showMessageDialog(null, "Message Edited!",
                                                        "1. View message history/Option 0. Edit Message", JOptionPane.PLAIN_MESSAGE);
                                        } else if (optionChoicePosition == 1) {
                                            // Option 1. Delete Message
                                            String messageToDeleteString = (String) JOptionPane.showInputDialog(null,
                                                    "Select a message to delete:", "1. View message history/1. Delete Message",
                                                    JOptionPane.PLAIN_MESSAGE, null, formattedHistoryList, null);
                                            // TODO: let server know choice
                                            if (messageToDeleteString == null) {
                                                oos.writeInt(-1);
                                                oos.flush();
                                                break;
                                            }
                                            int messageToDeleteIndex = -1;
                                            for (int i = 0; i < formattedHistoryList.length; i++) {
                                                if (messageToDeleteString.equals(formattedHistoryList[i])) {
                                                    messageToDeleteIndex = i;
                                                }
                                            }
                                            oos.writeInt(messageToDeleteIndex);
                                            oos.flush();
                                            String[] messageToBeDeletedList = formattedHistoryList[messageToDeleteIndex].split(": ");
                                            String messageToBeDeleted = messageToBeDeletedList[1];
                                            // TODO: client-server communication needed for next line
                                            Boolean sellerDeleteConfirm = ois.readBoolean();
                                            if (sellerDeleteConfirm)
                                                JOptionPane.showMessageDialog(null,
                                                        "Message [" + messageToBeDeleted + "] has been deleted" +
                                                                "\nNote: This only deletes a message for you, the receiver may still be able to view it",
                                                        "1. View message history/1. Delete Message", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    }
                                }


                            } else if (sellerMenuChoiceInt == 2) {
                                // 2. View my stores dashboards
                                int storeID = -1;
                                try {
                                    String formattedStoreString = (String) ois.readObject();
                                    if (formattedStoreString == null || formattedStoreString.isEmpty() || formattedStoreString.equals(" ")) {
                                        JOptionPane.showMessageDialog(null, "You don't have any stores!",
                                                "2. View my stores dashboards", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        // If the seller DOES have stores
                                        String[] formattedStoreList = formattedStoreString.split("\n");
                                        int storeToViewIndex = -1;
                                        String storeToViewString = (String) JOptionPane.showInputDialog(null,
                                                "Choose a Store to view its Dashboard", "2. View my stores dashboards",
                                                JOptionPane.PLAIN_MESSAGE, null, formattedStoreList, null);
                                        if (storeToViewString != null) {
                                            // TODO: Server: Send user choice to server
                                            for (int i = 0; i < formattedStoreList.length; i++) {
                                                if (storeToViewString.equals(formattedStoreList[i])) {
                                                    storeToViewIndex = i;
                                                    break;
                                                }
                                            }
                                            oos.writeInt(storeToViewIndex);
                                            oos.flush();
                                            String[] storeIDList = formattedStoreList[storeToViewIndex].split(" - ");
                                            storeID = Integer.parseInt(storeIDList[1]);
                                            ArrayList<String> dashboardWordsArrayList = new ArrayList<String>();
                                            ArrayList<String> dashboardUsersArrayList = new ArrayList<String>();

                                            String[] dashboardChoiceList = {"0. User", "1. Word"};

                                            int dashboardChoiceIndex = -1;
                                            String dashboardChoiceString = (String) JOptionPane.showInputDialog(null,
                                                    "Would you like the user dashboard or word dashboard?", "2. View my stores dashboards",
                                                    JOptionPane.PLAIN_MESSAGE, null, dashboardChoiceList, null);
                                            if (dashboardChoiceString == null) {
                                                oos.writeInt(-1);
                                                oos.flush();
                                                break;
                                            }
                                            // TODO: Server: Send user choice to server
                                            for (int i = 0; i < dashboardChoiceList.length; i++) {
                                                if (dashboardChoiceString.equals(dashboardChoiceList[i])) {
                                                    dashboardChoiceIndex = i;
                                                    break;
                                                }
                                            }
                                            oos.writeInt(dashboardChoiceIndex);
                                            oos.flush();
                                            if (dashboardChoiceIndex == 0) {
                                                // 0. User
                                                dashboardUsersArrayList = (ArrayList<String>) ois.readObject();
                                                String[] dashboardUsersList = new String[dashboardUsersArrayList.size()];
                                                for (int i = 0; i < dashboardUsersArrayList.size(); i++) {
                                                    dashboardUsersList[i] = dashboardUsersArrayList.get(i);
                                                }
                                                if (dashboardUsersArrayList.isEmpty()) {
                                                    JOptionPane.showMessageDialog(null,
                                                            "No such store exists!\nReturning to main menu",
                                                            "2. View my stores dashboards", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    String dashboardUsersString = "";
                                                    if (dashboardUsersArrayList.size() > 10) {
                                                        for (int i = 0; i < 10; i++) {
                                                            dashboardUsersString += dashboardUsersArrayList.get(i) + "\n";
                                                        }
                                                    } else {
                                                        for (String s : dashboardUsersArrayList) {
                                                            dashboardUsersString += s + "\n";
                                                        }
                                                    }
                                                    String[] sortOrReturnList = {"Sort by from least to most", "Return to Menu"};
                                                    String sortOrReturnString = (String) JOptionPane.showInputDialog(null,
                                                            "User Dashboard (Sorted by most to least)\n\n" + dashboardUsersString + "\nOptions:",
                                                            "2. View my stores dashboards",
                                                            JOptionPane.PLAIN_MESSAGE, null, sortOrReturnList, null);
                                                    if (sortOrReturnString == null)
                                                        break;
                                                    int sortOrReturnInt = -1;
                                                    for (int i = 0; i < sortOrReturnList.length; i++) {
                                                        if (sortOrReturnString.equals(sortOrReturnList[i])) {
                                                            sortOrReturnInt = i;
                                                            break;
                                                        }
                                                    }
                                                    if (sortOrReturnInt == 0) {
                                                        dashboardUsersString = "";
                                                        if (dashboardUsersArrayList.size() > 10) {
                                                            for (int i = 9; i >= 0; i--) {
                                                                dashboardUsersString += dashboardUsersArrayList.get(i) + "\n";
                                                            }
                                                        } else {
                                                            for (int i = dashboardUsersArrayList.size() - 1; i >= 0; i--) {
                                                                dashboardUsersString += dashboardUsersArrayList.get(i) + "\n";
                                                            }
                                                        }

                                                        JOptionPane.showMessageDialog(null,
                                                                "User Dashboard (Sorted by least to most)\n\n" + dashboardUsersString,
                                                                "2. View my stores dashboards",
                                                                JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                            } else if (dashboardChoiceIndex == 1) {
                                                // 1. Word
                                                dashboardWordsArrayList = (ArrayList<String>) ois.readObject();
                                                String[] dashboardWordsList = new String[dashboardWordsArrayList.size()];
                                                for (int i = 0; i < dashboardWordsArrayList.size(); i++) {
                                                    dashboardWordsList[i] = dashboardWordsArrayList.get(i);
                                                }
                                                if (dashboardWordsArrayList.isEmpty()) {
                                                    JOptionPane.showMessageDialog(null,
                                                            "No such store exists!\nReturning to main menu",
                                                            "2. View my stores dashboards", JOptionPane.ERROR_MESSAGE);
                                                } else {
                                                    String dashboardWordsString = "";
                                                    if (dashboardWordsArrayList.size() > 10)
                                                        for (int i = 0; i < 10; i++) {
                                                            dashboardWordsString += dashboardWordsArrayList.get(i) + "\n";
                                                        }
                                                    else
                                                        for (String s : dashboardWordsArrayList) {
                                                            dashboardWordsString += s + "\n";
                                                        }
                                                    String[] sortOrReturnList = {"Sort by from least to most", "Return to Menu"};
                                                    String sortOrReturnString = (String) JOptionPane.showInputDialog(null,
                                                            "Words Dashboard (Sorted by most to least)\n" + dashboardWordsString + "Options:",
                                                            "2. View my stores dashboards",
                                                            JOptionPane.PLAIN_MESSAGE, null, sortOrReturnList, null);
                                                    if (sortOrReturnString == null)
                                                        break;

                                                    int sortOrReturnInt = -1;
                                                    for (int i = 0; i < sortOrReturnList.length; i++) {
                                                        if (sortOrReturnString.equals(sortOrReturnList[i])) {
                                                            sortOrReturnInt = i;
                                                            break;
                                                        }
                                                    }
                                                    if (sortOrReturnInt == 0) {
                                                        dashboardWordsString = "";
                                                        if (dashboardWordsArrayList.size() > 10) {
                                                            for (int i = 9; i >= 0; i--) {
                                                                dashboardWordsString += dashboardWordsArrayList.get(i) + "\n";
                                                            }
                                                        } else {
                                                            for (int i = dashboardWordsArrayList.size() - 1; i >= 0; i--) {
                                                                dashboardWordsString += dashboardWordsArrayList.get(i) + "\n";
                                                            }
                                                        }
                                                        JOptionPane.showMessageDialog(null,
                                                                "User Dashboard (Sorted by least to most messages)\n" + dashboardWordsString,
                                                                "2. View my stores dashboards", JOptionPane.PLAIN_MESSAGE);
                                                    }
                                                }
                                            } else if (dashboardChoiceIndex == -1) {
                                                // do nothing
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Invalid choice!",
                                                        "2. View my stores dashboards", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "You don't have any stores to view!",
                                                    "2. View my stores dashboards", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null, "Invalid choice!",
                                            "2. View my stores dashboards", JOptionPane.ERROR_MESSAGE);
                                }

                            } else if (sellerMenuChoiceInt == 3) {
                                // 3. Manage stores
                                int reply;
                                String response;
                                int storeID;
                                String resp;
                                boolean bool;
                                try {
                                    storeEdit:
                                    do {
                                        String[] storeManageList = {"Edit Store", "Create New Store"};
                                        String storeResp = (String) JOptionPane.showInputDialog(null,
                                                "Would you like to create a new store or edit a current store?\n\nOptions:",
                                                "Manage Stores",
                                                JOptionPane.PLAIN_MESSAGE, null, storeManageList, null);
                                        oos.writeObject(storeResp);
                                        oos.flush();
                                        if (storeResp == null)
                                            break;
                                        if (storeResp.equals(storeManageList[0])) {
                                            // "Edit Store"
                                            String formattedStoreString = (String) ois.readObject();
                                            if (formattedStoreString == null || formattedStoreString.isEmpty() || formattedStoreString.equals(" ")) {
                                                JOptionPane.showMessageDialog(null, "You don't have any stores to edit!",
                                                        "3. Manage stores", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                // If the seller DOES have stores
                                                String[] formattedStoreList = formattedStoreString.split("\n");
                                                int storeToViewIndex = -1;
                                                String storeToViewString = (String) JOptionPane.showInputDialog(null,
                                                        "Choose a Store to edit", "Edit Store",
                                                        JOptionPane.PLAIN_MESSAGE, null, formattedStoreList, null);
                                                if (storeToViewString == null) {
                                                    oos.writeInt(-1);
                                                    oos.flush();
                                                    break;
                                                }
                                                storeID = Integer.parseInt(storeToViewString.substring(storeToViewString.indexOf("ID - ") + 5));
                                                oos.writeInt(storeID);
                                                oos.flush();
                                                String[] editStoreList = {"Edit Name", "Delete Store"};
                                                resp = (String) JOptionPane.showInputDialog(null,
                                                        "What would you like to do?", "Edit Store",
                                                        JOptionPane.PLAIN_MESSAGE, null, editStoreList, null);
                                                oos.writeObject(resp);
                                                oos.flush();
                                                if (resp == null)
                                                    break;
                                                if (resp.equals(editStoreList[0])) {
                                                    String newName = JOptionPane.showInputDialog(null, "Enter the new store name", "Edit Store Name",
                                                            JOptionPane.QUESTION_MESSAGE);
                                                    oos.writeObject(newName);
                                                    oos.flush();
                                                    bool = ois.readBoolean();
                                                    if (bool) {
                                                        JOptionPane.showMessageDialog(null, "Success!",
                                                                "Edit Store Name", JOptionPane.INFORMATION_MESSAGE);
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "ERROR: A store with that name already exists!",
                                                                "Edit Store Name", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                } else if (resp.equals(editStoreList[1])) {
                                                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you would like to delete [" + storeToViewString + "] ?", "Edit Store", JOptionPane.YES_NO_OPTION);
                                                    if (confirm == JOptionPane.YES_OPTION) {
                                                        oos.writeBoolean(true);
                                                        oos.flush();
                                                        JOptionPane.showMessageDialog(null, "Success!",
                                                                "Delete Store", JOptionPane.INFORMATION_MESSAGE);
                                                    } else {
                                                        oos.writeBoolean(false);
                                                        oos.flush();
                                                        JOptionPane.showMessageDialog(null, "Store was not deleted",
                                                                "Delete Store", JOptionPane.INFORMATION_MESSAGE);
                                                    }
                                                } else {
                                                    break;
                                                }
                                            }
                                        } else if (storeResp.equals(storeManageList[1])) {
                                            // "Create New Store"
                                            ArrayList<String> stores = (ArrayList<String>) ois.readObject();
                                            String newStoreName = "";
                                            String s1;
                                            Boolean confirmNewName;
                                            loop:
                                            while (true) {
                                                newStoreName = JOptionPane.showInputDialog(null, "Enter the new store's name", "Create New Store",
                                                        JOptionPane.QUESTION_MESSAGE);
                                                if (newStoreName.isEmpty()) {
                                                    JOptionPane.showMessageDialog(null, "ERROR: Please enter a Store Name!",
                                                            "Create New Store", JOptionPane.ERROR_MESSAGE);
                                                    continue;
                                                }
                                                oos.writeObject(newStoreName);
                                                oos.flush();
                                                confirmNewName = ois.readBoolean();
                                                if (!confirmNewName) {
                                                    JOptionPane.showMessageDialog(null, "ERROR: A store with that name already exists!",
                                                            "Create New Store", JOptionPane.ERROR_MESSAGE);
                                                    int cont = JOptionPane.showConfirmDialog(null, "Would you like to use a different name?", "Create New Store", JOptionPane.YES_NO_OPTION);
                                                    if (cont == JOptionPane.YES_OPTION) {
                                                        oos.writeBoolean(true);
                                                        oos.flush();
                                                        continue loop;
                                                    } else {
                                                        oos.writeBoolean(false);
                                                        oos.flush();
                                                        JOptionPane.showMessageDialog(null, "No Store Created!",
                                                                "Create New Store", JOptionPane.INFORMATION_MESSAGE);
                                                        break storeEdit;
                                                    }
                                                }
                                                break;
                                            }
                                            //Boolean newStoreConfirmation = ois.readBoolean();
                                            if (confirmNewName)
                                                JOptionPane.showMessageDialog(null, "Success",
                                                        "Create New Store", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null, "Invalid Input!",
                                                    "3. Manage stores", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        reply = JOptionPane.showConfirmDialog(null, "Would you like to edit again?",
                                                "Edit Store", JOptionPane.YES_NO_OPTION);
                                        oos.writeBoolean(reply == JOptionPane.YES_OPTION);
                                        oos.flush();
                                    } while (reply == JOptionPane.YES_OPTION);
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null, "ERROR: Not a Valid Number!",
                                            "3. Manage stores", JOptionPane.ERROR_MESSAGE);
                                }

                            } else if (sellerMenuChoiceInt == 4) {
                                // 4. Choose users to block
                                int blocker = -1;
                                do {
                                    String[] editStoreList = {"Add", "Remove"};
                                    blocker = JOptionPane.showOptionDialog(null,
                                            "Would you like to add or remove blocked users?", "Block Users",
                                            0, 3, null, editStoreList, editStoreList[0]);
                                    oos.writeInt(blocker);
                                    oos.flush();
                                    if (blocker == 0) {
                                        // Adding customers to wasBlockedBy in customerData and hasBlocked in sellerData
                                        ArrayList<String> notBlockedCustomerNameArrayList = (ArrayList<String>) ois.readObject();
                                        if (!notBlockedCustomerNameArrayList.isEmpty()) {
                                            String[] notBlockedCustomerNameList = new String[notBlockedCustomerNameArrayList.size()];
                                            for (int i = 0; i < notBlockedCustomerNameArrayList.size(); i++) {
                                                notBlockedCustomerNameList[i] = notBlockedCustomerNameArrayList.get(i);
                                            }
                                            String userToBlock = (String) JOptionPane.showInputDialog(null,
                                                    "Please select a customer to block:", "4. Choose users to block",
                                                    JOptionPane.PLAIN_MESSAGE, null, notBlockedCustomerNameList, null);
                                            if (notBlockedCustomerNameList == null || userToBlock == null) {
                                                oos.writeInt(-1);
                                                oos.flush();
                                                break;
                                            }
                                            String[] idToBlockList = userToBlock.split(" - ");
                                            int idToBlock = Integer.parseInt(idToBlockList[1]);
                                            oos.writeInt(idToBlock);
                                            oos.flush();
                                            Boolean blockConfirm = ois.readBoolean();
                                            if (blockConfirm)
                                                JOptionPane.showMessageDialog(null,
                                                        "Finished blocking!",
                                                        "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "You have no unblocked customers!",
                                                    "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                    } else if (blocker == 1) {
                                        // Removing customers from wasBlockedBy in customerData and hasBlocked in sellerData
                                        ArrayList<String> blockedCustomerNameArrayList = (ArrayList<String>) ois.readObject();
                                        if (!blockedCustomerNameArrayList.isEmpty()) {
                                            String[] blockedCustomerNameList = new String[blockedCustomerNameArrayList.size()];
                                            for (int i = 0; i < blockedCustomerNameArrayList.size(); i++) {
                                                blockedCustomerNameList[i] = blockedCustomerNameArrayList.get(i);
                                            }
                                            String userToUnblock = (String) JOptionPane.showInputDialog(null,
                                                    "Please select a customer to unblock:", "4. Choose users to block",
                                                    JOptionPane.PLAIN_MESSAGE, null, blockedCustomerNameList, null);
                                            if (blockedCustomerNameArrayList == null || userToUnblock == null) {
                                                oos.writeInt(-1);
                                                oos.flush();
                                                break;
                                            }
                                            String[] idToUnblockList = userToUnblock.split(" - ");
                                            int idToUnblock = Integer.parseInt(idToUnblockList[1]);
                                            oos.writeInt(idToUnblock);
                                            oos.flush();
                                            Boolean unBlockConfirm = ois.readBoolean();
                                            if (unBlockConfirm)
                                                JOptionPane.showMessageDialog(null,
                                                        "Finished unblocking!",
                                                        "4. Choose users to block", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "You have no blocked customers!", "4. Choose users to block",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null,
                                                "Invalid input! Back to the main manu!", "4. Choose users to block",
                                                JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                    int blockMore = -1;
                                    blockMore = JOptionPane.showConfirmDialog(null,
                                            "Do you want to block/unblock more people?", "4. Choose users to block",
                                            JOptionPane.YES_NO_OPTION);
                                    oos.writeInt(blockMore);
                                    oos.flush();
                                    if (blockMore == -1 || blockMore == 1)
                                        break;
                                } while (true);

                            } else if (sellerMenuChoiceInt == 5) {
                                // 5. Choose users to become invisible to
                                do {
                                    int addOrRemove = -1;
                                    String[] addOrRemoveList = {"Add", "Remove"};
                                    addOrRemove = JOptionPane.showOptionDialog(null,
                                            "Would you like to add or remove users that see you as invisible?",
                                            "5. Choose users to become invisible to",
                                            0, 3, null, addOrRemoveList, addOrRemoveList[0]);
                                    oos.writeInt(addOrRemove);
                                    oos.flush();
                                    if (addOrRemove == -1)
                                        break;
                                    if (addOrRemove == 0) {
                                        // Set customer as invisible
                                        ArrayList<String> seeAsVisibleArrayList = (ArrayList<String>) ois.readObject();
                                        String[] seeAsVisibleList = new String[seeAsVisibleArrayList.size()];
                                        for (int i = 0; i < seeAsVisibleArrayList.size(); i++) {
                                            seeAsVisibleList[i] = seeAsVisibleArrayList.get(i);
                                        }
                                        if (!seeAsVisibleArrayList.isEmpty()) {
                                            String userToInvisible = (String) JOptionPane.showInputDialog(null,
                                                    "Please select a customer that you want to see as invisible",
                                                    "5. Choose users to become invisible to",
                                                    JOptionPane.PLAIN_MESSAGE, null, seeAsVisibleList, null);
                                            oos.writeObject(userToInvisible);
                                            oos.flush();
                                            if (userToInvisible == null)
                                                break;
                                            Boolean confirmInvisibleAdd = ois.readBoolean();
                                            if (confirmInvisibleAdd)
                                                JOptionPane.showMessageDialog(null,
                                                        "Finished! You will now see this customer as invisible!",
                                                        "5. Choose users to become invisible to",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            JOptionPane.showMessageDialog(null,
                                                    "You have no visible customers!",
                                                    "5. Choose users to become invisible to",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                    } else if (addOrRemove == 1) {
                                        // Reverse setting customer as invisible
                                        ArrayList<String> seeAsInvisibleArrayList = (ArrayList<String>) ois.readObject();
                                        String[] seeAsInvisibleList = new String[seeAsInvisibleArrayList.size()];
                                        for (int i = 0; i < seeAsInvisibleArrayList.size(); i++) {
                                            seeAsInvisibleList[i] = seeAsInvisibleArrayList.get(i);
                                        }
                                        if (seeAsInvisibleArrayList.isEmpty()) {
                                            JOptionPane.showMessageDialog(null,
                                                    "You have no invisible customers!",
                                                    "5. Choose users to become invisible to",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                        String userToUninvisible = (String) JOptionPane.showInputDialog(null,
                                                "Please select an invisible customer that you want to see",
                                                "5. Choose users to become invisible to",
                                                JOptionPane.PLAIN_MESSAGE, null, seeAsInvisibleList, null);
                                        oos.writeObject(userToUninvisible);
                                        oos.flush();
                                        if (userToUninvisible == null)
                                            break;
                                        Boolean confirmUninvisible = ois.readBoolean();
                                        if (confirmUninvisible)
                                            JOptionPane.showMessageDialog(null,
                                                    "Finished! You will now see this customer as visible!",
                                                    "5. Choose users to become invisible to",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Invalid Input",
                                                "5. Choose users to become invisible to", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                    int invisMore = -1;
                                    invisMore = JOptionPane.showConfirmDialog(null,
                                            "Do you want to change the invisibility more?", "5. Choose users to become invisible to",
                                            JOptionPane.YES_NO_OPTION);
                                    oos.writeInt(invisMore);
                                    oos.flush();
                                    if (invisMore == -1 || invisMore == 1)
                                        break;
                                } while (true);

                            } else if (sellerMenuChoiceInt == 6) {
                                // 6. Edit account
                                String response = "";
                                do {
                                    String[] sellerEditList = {"Username", "Password", "Email"};
                                    String editTypeChoice = (String) JOptionPane.showInputDialog(null,
                                            "What do you want to edit?", "6. Edit account",
                                            JOptionPane.PLAIN_MESSAGE, null, sellerEditList, null);
                                    oos.writeObject(editTypeChoice);
                                    oos.flush();
                                    Boolean success;
                                    if (editTypeChoice.equals(sellerEditList[0])) {
                                        // Username
                                        String newUsername = JOptionPane.showInputDialog(null,
                                                "Enter your new username", "6. Edit account", JOptionPane.PLAIN_MESSAGE);
                                        oos.writeObject(newUsername);
                                        oos.flush();
                                        if (newUsername == null)
                                            break;
                                        else if (newUsername.isEmpty()) {
                                            JOptionPane.showMessageDialog(null,
                                                    "ERROR: No Username Entered!\nReturning to menu!", "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        success = ois.readBoolean();
                                        if (success)
                                            JOptionPane.showMessageDialog(null,
                                                    "Successful username change made.", "6. Edit account", JOptionPane.INFORMATION_MESSAGE);
                                        else
                                            JOptionPane.showMessageDialog(null,
                                                    "ERROR: That username is taken!", "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                    } else if (editTypeChoice.equals(sellerEditList[1])) {
                                        // Password
                                        String newPassword = JOptionPane.showInputDialog(null,
                                                "Enter your new password", "6. Edit account", JOptionPane.PLAIN_MESSAGE);
                                        oos.writeObject(newPassword);
                                        oos.flush();
                                        if (newPassword == null)
                                            break;
                                        else if (newPassword.isEmpty()) {
                                            JOptionPane.showMessageDialog(null,
                                                    "ERROR: No Password Entered!\nReturning to menu!", "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        Boolean passConfirm = ois.readBoolean();
                                        if (passConfirm)
                                            JOptionPane.showMessageDialog(null, "Successful password change made.",
                                                    "6. Edit account", JOptionPane.INFORMATION_MESSAGE);
                                    } else if (editTypeChoice.equals(sellerEditList[2])) {
                                        // Email
                                        String newEmail = JOptionPane.showInputDialog(null,
                                                "Enter your new email", "6. Edit account", JOptionPane.PLAIN_MESSAGE);
                                        oos.writeObject(newEmail);
                                        oos.flush();
                                        if (newEmail == null)
                                            break;
                                        else if (newEmail.isEmpty()) {
                                            JOptionPane.showMessageDialog(null,
                                                    "ERROR: No Email Entered!\nReturning to menu!", "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                        success = ois.readBoolean();
                                        if (success)
                                            JOptionPane.showMessageDialog(null, "Successful email change made.",
                                                    "6. Edit account", JOptionPane.INFORMATION_MESSAGE);
                                        else
                                            JOptionPane.showMessageDialog(null, "ERROR: That email is already associated with another account!",
                                                    "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Invalid input!",
                                                "6. Edit account", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                    int editMore = -1;
                                    editMore = JOptionPane.showConfirmDialog(null,
                                            "Would you like to edit again?", "6. Edit account",
                                            JOptionPane.YES_NO_OPTION);
                                    oos.writeInt(editMore);
                                    oos.flush();
                                    if (editMore == -1 || editMore == 1)
                                        break;
                                } while (true);

                            } else if (sellerMenuChoiceInt == 7) {
                                // 7. Delete Account
                                int response = -1;
                                response = JOptionPane.showConfirmDialog(null,
                                        "Do you want to delete this account?", "7. Delete Account",
                                        JOptionPane.YES_NO_OPTION);
                                oos.writeInt(response);
                                oos.flush();
                                if (response == 0) {
                                    String emailConfirm = JOptionPane.showInputDialog(null,
                                            "Confirm your email to delete your account\n    Email: ",
                                            "7. Delete Account", JOptionPane.PLAIN_MESSAGE);
                                    oos.writeObject(emailConfirm);
                                    oos.flush();
                                    if (emailConfirm == null)
                                        break;
                                    Boolean correctEmail = ois.readBoolean();
                                    if (correctEmail) {
                                        loggedIn = false;
                                        JOptionPane.showMessageDialog(null, "Successfully deleted account!",
                                                "7. Delete Account", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "ERROR: Incorrect email",
                                                "7. Delete Account", JOptionPane.ERROR_MESSAGE);
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
}
