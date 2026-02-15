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
    // Shared configuration
    private static final int CURRENCY_DECIMAL_PLACES = 2;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int PERCENTAGE_BASE = 100;
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
        return BigDecimal.valueOf(value).setScale(CURRENCY_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    private static LocalTime randomTime(int minHour, int minMinute, int maxHour, int maxMinute) {
        int minTotalMinutes = minHour * MINUTES_PER_HOUR + minMinute;
        int maxTotalMinutes = maxHour * MINUTES_PER_HOUR + maxMinute;
        int totalMinutes = ThreadLocalRandom.current().nextInt(minTotalMinutes, maxTotalMinutes + 1);
        return LocalTime.of(totalMinutes / MINUTES_PER_HOUR, totalMinutes % MINUTES_PER_HOUR);
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
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusMonths(HISTORY_MONTHS).plusDays(1);

        BigDecimal balance = BigDecimal.ZERO;

        // Generate salary transactions (1x per month, around 15th)
        balance = generateSalaryTransactions(demoAccount, transactions, startDate, endDate, balance);

        // Generate rent transactions (1x per month, around 1st-5th)
        balance = generateRentTransactions(demoAccount, transactions, startDate, endDate, balance);

        // Generate daily coffee transactions
        balance = generateCoffeeTransactions(demoAccount, transactions, startDate, endDate, balance);

        // Generate random FrogShop transactions
        balance = generateFrogShopTransactions(demoAccount, transactions, startDate, endDate, balance);

        // Generate weekly Marek transactions (Wednesdays)
        balance = generateMarekTransactions(demoAccount, transactions, startDate, endDate, balance);

        transactionRepository.saveAll(transactions);
        demoAccount.setBalance(balance);
        accountRepository.save(demoAccount);

        log.info("Generated {} demo transactions, final balance: {}", transactions.size(), balance);
    }

    private BigDecimal generateSalaryTransactions(Account demoAccount, List<Transaction> transactions,
                                                   LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        final int SALARY_DAY_OF_MONTH = 15;
        final int SALARY_MIN_AMOUNT = 6800;
        final int SALARY_MAX_AMOUNT = 7200;
        final int SALARY_TIME_HOUR = 15;
        final int SALARY_TIME_MIN_MINUTE = 0;
        final int SALARY_TIME_MAX_MINUTE = 30;

        Account acmeCorp = actorAccountProvider.getAcmeCorpAccount();
        LocalDate date = startDate.withDayOfMonth(Math.min(SALARY_DAY_OF_MONTH, startDate.lengthOfMonth()));

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(SALARY_MIN_AMOUNT, SALARY_MAX_AMOUNT);
            LocalDateTime dateTime = date.atTime(randomTime(SALARY_TIME_HOUR, SALARY_TIME_MIN_MINUTE, SALARY_TIME_HOUR, SALARY_TIME_MAX_MINUTE));

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
            if (date.getDayOfMonth() != SALARY_DAY_OF_MONTH) {
                date = date.withDayOfMonth(Math.min(SALARY_DAY_OF_MONTH, date.lengthOfMonth()));
            }
        }
        return balance;
    }

    // --- Utility methods ---

    private BigDecimal generateRentTransactions(Account demoAccount, List<Transaction> transactions,
                                                 LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        final int RENT_MIN_DAY = 1;
        final int RENT_MAX_DAY = 5;
        final int RENT_MIN_AMOUNT = 2800;
        final int RENT_MAX_AMOUNT = 3200;
        final int RENT_TIME_MIN_HOUR = 10;
        final int RENT_TIME_MAX_HOUR = 12;
        final int RENT_TIME_MINUTE = 0;

        Account landlord = actorAccountProvider.getLandlordAccount();
        // Rent due around 1st-5th of each month
        LocalDate date = startDate.withDayOfMonth(randomInt(RENT_MIN_DAY, RENT_MAX_DAY));
        if (date.isBefore(startDate)) {
            date = date.plusMonths(1);
        }

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(RENT_MIN_AMOUNT, RENT_MAX_AMOUNT);
            LocalDateTime dateTime = date.atTime(randomTime(RENT_TIME_MIN_HOUR, RENT_TIME_MINUTE, RENT_TIME_MAX_HOUR, RENT_TIME_MINUTE));

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
        final int COFFEE_WEEKEND_PROBABILITY_PERCENT = 70;
        final int COFFEE_MIN_AMOUNT = 15;
        final int COFFEE_MAX_AMOUNT = 25;
        final int COFFEE_TIME_MIN_HOUR = 7;
        final int COFFEE_TIME_MAX_HOUR = 9;
        final int COFFEE_TIME_MINUTE = 0;

        Account starbugs = actorAccountProvider.getStarbugsAccount();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            // Skip weekends sometimes (70% chance of coffee on weekends)
            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            if (isWeekend && ThreadLocalRandom.current().nextInt(PERCENTAGE_BASE) > COFFEE_WEEKEND_PROBABILITY_PERCENT) {
                date = date.plusDays(1);
                continue;
            }

            BigDecimal amount = randomAmount(COFFEE_MIN_AMOUNT, COFFEE_MAX_AMOUNT);
            LocalDateTime dateTime = date.atTime(randomTime(COFFEE_TIME_MIN_HOUR, COFFEE_TIME_MINUTE, COFFEE_TIME_MAX_HOUR, COFFEE_TIME_MINUTE));

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
        final int FROGSHOP_PROBABILITY_PERCENT = 30;
        final int FROGSHOP_MIN_AMOUNT = 10;
        final int FROGSHOP_MAX_AMOUNT = 40;
        final int FROGSHOP_TIME_MIN_HOUR = 11;
        final int FROGSHOP_TIME_MAX_HOUR = 21;
        final int FROGSHOP_TIME_MINUTE = 0;

        Account frogShop = actorAccountProvider.getFrogShopAccount();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            // ~30% chance per day (roughly a few times per week)
            if (ThreadLocalRandom.current().nextInt(PERCENTAGE_BASE) < FROGSHOP_PROBABILITY_PERCENT) {
                BigDecimal amount = randomAmount(FROGSHOP_MIN_AMOUNT, FROGSHOP_MAX_AMOUNT);
                LocalDateTime dateTime = date.atTime(randomTime(FROGSHOP_TIME_MIN_HOUR, FROGSHOP_TIME_MINUTE, FROGSHOP_TIME_MAX_HOUR, FROGSHOP_TIME_MINUTE));

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
        final int MAREK_MIN_AMOUNT = 20;
        final int MAREK_MAX_AMOUNT = 100;
        final int MAREK_TIME_MIN_HOUR = 18;
        final int MAREK_TIME_MAX_HOUR = 22;
        final int MAREK_TIME_MINUTE = 0;

        Account marek = actorAccountProvider.getKolegaMarekAccount();
        LocalDate date = startDate;

        // Find next Wednesday
        while (date.getDayOfWeek() != DayOfWeek.WEDNESDAY) {
            date = date.plusDays(1);
        }

        while (!date.isAfter(endDate)) {
            BigDecimal amount = randomAmount(MAREK_MIN_AMOUNT, MAREK_MAX_AMOUNT);
            LocalDateTime dateTime = date.atTime(randomTime(MAREK_TIME_MIN_HOUR, MAREK_TIME_MINUTE, MAREK_TIME_MAX_HOUR, MAREK_TIME_MINUTE));

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
