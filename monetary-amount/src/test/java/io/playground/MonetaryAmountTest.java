package io.playground;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.money.Monetary;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigurePostgresDatabase
class MonetaryAmountTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void unitedStatesDollar() {
        final var value = "1234.56";
        final var locale = Locale.forLanguageTag("en-US");

        createWallet(value, locale, (wallet, moneyFormatter) ->
                assertThat(wallet.getBalance())
                        .satisfies(balance -> assertThat(balance.toString()).isEqualTo("USD 1234.56"))
                        .satisfies(balance -> assertThat(moneyFormatter.format(balance)).isEqualTo("$1,234.56"))
        );
    }

    @Test
    void brazilianReal() {
        final var value = "1500.75";
        final var locale = Locale.forLanguageTag("pt-BR");

        createWallet(value, locale, (wallet, moneyFormatter) ->
                assertThat(wallet.getBalance())
                        .satisfies(balance -> assertThat(balance.toString()).isEqualTo("BRL 1500.75"))
                        .satisfies(balance -> assertThat(moneyFormatter.format(balance)).isEqualTo("R$ 1.500,75"))
        );
    }

    private void createWallet(String value, Locale locale, BiFunction<Wallet, MonetaryAmountFormat, ?> function) {
        final var number = new BigDecimal(value);
        final var currencyUnit = Monetary.getCurrency(locale);
        final var money = Money.of(number, currencyUnit);
        final var moneyFormatter = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(locale)
                        .set(CurrencyStyle.SYMBOL)
                        .build()
        );

        final var wallet = new Wallet();
        wallet.setBalance(money);

        // Hibernate: insert into wallet (balance_amount, balance_currency) values (?, ?)
        walletRepository.saveAndFlush(wallet);

        // Hibernate: select w1_0.id,w1_0.balance_amount,w1_0.balance_currency from wallet w1_0 where w1_0.id=?
        final var walletFoundById = walletRepository.findById(wallet.getId())
                .orElseThrow();

        assertThat(walletFoundById.getBalance())
                .satisfies(balance -> assertThat(balance.getCurrency()).isEqualTo(currencyUnit))
                .satisfies(balance -> assertThat(balance.getNumber().numberValue(BigDecimal.class)).isEqualTo(number));

        function.apply(walletFoundById, moneyFormatter);
    }
}
