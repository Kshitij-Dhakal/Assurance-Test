package com.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Assurance {
    public static void main(String[] args) throws ParseException {
        Assurance a = new Assurance();
    }

    /**
     * Find Asteroids that appeared between startDate and endDate. And among these asteroids find the most frequent in period between 1900 to 1999
     *
     * @param startDate start date in the form yyyy-MM-dd
     * @param endDate   end date in the form yyyy-MM-dd
     * @return name of the asteroid which appeared between startDate and endDate which appears most frequently between 1900 and 1999
     * @throws ParseException json parse exception
     */
    String hazardousAsteroids(String startDate, String endDate) throws ParseException {
        //fixme failed due to being unable to use org.json.* package
        int minFrequency = Integer.MIN_VALUE;
        String name = "";
        DateFormat f = new SimpleDateFormat("yyyy");
        final Date nineteenHundred = f.parse("1900");
        final Date ninetyNine = f.parse("1999");
        final String json = processGetRequest(startDate, endDate);
        final JSONObject jsonObject = new JSONObject(json);
        final JSONObject neo = jsonObject.getJSONObject("near_earth_objects");
        for (String s : neo.keySet()) {
            final JSONArray asteroids = neo.getJSONArray(s);
            for (Object obj : asteroids) {
                JSONObject asteroid = (JSONObject) obj;
                final String asteroidName = asteroid.getString("name");
                final JSONArray approachData = asteroid.getJSONArray("close_approach_data");
                int frequency = 0;
                for (Object datum : approachData) {
                    JSONObject dat = (JSONObject) datum;
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = format.parse(dat.getString("close_approach_date"));
                    if (date.after(nineteenHundred) && date.before(ninetyNine)) {
                        frequency++;
                    }
                }
                if (frequency > minFrequency) {
                    minFrequency = frequency;
                    name = asteroidName;
                }
            }
        }
        return name;
    }

    /**
     * Helper method for hazardousAsteroids()
     *
     * @param startDate start date in the form yyyy-MM-dd
     * @param endDate   end date in the form yyyy-MM-dd
     * @return json string retrieved from <a href="https://api.nasa.gov/">Nasa API</a>
     */
    String processGetRequest(String startDate, String endDate) {
        try {
            String key = "qjhhdDzP1PlpXh3GlaiKb8Zk1VEf1x3mY8wZhYC8";
            URL url = new URL("https://api.nasa.gov/neo/rest/v1/feed?start_date=" + startDate + "&end_date=" + endDate + "&api_key=" + key);//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                                                   + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            StringBuilder str = new StringBuilder();
            while ((output = br.readLine()) != null) {
                str.append(output);
            }
            conn.disconnect();
            return
                    str.toString();
        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }
        return "";
    }

    /**
     * Given a number k and array, find the number of pairs in array whose difference is equal to k.
     *
     *
     * <p>
     * Throws timeout if nested for loop is used.
     *
     * @param k number
     * @param a array of integers
     * @return n % 10<sup>9</sup>+7 where n is the number of pairs in a whose difference is equal to k.
     */
    int countPairsWithDifference(int k, int[] a) {
        //fixme PARTIAL failed 1 test case 290/300
        Map<Integer, Integer> aCount = new HashMap<>();
        for (int i : a) {
            if (aCount.containsKey(i)) {
                aCount.put(i, aCount.get(i) + 1);
            } else {
                aCount.put(i, 1);
            }
        }
        Map<Integer, Integer> sumMap = new HashMap<>();
        int n = 0;
        for (int i : a) {
            if (i == 404 || i == 443 || i == 369) {
                System.out.println(i);
            }
            int diff = i - k; //i - diff = l
            //[1, 6, 8, 2, 4, 9, 12]
            int temp = 0;
            if (!sumMap.containsKey(diff) && aCount.containsKey(diff)) {
                //if sumMap contains diff then given sum is already checked
                //if aCount doesnot contains diff then given diff doesnot exist in array so given sum is not possible with i
                temp += aCount.get(i) * aCount.get(diff);
                sumMap.put(i, temp);
            }
            diff = i + k; //diff - i = k
            //[1, 6, 8, 2, 4, 9, 12]
            if (!sumMap.containsKey(diff) && aCount.containsKey(diff)) {
                //if sumMap contains diff then given sum is already checked
                //if aCount doesnot contains diff then given diff doesnot exist in array so given sum is not possible with i
                temp += aCount.get(i) * aCount.get(diff);
                sumMap.put(i, temp);
            }
        }
        for (int v : sumMap.values()) {
            n += v;
        }
        return (int) (n % 10e+7);
    }

    /**
     * Same question as above but this solution gives timeout in some test cases.
     *
     * @param k number
     * @param a array of integers
     * @return n % 10<sup>9</sup>+7 where n is the number of pairs in a whose difference is equal to k.
     */
    int _countPairsWithDifference(int k, int[] a) {
        //failed several test cases due to timeout
        int n = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                int diff = absDiff(a[i], a[j]);
                if (diff == k) {
                    n++;
                    System.out.println(a[i] + " " + a[j]);
                }
            }
        }
        return (int) (n % 10e+7);
    }

    /**
     * Given state of accounts and list of requests. Process the requests and find the state of accounts after the transaction.
     * Request is in the form
     * <ul>
     *     <li>transfer a b amount</li>
     *     <li>withdraw a amount</li>
     *     <li>withdraw b amount</li>
     * </ul>
     * where a and b are 1-base index of accounts.
     * <p>
     * Requests are invalid if user is trying to withdraw or transfer amount more than they have in the accounts.
     * Request is also invalid if account doesn't exist
     *
     * @param accounts List of amount in account at given index
     * @param requests List of string in the form of given request
     * @return state of accounts or [-req_id] if request is invalid where req_id is the index of first invalid request
     */
    int[] bankRequests(int[] accounts, String[] requests) {
        //fixme PARTIAL failed 1 test case 290/300
        List<Integer> invalidRequests = new ArrayList<>();
        for (int i = 0; i < requests.length; i++) {
            String request = requests[i];
            final String[] tokens = request.split(" ");
            final String action = tokens[0];
            if (tokens.length < 3) {
                invalidRequests.add(-(i + 1));
                break;
            }
            final int a = Integer.parseInt(tokens[1]);
            if (a > accounts.length || a < 1/* account a doesn't exist */) {
                invalidRequests.add(-(i + 1));
            } else {
                if (action.equalsIgnoreCase("transfer")) {
                    if (tokens.length != 4) {
                        invalidRequests.add(-(i + 1));
                        break;
                    }
                    final int b = Integer.parseInt(tokens[2]);
                    if (b > accounts.length || b < 1 /* account b doesn't exist */) {
                        invalidRequests.add(-(i + 1));
                    } else {
                        final int amount = Integer.parseInt(tokens[3]);
                        if (amount > accounts[a - 1] /* trying to send money greater that in bank */) {
                            invalidRequests.add(-(i + 1));
                        } else {
                            //transfer money
                            int sumA = accounts[a - 1] - amount;
                            int sumB = accounts[b - 1] + amount;
                            accounts[a - 1] = sumA;
                            accounts[b - 1] = sumB;
                        }
                    }
                } else {
                    final int amount = Integer.parseInt(tokens[2]);
                    if (action.equalsIgnoreCase("withdraw")) {
                        if (amount > accounts[a - 1]/* trying to withdraw/deposit more than in bank */) {
                            invalidRequests.add(-(i + 1));
                        } else {
                            accounts[a - 1] = accounts[a - 1] - amount;
                        }
                    } else if (action.equalsIgnoreCase("deposit")) {
                        accounts[a - 1] = accounts[a - 1] + amount;
                    } else {
                        invalidRequests.add(-(i + 1));
                    }
                }

            }
        }
        if (invalidRequests.size() > 0) {
            return new int[]{invalidRequests.get(0)};
        } else {
            return accounts;
        }
    }

    /**
     * Find if given ListNode is palindrome on O(n) time using o(1) space.
     *
     * @param l head of ListNode
     * @return true if listNode is palindrome
     */
    // Singly-linked lists are already defined with this interface:
    // class ListNode<T> {
    //   ListNode(T x) {
    //     value = x;
    //   }
    //   T value;
    //   ListNode<T> next;
    // }
    //
    boolean isListPalindrome(ListNode<Integer> l) {
        ListNode<Integer> slow = l;
        boolean isPalindrome = true;
        Stack<Integer> stack = new Stack<>();

        while (slow != null) {
            stack.push(slow.value);
            slow = slow.next;
        }

        while (l != null) {

            int i = stack.pop();
            if (l.value == i) {
                isPalindrome = true;
            } else {
                isPalindrome = false;
                break;
            }
            l = l.next;
        }
        return isPalindrome;
    }

    /**
     * Geven prices[] where prices[i] is the price at i<sup>th</sup> day, find the maximum achievable profit.
     *
     * @param prices List of prices
     * @return maximum profit
     */
    int buyAndSellStock(int[] prices) {
        //fixme PARTIAL failed this method with few test cases
        // failed while using naive nested for loop method due to timeout
        List<Integer> priceList = new ArrayList<>();
        for (int price : prices) {
            priceList.add(price);
        }
        List<Integer> profitList = new ArrayList<>();
        Arrays.sort(prices);
        for (int i = 0, n = prices.length; i < n; i++) {
            int buyingIndex = priceList.indexOf(prices[i]);
            for (int j = n - 1; j > i; j--) {
                int sellingIndex = priceList.lastIndexOf(prices[j]);
                if (buyingIndex < sellingIndex) {
                    profitList.add(priceList.get(sellingIndex) - priceList.get(buyingIndex));
                    break;
                }
            }
        }
        profitList.sort(Integer::compareTo);
        if (profitList.size() > 0) {
            return profitList.get(profitList.size() - 1);
        } else {
            return 0;
        }
    }

    /**
     * Replace each character in given string with next character in the alphabet. Example Hello -> Ifmmp
     *
     * @param inputString input string
     * @return result after doing alphabetic shift
     */
    String alphabeticShift(String inputString) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == 'z') {
                str.append('a');
            } else if (inputString.charAt(i) == 'Z') {
                str.append('A');
            } else {
                str.append((char) (inputString.charAt(i) + 1));
            }
        }
        return str.toString();
    }

    /**
     * Given an array, find k such that sum of abs(a[0],k)+abs(a[1],k)+...+abs(a[n],k) is minimum.
     *
     * @param a list of numbers
     * @return k
     */
    int absoluteValuesSumMinimization(int[] a) {
        int ret = 0;
        int minSum = Integer.MAX_VALUE;
        for (int i : a) {
            int sum = 0;
            for (int j : a) {
                sum += absDiff(i, j);
            }
            if (sum < minSum) {
                minSum = sum;
                ret = i;
            }
        }
        return ret;
    }

    /**
     * Helper method for finding absolute difference between a and b
     *
     * @param a number
     * @param b number
     * @return |a-b|
     */
    int absDiff(int a, int b) {
        int diff = a - b;
        return (diff) < 0 ? -diff : diff;
    }
}


