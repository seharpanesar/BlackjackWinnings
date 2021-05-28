import java.util.ArrayList;
import java.util.Random;

public class BlackjackWinnings {
    private static ArrayList<Card> deck = initDeck();
    private static double startCash = 100;
    private static double totalCash = startCash;
    private static double bet;
    private static long wins = 0;
    private static long push = 0;
    private static long losses = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            bet = totalCash/100;
            totalCash -= bet;

            //creating and filling player hand
            ArrayList<Card> playerHand = new ArrayList<>();
            playerHand.add(getCard());
            playerHand.add(getCard());
            System.out.println("--------------");
            System.out.println("NEW ROUND\n");
            System.out.printf("Player hand: %s", playerHand.get(0).getValue());
            System.out.printf(" %s\n", playerHand.get(1).getValue());

            //creating and filling dealer hand
            ArrayList<Card> dealerHand = new ArrayList<>();
            dealerHand.add(getCard());
            dealerHand.add(getCard());
            System.out.printf("Dealer card: %s (%s)\n\n", dealerHand.get(0).getValue(), dealerHand.get(1).getValue());

            if (isSplit(playerHand, dealerHand)) {
                // splitting old hand into 2 new hands
                ArrayList<Card> newHand1 = new ArrayList<>();
                newHand1.add(playerHand.get(0));
                newHand1.add(getCard());
                ArrayList<Card> newHand2 = new ArrayList<>();
                newHand2.add(playerHand.get(1));
                newHand2.add(getCard());
                playRound(newHand1,dealerHand);
                playRound(newHand2,dealerHand);
            }

            else {
                playRound(playerHand, dealerHand);
            }
            System.out.printf("\nTotal cash: %.0f\n\n", totalCash);
            if (totalCash <= 10) {
                System.out.printf("Bankrupt %d\n", i);
                break;
            }

        }
        System.out.printf("player won %d times\n", wins);
        System.out.printf("player lost %d times\n", losses);
        System.out.printf("player pushed %d times\n", push);
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

    private static boolean isSplit(ArrayList<Card> playerHand, ArrayList<Card> dealerHand) {
        String firstCard = playerHand.get(0).getValue();
        String secondCard = playerHand.get(1).getValue();
        String dealerCard = dealerHand.get(0).getValue();
        if (!firstCard.equals(secondCard)) {
            return false;
        }

        String uniqueCode = firstCard + secondCard + dealerCard;

        switch (uniqueCode) {
            case "AA2","AA3","AA4","AA5","AA6","AA7","AA8","AA9","AAT","AAA",
                    "992","993","994","995","996","998","999",
                    "882","883","884","885","886","887","888","889","88T","88A",
                    "772","773","774","775","776","777",
                    "663","664","665","666",
                    "334","335","336","337",
                    "224","225","226","227":
                return true;
            default:
                return false;
        }
    }

    private static void playRound(ArrayList<Card> playerHand, ArrayList<Card> dealerHand) {
        //Checking for blackjack in player hand
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


        //Checking for blackjack in dealer hand
        if (isBlackJack(dealerHand) && !isBlackJack(playerHand)) {
            return;
        }

        // player move
        System.out.println("Player move");

        String nextMove;
        do {
            if (getNumericalValue(playerHand) > 21) { //BUST
                System.out.println("bust");
                break;
            }
            nextMove = determineNextMove(playerHand, dealerHand);
            switch (nextMove) {
                case "hit" -> playerHand = hit(playerHand);
                case "double" -> playerHand = doubleDown(playerHand);
            }
        } while (nextMove.equals("hit"));

        System.out.println();
        // dealer move
        System.out.println("Dealer move");

        int flag = 0; // indicates when to move on from dealer play

        do {
            if (isTotalHard(dealerHand)) {
                if (getNumericalValue(dealerHand) > 16) {
                    break;
                }
                dealerHand = hit(dealerHand);
            }
            else {
                if (getNumericalValue(dealerHand) + 10 > 16) {
                    break;
                }
                dealerHand = hit(dealerHand);
            }
        } while (true);

        System.out.println();

        int dealerFinal = getNumericalValue(dealerHand);
        int playerFinal = getNumericalValue(playerHand);
        System.out.println("Player final value: " +playerFinal);
        System.out.println("Dealer final value: " +dealerFinal);

        if (!isTotalHard(dealerHand)) {
            dealerFinal += 10;
        }

        if (playerFinal > 21) {
            System.out.println("Player busts");
            totalCash += 0; // player loses bet
            losses++;
            return;
        }
        else if (dealerFinal > 21) {
            System.out.println("Dealer busts");
            totalCash += 2*bet; // gets his bet and dealers bet back
            wins++;
            return;
        }

        if (playerFinal > dealerFinal) {
            System.out.println("Player wins");
            totalCash += 2*bet;
            wins++;
        }
        else if (playerFinal < dealerFinal) {
            System.out.println("Dealer wins");
            totalCash += 0;
            losses++;

        } else {
            System.out.println("Push");
            totalCash += bet;
            push++;
        }
    }

    private static ArrayList<Card> hit(ArrayList<Card> playerHand) {
        System.out.println("Hit");
        playerHand.add(getCard());
        System.out.print("New hand ");
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

    /* assumes All ACES ARE 1 */
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