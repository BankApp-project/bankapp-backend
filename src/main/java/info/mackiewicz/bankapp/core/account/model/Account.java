package info.mackiewicz.bankapp.core.account.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import info.mackiewicz.bankapp.core.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.core.account.model.interfaces.AccountInfo;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.account.util.IbanConverter;
import info.mackiewicz.bankapp.core.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a bank account in the system.
 * Each account is associated with a single user and has a unique IBAN.
 * To create a new account, use {@link AccountService#createAccount}.
 *
 * @see AccountService
 */
@Entity
@Table(name = "accounts")
public class Account implements AccountInfo {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Convert(converter = IbanConverter.class)
    @Column(unique = true, nullable = false)
    private Iban iban;

    @Getter
    @Column(name = "user_account_number", nullable = false)
    private Integer userAccountNumber;

    @Getter
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    /**
     * -- SETTER --
     *  Sets the balance of the account.
     *
     * @param newBalance The new balance to set
     *
     *
     */
    @Setter
    @Getter
    private BigDecimal balance;

    /**
     * Default constructor for JPA.
     * This constructor is package-private to prevent direct instantiation.
     * Use {@link AccountService} to create new accounts.
     */
    protected Account() {
    }

    /**
     * Creates a new account with specified owner, account number and IBAN.
     * This constructor is package-private to enforce creation through
     * {@link AccountService}.
     *
     * @param owner             The user who owns this account
     * @param userAccountNumber The user-specific account number
     * @param iban              The International Bank Account Number
     */
    Account(User owner, int userAccountNumber, Iban iban) {
        this.creationDate = LocalDateTime.now();
        this.balance = BigDecimal.ZERO;
        this.owner = owner;
        this.userAccountNumber = userAccountNumber;
        this.iban = iban;
    }

    /**
     * Creates a new AccountFactory instance.
     *
     * @return A new instance of AccountFactory
     */
    public static AccountFactory factory() {
        return new AccountFactory();
    }

    /**
     * Returns the IBAN in a formatted, human-readable form.
     *
     * @return The formatted IBAN string
     */
    public String getFormattedIban() {
        return iban.toFormattedString();
    }

    /**
     * Returns DTO with account owner's ID and full name.
     *
     * @return The owner's DTO
     */
    @JsonGetter("owner")
    public AccountOwnerDTO getOwner() {
        return new AccountOwnerDTO(owner);
    }

    @Override
    public String getOwnerFullname() {
        return owner.getFullName();
    }

    /**
     * Returns the actual User object that owns this account.
     * This method is for internal use only, for JSON serialization use getOwner()
     * which returns AccountOwnerDTO.
     *
     * @return The User object that owns this account
     */
    @JsonIgnore
    public User getRawOwner() {
        return owner;
    }


    /**
     * Returns a string representation of this account.
     *
     * @return A string containing the account number and balance
     */
    @Override
    public String toString() {
        return String.format("Account IBAN #%s [balance = %.2f]", iban.toFormattedString(), balance);
    }

    /**
     * Compares this account to another object for equality.
     * Two accounts are considered equal if they have the same ID or the same IBAN.
     *
     * @param o The object to compare with
     *
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return iban.equals(account.iban) && balance.equals(account.balance);
    }

    /**
     * Returns a hash code value for this account.
     * The hash code is based on the account ID and IBAN.
     *
     * @return A hash code value for this account
     */
    @Override
    public int hashCode() {
        return Objects.hash(iban, balance);
    }

}
