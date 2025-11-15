package vn.diepgia.mchis.currency_rate_fetcher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyRates {
    private float vibRate;
    private float vcbRate;
}
