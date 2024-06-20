package com.proger.cashtracker.api.currencyConversion

@Deprecated("не имеет смысла")
class CurrencyConversion(
    var baseCurrency: String,
    var targetCurrency: String,
    var exchangeRate: Double
) {
    companion object{
//        suspend fun getCurrencyRates(): CurrencyResponse? {
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://api.getgeoapi.com/api/v2/currency/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//
//            val service = retrofit.create(CurrencyApiService::class.java)
//            val response = service.getCurrencyRates()
//
//            return if (response.isSuccessful) {
//                response.body()
//            } else {
//                null
//            }
//        }

//        suspend fun getCurrency(): ArrayList<Currency> = withContext(Dispatchers.IO) {
//            var currency = ArrayList<Currency>()
//            val currencyRates = getCurrencyRates()
//            val rates = currencyRates?.rates
//            if (rates != null) {
//                for (rate in rates) {
//                    currency.add(
//                        Currency(
//                            null,
//                            rate.key,
//                            currencyRates.base_currency_name,
//                            rate.value.rate_for_amount
//                        )
//                    )
//                }
//            }
//            return@withContext currency
//        }
    }

    fun update(baseCurrency: String, targetCurrency: String, exchangeRate: Double) {
        this.baseCurrency = baseCurrency
        this.targetCurrency = targetCurrency
        this.exchangeRate = exchangeRate
    }

    fun convert(amount: Double): Double {
        return amount * exchangeRate
    }
}