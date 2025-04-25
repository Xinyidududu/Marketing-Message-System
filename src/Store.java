
import java.util.ArrayList;

/**
 * A Store Class for CS180 project 5
 *
 * @author Abhi Chalasani, Xinyi Guan, Marissa Capelli, Snigdha Mishra
 * @version 4/29/23
 */

public class Store {
    private int id;
    private String name;
    private ArrayList<String> commonWords;
    private ArrayList<Integer> commonWordsFrequency;
    private ArrayList<Integer> users;
    private ArrayList<Integer> userFrequency;


    public Store(int id, String name, ArrayList<String> commonWords, ArrayList<Integer> commonWordsFrequency
            , ArrayList<Integer> users, ArrayList<Integer> userFrequency) {
        this.id = id;
        this.name = name;
        this.commonWords = commonWords;
        this.commonWordsFrequency = commonWordsFrequency;
        this.users = users;
        this.userFrequency = userFrequency;
    }

    public Store(int id, String name) {
        this.id = id;
        this.name = name;
        this.commonWords = new ArrayList<String>();
        this.commonWordsFrequency = new ArrayList<Integer>();
        this.users = new ArrayList<Integer>();
        this.userFrequency = new ArrayList<Integer>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> formatStore() {
        ArrayList<String> formatted = new ArrayList<>();
        formatted.add(String.format("Store<ID:%d,Name:%s>", this.id, this.name));
        String usersFormat = "";
        String userFreq = "";
        if (users.size() != userFrequency.size())
            return formatted;
        for (int i = 0; i < users.size(); i++) {
            usersFormat += users.get(i);
            userFreq += userFrequency.get(i);
            if (i != users.size() - 1) {
                usersFormat += ",";
                userFreq += ",";
            }
        }
        if (!usersFormat.isEmpty())
            formatted.add(usersFormat);
        else
            formatted.add("0");
        if (!userFreq.isEmpty())
            formatted.add(userFreq);
        else
            formatted.add("0");
        String words = "";
        String wordFreq = "";
        if (commonWords.size() != commonWordsFrequency.size())
            return formatted;
        for (int i = 0; i < commonWords.size(); i++) {
            words += commonWords.get(i);
            wordFreq += commonWordsFrequency.get(i);
            if (i != commonWords.size() - 1) {
                words += ",";
                wordFreq += ",";
            }
        }
        if (!words.isEmpty())
            formatted.add(words);
        else
            formatted.add("0");
        if (!wordFreq.isEmpty())
            formatted.add(wordFreq);
        else
            formatted.add("0");
        return formatted;
    }

    public ArrayList<String> sortList() {
        ArrayList<Integer> wordsFreq = commonWordsFrequency;
        ArrayList<String> words = commonWords;
        ArrayList<String> wordSorted = new ArrayList<>();
        int maximum;
        int i;
        if (wordsFreq.size() == 1 && wordsFreq.get(0) == 0) {
            wordSorted.add("No users have messaged yet!");
            return wordSorted;
        }
        wordsFreq.remove((Integer) 0);
        words.remove("0");
        while (!wordsFreq.isEmpty()) {
            i = 1;
            maximum = wordsFreq.get(0);
            int index = 0;
            while (i < wordsFreq.size()) {
                if (maximum < wordsFreq.get(i)) {
                    maximum = wordsFreq.get(i);
                    index = i;
                }
                i++;
            }
            wordSorted.add("\"" + words.get(index) + "\" occurs " + wordsFreq.get(index) + " times");
            words.remove(index);
            wordsFreq.remove(index);
        }
        return wordSorted;
    }

    public ArrayList<String> sortUserList() {
        ArrayList<Integer> userFreq = userFrequency;
        ArrayList<Integer> usersList = users;
        ArrayList<String> userSorted = new ArrayList<>();
        Seller st = new Seller();
        int maximum;
        int i;
        if (usersList.size() == 1 && usersList.get(0) == 0) {
            userSorted.add("No users have messaged yet!");
            return userSorted;
        }
        userFreq.remove((Integer) 0);
        usersList.remove((Integer) 0);
        while (!userFreq.isEmpty()) {
            i = 1;
            maximum = userFreq.get(0);
            int index = 0;
            while (i < userFreq.size()) {
                if (maximum < userFreq.get(i)) {
                    maximum = userFreq.get(i);
                    index = i;
                }
                i++;
            }
            userSorted.add("\"" + st.getCustomerNameFromID(usersList.get(index)) + "\" has messaged you "
                    + userFreq.get(index) + " times");
            usersList.remove(index);
            userFreq.remove(index);
        }
        return userSorted;
    }

    public String formatStoreForDash() {
        return String.format("%s, ID - %d", this.name, this.id);
    }

    public void updateDash(String s, int cusID) {
        String[] words = s.split(" ");
        int index;
        for (String word : words) {
            if (!commonWords.contains(word)) {
                commonWords.add(word);
                commonWordsFrequency.add(1);
            } else {
                index = commonWords.indexOf(word);
                commonWordsFrequency.set(index, commonWordsFrequency.get(index) + 1);
            }
        }
        if (!users.contains(cusID)) {
            users.add(cusID);
            userFrequency.add(1);
        } else {
            index = users.indexOf(cusID);
            userFrequency.set(index, userFrequency.get(index) + 1);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
