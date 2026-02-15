package info.mackiewicz.bankapp.system.demo.data;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.system.demo.actor.ActorAccountProvider;
import info.mackiewicz.bankapp.system.demo.builder.DemoTransferBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates realistic 2-month transaction history for demo accounts.
 * Transactions are saved directly to repository (bypasses processing pipeline - this is seed data).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DemoDataGenerator {

    private static final int HISTORY_MONTHS = 2;
    // Salary titles
    private static final String[] SALARY_TITLES = {
            "Salary for hard work",
            "Paycheck - don't spend it all at once",
            "Transfer from the boss - you earned it",
    };
    // Rent titles
    private static final String[] RENT_TITLES = {
            "Rent - a gift for the landlady",
            "Housing shakedown",
            "Rent - paid with a smile",
    };
    // Coffee titles
    private static final String[] COFFEE_TITLES = {
            "Oat milk latte",
            "Espresso for survival",
            "Emergency coffee",
            "Cappuccino - well deserved",
            "Flat white - because worth it",
            "Americano for a good start",
    };
    // FrogShop titles
    private static final String[] FROGSHOP_TITLES = {
            "Hot-dog and prince polo",
            "Emergency shopping",
            "Chips and cola - movie night",
            "Sandwich and RedBull",
            "Ice cream and water - heatwave",
            "Weekend supplies",
    };
    // Buddy Marek titles
    private static final String[] MAREK_TITLES = {
            "For the kebab from last week",
            "Beer refund",
            "Debt of honor",
            "Transfer - don't ask what for",
            "For the match tickets",
            "Pizza payback",
            "Chipping in for a gift",
            "For the gas - thanks mate",
    };
    private final ActorAccountProvider actorAccountProvider;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private static BigDecimal randomAmount(int minWhole, int maxWhole) {
        double value = minWhole + ThreadLocalRandom.current().nextDouble() * (maxWhole - minWhole);
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private static LocalTime randomTime(int minHour, int minMinute, int maxHour, int maxMinute) {
        int minTotalMinutes = minHour * 60 + minMinute;
        int maxTotalMinutes = maxHour * 60 + maxMinute;
        int totalMinutes = ThreadLocalRandom.current().nextInt(minTotalMinutes, maxTotalMinutes + 1);
        return LocalTime.of(totalMinutes / 60, totalMinutes % 60);
    }

    private static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static String randomChoice(String[] options) {
        return options[ThreadLocalRandom.current().nextInt(options.length)];
    }

    /**
     * Generates 2-month transaction history for the given demo account.
     * Calculates and sets the final balance on the account.
     *
     * @param demoAccount the demo account to populate with transactions
     */
    public void generateTransactionHistory(Account demoAccount) {
        log.info("Generating demo transaction history for account ID: {}", demoAccount.getId());

        List<Transaction> transactions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(HISTORY_MONTHS);

        BigDecimal balance = BigDecimal.ZERO;

        // Generate salary transactions (1x per month, around 15th)
        balance = generateSalaryTransactions(demoAccount, transactions, startDate, today, balance);

        // Generate rent transactions (1x per month, around 1st-5th)
        balance = generateRentTransactions(demoAccount, transactions, startDate, today, balance);

        // Generate daily coffee transactions
        balance = generateCoffeeTransactions(demoAccount, transactions, startDate, today, balance);

        // Generate random FrogShop transactions
        balance = generateFrogShopTransactions(demoAccount, transactions, startDate, today, balance);

        // Generate weekly Marek transactions (Wednesdays)
        balance = generateMarekTransactions(demoAccount, transactions, startDate, today, balance);

        transactionRepository.saveAll(transactions);
        demoAccount.setBalance(balance);
        accountRepository.save(demoAccount);

        log.info("Generated {} demo transactions, final balance: {}", transactions.size(), balance);
    }

    private BigDecimal generateSalaryTransactions(Account demoAccount, List<Transaction> transactions,
                                                   LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        Account acmeCorp = actorAccountProvider.getAcmeCorpAccount();
        LocalDate date = startDate.withDayOfMonth(Math.min(15, startDate.lengthOfMonth()));

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(6800, 7200);
            LocalDateTime dateTime = date.atTime(randomTime(15, 0, 15, 30));

            Transaction tx = new DemoTransferBuilder()
                    .from(acmeCorp)
                    .to(demoAccount)
                    .withAmount(amount)
                    .withTitle(randomChoice(SALARY_TITLES))
                    .withDate(dateTime)
                    .build();
            transactions.add(tx);
            balance = balance.add(amount);

            date = date.plusMonths(1);
            if (date.getDayOfMonth() != 15) {
                date = date.withDayOfMonth(Math.min(15, date.lengthOfMonth()));
            }
        }
        return balance;
    }

    // --- Utility methods ---

    private BigDecimal generateRentTransactions(Account demoAccount, List<Transaction> transactions,
                                                 LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        Account landlord = actorAccountProvider.getLandlordAccount();
        // Rent due around 1st-5th of each month
        LocalDate date = startDate.withDayOfMonth(randomInt(1, 5));
        if (date.isBefore(startDate)) {
            date = date.plusMonths(1);
        }

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(2800, 3200);
            LocalDateTime dateTime = date.atTime(randomTime(10, 0, 12, 0));

            Transaction tx = new DemoTransferBuilder()
                    .from(demoAccount)
                    .to(landlord)
                    .withAmount(amount)
                    .withTitle(randomChoice(RENT_TITLES))
                    .withDate(dateTime)
                    .build();
            transactions.add(tx);
            balance = balance.subtract(amount);

            date = date.plusMonths(1);
        }
        return balance;
    }

    private BigDecimal generateCoffeeTransactions(Account demoAccount, List<Transaction> transactions,
                                                   LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        Account starbugs = actorAccountProvider.getStarbugsAccount();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            // Skip weekends sometimes (70% chance of coffee on weekends)
            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            if (isWeekend && ThreadLocalRandom.current().nextInt(100) > 70) {
                date = date.plusDays(1);
                continue;
            }

            BigDecimal amount = randomAmount(15, 25);
            LocalDateTime dateTime = date.atTime(randomTime(7, 0, 9, 0));

            Transaction tx = new DemoTransferBuilder()
                    .from(demoAccount)
                    .to(starbugs)
                    .withAmount(amount)
                    .withTitle(randomChoice(COFFEE_TITLES))
                    .withDate(dateTime)
                    .build();
            transactions.add(tx);
            balance = balance.subtract(amount);

            date = date.plusDays(1);
        }
        return balance;
    }

    private BigDecimal generateFrogShopTransactions(Account demoAccount, List<Transaction> transactions,
                                                     LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        Account frogShop = actorAccountProvider.getFrogShopAccount();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            // ~30% chance per day (roughly a few times per week)
            if (ThreadLocalRandom.current().nextInt(100) < 30) {
                BigDecimal amount = randomAmount(10, 40);
                LocalDateTime dateTime = date.atTime(randomTime(11, 0, 21, 0));

                Transaction tx = new DemoTransferBuilder()
                        .from(demoAccount)
                        .to(frogShop)
                        .withAmount(amount)
                        .withTitle(randomChoice(FROGSHOP_TITLES))
                        .withDate(dateTime)
                        .build();
                transactions.add(tx);
                balance = balance.subtract(amount);
            }
            date = date.plusDays(1);
        }
        return balance;
    }

    private BigDecimal generateMarekTransactions(Account demoAccount, List<Transaction> transactions,
                                                  LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        Account marek = actorAccountProvider.getKolegaMarekAccount();
        LocalDate date = startDate;

        // Find next Wednesday
        while (date.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            date = date.plusDays(1);
        }

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(20, 100);
            LocalDateTime dateTime = date.atTime(randomTime(18, 0, 22, 0));

            Transaction tx = new DemoTransferBuilder()
                    .from(demoAccount)
                    .to(marek)
                    .withAmount(amount)
                    .withTitle(randomChoice(MAREK_TITLES))
                    .withDate(dateTime)
                    .build();
            transactions.add(tx);
            balance = balance.subtract(amount);

            date = date.plusWeeks(1);
        }
        return balance;
    }
}
