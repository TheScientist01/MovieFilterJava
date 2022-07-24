import java.lang.reflect.*;
import java.io.*;
import java.lang.reflect.Array;
import java.security.Signature;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.zip.DataFormatException;

import javax.sound.sampled.SourceDataLine;
import javax.swing.ComponentInputMap;
import javax.swing.text.AbstractDocument.BranchElement;
import javax.swing.text.html.HTMLDocument.RunElement;
import javax.swing.tree.TreeCellRenderer;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import org.xml.sax.HandlerBase;

public class movieSort {
    
    static boolean dateValidator(String date) { // checks if entered date is valid or not
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); // required date type
        Date d = null;
        df.setLenient(false);
        try {
            d = df.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static String entityFinder(String entity) { // converts the user inputs to the index
        if (entity.equals("date") || entity.equals("1")) {
            return "1";

        } else if (entity.equals("title") || entity.equals("2")) {
            return "2";

        } else if (entity.equals("overview") || entity.equals("3")) {
            return "3";

        } else if (entity.equals("popularity") || entity.equals("4")) {
            return "4";
        } else if (entity.equals("vote count") || entity.equals("5")) {
            return "5";

        } else if (entity.equals("vote average") || entity.equals("6")) {
            return "6";

        } else if (entity.equals("language") || entity.equals("7")) {
            return "7";

        } else if (entity.equals("genre") || entity.equals("8")) {
            return "8";

        } else if (entity.equals("poster url") || entity.equals("9")) {
            return "9";

        } else {
            return null;
        }
    }

    static void listPrinter(List<String> list) { // prints lists
        Iterator<String> it = list.listIterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    static String[] genreNames() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("mymoviedb.csv"));
        String data[] = new String[9847];
        String genres[] = new String[100];
        String temp[] = new String[10];
        data = printSelected(sc, 8); // choose all the genres
        int i = 0;
        for (String genre : data) {
            if (genre.contains(",")) {
                temp = genre.substring(1, genre.length() - 1).split(", ");
            } else {
                temp = genre.split(", ");
            }
            
            for (String string : temp) {

                if (!Arrays.asList(genres).contains(string)) {
                    genres[i] = string;
                    i++;
                }
            }
        }

        sc.close();
        return genres;
    }

    static String[] colNames() throws FileNotFoundException { // return column names
        Scanner sc = new Scanner(new File("mymoviedb.csv"));
        String cols[] = sc.nextLine().split(",");
        sc.close();
        return cols;
    }

    static void sortDate(Scanner sc, List<String> list, int ascdesc) { // for sorting date

        if (ascdesc == 1) {
            Collections.sort(list, new Comparator<String>() {
                DateFormat f = new SimpleDateFormat("MM/dd/yyyy");

                @Override
                public int compare(String str1, String str2) {
                    if (null == str1) {
                        return null == str2 ? 0 : 1;
                    } else if (null == str2) {
                        return -1;
                    }
                    try {
                        return f.parse(str1).compareTo(f.parse(str2));
                    } catch (ParseException e) {
                        return -1;
                    }
                }
            });
        } else {
            Collections.sort(list, new Comparator<String>() {
                DateFormat f = new SimpleDateFormat("MM/dd/yyyy");

                @Override
                public int compare(String str2, String str1) {
                    if (null == str1) {
                        return null == str2 ? 0 : 1;
                    } else if (null == str2) {
                        return -1;
                    }
                    try {
                        return f.parse(str1).compareTo(f.parse(str2));
                    } catch (ParseException e) {
                        return -1;
                    }
                }
            });
        }
    }

    static void Sort(Scanner sc) {

        try {
            String data[] = new String[9847];
            String search;
            String input[];
            
            while (true) {
                System.out.println("Enter input like this :");
                System.out.println("title ASC or 2 ASC");
                System.out.println("Which field and how you want to sort: ");
                Scanner scan = new Scanner(System.in);
                input = scan.nextLine().split(" ");
                if(input.length!=2){
                    System.out.println("Wrong input!");
                    continue;
                }
                search = entityFinder(input[0]);
                try {
                    data = printSelected(sc, Integer.parseInt(entityFinder(search)));
                    break;
                } catch (NullPointerException e) {
                    System.out.println("Wrong input!");
                    continue;
                }
            }
            
            if (input[1].equals("ASC")) {

                try {

                    if (search.equals("1")) { // if the sort entity is date, we need to oparate special operation
                        List<String> list = new ArrayList<String>();
                        Collections.addAll(list, data);
                        sortDate(sc, list, 1);
                        listPrinter(list);
                    } else { // if it's not date
                        Arrays.sort(data);
                        arrayPrint(data);

                    }

                } catch (NullPointerException | NumberFormatException e) {
                    System.out.println("Wrong input!");
                }

            } else if (input[1].equals("DESC")) {

                try {

                    if (input[0].equals("1")) { // if the sort entity is date, we need to oparate special operation
                        List<String> list = new ArrayList<String>();
                        Collections.addAll(list, data);
                        sortDate(sc, list, 2);
                        listPrinter(list);
                    } else { // if it's not date
                        Arrays.sort(data, Collections.reverseOrder());
                        arrayPrint(data);
                    }

                } catch (NullPointerException | NumberFormatException e) {

                }
            }

            else {

                System.out.println("Wrong sort input!");
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    static String[] csvRead(Scanner sc) { // reads a line of csv and returns it
        String line[] = new String[9];
        if (sc.hasNextLine()) {
            line = sc.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        }

        return line;
    }

    static void csvWriter(String data[][], String csvName) throws FileNotFoundException { // writes filtered data into
                                                                                          // new csv file
        File csv = new File(csvName + ".csv");
        PrintWriter out = new PrintWriter(csv);
        out.println(String.join(",", colNames()));
        for (String string[] : data) {
            if (string[0] != null)
                out.println(String.join(",", string));
        }
        out.close();
    }

    static void arrayPrint(String array[]) { // array printer

        for (String string : array) {
            try {
                if (string == null || string == "zzzz") { // zzzz is just dummy text for handling the nulls
                    continue;
                }
                System.out.println(string);
            } catch (NullPointerException e) {
                continue;
            }
        }
    }

    // EXERCISE 1 A
    static void printAll(Scanner sc) {
        for (int i = 0; i < 100; i++) {
            for (String element : csvRead(sc)) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    static String[][] selectAll(Scanner sc) { // reads all the data from csv file
        String list[][] = new String[9847][9];
        String temp[] = new String[9];
        Double d;
        for (int i = 0; i < 9847 && sc.hasNext(); i++) {
            try { // this try-catch field checks if there is null field or not, if there is it
                  // continues with the next field
                temp = csvRead(sc);
                d = Double.parseDouble(temp[5]); // checks if one of the "number" field(I chose 5th vote average field)
                list[i] = temp;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }

        return list;
    }

    static String[] printSelected(Scanner sc, int index) { // this is operative function of next function, it prints the
                                                           // given index in movies
        String line[] = new String[9847];
        String data[][] = new String[9847][9];
        data = selectAll(sc);
        for (int i = 0; i < 9847; i++) {

            if (data[i][index - 1] != null)
                line[i] = data[i][index - 1];
            else {
                line[i] = "zzzz";
            } // for removing nulls
        }
        return line;

    }

    static String[] printSelected(Scanner sc) { // this function gets input from user, renders this and sends this to
                                                // the operative printSelected function
        while (true) { // this while is for the wrong inputs, if there is, user won't have to rerun the
                       // program
            System.out.println("The fields");
            System.out.println(
                    "1)date\n2)title\n3)overview\n4)popularity\n5)vote_count\n6)vote_average\n7)language\n8)genre\n9)post_url");
            System.out.println("You can either write the numbers or the names : ");
            System.out.println("For example: 1 or date");
            Scanner scan = new Scanner(System.in);
            String entity = scan.nextLine();
            if (entityFinder(entity) == null) {
                System.out.println("Wrong input " + entity);
                continue;
            }
            return printSelected(sc, Integer.parseInt(entityFinder(entity)));

        }

    }

    static void printRange(Scanner sc) { // prints the movies for the range which is entered by the user
        String line[];
        line = csvRead(sc);
        System.out.println(String.join(" ", line));
        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------");
        System.out.println("Enter the range seperated by space(e.g., 5 100 or 0 100)");
        int a, b; // ranges
        while (true) {
            Scanner scan = new Scanner(System.in);
            String input[] = scan.nextLine().split(" ");

            if (input.length == 2) { // if user entered the input like 5 100, length will be 2
                try { // if a and b is not it it will give an error and will wait for the true input
                    a = Integer.parseInt(input[0]);
                    b = Integer.parseInt(input[1]);
                    if (a > b) {
                        System.out.println(
                                "First range element should be greater than second range element!   " + a + ">" + b);
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input, enter only the numbers!");
                    continue;
                }
            } else { // if length is not 2
                System.out.println("Wrong input, enter 2 numbers seperated by space!");
                continue;
            }
        }
        for (int i = 0; i < b; i++) { // prints for the given range

            if (sc.hasNext()) {
                line = csvRead(sc);
                if (i >= a) {
                    arrayPrint(line);
                    System.out.println(
                            "-------------------------------------------------------------------------------------------------------------------");
                }

            } else {
                break;
            }
        }
    }

    // EXERCISE 3 A AND EXERCISE 5 A
    static boolean stringFields(String data[], String search, int field, int type) { // searchs for the given string
                                                                                     // field and key word
        switch (type) {

            case 1: // starts with

                try {
                    if (data[field].startsWith(search)) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {

                }
                break;
            case 2: // ends with

                try {
                    if (data[field].endsWith(search)) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {

                }
                break;
            case 3: // contains
                try {
                    if (data[field].contains(search)) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {

                }
                break;

            case 4: // exact equalty
                try {
                    if (data[field].equals(search)) {
                        return true;
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {

                }
                break;
            default:
                System.out.println("Wrong input!");
                return false;
        }

        return false;
    }

    // EXERCISE 3 B AND EXERCISE 5 C
    static boolean numFields(String data[], String search, int field, int type) { // searchs for the given number field
                                                                                  // and key word
        String list[] = new String[1];
        try {
            if (data.length == 9) {
                switch (type) {
                    case 1:// equal
                        if (data[field].equals(search)) {
                            return true;
                        }
                        break;
                    case 2: // greater than
                        if (Double.parseDouble(data[field]) > Double.parseDouble(search)) {
                            list[0] = String.join(",", data);
                            return true;
                        }
                        break;
                    case 3: // less than
                        if (Double.parseDouble(data[field]) < Double.parseDouble(search)) {
                            list[0] = String.join(",", data);
                            return true;
                        }
                        break;
                    case 4: // greater and equal
                        if (Double.parseDouble(data[field]) <= Double.parseDouble(search)) {
                            list[0] = String.join(",", data);
                            return true;
                        }
                        break;

                    case 5: // less and equal
                        if (Double.parseDouble(data[field]) >= Double.parseDouble(search)) {
                            list[0] = String.join(",", data);
                            return true;
                        }
                        break;
                    case 6: // between
                        if (Double.parseDouble(data[field]) > Double.parseDouble(search)
                                && Double.parseDouble(data[field]) < Double.parseDouble(search)) {
                            list[0] = String.join(",", data);
                            return true;
                        }
                        break;
                    default:
                        System.out.println("Wrong input!");
                        break;
                }

            }
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return false;
    }

    // EXERCISE 5 D
    static boolean dateFields1(String data[], String search, int type) {
        Calendar c=Calendar.getInstance();
        Calendar sear=Calendar.getInstance();
        try {
            
        String inp[]=search.split("/");
        c.set(Integer.parseInt(inp[2]),Integer.parseInt(inp[0]),Integer.parseInt(inp[1]));
        String dat[]=data[0].split("/");
        sear.set(Integer.parseInt(dat[2]),Integer.parseInt(dat[0]),Integer.parseInt(dat[1]));
        } catch (NullPointerException n) {
            return false;
        }
        switch (type) {
            case 1: // specific year
                try {
                    if(data[0].split("/")[2].equals(search)){
                        
                        return true;
                    }
                    return false;
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
          
            case 2: // specific month
            
                if(c.get(Calendar.MONTH)==sear.get(Calendar.MONTH)){
                    
                return true;
                }
                return false;
            
            case 3: // specific day
            if(c.get(Calendar.DAY_OF_MONTH)==sear.get(Calendar.DAY_OF_MONTH)){
                    
                return true;
                }
                return false;
            default:
                break;
        }
        return false;



        
    }

    static boolean dateFields(String data[], String search, int type) {
        Calendar c=Calendar.getInstance();
        Calendar sear=Calendar.getInstance();
        try {
            
        String inp[]=search.split("/");
        c.set(Integer.parseInt(inp[2]),Integer.parseInt(inp[0]),Integer.parseInt(inp[1]));
        String dat[]=data[0].split("/");
        sear.set(Integer.parseInt(dat[2]),Integer.parseInt(dat[0]),Integer.parseInt(dat[1]));
        } catch (NullPointerException n) {
            return false;
        }
        try {
            if (data.length == 9 && dateValidator(search)) {
                switch (type) {
                    case 1:// equal
                        if (c.equals(sear)) {
                            return true;
                        }
                        break;
                    case 2: // greater than
                        if (c.compareTo(sear) < 0) {
                            return true;
                        }
                        break;
                    case 3: // less than
                        if (c.compareTo(sear) > 0) {
                            return true;
                        }
                        break;
                    case 4: // greater and equal
                        if (c.compareTo(sear) == 0 || c.compareTo(sear) < 0) {
                            return true;
                        }
                        break;

                    case 5: // less and equal
                        if (c.compareTo(sear) == 0 || c.compareTo(sear) > 0) {
                            
                            return true;
                        }
                        break;

                    default:
                        System.out.println("Wrong input!");
                        break;
                }

            }
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return false;
    }

    // EXERCISE 5 E
    static boolean genreField(String data[], String search) {
        String genres = data[7];

        if (genres.contains(",")) {
            if (genres.contains(search)) {
                return true;
            }
        } else {
            if (genres.equals(search)) {
                return true;
            }
        }
        return false;
    }

    // EXERCISE 3
    static void searcher(Scanner sc) throws FileNotFoundException {

        while (true) {
            String name="";
            System.out.println("What whould you like to search : ");
            System.out.println("a)String fields");
            System.out.println("b)Non-string fields");
            System.out.println("c)genre fields");
            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine();
            if (input.equals("a")) {
                name+="a ";
                System.out.println("1)title");
                System.out.println("2)overview");
                System.out.println("Enter the field(e.g., 1 or title)");
                String check[] = { "1", "2", "title", "overview" }; // checks if user entered right input
                scan = new Scanner(System.in);
                input = scan.nextLine();
                if (Arrays.asList(check).contains(input)) {
                    System.out.println("Enter the string you want to search ");
                    scan = new Scanner(System.in);
                    String string = scan.nextLine();
                    if(input.equals("1") || input.equals("title")){
                        input="1";
                    }
                    else if (input.equals("2") || input.equals("overview")){
                        input="2";
                    }
                    name+=(" "+entityFinder(input));
                    name+=(" "+string);
                    try {
                        // checks if there is previous searches for this filter, if there is, it doesn't
                        // do the same operations and takes the same previous datas from the already
                        // created csv file
                        Scanner csv = new Scanner(new File(String.join("_", name.split("/")) + ".csv"));
                        String data2[][] = selectAll(csv);
                        try {
                            for (String[] strings : data2) {
                                arrayPrint(strings);
                            }
            
                        } catch (NullPointerException e) {
            
                        }
            
                        System.out.println("Printed from previous filter");
                    } catch (FileNotFoundException f) { 
                        String data[][]=new String[9847][9];
                        int counter=0;
                        for (String[] strings : selectAll(sc)) {
                            if (stringFields(strings, string, Integer.parseInt(entityFinder(input)), 3)) {
                                arrayPrint(strings);
                                data[counter]=strings;
                                counter++;
                            }
                        }
                        csvWriter(data,  String.join("_", name.split("/")));
                        return;
                    }
                } else {
                    System.out.println("Wrong input!");
                }
            } else if (input.equals("b")) {
                name+="b ";
                System.out.println("1)date");
                System.out.println("2)popularity");
                System.out.println("3)vote count");
                System.out.println("4)vote average");
                System.out.println("Enter the field(e.g., 1 or date)");
                String check[] = { "1", "2", "3", "4", "date", "vote count", "vote average" }; // checks if user entered
                                                                                               // right input
                scan = new Scanner(System.in);
                input = scan.nextLine();
                if(input.equals("1") || input.equals("date")){
                    input="-1";
                }
                else if (input.equals("2") || input.equals("popularity")){
                    input="2";
                }
                else if(input.equals("3") || input.equals("vote count")){
                    input="3";
                }
                else if(input.equals("3") || input.equals("vote average")){
                    input="4";
                }
                
                if (!Arrays.asList(check).contains(input)) {
                    System.out.println("Wrong input!");
                } else {
                    System.out.println("Enter the number you want to search ");
                    scan = new Scanner(System.in);
                    String num = scan.nextLine();
                    name+=(" "+entityFinder(input));
                    name+=(" "+num);
                    try {
                        // checks if there is previous searches for this filter, if there is, it doesn't
                        // do the same operations and takes the same previous datas from the already
                        // created csv file
                        Scanner csv = new Scanner(new File(String.join("_", name.split("/")) + ".csv"));
                        String data2[][] = selectAll(csv);
                        try {
                            for (String[] strings : data2) {
                                arrayPrint(strings);
                            }
            
                        } catch (NullPointerException e) {
            
                        }
            
                        System.out.println("Printed from previous filter");
                    } catch (FileNotFoundException f) { 
                        String data[][]=new String[9847][9];
                        int counter=0;
                        for (String[] strings : selectAll(sc)) {
                            
                            if (numFields(strings, num, Integer.parseInt(input)+1, 1)) {
                                arrayPrint(strings);
                                data[counter]=strings;
                                counter++;
                            }
                        }
                        csvWriter(data,  String.join("_", name.split("/")));
                    return;
                }
                }
            } else if (input.equals("c")) {
                name+="c";
                String genres[] = new String[20];
                genres = genreNames();
                arrayPrint(genres);
                System.out.println("Choose one of these genres");
                scan = new Scanner(System.in);
                input = scan.nextLine();
                if (!Arrays.asList(genres).contains(input)) {
                    System.out.println("Wrong genre input!");
                    continue;
                } else {
                    name+=(" "+input);
                    try {
                        // checks if there is previous searches for this filter, if there is, it doesn't
                        // do the same operations and takes the same previous datas from the already
                        // created csv file
                        Scanner csv = new Scanner(new File(String.join("_", name.split("/")) + ".csv"));
                        try {
                            for (String[] strings : selectAll(sc)) {
                                arrayPrint(strings);
                            }
            
                        } catch (NullPointerException e) {
            
                        }
            
                        System.out.println("Printed from previous filter");
                    } catch (FileNotFoundException f) { 
                        String data[][]=new String[9847][9];
                        int counter=0;
                        for (String[] strings : selectAll(sc)) {
                            try {
                                if (genreField(strings, input)) {
                                    arrayPrint(strings);
                                    data[counter]=strings;
                                    counter++;
                                }

                            } catch (NullPointerException e) {
                                continue;
                            }

                        }
                        csvWriter(data,  String.join("_", name.split("/")));
                        return;
                    }
                }
            } else {
                System.out.println("Wrong input!");
                continue;
            }
        }
    }

    // EXERCISE 5 FILTER
    // This function takes filter input from the user, renders it, and sends it to
    // the "searcher" function which gives us the result of the filter
    static String[][] filterReader(Scanner sc) throws FileNotFoundException {
        int theCount=0;
        List<String> keyWords = new ArrayList<String>(
                Arrays.asList("movies", "released", "of", "rating", "language", "votes", "with", "which", "has")); // for
                                                                                                                   // getting
                                                                                                                   // what
                                                                                                                   // user
                                                                                                                   // want
                                                                                                                   // to
                                                                                                                   // search
                                                                                                                   // and
                                                                                                                   // divide
                                                                                                                   // the
                                                                                                                   // searcher
                                                                                                                   // for
                                                                                                                   // example
                                                                                                                   // vote
                                                                                                                   // count
                                                                                                                   // starts
                                                                                                                   // with
                                                                                                                   // "with"
                                                                                                                   // so
                                                                                                                   // by
                                                                                                                   // using
                                                                                                                   // this
                                                                                                                   // we
                                                                                                                   // can
                                                                                                                   // seperate
                                                                                                                   // the
                                                                                                                   // searches

        String base[] = { "select", "all" }; // the first 2 words
        System.out.println("Your filter should be like this : ");
        
        System.out.println("i. select all the movies released in 2000 which has rating greater than 5.0");
        System.out.println("ii. select all the movies of Animation or Science Fiction released since 1/1/2020 (i.e., between 1/1/2020 and today)");
        System.out.println("iii. select all the movies with the original language English or French");
        System.out.println("iv. select all the Comedy movies with more than 10000 votes");
        System.out.println("DONT PUT . AT THE END OF THE FILTER!");
        
        System.out.println("Enter your filter :");
        Scanner scan = new Scanner(System.in);
        String filter = scan.nextLine(); // it is important, because for creating the new csv file or looking for the
                                         // already created csv file we need this search data
        List<String> string = new ArrayList<String>(Arrays.asList(filter.split(" ")));
        ListIterator<String> it = string.listIterator();
        Search s = new Search(); // will store all the filtered search datas here
        try {
            int i = 0;
            String temp;
            if (string.size() < 2) {
                System.out.println("Wrong input, should be longer!");
                return null;
            }
            while (it.hasNext()) {
                temp = it.next();
                

                if (i < 2) {
                    if (temp.equals(base[i])) {
                        i++;
                        continue;
                    } else {
                        System.out.println("Wrong input!");
                        return null;
                    }
                }

                else if (temp.equals("the") && it.hasNext()) { // genre
                    if(theCount!=0){
                        
                        continue;
                    }
                    else{
                    theCount++;
                    temp = it.next();

                    if (temp.equals("movies")) {
                        continue;
                    } else {
                        List<String> genre = new ArrayList<String>();
                        ListIterator<String> itGenre = genre.listIterator();
                        while (it.hasNext() && !temp.equals("movies")) {
                            itGenre.add(temp);
                            temp = it.next();
                        }
                        s.setGenre(genre);
                    }
                }
                }

                else if (temp.equals("released")) { // date
                    temp = it.next();
                    
                    List<String> release = new ArrayList<String>();
                    if (temp.equals("in")) {
                        try {
                            System.out.println("in");
                            if(it.hasNext()){
                                temp = it.next();
                            }
                            else{
                                System.out.println("Should be longer!");
                                return null;
                            }
                            if (dateValidator(temp)) {
                                release.add(temp);
                            } else {
                                int v = Integer.parseInt(temp);
                                release.add(temp);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Wrong release date input !");
                            return null;
                        }

                    } else if (temp.equals("since")) {

                        release.add("~");
                        try {
                            temp = it.next();
                            if (dateValidator(temp)) {
                                release.add(temp);
                            } else {
                                int v = Integer.parseInt(temp);
                                release.add(temp);
                            }
                        } catch (NumberFormatException e) {
                            
                                System.out.println("Wrong date input!1");
                                return null;
                        }
                    } else if (temp.equals("between")) {

                        for (int j = 0; j < 3; j++) {
                            try {
                                temp = it.next();
                                if (dateValidator(temp)) {
                                    release.add(temp);
                                }
                                else if(temp.equals("and")){
                                    release.add(temp);
                                }
                                else {
                                    int v = Integer.parseInt(temp);
                                    release.add(temp);
                                }
                            } catch (NumberFormatException e) {
                                
                                    System.out.println("Wrong date input!2");
                                    return null;
                            }
                        }
                    } else {
                        return null;
                    }
                    s.setDate(release);
                }

                else if (temp.equals("of")) {
                    List<String> genre = new ArrayList<String>();
                    ListIterator<String> itGenre = genre.listIterator();
                    int counter = 0;
                    while (it.hasNext()) {

                        temp = it.next();

                        if (!keyWords.toString().contains(temp) && counter != 3) {
                            counter++;
                            itGenre.add(temp);
                            i++;

                        } else {
                            break;
                        }
                    }

                    s.setGenre(genre);

                } else if (temp.equals("rating")) {
                    
                    List<String> rate = new ArrayList<String>();
                    ListIterator<String> itRate = rate.listIterator();
                    int checker = 0; // for checking if user used "than" word
                    int checker1 = 0; // for checking if there is more than 1 "more" or "less"
                    temp = it.next();
                    while (!keyWords.contains(temp)) {
                        if (temp.equals("greater")) {
                            if (!itRate.hasNext()) {
                                itRate.add(">");
                                checker1++;
                            } else {
                                System.out.println("Wrong rating input!1");
                                return null;
                            }
                        } else if (temp.equals("less")) {
                            if (!itRate.hasNext()) {
                                itRate.add("<");
                                checker1++;
                            } else {
                                System.out.println("Wrong rating input!2");
                                return null;
                            }
                        } else if (temp.equals("than")) {
                            checker++;
                        } else {

                            try {
                                Double.parseDouble(temp);
                                itRate.add(temp);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Wrong rating input3");
                                return null;
                            }
                        }
                        temp = it.next();
                    }

                    if (checker1 == 1) {
                        if (checker == 1) {
                            s.setVote(rate);
                        } else {
                            System.out.println("Wrong vote input!1" + checker);
                            return null;
                        }
                    } else if (checker1 > 1) {
                        System.out.println("Wrong vote input!2" + checker);
                        return null;
                    } else {
                        s.setVote(rate);
                    }
                } else if (temp.equals("language")) {
                    
                    List<String> lang = new ArrayList<String>();
                    ListIterator<String> itLang = lang.listIterator();
                    temp = it.next();
                    while (!keyWords.contains(temp)) {
                        
                        itLang.add(temp);
                        if (it.hasNext()) {
                            temp = it.next();
                        } else {
                            break;
                        }
                    }
                    s.setLanguage(lang);
                }

                else if (temp.equals("votes")) {
                    int counter = 0; // counts how many previous() function called and then will compansate it by
                                     // calling next() function at same count
                    int checker = 0; // for checking if user used "than" word
                    int checker1 = 0; // for checking if there is more than 1 "more" or "less"
                    List<String> vote = new ArrayList<String>();
                    ListIterator<String> itVote = vote.listIterator();
                    temp = it.previous();
                    while (!keyWords.contains(temp = it.previous())) {
                        if (temp.equals("more")) {
                            if (!itVote.hasNext()) {
                                itVote.add(">");
                                checker1++;
                            } else {
                                System.out.println("Wrong vote input!");
                                return null;
                            }
                        } else if (temp.equals("less")) {
                            if (!itVote.hasNext()) {
                                itVote.add("<");
                                checker1++;
                            } else {
                                System.out.println("Wrong vote input!");
                                return null;
                            }
                        } else if (temp.equals("than")) {
                            checker++;
                        } else {

                            try {
                                Double.parseDouble(temp);
                                itVote.add(temp);

                            } catch (NumberFormatException e) {
                                System.out.println("Wrong vote input");
                                return null;
                            }
                        }
                        counter++;
                    }

                    for (int j = 0; j < counter + 2; j++) { // compansating the previous() function calls
                        it.next();
                    }

                    if (checker1 == 1) {
                        if (checker == 1) {
                            s.setVoteCount(vote);
                        } else {
                            System.out.println("Wrong vote input!" + checker);
                            return null;
                        }
                    } else if (checker1 > 1) {
                        System.out.println("Wrong vote input!" + checker);
                        return null;
                    } else {
                        s.setVoteCount(vote);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {

        }
        String data[][] = new String[9847][9];
        data = filter(s, sc, filter);
        
        return data;
    }

    // filters csv file for the given filter input
    // writes it into a new csv file
    static String[][] filter(Search s, Scanner sc, String filter) throws FileNotFoundException {

        String data[][] = new String[9847][9];
        
        try {
            // checks if there is previous searches for this filter, if there is, it doesn't
            // do the same operations and takes the same previous datas from the already
            // created csv file
            Scanner csv = new Scanner(new File(String.join("_", filter.split("/")) + ".csv"));
            String data2[][] = selectAll(csv);
            return data2;

            
        } catch (FileNotFoundException f) { // if csv file doesn't exist, it will run the function searcher's main part

            data = selectAll(sc);

            try { // genre filter
                List<String> genre = new ArrayList<String>();
                genre = s.getGenre();

                String temp[][] = new String[9847][9];
                if (genre.size() == 3) {
                    if (!genre.get(1).equals("or")) {
                        System.out.println("Wrong genre input!1");
                        return null;
                    }
                    int counter = 0;
                    for (String[] strings : data) {
                        if (genre.get(0) != null && stringFields(strings, genre.get(0), 7, 3)) {

                            temp[counter] = strings;
                            counter++;
                        }
                    }
                    for (String[] strings : data) {
                        if (stringFields(strings, genre.get(2), 7, 3)) {
                            temp[counter] = strings;
                            counter++;
                        }
                    }
                    data = temp;

                } else if (genre.size() == 1) {
                    int counter = 0;
                    for (String[] strings : data) {
                        if (strings != null && stringFields(strings, genre.get(0), 7, 3)) {
                            temp[counter] = strings;
                            counter++;
                        }
                    }
                    data = temp;
                } else {
                    System.out.println("Wrong genre input!!");
                    listPrinter(genre);
                    return null;
                }

            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            }

            try { //date
                List<String> date = new ArrayList<String>();
                date = s.getDate();
                listPrinter(date);
                int counter = 0;
                String temp[][] = new String[9847][9];
                
                if (date.size() == 1) {
                    if(date.get(0).contains("/")){ //if it is full date
                        
                        for (String strings[] : data) {
                            if (dateFields1(strings, date.get(0), 4)) {
                                if (strings != null) {
                                    temp[counter] = strings;
                                    counter++;
                                }
                            }
                        }
                        data = temp;
                        
                        
                    }
                    else{ //if it is just year, like, 2005
                        for (String strings[] : data) {
                            if (dateFields(strings, "1/1/"+date.get(0), 4)) {
                                if (strings != null) {
                                    temp[counter] = strings;
                                    counter++;
                                }
                            }
                        }
                        data = temp;
                        String temp1[][] = new String[9847][9];
                        counter = 0;
                        
                        int y=(Integer.parseInt(date.get(0))+1); //for getting 1 year later
                        String year=Integer.toString(y);
                        for (String strings[] : data) {
                            if (dateFields(strings, "1/1/"+year, 5)) {
                                if (strings != null) {
                                    temp1[counter] = strings;
                                    counter++;
                                }
                            }
                        }
                        data = temp1;
                    }

                    }
                    
                 else if (date.size() == 2) {
                    if (date.get(0).equals("~")) {
                        if(date.get(0).contains("/")){
                        
                            for (String[] strings : data) {
                                if (dateFields(strings, date.get(1), 2)) {
                                    if (strings != null) {
                                        temp[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp; 
                        }
                        else{
                            for (String[] strings : data) {
                                if (dateFields(strings, "1/1/"+date.get(1), 2)) {
                                    
                                    if (strings != null) {
                                        temp[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp;
                            
                        }
                    } else {
                        System.out.println("Wrong date input!3");
                        return null;
                    }
                } else if (date.size() == 3) {
                    if (date.get(1).equals("and")) {
                        
                        if(date.get(0).contains("/")){
                        
                            for (String strings[] : data) {
                                if (dateFields(strings, date.get(0), 4)) {
                                    if (strings != null) {
                                        temp[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp;
                            String temp1[][] = new String[9847][9];
                            counter = 0;
                            for (String strings[] : data) {
                                if (dateFields(strings, date.get(2), 5)) {
                                    if (strings != null) {
                                        temp1[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp1;
                            
                        }
                        else{
                            for (String strings[] : data) {
                                if (dateFields(strings, "1/1/"+date.get(0), 4)) {
                                    if (strings != null) {
                                        temp[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp;
                            String temp1[][] = new String[9847][9];
                            counter = 0;
                            for (String strings[] : data) {
                                if (dateFields(strings, "1/1/"+date.get(2), 5)) {
                                    if (strings != null) {
                                        temp1[counter] = strings;
                                        counter++;
                                    }
                                }
                            }
                            data = temp1;
                        }
                        //
                        
                    } else {
                        System.out.println("Wrong date input!");
                        return null;
                    }
                }

            } catch (ArrayIndexOutOfBoundsException | NullPointerException  e) {
            }

            try { // language filter
                List<String> lang = new ArrayList<String>();
                lang = s.getLanguage();
                listPrinter(lang);

                String temp[][] = new String[9847][9];
                if (lang.size() == 3) {
                    if (!lang.get(1).equals("or")) {
                        System.out.println("Wrong language input!1");
                        return null;
                    }
                    int counter = 0;
                    for (String[] strings : data) {

                        if (lang.get(0) != null && stringFields(strings, lang.get(0).substring(0, 2).toLowerCase(), 6, 3)) {

                            
                            temp[counter] = strings;
                            counter++;
                        }
                    }
                    for (String[] strings : data) {
                        if (lang.get(2) != null && stringFields(strings, lang.get(2).substring(0, 2).toLowerCase(), 6, 3)) {
                            temp[counter] = strings;
                            counter++;
                        }
                    }
                    data = temp;

                } else if (lang.size() == 1) {
                    System.out.println(1);
                    int counter = 0;
                    for (String[] strings : data) {
                        if (lang.get(0) != null && stringFields(strings, lang.get(0).substring(0, 2), 6, 3)) {
                            temp[counter] = strings;
                            counter++;
                        }
                        data = temp;
                    }

                } else {
                    System.out.println("Wrong genre input!");
                    listPrinter(lang);
                    return null;
                }

            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            }

            try { // rating filter
                List<String> rate = new ArrayList<String>();
                rate = s.getVote();
                listPrinter(rate);
                if (rate.size() == 1) {
                    String temp[][] = new String[9847][9];
                    int index = 0;
                    int counter = 0;
                    for (String[] strings : data) {
                        if (numFields(strings, rate.get(0), 5, 1)) {
                            counter++;
                            temp[index] = strings;
                            index++;
                        }
                    }
                    System.out.println(counter);
                    data = temp;
                } else if (rate.size() == 2) {

                    String temp[][] = new String[9847][9];
                    int counter = 0, index = 0;
                    if (rate.get(0).equals(">")) {
                        for (String[] strings : data) {

                            if (numFields(strings, rate.get(1), 5, 2)) {

                                counter++;
                                temp[index] = strings;
                                index++;
                            }
                        }
                    } else if (rate.get(0).equals("<")) {
                        for (String[] strings : data) {
                            if (numFields(strings, rate.get(1), 5, 3)) {

                                temp[index] = strings;
                                index++;
                            }
                        }
                    } else {
                        System.out.println("Wrong rating input!5");
                        return null;
                    }

                    System.out.println(counter);
                    data = temp;

                } else if (rate.size() == 3) { // between
                    if (rate.get(1).equals("and")) {
                        String temp[][] = new String[9847][9];
                        int index = 0;
                        for (String[] strings : data) {
                            if (numFields(strings, rate.get(0), 5, 2)) {
                                temp[index] = strings;
                                index++;
                            }
                        }
                        data = temp;
                        String temp2[][] = new String[9847][9];
                        index = 0;
                        for (String[] strings : data) {
                            if (numFields(strings, rate.get(2), 5, 3)) {
                                temp2[index] = strings;
                                index++;
                            }
                        }
                        data = temp2;
                    }
                } else {
                    System.out.println("Wrong rate input!");
                    return null;
                }

            } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {

            }

            try { // vote count filter
                List<String> vote = new ArrayList<String>();
                vote = s.getVoteCount();

                if (vote.size() == 1) {
                    String temp[][] = new String[9847][9];
                    int index = 0;
                    int counter = 0;
                    for (String[] strings : data) {
                        if (numFields(strings, vote.get(0), 4, 1)) {
                            counter++;
                            temp[index] = strings;
                            index++;
                        }
                    }
                    System.out.println(counter);
                    data = temp;
                } else if (vote.size() == 2) {

                    String temp[][] = new String[9847][9];
                    int counter = 0, index = 0;
            
                    if (vote.get(1).equals(">")) {
                        for (String[] strings : data) {

                            if (numFields(strings, vote.get(0), 4, 2)) {

                                temp[index] = strings;
                                index++;
                            }
                        }
                    } else if (vote.get(1).equals("<")) {
                        for (String[] strings : data) {
                            if (numFields(strings, vote.get(0), 4, 3)) {
                                counter++;
                                temp[index] = strings;
                                index++;
                            }
                        }
                    } else {
                        System.out.println("Wrong vote input!");
                        return null;
                    }

                    System.out.println(counter);
                    data = temp;

                } else if (vote.size() == 3) { // between
                    if (vote.get(1).equals("and")) {
                        String temp[][] = new String[9847][9];
                        int index = 0;
                        for (String[] strings : data) {
                            if (numFields(strings, vote.get(0), 5, 2)) {
                                temp[index] = strings;
                                index++;
                            }
                        }
                        data = temp;
                        String temp2[][] = new String[9847][9];
                        index = 0;
                        for (String[] strings : data) {
                            if (numFields(strings, vote.get(2), 5, 3)) {
                                temp2[index] = strings;
                                index++;
                            }
                        }
                        data = temp2;
                    }
                } else {
                    System.out.println("Wrong vote input!" + vote.size());
                    return null;
                }

            } catch (NullPointerException e) {

            }

            csvWriter(data, String.join("_", filter.split("/"))); // writes the filtered data into the new csv file,
                                                                  // named with user's seach
        }
        return data;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Reflection API stuff :"); 
        Class s=Class.forName("Search");
        System.out.println("Class name - "+s.getName());
        System.out.println("Is interface - "+s.isInterface());
        while (true) {
            Scanner csv = new Scanner(new File("mymoviedb.csv"));
            System.out.println("Please enter you request number : ");
            System.out.println("1)List");
            System.out.println("2)Sort");
            System.out.println("3)Search");
            System.out.println("4)List column names");
            System.out.println("5)Filter");
            System.out.println("Enter x for exit");
            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine();

            switch (input) {
                case "1":
                    System.out.println("a)List all the fields of each entity(100 rows)");
                    System.out.println("b)List only the selected fields of each entity(100 rows)");
                    System.out.println("c)List entities based on the range of rows");
                    System.out.println("Enter x for exit");
                    scan = new Scanner(System.in);
                    input = scan.nextLine();
                    switch (input) {
                        case "a":
                            String data[][]=selectAll(csv);
                            int counter=0;
                            for (String[] strings : data) {
                                try {
                                    if(strings!=null && counter<100){
                                        arrayPrint(strings);
                                        counter++;
                                    }
                                    
                                } catch (NullPointerException e) {
                                    continue;
                                }
                            }
                            break;
                        case "b":
                            String data1[]=printSelected(csv);
                            int counter1=0;
                            for (String strings : data1) {
                                try {
                                    if(strings!=null && counter1<100){
                                        System.out.println(strings);
                                    counter1++;
                                }
                                
                            } catch (NullPointerException e) {
                                continue;
                            }
                        }
                            break;
                        case "c":
                            printRange(csv);
                            break;
                        case "x":
                            return;
                        default:
                            System.out.println("Wrong input!");
                            break;
                    }

                    break;
                case "2":
                    Sort(csv);
                    break;
                case "3":
                    searcher(csv);
                    break;
                case "4":
                    arrayPrint(colNames());
                    break;
                case "5":
                    String data[][] = filterReader(csv);
                        try {
                            for (String[] strings : data) {
                                arrayPrint(strings);
                            }

                        } catch (NullPointerException e) {

                        }
                    break;
                case "x":
                    return;
                default:
                    System.out.println("Wrong input!");
                    break;
            }

        }

    }

}