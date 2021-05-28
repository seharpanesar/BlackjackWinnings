/*import java.util.ArrayList;
import java.util.Random;

public class scratch {
    private static ArrayList<Card> deck = initDeck();
    private static int totalCash = 1000;
    private static int bet;

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            bet = totalCash/10;
            totalCash -= bet;

            //creating and filling player hand
            ArrayList<Card> playerHand = new ArrayList<>();
            playerHand.add(new Card("6"));
            playerHand.add(new Card("5"));

            //creating and filling dealer hand
            ArrayList<Card> dealerHand = new ArrayList<>();
            dealerHand.add(new Card("T"));
            dealerHand.add(new Card("4"));

            playRound(playerHand, dealerHand);
        }
    }

    private static ArrayList<Card> initDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        // 8 decks, 4 suits = 32 repetitions of 13 cards
        for (int i = 0; i < 32; i++) {
            deck.add(new Card("A"));
            deck.add(new Card("2"));
            deck.add(new Card("3"));
            deck.add(new Card("4"));
            deck.add(new Card("5"));
            deck.add(new Card("6"));
            deck.add(new Card("7"));
            deck.add(new Card("8"));
            deck.add(new Card("9"));
            deck.add(new Card("T")); //10
            deck.add(new Card("T")); //Jack
            deck.add(new Card("T")); //Queen
            deck.add(new Card("T")); //King
        }
        return deck;
    }

    private static Card getCard() {
        Random random = new Random();

        int size = deck.size();
        int randIndex = random.nextInt(size);
        Card pulledOut = deck.get(randIndex);
        deck.remove(randIndex);

        if (deck.size() == 0) {
            deck = initDeck();
        }
        return pulledOut;

    }

    private static void playRound(ArrayList<Card> playerHand, ArrayList<Card> dealerHand) {
        if (isBlackJack(playerHand)) {
            if (!isBlackJack(dealerHand)) {
                totalCash += (int) (bet + 1.5*bet);
                return;
            }
            else {
                totalCash += bet;
                return;
            }
        }

        if (isBlackJack(dealerHand) && !isBlackJack(playerHand)) {
            return;
        }

        String nextMove;
        do {
            if (getNumericalValue(playerHand) > 21) { //BUST
                System.out.println("bust");
                break;
            }
            nextMove = determineNextMove(playerHand, dealerHand);
            if (nextMove.equals("stay")) {
                System.out.println("stay");
            }
            switch (nextMove) {
                case "hit" -> playerHand = hit(playerHand);
                case "double" -> playerHand = doubleDown(playerHand);
            }
        } while(nextMove.equals("hit"));
    }

    private static ArrayList<Card> hit(ArrayList<Card> playerHand) {
        System.out.println("Hit");
        playerHand.add(getCard());
        System.out.print("Player hand ");
        for (Card card : playerHand) {
            System.out.printf("%s ", card.getValue());
        }
        System.out.println();
        return playerHand;
    }

    private static ArrayList<Card> doubleDown(ArrayList<Card> playerHand) {
        System.out.println("Double");
        playerHand.add(getCard());
        bet *= 2;

        System.out.print("Player hand ");
        for (Card card : playerHand) {
            System.out.printf("%s ", card.getValue());
        }
        System.out.println();

        return playerHand;
    }

    private static boolean isBlackJack(ArrayList<Card> hand) {
        String value = hand.get(0).getValue() + hand.get(1).getValue();
        if (hand.size() == 2) {
            if (value.equals("AT") || value.equals("TA")) {
                return true;
            }
        }
        return false;
    }

    private static String determineNextMove(ArrayList<Card> player, ArrayList<Card> dealer) {
        boolean totalIsHard = isTotalHard(player);
        Card dealerCard = dealer.get(0);

        if (totalIsHard) {
            int playerTotal = getNumericalValue(player);
            if (playerTotal < 9) {
                return "hit";
            }
            if (playerTotal > 16) {
                return "stay";
            }
            String uniqueCode = Integer.toString(playerTotal) + dealerCard.getValue();
            switch (uniqueCode) {
                case "162","163","164","165","166",
                        "152","153","154","155","156",
                        "142","143","144","145","146",
                        "132","133","134","135","136",
                        "124", "125", "126":
                    return "stay";
                case "112","113","114","115","116","117","118","119","11T","11A",
                        "102","103","104","105","106","107","108","109",
                        "93","94","95","96":
                    if (player.size() == 2) {
                        return "double";
                    }
                    else {
                        return "hit";
                    }
                default:
                    return "hit";
            }
        }
        else {
            int nonAceTotal = 0; //counts value of hand minus THE ACES
            for (Card card : player) { // assigns value to nonAceTotal
                if (!card.getValue().equals("A")) {
                    nonAceTotal += Integer.parseInt(card.getValue());
                }
            }
            assert nonAceTotal < 10 && nonAceTotal > 2;
            String nonAceTotalString = Integer.toString(nonAceTotal);

            String uniqueCode = "A" + nonAceTotalString + dealerCard.getValue();
            switch (uniqueCode) {
                case "A92","A93","A94","A95","A96","A97","A98","A99","A9T","A9A",
                        "A82","A83","A84","A85","A87","A88","A89","A8T","A8A",
                        "A77","A78":
                    return "stay";
                case "A86","A72","A73","A74","A75","A76",
                        "A63","A64","A65","A66",
                        "A54","A55","A56",
                        "A44","A45","A46",
                        "A35","A36",
                        "A25","A26":
                    if (player.size() == 2) {
                        return "double";
                    }
                    else {
                        return "hit";
                    }
                default:
                    return "hit";
            }

        }
    }

    private static boolean isTotalHard(ArrayList<Card> player) {
        boolean aceDetecter = false;
        for (int i = 0; i < player.size(); i++) {
            if (player.get(i).getValue().equals("A")) {
                aceDetecter = true;
            }
        }
        if (!aceDetecter) {
            return true; // no aces = hard total
        }

        // control reaching here means theres atleast 1 ace

        int value = getNumericalValue(player); //ASSUMING ALL ACES ARE 1

        if (value + 10 <= 21) { //if we can add replace 1 ace (value = 1) with value = 11, and still have it under 21, it is soft
            return false;
        } else {
            return true;
        }
    }


 */
    /* assumes All ACES ARE 1 */
/*
    private static int getNumericalValue(ArrayList<Card> player) {
        int total = 0;
        for (int i = 0; i < player.size(); i++) {
            switch (player.get(i).getValue()) {
                case "A": total += 1;
                    break;
                case "2":
                    total += 2;
                    break;
                case "3":
                    total += 3;
                    break;
                case "4":
                    total += 4;
                    break;
                case "5":
                    total += 5;
                    break;
                case "6":
                    total += 6;
                    break;
                case "7":
                    total += 7;
                    break;
                case "8":
                    total += 8;
                    break;
                case "9":
                    total += 9;
                    break;
                case "T":
                    total += 10;
                    break;
            }
        }
        return total;
    }
}

*/