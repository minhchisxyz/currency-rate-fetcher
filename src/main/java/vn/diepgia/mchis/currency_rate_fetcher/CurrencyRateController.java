package vn.diepgia.mchis.currency_rate_fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/CurrencyRateFetcher/api/v1")
public class CurrencyRateController {
    private final CurrencyRateService service;

    @GetMapping
    public ResponseEntity<CurrencyRates> getCurrencyRate() {
        return ResponseEntity.ok(service.getRates());
    }
}
